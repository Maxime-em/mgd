package org.mgd.jab.objet;

public class Pegi extends Jo {
    private final Joc<Integer> age = new Joc<>(this);

    public Integer getAge() {
        return age.get();
    }

    public void setAge(Integer age) {
        this.age.set(age);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Pegi pegi)) return false;
        return age.idem(pegi.age);
    }
}
