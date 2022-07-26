package Kuboid.manager;

import org.joml.Vector3f;

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

        this.rotation.x %= 360;
        this.rotation.y %= 360;
        this.rotation.z %= 360;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

}
