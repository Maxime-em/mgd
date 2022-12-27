module gmel.coeur {
    requires mgd.commun;
    requires mgd.jab;

    opens org.mgd.gmel.coeur.commun to com.google.gson;
    opens org.mgd.gmel.coeur.dto to com.google.gson;
    opens org.mgd.gmel.coeur.objet to com.google.gson;
    opens org.mgd.gmel.coeur.persistence to mgd.jab;

    exports org.mgd.gmel.coeur.commun;
    exports org.mgd.gmel.coeur.dto;
    exports org.mgd.gmel.coeur.objet;
    exports org.mgd.gmel.coeur.persistence;
    exports org.mgd.gmel.coeur.source;
    exports org.mgd.gmel.coeur;
}
