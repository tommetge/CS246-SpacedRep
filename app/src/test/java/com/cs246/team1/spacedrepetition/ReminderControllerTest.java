package com.cs246.team1.spacedrepetition;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ReminderControllerTest {
    @Test
    public void addReminder() {
        ReminderController testReminder = new ReminderController();
        testReminder.addReminder("test summary", "test content", 1);
        List<Reminder> reminderList = testReminder.listReminders();
        assertEquals(1, reminderList.size());

        Reminder reminder = reminderList.get(0);
        assertEquals("test summary", reminder.getSummary());
    }

    @Test
    public void listReminder() {
        ReminderController testReminder = new ReminderController();

        for (int i=0; i<10; i++) {
            testReminder.addReminder("test summary", "test content", 1);
        }
        List<Reminder> reminderList = testReminder.listReminders();
        assertEquals(10, reminderList.size());

        String newSummary = "Something unexpected";
        Reminder reminderToEdit = reminderList.get(0);
        reminderToEdit.setSummary(newSummary);

        reminderList = testReminder.listReminders();
        Reminder editedReminder = reminderList.get(0);

        assertEquals(editedReminder.getSummary(), newSummary);
        assertEquals(10, reminderList.size());
    }

    @Test
    public void editReminder() {
        ReminderController testReminder = new ReminderController();
        testReminder.addReminder("test summary", "test content", 1);

        String newSummary = "Something unexpected";
        List<Reminder> reminderList = testReminder.listReminders();
        Reminder reminderToEdit = reminderList.get(0);
        reminderToEdit.setSummary(newSummary);

        reminderList = testReminder.listReminders();
        Reminder editedReminder = reminderList.get(0);

        assertEquals(editedReminder.getSummary(), newSummary);
        assertEquals(10, reminderList.size());
    }

    @Test
    public void removeReminder() {
        ReminderController testReminder = new ReminderController();
        testReminder.addReminder("test summary", "test content", 1);

        List<Reminder> reminderList = testReminder.listReminders();
        assertEquals(1, reminderList.size());

        testReminder.removeReminder(reminderList.get(0));

        reminderList = testReminder.listReminders();
        assertEquals(0, reminderList.size());
    }
}