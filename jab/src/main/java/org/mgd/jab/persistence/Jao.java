package org.mgd.jab.persistence;

import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.commun.Tabulable;
import org.mgd.jab.JabCreation;
import org.mgd.jab.JabSauvegarde;
import org.mgd.jab.JabSingletons;
import org.mgd.jab.JabTable;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.dto.ReferenceDto;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.persistence.exception.JaoRuntimeException;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Classe de base des objets permettant de manipuler les données provenant d'un système de fichiers JSON.
 * Chaque classe fille sera associée à une classe métier {@link O} et une classe de transfert {@link D}
 * et sera instanciée pour lire et écrire les données du système de fichiers JSON.
 *
 * @param <O> classe métier de l'objet
 * @param <D> classe de transfert de données de l'objet
 * @author Maxime
 */
public abstract class Jao<D extends Dto, O extends Jo> {
    private static final Logger LOGGER = LogManager.getLogger(Jao.class);

    private final Class<D> classeDto;
    private final Class<O> classeJo;
    private final JabTable<O> table;
    private final JabCreation creation;

    protected Jao(Class<D> classeDto, Class<O> classeJo) {
        this.classeDto = classeDto;
        this.classeJo = classeJo;
        this.table = JabSingletons.table(classeJo);
        this.creation = JabSingletons.creation();
    }

    public abstract D dto(O objet);

    public abstract void enrichir(D dto, O objet) throws JaoExecutionException, JaoParseException, VerificationException;

    protected abstract void copier(O source, O cible) throws JaoExecutionException, JaoParseException;

    private D dto(Path fichier) throws JaoParseException {
        try {
            JabSingletons.sauvegarde();
            return JabSauvegarde.gsonSauvegarde.fromJson(Files.newBufferedReader(fichier), classeDto);
        } catch (IOException | JsonParseException e) {
            throw new JaoParseException(MessageFormat.format("Impossible de charger le fichier {0}.", fichier), e);
        }
    }

    public JabTable<O> table() {
        return table;
    }

    public D decharger(O objet) {
        return decharger(objet, () -> dto(objet));
    }

    private <T extends Dto> T decharger(O objet, Supplier<T> obtenirDto) {
        if (objet == null) {
            return null;
        }
        T dto = obtenirDto.get();
        dto.setIdentifiant(objet.getIdentifiant().toString());
        return dto;
    }

    public List<D> decharger(Collection<O> objets) {
        return objets.stream().map(this::decharger).toList();
    }

