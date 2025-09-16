module org.mgd.pab {
    requires org.apache.pdfbox;
    requires org.apache.fontbox;
    requires org.mgd.commun;

    exports org.mgd.pam.commun;
    exports org.mgd.pam.exception;
    exports org.mgd.pam.pagination;
    exports org.mgd.pam.producteur.exception;
    exports org.mgd.pam.producteur;
    exports org.mgd.pam.zone.exception;
    exports org.mgd.pam.zone;
    exports org.mgd.pam;
}
