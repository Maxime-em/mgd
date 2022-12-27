package org.mgd.gmel.javafx.controle;

import javafx.beans.NamedArg;
import javafx.scene.control.Skin;
import org.mgd.gmel.javafx.controle.evenement.BoutonIconeEvent;

import java.util.Objects;

@SuppressWarnings("java:S110")
public class BoutonIcone extends Bouton {
    private static final String DEFAULT_STYLE_CLASS = "bouton-icone";
    private final BoutonIconeType type;

    public BoutonIcone(@NamedArg("type") BoutonIconeType type, @NamedArg(value = "taille") BoutonIconeTaille taille) {
        super();

        this.type = type;
        this.getStyleClass().setAll(DEFAULT_STYLE_CLASS, this.type.getStyle(), taille.getStyle());
    }

    @Override
    public void fireCliquer() {
        if (!this.isDisabled()) {
            this.fireEvent(new BoutonIconeEvent(type));
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BoutonIconeTheme(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(getClass().getResource("css/bouton-icone.css")).toExternalForm();
    }
}
