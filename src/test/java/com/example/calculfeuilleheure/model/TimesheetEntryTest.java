package com.example.calculfeuilleheure.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TimesheetEntryTest {

    @Test
    public void testGetSetEndHours() {
        TimesheetEntry entry = new TimesheetEntry(LocalDate.now(), 8.5, 17.5);
        assertEquals(17.5, entry.getEndHours(), 0.001);

        entry.setEndHours(18.25);
        assertEquals(18.25, entry.getEndHours(), 0.001);
    }

    @Test
    public void testGetEndHoursFormatted() {
        TimesheetEntry entry = new TimesheetEntry(LocalDate.now(), 8.5, 17.5);
        assertEquals("17h30", entry.getEndHoursFormatted());

        entry.setEndHours(9.0);
        assertEquals("9h00", entry.getEndHoursFormatted());

        entry.setEndHours(14.75);
        assertEquals("14h45", entry.getEndHoursFormatted());
    }

    @Test
    public void testParseHoursWithNewFormat() {
        // Test format sans 'h' : "xxyy"
        assertEquals(8.5, TimesheetEntry.parseHours("830"), 0.001, "830 devrait donner 8.5");
        assertEquals(9.0, TimesheetEntry.parseHours("900"), 0.001, "900 devrait donner 9.0");
        assertEquals(14.75, TimesheetEntry.parseHours("1445"), 0.001, "1445 devrait donner 14.75");
        assertEquals(0.0, TimesheetEntry.parseHours("000"), 0.001, "000 devrait donner 0.0");
        assertEquals(23.983, TimesheetEntry.parseHours("2359"), 0.001, "2359 devrait donner 23.983");
    }

    @Test
    public void testParseHoursInvalidFormats() {
        // Test formats invalides
        assertThrows(NumberFormatException.class, () -> TimesheetEntry.parseHours("abc"));
        assertThrows(NumberFormatException.class, () -> TimesheetEntry.parseHours("8h"));
        assertThrows(NumberFormatException.class, () -> TimesheetEntry.parseHours("h30"));
        // Note: "25h00" est valide selon parseHours (heures peuvent être > 23)
        assertThrows(NumberFormatException.class, () -> TimesheetEntry.parseHours("12345"));
        assertThrows(NumberFormatException.class, () -> TimesheetEntry.parseHours("12"));
        assertThrows(NumberFormatException.class, () -> TimesheetEntry.parseHours("123456"));
    }









    @Test
    public void testFormatHoursEdgeCases() {
        assertEquals("0h00", TimesheetEntry.formatHours(0.0));
        assertEquals("23h59", TimesheetEntry.formatHours(23.983));
        assertEquals("1h30", TimesheetEntry.formatHours(1.5));
        assertEquals("0h01", TimesheetEntry.formatHours(0.017));
    }
}
