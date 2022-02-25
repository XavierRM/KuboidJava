package Kuboid.manager;

import test.Launcher;

import static org.lwjgl.opengl.GL11.*;

public class RenderManager {

    private final WindowManager window;

    public RenderManager() {
        window = Launcher.getWindow();
    }

    public void init() throws Exception {

    }

    public void render() {

    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {

    }
}
