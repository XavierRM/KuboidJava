package Kuboid.manager.generation;

import Kuboid.manager.ObjectLoader;
import Kuboid.manager.entity.Entity;
import Kuboid.manager.entity.Model;
import Kuboid.manager.entity.Texture;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Terrain {

    private ObjectLoader loader;

    private final long size;
    private final boolean plain;
    private final boolean isWireframe;
    private List<Entity> entities = new ArrayList<>();

    float[] verticesDirt = new float[]{
            -0.5f, 0.5f, 0.5f, //0
            -0.5f, -0.5f, 0.5f, //1
            0.5f, -0.5f, 0.5f, //2
            0.5f, 0.5f, 0.5f, //3
            //Front
            -0.5f, 0.5f, -0.5f, //4
            -0.5f, -0.5f, -0.5f, //5
            0.5f, -0.5f, -0.5f, //6
            0.5f, 0.5f, -0.5f, //7
            //Back
            -0.5f, 0.5f, -0.5f, //8
            -0.5f, -0.5f, -0.5f, //9
            0.5f, -0.5f, -0.5f, //10
            0.5f, 0.5f, -0.5f, //11
            //Duplicates for proper texture rendering

    };

    float[] verticesGrassBlock = new float[]{
            -0.5f, 0.5f, 0.5f, //0
            -0.5f, -0.5f, 0.5f, //1
            0.5f, -0.5f, 0.5f, //2
            0.5f, 0.5f, 0.5f, //3
            //Front
            -0.5f, 0.5f, -0.5f, //4
            -0.5f, -0.5f, -0.5f, //5
            0.5f, -0.5f, -0.5f, //6
            0.5f, 0.5f, -0.5f, //7
            //Back
            -0.5f, 0.5f, -0.5f, //8
            -0.5f, 0.5f, 0.5f, //9
            0.5f, 0.5f, 0.5f, //10
            0.5f, 0.5f, -0.5f, //11
            //Top
            -0.5f, -0.5f, -0.5f, //12
            -0.5f, -0.5f, 0.5f, //13
            0.5f, -0.5f, 0.5f, //14
            0.5f, -0.5f, -0.5f, //15
            //Bottom

    };

    float[] textCoordsDirt = new float[]{
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            //
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            //
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    float[] textCoordsGrassBlock = new float[]{
            0.0f, 0.25f,
            0.0f, 0.5f,
            0.25f, 0.5f,
            0.25f, 0.25f,
            //ok
            0.25f, 0.25f,
            0.25f, 0.5f,
            0.0f, 0.5f,
            0.0f, 0.25f,
            //ok
            0.5f, 0.5f,
            0.5f, 0.75f,
            0.75f, 0.75f,
            0.75f, 0.5f,
            //Top
            0.0f, 0.5f,
            0.0f, 0.75f,
            0.25f, 0.75f,
            0.25f, 0.5f,
            //Bottom
    };

    int[] indicesDirt = new int[]{
            0, 1, 2, 2, 3, 0, //front
            7, 6, 5, 5, 4, 7, //back
            8, 0, 3, 3, 11, 8, //top
            9, 1, 2, 2, 10, 9, // bottom
            4, 5, 1, 1, 0, 4, //left
            3, 2, 6, 6, 7, 3, //right
    };

    int[] indicesGrassBlock = new int[]{
            0, 1, 2, 2, 3, 0, //front
            7, 6, 5, 5, 4, 7, //back
            8, 9, 10, 10, 11, 8, //top
            12, 13, 14, 14, 15, 12, // bottom
            4, 5, 1, 1, 0, 4, //left
            3, 2, 6, 6, 7, 3, //right
    };

    public Terrain(long size, boolean plain, boolean isWireframe) throws Exception {
        this.size = size;
        this.plain = plain;
        this.isWireframe = isWireframe;

        init();
        generateTerrain();
    }

    public void init() {
        loader = new ObjectLoader();

    }

    public void generateTerrain() throws Exception {

        long x, y = 0, z;

        Model model;

        if (isWireframe)
            model = loader.loadModel(verticesDirt, indicesDirt);
        else {
            //model = loader.loadModel(verticesDirt, textCoordsDirt, indicesDirt);
            //model.setTexture(new Texture(loader.loadTexture("textures/dirt.png")));

            model = loader.loadModel(verticesGrassBlock, textCoordsGrassBlock, indicesGrassBlock);
            model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")));
        }

        if (this.size > 1) {
            for (x = -(size / 2); x < (this.size / 2); x++) {
                for (z = (size / 2); z > -(this.size / 2); z--) {
                    entities.add(new Entity(model, new Vector3f(x, 0, z), new Vector3f(0, 0, 0), 1f));
                }
            }
        } else {
            entities.add(new Entity(model, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1f));
        }
    }

    public List<Entity> getTerrain() {
        return entities;
    }
}
