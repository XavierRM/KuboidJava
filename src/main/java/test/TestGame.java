package test;

import Kuboid.manager.*;
import Kuboid.manager.UI.Crosshair;
import Kuboid.manager.UI.UILayer;
import Kuboid.manager.generation.Terrain;
import Kuboid.manager.lighting.DirectionalLight;
import Kuboid.manager.lighting.Sun;
import Kuboid.manager.persistency.Add;
import Kuboid.manager.persistency.Change;
import Kuboid.manager.persistency.Remove;
import Kuboid.manager.persistency.World;
import Kuboid.manager.utils.RayCast;
import Kuboid.manager.utils.Utils;
import Kuboid.manager.voxel.VoxelType;
import com.google.gson.Gson;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static Kuboid.manager.RenderOptions.*;
import static Kuboid.manager.utils.Constants.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class TestGame implements ILogic {

    private int previousKey;
    private String saveFilename = "World.json";
    private String content = "";

    private final RenderManager renderer;
    private Crosshair crosshair;
    private final ObjectLoader loader;
    private final WindowManager window;

    private World worldObject;
    private List<Change> changes = new ArrayList<>();
    private Terrain terrain;
    private DirectionalLight dirlight = new DirectionalLight(new Vector3f(100f, 50f, 0f));
    private Sun sunlight;
    private Camera camera;
    private RenderOptions renderOptions = DEFAULT;

    Thread thread;
    private Vector3f cameraInc;

    public TestGame() throws Exception {
        window = Launcher.getWindow();
        window.setUiLayer(new UILayer(window.getFOV(), new Vector3f(0.529f, 0.807f, 0.921f)));
        loader = new ObjectLoader();
        camera = new Camera();
        renderer = new RenderManager(camera, renderOptions);
        worldObject = new World();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        renderer.setRenderOptions(renderOptions);
        renderer.init();
        crosshair = new Crosshair(window);
        window.switchWireframe(((renderOptions == WIREFRAME) ? true : false));

        //Here we would set the checks for the resizable window
        glViewport(0, 0, window.getWidth(), window.getHeight());

        window.setClearColour(0.529f, 0.807f, 0.921f, 0.0f);

//        terrain = new Terrain(8, 48, true, ((renderOptions == WIREFRAME) ? true : false), camera.getPosition());
        terrain = new Terrain(4, 48, true, ((renderOptions == WIREFRAME) ? true : false), camera.getPosition());

        worldObject.seed = terrain.getSeed();

        sunlight = new Sun(dirlight);

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
            renderOptions = WIREFRAME;
            renderer.switchRenderer();
            //Might stop for a couple milliseconds
            init();
        }

        //Change to NoShadows view
        if (window.isKeyPressed(GLFW_KEY_O)) {
            previousKey = GLFW_KEY_O;
            renderOptions = NO_SHADOWS;
            renderer.switchRenderer();
            init();
        }

        //Change to Normals view
        if (window.isKeyPressed(GLFW_KEY_L)) {
            previousKey = GLFW_KEY_L;
            renderOptions = NORMALS;
            renderer.switchRenderer();
            init();
        }

        //Change to default view
        if (window.isKeyPressed(GLFW_KEY_K)) {
            previousKey = GLFW_KEY_K;
            renderOptions = DEFAULT;
            renderer.switchRenderer();
            init();
        }

        if (window.isKeyPressed(GLFW_KEY_V)) {
            System.out.println("Camera position: " + camera.getPosition().toString(NumberFormat.getNumberInstance()));
            System.out.println("Camera rotation: " + camera.getRotation().toString(NumberFormat.getNumberInstance()));
            sunlight.printValues();

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
                    Vector3f previousHit = new Vector3f(rayCast.getPreviousToHit());

                    worldObject.changes.add(new Add(new Vector3f(previousHit), VoxelType.STONE));
                    terrain.addVoxel(new Vector3f(previousHit));
                } else
                    System.out.println("Hit is null! - Add");

            } else {
                if (hit != null) {
                    System.out.println(hit.toString(NumberFormat.getNumberInstance()));

                    worldObject.changes.add(new Remove(new Vector3f(hit)));
                    terrain.removeVoxel(new Vector3f(hit));
                } else
                    System.out.println("Hit is null! - Remove");
            }

        }

        //Update the GUI
        window.setFOV(window.getUiLayer().getFOV_degrees());

        Vector3f backgroundColour = window.getUiLayer().getBackgroundColour();
        window.setClearColour(backgroundColour.x, backgroundColour.y, backgroundColour.z, 0.0f);

        terrain.update(camera.getPosition());
        //Call to update the sun
//        sunlight.update();
    }

    @Override
    public void render() throws Exception {
        renderer.clear();
        renderer.normalsList = terrain.normalsList;
        renderer.render(terrain.getTerrain(), sunlight.getDirectionalLight());
        crosshair.render();
    }

    @Override
    public void cleanup() {

        if (window.getUiLayer().save()) {
            //Save the changes in a JSON file
            try {
                File file = new File("C:\\Users\\Usuario\\IdeaProjects\\Kuboid\\" + saveFilename);

                file.createNewFile();

                //Get the changes and build the JSON, then convert to string
                worldObject.changes = changes;
                content = (new Gson()).toJson(worldObject);

                byte[] b = content.getBytes();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b);
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        terrain.stopLoop();
        thread.interrupt();
        renderer.cleanup();
        loader.cleanup();
    }
}
