package Kuboid.manager.lighting;

import Kuboid.manager.utils.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Light {

    private Vector3f position;
    private Vector3f orientation;
    private Vector3f color;
    private float intensity;

    private int intervals;
    private int currentInterval;

    public Light(Vector3f position, Vector3f orientation, Vector3f color, float intensity) {
        this.position = position;
        this.orientation = orientation;
        this.color = color;

        //Check intensity is in bounds
        if (intensity > 1)
            this.intensity = 1;
        else {
            if (intensity < 0)
                this.intensity = 0;
            else
                this.intensity = intensity;
        }
    }

    public Light(Vector3f position, Vector3f orientation) {
        this.position = position;
        this.orientation = orientation;
        this.color = new Vector3f(201, 226, 255);
        this.intensity = 1f;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getOrientation() {
        return orientation;
    }

    public void setOrientation(Vector3f orientation) {
        this.orientation = orientation;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Matrix4f getMVPMatrix() {
        // Compute MVP from the lights POV
        Matrix4f depthProjectionMatrix = Transformation.createOrtho(-10, 10, -10, 10, -10, 20);
        Matrix4f depthViewMatrix = Transformation.getViewMatrix(position, orientation);
        Matrix4f depthModelMatrix = new Matrix4f(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f);

        Matrix4f depthMVP = (new Matrix4f(depthProjectionMatrix).mul(new Matrix4f(depthViewMatrix))).mul(new Matrix4f(depthModelMatrix));

        return depthMVP;
    }
}