    public <K> Map<K, D> decharger(Map<K, O> objets) {
        return objets.entrySet()
                .stream()
                .map(element -> new AbstractMap.SimpleEntry<>(element.getKey(), decharger(element.getValue())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    public <V extends Dto, W extends Jo> Map<D, V> decharger(Jao<V, W> jao, Map<O, W> objets) {
        return objets.entrySet()
                .stream()
                .map(element -> new AbstractMap.SimpleEntry<>(decharger(element.getKey()), jao.decharger(element.getValue())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    @SuppressWarnings("unchecked")
    public D[][] decharger(O[][] objets) {
        D[][] dtos = (D[][]) Array.newInstance(classeDto, objets.length, objets[0].length);
        for (int i = 0; i < objets.length; i++) {
            for (int j = 0; j < objets[i].length; j++) {
                dtos[i][j] = decharger(objets[i][j]);
            }
        }
        return dtos;
    }

    public O dupliquer(O source) throws JaoExecutionException, JaoParseException {
        O objet = nouveau();
        copier(source, objet);
        return objet;
    }

    public List<O> dupliquer(Collection<O> sources) throws JaoExecutionException, JaoParseException {
        List<O> nouveaux = new ArrayList<>(sources.size());
        for (O source : sources) {
            nouveaux.add(dupliquer(source));
        }
        return nouveaux;
    }

    public <K> Map<K, O> dupliquer(Map<K, O> sources) throws JaoExecutionException, JaoParseException {
        Map<K, O> nouveaux = HashMap.newHashMap(sources.size());
        for (Map.Entry<K, O> element : sources.entrySet()) {
            nouveaux.put(element.getKey(), dupliquer(element.getValue()));
        }
        return nouveaux;
    }

    public <V extends Dto, W extends Jo> Map<O, W> dupliquer(Jao<V, W> jao, Map<O, W> sources) throws JaoExecutionException, JaoParseException {
        Map<O, W> nouveaux = HashMap.newHashMap(sources.size());
        for (Map.Entry<O, W> element : sources.entrySet()) {
            nouveaux.put(dupliquer(element.getKey()), jao.dupliquer(element.getValue()));
        }
        return nouveaux;
    }

    @SuppressWarnings("unchecked")
    public O[][] dupliquer(O[][] sources) throws JaoExecutionException, JaoParseException {
        O[][] nouveaux = (O[][]) Array.newInstance(classeDto, sources.length, sources[0].length);
        for (int i = 0; i < sources.length; i++) {
            for (int j = 0; j < sources[i].length; j++) {
                nouveaux[i][j] = dupliquer(sources[i][j]);
            }
        }
        return nouveaux;
    }

    public O nouveau() throws JaoExecutionException, JaoParseException {
        return nouveau(UUID.randomUUID(), _ -> {
        });
    }

    public O nouveau(Enrichissement<O> enrichir) throws JaoExecutionException, JaoParseException {
        return nouveau(UUID.randomUUID(), enrichir);
    }

    public O nouveau(BiConsumer<O, Jo[]> enrichir, Jo... autresJos) throws JaoExecutionException, JaoParseException {
        return nouveau(UUID.randomUUID(), jo -> enrichir.accept(jo, autresJos));
    }

    private O nouveau(UUID identifiant, Enrichissement<O> enrichir) throws JaoExecutionException, JaoParseException {
        try {
            O objet = classeJo.getConstructor().newInstance();
            objet.setIdentifiant(identifiant);
            enrichir.faire(objet);
            objet.setDetache(false);

            table.referencer(objet);

            return objet;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new JaoExecutionException(e);
        } catch (VerificationException e) {
            throw new JaoParseException(e);
        }
    }

    public void enrichirPostCreation(D dto, O objet) throws JaoExecutionException, JaoParseException {
        // Rien à faire
    }

    public O charger(Path fichier) throws JaoParseException, JaoExecutionException {
        if (creation.getGermes().containsKey(fichier)) {
            return table.selectionner(creation.getGermes().get(fichier));
        } else {
            D dto = dto(fichier);
            O objet = charger(dto, null);

            creation.ajouter(fichier, objet.getIdentifiant(), this);

            enrichirPostCreation(dto, objet);

            return objet;
        }
    }

    public O charger(D dto, Jo parent) throws JaoParseException, JaoExecutionException {
        if (dto == null) {
            throw new JaoParseException("Objet inconnu.");
        } else {
            try {
                return charger(dto, parent, dto.getIdentifiant() != null ? UUID.fromString(dto.getIdentifiant()) : UUID.randomUUID());
            } catch (JaoRuntimeException e) {
                throw new JaoParseException(e);
            }
        }
    }

    private O charger(D dto, Jo parent, UUID identifiant) throws JaoExecutionException, JaoParseException {
        return table.existe(identifiant) ? table.selectionner(identifiant) : construire(dto, parent, identifiant);
    }

    private O construire(D dto, Jo parent, UUID identifiant) throws JaoExecutionException, JaoParseException {
        O objet = nouveau(identifiant, nouveau -> enrichir(dto, nouveau));

        if (parent != null) {
            objet.ajouterParent(parent);
        }

        return objet;
    }

    public List<O> charger(Collection<D> dtos, Jo parent) throws JaoParseException, JaoExecutionException {
        List<O> objets = new ArrayList<>(dtos.size());
        for (D dto : dtos) {
            objets.add(charger(dto, parent));
        }
        return objets;
    }

    public <K> Map<K, O> charger(Map<K, D> dtos, Jo parent) throws JaoParseException {
        try {
            return dtos.entrySet().stream().map(element -> {
                try {
                    return new AbstractMap.SimpleEntry<>(element.getKey(), charger(element.getValue(), parent));
                } catch (JaoParseException | JaoExecutionException e) {
                    throw new JaoRuntimeException(e);
                }
            }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        } catch (JaoRuntimeException e) {
            throw new JaoParseException(MessageFormat.format("Impossible de charger le tableau associatif d''objets. {0}", e.getCause().getMessage()), e);
        }
    }

    public <V extends Dto, W extends Jo> Map<O, W> charger(Jao<V, W> jao, Map<D, V> dtos, Jo parent) throws JaoParseException {
        try {
            return dtos.entrySet().stream().map(element -> {
                try {
                    return new AbstractMap.SimpleEntry<>(charger(element.getKey(), parent), jao.charger(element.getValue(), parent));
                } catch (JaoParseException | JaoExecutionException e) {
                    throw new JaoRuntimeException(e);
                }
            }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        } catch (JaoRuntimeException e) {
            throw new JaoParseException(MessageFormat.format("Impossible de charger le tableau associatif d''objets. {0}", e.getCause().getMessage()), e);
        }
    }

    @SuppressWarnings("unchecked")
    public O[][] charger(D[][] dtos, Jo parent) throws JaoParseException, JaoExecutionException {
        O[][] objets = (O[][]) Array.newInstance(classeJo, dtos.length, dtos[0].length);
        for (int ligne = 0; ligne < dtos.length; ligne++) {
            for (int colonne = 0; colonne < dtos[ligne].length; colonne++) {
                objets[ligne][colonne] = charger(dtos[ligne][colonne], parent);
                if (objets[ligne][colonne] instanceof Tabulable element) {
                    element.ligne(ligne);
                    element.colonne(colonne);
                }
            }
        }
        return objets;
    }

    public <V extends Dto, W extends Jo, X extends Jao<V, W>> ReferenceDto<V, W, X> dechargerVersReference(O objet, Class<W> classeRacine, Class<X> classeFournisseur) {
        if (objet == null) {
            return null;
        }
        ReferenceDto<V, W, X> referenceDto = decharger(objet, ReferenceDto::new);
        referenceDto.setChemin(creation.getFichiers()
                .entrySet()
                .stream()
                .filter(element -> objet.racines(classeRacine).stream().anyMatch(racine -> racine.getIdentifiant().equals(element.getKey())))
                .findFirst()
                .orElseThrow()
                .getValue());
        referenceDto.setClasseFournisseur(classeFournisseur);
        return referenceDto;
    }

    public <V extends Dto, W extends Jo, X extends Jao<V, W>> List<ReferenceDto<V, W, X>> dechargerVersReferences(Collection<O> objets, Class<W> classeRacine, Class<X> classeFournisseur) {
        return objets.stream().map(objet -> dechargerVersReference(objet, classeRacine, classeFournisseur)).toList();
    }

    public <V extends Dto, W extends Jo, X extends Jao<V, W>> O chargerParReference(ReferenceDto<V, W, X> referenceDto) throws JaoParseException, JaoExecutionException {
        if (referenceDto == null) {
            throw new JaoParseException("Référence vers un objet inconnu.");
        } else if (referenceDto.getIdentifiant() == null) {
            throw new JaoParseException("Référence vers un objet d'identifiant inconnu.");
        } else {
            UUID identifiant = UUID.fromString(referenceDto.getIdentifiant());
            if (!table.existe(identifiant)) {
                constuireParReference(referenceDto, identifiant);
            }
            if (!table.existe(identifiant)) {
                throw new JaoExecutionException(MessageFormat.format("Référence vers un objet d''identifiant {0} introuvable.", identifiant));
            }
            if (LOGGER.isDebugEnabled() && referenceDto.getChemin() == null) {
                LOGGER.warn("La réference {} n'a pas de chemin.", referenceDto.getIdentifiant());
            }
            if (LOGGER.isDebugEnabled() && referenceDto.getClasseFournisseur() == null) {
                LOGGER.warn("La réference {} n'a pas de classe pour son fournisseur.", referenceDto.getIdentifiant());
            }
            return table.selectionner(identifiant);
        }
    }

    private <V extends Dto, W extends Jo, X extends Jao<V, W>> void constuireParReference(ReferenceDto<V, W, X> referenceDto, UUID identifiant) throws JaoParseException, JaoExecutionException {
        if (referenceDto.getClasseFournisseur() == null) {
            throw new JaoParseException(MessageFormat.format("Référence vers un objet d''identifiant {0} sans classe pour le fournisseur.", identifiant));
        } else if (referenceDto.getChemin() == null) {
            throw new JaoParseException(MessageFormat.format("Référence vers un objet d''identifiant {0} sans chemin.", identifiant));
        }
        Class<X> classeFournisseur = referenceDto.getClasseFournisseur();
        try {
            classeFournisseur.getConstructor().newInstance().charger(referenceDto.getChemin());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new JaoExecutionException(MessageFormat.format("Impossible d''instancier le fournisseur {1} de la référence {0}.", identifiant, classeFournisseur), e);
        }
    }

    public <V extends Dto, W extends Jo, X extends Jao<V, W>> List<O> chargerParReferences(Collection<ReferenceDto<V, W, X>> referencesDtos) throws JaoParseException, JaoExecutionException {
        List<O> objets = new ArrayList<>(referencesDtos.size());
        for (ReferenceDto<V, W, X> dto : referencesDtos) {
            objets.add(chargerParReference(dto));
        }
        return objets;
    }

    @FunctionalInterface
    public interface Enrichissement<O extends Jo> {
        void faire(O objet) throws JaoExecutionException, JaoParseException, VerificationException;
    }
}
