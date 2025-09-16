#version 330

in vec2 sortie_basique_vert;

out vec4 sortie_basique_frag;

uniform sampler2D echantillonneur;

void main() {
    vec4 couleur = texture(echantillonneur, sortie_basique_vert);
    sortie_basique_frag = vec4(couleur.x * 0.7, couleur.y * 0.5, couleur.z, 1.0);
}