package org.mgd.pam.producteur;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.mgd.pam.producteur.exception.ProducteurException;
import org.mgd.pam.zone.Zone;

/**
 * Permet la production d'un contenu à partie d'un élément de type {@code <E>}.
 *
 * @param <E> La classe de l'objet qui doit permettre la production du contenu.
 */
public abstract class Producteur<E> {
    public abstract void produire(E element, PDFont police, PDPageContentStream content, Zone racine) throws ProducteurException;
}
