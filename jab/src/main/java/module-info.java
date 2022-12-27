module mgd.jab {
    requires com.google.gson;

    opens org.mgd.jab to com.google.gson;
    opens org.mgd.jab.dto to com.google.gson;
    opens org.mgd.jab.objet to com.google.gson;
    opens org.mgd.jab.source to com.google.gson;
    opens org.mgd.jab.persistence to com.google.gson;

    exports org.mgd.jab.dto;
    exports org.mgd.jab.exception;
    exports org.mgd.jab.source;
    exports org.mgd.jab.objet;
    exports org.mgd.jab.persistence.exception;
    exports org.mgd.jab.persistence;
    exports org.mgd.jab.utilitaire.exception;
    exports org.mgd.jab.utilitaire;
    exports org.mgd.jab;
}
