module org.mgd.lwjgl {
    requires java.desktop;
    requires org.apache.logging.log4j;
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires org.lwjgl.stb;
    requires org.lwjgl.nanovg;
    requires org.mgd.commun;

    exports org.mgd.lwjgl;
    exports org.mgd.lwjgl.exception;
    exports org.mgd.lwjgl.forme;
    exports org.mgd.lwjgl.element;
}