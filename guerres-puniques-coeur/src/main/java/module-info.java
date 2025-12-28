module org.mgd.guerres.puniques.coeur {
    requires org.jetbrains.annotations;
    requires org.apache.logging.log4j;
    requires org.mgd.commun;
    requires org.mgd.jab;

    opens org.mgd.guerres.puniques.coeur.commun to com.google.gson;
    opens org.mgd.guerres.puniques.coeur.dto to com.google.gson;
    opens org.mgd.guerres.puniques.coeur.objet to com.google.gson;
    opens org.mgd.guerres.puniques.coeur.persistence to org.mgd.jab;

    exports org.mgd.guerres.puniques.coeur.commun;
    exports org.mgd.guerres.puniques.coeur.dto;
    exports org.mgd.guerres.puniques.coeur.objet;
    exports org.mgd.guerres.puniques.coeur.persistence;
    exports org.mgd.guerres.puniques.coeur.source;
    exports org.mgd.guerres.puniques.coeur;
}