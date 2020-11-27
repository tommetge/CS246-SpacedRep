package com.cs246.team1.spacedrepetition;

import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class ReminderTest {
    @Test
    public void testIdentifier() {
        Reminder reminder = new Reminder();
        // Should be null to begin with
        assertNull(reminder.getIdentifier());
        // Test setting it
        String identifier = UUID.randomUUID().toString();
        reminder.setIdentifier(identifier);
        assertNotNull(reminder.getIdentifier());
        assertEquals(reminder.getIdentifier(), identifier);
        // Test changing it
        identifier = UUID.randomUUID().toString();
        reminder.setIdentifier(identifier);
        assertEquals(reminder.getIdentifier(), identifier);
    }

    @Test
    public void testSummary() {
        Reminder reminder = new Reminder();
        // Should be null to begin with
        assertNull(reminder.getSummary());
        // Test setting it
        String summary = UUID.randomUUID().toString();
        reminder.setSummary(summary);
        assertNotNull(reminder.getSummary());
        assertEquals(reminder.getSummary(), summary);
        // Test changing it
        summary = UUID.randomUUID().toString();
        reminder.setSummary(summary);
        assertEquals(reminder.getSummary(), summary);
    }

    @Test
    public void testContent() {
        Reminder reminder = new Reminder();
        // Should be null to begin with
        assertNull(reminder.getContent());
        // Test setting it
        String content = UUID.randomUUID().toString();
        reminder.setContent(content);
        assertNotNull(reminder.getContent());
        assertEquals(reminder.getContent(), content);
        // Test changing it
        content = UUID.randomUUID().toString();
        reminder.setContent(content);
        assertEquals(reminder.getContent(), content);
    }

    @Test
    public void testDaysToLive() {
        Reminder reminder = new Reminder();
        // Should be 30 to begin with
        assertEquals(reminder.getDaysToLive(), new Integer(30));
        // Test setting it
        reminder.setDaysToLive(10);
        assertEquals(reminder.getDaysToLive(), new Integer(10));
        // Test changing it
        reminder.setDaysToLive(100);
        assertEquals(reminder.getDaysToLive(), new Integer(100));
        // Test illegal values
        reminder.setDaysToLive(-1);
        assertEquals(reminder.getDaysToLive(), new Integer(100));
        reminder.setDaysToLive(0);
        assertEquals(reminder.getDaysToLive(), new Integer(100));
    }

    @Test
    public void testLastNotifiedAt() {
        Reminder reminder = new Reminder();
        // Should be null to begin with
        assertNull(reminder.getLastNotifiedAt());
        // Test setting it
        Date date = new Date();
        reminder.setLastNotifiedAt(date);
        assertNotNull(reminder.getLastNotifiedAt());
        assertEquals(reminder.getLastNotifiedAt(), date);
        // Test changing it
        date = new Date(1234);
        reminder.setLastNotifiedAt(date);
        assertEquals(reminder.getLastNotifiedAt(), date);
    }

    @Test
    public void testCurrentPeriod() {
        Reminder reminder = new Reminder();
        // Should be 0 to begin with
        assertEquals(reminder.getCurrentPeriod(), new Integer(0));
        // Test setting it
        reminder.setCurrentPeriod(10);
        assertEquals(reminder.getCurrentPeriod(), new Integer(10));
        // Test changing it
        reminder.setCurrentPeriod(100);
        assertEquals(reminder.getCurrentPeriod(), new Integer(100));
        // Test illegal values
        reminder.setCurrentPeriod(-1);
        assertEquals(reminder.getCurrentPeriod(), new Integer(100));
    }

    @Test
    public void testNotificationId() {
        Reminder reminder = new Reminder();
        Integer notificationId = reminder.notificationId();
        assertNotNull(notificationId);
        assertTrue(notificationId > 0);
        // It should be different now
        Integer nextNotificationId = reminder.notificationId();
        assertNotNull(nextNotificationId);
        assertNotEquals(notificationId, nextNotificationId);
    }

    @Test
    public void testEquals() {
        Reminder firstReminder = new Reminder();
        Reminder secondReminder = new Reminder();
        // test summary equality
        assertEquals(firstReminder, secondReminder);
        firstReminder.setSummary("Test");
        assertNotEquals(firstReminder, secondReminder);
        secondReminder.setSummary("Test");
        assertEquals(firstReminder, secondReminder);
        // test content equality
        assertEquals(firstReminder, secondReminder);
        firstReminder.setContent("Test");
        assertNotEquals(firstReminder, secondReminder);
        secondReminder.setContent("Test");
        assertEquals(firstReminder, secondReminder);
        // test daysToLive equality
        assertEquals(firstReminder, secondReminder);
        firstReminder.setDaysToLive(10);
        assertNotEquals(firstReminder, secondReminder);
        secondReminder.setDaysToLive(10);
        assertEquals(firstReminder, secondReminder);
        // test lastNotifiedAt equality
        assertEquals(firstReminder, secondReminder);
        Date date = new Date();
        firstReminder.setLastNotifiedAt(date);
        assertNotEquals(firstReminder, secondReminder);
        secondReminder.setLastNotifiedAt(date);
        assertEquals(firstReminder, secondReminder);
        // test currentPeriod equality
        assertEquals(firstReminder, secondReminder);
        firstReminder.setCurrentPeriod(1);
        assertNotEquals(firstReminder, secondReminder);
        secondReminder.setCurrentPeriod(1);
        assertEquals(firstReminder, secondReminder);
        // test identifier equality
        assertEquals(firstReminder, secondReminder);
        firstReminder.setIdentifier("Test Identifier");
        assertNotEquals(firstReminder, secondReminder);
        secondReminder.setIdentifier("Test Identifier");
        assertEquals(firstReminder, secondReminder);
        // TODO: test un-setting other values and re-testing
    }
}
