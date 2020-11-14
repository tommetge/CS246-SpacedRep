package com.cs246.team1.spacedrepetition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        EditOrDeletePopUp.EditOrDeletePopUpListener {

    private static final String LOGTAG = "MainActivity";
    private static final String ReminderNotificationChannelId =
            "com.cs246.team1.spacedrepetition.notifications.reminders";
    private static final int RC_SIGN_IN = 123;
    private FirebaseUser user;
    private List<String> _reminderList = new ArrayList<String>();
    private ArrayAdapter<String> _reminderAdapter;
    private Reminder _testReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            TextView text = (TextView) findViewById(R.id.reminderLabel);
            text.setText("Hello, " + user.getDisplayName());
        }

        _testReminder = new Reminder();
        _testReminder.setSummary("Test Reminder");
        _testReminder.setContent("Test content for the test reminder");
        _testReminder.setDaysToLive(10);

        _reminderAdapter =
                new ArrayAdapter<String>(
                        this, android.R.layout.simple_list_item_1, _reminderList);
        ListView reminderView = (ListView) findViewById(R.id.reminderList);
        reminderView.setAdapter(_reminderAdapter);
        reminderView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DialogFragment dialog = new EditOrDeletePopUp();
                dialog.show(getSupportFragmentManager(), "EditOrDeletePopUpFragment");
            }
        });

        _reminderAdapter.clear();
        _reminderAdapter.add(_testReminder.summary());

        // Test code: this fires a notification 3 seconds after launch
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Reminder reminder = new Reminder();
                showReminderNotification(_testReminder);
            }
        }, 3000);

    }

    public void onSignOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(Task<Void> task) {
                        // ...
                    }
                });
    }

    public void onNewReminder(View view) {
        Intent intent = new Intent(this, EditReminderActivity.class);
        startActivity(intent);
    }

    private void createNotificationChannel() {
         CharSequence name = getString(R.string.channel_name);
         String description = getString(R.string.channel_description);
         int importance = NotificationManager.IMPORTANCE_DEFAULT;
         NotificationChannel channel =
                 new NotificationChannel(ReminderNotificationChannelId, name, importance);
         channel.setDescription(description);
         // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void showReminderNotification(Reminder reminder) {
        // TODO: implement this
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        MainActivity.this, ReminderNotificationChannelId);
        builder.setContentTitle("Reminder");
        /* Need to make the text the reminder summary*/
        builder.setContentText(_testReminder.summary());
        /* We could add a custom icon as a stretch goal */
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
        managerCompat.notify(1, builder.build());
    }

    public void showReminder(View view) {
        Intent intent = new Intent(this, ReminderActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDialogEditClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.d(LOGTAG, "Edit pressed");

        Intent intent = new Intent(this, EditReminderActivity.class);
        intent.putExtra(EditReminderActivity.ReminderKey, _testReminder.toJSON());

        startActivity(intent);
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.d(LOGTAG, "Delete pressed");
    }
}