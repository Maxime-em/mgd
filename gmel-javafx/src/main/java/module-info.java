module org.mgd.gmel.javafx {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires org.mgd.commun;
    requires org.mgd.jab;
    requires org.mgd.pab;
    requires org.mgd.gmel.coeur;
    requires org.mgd.gmel.pdf;

    opens org.mgd.gmel.javafx to javafx.fxml, javafx.graphics;
    opens org.mgd.gmel.javafx.composant to javafx.fxml, javafx.graphics;
    opens org.mgd.gmel.javafx.composant.cellule to javafx.fxml;
    opens org.mgd.gmel.javafx.controle to javafx.fxml, javafx.graphics;
    opens org.mgd.gmel.javafx.scene to javafx.fxml, javafx.graphics;

    exports org.mgd.gmel.javafx.connexions;
    exports org.mgd.gmel.javafx.connexions.exception;
    exports org.mgd.gmel.javafx;
}
