package com.cs246.team1.spacedrepetition;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        EditOrDeletePopUp.EditOrDeletePopUpListener {

    private static final String LOGTAG = "MainActivity";
    private static final String ReminderNotificationChannelId =
            "com.cs246.team1.spacedrepetition.notifications.reminders";
    private FirebaseUser user;
    private final List<String> _reminderList = new ArrayList<>();
    private ArrayAdapter<String> _reminderAdapter;
    private List<Reminder> _reminders;
    private Reminder _selectedReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            Log.d(LOGTAG, "User already logged in");
            TextView text = findViewById(R.id.reminderLabel);
            text.setText(getString(R.string.main_hello, user.getDisplayName()));
        }

        _reminderAdapter =
                new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1, _reminderList);
        ListView reminderView = findViewById(R.id.reminderList);
        reminderView.setAdapter(_reminderAdapter);
        reminderView.setOnItemClickListener((parent, view, position, id) -> {
            _selectedReminder = _reminders.get(position);
            DialogFragment dialog = new EditOrDeletePopUp();
            dialog.show(getSupportFragmentManager(), "EditOrDeletePopUpFragment");
        });
    }

    @Override
    protected void onResume() {
        Log.d(LOGTAG, "Resumed activity");
        onRemindersChanged();
        super.onResume();
    }

    /**
     * Opens the LoginActivity when the User signs out
     * @param view - the view from which the method is called.
     */
    public void onSignOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener((task) -> {
                    user = null;
                    startActivity(new Intent(this, LoginActivity.class));
                });
    }

    /**
     * Opens the editReminderActivity when called.
     * @param view - the view from which the method is called.
     */
    public void onNewReminder(View view) {
        Intent intent = new Intent(this, EditReminderActivity.class);
        startActivity(intent);
    }

    private void onRemindersChanged() {
        Log.d(LOGTAG, "Reloading reminders");
        Reminder.loadReminders((reminders, success) -> {
            if (!success) {
                Log.e(LOGTAG, "Failed to load reminders!");
                return;
            }

            Log.d(LOGTAG, "Loaded reminders: " + reminders.toString());
            new Handler(Looper.getMainLooper()).post(() -> {
                _reminders = reminders;
                _reminderAdapter.clear();
                for (Reminder reminder : reminders) {
                    _reminderAdapter.add(reminder.getSummary());
                }
            });

            // Test code: this fires a notification 3 seconds after launch
            new Handler().postDelayed(() -> {
                Reminder reminder = new Reminder();
                showReminderNotification(reminders.get(0));
            }, 3000);
        });
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

    /**
     * Shows a notification with the summary and title of the given reminder.
     * @param reminder - the reminder that will be shown on the notification.
     */
    public void showReminderNotification(Reminder reminder) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        MainActivity.this, ReminderNotificationChannelId);
        builder.setContentTitle("Reminder");
        /* Need to make the text the reminder summary*/
        builder.setContentText(reminder.getSummary());
        /* We could add a custom icon as a stretch goal */
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
        managerCompat.notify(reminder.notificationId(), builder.build());
    }

    /**
     * Shows the reminder that was clicked in the given view.
     * @param view - the view from which this method was called.
     */
    public void showReminder(View view) {
        Intent intent = new Intent(this, ReminderActivity.class);
        startActivity(intent);
    }

    /**
     * Opens the edit dialog for the reminder that called the method.
     * @param dialog - the dialog that's sending the message.
     */
    @Override
    public void onDialogEditClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.d(LOGTAG, "Edit pressed");

        Intent intent = new Intent(this, EditReminderActivity.class);
        intent.putExtra(EditReminderActivity.ReminderKey, _selectedReminder.toJSON());

        startActivity(intent);
    }

    /**
     * Opens the delete dialog for the reminder that called the method.
     * @param dialog - the dialog that's sending the message.
     */
    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.d(LOGTAG, "Delete pressed");
        _selectedReminder.delete(success -> onRemindersChanged());
    }
}