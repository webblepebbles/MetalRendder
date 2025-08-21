#import <Cocoa/Cocoa.h>
#import <Metal/Metal.h>
#import <QuartzCore/CAMetalLayer.h>
#import <simd/simd.h>
#import "metalrender.h"

typedef struct {
    id<MTLDevice> device;
    CAMetalLayer* layer;
    id<MTLCommandQueue> queue;
    id<MTLLibrary> lib;
    id<MTLRenderPipelineState> pipelineVS;
    id<MTLBuffer> vertexBuffer;
    NSUInteger vertexStride;
    NSUInteger vertexCount;
    id<MTLTexture> depthTex;
    matrix_float4x4 viewProj;
    BOOL useMesh;
} MRState;

static id<MTLTexture> makeDepth(id<MTLDevice> dev, CGSize size) {
    MTLTextureDescriptor* d = [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatDepth32Float width:(NSUInteger)size.width height:(NSUInteger)size.height mipmapped:NO];
    d.storageMode = MTLStorageModePrivate;
    d.usage = MTLTextureUsageRenderTarget;
    return [dev newTextureWithDescriptor:d];
}

static NSString* mslVS() {
    return @"using namespace metal;"
           "struct VSIn { float3 pos [[attribute(0)]]; float3 col [[attribute(1)]]; float1 pad [[attribute(2)]]; };"
           "struct VSOut { float4 pos [[position]]; float3 col; };"
           "struct UBO { float4x4 vp; };"
           "vertex VSOut vmain(VSIn in [[stage_in]], constant UBO& u [[buffer(1)]]) {"
           "VSOut o; o.pos = u.vp * float4(in.pos,1.0); o.col = in.col; return o; }"
           "fragment float4 fmain(VSOut in [[stage_in]]) { return float4(in.col,1.0); }";
}

static id<MTLLibrary> makeLib(id<MTLDevice> dev) {
    NSError* err = nil;
    MTLCompileOptions* opts = [MTLCompileOptions new];
    return [dev newLibraryWithSource:mslVS() options:opts error:&err];
}

static id<MTLRenderPipelineState> makePSOvs(id<MTLDevice> dev, id<MTLLibrary> lib, MTLPixelFormat pf) {
    NSError* err = nil;
    id<MTLFunction> v = [lib newFunctionWithName:@"vmain"];
    id<MTLFunction> f = [lib newFunctionWithName:@"fmain"];
    MTLRenderPipelineDescriptor* d = [MTLRenderPipelineDescriptor new];
    d.vertexFunction = v;
    d.fragmentFunction = f;
    d.colorAttachments[0].pixelFormat = pf;
    d.depthAttachmentPixelFormat = MTLPixelFormatDepth32Float;
    return [dev newRenderPipelineStateWithDescriptor:d error:&err];
}

