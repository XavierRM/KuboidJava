package Kuboid.manager.generation;

import Kuboid.manager.ObjectLoader;
import Kuboid.manager.entity.Entity;
import Kuboid.manager.model.Model;
import Kuboid.manager.model.Texture;
import Kuboid.manager.utils.PerlinNoise;
import Kuboid.manager.utils.SimplexNoise;
import Kuboid.manager.voxel.Voxel;
import Kuboid.manager.voxel.VoxelType;
import org.joml.Vector3f;

import java.util.*;
import java.util.random.RandomGenerator;

public class Terrain implements Runnable {

    private ObjectLoader loader;
    private Model model, newModel;
    private Texture texture;
    private PerlinNoise generator;
    private SimplexNoise simplexNoise;

    private long seed;
    private long size;
    private long index = 0;
    private long chunkSize, chunkDepth;
    private boolean plain;
    private boolean isWireframe;
    private boolean running = true;
    private Vector3f camPos;

    private Map<Model, List<Entity>> entitiesMap = Collections.synchronizedMap(new HashMap<>());
    private List<ChunkMesh> chunks = Collections.synchronizedList(new ArrayList<>());
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

    private final int[] indicesDirt = new int[]{
            0, 1, 2, 2, 3, 0, //front
            7, 6, 5, 5, 4, 7, //back
            8, 0, 3, 3, 11, 8, //top
            9, 1, 2, 2, 10, 9, // bottom
            4, 5, 1, 1, 0, 4, //left
            3, 2, 6, 6, 7, 3, //right
    };

    public Terrain(long chunksPerAxis, long chunkSize, boolean plain, boolean isWireframe, Vector3f camPos) throws Exception {
        this.size = chunksPerAxis * chunkSize;
        this.chunkSize = chunkSize;
        this.chunkDepth = 1;
        this.plain = plain;
        this.isWireframe = isWireframe;
        this.camPos = camPos;
        this.loader = new ObjectLoader();
        this.generator = new PerlinNoise();
        this.simplexNoise = new SimplexNoise();
        this.texture = new Texture(loader.loadTexture("textures/terrain.jpg"));

        RandomGenerator random = RandomGenerator.of("Random");

        SimplexNoise.setSeed(random.nextLong(Integer.MAX_VALUE));

        if (isWireframe)
            model = loader.loadModel(verticesDirt, indicesDirt);

    }

    public void setWireframe(boolean wireframe) {
        isWireframe = wireframe;
    }

    public void generateTerrain() {
        long x, z;
        float levels = 1 / 64f;

        Vector3f vector;
        Chunk chunk;

        //This could be optimized for sure
        for (x = ((int) camPos.x - (size / 2)) / chunkSize; x < ((int) camPos.x + (size / 2)) / chunkSize; x++) {
            for (z = ((int) camPos.z - (size / 2)) / chunkSize; z < ((int) camPos.z + (size / 2)) / chunkSize; z++) {
                vector = new Vector3f(x * chunkSize, 0, z * chunkSize);

                if (!usedPos.contains(vector)) {

                    List<Voxel> blocks = new ArrayList<>();

                    for (int i = 0; i < chunkSize; i++) {
                        for (int j = 0; j < chunkSize; j++) {
                            //float k = generator.generateHeight((int) (i + (x * chunkSize)), (int) (j + (z * chunkSize)));

                            double nx = (i + (x * chunkSize));
                            double nz = (j + (z * chunkSize));

                            /*
                             * The values multiplying the nx and nz variables are the frequency of the noise generation,
                             * for terrain generation this translates into a flatter world when values are near 0 and more
                             * hills or extreme changes in altitude when does values get further away from 0, to get a better
                             * terrain generation we can combine multiple wavelengths of noise giving us the perfect combination
                             * of flat and hilly terrain.
                             * */

                            double noise = simplexNoise.noise(0.01 * nx, 0.01 * nz);
                            noise += simplexNoise.noise(0.001 * nx, 0.001 * nz);
                            noise += 0.75 * simplexNoise.noise(0.03 * nx, 0.03 * nz);
                            noise += 0.05 * simplexNoise.noise(0.04 * nx, 0.04 * nz);
                            noise += 0.20 * simplexNoise.noise(0.06 * nx, 0.06 * nz);
                            noise += 0.10 * simplexNoise.noise(0.08 * nx, 0.08 * nz);
                            noise += 0.05 * simplexNoise.noise(0.1 * nx, 0.1 * nz);
                            noise /= 3.15;
                            double k = (1 + noise) / 2;

                            //To enhance the flat areas or create news ones the redistribution function can be changed
                            k = Math.pow((float) k, .75);

                            //Altitude ranges from 0 to 64
                            k = Math.round(k / levels);

                            blocks.add(new Voxel(new Vector3f(i, (float) k, j), VoxelType.GRASS));
                            blocks.add(new Voxel(new Vector3f(i, (float) (k - 1), j), VoxelType.DIRT));
                        }
                    }

                    chunk = new Chunk(blocks, vector, chunkSize);

                    usedPos.add(vector);
                    chunks.add(new ChunkMesh(chunk));
                }
            }
        }
    }

    public void update(Vector3f camPos) {
        this.camPos = camPos;

        if (index < chunks.size()) {
            for (long i = index; i < chunks.size(); i++) {
                ChunkMesh chunk = chunks.get((int) i);
                newModel = loader.loadModel(chunk.positions, chunk.uvs);

                try {
                    newModel.setTexture(texture);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                entities.add(new Entity(newModel, chunk.chunk.getOrigin(), new Vector3f(0, 0, 0), 1));

            }

            index++;
        }
    }

    public Map<Model, List<Entity>> getTerrain() {
        entitiesMap = new HashMap<>();

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            Vector3f pos = entity.getPos();

            float distX = (camPos.x - pos.x);
            float distZ = (camPos.z - pos.z);

            if ((Math.abs(distX) <= (size / 2)) && (Math.abs(distZ) <= (size / 2))) {
                addEntity(entity);
            }
        }

        return entitiesMap;
    }


    public void addEntity(Entity entity) {
        List<Entity> entitiesList = entitiesMap.get(entity.getModel());
        if (entitiesList != null) {
            if (!entitiesList.contains(entity)) {
                entitiesList.add(entity);
                entitiesMap.put(entity.getModel(), entitiesList);
            }
        } else {
            List<Entity> aux = new ArrayList<>();
            aux.add(entity);
            entitiesMap.put(entity.getModel(), aux);
        }
    }

    public void stopLoop() {
        running = false;
    }

    public void run() {
        try {
            generateTerrain();
            while (running) {
                generateTerrain();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
