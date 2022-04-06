package Kuboid.manager.generation;

import Kuboid.manager.ObjectLoader;
import Kuboid.manager.entity.Entity;
import Kuboid.manager.entity.Model;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Terrain {

    private ObjectLoader loader;

    private final long size;
    private final boolean plain;
    private List<Entity> entities = new ArrayList<>();

    /*
    float[] vertices = new float[] {
            -0.5f, -0.5f,  0.5f,
            0.5f, -0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
    };

    int[] indices = new int[]{
            0, 1, 2, 3, 7, 1, 5, 4, 7, 6, 2, 4, 0, 1
    };*/

    float[] vertices = new float[]{
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,//
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,//
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,//
            0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,//
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,//
    };
    float[] textCoords = new float[]{
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,//
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,//
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,//
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,//
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f//
    };
    int[] indices = new int[]{
            0, 1, 3, 3, 1, 2, //front
            8, 10, 11, 9, 8, 11, //top
            12, 13, 7, 5, 12, 7, //right
            14, 15, 6, 4, 14, 6, //left
            16, 18, 19, 17, 16, 19, //bottom
            4, 6, 7, 5, 4, 7, //back
    };

    public Terrain(long size, boolean plain) throws Exception {
        this.size = size;
        this.plain = plain;

        init();
        generateTerrain();
    }

    public void init() {
        loader = new ObjectLoader();

    }

    public void generateTerrain() throws Exception {

        long x, y = 0, z;

        //Model model = loader.loadModel(vertices, textCoords, indices);
        //model.setTexture(new Texture(loader.loadTexture("textures/dirt.png")));

        Model model = loader.loadModel(vertices, indices);

        for (x = -(size / 2); x < (this.size / 2); x++) {
            for (z = (size / 2); z > -(this.size / 2); z--) {
                entities.add(new Entity(model, new Vector3f(x, 0, z), new Vector3f(0, 0, 0), 1f));
            }
        }
    }

    public List<Entity> getTerrain() {
        return entities;
    }
}
