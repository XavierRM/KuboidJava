package Kuboid.manager.generation;

import Kuboid.manager.ObjectLoader;
import Kuboid.manager.entity.Entity;
import Kuboid.manager.model.Model;
import Kuboid.manager.model.Texture;
import Kuboid.manager.utils.Pair;
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

    private long size;
    private long seed = RandomGenerator.of("Random").nextLong(Integer.MAX_VALUE);
    private long index = 0;
    private long chunkSize, chunkDepth;
    private boolean plain;
    private boolean isWireframe;
    private boolean running = true;
    private Vector3f camPos;
    private boolean rebuildMeshes = false;

    private Map<Model, List<Entity>> entitiesMap = Collections.synchronizedMap(new HashMap<>());
    private List<ChunkMesh> chunkMeshes = Collections.synchronizedList(new ArrayList<>());
    private List<Chunk> activeChunks = Collections.synchronizedList(new ArrayList<>());
    private List<Vector3f> blockPositions = Collections.synchronizedList(new ArrayList<>());
    public List<Vector3f> normalsList = Collections.synchronizedList(new ArrayList<>());
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
        this.chunkDepth = -4;
        this.plain = plain;
        this.isWireframe = isWireframe;
        this.camPos = camPos;
        this.loader = new ObjectLoader();
        this.generator = new PerlinNoise();
        this.simplexNoise = new SimplexNoise();
        this.texture = new Texture(loader.loadTexture("textures/default_texture.png"));

        SimplexNoise.setSeed(seed);

        System.out.println(seed);
