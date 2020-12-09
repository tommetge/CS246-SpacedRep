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
            getSupportActionBar().setTitle(getString(R.string.addReminderTitle));
        } else {
            getSupportActionBar().setTitle(getString(R.string.editReminderTitle));
        }
        _reminder = reminder;

        Log.d(LOGTAG, "Showing reminder " + _reminder.toString());
        ((EditText) findViewById(R.id.summaryInput)).setText(_reminder.getSummary());
        ((EditText) findViewById(R.id.contentInput)).setText(_reminder.getContent());
        ((EditText) findViewById(R.id.daysInput)).setText(String.valueOf(_reminder.getDaysToLive()));
    }

    public void onSave(View view) {
        // Note: save the reminder before finishing
        Log.d(LOGTAG, "Save pressed");
        _reminder.setSummary(((EditText) findViewById(R.id.summaryInput)).getText().toString());
        _reminder.setContent(((EditText) findViewById(R.id.contentInput)).getText().toString());
        _reminder.setDaysToLive(
                Integer.valueOf(((EditText) findViewById(R.id.daysInput)).getText().toString()));

        Log.d(LOGTAG, "Saving reminder: " + _reminder.toString());
        ReminderDatabase.defaultDatabase().addReminder(_reminder, (success) -> {
            if (!success) {
                Log.e(LOGTAG, "Failed to save reminder");
            }

            finish();
        });
    }
}
