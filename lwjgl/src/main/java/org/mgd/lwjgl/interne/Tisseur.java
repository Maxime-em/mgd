package org.mgd.lwjgl.interne;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.mgd.lwjgl.ITisseur;
import org.mgd.lwjgl.exception.LwjglException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Tisseur implements ITisseur {
    private static final Map<String, Integer> textures = new HashMap<>();
    private static final Map<Font, Map<Character, Glyphe>> glyphes = new HashMap<>();
    private static final Map<Font, Integer> largeurs = new HashMap<>();
    private static final Map<Font, Integer> hauteurs = new HashMap<>();
    private static final Map<Font, BufferedImage> images = new HashMap<>();

    public static void compiler(String identifiant, Map<String, String> fichiers) throws LwjglException {
        try (MemoryStack pile = MemoryStack.stackPush()) {
            for (String pseudo : fichiers.keySet()) {
                textures.put(nom(identifiant, pseudo), creer(pile, fichiers.get(pseudo)));
            }
        }
    }

    public static Optional<Integer> obtenir(String identifiant, String pseudo) {
        return Optional.ofNullable(textures.get(nom(identifiant, pseudo)));
    }

    public static Optional<Integer> obtenir(String identifiant, Font police, String pseudo) {
        Optional<Integer> option = Optional.of(textures.computeIfAbsent(nom(police, pseudo), cle -> creer(police)));
        option.ifPresent(texture -> textures.put(nom(identifiant, pseudo), texture));
        return option;
    }

    public static Optional<Glyphe> glyphe(Font police, char c) {
        return Optional.ofNullable(glyphes.get(police).get(c));
    }

    private static int creer(MemoryStack pile, String fichier) throws LwjglException {
        IntBuffer largeur = pile.mallocInt(1);
        IntBuffer hauteur = pile.mallocInt(1);
        IntBuffer canaux = pile.mallocInt(1);

        ByteBuffer bytes = stbi_load(fichier, largeur, hauteur, canaux, 4);
        if (bytes == null) {
            throw new LwjglException(MessageFormat.format("L''image {0} est impossible à charger. {1}", fichier, stbi_failure_reason()));
        }

        int identifiant = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, identifiant);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, largeur.get(), hauteur.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, bytes);
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(bytes);

        return identifiant;
    }

    private static int creer(Font police) {
        creerGlyphes(police);
        calculerDimensions(police);
        creerImage(police);
        return creerTexture(police);
    }

    private static void creerGlyphes(Font police) {
        AtomicInteger dernierePosition = new AtomicInteger(0);
        glyphes.computeIfAbsent(police, cle ->
                        IntStream.range(32, 256)
                                .filter(i -> i != 127)
                                .mapToObj(i -> (char) i).collect(Collectors.toMap(Function.identity(), i -> new Glyphe(police, i))))
                .values()
                .forEach(glyphe -> {
                    glyphe.creerImage();
                    glyphe.positionner(dernierePosition.getAndAdd(glyphe.largeur));
                });
    }

    private static void calculerDimensions(Font police) {
        largeurs.put(police, glyphes.getOrDefault(police, Collections.emptyMap()).values().stream().filter(glyphe -> glyphe.valide).reduce(0, (resultat, glyphe) -> resultat + glyphe.largeur, Integer::sum));
        hauteurs.put(police, glyphes.getOrDefault(police, Collections.emptyMap()).values().stream().filter(glyphe -> glyphe.valide).reduce(0, (resultat, glyphe) -> Math.max(resultat, glyphe.hauteur), Math::max));
    }

    private static void creerImage(Font police) {
        if (largeurs.getOrDefault(police, 0) == 0 || hauteurs.getOrDefault(police, 0) == 0) {
            throw new IllegalArgumentException(MessageFormat.format("Les dimensions de la texture pour la police {0} ne sont pas correctes.", police.getFontName()));
        }

        BufferedImage image = images.computeIfAbsent(police, cle -> new BufferedImage(largeurs.get(police), hauteurs.get(police), BufferedImage.TYPE_INT_ARGB));
        Graphics2D graphique = image.createGraphics();

        AtomicInteger x = new AtomicInteger(0);
        glyphes.getOrDefault(police, Collections.emptyMap()).values().stream().filter(glyphe -> glyphe.valide).forEach(glyphe -> graphique.drawImage(glyphe.image, x.getAndAdd(glyphe.largeur), 0, null));
    }

    private static int creerTexture(Font police) {
        if (!images.containsKey(police)) {
            throw new IllegalArgumentException(MessageFormat.format("L''image de la texture pour la police {0} n''est spas correcte.", police.getFontName()));
        }
        BufferedImage image = images.get(police);

        int largeur = image.getWidth();
        int hauteur = image.getHeight();
        int[] pixels = new int[largeur * hauteur];
        image.getRGB(0, 0, largeur, hauteur, pixels, 0, largeur);

        ByteBuffer tanpom = MemoryUtil.memAlloc(largeur * hauteur * 4);
        for (int i = 0; i < hauteur; i++) {
            for (int j = 0; j < largeur; j++) {
                /*
                 * Pixel au format RGBA: 0xAARRGGBB
                 * Rouge 0xAARRGGBB >> 16 = 0x0000AARR
                 * Vert 0xAARRGGBB >> 8 = 0x00AARRGG
                 * Bleu 0xAARRGGBB >> 0 = 0xAARRGGBB
                 * Transparence 0xAARRGGBB >> 24 = 0x000000AA
                 */
                int pixel = pixels[i * largeur + j];
                tanpom.put((byte) ((pixel >> 16) & 0xFF));
                tanpom.put((byte) ((pixel >> 8) & 0xFF));
                tanpom.put((byte) (pixel & 0xFF));
                tanpom.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        tanpom.flip();

        int identifiant = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, identifiant);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, largeur, hauteur, 0, GL_RGBA, GL_UNSIGNED_BYTE, tanpom);
        glGenerateMipmap(GL_TEXTURE_2D);

        return identifiant;
    }

    private static String nom(String identifiant, String pseudo) {
        return String.join("@", identifiant, pseudo);
    }

    private static String nom(Font police, String pseudo) {
        return String.join("@", "police", police.getFontName(), String.valueOf(police.getSize()), pseudo);
    }

    public static class Glyphe {
        public static final float UNITE = 0.00019f;

        private final Font police;
        private final Character caractere;

        private BufferedImage image;
        private int position;
        private int largeur;
        private int hauteur;
        private boolean valide;

        public Glyphe(Font police, Character caractere) {
            this.police = police;
            this.caractere = caractere;
        }

        private void creerImage() {
            image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphique = image.createGraphics();
            graphique.setFont(police);

            FontMetrics metrique = graphique.getFontMetrics();
            graphique.dispose();

            largeur = metrique.charWidth(caractere);
            hauteur = metrique.getHeight();
            valide = largeur != 0;
            if (valide) {
                image = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);
                graphique = image.createGraphics();
                graphique.setFont(police);
                graphique.setPaint(java.awt.Color.WHITE);
                graphique.drawString(String.valueOf(caractere), 0, metrique.getAscent());
                graphique.dispose();
            }
        }

        private void positionner(int position) {
            this.position = position;
        }

        public float positionHomogene() {
            return (float) position / largeurs.getOrDefault(police, 1);
        }

        public float largeurHomogene() {
            return (float) largeur / largeurs.getOrDefault(police, 1);
        }

        public float largeurUnitaire() {
            return largeur * UNITE;
        }

        public float hauteurUnitaire() {
            return hauteur * UNITE;
        }

        public boolean valide() {
            return valide;
        }
    }

    public static class Scribe {
        private final Map<float[], Glyphe> glyphes;
        private float largeur;
        private float hauteur;

        public Scribe(Font police, String texte) {
            this.glyphes = new HashMap<>();
            this.largeur = 0f;
            this.hauteur = 0f;
            AtomicReference<Float> bas = new AtomicReference<>(0f);
            AtomicReference<Float> hauteurLigne = new AtomicReference<>(0f);
            AtomicReference<Float> gauche = new AtomicReference<>();
            texte.lines().forEach(ligne -> {
                bas.getAndAccumulate(hauteurLigne.get(), Float::sum);
                hauteurLigne.set(0f);
                gauche.set(0f);
                ligne.chars()
                        .mapToObj(caractere -> Tisseur.glyphe(police, (char) caractere))
                        .filter(option -> option.isPresent() && option.get().valide())
                        .forEach(option -> {
                            Tisseur.Glyphe glyphe = option.get();
                            glyphes.put(new float[]{
                                    bas.get(),
                                    gauche.get() + glyphe.largeurUnitaire(),
                                    bas.get() + glyphe.hauteurUnitaire(),
                                    gauche.get(),
                            }, glyphe);
                            gauche.getAndAccumulate(glyphe.largeurUnitaire(), Float::sum);
                            hauteurLigne.getAndAccumulate(glyphe.hauteurUnitaire(), Math::max);
                        });
                this.largeur = Math.max(this.largeur, gauche.get());
                this.hauteur += hauteurLigne.get();
            });
            glyphes.keySet().forEach(valeurs -> {
                valeurs[0] = this.hauteur - valeurs[0];
                valeurs[2] = this.hauteur - valeurs[2];
            });
        }

        public Map<float[], Glyphe> glyphes() {
            return glyphes;
        }

        public float largeur() {
            return largeur;
        }

        public float hauteur() {
            return hauteur;
        }
    }
}
