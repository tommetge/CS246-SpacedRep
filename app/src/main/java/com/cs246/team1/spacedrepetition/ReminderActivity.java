package com.cs246.team1.spacedrepetition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class ReminderActivity extends AppCompatActivity {
    private static final String LOGTAG = "ReminderActivity";

    public static final String ReminderKey = "ReminderActivity.reminder";

    private Reminder reminder;
    private List<Reminder> reminders;
    private Integer reminderIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        String reminderJSON = getIntent().getStringExtra(ReminderKey);
        setReminder(Reminder.fromJSON(reminderJSON));

        Reminder.loadReminders((reminders, success) -> {
            if (!success) {
                Log.e(LOGTAG, "Failed to load reminders!");
                return;
            }

            Log.d(LOGTAG, "Loaded reminders: " + reminders);
            this.reminders = reminders;
            for (int i=0; i<reminders.size(); i++) {
                if (this.reminder.equals(reminders.get(i))) {
                    Log.d(LOGTAG, "Reminder found at offset " + i);
                    this.reminderIndex = i;
                    break;
                }
            }
        });
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
        ((TextView)findViewById(R.id.summary)).setText(reminder.getSummary());
        ((TextView)findViewById(R.id.content)).setText(reminder.getContent());
    }

    public void onNotifyButton(View view) {
        Log.d(LOGTAG, "Showing reminder notification " + reminder.toString());
        MainActivity.showReminderNotification(this, reminder);
    }

    public void onDone(View view) {
        finish();
    }

    public void onNextReminder(View view) {
        if (reminderIndex >= reminders.size() - 1) {
            return;
        }

        Reminder nextReminder = reminders.get(reminderIndex + 1);
        Log.d(LOGTAG, "Showing next reminder: " + nextReminder);

        Intent intent = new Intent(this, ReminderActivity.class);
        intent.putExtra(ReminderActivity.ReminderKey, nextReminder.toJSON());

        startActivity(intent);
    }

    public void onPreviousReminder(View view) {
        if (reminderIndex == 0) {
            return;
        }

        Reminder previousReminder = reminders.get(reminderIndex - 1);
        Log.d(LOGTAG, "Showing previous reminder: " + previousReminder);

        Intent intent = new Intent(this, ReminderActivity.class);
        intent.putExtra(ReminderActivity.ReminderKey, previousReminder.toJSON());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

}