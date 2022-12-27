package org.mgd.jab.persistence;

import org.mgd.jab.JabCreation;
import org.mgd.jab.JabSingletons;
import org.mgd.jab.JabTable;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.persistence.exception.JaoRuntimeException;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Classe de base des objets permettant de manipuler les données provenant d'un système de fichiers JSON.
 * Chaque classe fille sera associée à une classe métier {@link O} et une classe de transfert {@link D}
 * et sera instanciée pour lire et écrire les données du système de fichiers JSON.
 *
 * @param <O> classe métier
 * @param <D> classe de transfert de données
 * @author Maxime
 */
public abstract class Jao<D extends Dto, O extends Jo<D>> {
    private final Class<D> classeDto;
    private final Class<O> classeJo;
    private final JabTable<D, O> table;
    private final JabCreation creation;

    protected Jao(Class<D> classeDto, Class<O> classeJo) {
        this.classeDto = classeDto;
        this.classeJo = classeJo;
        this.table = JabSingletons.table(classeJo);
        this.creation = JabSingletons.creation();
    }

    protected abstract D to(O objet);

    protected abstract void copier(O source, O cible) throws JaoExecutionException;

    private D from(Path fichier) throws JaoParseException {
        try {
            return JabSingletons.sauvegarde().gsonSauvegarde.fromJson(Files.newBufferedReader(fichier), classeDto);
        } catch (IOException e) {
            throw new JaoParseException(MessageFormat.format("Impossible de charger le fichier {0}", fichier), e);
        }
    }

    public JabTable<D, O> table() {
        return table;
    }

    public D decharger(O objet) {
        if (objet == null) {
            return null;
        }
        D dto = to(objet);
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

    public <V extends Dto, W extends Jo<V>> Map<D, V> decharger(Jao<V, W> jao, Map<O, W> objets) {
        return objets.entrySet()
                .stream()
                .map(element -> new AbstractMap.SimpleEntry<>(decharger(element.getKey()), jao.decharger(element.getValue())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    public O dupliquer(O source) throws JaoExecutionException {
        O objet = nouveau();
        copier(source, objet);
        return objet;
    }

    public List<O> dupliquer(Collection<O> sources) throws JaoExecutionException {
        List<O> nouveaux = new ArrayList<>(sources.size());
        for (O source : sources) {
            nouveaux.add(dupliquer(source));
        }
        return nouveaux;
    }

    public <K> Map<K, O> dupliquer(Map<K, O> sources) throws JaoExecutionException {
        Map<K, O> nouveaux = HashMap.newHashMap(sources.size());
        for (Map.Entry<K, O> element : sources.entrySet()) {
            nouveaux.put(element.getKey(), dupliquer(element.getValue()));
        }
        return nouveaux;
    }

    public <V extends Dto, W extends Jo<V>> Map<O, W> dupliquer(Jao<V, W> jao, Map<O, W> sources) throws JaoExecutionException {
        Map<O, W> nouveaux = HashMap.newHashMap(sources.size());
        for (Map.Entry<O, W> element : sources.entrySet()) {
            nouveaux.put(dupliquer(element.getKey()), jao.dupliquer(element.getValue()));
        }
        return nouveaux;
    }

    public O nouveau() throws JaoExecutionException {
        return nouveau(UUID.randomUUID(), nouveau -> {
        });
    }

    public O nouveau(Consumer<O> enrichir) throws JaoExecutionException {
        return nouveau(UUID.randomUUID(), enrichir);
    }

    public O nouveau(BiConsumer<O, Jo<?>[]> enrichir, Jo<?>... autresJos) throws JaoExecutionException {
        return nouveau(UUID.randomUUID(), jo -> enrichir.accept(jo, autresJos));
    }

    private O nouveau(UUID identifiant, Consumer<O> enrichir) throws JaoExecutionException {
        try {
            O objet = classeJo.getConstructor().newInstance();
            objet.setIdentifiant(identifiant);
            enrichir.accept(objet);
            objet.setDetache(false);

            table.referencer(objet);

            return objet;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new JaoExecutionException(e);
        }
    }

    public O charger(Path fichier) throws JaoParseException, JaoExecutionException {
        if (creation.getGermes().containsKey(fichier)) {
            return table.selectionner(creation.getGermes().get(fichier));
        } else {
            D dto = from(fichier);
            O objet = charger(dto, null);

            creation.ajouter(fichier, objet.getIdentifiant());

            if (dto.getIdentifiant() == null) {
                objet.sauvegarder();
            }

            return objet;
        }
    }

    public <P extends Dto> O charger(D dto, Jo<P> parent) throws JaoParseException, JaoExecutionException {
        if (dto == null) {
            throw new JaoParseException("Objet inconnu");
        } else {
            try {
                UUID identifiant = dto.getIdentifiant() != null ? UUID.fromString(dto.getIdentifiant()) : UUID.randomUUID();
                return construire(dto, parent, identifiant);
            } catch (JaoRuntimeException e) {
                throw new JaoParseException(e);
            }
        }
    }

    private <P extends Dto> O construire(D dto, Jo<P> parent, UUID identifiant) throws JaoExecutionException {
        if (table.existe(identifiant)) {
            return table.selectionner(identifiant);
        } else {
            O objet = nouveau(identifiant, nouveau -> {
                try {
                    nouveau.depuis(dto);
                } catch (JaoExecutionException | JaoParseException | VerificationException e) {
                    throw new JaoRuntimeException(e);
                }
            });

            if (parent != null) {
                objet.ajouterParent(parent);
            }

            return objet;
        }
    }

    public <P extends Dto> List<O> charger(Collection<D> dtos, Jo<P> parent) throws JaoParseException, JaoExecutionException {
        List<O> objets = new ArrayList<>(dtos.size());
        for (D dto : dtos) {
            objets.add(charger(dto, parent));
        }
        return objets;
    }

    public <K, P extends Dto> Map<K, O> charger(Map<K, D> dtos, Jo<P> parent) throws JaoParseException {
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

    public <V extends Dto, W extends Jo<V>, P extends Dto> Map<O, W> charger(Jao<V, W> jao, Map<D, V> dtos, Jo<P> parent) throws JaoParseException {
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
}
