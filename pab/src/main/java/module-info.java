module mgd.pab {
    requires mgd.commun;
    requires org.apache.pdfbox;
    requires org.apache.fontbox;

    exports org.mgd.pam.commun;
    exports org.mgd.pam.exception;
    exports org.mgd.pam.pagination;
    exports org.mgd.pam.producteur.exception;
    exports org.mgd.pam.producteur;
    exports org.mgd.pam.zone.exception;
    exports org.mgd.pam.zone;
    exports org.mgd.pam;
}
