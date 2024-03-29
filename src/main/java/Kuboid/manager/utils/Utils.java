package Kuboid.manager.utils;

import Kuboid.manager.Camera;
import Kuboid.manager.WindowManager;
import org.joml.*;

import java.io.InputStream;
import java.lang.Math;
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

    private static Vector2f getNormalizedScreenPos(Vector2f pos, WindowManager windowManager) {
        Vector2f result = new Vector2f();

        result.x = (2 * pos.x) / windowManager.getWidth() - 1f;
        result.y = (2 * pos.y) / windowManager.getHeight() - 1f;

        return result;
    }

    private static Vector4f toEyeCoords(Vector4f clipCoords, Matrix4f projectionMatrix) {
        Matrix4f invertedProjectionMatrix = projectionMatrix.invert();
        Vector4f eyeCoords = invertedProjectionMatrix.transform(clipCoords);

        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 1f);
    }

    private static Vector3f toWorldCoords(Vector4f eyeCoords, Matrix4f viewMatrix) {
        Matrix4f invertedViewMatrix = viewMatrix.invert();
        Vector4f rayWorld = invertedViewMatrix.transform(eyeCoords);

        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);

        //Normalize the ray
        float lengthMouseRay = mouseRay.length();
        mouseRay.y /= lengthMouseRay;
        mouseRay.y *= 2;

        return mouseRay;
    }

    public static Vector3f convert2DPositionTo3D(Vector2f pos2D, Camera camera, WindowManager window) {

        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);
        Matrix4f projectionMatrix = window.getProjectionMatrix();

        //Normalized the screen position to the OpenGL coordinate system from -1 to 1
        Vector2f normalizedPos2D = getNormalizedScreenPos(pos2D, window);

        //Calculated the clip coords
        Vector4f clipCoords = new Vector4f(normalizedPos2D.x, normalizedPos2D.y, -1f, 0f);

        //Converted to the equivalent of the eye coords
        Vector4f eyeCoords = toEyeCoords(clipCoords, new Matrix4f(viewMatrix));

        //Calculate the world ray, as a unit vector (normalized)
        Vector3f worldRay = toWorldCoords(eyeCoords, new Matrix4f(projectionMatrix));

        worldRay.z = (float) -Math.cos(Math.toRadians(Math.abs(camera.getRotation().y))) * (float) Math.cos(Math.toRadians(Math.abs(camera.getRotation().x)));

        return worldRay;
    }

    public static Vector3d calculateDirection(Camera camera) {
        Vector3d direction = new Vector3d();

        direction.x = Math.sin(Math.toRadians(camera.getRotation().y)) * Math.cos(Math.toRadians(camera.getRotation().x));

        direction.y = -Math.sin(Math.toRadians(camera.getRotation().x));

        direction.z = -Math.cos(Math.toRadians(Math.abs(camera.getRotation().y))) * Math.cos(Math.toRadians(Math.abs(camera.getRotation().x)));

        return new Vector3d(direction);
    }

    public static Vector3d calculateDirection(Vector3f rotation) {
        Vector3d direction = new Vector3d();

        direction.x = Math.sin(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x));

        direction.y = -Math.sin(Math.toRadians(rotation.x));

        direction.z = -Math.cos(Math.toRadians(Math.abs(rotation.y))) * Math.cos(Math.toRadians(Math.abs(rotation.x)));

        return new Vector3d(direction);
    }

    public static Vector3f calculateAngles(Vector3f direction) {
        Vector3f angles = new Vector3f();

        //Pitch
        angles.x = (float) Math.toDegrees(Math.acos(new Vector2f(direction.x, direction.z).length()));
        //Yaw
        angles.y = (float) Math.toDegrees(Math.atan(direction.x / direction.z));
        angles.y = direction.z > 0 ? angles.y - 180 : angles.y;
        angles.y = -angles.y;

        angles.z = 0;

        return new Vector3f(angles);
    }
}
