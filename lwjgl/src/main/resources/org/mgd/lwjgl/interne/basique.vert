#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec4 texture;

out vec4 sortie_basique_vert;

uniform mat4 projection;
uniform mat4 vision;
uniform mat4 transformation;
uniform int fixe;

void main()
{
    if (fixe != 1) {
        gl_Position = projection * vision * transformation * vec4(position, 1.0);
    } else {
        gl_Position = vec4(position, 1.0);
    }

    sortie_basique_vert = texture;
}