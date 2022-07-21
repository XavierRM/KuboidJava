package test;

import Kuboid.manager.*;
import Kuboid.manager.entity.Entity;
import Kuboid.manager.generation.Terrain;
import Kuboid.manager.model.Model;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import static Kuboid.manager.utils.Constants.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class TestGame implements ILogic {

    private int previousKey;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Terrain terrain;
    private Camera camera;
    private boolean isWireframe = false;

    Thread thread;
    private Vector3f cameraInc;

    public TestGame() {
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        renderer = new RenderManager(camera, isWireframe);
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        renderer.init();
        window.switchWireframe(isWireframe);

        //Here we would set the checks for the resizable window
        glViewport(0, 0, window.getWidth(), window.getHeight());

        window.setClearColour(0.529f, 0.807f, 0.921f, 0.0f);

        terrain = new Terrain(2, 48, true, isWireframe, camera.getPosition());

        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            terrain.setWireframe(isWireframe);
        }

        thread = new Thread(terrain);
        thread.start();
    }

    @Override
    public void input() throws Exception {
        cameraInc.set(0, 0, 0);

        //Go forward
        if (window.isKeyPressed(GLFW_KEY_W)) {
            if (previousKey == GLFW_KEY_W && window.isKeyPressed(GLFW_KEY_LEFT_ALT))
                cameraInc.z = -2;
            else
                cameraInc.z = -1;

            previousKey = GLFW_KEY_W;
        }

        //Go backwards
        if(window.isKeyPressed(GLFW_KEY_S)) {
            previousKey = GLFW_KEY_S;
            cameraInc.z = 1;
        }

        //Go left
        if(window.isKeyPressed(GLFW_KEY_A)) {
            previousKey = GLFW_KEY_A;
            cameraInc.x = -1;
        }

        //Go right
        if(window.isKeyPressed(GLFW_KEY_D)) {
            previousKey = GLFW_KEY_D;
            cameraInc.x = 1;
        }

        //Go up
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            previousKey = GLFW_KEY_SPACE;
            cameraInc.y = -1;
        }

        //Go down
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            previousKey = GLFW_KEY_LEFT_SHIFT;
            cameraInc.y = 1;
        }

        if (window.isKeyPressed(GLFW_KEY_P)) {
            previousKey = GLFW_KEY_P;
            isWireframe = !isWireframe;
            renderer.setWireframe(isWireframe);
            renderer.switchRenderer();
            //Might stop for a couple milliseconds
            init();
        }

    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        window.updateProjectionMatrix();

        if (cameraInc.z == -2) {
            cameraInc.z = -1;
            camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED_FAST, cameraInc.y * CAMERA_MOVE_SPEED_FAST, cameraInc.z * CAMERA_MOVE_SPEED_FAST);
        } else {
            camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y * CAMERA_MOVE_SPEED, cameraInc.z * CAMERA_MOVE_SPEED);
        }

        if(mouseInput.isLefButtonPress()) {
            Vector2f rotVec = mouseInput.getDisplayVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

            System.out.println(camera.getCenterPos(window).toString(NumberFormat.getNumberInstance()));
            Vector3f cameraPos = camera.getPosition();
            Vector3f cameraCenterPos = camera.getCenterPos(window);

            Map<Model, List<Entity>> terrainData = terrain.getTerrain();

            for (Model model : terrainData.keySet()) {
                List<Entity> positions = terrainData.get(model);

                for (Entity entity : positions) {
                    /*Check for the possible positions of voxels along the direction of the
                    ray starting at the camera position 'n' times, being 'n' the length of the ray
                    or the number of possible positions we'll check if we find a position that matches
                    the terrain data then we would eliminate it from the data and finish the cycling*/
                }
            }

        }

        terrain.update(camera.getPosition());
    }

    @Override
    public void render() {
        renderer.clear();
        renderer.render(terrain.getTerrain());
    }

    @Override
    public void cleanup() {
        terrain.stopLoop();
        thread.interrupt();
        renderer.cleanup();
        loader.cleanup();
    }
}