JNIEXPORT jboolean JNICALL Java_com_metalrender_nativebridge_MetalBackend_supportsMeshShaders(JNIEnv* env, jclass cls) {
    id<MTLDevice> dev = MTLCreateSystemDefaultDevice();
    BOOL ok = [dev supportsFamily:MTLGPUFamilyApple7] || [dev supportsFamily:MTLGPUFamilyMac2];
    return ok ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jlong JNICALL Java_com_metalrender_nativebridge_MetalBackend_init(JNIEnv* env, jclass cls, jlong nsWindowPtr, jboolean srgb) {
    NSWindow* window = (NSWindow*)nsWindowPtr;
    id<MTLDevice> device = MTLCreateSystemDefaultDevice();
    CAMetalLayer* layer = [CAMetalLayer layer];
    layer.device = device;
    layer.pixelFormat = srgb ? MTLPixelFormatBGRA8Unorm_sRGB : MTLPixelFormatBGRA8Unorm;
    NSView* view = [window contentView];
    view.wantsLayer = YES;
    view.layer = layer;
    MRState* s = calloc(1, sizeof(MRState));
    s->device = device;
    s->layer = layer;
    s->queue = [device newCommandQueue];
    s->lib = makeLib(device);
    s->pipelineVS = makePSOvs(device, s->lib, layer.pixelFormat);
    s->useMesh = NO;
    CGSize size = CGSizeMake(view.bounds.size.width, view.bounds.size.height);
    s->depthTex = makeDepth(device, size);
    matrix_float4x4 I = (matrix_float4x4){ (vector_float4){1,0,0,0}, (vector_float4){0,1,0,0}, (vector_float4){0,0,1,0}, (vector_float4){0,0,0,1} };
    s->viewProj = I;
    return (jlong)s;
}

JNIEXPORT void JNICALL Java_com_metalrender_nativebridge_MetalBackend_setCamera(JNIEnv* env, jclass cls, jlong handle, jfloatArray mat) {
    MRState* s = (MRState*)handle;
    if (!s) return;
    jfloat* m = (*env)->GetFloatArrayElements(env, mat, NULL);
    if (!m) return;
    matrix_float4x4 vp = {
        (vector_float4){m[0],m[1],m[2],m[3]},
        (vector_float4){m[4],m[5],m[6],m[7]},
        (vector_float4){m[8],m[9],m[10],m[11]},
        (vector_float4){m[12],m[13],m[14],m[15]}
    };
    s->viewProj = vp;
    (*env)->ReleaseFloatArrayElements(env, mat, m, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_com_metalrender_nativebridge_MetalBackend_uploadStaticMesh(JNIEnv* env, jclass cls, jlong handle, jobject buf, jint vcount, jint stride) {
    MRState* s = (MRState*)handle;
    if (!s) return;
    void* ptr = (*env)->GetDirectBufferAddress(env, buf);
    if (!ptr) return;
    NSUInteger bytes = (NSUInteger)vcount * (NSUInteger)stride;
    s->vertexBuffer = [s->device newBufferWithBytes:ptr length:bytes options:MTLResourceStorageModeManaged];
    s->vertexStride = (NSUInteger)stride;
    s->vertexCount = (NSUInteger)vcount;
}

JNIEXPORT void JNICALL Java_com_metalrender_nativebridge_MetalBackend_resize(JNIEnv* env, jclass cls, jlong handle, jint w, jint h) {
    MRState* s = (MRState*)handle;
    if (!s) return;
    CGSize size; size.width = w; size.height = h;
    s->layer.drawableSize = size;
    s->depthTex = makeDepth(s->device, size);
}

JNIEXPORT void JNICALL Java_com_metalrender_nativebridge_MetalBackend_render(JNIEnv* env, jclass cls, jlong handle, jfloat time) {
    MRState* s = (MRState*)handle;
    if (!s) return;
    @autoreleasepool {
        id<CAMetalDrawable> drawable = [s->layer nextDrawable];
        if (!drawable) return;
        MTLRenderPassDescriptor* rp = [MTLRenderPassDescriptor renderPassDescriptor];
        rp.colorAttachments[0].texture = drawable.texture;
        rp.colorAttachments[0].loadAction = MTLLoadActionClear;
        rp.colorAttachments[0].storeAction = MTLStoreActionStore;
        float c = 0.02f;
        rp.colorAttachments[0].clearColor = MTLClearColorMake(c,c,c,1.0);
        rp.depthAttachment.texture = s->depthTex;
        rp.depthAttachment.loadAction = MTLLoadActionClear;
        rp.depthAttachment.storeAction = MTLStoreActionDontCare;
        rp.depthAttachment.clearDepth = 1.0;
        id<MTLCommandBuffer> cb = [s->queue commandBuffer];
        id<MTLRenderCommandEncoder> enc = [cb renderCommandEncoderWithDescriptor:rp];
        [enc setRenderPipelineState:s->pipelineVS];
        typedef struct { matrix_float4x4 vp; } UBO;
        UBO u; u.vp = s->viewProj;
        [enc setVertexBuffer:s->vertexBuffer offset:0 atIndex:0];
        [enc setVertexBytes:&u length:sizeof(UBO) atIndex:1];
        if (s->vertexBuffer && s->vertexCount>0) {
            [enc drawPrimitives:MTLPrimitiveTypeTriangle vertexStart:0 vertexCount:(NSUInteger)s->vertexCount];
        }
        [enc endEncoding];
        [cb presentDrawable:drawable];
        [cb commit];
    }
}

JNIEXPORT void JNICALL Java_com_metalrender_nativebridge_MetalBackend_destroy(JNIEnv* env, jclass cls, jlong handle) {
    MRState* s = (MRState*)handle;
    if (!s) return;
    free(s);
}