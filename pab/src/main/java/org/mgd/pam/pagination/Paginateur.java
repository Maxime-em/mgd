package org.mgd.pam.pagination;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.util.Matrix;
import org.mgd.pam.producteur.Producteur;
import org.mgd.pam.producteur.exception.ProducteurException;
import org.mgd.pam.producteur.exception.ProducteurStreamException;
import org.mgd.pam.zone.Zone;

import java.io.IOException;
import java.util.List;

/**
 * Permet la création des pages du document. Le paginateur doit savoir récupérer à partir de l'objet de type {@code <O>}
 * les éléments de type {@code <E>} qui permettre la production des pages du document. Il crée une page par élément de
 * la liste.
 *
 * @param <O> La classe de l'objet global qui doit permettre la production de toutes les pages.
 * @param <E> La classe de l'objet qui doit permettre la production d'une page.
 */
public abstract class Paginateur<O, E> {
    public static final int LONGUEUR_MARGE_DEFAUT = 30;
    public static final float POLICE_UNITE_RATIO = 1000.0f;
    protected final Producteur<E> producteur;

    protected Paginateur(Producteur<E> producteur) {
        this.producteur = producteur;
    }

    protected abstract List<E> lister(O objet);

    public void paginer(PDDocument document, PDFont police, O objet) throws ProducteurException {
        try {
            lister(objet).forEach(element -> {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    PDRectangle pageSize = page.getMediaBox();
                    float largeurPage = pageSize.getHeight();
                    float hauteurPage = pageSize.getWidth();
                    float longeurX = largeurPage - 2 * LONGUEUR_MARGE_DEFAUT;
                    float longueurY = hauteurPage - 2 * LONGUEUR_MARGE_DEFAUT;
                    content.transform(new Matrix(0, -1, 1, 0, 0, largeurPage));

                    producteur.produire(element, police, content, Zone.racine(longeurX, longueurY));
                } catch (IOException | ProducteurException e) {
                    throw new ProducteurStreamException(e);
                }
            });
        } catch (ProducteurStreamException e) {
            throw new ProducteurException(e);
        }
    }
}
