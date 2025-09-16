#version 330

in vec4 sortie_basique_vert;

out vec4 sortie_basique_frag;

uniform int colorisation;
uniform sampler2D echantillonneur;

void main()
{
    if (colorisation == 0) {
        sortie_basique_frag = sortie_basique_vert;
    } else if (colorisation == 1) {
        sortie_basique_frag = texture(echantillonneur, sortie_basique_vert.xy);
    } else {
        sortie_basique_frag = vec4(sortie_basique_vert.xyz, 1.0);
    }
}