#version 400 core

in vec2 fragTextureCoord;
in vec4 mLightViewVertexPos;

out vec4 fragColour;

uniform sampler2D textureSampler;
uniform sampler2D shadowMap;

float calcShadow(vec4 position) {

    float shadowFactor = 0.3;
    float bias = 0.0005;
    vec3 projCoords = position.xyz;

    projCoords = projCoords * 0.5 + 0.5;

    if(projCoords.z - bias < texture(shadowMap, projCoords.xy).r) {
        shadowFactor = 0;
    }

    return 1 - shadowFactor;

}

void main() {
    float shadow = calcShadow(mLightViewVertexPos);
    fragColour = shadow * texture(textureSampler, fragTextureCoord);
}