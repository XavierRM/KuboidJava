#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;

uniform mat4 modelLightMatrix;
uniform mat4 orthoProjectionViewMatrix;

void main()
{
    gl_Position = orthoProjectionViewMatrix * modelLightMatrix * vec4(position, 1.0f);
}