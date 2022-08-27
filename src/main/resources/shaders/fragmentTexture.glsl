#version 400 core

in vec2 fragTextureCoord;
//flat in vec3 fragNormal;

//From lights perspective (ShadowMapping)
in vec4 mLightViewVertexPos;
//From cameras perspective (Illumination)
flat in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColour;

struct DirectionalLight {
    vec3 colour;
    vec3 position;
    float intensity;
};

//uniform float specularPower;
uniform sampler2D textureSampler;
uniform sampler2D shadowMap;
uniform DirectionalLight directionalLight;
//uniform mat4 viewLightMatrix;
//uniform vec3 cameraPosition;

vec3 ambientLight = vec3(0.3, 0.3, 0.3);
//vec3 lightPosition = directionalLight.direction * 1000;
float reflectance = 0;
float specularPower = 10;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColours(vec2 textCoord) {
    ambientC = texture(textureSampler, textCoord);
    diffuseC = ambientC;
    specularC = ambientC;
}


vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 lightPos, vec3 normal)
{
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);
    vec3 fromLightDir = normalize(-lightPos);

    // Diffuse Light
    float diffuseFactor = max(dot(-fromLightDir, normal), 0.0);

    //If the diffuseFactor surpases a certain threshold is enough to make it iluminated, but since the values might
    //be really low it's not enough to make a difference in the end
    if (diffuseFactor >= 0.3 && diffuseFactor < 0.7)
    diffuseFactor = 0.71;

    if (diffuseFactor > 0 && diffuseFactor < 0.3)
    diffuseFactor += 0.1;

    diffuseColour = diffuseC * vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

    // Specular Light
    //    vec3 camera_direction = normalize(-position);
    //    vec3 reflected_light = normalize(reflect(fromLightDir , normal));
    //    float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
    //    specularFactor = pow(specularFactor, specularPower);
    //    specColour = specularC * light_intensity * specularFactor * reflectance * vec4(light_colour, 1.0);

    //    return (diffuseColour + specColour);
    return (diffuseColour);
}

vec4 calcDirectionalLight(DirectionalLight directionalLight, vec3 position, vec3 normal) {

    //    return calcLightColour(directionalLight.colour, directionalLight.intensity, position, directionalLight.direction, normal);
    return calcLightColour(directionalLight.colour, directionalLight.intensity, position, directionalLight.position, normal);
}

float calcShadow(vec4 position) {

    float shadowFactor = 0.3;
    float bias = 0.0005;
    vec3 projCoords = position.xyz;

    projCoords = projCoords * 0.5 + 0.5;

    if (projCoords.z - bias < texture(shadowMap, projCoords.xy).r) {
        shadowFactor = 0;
    }

    return 1 - shadowFactor;

}

void main() {
    setupColours(fragTextureCoord);
    float shadow = calcShadow(mLightViewVertexPos);
    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, mvVertexPos, mvVertexNormal);

    fragColour = shadow * (ambientC * vec4(ambientLight, 1) + diffuseSpecularComp);
    //    fragColour = shadow * (ambientC  + diffuseSpecularComp);
    //    fragColour = vec4(mvVertexNormal, 0.0);

    //    if(fragNormal.x < 0) {
    //        fragColour = vec4(0, 0, 0, 0);
    //    } else {
    //        if(fragNormal.y < 0) {
    //            fragColour = vec4(0.5, 0.5, 0.5, 0);
    //        } else {
    //            if(fragNormal.z < 0) {
    //               fragColour = vec4(1, 1, 1, 0);
    //            } else {
    //                fragColour = vec4(fragNormal, 0);
    //            }
    //        }
    //    }
    //    fragColour = vec4(fragNormal, 0);

}