package Kuboid.manager.voxel;

import org.joml.Vector3f;

public class Voxel {

    public Vector3f origin;
    public VoxelType type;


    public Voxel(Vector3f origin, VoxelType type) {
        this.origin = origin;
        this.type = type;
    }

    public Voxel(int x, int y, int z, VoxelType type) {
        this.origin = new Vector3f(x, y, z);
        this.type = type;
    }
}
