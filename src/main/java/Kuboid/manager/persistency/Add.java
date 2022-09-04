package Kuboid.manager.persistency;

import Kuboid.manager.voxel.VoxelType;
import org.joml.Vector3f;

public class Add extends Change {

    private VoxelType type;

    public Add(Vector3f location, VoxelType type) {
        setLocation(location);
        this.type = type;
    }

    public VoxelType getType() {
        return type;
    }

    public void setType(VoxelType type) {
        this.type = type;
    }
}
