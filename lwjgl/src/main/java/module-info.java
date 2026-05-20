module org.mgd.lwjgl {
    requires org.apache.logging.log4j;
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires org.lwjgl.stb;
    requires org.lwjgl.nanovg;
    requires org.mgd.commun;
    requires org.jetbrains.annotations;

    exports org.mgd.lwjgl;
    exports org.mgd.lwjgl.exception;
    exports org.mgd.lwjgl.affichage;
    exports org.mgd.lwjgl.affichage.element;
    exports org.mgd.lwjgl.affichage.tetehaute;
    exports org.mgd.lwjgl.affichage.tetehaute.composant;
    exports org.mgd.lwjgl.affichage.tetehaute.nvg;
    exports org.mgd.lwjgl.affichage.element.forme;
    exports org.mgd.lwjgl.souscription;
}