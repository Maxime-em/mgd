module org.mgd.guerres.puniques.jeu {
    requires org.apache.logging.log4j;
    requires org.mgd.commun;
    requires org.mgd.jab;
    requires org.mgd.guerres.puniques.coeur;

    exports org.mgd.guerres.puniques.jeu;
    exports org.mgd.guerres.puniques.jeu.exception;
    exports org.mgd.guerres.puniques.jeu.souscription;
}