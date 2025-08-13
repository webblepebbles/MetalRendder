package pebblesboon.metalrender.bridge;

public class MetalBridge {

    public static void uploadChunkMesh(Object output) {

        System.out.println("[MetalBridge] uploadChunkMesh called for: " + output);
    }

    public static void deleteChunkMesh(Object regionOrId) {
    
        System.out.println("[MetalBridge] deleteChunkMesh called for: " + regionOrId);
    }
}
