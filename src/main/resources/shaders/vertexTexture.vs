#version 400 core

in vec3 position;
in vec2 textureCoord;

out vec2 fragTextureCoord;
out vec4 mLightViewVertexPos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform mat4 viewProjectionLightMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    fragTextureCoord = textureCoord;
    mLightViewVertexPos = viewProjectionLightMatrix * transformationMatrix * vec4(position, 1.0);
}