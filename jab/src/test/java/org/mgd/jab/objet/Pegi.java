package org.mgd.jab.objet;

import org.mgd.jab.dto.PegiDto;
import org.mgd.jab.persistence.PegiJao;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

public class Pegi extends Jo<PegiDto> {
    private final Joc<Integer> age = new Joc<>(this);

    public Integer getAge() {
        return age.get();
    }

    public void setAge(Integer age) {
        this.age.set(age);
    }

    @Override
    public PegiDto dto() {
        return new PegiJao().decharger(this);
    }

    @Override
    public void depuis(PegiDto dto) throws VerificationException {
        Verifications.nonBorne(dto.getAge(), 0, 99, "L''age du système PEGI doit être compris entre {1} et {2} ans.");

        setAge(dto.getAge());
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Pegi pegi)) return false;
        return age.idem(pegi.age);
    }
}
