package com.example.calculfeuilleheure.strategy;

/**
 * Stratégie de validation pour le format des heures au format "xxhyy" (ex: 08h30) ou "xxyy" (ex: 830 pour 8h30).
 * Utilise une expression régulière pour vérifier le format.
 */
public class HourFormatValidationStrategy implements ValidationStrategy {

    // Expression régulière pour valider le format xxhyy (ex: 08h30) ou xxyy (ex: 830)
    private static final String HOUR_FORMAT_REGEX = "^(\\d{1,2}h\\d{2}|\\d{3,4})$";

    @Override
    public boolean validate(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return input.matches(HOUR_FORMAT_REGEX);
    }
}
