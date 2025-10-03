package com.example.calculfeuilleheure.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

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
}
