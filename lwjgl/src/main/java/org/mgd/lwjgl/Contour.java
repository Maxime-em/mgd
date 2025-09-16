package org.mgd.lwjgl;

import org.mgd.commun.Matrice;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Contour {
    private static final float TOLERANCE = 1.0e-6f;
    private final List<Float[][]> plans;

    public Contour(Matrice<Float> points) {
        Objects.requireNonNull(points, "Les points du contour ne peuvent pas être null.");
        if (points.nombreLignes() < 3 || points.nombreColonnes() < 3 || points.nombreColonnes() % 3 != 0) {
            throw new IllegalArgumentException("Les points doivent être regroupés en triplets 3D.");
        }
        this.plans = new LinkedList<>();
        Float[][] plan = null;
        for (int index = 0; index < points.nombreColonnes(); index++) {
            int rang = index % 3;
            if (rang == 0) {
                plan = new Float[4][3];
                this.plans.add(plan);
            }
            System.arraycopy(points.colonne(index), 0, plan[rang], 0, 3);
            if (rang == 2) {
                // Calcule d'une normale au plan qui est défini par le trois points plan[0], plan[1] et plan[2]
                plan[3] = new Float[]{
                        // vy      * (wz         - uz)         - uy         * wz         + uz         * wy         + vz         * (uy         - wy)
                        plan[1][1] * (plan[2][2] - plan[0][2]) - plan[0][1] * plan[2][2] + plan[0][2] * plan[2][1] + plan[1][2] * (plan[0][1] - plan[2][1]),
                        // ux      * wz         + vx         * (uz         - wz)         + vz         * (wx         - ux)         - uz         * wx
                        plan[0][0] * plan[2][2] + plan[1][0] * (plan[0][2] - plan[2][2]) + plan[1][2] * (plan[2][0] - plan[0][0]) - plan[0][2] * plan[2][0],
                        // vx      * (wy         - uy)         - ux         * wy         + uy         * wx         + vy         * (ux         - wx)
                        plan[1][0] * (plan[2][1] - plan[0][1]) - plan[0][0] * plan[2][1] + plan[0][1] * plan[2][0] + plan[1][1] * (plan[0][0] - plan[2][0])
                };
            }
        }
    }

    /**
     * On cherche à savoir si la droite de vecteur directeur d = (dirx, diry, -1) intersecte au moins un plan (u, v, w)
     * à l'intérieur du parallélogramme formé par les points u, v, w.
     * Le principe est de calculer les intersections d'une droite de vecteur directeur d = (dirx, diry, -1) et tous les
     * plans définis par le contour.
     * <p>
     * On considère un plan définit par trois points u, v, w pris dans le contour.
     * On cherche un vecteur normal n = (nx, ny, nz) qui vérifie
     * <ul>
     *     <li>{@literal <}n, v - u> = 0</li>
     *     <li>{@literal <}n, w - u> = 0</li>
     * </ul>
     * Ce vecteur normal est donné par
     * <ul>
     *    <li>nx = vy * (wz - uz) - uy * wz + uz * wy + vz * (uy - wy)</li>
     *    <li>ny = ux * wz + vx * (uz - wz) + vz * (wx - ux) - uz * wx</li>
     *    <li>nz = vx * (wy - uy) - ux * wy + uy * wx + vy * (ux - wx)</li>
     * </ul>
     * L'équation du plan est donnée par
     *  nz*(z - uy) + ny*(y - uy) + nx*(x - ux) = 0
     * Le point d'intersection entre la droite et le plan est
     * <ul>
     *     <li>px = dirx * alpha</li>
     *     <li>py = diry * alpha</li>
     *     <li>pz = -alpha</li>
     * </ul>
     * où alpha = ((nz + ny) * uy + nx * ux)) / (-nz + diry * ny + dirx * nx).
     * <p>
     * Le survole est vrai au moins un point d'intersection pour un plan (u, v, x) est intérieur au parallélogramme
     * formé par les points u, v, w. C'est-à-dire si
     * <ul>
     *     <li>0 <= {@literal <}p - u, v - u> <= ||v - u||^2</li>
     *     <li>0 <= {@literal <}p - u, w - u> <= ||w - u||^2</li>
     *
     * @param direction Coordonnées en x et y du vecteur directeur de la droite.
     * @return Vrai si la droite de vecteur directeur d = (dirx, diry, -1) intersecte au moins un plan (u, v, w) à
     * l'intérieur du parallélogramme formé par les points u, v, w.
     * TODO Si test de profondeur conserver la coordonnées en z du point d'intersection (dirx * alpha, diry * alpha, -alpha).
     * Puis associer un poids en fonction de cette coordonnées au survole de la forme.
     */
    public boolean intersecter(float... direction) {
        if (direction == null || direction.length < 2) {
            throw new IllegalArgumentException("La direction doit contenir au moins deux coordonnées.");
        }
        return plans.stream().anyMatch(plan -> {
            //    normeCarreUV = (vz         - uz)^2                                   + (vy         -uy)^2                                    + (vx         - ux)^2
            float normeCarreUV = (plan[1][2] - plan[0][2]) * (plan[1][2] - plan[0][2]) + (plan[1][1] - plan[0][1]) * (plan[1][1] - plan[0][1]) + (plan[1][0] - plan[0][0]) * (plan[1][0] - plan[0][0]);
            if (normeCarreUV <= TOLERANCE * TOLERANCE) {
                return false;
            }
            //    normeCarreUW = (wz         - uz)^2                                   + (wy         -uy)^2                                    + (wx         - ux)^2
            float normeCarreUW = (plan[2][2] - plan[0][2]) * (plan[2][2] - plan[0][2]) + (plan[2][1] - plan[0][1]) * (plan[2][1] - plan[0][1]) + (plan[2][0] - plan[0][0]) * (plan[2][0] - plan[0][0]);
            if (normeCarreUW <= TOLERANCE * TOLERANCE) {
                return false;
            }
            //    denominateur = -nz         + diry         * ny        + dirx         * nx
            float denominateur = -plan[3][2] + direction[1] * plan[3][1] + direction[0] * plan[3][0];
            float echelleDenominateur = Math.abs(plan[3][2]) + Math.abs(direction[1] * plan[3][1]) + Math.abs(direction[0] * plan[3][0]);
            if (Math.abs(denominateur) <= TOLERANCE * echelleDenominateur) {
                return false;
            }
            //    alpha = (nz         * uz         + ny         * uy         + nx         * ux)         / denominateur
            float alpha = (plan[3][2] * plan[0][2] + plan[3][1] * plan[0][1] + plan[3][0] * plan[0][0]) / denominateur;
            float[] scalaires = {
                    // (-alpha - uz)      * (vz         - uz)         + (diry         * alpha - uy)         * (vy         - uy        ) + (dirx         * alpha - ux)         * (vx         - ux)
                    (-alpha - plan[0][2]) * (plan[1][2] - plan[0][2]) + (direction[1] * alpha - plan[0][1]) * (plan[1][1] - plan[0][1]) + (direction[0] * alpha - plan[0][0]) * (plan[1][0] - plan[0][0]),
                    // (-alpha - uz)      * (wz         - uz)         + (diry         * alpha - uy)         * (wy         - uy)         + (dirx         * alpha - ux)         * (wx         - ux)
                    (-alpha - plan[0][2]) * (plan[2][2] - plan[0][2]) + (direction[1] * alpha - plan[0][1]) * (plan[2][1] - plan[0][1]) + (direction[0] * alpha - plan[0][0]) * (plan[2][0] - plan[0][0])
            };
            return (0 <= scalaires[0] && scalaires[0] <= normeCarreUV) && (0 <= scalaires[1] && scalaires[1] <= normeCarreUW);
        });
    }

    public Matrice<Float> projeterPlanEcran() {
        return Matrice.identitef(4, plans.size() * 3).insererParLignes((ligne, colonne, _) -> {
            if (ligne < 2) {
                Float[] vecteur = plans.get(colonne / 3)[colonne % 3];
                return -vecteur[ligne] / vecteur[2];
            } else {
                return -1f;
            }
        });
    }
}
