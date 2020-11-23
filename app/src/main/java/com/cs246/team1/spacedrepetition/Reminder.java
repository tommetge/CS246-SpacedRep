package com.cs246.team1.spacedrepetition;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reminder {

    private static final String LOGTAG = "Reminder";

    public interface  ReminderLoadOperator {
        /**
         * Callback issued when Reminder.loadReminders() finishes.
         * @param reminders List of reminders loaded from the database.
         * @param success Indicates if the load call succeeded or failed.
         */
        public void loadOperationComplete(List<Reminder> reminders, Boolean success);
    }
    public interface  ReminderSaveOperator {
        /**
         * Callback issued when Reminder.save() finishes.
         * @param success Indicates if the load call succeeded or failed.
         */
        public void saveOperationComplete(Boolean success);
    }
    public interface ReminderDeleteOperator {
        /**
         * Callback issued when Reminder.delete() finishes.
         * @param success Indicates if the load call succeeded or failed.
         */
        public void deleteOperationComplete(Boolean success);
    }

    // For serializing to / de-serializing from Firebase
    private static final String ReminderCollectionName = "reminders";
    private static final String ReminderSummaryKey = "summary";
    private static final String ReminderContentKey = "content";
    private static final String ReminderDaysToLiveKey = "daysToLive";
    private static final String ReminderLastNotifiedAtKey = "lastNotifiedAt";
    private static final String ReminderCurrentPeriodKey = "currentPeriod";

    private String identifier;
    private String summary;
    private String content;
    private Integer daysToLive = 30;
    private Date lastNotifiedAt;
    private Integer currentPeriod = 0;

    // Accessors
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getDaysToLive() {
        return daysToLive;
    }

    public void setDaysToLive(Integer daysToLive) {
        this.daysToLive = daysToLive;
    }

    public Date getLastNotifiedAt() {
        return lastNotifiedAt;
    }

    public void setLastNotifiedAt(Date date) {
        this.lastNotifiedAt = date;
    }

    public Integer getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(Integer period) {
        this.currentPeriod = period;
    }

    // Generates a unique identifier for notifications derived from this reminder.

    /**
     * Generates a unique identifier for notifications derived from this reminder.
     * @return An identifier suitable for use with NotificationManagerCompat.notify()
     */
    public Integer notificationId() {
        // Note: We'll want to make this dependent on last notification date
        // and notification period. For now, we'll just make it the current
        // date.
        return Math.toIntExact(System.currentTimeMillis() / 1000);
    }

    // Firebase integration

    /**
     * Persists this reminder instance to the database.
     * @param operator Callback to indicate success or failure.
     */
    public void save(ReminderSaveOperator operator) {
        if (this.identifier == null) {
            saveByAdding(operator);
            return;
        }

        saveByUpdating(operator);
    }

    // Called when the reminder is new and does not have an existing Firebase-provided identifier.
    private void saveByAdding(ReminderSaveOperator operator) {
        Log.d(LOGTAG, "Saving new reminder");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ReminderCollectionName)
                .add(this)
                .addOnSuccessListener((documentReference) -> {
                    Log.d(LOGTAG, "Reminder added to Firebase with ID: " + documentReference.getId());
                    identifier = documentReference.getId();
                    operator.saveOperationComplete(true);
                })
                .addOnFailureListener((e) -> {
                    Log.w(LOGTAG, "Error adding reminder", e);
                    operator.saveOperationComplete(false);
                });
    }

    // Called when the reminder already exists in the database and must be updated in place.
    private void saveByUpdating(ReminderSaveOperator operator) {
        Log.d(LOGTAG, "Updating existing reminder");
        final String identifier = this.identifier;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ReminderCollectionName).document(this.identifier)
                .set(this)
                .addOnSuccessListener(aVoid -> {
                    Log.d(LOGTAG, "Reminder updated in Firebase with ID: " + identifier);
                    operator.saveOperationComplete(true);
                })
                .addOnFailureListener((e) -> {
                    Log.w(LOGTAG, "Error updating reminder", e);
                    operator.saveOperationComplete(false);
                });
    }

    public void delete(ReminderDeleteOperator operator) {
        Log.d(LOGTAG, "Deleting existing reminder");
        final String identifier = this.identifier;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ReminderCollectionName).document(this.identifier)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(LOGTAG, "Reminder deleted from Firebase with ID: " + identifier);
                    operator.deleteOperationComplete(true);
                })
                .addOnFailureListener((e) -> {
                    Log.e(LOGTAG, "Error deleting reminder", e);
                    operator.deleteOperationComplete(false);
                });
    }

    /**
     * Asynchronously loads all reminders from the database.
     * @param operator Callback issued when the load operation finishes. See
     *                 ReminderLoadOperator.loadOperationComplete()
     */
    public static void loadReminders(ReminderLoadOperator operator) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ReminderCollectionName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Reminder> reminders = new ArrayList();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reminder reminder = document.toObject(Reminder.class);
                            reminder.setIdentifier(document.getId());
                            Log.d(LOGTAG, document.getId() + " => " + reminder.toString());
                            reminders.add(reminder);
                        }
                        operator.loadOperationComplete(reminders, true);
                    } else {
                        Log.w(LOGTAG, "Error getting documents.", task.getException());
                        operator.loadOperationComplete(null, false);
                    }
                });
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != this.getClass())
            return false;

        Reminder other = (Reminder)obj;
        if (!this.summary.equals(other.getSummary()))
            return false;
        if (!this.content.equals(other.getContent()))
            return false;
        if (this.daysToLive != other.getDaysToLive())
            return false;
        if (!this.lastNotifiedAt.equals(other.getLastNotifiedAt()))
            return false;
        if (this.currentPeriod != other.getCurrentPeriod())
            return false;

        return true;
    }

    /**
     * Offers support for deserializing a Reminder instance that is JSON-ified.
     * @param json Source JSON describing the Reminder.
     * @return Deserialized Reminder instance.
     */
    public static Reminder fromJSON(String json) {
        return new Gson().fromJson(json, Reminder.class);
    }

    /**
     * Offers support for serializing a Reminder instance to JSON.
     * @return JSON-ified (serialized) Reminder instance.
     */
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String toString() {
        return super.toString() + "{" + identifier + ": summary=\"" + summary + "\", content: \""
                + "\", daysToLive=" + daysToLive + ", lastNotified=" + lastNotifiedAt
                + ", currentPeriod=" + currentPeriod;
    }
}
