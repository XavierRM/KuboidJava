package Kuboid.manager.model;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class VoxelModel {
    private static final float ERROR = 0.000f;

    public static Vector3f[] POS_PX = {

            new Vector3f(0.5f, 0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, 0.5f, 0.5f),
            new Vector3f(0.5f, 0.5f, -0.5f)

    };

    public static Vector3f[] POS_NX = {

            new Vector3f(-0.5f, 0.5f, -0.5f),
            new Vector3f(-0.5f, -0.5f, -0.5f),
            new Vector3f(-0.5f, -0.5f, 0.5f),
            new Vector3f(-0.5f, -0.5f, 0.5f),
            new Vector3f(-0.5f, 0.5f, 0.5f),
            new Vector3f(-0.5f, 0.5f, -0.5f)

    };

    public static Vector3f[] POS_PY = {

            new Vector3f(-0.5f, 0.5f, 0.5f),
            new Vector3f(-0.5f, 0.5f, -0.5f),
            new Vector3f(0.5f, 0.5f, -0.5f),
            new Vector3f(0.5f, 0.5f, -0.5f),
            new Vector3f(0.5f, 0.5f, 0.5f),
            new Vector3f(-0.5f, 0.5f, 0.5f)

    };

    public static Vector3f[] POS_NY = {

            new Vector3f(-0.5f, -0.5f, 0.5f),
            new Vector3f(-0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, 0.5f),
            new Vector3f(-0.5f, -0.5f, 0.5f)

    };

    public static Vector3f[] POS_PZ = {

            new Vector3f(-0.5f, 0.5f, 0.5f),
            new Vector3f(-0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, 0.5f, 0.5f),
            new Vector3f(-0.5f, 0.5f, 0.5f)

    };

    public static Vector3f[] POS_NZ = {

            new Vector3f(-0.5f, 0.5f, -0.5f),
            new Vector3f(-0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, 0.5f, -0.5f),
            new Vector3f(-0.5f, 0.5f, -0.5f)

    };

    public static Vector2f[] UV = {

            new Vector2f(0.f, 0.f),
            new Vector2f(0.f, 1.f),
            new Vector2f(1.f, 1.f),
            new Vector2f(1.f, 1.f),
            new Vector2f(1.f, 0.f),
            new Vector2f(0.f, 0.f)

    };

    public static Vector2f[] UV_PX = {

            // GRASS
            new Vector2f((3.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((3.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((3.f / 16.f) + ERROR, 0.f + ERROR),

            // DIRT
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),

            // STONE
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),

            // TREEBARK
            new Vector2f(4.f / 16.f, 0.f),
            new Vector2f(4.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 0.f),
            new Vector2f(4.f / 16.f, 0.f),

            // TREELEAF
            new Vector2f(6.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 0.f)

    };

    public static Vector2f[] UV_NX = {

            // GRASS
            new Vector2f((3.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((3.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((3.f / 16.f) + ERROR, 0.f + ERROR),

            // DIRT
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),

            // STONE
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),

            // TREEBARK
            new Vector2f(4.f / 16.f, 0.f),
            new Vector2f(4.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 0.f),
            new Vector2f(4.f / 16.f, 0.f),

            // TREELEAF
            new Vector2f(6.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 0.f)

    };

    public static Vector2f[] UV_PY = {

            // GRASS
            new Vector2f(0.f + ERROR, 0.f + ERROR),
            new Vector2f(0.f + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((1.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((1.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((1.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f(0.f + ERROR, 0.f + ERROR),

            // DIRT
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),

            // STONE
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),

            // TREEBARK
            new Vector2f(5.f / 16.f, 0.f),
            new Vector2f(5.f / 16.f, 1.f / 16.f),
            new Vector2f(6.f / 16.f, 1.f / 16.f),
            new Vector2f(6.f / 16.f, 1.f / 16.f),
            new Vector2f(6.f / 16.f, 0.f),
            new Vector2f(5.f / 16.f, 0.f),

            // TREELEAF
            new Vector2f(6.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 0.f)

    };

    public static Vector2f[] UV_NY = {

            // GRASS
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),

            // DIRT
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),

            // STONE
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),

            // TREEBARK
            new Vector2f(5.f / 16.f, 0.f),
            new Vector2f(5.f / 16.f, 1.f / 16.f),
            new Vector2f(6.f / 16.f, 1.f / 16.f),
            new Vector2f(6.f / 16.f, 1.f / 16.f),
            new Vector2f(6.f / 16.f, 0.f),
            new Vector2f(5.f / 16.f, 0.f),

            // TREELEAF
            new Vector2f(6.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 0.f)

    };

    public static Vector2f[] UV_PZ = {

            // GRASS
            new Vector2f((3.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((3.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((3.f / 16.f) + ERROR, 0.f + ERROR),

            // DIRT
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),

            // STONE
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),

            // TREEBARK
            new Vector2f(4.f / 16.f, 0.f),
            new Vector2f(4.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 0.f),
            new Vector2f(4.f / 16.f, 0.f),

            // TREELEAF
            new Vector2f(6.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 0.f)

    };

    public static Vector2f[] UV_NZ = {

            // GRASS
            new Vector2f((3.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((3.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((4.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((3.f / 16.f) + ERROR, 0.f + ERROR),

            // DIRT
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((3.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((2.f / 16.f) + ERROR, 0.f + ERROR),

            // STONE
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, (1.f / 16.f) - ERROR),
            new Vector2f((2.f / 16.f) - ERROR, 0.f + ERROR),
            new Vector2f((1.f / 16.f) + ERROR, 0.f + ERROR),

            // TREEBARK
            new Vector2f(4.f / 16.f, 0.f),
            new Vector2f(4.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 1.f / 16.f),
            new Vector2f(5.f / 16.f, 0.f),
            new Vector2f(4.f / 16.f, 0.f),

            // TREELEAF
            new Vector2f(6.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 1.f / 16.f),
            new Vector2f(7.f / 16.f, 0.f),
            new Vector2f(6.f / 16.f, 0.f)

    };

    public static Vector3f[] NORMALS = {

            new Vector3f(0.f, 0.f, 0.f),
            new Vector3f(0.f, 0.f, 0.f),
            new Vector3f(0.f, 0.f, 0.f),
            new Vector3f(0.f, 0.f, 0.f),
            new Vector3f(0.f, 0.f, 0.f),
            new Vector3f(0.f, 0.f, 0.f)

    };

    public static Vector3f NORMALS_PX = new Vector3f(1.f, 0.f, 0.f);
    public static Vector3f NORMALS_NX = new Vector3f(-1.f, 0.f, 0.f);
    public static Vector3f NORMALS_PY = new Vector3f(0.f, 1.f, 0.f);
    public static Vector3f NORMALS_NY = new Vector3f(0.f, -1.f, 0.f);
    public static Vector3f NORMALS_PZ = new Vector3f(0.f, 0.f, 1.f);
    public static Vector3f NORMALS_NZ = new Vector3f(0.f, 0.f, -1.f);

//    Here we should have the NORMALS divided in arrays based on direction like on the POSITIONS and the UV`s
//    Remember that each face has a total of 6 vertices which form the two triangles for rendering like it is done
//    in the positions and uv`s

    public static float[] vertices = {

            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,

            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,

            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,

            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,

            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,

            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f

    };

    public static int[] indices = {

            0, 1, 3,
            3, 1, 2,
            4, 5, 7,
            7, 5, 6,
            8, 9, 11,
            11, 9, 10,
            12, 13, 15,
            15, 13, 14,
            16, 17, 19,
            19, 17, 18,
            20, 21, 23,
            23, 21, 22

    };

    public static float[] uv = {

            0, 0,
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            1, 0

    };
}
