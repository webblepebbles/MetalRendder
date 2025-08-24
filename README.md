# MetalRender

Welcome to MetalRender! This mod is a Optimisation mod for MC 1.21 versions and above. It uses Mesh Shading on Metal GPUs to speed up chunk rendering.
This mod is currently in Alpha. No results are guarranteed in this stage and please save important stuff, this mod uses native Objective C.


## Dependencies
| Dependency                          | Version                     | Scope              | Notes                                                                 |
|-------------------------------------|-----------------------------|--------------------|-----------------------------------------------------------------------|
| Minecraft                           | 1.21.8                      | minecraft          | Target game version.                                                  |
| Fabric Loader                       | >0.17.2                     | modImplementation  | Required mod loader.                                                  |
| Fabric API                          | >0.131.0+1.21.8             | modImplementation  | Provides hooks & utilities for mods.                                  |
| Yarn Mappings                       | 1.21.6+build.1 (v2)         | mappings           | Minecraft deobfuscation mappings (slightly behind 1.21.8).            |
| Sodium                              | >mc1.21.6-0.6.13-fabric     | modImplementation  | Rendering optimization mod.                                           |
| LWJGL (core)                        | 3.3.4                       | implementation     | Required for graphics bindings.                                       |
| LWJGL GLFW                          | 3.3.4                       | implementation     | Required for window/input handling.                                   |
| SpongePowered Mixin (via Fabric)    | 0.8.7                       | runtime (included) | Already bundled with Fabric Loader.                                   |

## Install
1. Install Fabric Loader.
2. Install Sodium.
3. Put `metalrender-x.x.x.jar` in `mods` folder.
4. macOS only.

## Build
You need:
- Java 21
- Gradle
- CMake
- Xcode CLI tools

Steps:
```bash
# Build native Metal code
cd native
cmake .
make

# Build mod
cd ..
./gradlew build
