package Kuboid.manager.utils;

import Kuboid.manager.Camera;
import Kuboid.manager.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    public static Matrix4f createTransformationMatrix(Entity entity) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(entity.getPos()).
                rotateX((float) Math.toRadians(entity.getRotation().x)).
                rotateY((float) Math.toRadians(entity.getRotation().y)).
                rotateZ((float) Math.toRadians(entity.getRotation().z)).
                scale(entity.getScale());

        return matrix;
    }

    public static Matrix4f getViewMatrix(Camera camera) {
        Vector3f pos = camera.getPosition();
        Vector3f rot = camera.getRotation();

        Matrix4f matrix = new Matrix4f();
        matrix.identity();

        matrix.rotate((float) Math.toRadians(rot.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rot.y), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(rot.z), new Vector3f(0, 0, 1));
        matrix.translate(-pos.x, -pos.y, -pos.z);

        return matrix;
    }

    public static Matrix4f getViewMatrix(Vector3f position, Vector3f rotation) {
        Vector3f pos = position;
        Vector3f rot = rotation;

        Matrix4f matrix = new Matrix4f();
        matrix.identity();

        matrix.rotate((float) Math.toRadians(rot.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rot.y), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(rot.z), new Vector3f(0, 0, 1));
        matrix.translate(-pos.x, -pos.y, -pos.z);

        return matrix;
    }

    public static Matrix4f createOrtho(float left, float right, float bottom, float top, float near, float far) {

        final Matrix4f matrix = new Matrix4f();
        matrix.identity();

        matrix.m00(2.0f / (right - left));
        matrix.m01(0);
        matrix.m02(0);
        matrix.m03(0);
        matrix.m10(0);
        matrix.m11(2.0f / (top - bottom));
        matrix.m12(0);
        matrix.m13(0);
        matrix.m20(0);
        matrix.m21(0);
        matrix.m22(-2.0f / (far - near));
        matrix.m23(0);
        matrix.m30(-(right + left) / (right - left));
        matrix.m31(-(top + bottom) / (top - bottom));
        matrix.m32(-(far + near) / (far - near));
        matrix.m33(1);

        return matrix;
    }
}
