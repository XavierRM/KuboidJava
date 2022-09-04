package Kuboid.manager.persistency;

import org.joml.Vector3f;

public abstract class Change {
    private int locationX;
    private int locationY;
    private int locationZ;

    public void setLocation(Vector3f location) {
        this.locationX = (int) location.x;
        this.locationY = (int) location.y;
        this.locationZ = (int) location.z;
    }

    public Vector3f getLocation() {
        return new Vector3f(locationX, locationY, locationZ);
    }

}


