package Kuboid.manager.lighting;

import Kuboid.manager.model.DepthTexture;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {

    public static final int SHADOW_MAP_WIDTH = 2048;
    public static final int SHADOW_MAP_HEIGHT = 2048;

    private int depthMapFBO;
    private DepthTexture depthMap;

    public ShadowMap() throws Exception {
        //Create FBO for the depth map
        depthMapFBO = glGenFramebuffers();

        //Create texture to pass to the second processing (The one from the cameras POV)
        depthMap = new DepthTexture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);

        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getId(), 0);

        //Make sure we set the options, so it doesn't register color values, only depth values
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        //Check if the FBO generated correctly
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new Exception("Could not create framebuffer");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public int getDepthMapFBO() {
        return depthMapFBO;
    }

    public DepthTexture getDepthMap() {
        return depthMap;
    }

    public void cleanup() {
        glDeleteFramebuffers(depthMapFBO);
    }
}
