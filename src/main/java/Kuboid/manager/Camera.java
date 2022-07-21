package Kuboid.manager;

import Kuboid.manager.utils.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {

    private Vector3f position, rotation;

    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.rotation = rotation;
        this.position = position;
    }

    public void movePosition(float x, float y, float z) {

        //forward and backwards
        if(z != 0) {
            position.x -= (float) Math.sin(Math.toRadians(rotation.y)) * z;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
        }

        //left and right
        if(x != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y + 90)) * x;
            position.z -= (float) Math.cos(Math.toRadians(rotation.y + 90)) * x;
        }

        position.y -= y;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void moveRotation(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getCenterPos(WindowManager window) {
        //Point to convert from screen coordinates to world coordinates
        Vector2f screenPos = new Vector2f(window.getWidth() / 2, window.getHeight() / 2);

        Matrix4f viewMatrix = Transformation.getViewMatrix(this);
        Matrix4f projectionMatrix = window.getProjectionMatrix();

        Matrix4f vpMatrixInverted = viewMatrix.mul(projectionMatrix).invert();

        Vector4f inWorldPos = new Vector4f(
                (2.0f * ((float) (screenPos.x) / window.getWidth())) - 1.0f,
                1.0f - (2.0f * ((float) (screenPos.y) / window.getHeight())),
                1.0f,
                1.0f
        );

        Vector4f newPos = inWorldPos.mul(vpMatrixInverted);

        return new Vector3f(newPos.x / newPos.w, newPos.y / newPos.w, newPos.z / newPos.w);
    }
}
