package org.mgd.lwjgl.commun;

import java.util.UUID;

@FunctionalInterface
public interface Identifiable {
    UUID uuid();
}
