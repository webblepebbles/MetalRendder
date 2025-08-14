# MetalRender

Welcome to MetalRender! This mod is a Optimisation mod for MC 1.21 versions and above. It uses Mesh Shading on Metal GPUs to speed up chunk rendering.
This mod is currently in Alpha. No results are guarranteed in this stage and please save important stuff, this mod uses native Objective C++.
This mod will now recieve Nightly updates, no nightly releases are certain to be stable.

## Dependencies
- Sodium                        |Required
- Fabric                        |Required
- Fabric API                    |Required
- Minecraft java 1.21.x         |Required
- Java >21 (for building)       |Required
- a bowl of ramen noodles   `   |Recommended

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
