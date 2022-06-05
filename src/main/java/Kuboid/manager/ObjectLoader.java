package Kuboid.manager;

import Kuboid.manager.model.Model;
import Kuboid.manager.utils.Utils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Kuboid.manager.utils.Utils.storeDataInFloatBuffer;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class ObjectLoader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    //private List<Integer> textures = new ArrayList<>();
    private HashMap<String, Integer> textures = new HashMap<String, Integer>();

    public Model loadModel(float[] vertices, float[] uvs, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDatainAttribList(0, 3, vertices);
        storeDatainAttribList(1, 2, uvs);
        unbind();
        return new Model(id, indices.length);
    }

    public Model loadModel(float[] vertices, float[] uvs) {
        int id = createVAO();
        storeDatainAttribList(0, 3, vertices);
        storeDatainAttribList(1, 2, uvs);
        unbind();
        return new Model(id, vertices.length);
    }

    public Model loadModel(float[] vertices, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDatainAttribList(0, 3, vertices);
        unbind();
        return new Model(id, indices.length);
    }

    public int loadTexture(String filename) throws Exception {
        int width, height;
        ByteBuffer buffer;

        if (textures.containsKey(filename)) {
            return textures.get(filename);
        } else {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer c = stack.mallocInt(1);

                buffer = STBImage.stbi_load(filename, w, h, c, 4);

                if (buffer == null)
                    throw new Exception("Image File " + filename + " not loaded. " + STBImage.stbi_failure_reason());

                width = w.get();
                height = h.get();

                w = null;
                h = null;
                c = null;
            }

            int id = glGenTextures();
            textures.put(filename, id);

            glBindTexture(GL_TEXTURE_2D, id);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            glGenerateMipmap(GL_TEXTURE_2D);
            STBImage.stbi_image_free(buffer);

            return id;
        }
    }

    private int createVAO() {
        int id = glGenVertexArrays();
        vaos.add(id);
        glBindVertexArray(id);
        return id;
    }

    private void storeIndicesBuffer(int[] indices) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private void storeDatainAttribList(int attribNo, int vertexCount, float[] data) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attribNo, vertexCount, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }

    private void unbind() {
        glBindVertexArray(0);
    }

    public void cleanup() {
        for(int vao : vaos)
            glDeleteVertexArrays(vao);
        for (int vbo : vbos)
            glDeleteBuffers(vbo);
        for (int texture : textures.values())
            glDeleteTextures(texture);
    }
}
