#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texture;

out vec2 sortie_basique_vert;

uniform mat4 projection;
uniform mat4 vision;
uniform mat4 transformation;

void main() {
    gl_Position = projection * vision * transformation * vec4(position, 1.0);
    sortie_basique_vert = texture;
}