package Kuboid.manager;

import org.joml.Vector2d;
import org.joml.Vector2f;
import test.Launcher;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d previousPos, currentPos;
    private final Vector2f displayVec;

    private boolean inWindow = false, lefButtonPress = false, rightButtonPress = false;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displayVec = new Vector2f();
    }

    public void init() {


        glfwSetCursorPosCallback(Launcher.getWindow().getWindowId(), ((window, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        }));

        glfwSetCursorEnterCallback(Launcher.getWindow().getWindowId(), ((window, entered) -> {
            inWindow = entered;
        }));

        glfwSetMouseButtonCallback(Launcher.getWindow().getWindowId(), ((window, button, action, mods) -> {
            lefButtonPress = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPress = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        }));
    }

    public void input() {
        displayVec.x = 0;
        displayVec.y = 0;

        if(previousPos.x > 0  && previousPos.y > 0 && inWindow) {
            double x = currentPos.x - previousPos.x;
            double y = currentPos.y - previousPos.y;
            boolean rotateX = x != 0;
            boolean rotateY = y != 0;

            if(rotateX)
                displayVec.y = (float) x;

            if(rotateY)
                displayVec.x = (float) y;
        }

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public Vector2f getDisplayVec() {
        return displayVec;
    }

    public boolean isLefButtonPress() {
        return lefButtonPress;
    }

    public boolean isRightButtonPress() {
        return rightButtonPress;
    }

    public Vector2f getCursorPosition(WindowManager windowManager) {
        double[] xpos = new double[1], ypos = new double[1];

        glfwGetCursorPos(windowManager.getWindowId(), xpos, ypos);

        return new Vector2f((float) xpos[0], (float) ypos[0]);
    }
}
