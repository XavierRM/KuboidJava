package Kuboid.manager.utils;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

// *
// * This algorithm implementation for RayCasting in a Voxel World is based of the paper written by
// * John Amanatides and Andres Woo, "A Fast Voxel Traversal Algorithm for Ray Tracing", that can be
// * found here:
// * @see <a href="https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.42.3443&rep=rep1&type=pdf">A Fast Voxel Traversal Algorithm for Ray Tracing</a>
// *
// *


public class RayCast {

    private Vector3f origin;
    private Vector3f secondPoint;
    private Vector3f direction;
    private long length;
    private List<Vector3f> worldPositions;

    public RayCast(Vector3f origin, Vector3f direction, long length, List<Vector3f> worldPositions) {
        this.origin = new Vector3f(origin);

        Vector3f scaledDirection = new Vector3f(direction).mul(length);
        this.secondPoint = new Vector3f(origin).add(scaledDirection);
        this.direction = new Vector3f(direction);

        if (length == 0)
            //If the length == 0 then we set the length of the ray to be 'infinite'
            this.length = (long) Constants.Z_FAR;
        else
            this.length = length;
        this.worldPositions = new ArrayList<>(worldPositions);
    }

    private int calculateStep(float axisDirection) {

        if (axisDirection > 0)
            return 1;
        else
            return (axisDirection < 0) ? -1 : 0;
    }

    private float calculateTMax(int nextVoxelAxis, int step, Vector3f axis) {

        /*
         * First we check the values of the 'step', in case it's 0, is never going to hit
         * another voxel in that direction, so we set it to the maximum possible value (1),
         * later on when we calculate the 'min' of all the tMax's the one with a value of 1
         * will not be selected.
         *
         * Then, the axis vector will give us which axis we are calculating, for example, if axis = (1, 0, 0)
         * we are trying to calculate the tMax in the 'x' axis. After the multiplication and subtraction
         * we would end up with something like (0.35, 0, 0) to extract the value on the 'x' axis which the relevant
         * in this case we just have to return the result of adding all the components.
         * */

        if (step == 0)
            return 1;
        else {
            Vector3f aux;
            aux = (step > 0) ? (new Vector3f(axis).mul(nextVoxelAxis)).sub(new Vector3f(axis).mul(this.origin)) :
                    ((new Vector3f(axis).mul(nextVoxelAxis)).add(axis)).sub(new Vector3f(axis).mul(this.origin));

            //TODO after fixing the contains in the while loop try without 'normalizing' the values
            Vector3f directionValue = new Vector3f(axis).mul(direction);

            return Math.abs(aux.x + aux.y + aux.z) / Math.abs(directionValue.x + directionValue.y + directionValue.z);
        }
    }

    public Vector3i cast() {
        Vector3i originVoxel;
        Vector3f pointCandidate = new Vector3f(origin);

        int stepX = calculateStep(this.direction.x);
        int stepY = calculateStep(this.direction.y);
        int stepZ = calculateStep(this.direction.z);

        //Voxel that corresponds to the origin of the ray
        originVoxel = new Vector3i((int) Math.floor(this.origin.x), (int) Math.floor(this.origin.y), (int) Math.floor(this.origin.z));

        //The next voxel the ray is going to hit
        Vector3i nextVoxel = new Vector3i(originVoxel).add(stepX, stepY, stepZ);

        //Calculating tMax value for each axis
        float tMaxX = calculateTMax(nextVoxel.x, stepX, new Vector3f(1f, 0f, 0f));
        float tMaxY = calculateTMax(nextVoxel.y, stepY, new Vector3f(0f, 1f, 0f));
        float tMaxZ = calculateTMax(nextVoxel.z, stepZ, new Vector3f(0f, 0f, 1f));

        //Calculating tDelta for each axis, length of the voxel in an axis represented in units of 't'
        //If the size of the voxel != 1 then we would replace '1' with the 'voxelSize'
        float tDeltaX = Math.abs(1 / this.direction.x);
        float tDeltaY = Math.abs(1 / this.direction.y);
        float tDeltaZ = Math.abs(1 / this.direction.z);

        //Ray = u + t*v, being 'u' the original point and 'v' the direction, we increment 't' in intervals to get
        //different points of the ray, that is the basic principle

        //Initialize to the origin of the ray
        long x = originVoxel.x, y = originVoxel.y, z = originVoxel.z;
        float lengthIncrementVector = 0;

        do {

            if (tMaxX < tMaxY && tMaxX < tMaxZ) {
                // X-axis traversal.
                x += stepX;
                tMaxX += tDeltaX;
            } else if (tMaxY < tMaxZ) {
                // Y-axis traversal.
                y += stepY;
                tMaxY += tDeltaY;
            } else {
                // Z-axis traversal.
                z += stepZ;
                tMaxZ += tDeltaZ;
            }

            //System.out.println("Direction: " + this.direction.toString(NumberFormat.getNumberInstance()));
            //System.out.println("tMax: " + new Vector3f(tMaxX, tMaxY, tMaxZ).toString(NumberFormat.getNumberInstance()));
            //System.out.println("VoxelCandidate: " + new Vector3f(x, y, z).toString(NumberFormat.getNumberInstance()));
            //System.out.println();

            lengthIncrementVector = (float) Math.sqrt(Math.pow(tMaxX, 2) + Math.pow(tMaxY, 2) + Math.pow(tMaxZ, 2));

            //System.out.println("Contains: " + !worldPositions.contains(new Vector3f(x, y, z)));

        } while (!worldPositions.contains(new Vector3f(x, y, z)) && lengthIncrementVector <= length);

        Vector3i result = (lengthIncrementVector <= length) ? new Vector3i((int) x, (int) y, (int) z) : null;

        return result;
    }

    public Vector3i castDDA() {

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

        //This should be the voxel size
        Vector3f increment = new Vector3f(Xinc, Yinc, Zinc);
        long i = 0;

        do {
            //Next point in the line to check
            pointCandidate = new Vector3f(pointCandidate.add(increment));
            voxelCandidate = new Vector3i(((int) Math.floor(pointCandidate.x)), ((int) Math.floor(pointCandidate.y)), ((int) Math.floor(pointCandidate.z)));

            i++;

            //While condition checks for a hit or if the length of the ray has already reached its max,
            //if so we finish the execution and return the voxel that hit
        } while (!worldPositions.contains(new Vector3f(voxelCandidate)) && i < length);

        if (i >= length)
            return null;

        //In case we don't find any position that matches the previous requirements then we return null
        return voxelCandidate;
    }
}
