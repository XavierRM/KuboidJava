package Kuboid.manager.utils;

import org.joml.Vector2i;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;

public class Utils {

    private static FloatBuffer floatBuffer;
    private static IntBuffer intBuffer;

    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        floatBuffer = memAllocFloat(data.length);
        floatBuffer.put(data).flip();
        return floatBuffer;
    }

    public static IntBuffer storeDataInIntBuffer(int[] data) {
        intBuffer = memAllocInt(data.length);
        intBuffer.put(data).flip();
        return intBuffer;
    }

    public static String loadResource(String filename) throws Exception {
        String result;

        try (InputStream in = Utils.class.getResourceAsStream(filename);
             Scanner scanner = new Scanner(in, UTF_8.name())) {

            result = scanner.useDelimiter("\\A").next();
        }

        return result;
    }

    /*public float[][] generateTerrainDiamondSquare() {
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

        System.out.println(corners.toString());
        System.out.println();

        //Matrix to calculate DiamondSquare result
        float[][] m = new float[(int) size + 1][(int) size + 1];

        //PerlinNoise
        m[0][0] = generator.generateHeight(corners.get(TOP_LEFT).x, corners.get(TOP_LEFT).y);
        m[0][m.length - 1] = generator.generateHeight(corners.get(TOP_RIGHT).x, corners.get(TOP_RIGHT).y);
        m[m.length - 1][m.length - 1] = generator.generateHeight(corners.get(BOTTOM_RIGHT).x, corners.get(BOTTOM_RIGHT).y);
        m[m.length - 1][0] = generator.generateHeight(corners.get(BOTTOM_LEFT).x, corners.get(BOTTOM_LEFT).y);

        //System.out.println(generator.generateHeight(corners.get(TOP_LEFT).x, corners.get(TOP_LEFT).y));
        //System.out.println(generator.generateHeight(corners.get(TOP_RIGHT).x, corners.get(TOP_RIGHT).y));
        //System.out.println(generator.generateHeight(corners.get(BOTTOM_RIGHT).x, corners.get(BOTTOM_RIGHT).y));
        //System.out.println(generator.generateHeight(corners.get(BOTTOM_LEFT).x, corners.get(BOTTOM_LEFT).y));
        //System.out.println();

        var result = diamondSquare(m, new Vector2i(0, 0), new Vector2i((int) size, (int) size));

        return result;

    }*/

    public static float[][] diamondSquare(float[][] matrix, Vector2i start, Vector2i end) {

        Vector2i midpoint = new Vector2i(Math.round((end.x - start.x) / 2) + start.x, Math.round((end.y - start.y) / 2) + start.y);

        //Calculate center
        matrix[midpoint.x][midpoint.y] = matrix[start.x][start.y] + matrix[start.x][end.y] + matrix[end.x][end.y] + matrix[end.x][start.y];
        matrix[midpoint.x][midpoint.y] = Math.round(matrix[midpoint.x][midpoint.y] / 4);

        //Calculate edges - Could have slightly better precision if in some cases I used 4 point instead of always 3 for the average
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
}
