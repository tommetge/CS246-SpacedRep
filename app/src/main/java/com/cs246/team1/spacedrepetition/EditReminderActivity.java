package com.cs246.team1.spacedrepetition;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditReminderActivity extends AppCompatActivity {
    private static final String LOGTAG = "EditReminderActivity";

    public static final String ReminderKey = "EditReminderActivity.reminder";

    private Reminder _reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);
        String reminderJSON = getIntent().getStringExtra(ReminderKey);
        setReminder(Reminder.fromJSON(reminderJSON));
    }

    public void setReminder(Reminder reminder) {
        if (reminder == null) {
            reminder = new Reminder();
        }
        _reminder = reminder;

        Log.d(LOGTAG, "Showing reminder " + String.valueOf(_reminder.daysToLive()));
        ((EditText)findViewById(R.id.summaryInput)).setText(_reminder.summary());
        ((EditText)findViewById(R.id.contentInput)).setText(_reminder.content());
        ((EditText)findViewById(R.id.daysInput)).setText(String.valueOf(_reminder.daysToLive()));
    }

    public void onSave(View view) {
        // Note: save the reminder before finishing
        Log.d(LOGTAG, "Save pressed");
        finish();
    }
}
