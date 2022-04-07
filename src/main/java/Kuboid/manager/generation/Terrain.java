package Kuboid.manager.generation;

import Kuboid.manager.ObjectLoader;
import Kuboid.manager.entity.Entity;
import Kuboid.manager.entity.Model;
import Kuboid.manager.entity.Texture;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Terrain implements Runnable {

    private ObjectLoader loader;
    private Model model;

    Thread cleanupThread;

    private long size;
    private boolean plain;
    private boolean isWireframe;
    private boolean running = true;
    private Vector3f camPos;

    private List<Entity> entities = Collections.synchronizedList(new ArrayList<>());
    private List<Vector3f> usedPos = Collections.synchronizedList(new ArrayList<>());

    private final float[] verticesDirt = new float[]{
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

    private final float[] verticesGrassBlock = new float[]{
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

    private final float[] textCoordsDirt = new float[]{
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

    private final float[] textCoordsGrassBlock = new float[]{
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

    private final int[] indicesDirt = new int[]{
            0, 1, 2, 2, 3, 0, //front
            7, 6, 5, 5, 4, 7, //back
            8, 0, 3, 3, 11, 8, //top
            9, 1, 2, 2, 10, 9, // bottom
            4, 5, 1, 1, 0, 4, //left
            3, 2, 6, 6, 7, 3, //right
    };

    private final int[] indicesGrassBlock = new int[]{
            0, 1, 2, 2, 3, 0, //front
            7, 6, 5, 5, 4, 7, //back
            8, 9, 10, 10, 11, 8, //top
            12, 13, 14, 14, 15, 12, // bottom
            4, 5, 1, 1, 0, 4, //left
            3, 2, 6, 6, 7, 3, //right
    };

    public Terrain(long size, boolean plain, boolean isWireframe, Vector3f camPos) {
        this.size = size;
        this.plain = plain;
        this.isWireframe = isWireframe;
        this.camPos = camPos;
        loader = new ObjectLoader();

        if (isWireframe)
            model = loader.loadModel(verticesDirt, indicesDirt);
        else {
            //model = loader.loadModel(verticesDirt, textCoordsDirt, indicesDirt);
            //model.setTexture(new Texture(loader.loadTexture("textures/dirt.png")));

            model = loader.loadModel(verticesGrassBlock, textCoordsGrassBlock, indicesGrassBlock);
            try {
                model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    for (int i = 0; i < entities.size(); i++) {

                        Vector3f pos = entities.get(i).getPos();

                        int distX = (int) (camPos.x - pos.x);
                        int distZ = (int) (camPos.z - pos.z);

                        if ((Math.abs(distX) > (size / 2)) || (Math.abs(distZ) > (size / 2))) {
                            usedPos.remove(pos);
                            entities.remove(i);
                        }
                    }
                }
            }
        }).start();
    }

    private void generateTerrain() {
        long x, y = 0, z;

        Vector3f vector;

        if (size > 1) {
            for (x = ((int) camPos.x - (size / 2)); x < ((int) camPos.x + (size / 2)); x++) {
                for (z = ((int) camPos.z - (size / 2)); z < ((int) camPos.z + (size / 2)); z++) {
                    vector = new Vector3f(x, 0, z);

                    if (!usedPos.contains(vector)) {
                        entities.add(new Entity(model, vector, new Vector3f(0, 0, 0), 1f));
                        usedPos.add(vector);
                    }
                }
            }
        } else {
            entities.add(new Entity(model, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 1f));
        }
    }

    public void update(Vector3f camPos) {
        this.camPos = camPos;
    }

    public List<Entity> getTerrain() {
        return entities;
    }

    public void setCamPos(Vector3f camPos) {
        this.camPos = camPos;
    }

    public void stopLoop() {
        running = false;
    }

    public void run() {
        try {
            while (running) {
                generateTerrain();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
