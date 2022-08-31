#version 400 core

in vec3 position;
in vec2 textureCoord;
layout (location=2) in vec3 normal;

out vec2 fragTextureCoord;
flat out vec3 mvVertexNormal;
out vec3 mvVertexPos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;


void main() {
    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    fragTextureCoord = textureCoord;
    mvVertexNormal = normalize(transformationMatrix * vec4(normal, 0.0)).xyz;
    mvVertexPos = (transformationMatrix * vec4(position, 1.0)).xyz;
}