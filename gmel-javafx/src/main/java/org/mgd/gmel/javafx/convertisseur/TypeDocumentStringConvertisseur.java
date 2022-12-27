package org.mgd.gmel.javafx.convertisseur;

import javafx.util.StringConverter;
import org.mgd.gmel.coeur.commun.TypeDocument;

public class TypeDocumentStringConvertisseur extends StringConverter<TypeDocument> {
    @Override
    public String toString(TypeDocument typeDocument) {
        return typeDocument.getLibelle();
    }

    @Override
    public TypeDocument fromString(String libelle) {
        return TypeDocument.depuisLibelle(libelle);
    }
}
