#version 330

in vec2 sortie_basique_vert;

out vec4 sortie_basique_frag;

uniform sampler2D echantillonneur;

void main() {
    sortie_basique_frag = texture(echantillonneur, sortie_basique_vert);
}