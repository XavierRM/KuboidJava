package Kuboid.manager;

import Kuboid.manager.entity.Entity;
import Kuboid.manager.lighting.DirectionalLight;
import Kuboid.manager.lighting.ShadowMap;
import Kuboid.manager.model.Model;
import Kuboid.manager.utils.Transformation;
import Kuboid.manager.utils.Utils;
import test.Launcher;

import java.util.List;
import java.util.Map;

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

    private boolean isWireframe;

    public RenderManager(Camera camera, boolean isWireframe) throws Exception {
        window = Launcher.getWindow();
        this.camera = camera;
        this.isWireframe = isWireframe;
    }

    public void init() throws Exception {

        //ShadowMap where the FBO and texture info are stored and initialized
        this.shadowMap = new ShadowMap();
        shader = new ShaderManager();
        shaderDepth = new ShaderManager();

        shaderDepth.createVertexShader(Utils.loadResource("/shaders/depth_vertex.vs"));
        shaderDepth.createFragmentShader(Utils.loadResource("/shaders/depth_fragment.fs"));

        if (isWireframe) {
            //Wireframe view
            shader.createVertexShader(Utils.loadResource("/shaders/vertexWireframe.vs"));
            shader.createFragmentShader(Utils.loadResource("/shaders/fragmentWireframe.fs"));
        } else {
            //Normal view
            shader.createVertexShader(Utils.loadResource("/shaders/vertexTexture.vs"));
            shader.createFragmentShader(Utils.loadResource("/shaders/fragmentTexture.fs"));

//            shader.createVertexShader(Utils.loadResource("/shaders/vertexNoShadowsTexture.vs"));
//            shader.createFragmentShader(Utils.loadResource("/shaders/fragmentNoShadowsTexture.fs"));
        }

        //Shader for calculating the depth texture, runs before the main shader
        shaderDepth.link();
        //Main shader
        shader.link();

        if (!isWireframe) {
            shader.createUniform("textureSampler");
        }

        //ShadowMap storing the depth texture
        shader.createUniform("shadowMap");

        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");

        //View + Projection matrix to convert a point in the 3D world space to the light perspective and check depth value
        shader.createUniform("viewProjectionLightMatrix");

        //OrthoProjection matrix to calculate the depth texture and the model matrix for the light
        shaderDepth.createUniform("orthoProjectionViewMatrix");
        shaderDepth.createUniform("modelLightMatrix");
    }

    public void render(Map<Model, List<Entity>> entities, DirectionalLight sunlight) throws Exception {
        //For now, it's just the sunlight, but it should be a list of lights

        //First we render from the lights perspective to get the depth texture
        renderDepthMap(entities, sunlight);

        //Readjust the Viewport size
        glViewport(0, 0, window.getWidth(), window.getHeight());

        //Render models in the game
        for (Model model : entities.keySet()) {
            shader.bind();

            if (!isWireframe)
                shader.setUniform("textureSampler", 0);

            //Should precompute this
            shader.setUniform("projectionMatrix", window.getProjectionMatrix());
            shader.setUniform("viewMatrix", Transformation.getViewMatrix(this.camera));

            shader.setUniform("viewProjectionLightMatrix", sunlight.getVPMatrix());
            shader.setUniform("shadowMap", 1);

            glBindVertexArray(model.getId());
            glEnableVertexAttribArray(0);

            if (!isWireframe) {
                glEnableVertexAttribArray(1);
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, model.getTexture().getId());
            }

            //Bind the texture to the shader
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMap().getId());

            List<Entity> batch = entities.get(model);

            for (Entity entity : batch) {
                shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));

                glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
            }

            glDisableVertexAttribArray(0);
            if (!isWireframe)
                glDisableVertexAttribArray(1);

            glBindVertexArray(0);

            shader.unbind();

        }
    }

    private void renderDepthMap(Map<Model, List<Entity>> entities, DirectionalLight light) {
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
        isWireframe = wireframe;
    }

    public void switchRenderer() throws Exception {
        clear();
        cleanup();
        init();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shader.cleanup();
    }
}
