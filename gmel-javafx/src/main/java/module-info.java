module org.mgd.gmel.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires mgd.commun;
    requires mgd.jab;
    requires mgd.pab;
    requires gmel.coeur;
    requires gmel.pdf;
    requires java.desktop;

    opens org.mgd.gmel.javafx to javafx.fxml, javafx.graphics;
    opens org.mgd.gmel.javafx.composant to javafx.fxml, javafx.graphics;
    opens org.mgd.gmel.javafx.composant.cellule to javafx.fxml;
    opens org.mgd.gmel.javafx.composant.champ to javafx.fxml;
    opens org.mgd.gmel.javafx.controle to javafx.fxml, javafx.graphics;
    opens org.mgd.gmel.javafx.scene to javafx.fxml, javafx.graphics;

    exports org.mgd.gmel.javafx.persistence.exception;
    exports org.mgd.gmel.javafx.persistence;
    exports org.mgd.gmel.javafx;
}
