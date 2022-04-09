package Kuboid.manager.generation;

import Kuboid.manager.entity.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    private List<Entity> entities = new ArrayList<>();
    private Vector3f origin;
    private long chunkSize;

    public Chunk(List<Entity> blocks, Vector3f origin, long chunkSize) {
        this.entities = blocks;
        this.origin = origin;
        this.chunkSize = chunkSize;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
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
