package com.cs246.team1.spacedrepetition;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ReminderControllerTest {
    @Test
    public void addReminder_isCorrect() {
        ReminderController testReminder = new ReminderController();
        testReminder.addReminder("test summary", "test content", 1);
    }
}