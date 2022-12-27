package org.mgd.pam.commun;

public final class Disposition {
    private final int index;
    private final int pourmilleX;
    private final int pourmilleY;
    private float x;
    private float y;

    public Disposition(int index, int pourmilleX, int pourmilleY, float x, float y) {
        this.index = index;
        this.pourmilleX = pourmilleX;
        this.pourmilleY = pourmilleY;
        this.x = x;
        this.y = y;
    }

    public Disposition(int index, int pourmilleX, int pourmilleY) {
        this(index, pourmilleX, pourmilleY, 0, 0);
    }

    public int getIndex() {
        return index;
    }

    public int getPourmilleX() {
        return pourmilleX;
    }

    public int getPourmilleY() {
        return pourmilleY;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
