#version 400 core

// Input vertex data, different for all executions of this shader.
in vec3 vertexPosition;

// Values that stay constant for the whole mesh.
uniform mat4 depthMVP;

void main(){
 gl_Position =  depthMVP * vec4(vertexPosition, 1);
}