package org.mgd.gmel.javafx.service;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.objet.*;
import org.mgd.gmel.javafx.GmelSingletons;
import org.mgd.gmel.javafx.connexions.exception.ConnexionsException;
import org.mgd.gmel.javafx.service.exception.ServiceException;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ImprimerieService extends Service {
    private static final ImprimerieService instance;

    static {
        try {
            instance = new ImprimerieService();
        } catch (ConnexionsException | ConnexionException | JaoExecutionException | IOException | JaoParseException e) {
            throw new ServiceException(e);
        }
    }

    private final Map<Produit, Long> restes = new HashMap<>();
    private final Map<Produit, Map<Mesure, ProduitQuantifier>> produitsQuantifierParProduitEtMesure = new HashMap<>();
    private final EpicerieService epicerieService = EpicerieService.getInstance();
    private final SimpleObjectProperty<Agenda> agenda = new SimpleObjectProperty<>(this, "agenda");
    private final SimpleListProperty<Menu> menus = new SimpleListProperty<>(this, "menus");
    private final SimpleObjectProperty<Inventaire> inventaire = new SimpleObjectProperty<>(this, "inventaire");
    private final SimpleListProperty<ProduitQuantifier> produitsQuantifier = new SimpleListProperty<>(this, "produitsQuantifier");

    private ImprimerieService() throws ConnexionsException, ConnexionException, JaoExecutionException, IOException, JaoParseException {
        menus.bind(new ListeLiaison<>(agenda, Agenda::getMenus));
        produitsQuantifier.bind(new ListeLiaison<>(inventaire, Inventaire::getProduitsQuantifier));

        GmelSingletons.connexion().ouvrir();
        agenda.set(GmelSingletons.connexion().getJabm().agenda());
        inventaire.set(GmelSingletons.connexion().getJabm().inventaire());

        menus.addListener(propager(agenda, Agenda::getMenus));
        produitsQuantifier.addListener(propager(inventaire, Inventaire::getProduitsQuantifier));
    }

    public static ImprimerieService getInstance() {
        return instance;
    }

    public void remplirAgenda(LocalDate debut, LocalDate fin) {
        menus.setAll(debut.datesUntil(fin.plusDays(1), Period.ofDays(7)).map(jour -> {
            try {
                return GmelSingletons.connexion().getJabm().menu(jour);
            } catch (JaoParseException | JaoExecutionException | IOException | ConnexionsException e) {
                throw new ServiceException(e);
            }
        }).toList());
    }

    public void remplirInventaire() {
        restes.clear();
        produitsQuantifierParProduitEtMesure.clear();
        menus.forEach(menu ->
                menu.getFormules().forEach(formule ->
                        formule.getRecette().getProduitsQuantifier().forEach(produitQuantifier -> {
                            Produit produit = produitQuantifier.getProduit();
                            Mesure mesure = produitQuantifier.getQuantite().getMesure();
                            restes.computeIfAbsent(produit, k -> 0L);
                            produitsQuantifierParProduitEtMesure.computeIfAbsent(produit, k -> new EnumMap<>(Mesure.class))
                                    .computeIfAbsent(mesure, k -> {
                                        try {
                                            return epicerieService.creerNouveauProduitQuantifier(produit, calcul(produit, mesure), mesure);
                                        } catch (JaoExecutionException | JaoParseException e) {
                                            throw new ServiceException(e);
                                        }
                                    });
                        })));
        produitsQuantifier.setAll(produitsQuantifierParProduitEtMesure.values()
                .stream()
                .flatMap(produitsQuantifierParMesure -> produitsQuantifierParMesure.values().stream())
                .toList());
    }

    /**
     * Formule de calcul de la valeur totale d'une quantité pour un produit et une mesure donnée
     *
     * @param produit Le produit
     * @param mesure  La mesure
     * @return La valeur totale
     */
    private long calcul(Produit produit, Mesure mesure) {
        return menus.stream().reduce(0L, (resultat, menu) ->
                resultat + menu.getFormules().stream().reduce(0L, (resultatMenu, formule) -> {
                    long resultatFormule = restes.get(produit) + formule.getRecette()
                            .getProduitsQuantifier()
                            .stream()
                            .filter(produitQuantifier -> produitQuantifier.getProduit().idem(produit) && produitQuantifier.getQuantite().getMesure().equals(mesure))
                            .mapToLong(produitQuantifier -> produitQuantifier.getQuantite().getValeur())
                            .sum() * formule.getNombreConvives() * formule.getPeriode().getTaille();
                    restes.put(produit, resultatFormule % formule.getRecette().getNombrePersonnes());
                    return resultatMenu + resultatFormule / formule.getRecette().getNombrePersonnes();
                }, Long::sum), Long::sum) + (restes.get(produit) > 0 ? 1 : 0);
    }

    public SimpleObjectProperty<Agenda> agendaProperty() {
        return agenda;
    }

    public SimpleListProperty<Menu> menusProperty() {
        return menus;
    }

    public SimpleObjectProperty<Inventaire> inventaireProperty() {
        return inventaire;
    }

    public SimpleListProperty<ProduitQuantifier> produitsQuantifierProperty() {
        return produitsQuantifier;
    }
}
