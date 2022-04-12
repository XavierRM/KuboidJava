package Kuboid.manager.voxel;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {

    public Vector3f position, normals;
    public Vector2f uvs;

    public Vertex(Vector3f position, Vector3f normals, Vector2f uvs) {
        this.position = position;
        this.normals = normals;
        this.uvs = uvs;
    }
}
