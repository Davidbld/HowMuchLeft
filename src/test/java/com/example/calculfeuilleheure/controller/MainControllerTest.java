package com.example.calculfeuilleheure.controller;

import com.example.calculfeuilleheure.model.TimesheetEntry;
import com.example.calculfeuilleheure.strategy.HourFormatValidationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour MainController.
 * Utilise des mocks pour éviter les dépendances JavaFX.
 */
public class MainControllerTest {

    private MainController controller;

    @Mock
    private HourFormatValidationStrategy mockValidationStrategy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new MainController();
        // Note: Dans un vrai test, il faudrait injecter le mock via un setter ou constructeur
        // Pour simplifier, nous testons les méthodes statiques et utilitaires
    }

    @Test
    public void testValidationStrategyUsage() {
        // Test que la stratégie de validation est correctement instanciée
        HourFormatValidationStrategy strategy = new HourFormatValidationStrategy();

        // Formats valides
        assertTrue(strategy.validate("08h30"));
        assertTrue(strategy.validate("830"));
        assertTrue(strategy.validate("9h00"));

        // Formats invalides
        assertFalse(strategy.validate("abc"));
        assertFalse(strategy.validate(""));
        assertFalse(strategy.validate(null));
    }

    @Test
    public void testTimesheetEntryCreation() {
        LocalDate date = LocalDate.of(2023, 10, 1);
        TimesheetEntry entry = new TimesheetEntry(date, 8.5, 17.5);

        assertEquals(date, entry.getDate());
        assertEquals(8.5, entry.getHours(), 0.001);
        assertEquals(17.5, entry.getEndHours(), 0.001);
        assertEquals(9.0, entry.getWorkedHours(), 0.001);
    }

    @Test
    public void testTotalCalculations() {
        LocalDate date1 = LocalDate.of(2023, 10, 1);
        LocalDate date2 = LocalDate.of(2023, 10, 2);

        List<TimesheetEntry> entries = List.of(
            new TimesheetEntry(date1, 8.0, 12.0), // 4h
            new TimesheetEntry(date1, 13.0, 17.0), // 4h
            new TimesheetEntry(date2, 9.0, 17.0)  // 8h
        );

        // Test calcul total journalier (méthode supprimée, test supprimé)
    }

    @Test
    public void testNightShiftCalculation() {
        // Test travail de nuit
        TimesheetEntry nightEntry = new TimesheetEntry(LocalDate.now(), 22.0, 6.0);
        assertEquals(8.0, nightEntry.getWorkedHours(), 0.001);

        // Test journée normale
        TimesheetEntry dayEntry = new TimesheetEntry(LocalDate.now(), 8.0, 17.0);
        assertEquals(9.0, dayEntry.getWorkedHours(), 0.001);
    }

    @Test
    public void testParseHoursIntegration() {
        // Test intégration avec parseHours
        try {
            double hours1 = TimesheetEntry.parseHours("08h30");
            assertEquals(8.5, hours1, 0.001);

            double hours2 = TimesheetEntry.parseHours("830");
            assertEquals(8.5, hours2, 0.001);

            // Test format invalide
            assertThrows(NumberFormatException.class, () -> TimesheetEntry.parseHours("abc"));
        } catch (Exception e) {
            fail("Erreur inattendue lors du parsing des heures: " + e.getMessage());
        }
    }
}
