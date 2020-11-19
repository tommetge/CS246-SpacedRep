package com.cs246.team1.spacedrepetition;

import java.util.ArrayList;
import java.util.List;
public class ReminderController {
    ArrayList<Reminder> _reminders = new ArrayList<Reminder>();

    public void addReminder(String summary, String content, Integer daysToLive) {
        Reminder reminder = new Reminder();
        reminder.setSummary(summary);
        reminder.setContent(content);
        reminder.setDaysToLive(daysToLive);

        _reminders.add(reminder);
    }

    public List<Reminder> listReminders() {
        return _reminders;
    }

    public void removeReminder(Reminder reminder) {
        boolean found = false;
        int index = 0;

        for (int i=0; i<_reminders.size(); i++) {
            if (_reminders.get(i) != reminder) {
                continue;
            }

            found = true;
            index = i;
            break;
        }

        if (found) {
            _reminders.remove(index);
        }
    }

    public void pushNotifications() {

    }
}