package test;

import Kuboid.manager.*;
import Kuboid.manager.generation.Terrain;
import Kuboid.manager.lighting.DirectionalLight;
import Kuboid.manager.utils.RayCast;
import Kuboid.manager.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.text.NumberFormat;
import java.util.List;

import static Kuboid.manager.RenderOptions.*;
import static Kuboid.manager.utils.Constants.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class TestGame implements ILogic {

    private int previousKey;

    private final RenderManager renderer;
    private UI ui;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Terrain terrain;
    private DirectionalLight sunlight;
    private Camera camera;
    private RenderOptions renderOptions = NORMAL;

    Thread thread;
    private Vector3f cameraInc;

    public TestGame() throws Exception {
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        renderer = new RenderManager(camera, renderOptions);
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        renderer.init();
        ui = new UI(window);
        window.switchWireframe(((renderOptions == WIREFRAME) ? true : false));

        //Here we would set the checks for the resizable window
        glViewport(0, 0, window.getWidth(), window.getHeight());

        window.setClearColour(0.529f, 0.807f, 0.921f, 0.0f);

        terrain = new Terrain(4, 48, true, ((renderOptions == WIREFRAME) ? true : false), camera.getPosition());

        sunlight = new DirectionalLight(new Vector3f(100f, 50f, 0f));

        if (thread != null && thread.isAlive()) {
            thread.interrupt();

            terrain.setWireframe(((renderOptions == WIREFRAME) ? true : false));
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

        //Change to wireframe view
        if (window.isKeyPressed(GLFW_KEY_P)) {
            previousKey = GLFW_KEY_P;
            renderOptions = (renderOptions != NO_SHADOWS) ? NO_SHADOWS : NORMAL;
            renderer.setWireframe(((renderOptions == WIREFRAME) ? true : false));
            renderer.switchRenderer();
            //Might stop for a couple milliseconds
            init();
        }

        if (window.isKeyPressed(GLFW_KEY_V)) {
            System.out.println("Camera position: " + camera.getPosition().toString(NumberFormat.getNumberInstance()));
            System.out.println("Camera rotation: " + camera.getRotation().toString(NumberFormat.getNumberInstance()));

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

        if (mouseInput.isLefButtonPress()) {
            Vector2f rotVec = mouseInput.getDisplayVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

        }

        if (mouseInput.isRightButtonPress()) {
            Vector3f cameraPos = camera.getPosition();  //Starting position for the RayCast

            //Vector3f direction = Utils.convert2DPositionTo3D(new Vector2f(window.getWidth() / 2, window.getHeight() / 2), camera, window);
            Vector3d direction = Utils.calculateDirection(camera);  //Direction for the RayCast

            //Get blocks to compare the ray hit with
            List<Vector3f> blocksPositions = terrain.getActiveBlockPositions();

            RayCast rayCast = new RayCast(cameraPos, direction, 0, blocksPositions);
            Vector3i hit = rayCast.castDDA();

            if (window.isKeyPressed(GLFW_KEY_LEFT_ALT)) {

                if (hit != null) {
                    System.out.println(rayCast.getPreviousToHit().toString(NumberFormat.getNumberInstance()));

                    terrain.addVoxel(new Vector3f(rayCast.getPreviousToHit()));
                } else
                    System.out.println("Hit is null! - Add");

            } else {
                if (hit != null) {
                    System.out.println(hit.toString(NumberFormat.getNumberInstance()));

                    terrain.removeVoxel(new Vector3f(hit));
                } else
                    System.out.println("Hit is null! - Remove");
            }

        }

        terrain.update(camera.getPosition());
    }

    @Override
    public void render() throws Exception {
        renderer.clear();
        renderer.render(terrain.getTerrain(), sunlight);
        ui.render();
    }

    @Override
    public void cleanup() {
        terrain.stopLoop();
        thread.interrupt();
        renderer.cleanup();
        loader.cleanup();
    }
}
