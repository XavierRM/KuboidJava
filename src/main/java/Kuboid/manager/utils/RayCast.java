package Kuboid.manager.utils;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.text.NumberFormat;
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
    private Vector3d direction;
    private Vector3i hit;
    private long length;
    private List<Vector3f> worldPositions;

    public RayCast(Vector3f origin, Vector3d direction, long length, List<Vector3f> worldPositions) {
        this.origin = new Vector3f(origin);

        this.direction = new Vector3d(direction);

        //Just in case the value is tiny, it should be considered like 0, otherwise we end up with errors over time
        this.direction.x = (this.direction.x < 0.1 && this.direction.x > 0) ? 0 : this.direction.x;
        this.direction.y = (this.direction.y < 0.1 && this.direction.y > 0) ? 0 : this.direction.y;
        this.direction.z = (this.direction.z < 0.1 && this.direction.z > 0) ? 0 : this.direction.z;

        this.direction.x = (this.direction.x > -0.1 && this.direction.x < 0) ? 0 : this.direction.x;
        this.direction.y = (this.direction.y > -0.1 && this.direction.y < 0) ? 0 : this.direction.y;
        this.direction.z = (this.direction.z > -0.1 && this.direction.z < 0) ? 0 : this.direction.z;

        //Just in case the value is big, it should be considered like 1 or -1, otherwise we end up with errors over time
        this.direction.x = (this.direction.x < 0.9 && this.direction.x > 1) ? 1 : this.direction.x;
        this.direction.y = (this.direction.y < 0.9 && this.direction.y > 1) ? 1 : this.direction.y;
        this.direction.z = (this.direction.z < 0.9 && this.direction.z > 1) ? 1 : this.direction.z;

        this.direction.x = (this.direction.x < -0.9 && this.direction.x > -1) ? -1 : this.direction.x;
        this.direction.y = (this.direction.y < -0.9 && this.direction.y > -1) ? -1 : this.direction.y;
        this.direction.z = (this.direction.z < -0.9 && this.direction.z > -1) ? -1 : this.direction.z;


        if (length == 0)
            //If the length == 0 then we set the length of the ray to be 'infinite'
            this.length = (long) Constants.Z_FAR;
        else
            this.length = length;
        this.worldPositions = new ArrayList<>(worldPositions);
    }

    private int calculateStep(double axisDirection) {

        if (Math.abs(axisDirection) == 0)
            return 0;
        else
            return (axisDirection < 0) ? -1 : 1;
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

            return Math.abs(aux.x + aux.y + aux.z);
        }
    }

    public Vector3i cast() {
        Vector3i originVoxel;

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
        //Was previously absolute value of the result
        double tDeltaX = (this.direction.x != 0) ? Math.abs(1 / this.direction.x) : 1;
        double tDeltaY = (this.direction.y != 0) ? Math.abs(1 / this.direction.y) : 1;
        double tDeltaZ = (this.direction.z != 0) ? Math.abs(1 / this.direction.z) : 1;

        //Initialize to the origin of the ray
        double x = originVoxel.x, y = originVoxel.y, z = originVoxel.z;
        double lengthIncrementVector = 0;

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

            lengthIncrementVector = (float) Math.sqrt(Math.pow(tMaxX, 2) + Math.pow(tMaxY, 2) + Math.pow(tMaxZ, 2));

            System.out.println(lengthIncrementVector);

        } while (!worldPositions.contains(new Vector3d(x, y, z)) && lengthIncrementVector <= length);

        Vector3i result = (lengthIncrementVector <= length) ? new Vector3i((int) x, (int) y, (int) z) : null;

        if (result != null)
            this.hit = new Vector3i(result);

        return result;
    }

    public Vector3i castDDA() {

        //Calculate the differences
        double dx = this.direction.x;
        double dy = this.direction.y;
        double dz = this.direction.z;

        //Calculate the steps
        double steps;

        if (Math.abs(dx) > Math.abs(dy))
            steps = (Math.abs(dx) > Math.abs(dz)) ? Math.abs(dx) : Math.abs(dz);
        else
            steps = (Math.abs(dy) > Math.abs(dz)) ? Math.abs(dy) : Math.abs(dz);

        //Calculate the increment for each axis
        double Xinc = dx / steps;
        double Yinc = dy / steps;
        double Zinc = dz / steps;

        Vector3i voxelCandidate = null;
        Vector3d pointCandidate = new Vector3d(origin);

        //This should be the voxel size
        Vector3d increment = new Vector3d(Xinc, Yinc, Zinc);
        long i = 0;

        do {
            //Next point in the line to check
            pointCandidate = new Vector3d(pointCandidate.add(increment));
            voxelCandidate = new Vector3i(((int) Math.floor(pointCandidate.x)), ((int) Math.floor(pointCandidate.y)), ((int) Math.floor(pointCandidate.z)));

            i++;

            //While condition, checks for a hit or if the length of the ray has already reached its max,
            //if so we finish the execution and return the voxel that hit
        } while (!worldPositions.contains(new Vector3f(voxelCandidate)) && i < length);

        if (i >= length)
            return null;

        this.hit = new Vector3i(voxelCandidate);

        //In case we don't find any position that matches the previous requirements then we return null
        return voxelCandidate;
    }

    public Vector3i getPreviousToHit() {
        float dirValue = 0;
        Vector3i voxelOrigin = new Vector3i(((int) Math.floor(this.origin.x)), ((int) Math.floor(this.origin.y)), ((int) Math.floor(this.origin.z)));
        Vector3f direction = new Vector3f(hit).sub(new Vector3f(voxelOrigin));
        Vector3i vec;

        //Get the highest value for axis in direction
        if (Math.abs(direction.x) >= Math.abs(direction.y)) {
            dirValue = (Math.abs(direction.x) > Math.abs(direction.z)) ? direction.x : direction.z;
            vec = (Math.abs(direction.x) > Math.abs(direction.z)) ? new Vector3i(1, 0, 0) : new Vector3i(0, 0, 1);
        } else {
            dirValue = (Math.abs(direction.y) > Math.abs(direction.z)) ? direction.y : direction.z;
            vec = (Math.abs(direction.y) > Math.abs(direction.z)) ? new Vector3i(0, 1, 0) : new Vector3i(0, 0, 1);
        }

        if (dirValue > 0)
            vec = new Vector3i(vec).mul(-1);

        Vector3i result = new Vector3i(this.hit).add(vec);

        System.out.println("Vec: " + vec.toString(NumberFormat.getNumberInstance()));

        System.out.println("Hit: " + this.hit.toString(NumberFormat.getNumberInstance()));
        System.out.println("PreviousToHit: " + result.toString(NumberFormat.getNumberInstance()));

        return result;
    }
}
