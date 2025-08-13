#import "MetalRenderer.h"
#import <jni.h>
#import <Foundation/Foundation.h>
#import <AppKit/AppKit.h> // macOS window handling

static MetalRendererBackend* g_renderer = nullptr;

MetalRendererBackend::MetalRendererBackend()
    : device(nil), commandQueue(nil), layer(nil) {}

MetalRendererBackend::~MetalRendererBackend() {
    shutdown();
}

void MetalRendererBackend::init() {
    device = MTLCreateSystemDefaultDevice();
    if (!device) {
        NSLog(@"[MetalRender] ERROR: No Metal device found!");
        return;
    }

    commandQueue = [device newCommandQueue];
    if (!commandQueue) {
        NSLog(@"[MetalRender] ERROR: Failed to create Metal command queue!");
        return;
    }

    layer = [CAMetalLayer layer];
    layer.device = device;
    layer.pixelFormat = MTLPixelFormatBGRA8Unorm;
    layer.contentsScale = [NSScreen mainScreen].backingScaleFactor;

    NSLog(@"[MetalRender] Metal backend initialized successfully");
}

void MetalRendererBackend::renderFrame() {
    if (!layer) return;

    @autoreleasepool {
        id<CAMetalDrawable> drawable = [layer nextDrawable];
        if (!drawable) return;

        MTLRenderPassDescriptor* passDesc = [MTLRenderPassDescriptor renderPassDescriptor];
        passDesc.colorAttachments[0].texture = drawable.texture;
        passDesc.colorAttachments[0].loadAction = MTLLoadActionClear;
        passDesc.colorAttachments[0].clearColor = MTLClearColorMake(0.0, 0.2, 0.6, 1.0);
        passDesc.colorAttachments[0].storeAction = MTLStoreActionStore;

        id<MTLCommandBuffer> cmdBuffer = [commandQueue commandBuffer];
        id<MTLRenderCommandEncoder> encoder = [cmdBuffer renderCommandEncoderWithDescriptor:passDesc];
        [encoder endEncoding];
        [cmdBuffer presentDrawable:drawable];
        [cmdBuffer commit];
    }
}

void MetalRendererBackend::shutdown() {
    commandQueue = nil;
    device = nil;
    layer = nil;
    NSLog(@"[MetalRender] Metal backend shut down");
}

extern "C" {

JNIEXPORT void JNICALL Java_pebblesboon_metalrender_MetalRenderer_initMetal(JNIEnv*, jclass) {
    if (!g_renderer) g_renderer = new MetalRendererBackend();
    g_renderer->init();
}

JNIEXPORT void JNICALL Java_pebblesboon_metalrender_MetalRenderer_renderMetalFrame(JNIEnv*, jclass) {
    if (g_renderer) g_renderer->renderFrame();
}

JNIEXPORT void JNICALL Java_pebblesboon_metalrender_MetalRenderer_shutdownMetal(JNIEnv*, jclass) {
    if (g_renderer) {
        g_renderer->shutdown();
        delete g_renderer;
        g_renderer = nullptr;
    }
}

}
