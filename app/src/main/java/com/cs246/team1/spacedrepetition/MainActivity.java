package com.cs246.team1.spacedrepetition;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        EditOrDeletePopUp.EditOrDeletePopUpListener {

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOGTAG, "Scheduled wake, will trigger notification");
            MainActivity.showRemindersNotification(context);
        }
    }

    private static final String LOGTAG = "MainActivity";
    private static final String ReminderNotificationChannelId =
            "com.cs246.team1.spacedrepetition.notifications.reminders";
    private FirebaseUser user;
    private final ReminderDatabase.ReminderDatabaseStore _remindersDB =
            ReminderDatabase.defaultDatabase();
    private final List<String> _reminderList = new ArrayList<>();
    private ReminderAdapter _reminderAdapter;
    private List<Reminder> _reminders = new ArrayList<>();
    private Reminder _selectedReminder;
    private Boolean _notificationsScheduled = false;

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
            getSupportActionBar().setTitle(getString(R.string.main_reminders_title));
        }

        _reminderAdapter =
                new ReminderAdapter(
                        this, _reminders);
        ListView reminderView = findViewById(R.id.reminderList);
        reminderView.setAdapter(_reminderAdapter);
        reminderView.setOnItemClickListener((parent, view, position, id) -> {
            Reminder reminder = _reminders.get(position);
            _selectedReminder = reminder;
            onReminderClick(reminder);
        });

        ReminderDatabase.defaultDatabase().addListener(() -> {
            Log.d(LOGTAG, "Notified of change to reminders");
            onRemindersChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_action_add: {
                onNewReminder(null);
                return true;
            }
            case R.id.nav_action_logout:
                onSignOut(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        Log.d(LOGTAG, "Resumed activity");
        onRemindersChanged();
        super.onResume();
    }

    /**
     * Opens the LoginActivity when the User signs out
     * @param view the view from which the method is called.
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
     * @param view the view from which the method is called.
     */
    public void onNewReminder(View view) {
        Intent intent = new Intent(this, EditReminderActivity.class);
        startActivity(intent);
    }

    private void onRemindersChanged() {
        Log.d(LOGTAG, "Reloading reminders");
        _remindersDB.listReminders((reminders, success) -> {
            if (!success) {
                Log.e(LOGTAG, "Failed to load reminders!");
                return;
            }

            Log.d(LOGTAG, "Loaded reminders: " + reminders.toString());
            new Handler(Looper.getMainLooper()).post(() -> {
                _reminders = reminders;
                _reminderAdapter.clear();
                for (Reminder reminder : reminders) {
                    _reminderAdapter.add(reminder);
                }
            });

            List<Reminder> remindersToReview = Review.ReminderReview.getRemindersForReview(reminders);
            if (remindersToReview.size() == 0) {
                Log.d(LOGTAG, "No reminders to review");
                Button reviewButton = findViewById(R.id.startReview);
                reviewButton.setText(R.string.main_review_button_title_inactive);
                reviewButton.setEnabled(false);
            }

            if (_notificationsScheduled) {
                return;
            }

            Log.d(LOGTAG, "Scheduling review");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 9); // 9am

            Intent intent = new Intent(this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

            _notificationsScheduled = true;
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
     * @param context activity from which the notification is called.
     * @param reminder the reminder that will be shown on the notification.
     */
    public static void showReminderNotification(Context context, Reminder reminder) {
        Log.d(LOGTAG, "Showing notification for reminder " + reminder.toString());

        Intent intent = new Intent(context, ReminderActivity.class);
        intent.putExtra(ReminderActivity.ReminderIndexKey, 0);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context, ReminderNotificationChannelId);
        builder.setContentTitle("Reminder");
        /* Need to make the text the reminder summary*/
        builder.setContentText(reminder.getSummary());
        // We can include the content but it breaks the flash card model
        // builder.setStyle(new NotificationCompat.BigTextStyle().bigText(reminder.getContent()));
        /* We could add a custom icon as a stretch goal */
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(reminder.notificationId(), builder.build());
    }

    public static void showRemindersNotification(Context context) {
        ReminderDatabase.defaultDatabase().listReminders(((reminders, success) -> {
            if (!success) {
                Log.e(LOGTAG, "Failed to load reminders, will not post notifications!");
                return;
            }

            List<Reminder> remindersToReview = Review.ReminderReview.getRemindersForReview(reminders);
            if (remindersToReview.size() == 0) {
                Log.d(LOGTAG, "No reminders to review");
                return;
            }

            showReminderNotification(context, reminders.get(0));
        }));
    }

    /**
     * Show the first reminder in the review queue.
     * @param view the view from which this method was called.
     */
    public void startReview(View view) {
        List<Reminder> remindersToReview = Review.ReminderReview.getRemindersForReview(_reminders);
        if (remindersToReview.size() == 0) {
            Log.d(LOGTAG, "No reminders to review");
            return;
        }

        Intent intent = new Intent(this, ReminderActivity.class);
        intent.putExtra(ReminderActivity.ReminderIndexKey, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    private void onReminderClick(Reminder reminder) {
        // User tapped the reminder
        Log.d(LOGTAG, "Edit pressed");

        Intent intent = new Intent(this, EditReminderActivity.class);
        intent.putExtra(EditReminderActivity.ReminderKey, reminder.toJSON());

        startActivity(intent);
    }

    /**
     * Opens the edit dialog for the reminder that called the method.
     * @param dialog the dialog that's sending the message.
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
     * @param dialog the dialog that's sending the message.
     */
    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.d(LOGTAG, "Delete pressed");
        _remindersDB.deleteReminder(_selectedReminder, (success) -> onRemindersChanged());
    }
}