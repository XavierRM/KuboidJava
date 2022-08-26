package Kuboid.manager.lighting;

import org.joml.Vector3f;

import java.text.NumberFormat;

public class Sun {

    /* So the sun has 3 main stages, Dawn, Dusk and MidDay.
     *
     *  Dawn: (1, 0, 0)
     *  MidDay: (0, 1, 0)
     *  Dusk: (-1, 0, 0)
     *
     *  In that order we'll start at dawn and finish at dusk, to represent the
     *  night, then we'll set the intensity to 0f, but we'll keep updating the suns
     *  position. With all of this we should have a day-night cycle.
     */

    private DirectionalLight directionalLight;
    private float increment = 1.1f;
    private float posMultiplier = 100f;
    private float lightAngle = 90f;

    public Sun(DirectionalLight light, float increment) {
        this.directionalLight = light;
        this.increment = increment;
    }

    public Sun(DirectionalLight light) {
        this.directionalLight = light;

    }

    public Sun(Vector3f direction, Vector3f color, float intensity) {
        this.directionalLight = new DirectionalLight(new Vector3f(direction).mul(posMultiplier), new Vector3f(1f, 1f, 1f), 1f);
    }

    public Sun(Vector3f direction, Vector3f color, float intensity, float increment) {
        this.directionalLight = new DirectionalLight(new Vector3f(direction).mul(posMultiplier), new Vector3f(1f, 1f, 1f), 1f);
        this.increment = increment;
    }

    public void printValues() {
        System.out.println("Light direction inside Sun: " + this.directionalLight.getDirection().toString(NumberFormat.getNumberInstance()));
        System.out.println("Light position inside Sun: " + this.directionalLight.getPosition().toString(NumberFormat.getNumberInstance()));
    }

    public void update() {
        //Update directional light direction, intensity and colour

        lightAngle -= increment;

        if (lightAngle < -90) {
            directionalLight.setIntensity(0f);

            if (lightAngle <= -360) {
                lightAngle = 90f;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (Math.abs(lightAngle) - 80) / 10f;

            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1f);
            directionalLight.getColor().x = 1f;
            directionalLight.getColor().y = 1f;
            directionalLight.getColor().z = 1f;
        }

        float directionX = (float) Math.sin(lightAngle);
        float directionY = (float) Math.cos(lightAngle);
        float directionZ = directionalLight.getDirection().z;

        directionalLight.setDirection(new Vector3f(directionX, directionY, directionZ));
    }

    public DirectionalLight getDirectionalLight() {

        return directionalLight;
    }
}
