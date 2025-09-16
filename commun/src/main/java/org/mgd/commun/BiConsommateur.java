package org.mgd.commun;

@FunctionalInterface
public interface BiConsommateur<T, U> {
    void accepter(T t, U u) throws Exception;
}
