package Kuboid.manager.generation;

import Kuboid.manager.voxel.Voxel;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    private List<Voxel> voxels = new ArrayList<>();
    private Vector3f origin;
    private long chunkSize;

    public Chunk(List<Voxel> blocks, Vector3f origin, long chunkSize) {
        this.voxels = blocks;
        this.origin = origin;
        this.chunkSize = chunkSize;
    }

    public List<Voxel> getVoxels() {
        return voxels;
    }

    public void setVoxels(List<Voxel> voxels) {
        this.voxels = voxels;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public void setChunkCoords(Vector3f origin) {
        this.origin = origin;
    }

    public Vector2f getChunkCoords() {
        return new Vector2f(origin.x / chunkSize, origin.x / chunkSize);
    }
}
