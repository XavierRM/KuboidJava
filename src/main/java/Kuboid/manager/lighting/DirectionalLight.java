package Kuboid.manager.lighting;

import Kuboid.manager.utils.Transformation;
import Kuboid.manager.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static Kuboid.manager.utils.Constants.Z_FAR;
import static Kuboid.manager.utils.Constants.Z_NEAR;

public class DirectionalLight {

    private Vector3f position;
    private Vector3f direction;
    private Vector3f angles;
    private Vector3f color;
    private float intensity;

//    public Light(Vector3f direction, Vector3f color, float intensity) {
//        Vector3f anglesVector;
//        this.direction = direction;
//        this.position = direction.mul(2);
//
//        float lightAngleX = (float) Math.toDegrees(Math.acos(direction.z));
//        float lightAngleY = (float) Math.toDegrees(Math.asin(direction.x));
//        float lightAngleZ = 0;
//
//        anglesVector = new Vector3f(lightAngleX, lightAngleY, lightAngleZ);
//        this.angles = anglesVector;
//        this.color = color;
//
//        //Check intensity is in bounds
//        if (intensity > 1)
//            this.intensity = 1;
//        else {
//            if (intensity < 0)
//                this.intensity = 0;
//            else
//                this.intensity = intensity;
//        }
//    }

    public DirectionalLight(Vector3f position) {
        this.direction = new Vector3f(position).negate().normalize();
        this.position = new Vector3f(position);

        this.angles = Utils.calculateAngles(direction);

        this.color = new Vector3f(201, 226, 255);
        this.intensity = 1f;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = new Vector3f(position);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        //Recalculate anglesVector as well
        this.direction = direction;
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

    public Matrix4f getVPMatrix() {
        // Compute VP from the lights POV
        Matrix4f depthProjectionMatrix = new Matrix4f();
        depthProjectionMatrix.identity();
        depthProjectionMatrix.ortho(-200, 200, -200, 200, Z_NEAR, Z_FAR);
        Matrix4f depthViewMatrix = Transformation.getViewMatrix(position, angles);

        Matrix4f depthVP = (new Matrix4f(depthProjectionMatrix).mul(new Matrix4f(depthViewMatrix)));

        return depthVP;
    }
}
