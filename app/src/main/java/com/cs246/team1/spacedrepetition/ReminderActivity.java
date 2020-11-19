package com.cs246.team1.spacedrepetition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

public class ReminderActivity extends AppCompatActivity {
    private static final String LOGTAG = "ReminderActivity";

    public static final String ReminderKey = "ReminderActivity.reminder";

    private Reminder _reminder;

    Button notifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        notifyBtn = findViewById(R.id.notify_btn);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Reminder Notification","Reminder Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        /* This is an OnClick listener for now, until I figure out how to make the notification show up with a timer. */
        notifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotificationCompat.Builder builder = new NotificationCompat.Builder(ReminderActivity.this, "Reminder Notification");
                builder.setContentTitle("Reminder");
                /* Need to make the text the reminder summary*/
                builder.setContentText("Reminder Summary should go here");
                /* We could add a custom icon as a strech goal */
                builder.setSmallIcon(R.drawable.ic_launcher_background);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ReminderActivity.this);
                managerCompat.notify(1, builder.build());
            }
        });

        String reminderJSON = getIntent().getStringExtra(ReminderKey);
        setReminder(Reminder.fromJSON(reminderJSON));
    }

    public void setReminder(Reminder reminder) {
        _reminder = reminder;

        Log.d(LOGTAG, "Showing reminder " + String.valueOf(_reminder.getDaysToLive()));
        ((EditText)findViewById(R.id.summaryInput)).setText(_reminder.getSummary());
        ((EditText)findViewById(R.id.contentInput)).setText(_reminder.getContent());
        ((EditText)findViewById(R.id.daysInput)).setText(String.valueOf(_reminder.getDaysToLive()));
    }

    public void onDone(View view) {
        finish();
    }

}