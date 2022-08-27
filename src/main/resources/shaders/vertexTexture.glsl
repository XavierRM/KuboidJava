#version 400 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoord;
layout (location=2) in vec3 normal;

//flat out vec3 fragNormal;
out vec2 fragTextureCoord;
out vec4 mLightViewVertexPos;
flat out vec3 mvVertexNormal;
out vec3 mvVertexPos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform mat4 viewLightMatrix;
uniform mat4 viewProjectionLightMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    fragTextureCoord = textureCoord;
    mvVertexNormal = normalize(transformationMatrix * vec4(normal, 0.0)).xyz;
    mvVertexPos = (transformationMatrix * vec4(position, 1.0)).xyz;
    mLightViewVertexPos = viewProjectionLightMatrix * transformationMatrix * vec4(position, 1.0);
}