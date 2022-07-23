package Kuboid.manager.utils;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class RayCast {

    private Vector3f origin;
    private Vector3f secondPoint;
    private long length;
    private List<Vector3f> worldPositions;

    public RayCast(Vector3f origin, Vector3f direction, long length, List<Vector3f> worldPositions) {
        this.origin = new Vector3f(origin);

        Vector3f scaledDirection = new Vector3f(direction).mul(length);
        this.secondPoint = new Vector3f(origin).add(scaledDirection);

        System.out.println("Direction: " + direction.toString(NumberFormat.getNumberInstance()));
        System.out.println("Origin: " + this.origin.toString(NumberFormat.getNumberInstance()));
        System.out.println("SecondPoint: " + this.secondPoint.toString(NumberFormat.getNumberInstance()));

        if (length == 0)
            //If the length == 0 then we set the length of the ray to be 'infinite'
            this.length = (long) Constants.Z_FAR;
        else
            this.length = length;
        this.worldPositions = new ArrayList<>(worldPositions);
    }

    public Vector3i cast() {

        //Calculate the differences
        float dx = secondPoint.x - origin.x;
        float dy = secondPoint.y - origin.y;
        float dz = secondPoint.z - origin.z;

        //Calculate the steps
        float steps;

        if (Math.abs(dx) > Math.abs(dy))
            steps = (Math.abs(dx) > Math.abs(dz)) ? Math.abs(dx) : Math.abs(dz);
        else
            steps = (Math.abs(dy) > Math.abs(dz)) ? Math.abs(dy) : Math.abs(dz);

        //Calculate the increment for each axis
        float Xinc = dx / steps;
        float Yinc = dy / steps;
        float Zinc = dz / steps;

        Vector3i voxelCandidate;
        Vector3f pointCandidate = new Vector3f(origin);

        //Ray = u + t*v, being 'u' the original point and 'v' the direction, we increment 't' in intervals of 0,1f until we find a
        //voxel that is in our grid of active voxels.

        //This should be the voxel size
        Vector3f increment = new Vector3f(Xinc, Yinc, Zinc);
        long i = 0;

        glBegin(GL_POINTS);

        do {
            //Next point in the line to check
            pointCandidate = new Vector3f(pointCandidate.add(increment));
            voxelCandidate = new Vector3i(((int) Math.floor(pointCandidate.x)), ((int) Math.floor(pointCandidate.y)), ((int) Math.floor(pointCandidate.z)));

            i++;

            glVertex3f(pointCandidate.x, pointCandidate.y, pointCandidate.z);
            //System.out.println(pointCandidate.toString(NumberFormat.getNumberInstance()));

            //While condition checks for a hit or if the length of the ray has already reached its max,
            //if so we finish the execution and return the voxel that hit
        } while (!worldPositions.contains(new Vector3f(voxelCandidate)) && i < length);

        glEnd();

        if (i >= length)
            return null;

        //In case we don't find any position that matches the previous requirements then we return null
        return voxelCandidate;
    }
}
