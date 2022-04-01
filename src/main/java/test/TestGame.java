package test;

import Kuboid.manager.*;
import Kuboid.manager.entity.Entity;
import Kuboid.manager.entity.Model;
import Kuboid.manager.entity.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static Kuboid.manager.utils.Constants.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class TestGame implements ILogic {

    private int direction = 0;
    private float colour = 0.0f;
    private int previousKey;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;
    private Camera camera;

    private Vector3f cameraInc;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,// front
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,//
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,//
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f, //
                0.5f, -0.5f, -0.5f, //
                -0.5f, -0.5f, 0.5f, //
                0.5f, -0.5f, 0.5f, //
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,// front
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,// back
                0.0f, 0.0f,//originally was 0.5 | 9
                1.0f, 0.0f,// 10 originally 1.0f, 1.0f
                0.0f, 1.0f,
                1.0f, 1.0f,//
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 0.0f, // ok
                1.0f, 0.0f, //
                0.0f, 1.0f, //
                1.0f, 1.0f //
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2, //front
                8, 10, 11, 9, 8, 11, //top
                12, 13, 7, 5, 12, 7, //right
                14, 15, 6, 4, 14, 6, //left
                16, 18, 19, 17, 16, 19, //bottom
                4, 6, 7, 5, 4, 7, //back
        };

        Model model = loader.loadModel(vertices, textCoords, indices);
        model.setTexture(new Texture(loader.loadTexture("textures/dirt.png")));

        entity = new Entity(model, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), 1);
    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);

        //Go forward
        if(window.isKeyPressed(GLFW_KEY_W)) {
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
        if(window.isKeyPressed(GLFW_KEY_SPACE)) {
            previousKey = GLFW_KEY_SPACE;
            cameraInc.y = -1;
        }

        //Go down
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            previousKey = GLFW_KEY_LEFT_SHIFT;
            cameraInc.y = 1;
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
        }

        //entity.incRotation(0.0f, 0.5f, 0.0f);
    }

    @Override
    public void render() {
        //Here we would set the checks for the resizable window
        glViewport(0, 0, window.getWidth(), window.getHeight());

        window.setClearColour(1.0f, 1.0f, 1.0f, 0.0f);
        renderer.render(entity, camera);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
