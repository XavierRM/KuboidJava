package Kuboid.manager.utils;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;

public class RayCast {

    private Vector3f origin;
    private Vector3f direction;
    private long length;
    private List<Vector3f> worldPositions;

    public RayCast(Vector3f origin, Vector3f direction, long length, List<Vector3f> worldPositions) {
        this.origin = origin;
        this.direction = direction;
        if (length == 0)
            //If the length == 0 then we set the length of the ray to be 'infinite'
            this.length = (long) Constants.Z_FAR;
        else
            this.length = length;
        this.worldPositions = worldPositions;
    }

    public Vector3i cast() {
        int i = 0;
        System.out.println("Inside the cast" + (i++));
        Vector3i voxelCandidate;
        Vector3f pointCandidate;

        //Ray = u + t*v, being 'u' the original point and 'v' the direction, we increment 't' in intervals of 0,1f until we find a
        //voxel that is in our grid of active voxels.
        for (float t = 0; t < length; t += 0.1f) {
            //Next point in the line to check
            pointCandidate = origin.add(direction.mul(t));
            voxelCandidate = new Vector3i(((int) Math.floor(pointCandidate.x)), ((int) Math.floor(pointCandidate.y)), ((int) Math.floor(pointCandidate.z)));

            if (worldPositions.contains(new Vector3f(voxelCandidate)))
                return voxelCandidate;
        }

        //In case we don't find any position that matches the previous requirements then we return null
        return null;
    }
}
