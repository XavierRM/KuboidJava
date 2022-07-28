package Kuboid.manager.utils;

import Kuboid.manager.Camera;
import Kuboid.manager.WindowManager;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

    private static Vector2f getNormalizedScreenPos(Vector2f pos, WindowManager windowManager) {
        Vector2f result = new Vector2f();

        result.x = (2 * pos.x) / windowManager.getWidth() - 1f;
        result.y = (2 * pos.y) / windowManager.getHeight() - 1f;

        return result;
    }

    private static Vector4f toEyeCoords(Vector4f clipCoords, Matrix4f projectionMatrix) {
        Matrix4f invertedProjectionMatrix = projectionMatrix.invert();
        Vector4f eyeCoords = invertedProjectionMatrix.transform(clipCoords);

        //System.out.println("eyeCoords: " + new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 1f).toString(NumberFormat.getNumberInstance()));

        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 1f);
    }

    private static Vector3f toWorldCoords(Vector4f eyeCoords, Matrix4f viewMatrix) {
        Matrix4f invertedViewMatrix = viewMatrix.invert();
        Vector4f rayWorld = invertedViewMatrix.transform(eyeCoords);

        //System.out.println("worldCoords: " + new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z).toString(NumberFormat.getNumberInstance()));

        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);

        //Normalize the ray
        float lengthMouseRay = mouseRay.length();
        mouseRay.y /= lengthMouseRay;
        mouseRay.y *= 2;
        //mouseRay.normalize();

        return mouseRay;
    }

    public static Vector3f convert2DPositionTo3D(Vector2f pos2D, Camera camera, WindowManager window) {

        /*
         * TODO: Right now the value of 'z' is always -1 which theoretically makes sense for a camera that can move
         *       freely but is always pointing to the '-z' direction, in my case I can be pointing in any 'z' value
         *       so it should vary when I rotate the camera, the 'x' and 'y' axis are fine, but for the 'z' I should
         *       consider the camera rotation in the 'y' axis to calculate the value.
         *
         */

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

        //worldRay.normalize();

        return worldRay;
    }
}
