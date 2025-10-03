package com.example.calculfeuilleheure.model;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;

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
     * Calcule le total des heures travaillées pour une date donnée.
     * Méthode statique utilitaire pour calculer la somme des heures travaillées
     * pour toutes les entrées d'une même date.
     * @param entries liste de toutes les entrées
     * @param date date pour laquelle calculer le total
     * @return total des heures travaillées pour cette date
     */
    public static double calculateTotalHoursForDate(List<TimesheetEntry> entries, LocalDate date) {
        return entries.stream()
                .filter(entry -> entry.getDate().equals(date))
                .mapToDouble(TimesheetEntry::getWorkedHours)
                .sum();
    }

    /**
     * Vérifie si cette entrée fait partie d'un groupe (même date avec plusieurs entrées).
     * @param entries liste de toutes les entrées
     * @return true si cette entrée fait partie d'un groupe de plusieurs entrées pour la même date
     */
    public boolean isPartOfGroup(List<TimesheetEntry> entries) {
        long countForDate = entries.stream()
                .map(TimesheetEntry::getDate)
                .filter(d -> d.equals(this.date))
                .count();
        return countForDate > 1;
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
     * Parse une chaîne au format "xxhyy" en double (heures + minutes/60).
     * Exemple : "8h30" -> 8.5
     * @param hoursText chaîne au format "xxhyy"
     * @return nombre d'heures en double
     * @throws NumberFormatException si le format est invalide
     */
    public static double parseHours(String hoursText) throws NumberFormatException {
        String[] parts = hoursText.split("h");
        if (parts.length != 2) {
            throw new NumberFormatException("Format invalide : doit être xxhyy");
        }
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        if (minutes < 0 || minutes >= 60) {
            throw new NumberFormatException("Minutes invalides : doivent être entre 00 et 59");
        }
        return hours + minutes / 60.0;
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

    /**
     * Calcule le total des heures travaillées pour une semaine donnée.
     * @param entries liste de toutes les entrées
     * @param weekYear année de la semaine
     * @param weekNumber numéro de la semaine
     * @return total des heures travaillées pour cette semaine
     */
    public static double calculateTotalHoursForWeek(List<TimesheetEntry> entries, int weekYear, int weekNumber) {
        return entries.stream()
                .filter(entry -> entry.getWeekYear() == weekYear && entry.getWeekNumber() == weekNumber)
                .mapToDouble(TimesheetEntry::getWorkedHours)
                .sum();
    }

    /**
     * Vérifie si cette entrée est la dernière de sa semaine (date la plus récente).
     * @param entries liste de toutes les entrées
     * @return true si cette entrée est la dernière de sa semaine
     */
    public boolean isLastInWeek(List<TimesheetEntry> entries) {
        int myWeekYear = getWeekYear();
        int myWeekNumber = getWeekNumber();
        LocalDate maxDateInWeek = entries.stream()
                .filter(entry -> entry.getWeekYear() == myWeekYear && entry.getWeekNumber() == myWeekNumber)
                .map(TimesheetEntry::getDate)
                .max(LocalDate::compareTo)
                .orElse(null);
        return maxDateInWeek != null && date.equals(maxDateInWeek);
    }
}