//        SimplexNoise.setSeed(1000);

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

                            double noise = 2 * simplexNoise.noise(0.01 * nx, 0.01 * nz);
                            noise += 2 * simplexNoise.noise(0.005 * nx, 0.005 * nz);
                            noise += 0.75 * simplexNoise.noise(0.03 * nx, 0.03 * nz);
                            noise += 0.20 * simplexNoise.noise(0.04 * nx, 0.04 * nz);
                            noise += 0.25 * simplexNoise.noise(0.06 * nx, 0.06 * nz);
                            noise += 0.20 * simplexNoise.noise(0.08 * nx, 0.08 * nz);
                            noise += 0.15 * simplexNoise.noise(0.1 * nx, 0.1 * nz);
                            noise /= 3.65;
                            double k = (1 + noise) / 2;

                            //To enhance the flat areas or create new ones the redistribution function can be changed
                            k = Math.pow((float) k, 1.5);

                            //Altitude ranges from 1 to levels^(-1)
                            k = Math.floor(k / levels);

                            for (int v = (int) k; v > (k + chunkDepth); v--) {
                                if (v > 10) {
                                    if (v == k) {
                                        blocks.add(new Voxel(new Vector3f(i, (float) v, j), VoxelType.GRASS));
                                    } else
                                        blocks.add(new Voxel(new Vector3f(i, (float) v, j), VoxelType.DIRT));
                                } else {
                                    blocks.add(new Voxel(new Vector3f(i, (float) v, j), VoxelType.DIRT));
                                }
                            }
                        }
                    }

                    chunk = new Chunk(blocks, vector, chunkSize);

                    usedPos.add(vector);
                    chunkMeshes.add(new ChunkMesh(chunk));
                }
            }
        }
    }

    public void update(Vector3f camPos) {
        this.camPos = camPos;

        if (!rebuildMeshes) {
            if (index < chunkMeshes.size()) {
                for (long i = index; i < chunkMeshes.size(); i++) {
                    ChunkMesh chunk = chunkMeshes.get((int) i);
                    newModel = loader.loadModel(chunk.positions, chunk.uvs, chunk.normals);

                    try {
                        newModel.setTexture(texture);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    entities.add(new Entity(newModel, chunk.chunk.getOrigin(), new Vector3f(0, 0, 0), 1));

                }

                index++;
            }
        } else {
            rebuildMeshes = false;
            entities = new ArrayList<>();

            for (long i = 0; i < chunkMeshes.size(); i++) {
                ChunkMesh chunk = chunkMeshes.get((int) i);
                newModel = loader.loadModel(chunk.positions, chunk.uvs, chunk.normals);

                try {
                    newModel.setTexture(texture);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                entities.add(new Entity(newModel, chunk.chunk.getOrigin(), new Vector3f(0, 0, 0), 1));

            }
        }
    }

    public Map<Model, List<Entity>> getTerrain() {
        entitiesMap = new HashMap<>();
        int totalVertexCount = 0;

        for (Entity entity : entities) {
            totalVertexCount += entity.getModel().getVertexCount();

            Vector3f pos = entity.getPos();

            float distX = (camPos.x - pos.x);
            float distZ = (camPos.z - pos.z);

            if ((Math.abs(distX) <= (size / 2)) && (Math.abs(distZ) <= (size / 2))) {
                addEntity(entity);
            }
        }

        return entitiesMap;
    }

    public List<Vector3f> getActiveBlockPositions() {
        activeChunks = new ArrayList<>();
        blockPositions = new ArrayList<>();

        for (ChunkMesh chunkMesh : chunkMeshes) {
            Vector3f pos = chunkMesh.chunk.getOrigin();

            float distX = (camPos.x - pos.x);
            float distZ = (camPos.z - pos.z);

            if ((Math.abs(distX) <= (size / 2)) && (Math.abs(distZ) <= (size / 2))) {
                activeChunks.add(chunkMesh.chunk);
            }
        }

        for (Chunk chunk : activeChunks) {
            List<Voxel> voxels = chunk.getVoxels();

            for (Voxel voxel : voxels) {
                blockPositions.add(new Vector3f(voxel.origin).add(chunk.getOrigin()));
            }
        }

        return blockPositions;
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

    private Pair<ChunkMesh, Integer> getChunk(Vector3f chunkOriginPos) {
        ChunkMesh chunk = null;
        int index = -1;

        for (int i = 0; i < chunkMeshes.size(); i++) {
            ChunkMesh chunkMesh = chunkMeshes.get(i);

            if (chunkMesh.chunk.getOrigin().equals(chunkOriginPos.x, chunkOriginPos.y, chunkOriginPos.z)) {
                chunk = chunkMesh;
                index = i;
            }
        }

        return new Pair<>(chunk, index);
    }

    public void removeVoxel(Vector3f position) {
        Vector3f chunkPos = new Vector3f((int) Math.floor(position.x / chunkSize),
                0,
                (int) Math.floor(position.z / chunkSize));
        Vector3f chunkOriginPos = new Vector3f(chunkPos).mul(chunkSize);

        Pair<ChunkMesh, Integer> resultGetChunk = getChunk(chunkOriginPos);

        ChunkMesh chunk = resultGetChunk.p1;
        int index = resultGetChunk.p2;


        if (chunk != null && (index != -1)) {
            chunk.deleteVoxel(new Vector3f(position).sub(chunkOriginPos));
            chunk.updateMesh();
            chunkMeshes.remove(index);
            chunkMeshes.add(index, chunk);
            rebuildMeshes = true;
        }
    }

    public void addVoxel(Vector3f position) {
        Vector3f chunkPos = new Vector3f((int) Math.floor(position.x / chunkSize),
                0,
                (int) Math.floor(position.z / chunkSize));
        Vector3f chunkOriginPos = new Vector3f(chunkPos).mul(chunkSize);

        Pair<ChunkMesh, Integer> resultGetChunk = getChunk(chunkOriginPos);

        ChunkMesh chunk = resultGetChunk.p1;
        int index = resultGetChunk.p2;


        if (chunk != null && (index != -1)) {
            chunk.addVoxel(new Vector3f(position).sub(chunkOriginPos));
            chunk.updateMesh();
            chunkMeshes.remove(index);
            chunkMeshes.add(index, chunk);
            rebuildMeshes = true;
        }
    }

    public void stopLoop() {
        running = false;
    }

    public void run() {
        try {
//            generateTerrain();

            while (running) {
                generateTerrain();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }
}
