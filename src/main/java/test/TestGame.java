package test;

import Kuboid.manager.ILogic;
import Kuboid.manager.RenderManager;
import Kuboid.manager.WindowManager;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.opengl.GL11.glViewport;

public class TestGame implements ILogic {

    private int direction = 0;
    private float colour = 0.0f;

    private final RenderManager renderer;
    private final WindowManager window;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
    }

    @Override
    public void init() throws Exception {
        renderer.init();
    }

    @Override
    public void input() {
        if(window.isKeyPressed(GLFW_KEY_UP))
            direction = 1;
        else {
            if (window.isKeyPressed(GLFW_KEY_DOWN))
                direction = -1;
            else
                direction = 0;
        }
    }

    @Override
    public void update() {
        colour += direction * 0.01f;
        if(colour > 1)
            colour = 1.0f;
        else {
            if (colour <= 0)
                colour = 0.0f;
        }
    }

    @Override
    public void render() {
        //Here we would set the checks for the resizable window
        glViewport(0, 0, window.getWidth(), window.getHeight());

        window.setClearColour(colour, colour, colour, 0.0f);
        renderer.clear();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}