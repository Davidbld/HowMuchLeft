package com.example.calculfeuilleheure.strategy;

/**
 * Interface pour les stratégies de validation.
 * Définit une méthode pour valider une entrée utilisateur.
 */
public interface ValidationStrategy {

    /**
     * Valide l'entrée donnée.
     * @param input l'entrée à valider
     * @return true si l'entrée est valide, false sinon
     */
    boolean validate(String input);
}
