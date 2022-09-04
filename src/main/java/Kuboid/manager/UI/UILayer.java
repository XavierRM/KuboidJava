package Kuboid.manager.UI;

import imgui.ImGui;
import org.joml.Vector3f;

public class UILayer {

    private boolean save = false;
    private float[] fovArray;
    private float[] redArray;
    private float[] greenArray;
    private float[] blueArray;

    public UILayer(float FOV_radians, Vector3f colour) {

        this.fovArray = new float[]{(int) Math.toDegrees(FOV_radians)};
        this.redArray = new float[]{colour.x};
        this.greenArray = new float[]{colour.y};
        this.blueArray = new float[]{colour.z};
    }

    public void ImGui() {
        ImGui.begin("Debugging");
        ImGui.setWindowSize(200, 200);

        ImGui.sliderFloat(" FOV", fovArray, 30, 120);

        ImGui.newLine();

        ImGui.sliderFloat(" R", redArray, 0f, 1f);
        ImGui.sliderFloat(" G", greenArray, 0f, 1f);
        ImGui.sliderFloat(" B", blueArray, 0f, 1f);

        ImGui.newLine();

        if (ImGui.button("Save world & changes")) {
            save = true;
        }

        ImGui.end();
    }

    public Vector3f getBackgroundColour() {
        return new Vector3f(redArray[0], greenArray[0], blueArray[0]);
    }

    public int getFOV_degrees() {
        return (int) fovArray[0];
    }
}
