package Kuboid.manager.lighting;

import Kuboid.manager.utils.Transformation;
import Kuboid.manager.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static Kuboid.manager.utils.Constants.Z_FAR;
import static Kuboid.manager.utils.Constants.Z_NEAR;

public class DirectionalLight {

    private float directionX, directionY, directionZ;
    private float positionX, positionY, positionZ;
    private float anglesX, anglesY, anglesZ;
    private Vector3f color;
    private Vector3f position;
    private Vector3f angles;
    private float intensity;
    private float posMultiplier = 100f;

    public DirectionalLight(Vector3f position, Vector3f color, float intensity) {
        Vector3f direction = new Vector3f(position).negate().normalize();
        setDirection(direction);
        setPosition(position);

        Vector3f auxAngles = Utils.calculateAngles(new Vector3f(direction));

        setAngles(auxAngles);

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

    public DirectionalLight(Vector3f position) {
        Vector3f auxPosition = new Vector3f(position);
        Vector3f direction = (auxPosition.negate()).normalize();
        setDirection(direction);

        this.position = new Vector3f(position);
//        setPosition(position);
        Vector3f auxAngles = Utils.calculateAngles(new Vector3f(direction));
        setAngles(auxAngles);

        this.color = new Vector3f(0.78515625f, 0.8828125f, 1f);
        this.intensity = 1f;
    }

    public Vector3f getPosition() {

        return this.position;
    }

    public void setPosition(Vector3f position) {
        this.positionX = position.x;
        this.positionY = position.y;
        this.positionZ = position.z;
    }

    public Vector3f getDirection() {
        return new Vector3f(directionX, directionY, directionZ);
    }

    public void setDirection(Vector3f direction) {
        this.directionX = direction.x;
        this.directionY = direction.y;
        this.directionZ = direction.z;

        //Recalculate anglesVector as well
        Vector3f directionAux = new Vector3f(direction).negate();
        setPosition(new Vector3f(direction).mul(posMultiplier));
        setAngles(Utils.calculateAngles(directionAux));
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

    private void setAngles(Vector3f angles) {
        this.anglesX = angles.x;
        this.anglesY = angles.y;
        this.anglesZ = angles.z;
    }

    public Vector3f getAngles() {
        return new Vector3f(anglesX, anglesY, anglesZ);
    }

    public Matrix4f getVPMatrix() {
        // Compute VP from the lights POV
        Matrix4f depthProjectionMatrix = new Matrix4f();
        depthProjectionMatrix.identity();
        depthProjectionMatrix.ortho(-200, 200, -200, 200, Z_NEAR, Z_FAR);
        Matrix4f depthViewMatrix = Transformation.getViewMatrix(getPosition(), getAngles());

        Matrix4f depthVP = (new Matrix4f(depthProjectionMatrix).mul(new Matrix4f(depthViewMatrix)));

        return depthVP;
    }

    public Matrix4f getVMatrix() {
        Matrix4f viewMatrix = new Matrix4f(Transformation.getViewMatrix(getPosition(), getAngles()));

        return viewMatrix;
    }
}
