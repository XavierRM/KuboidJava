package Kuboid.manager;

import Kuboid.manager.UI.UILayer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import test.Launcher;

import static Kuboid.manager.utils.Constants.TITLE;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class EngineManager {

    public static final float MAX_FPS = 10000;
    public static final double TICKS = 30;
    public static final double NANOSECOND = 1000000000L;

    private static int fps;

    private boolean isRunning = false;

    private static WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        window = Launcher.getWindow();
        window.setUiLayer(new UILayer());
        gameLogic = Launcher.getGame();
        mouseInput = new MouseInput();
        window.init();
        mouseInput.init();
        gameLogic.init();
    }

    public void start() throws Exception {
        init();
        if(isRunning)
            return;

        run();
    }

    public void run() throws Exception {
        this.isRunning = true;
        long initialTime = System.nanoTime();
        final double timeU = NANOSECOND / TICKS;
        final double timeF = NANOSECOND / MAX_FPS;
        double deltaU = 0, deltaF = 0;
        int frames = 0;//, ticks = 0;
        long timer = System.currentTimeMillis();

        while (isRunning) {

            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;

            if (deltaU >= 1) {
                input();
                update((float) deltaU);
                //ticks++;
                deltaU--;
            }

            if (deltaF >= 1) {
                render();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                setFps(frames);
                window.setTitle(TITLE + " FPS: " + getFps());
                frames = 0;
                //ticks = 0;
                timer += 1000;
            }
        }

        cleanup();
    }

    private void stop() {
        if(!isRunning)
            return;
        isRunning = false;
    }

    private void input() throws Exception {
        if (window.windowShouldClose())
            stop();

        mouseInput.input();
        gameLogic.input();
    }

    private void render() throws Exception {
        gameLogic.render();
        window.update();
    }

    private void update(float interval) throws Exception {
        gameLogic.update(interval, mouseInput);
    }

    private void cleanup() {
        window.cleanup();
        gameLogic.cleanup();
        errorCallback.free();
        glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
