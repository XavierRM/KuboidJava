package Kuboid.manager;

import Kuboid.manager.entity.Entity;
import Kuboid.manager.lighting.DirectionalLight;
import Kuboid.manager.lighting.ShadowMap;
import Kuboid.manager.model.Model;
import Kuboid.manager.utils.Transformation;
import Kuboid.manager.utils.Utils;
import org.joml.Vector3f;
import test.Launcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Kuboid.manager.RenderOptions.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class RenderManager {

    private final WindowManager window;
    private ShaderManager shader;
    private ShaderManager shaderDepth;
    private final Camera camera;
    private ShadowMap shadowMap;

    public List<Vector3f> normalsList = new ArrayList<>();

    private RenderOptions renderOptions;

    public RenderManager(Camera camera, RenderOptions renderOptions) throws Exception {
        window = Launcher.getWindow();
        this.camera = camera;
        this.renderOptions = renderOptions;
    }

    public void init() throws Exception {

        System.out.println("RenderOptions: " + renderOptions.toString());

        //ShadowMap where the FBO and texture info are stored and initialized
        shader = new ShaderManager();

        if (renderOptions != WIREFRAME || renderOptions != NORMALS) {
            shaderDepth = new ShaderManager();
            this.shadowMap = new ShadowMap();

        }

        if (renderOptions == WIREFRAME) {
            //Wireframe view
            shader.createVertexShader(Utils.loadResource("/shaders/vertexWireframe.vs"));
            shader.createFragmentShader(Utils.loadResource("/shaders/fragmentWireframe.fs"));
        }

        if (renderOptions == DEFAULT) {
            shaderDepth.createVertexShader(Utils.loadResource("/shaders/depth_vertex.vs"));
            shaderDepth.createFragmentShader(Utils.loadResource("/shaders/depth_fragment.fs"));

            //Normal view
            shader.createVertexShader(Utils.loadResource("/shaders/vertexTexture.glsl"));
            shader.createFragmentShader(Utils.loadResource("/shaders/fragmentTexture.glsl"));

            //Shader for calculating the depth texture, runs before the main shader
            shaderDepth.link();
        }

        if (renderOptions == NO_SHADOWS) {
            //View with no shadows
            shader.createVertexShader(Utils.loadResource("/shaders/vertexNoShadowsTexture.vs"));
            shader.createFragmentShader(Utils.loadResource("/shaders/fragmentNoShadowsTexture.fs"));
        }

        if (renderOptions == NORMALS) {
            //View with just normals
            shader.createVertexShader(Utils.loadResource("/shaders/normalsVertex.glsl"));
            shader.createFragmentShader(Utils.loadResource("/shaders/normalsFragment.glsl"));
        }

        //Main shader
        shader.link();

        if (renderOptions == DEFAULT || renderOptions == NO_SHADOWS) {
            shader.createUniform("textureSampler");
        }

        if (renderOptions == DEFAULT) {
            //ShadowMap storing the depth texture
            shader.createUniform("shadowMap");

            //View + Projection matrix to convert a point in the 3D world space to the light perspective and check depth value
            shader.createUniform("viewProjectionLightMatrix");

            //OrthoProjection matrix to calculate the depth texture and the model matrix for the light
            shaderDepth.createUniform("orthoProjectionViewMatrix");
            shaderDepth.createUniform("modelLightMatrix");
        }

        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
//        shader.createUniform("viewLightMatrix");
//        shader.createUniform("cameraPosition");
//        shader.createUniform("specularPower");

        if (renderOptions == DEFAULT) {
            shader.createUniform("directionalLight.colour");
            shader.createUniform("directionalLight.intensity");
            shader.createUniform("directionalLight.position");
        }

    }

    public void render(Map<Model, List<Entity>> entities, DirectionalLight sunlight) throws Exception {
//    public void render(Map<Model, List<Entity>> entities, Sun sunlight) throws Exception {
        //For now, it's just the sunlight, but it should be a list of lights

        if (renderOptions == DEFAULT) {
            //First we render from the lights perspective to get the depth texture
            renderDepthMap(entities, sunlight);
        }

        //Readjust the Viewport size
        glViewport(0, 0, window.getWidth(), window.getHeight());

        //Render models in the game
        for (Model model : entities.keySet()) {
            shader.bind();

            if (renderOptions == DEFAULT || renderOptions == NO_SHADOWS)
                shader.setUniform("textureSampler", 0);

            //Should precompute this
            shader.setUniform("projectionMatrix", window.getProjectionMatrix());
            shader.setUniform("viewMatrix", Transformation.getViewMatrix(this.camera));
//            shader.setUniform("viewLightMatrix", sunlight.getVMatrix());
//            shader.setUniform("cameraPosition", this.camera.getPosition());
//            shader.setUniform("specularPower", 10f);

            if (renderOptions == DEFAULT) {
                shader.setUniform("viewProjectionLightMatrix", sunlight.getVPMatrix());
                shader.setUniform("shadowMap", 1);

                //Vector4f dirLight = new Vector4f(sunlight.getDirection(), 0);

                //dirLight.mul(sunlight.getVMatrix());

//                shader.setUniform("directionalLight.direction", new Vector3f(dirLight.x, dirLight.y, dirLight.z));
                shader.setUniform("directionalLight.position", sunlight.getPosition());
                shader.setUniform("directionalLight.colour", sunlight.getColor());
                shader.setUniform("directionalLight.intensity", sunlight.getIntensity());
            }

            glBindVertexArray(model.getId());
            glEnableVertexAttribArray(0);

            if (renderOptions != WIREFRAME) {
                glEnableVertexAttribArray(1);
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, model.getTexture().getId());
            }
            glBindVertexArray(model.getId());
            glEnableVertexAttribArray(2);

            if (renderOptions == DEFAULT) {
//                Bind the texture to the shader
                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMap().getId());
            }

            List<Entity> batch = entities.get(model);

            for (Entity entity : batch) {
                shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));

                glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
            }

            glDisableVertexAttribArray(0);
            if (renderOptions != WIREFRAME)
                glDisableVertexAttribArray(1);

            glBindVertexArray(0);

            shader.unbind();

        }
    }

    private void renderDepthMap(Map<Model, List<Entity>> entities, DirectionalLight light) {
//    private void renderDepthMap(Map<Model, List<Entity>> entities, Sun light) {
        //Bind the buffer before writing to it
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        //Adjust viewport to the FBO size
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);

        //Make sure the depth buffer is clear
        glClear(GL_DEPTH_BUFFER_BIT);

        shaderDepth.bind();

        shaderDepth.setUniform("orthoProjectionViewMatrix", light.getVPMatrix());

        //Same process as the main shader
        for (Model model : entities.keySet()) {

            glBindVertexArray(model.getId());
            glEnableVertexAttribArray(0);

            List<Entity> batch = entities.get(model);

            for (Entity entity : batch) {
                //Create model matrix like in the main shader
                shaderDepth.setUniform("modelLightMatrix", Transformation.createTransformationMatrix(entity));

                glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
            }
        }

        shaderDepth.unbind();
        //Make sure the FBO is unbounded as well
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public void setWireframe(boolean wireframe) {
        if (wireframe)
            renderOptions = WIREFRAME;
    }

    public void setRenderOptions(RenderOptions renderOptions) {
        this.renderOptions = renderOptions;
    }

    public void switchRenderer() throws Exception {
        clear();
        cleanup();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shader.cleanup();
    }
}
