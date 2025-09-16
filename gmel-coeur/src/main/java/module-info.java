module org.mgd.gmel.coeur {
    requires org.mgd.commun;
    requires org.mgd.jab;

    opens org.mgd.gmel.coeur.commun to com.google.gson;
    opens org.mgd.gmel.coeur.dto to com.google.gson;
    opens org.mgd.gmel.coeur.objet to com.google.gson;
    opens org.mgd.gmel.coeur.persistence to org.mgd.jab;

    exports org.mgd.gmel.coeur.commun;
    exports org.mgd.gmel.coeur.dto;
    exports org.mgd.gmel.coeur.objet;
    exports org.mgd.gmel.coeur.persistence;
    exports org.mgd.gmel.coeur.source;
    exports org.mgd.gmel.coeur;
}
