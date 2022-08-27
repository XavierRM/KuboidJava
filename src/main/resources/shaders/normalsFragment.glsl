#version 400 core

in vec2 fragTextureCoord;
flat in vec3 fragNormal;

out vec4 fragColour;

void main() {
    if (fragNormal.x < 0) {
        fragColour = vec4(0, 0, 0, 0);
    } else {
        if (fragNormal.y < 0) {
            fragColour = vec4(0.5, 0.5, 0.5, 0);
        } else {
            if (fragNormal.z < 0) {
                fragColour = vec4(1, 1, 1, 0);
            } else {
                fragColour = vec4(fragNormal, 0);
            }
        }
    }
}
