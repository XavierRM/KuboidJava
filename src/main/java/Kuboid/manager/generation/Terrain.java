package Kuboid.manager.generation;

import Kuboid.manager.ObjectLoader;
import Kuboid.manager.entity.Entity;
import Kuboid.manager.model.Model;
import Kuboid.manager.model.Texture;
import Kuboid.manager.utils.PerlinNoise;
import Kuboid.manager.voxel.Voxel;
import Kuboid.manager.voxel.VoxelType;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.*;
import java.util.random.RandomGenerator;

public class Terrain implements Runnable {

    private ObjectLoader loader;
    private Model model, newModel;
    private PerlinNoise generator;

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
//    private List<Vector3f> usedAbsolutePositions = Collections.synchronizedList(new ArrayList<>());

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

    public Terrain(long chunksPerAxis, long chunkSize, boolean plain, boolean isWireframe, Vector3f camPos) {
        this.size = chunksPerAxis * chunkSize;
        this.chunkSize = chunkSize;
        this.chunkDepth = 1;
        this.plain = plain;
        this.isWireframe = isWireframe;
        this.camPos = camPos;
        this.loader = new ObjectLoader();
        this.generator = new PerlinNoise();

        RandomGenerator random = RandomGenerator.of("Random");
        generator.setSeed(random.nextLong());

        if (isWireframe)
            model = loader.loadModel(verticesDirt, indicesDirt);
        else {
            //model = loader.loadModel(verticesDirt, textCoordsDirt, indicesDirt);
            /*try {
                model.setTexture(new Texture(loader.loadTexture("textures/dirt.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            /*model = loader.loadModel(verticesGrassBlock, textCoordsGrassBlock, indicesGrassBlock);
            try {
                model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }

    public void setWireframe(boolean wireframe) {
        isWireframe = wireframe;
    }

    public void generateTerrain() {
        long x, y = 0, z;

        Vector3f vector;
        Chunk chunk;

        System.out.println(size / 2);

        //This could be optimized for sure
        for (x = ((int) camPos.x - (size / 2)) / chunkSize; x < ((int) camPos.x + (size / 2)) / chunkSize; x++) {
            for (z = ((int) camPos.z - (size / 2)) / chunkSize; z < ((int) camPos.z + (size / 2)) / chunkSize; z++) {
                vector = new Vector3f(x * chunkSize, 0, z * chunkSize);

                if (!usedPos.contains(vector)) {

                    List<Voxel> blocks = new ArrayList<>();

                    for (int i = 0; i < chunkSize; i++) {
                        for (int j = 0; j < chunkSize; j++) {
                            float k = generator.generateHeight((int) (i + (x * chunkSize)), (int) (j + (z * chunkSize)));
                            //for (int k = (int) perlinNoiseGenerator.generateHeight((int) (i + (x * chunkSize)), (int) (j + (z * chunkSize))); k > -chunkDepth; k--) {
                            blocks.add(new Voxel(new Vector3f(i, k, j), VoxelType.DIRT));
                            //usedAbsolutePositions.add(new Vector3f((vector.x * chunkSize) + i, vector.y + k, (vector.z * chunkSize) + j));
                            //}
                        }
                    }

                    chunk = new Chunk(blocks, vector, chunkSize);

                    usedPos.add(vector);
                    chunks.add(new ChunkMesh(chunk));
                }
            }
        }
    }

    public void generateTerrainDiamondSquare() {
        int TOP_LEFT = 0;
        int TOP_RIGHT = 1;
        int BOTTOM_RIGHT = 2;
        int BOTTOM_LEFT = 3;

        List<Vector2i> corners = new ArrayList<>();

        //TOP_LEFT
        corners.add(new Vector2i((int) (camPos.x - (size / 2)), (int) (camPos.z - (size / 2))));
        //TOP_RIGHT
        corners.add(new Vector2i((int) (camPos.x + (size / 2)), (int) (camPos.z - (size / 2))));
        //BOTTOM_RIGHT
        corners.add(new Vector2i((int) (camPos.x - (size / 2)), (int) (camPos.z + (size / 2))));
        //BOTTOM_LEFT
        corners.add(new Vector2i((int) (camPos.x + (size / 2)), (int) (camPos.z + (size / 2))));

        //Matrix to calculate DiamondSquare result
        float[][] m = new float[(int) size + 1][(int) size + 1];

        m[0][0] = generator.generateHeight(corners.get(TOP_LEFT).x, corners.get(TOP_LEFT).y);
        m[0][m.length] = generator.generateHeight(corners.get(TOP_RIGHT).x, corners.get(TOP_RIGHT).y);
        m[m.length][m.length] = generator.generateHeight(corners.get(BOTTOM_RIGHT).x, corners.get(BOTTOM_RIGHT).y);
        m[m.length][0] = generator.generateHeight(corners.get(BOTTOM_LEFT).x, corners.get(BOTTOM_LEFT).y);


    }

    private static float[][] diamondSquare(float[][] matrix, Vector2i start, Vector2i end) {

        Vector2i midpoint = new Vector2i(Math.round((end.x - start.x) / 2) + start.x, Math.round((end.y - start.y) / 2) + start.y);

        //Calculate center
        matrix[midpoint.x][midpoint.y] = matrix[start.x][start.y] + matrix[start.x][end.y] + matrix[end.x][end.y] + matrix[end.x][start.y];
        matrix[midpoint.x][midpoint.y] = Math.round(matrix[midpoint.x][midpoint.y] / 4);

        //Calculate edges
        matrix[start.x][midpoint.y] = Math.round((matrix[start.x][start.y] + matrix[midpoint.x][midpoint.y] + matrix[start.x][end.y]) / 3);
        matrix[midpoint.x][start.y] = Math.round((matrix[start.x][start.y] + matrix[midpoint.x][midpoint.y] + matrix[end.x][start.y]) / 3);
        matrix[end.x][midpoint.y] = Math.round((matrix[midpoint.x][midpoint.y] + matrix[end.x][start.y] + matrix[end.x][end.y]) / 3);
        matrix[midpoint.x][end.y] = Math.round((matrix[midpoint.x][midpoint.y] + matrix[start.x][end.y] + matrix[end.x][end.y]) / 3);

        if ((end.x - start.x) >= 3) {
            matrix = diamondSquare(matrix, new Vector2i(start.x, start.y), new Vector2i(midpoint.x, midpoint.y));
            matrix = diamondSquare(matrix, new Vector2i(midpoint.x, start.y), new Vector2i(end.x, midpoint.y));
            matrix = diamondSquare(matrix, new Vector2i(start.x, midpoint.y), new Vector2i(midpoint.x, end.y));
            matrix = diamondSquare(matrix, new Vector2i(midpoint.x, midpoint.y), new Vector2i(end.x, end.y));
        }

        return matrix;
    }

    public void update(Vector3f camPos) {
        this.camPos = camPos;

        if (index < chunks.size()) {
            for (int i = 0; i < chunks.size(); i++) {
                newModel = loader.loadModel(chunks.get(i).positions, chunks.get(i).uvs);

                try {
                    newModel.setTexture(new Texture(loader.loadTexture("textures/dirt.png")));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                entities.add(new Entity(newModel, chunks.get(i).chunk.getOrigin(), new Vector3f(0, 0, 0), 1));

            }

            index++;
        }
    }

    public Map<Model, List<Entity>> getTerrain() {
        entitiesMap = new HashMap<>();
        //activeChunks = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            Vector3f pos = entity.getPos();

            float distX = (camPos.x - pos.x);
            float distZ = (camPos.z - pos.z);

            if ((Math.abs(distX) <= (size / 2)) && (Math.abs(distZ) <= (size / 2))) {
                addEntity(entity);

                /*for (int j = 0; j < chunks.size(); j++) {
                    if(pos.equals(chunks.get(j).chunk.getOrigin())) {
                        activeChunks.add(chunks.get(j));
                    }
                }*/
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

    public static void main(String args[]) {
        float[][] matrix = new float[5][5];

        matrix[0][0] = 10;
        matrix[0][matrix.length - 1] = 5;
        matrix[matrix.length - 1][matrix.length - 1] = 7;
        matrix[matrix.length - 1][0] = 1;

        matrix = diamondSquare(matrix, new Vector2i(0, 0), new Vector2i(matrix.length - 1, matrix.length - 1));

        for (float[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
