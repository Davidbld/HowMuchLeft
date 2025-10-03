package com.example.calculfeuilleheure.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests spécifiques pour le calcul des heures de nuit
 */
public class TimesheetEntryNightShiftTest {

    @Test
    public void testNightShiftCalculation() {
        // Test travail de nuit : 22h à 6h = 8h
        TimesheetEntry entry = new TimesheetEntry(LocalDate.now(), 22.0, 6.0);
        assertEquals(8.0, entry.getWorkedHours(), 0.001,
            "Le calcul du travail de nuit devrait donner 8h (22h à 6h)");
    }

    @Test
    public void testNightShiftWithMinutes() {
        // Test travail de nuit avec minutes : 23h30 à 7h45 = 8h15
        TimesheetEntry entry = new TimesheetEntry(LocalDate.now(), 23.5, 7.75);
        assertEquals(8.25, entry.getWorkedHours(), 0.001,
            "Le calcul du travail de nuit avec minutes devrait donner 8h15 (23h30 à 7h45)");
    }

    @Test
    public void testNightShiftEdgeCase() {
        // Test cas limite : 23h59 à 0h01 = 0h02
        TimesheetEntry entry = new TimesheetEntry(LocalDate.now(), 23.983, 0.017);
        assertEquals(0.034, entry.getWorkedHours(), 0.001,
            "Le calcul du travail de nuit cas limite devrait fonctionner");
    }

    @Test
    public void testNormalShiftStillWorks() {
        // Vérifier que les cas normaux continuent de fonctionner
        TimesheetEntry entry = new TimesheetEntry(LocalDate.now(), 8.5, 17.5);
        assertEquals(9.0, entry.getWorkedHours(), 0.001,
            "Les cas normaux devraient continuer de fonctionner");
    }
}
