package Kuboid.manager.UI;

import imgui.ImGui;

public class UILayer {

    private boolean showText = false;

    public void ImGui() {
        ImGui.begin("New window");

        if (ImGui.button("I am a button")) {
            showText = true;
        }

        if (showText) {
            ImGui.text("You clicked a button");
            ImGui.sameLine();
            if (ImGui.button("Stop showing text")) {
                showText = false;
            }
        }

        ImGui.end();
    }
}
