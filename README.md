# MetalRender

Welcome to MetalRender! This mod is a Optimisation mod for MC 1.21 versions and above. It uses Mesh Shading on Metal GPUs to speed up chunk rendering.
This mod is currently in Alpha. No results are guarranteed in this stage and please save important stuff, this mod uses native Objective C++.

## Features
- Works with Fabric 1.21.x.
- Needs Sodium.
- Uses Java 21.
- Uses native Metal code with JNI.
- Can fall back to OpenGL if Metal fails.
- Client-side only.

## Contributors
This mod not solely made by me. All Objective C++ code was made by SkaterKurtKool. Hes rlly amazing :DDDD. I only do Java so he was like rlly helpful. he made all the C stuff.


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
