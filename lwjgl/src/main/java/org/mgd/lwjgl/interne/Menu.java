package org.mgd.lwjgl.interne;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.mgd.lwjgl.exception.LwjglException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;

public class Menu {
    private boolean visible;
    private long contexte;
    private NVGColor couleur;
    private ByteBuffer police;

    public void initialiser() throws LwjglException, IOException {
        this.contexte = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (this.contexte == 0L) {
            throw new LwjglException("Impossible d'initialiser NanoVG.");
        }

        byte[] bytes = Files.readAllBytes(Path.of("C:\\WINDOWS\\FONTS\\calibri.ttf"));
        police = BufferUtils.createByteBuffer(bytes.length);
        police.put(bytes);
        police.flip();

        int font = nvgCreateFontMem(contexte, "police", police, false);
        if (font < 0) {
            throw new LwjglException("Impossible de charger la police d'écriture du menu.");
        }
        couleur = NVGColor.create();
        couleur.r(0.5f);
        couleur.g(0.5f);
        couleur.b(0.5f);
        couleur.a(1f);
    }

    public void produire(float largeur, float hauteur) {
        nvgBeginFrame(contexte, largeur, hauteur, 1f);
        nvgBeginPath(contexte);
        nvgRect(contexte, 0, hauteur - 100, largeur, 50);
        nvgFillColor(contexte, couleur);
        nvgFill(contexte);
        nvgEndFrame(contexte);
    }

    public void liberer() {
        nvgDelete(contexte);
    }

    public void basculer() {
        visible = !visible;
    }

    public boolean visible() {
        return visible;
    }
}
