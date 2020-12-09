package com.cs246.team1.spacedrepetition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class ReminderActivity extends AppCompatActivity {
    private static final String LOGTAG = "ReminderActivity";

    public static final String ReminderIndexKey = "ReminderActivity.reminderIndex";

    private Reminder reminder;
    private List<Reminder> reminders;
    private Integer reminderIndex = 0;
    private boolean contentShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        getSupportActionBar().setTitle(getString(R.string.review_title));

        ReminderDatabase.defaultDatabase().listReminders((reminders, success) -> {
            if (!success) {
                Log.e(LOGTAG, "Failed to load reminders!");
                return;
            }

            Log.d(LOGTAG, "Loaded reminders: " + reminders);
            this.reminders = Review.ReminderReview.getRemindersForReview(reminders);

            setReminderIndex(getIntent().getIntExtra(ReminderIndexKey, 0));
        });
    }

    public void setReminderIndex(int index) {
        if (index < 0 || index >= reminders.size()) {
            return;
        }

        reminderIndex = index;
        reminder = this.reminders.get(index);
        ((TextView)findViewById(R.id.summary)).setText(reminder.getSummary());
        ((TextView)findViewById(R.id.content)).setText(getString(R.string.reminder_content_hidden));
        contentShowing = false;
        Button revealButton = findViewById(R.id.toggleContent);
        revealButton.setEnabled(true);
        revealButton.setText(getString(R.string.review_reveal_button_title_reveal));
    }

    public void clearReminder() {
        ((TextView)findViewById(R.id.summary)).setText(reminder.getSummary());
        ((TextView)findViewById(R.id.content)).setText(getString(R.string.reminder_content_hidden));
        Button revealButton = findViewById(R.id.toggleContent);
        revealButton.setEnabled(false);
        revealButton.setText(getString(R.string.review_reveal_button_title_loading));
    }

    public void onNotifyButton(View view) {
        Log.d(LOGTAG, "Showing reminder notification " + reminder.toString());
        MainActivity.showReminderNotification(this, reminder);
    }

    public void onReveal(View view) {
        Button button = findViewById(R.id.toggleContent);
        TextView contentView = findViewById(R.id.content);
        if (contentShowing) {
            contentView.setText(getString(R.string.reminder_content_hidden));
            button.setText(getString(R.string.reminder_toggle_button_reveal));
        } else {
            contentView.setText(this.reminder.getContent());
            button.setText(getString(R.string.reminder_toggle_button_hide));
        }
        contentShowing = !contentShowing;
    }

    public void onDone(View view) {
        reminder.setLastNotifiedAt(new Date());
        ReminderDatabase.defaultDatabase().updateReminder(reminder, success -> { finish(); });
    }

    public void onNextReminder(View view) {
        if (reminderIndex >= reminders.size() - 1) {
            return;
        }

        clearReminder();
        reminder.setLastNotifiedAt(new Date());
        ReminderDatabase.defaultDatabase().updateReminder(reminder, success -> {
            Log.d(LOGTAG, "Showing next reminder");
            setReminderIndex(reminderIndex + 1);
        });
    }

    public void onPreviousReminder(View view) {
        if (reminderIndex == 0) {
            return;
        }

        clearReminder();
        reminder.setLastNotifiedAt(new Date());
        ReminderDatabase.defaultDatabase().updateReminder(reminder, success -> {
            Log.d(LOGTAG, "Showing previous reminder");
            setReminderIndex(reminderIndex - 1);
        });
    }

}