
#import <Metal/Metal.h>
#import <QuartzCore/CAMetalLayer.h>
#import <Foundation/Foundation.h>
#import <jni.h>

@interface MRRenderer : NSObject
@property(nonatomic, strong) id<MTLDevice> device;
@property(nonatomic, strong) id<MTLCommandQueue> queue;
@property(nonatomic, strong) id<MTLTexture> colorTex;
@property(nonatomic, assign) int width;
@property(nonatomic, assign) int height;
@end

@implementation MRRenderer
@end

static inline MRRenderer* fromHandle(jlong h) { return (__bridge MRRenderer*) (void*)h; }

JNIEXPORT jboolean JNICALL Java_pebblesboon_metalrender_MetalRuntime_nativeIsSupported
  (JNIEnv* env, jclass cls) {
    id<MTLDevice> dev = MTLCreateSystemDefaultDevice();
    return dev != nil;
}

JNIEXPORT jlong JNICALL Java_pebblesboon_metalrender_MetalRuntime_nativeCreate
  (JNIEnv* env, jclass cls, jint width, jint height) {
    MRRenderer* r = [MRRenderer new];
    r.device = MTLCreateSystemDefaultDevice();
    if (!r.device) return 0;
    r.queue = [r.device newCommandQueue];
    r.width = width; r.height = height;

    MTLTextureDescriptor* desc = [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatBGRA8Unorm
                                                                                    width:width height:height mipmapped:NO];
    desc.usage = MTLTextureUsageRenderTarget | MTLTextureUsageShaderRead | MTLTextureUsageBlit;
    r.colorTex = [r.device newTextureWithDescriptor:desc];
    return (jlong)(__bridge_retained void*)r;
}

JNIEXPORT void JNICALL Java_pebblesboon_metalrender_MetalRuntime_nativeResize
  (JNIEnv* env, jclass cls, jlong handle, jint width, jint height) {
    MRRenderer* r = fromHandle(handle);
    if (!r) return;
    r.width = width; r.height = height;
    MTLTextureDescriptor* desc = [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatBGRA8Unorm
                                                                                    width:width height:height mipmapped:NO];
    desc.usage = MTLTextureUsageRenderTarget | MTLTextureUsageShaderRead | MTLTextureUsageBlit;
    r.colorTex = [r.device newTextureWithDescriptor:desc];
}

JNIEXPORT void JNICALL Java_pebblesboon_metalrender_MetalRuntime_nativeBeginFrame
  (JNIEnv* env, jclass cls, jlong handle) {
    MRRenderer* r = fromHandle(handle);
    if (!r) return;
    id<MTLCommandBuffer> cb = [r.queue commandBuffer];
    MTLRenderPassDescriptor* rp = [MTLRenderPassDescriptor renderPassDescriptor];
    rp.colorAttachments[0].texture = r.colorTex;
    rp.colorAttachments[0].loadAction = MTLLoadActionClear;
    rp.colorAttachments[0].storeAction = MTLStoreActionStore;
    rp.colorAttachments[0].clearColor = MTLClearColorMake(0.07, 0.12, 0.20, 1.0);

    id<MTLRenderCommandEncoder> enc = [cb renderCommandEncoderWithDescriptor:rp];
    // TODO: issue real draw calls here (compute culling, indirect draws, etc.)
    [enc endEncoding];
    [cb commit];
    [cb waitUntilScheduled];
}

JNIEXPORT void JNICALL Java_pebblesboon_metalrender_MetalRuntime_nativeEndFrame
  (JNIEnv* env, jclass cls, jlong handle) {
    // no-op for offscreen path
}

JNIEXPORT void JNICALL Java_pebblesboon_metalrender_MetalRuntime_nativeDestroy
  (JNIEnv* env, jclass cls, jlong handle) {
    if (!handle) return;
    CFBridgingRelease((void*)handle);
}

JNIEXPORT jlong JNICALL Java_pebblesboon_metalrender_MetalRuntime_nativeGetIOSurfaceId
  (JNIEnv* env, jclass cls, jlong handle) {
    return 0;
}
