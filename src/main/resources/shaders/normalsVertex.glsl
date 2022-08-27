#version 400 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoord;
layout (location=2) in vec3 normal;

flat out vec3 fragNormal;
out vec2 fragTextureCoord;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
    fragTextureCoord = textureCoord;
    fragNormal = normal;
}
