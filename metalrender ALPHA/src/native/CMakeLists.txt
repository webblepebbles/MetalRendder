cmake_minimum_required(VERSION 3.20)
project(MetalRenderNative LANGUAGES C CXX OBJC OBJCXX)

set(CMAKE_CXX_STANDARD 17)

find_library(FOUNDATION_FRAMEWORK Foundation)
find_library(APPKIT_FRAMEWORK AppKit)
find_library(METAL_FRAMEWORK Metal)
find_library(QUARTZCORE_FRAMEWORK QuartzCore)

add_library(metalrender_native SHARED
    MetalRenderer.mm
)

target_link_libraries(metalrender_native
    ${FOUNDATION_FRAMEWORK}
    ${APPKIT_FRAMEWORK}
    ${METAL_FRAMEWORK}
    ${QUARTZCORE_FRAMEWORK}
)

set_target_properties(metalrender_native PROPERTIES
    OUTPUT_NAME "metalrender"
)
