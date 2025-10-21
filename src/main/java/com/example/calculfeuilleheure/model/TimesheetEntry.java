package com.example.calculfeuilleheure.model;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

/**
 * Classe représentant une entrée de feuille d'heure.
 * Contient la date, l'heure de début et l'heure de fin.
 */
public class TimesheetEntry {
    private static final WeekFields weekFields = WeekFields.ISO;
    private LocalDate date;
    private double hours;
    private double endHours;

    public TimesheetEntry(LocalDate date, double hours, double endHours) {
        this.date = date;
        this.hours = hours;
        this.endHours = endHours;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getEndHours() {
        return endHours;
    }

    public void setEndHours(double endHours) {
        this.endHours = endHours;
    }

    /**
     * Formate un nombre d'heures en double au format "xxhyy".
     * Par exemple, 3.5 devient "3h30".
     * @param hours heures à formater
     * @return chaîne au format "xxhyy"
     */
    public static String formatHours(double hours) {
        int h = (int) hours;
        int m = (int) Math.round((hours - h) * 60);
        return String.format("%dh%02d", h, m);
    }

    /**
     * Convertit le nombre d'heures en double au format "xxhyy".
     * Par exemple, 3.5 devient "3h30".
     * @return chaîne au format "xxhyy"
     */
    public String getHoursFormatted() {
        return formatHours(hours);
    }

    /**
     * Convertit l'heure de fin en double au format "xxhyy".
     * Par exemple, 17.5 devient "17h30".
     * @return chaîne au format "xxhyy"
     */
    public String getEndHoursFormatted() {
        return formatHours(endHours);
    }

    /**
     * Calcule la durée travaillée entre l'heure de début et l'heure de fin.
     * Gère le cas du travail de nuit où l'heure de fin est plus petite que l'heure de début.
     * @return durée travaillée en double (heures)
     */
    public double getWorkedHours() {
        // Cas du travail de nuit : si l'heure de fin est plus petite que l'heure de début,
        // cela signifie qu'on a travaillé après minuit (ex: 22h à 6h)
        if (endHours < hours) {
            // Ajouter 24 heures à l'heure de fin pour calculer correctement la durée
            return (endHours + 24.0) - hours;
        } else {
            // Cas normal : soustraction directe
            return endHours - hours;
        }
    }

    /**
     * Convertit la durée travaillée en format "xxhyy".
     * @return durée travaillée formatée en chaîne
     */
    public String getWorkedHoursFormatted() {
        return formatHours(getWorkedHours());
    }



    /**
     * Formate le total des heures en format "xxhyy".
     * @param totalHours total des heures à formater
     * @return chaîne au format "xxhyy"
     */
    public static String formatTotalHours(double totalHours) {
        return formatHours(totalHours);
    }

    /**
     * Parse une chaîne au format "xxhyy" (ex: "8h30") ou "xxyy" (ex: "830" pour 8h30) en double (heures + minutes/60).
     * Exemple : "8h30" -> 8.5, "830" -> 8.5
     * @param hoursText chaîne au format "xxhyy" ou "xxyy"
     * @return nombre d'heures en double
     * @throws NumberFormatException si le format est invalide
     */
    public static double parseHours(String hoursText) throws NumberFormatException {
        if (hoursText.contains("h")) {
            // Format avec 'h' : "xxhyy"
            String[] parts = hoursText.split("h");
            if (parts.length != 2) {
                throw new NumberFormatException("Format invalide : doit être xxhyy ou xxyy");
            }
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            if (minutes < 0 || minutes >= 60) {
                throw new NumberFormatException("Minutes invalides : doivent être entre 00 et 59");
            }
            return hours + minutes / 60.0;
        } else {
            // Format sans 'h' : "xxyy" (ex: "830" pour 8h30)
            if (hoursText.length() < 3 || hoursText.length() > 4) {
                throw new NumberFormatException("Format invalide : doit être xxhyy ou xxyy");
            }
            int totalMinutes = Integer.parseInt(hoursText);
            int hours = totalMinutes / 100;
            int minutes = totalMinutes % 100;
            if (minutes < 0 || minutes >= 60) {
                throw new NumberFormatException("Minutes invalides : doivent être entre 00 et 59");
            }
            return hours + minutes / 60.0;
        }
    }

    /**
     * Obtient le numéro de semaine ISO pour cette entrée.
     * @return numéro de semaine (1-53)
     */
    public int getWeekNumber() {
        return date.get(weekFields.weekOfYear());
    }

    /**
     * Obtient l'année basée sur la semaine ISO pour cette entrée.
     * @return année de la semaine
     */
    public int getWeekYear() {
        return date.get(weekFields.weekBasedYear());
    }


}
