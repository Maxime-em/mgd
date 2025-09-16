module org.mgd.commun {
    requires com.google.gson;

    opens org.mgd.commun to com.google.gson;
    opens org.mgd.temps to com.google.gson;

    exports org.mgd.commun;
    exports org.mgd.connexion;
    exports org.mgd.connexion.exception;
    exports org.mgd.temps;
    exports org.mgd.utilitaire;
}
