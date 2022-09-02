package Kuboid.manager.UI;

import Kuboid.manager.WindowManager;

import static org.lwjgl.opengl.GL11.*;

public class Crosshair {

    private int id;
    private WindowManager window;

    public Crosshair(WindowManager window) {
        this.id = 0;
        this.window = window;
    }

    private void drawCrosshairOLD() {
        glPushMatrix();
        glViewport(0, 0, window.getWidth(), window.getHeight());
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, window.getWidth(), window.getHeight(), 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glColor3ub((byte) 240, (byte) 240, (byte) 240);//white
        glLineWidth(2.0f);
        glBegin(GL_LINES);
        //horizontal line
        glVertex2i(window.getWidth() / 2 - 7, window.getHeight() / 2);
        glVertex2i(window.getWidth() / 2 + 7, window.getHeight() / 2);
        glEnd();
        //vertical line
        glBegin(GL_LINES);
        glVertex2i(window.getWidth() / 2, window.getHeight() / 2 + 7);
        glVertex2i(window.getWidth() / 2, window.getHeight() / 2 - 7);
        glEnd();

        glPopMatrix();
    }

    public void render() {
        drawCrosshairOLD();
    }
}
