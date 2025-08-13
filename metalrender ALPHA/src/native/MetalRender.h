#pragma once
#include <Metal/Metal.h>
#include <QuartzCore/CAMetalLayer.h>

class MetalRendererBackend {
public:
    MetalRendererBackend();
    ~MetalRendererBackend();

    void init();
    void renderFrame();
    void shutdown();

private:
    id<MTLDevice> device;
    id<MTLCommandQueue> commandQueue;
    CAMetalLayer* layer;
};
