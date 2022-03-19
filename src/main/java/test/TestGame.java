package test;

import Kuboid.manager.*;
import Kuboid.manager.entity.Entity;
import Kuboid.manager.entity.Model;
import Kuboid.manager.entity.Texture;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class TestGame implements ILogic {

    private static final float CAMERA_MOVE_SPEED = 0.01f;

    private int direction = 0;
    private float colour = 0.0f;

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
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.5f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 1.0f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
                8, 10, 11, 9, 8, 11,
                12, 13, 7, 5, 12, 7,
                14, 15, 6, 4, 14, 6,
                16, 18, 19, 17, 16, 19,
                4, 6, 7, 5, 4, 7,
        };

        Model model = loader.loadModel(vertices, textCoords, indices);
        model.setTexture(new Texture(loader.loadTexture("textures/dirt_grass.png")));

        entity = new Entity(model, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), 1);
    }

    @Override
    public void input() {
        /*if(window.isKeyPressed(GLFW_KEY_UP))
            direction = 1;
        else {
            if (window.isKeyPressed(GLFW_KEY_DOWN))
                direction = -1;
            else
                direction = 0;
        }*/
        cameraInc.set(0, 0, 0);

        //Go forward
        if(window.isKeyPressed(GLFW_KEY_W))
            cameraInc.z = -1;

        //Go backwards
        if(window.isKeyPressed(GLFW_KEY_S))
            cameraInc.z = 1;

        //Go left
        if(window.isKeyPressed(GLFW_KEY_A))
            cameraInc.x = 1;

        //Go right
        if(window.isKeyPressed(GLFW_KEY_D))
            cameraInc.x = -1;

        //Go up
        if(window.isKeyPressed(GLFW_KEY_SPACE))
            cameraInc.y = -1;

        //Go down
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
            cameraInc.y = 1;

    }

    @Override
    public void update() {
        /*colour += direction * 0.01f;
        if(colour > 1)
            colour = 1.0f;
        else {
            if (colour <= 0)
                colour = 0.0f;
        }

        if(entity.getPos().x < -1.5f)
            entity.getPos().x = 1.5f;

        entity.getPos().x -= 0.01f;*/

        window.updateProjectionMatrix();
        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y * CAMERA_MOVE_SPEED, cameraInc.z * CAMERA_MOVE_SPEED);
        entity.incRotation(0.0f, 0.5f, 0.0f);
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
