package Kuboid.manager;

import Kuboid.manager.UI.UILayer;
import Kuboid.manager.utils.Constants;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Matrix4f;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static Kuboid.manager.utils.Constants.Z_FAR;
import static Kuboid.manager.utils.Constants.Z_NEAR;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowManager {

    private String title;

    private int width, height;
    private float FOV;
    private long window;

    private boolean resize, vSync;

    private final Matrix4f projectionMatrix;

    private UILayer uiLayer;

    private final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();

    private String glslVersion = null;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        this.FOV = Constants.FOV;
        projectionMatrix = new Matrix4f();
        projectionMatrix.identity();
    }

    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        glslVersion = "#version 400";

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create the window
        window = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (window == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        //Setting the window in the center of the screen
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        if(isvSync())
            glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        initImGui();
        imGuiImplGlfw.init(window, true);
        imGuiImplGl3.init(glslVersion);

    }

    private void initImGui() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
    }

    public void update() {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();

        if (uiLayer != null)
            uiLayer.ImGui();

        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            org.lwjgl.glfw.GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void cleanup() {
        imGuiImplGl3.dispose();
        imGuiImplGlfw.dispose();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    public void setUiLayer(UILayer layer) {
        this.uiLayer = layer;
    }

    public UILayer getUiLayer() {
        return uiLayer;
    }

    public boolean isKeyPressed(int keycode) {
        return glfwGetKey(window, keycode) == GLFW_PRESS;
    }

    public void switchWireframe(boolean isWireframe) {
        if (isWireframe) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
    }

    public void setClearColour(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(window, title);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindowId() {
        return window;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix() {
        float aspectRatio = (float) width / (float) height;
        return projectionMatrix.setPerspective(this.FOV, aspectRatio, Z_NEAR, Z_FAR);

    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspectRatio = (float) width / (float) height;
        return matrix.setPerspective(this.FOV, aspectRatio, Z_NEAR, Z_FAR);

    }

    public void setFOV(int FOV_degrees) {
        this.FOV = (float) Math.toRadians(FOV_degrees);
    }

    public float getFOV() {
        return this.FOV;
    }

    public boolean isvSync() {
        return vSync;
    }
}
