package com.cs246.team1.spacedrepetition;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Factory for generating ReminderDatabaseStore instances.
 * @author Tom Metge
 */
public class ReminderDatabase {
    public interface  ReminderDBLoadOperator {
        /**
         * Callback issued when ReminderDatabaseStore.listReminders() finishes.
         * @param reminders List of reminders loaded from the database.
         * @param success Indicates if the load call succeeded or failed.
         */
        void loadOperationComplete(List<Reminder> reminders, Boolean success);
    }
    public interface ReminderDBFindOperator {
        /**
         * Callback issued when ReminderDatabaseStore.findReminder() finishes.
         * @param index Index of the reminder - negative if not found.
         * @param success Indicates if the reminder is found or not.
         */
        void findOperationComplete(int index, Boolean success);
    }
    public interface ReminderDBOperator {
        /**
         * Callback issued when the method call completes.
         * @param success Indicates if the call succeeded or failed.
         */
        void operationComplete(Boolean success);
    }
    public interface ReminderDBListener {
        /**
         * Callback issued when the database contents have changed.
         */
        void onChange();
    }

    /**
     * Interface for interacting with ReminderDatabaseStore instances. These instances offer
     * methods for reading and writing Reminder data to various backends, including Firebase
     * (via the ReminderFirebaseStore).
     */
    public interface ReminderDatabaseStore {
        /**
         * Asynchronously adds the reminder to the database.
         * @param operator Callback issued when the operation finishes.
         */
        void addReminder(Reminder reminder, ReminderDBOperator operator);

        /**
         * Asynchronously deletes the reminder from the database.
         * @param operator Callback issued when the operation finishes.
         */
        void deleteReminder(Reminder reminder, ReminderDBOperator operator);

        /**
         * Asynchronously updates the reminder and saves it to the database.
         * @param operator Callback issued when the load operation finishes.
         */
        void updateReminder(Reminder reminder, ReminderDBOperator operator);

        /**
         * Asynchronously loads all reminders from the database.
         * @param operator Callback issued when the load operation finishes.
         */
        void listReminders(ReminderDBLoadOperator operator);

        /**
         * Asynchronously find the index of a given reminder, if present.
         * @param operator Callback issued when the operation finishes.
         */
        void findReminder(Reminder reminder, ReminderDBFindOperator operator);

        /**
         * Adds the listener as an observer. The listener will be called when
         * database contents change.
         * @param listener Callback issued when the database changes.
         */
        void addListener(ReminderDBListener listener);

        /**
         * Removes the specified listener as an observer.
         * @param listener The observer to remove.
         */
        void removeListener(ReminderDBListener listener);
    }

    /**
     * ReminderDatabaseStore backed by Firebase.
     */
    public static class ReminderFirebaseStore implements ReminderDatabaseStore {

        private static final String LOGTAG = "ReminderFirebaseStore";

        // For serializing to / de-serializing from Firebase
        private static final String ReminderCollectionName = "reminders";

        private final FirebaseFirestore _db = FirebaseFirestore.getInstance();

        private ArrayList<ReminderDBListener> _listeners = new ArrayList<>();

        public ReminderFirebaseStore() {
            _db.collection(ReminderCollectionName).addSnapshotListener((value, e) -> {
                if (e != null) {
                    Log.w(LOGTAG, "Listen failed: ", e);
                    return;
                }

                for (ReminderDBListener listener : _listeners) {
                    Log.d(LOGTAG, "Notifying listener of change");
                    listener.onChange();
                }
            });
        }

        @Override
        public void addReminder(Reminder reminder, ReminderDBOperator operator) {
            Log.d(LOGTAG, "Saving new reminder");
            if (reminder.getIdentifier() != null) {
                updateReminder(reminder, operator);
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Log.e(LOGTAG, "Attempted to save a reminder without a logged-in user");
                operator.operationComplete(false);
            }

            reminder.setUserId(user.getUid());

            _db.collection(ReminderCollectionName)
                .add(reminder)
                .addOnSuccessListener((documentReference) -> {
                    Log.d(LOGTAG, "Reminder added to Firebase with ID: " + documentReference.getId());
                    reminder.setIdentifier(documentReference.getId());
                    operator.operationComplete(true);
                })
                .addOnFailureListener((e) -> {
                    Log.w(LOGTAG, "Error adding reminder", e);
                    operator.operationComplete(false);
                });
        }

        @Override
        public void deleteReminder(Reminder reminder, ReminderDBOperator operator) {
            Log.d(LOGTAG, "Deleting existing reminder");
            final String identifier = reminder.getIdentifier();
            _db.collection(ReminderCollectionName).document(identifier)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(LOGTAG, "Reminder deleted from Firebase with ID: " + identifier);
                        operator.operationComplete(true);
                    })
                    .addOnFailureListener((e) -> {
                        Log.e(LOGTAG, "Error deleting reminder", e);
                        operator.operationComplete(false);
                    });
        }

        @Override
        public void updateReminder(Reminder reminder, ReminderDBOperator operator) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Log.e(LOGTAG, "Attempted to save a reminder without a logged-in user");
                operator.operationComplete(false);
            }

            reminder.setUserId(user.getUid());

            Log.d(LOGTAG, "Updating existing reminder");
            final String identifier = reminder.getIdentifier();
            _db.collection(ReminderCollectionName).document(identifier)
                    .set(reminder)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(LOGTAG, "Reminder updated in Firebase with ID: " + identifier);
                        operator.operationComplete(true);
                    })
                    .addOnFailureListener((e) -> {
                        Log.w(LOGTAG, "Error updating reminder", e);
                        operator.operationComplete(false);
                    });
        }

        @Override
        public void listReminders(ReminderDBLoadOperator operator) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Log.e(LOGTAG, "Attempted to save a reminder without a logged-in user");
                operator.loadOperationComplete(null, false);
            }

            _db.collection(ReminderCollectionName).orderBy("createdAt")
                .whereEqualTo("userId", user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Reminder> reminders = new ArrayList<>();
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
        public void findReminder(Reminder reminder, ReminderDBFindOperator operator) {
            listReminders((reminders, success) -> {
                if (!success) {
                    operator.findOperationComplete(-1, false);
                    return;
                }

                int index = ReminderDatabase.indexOf(reminder, reminders);
                if (index < 0) {
                    operator.findOperationComplete(-1, false);
                    return;
                }

                operator.findOperationComplete(index, true);
            });
        }

        @Override
        public void addListener(ReminderDBListener listener) {
            if (_listeners.contains(listener)) {
                return;
            }

            _listeners.add(listener);
        }

        @Override
        public void removeListener(ReminderDBListener listener) {
            if (_listeners.contains(listener)) {
                _listeners.remove(listener);
            }
        }
    }

    /**
     * ReminderDatabaseStore backed by memory only (a simple array). Useful for testing.
     */
    public static class ReminderArrayStore implements ReminderDatabaseStore {
        private final ArrayList<Reminder> _reminders = new ArrayList<>();

        @Override
        public void addReminder(Reminder reminder, ReminderDBOperator operator) {
            if (reminder.getIdentifier() != null) {
                updateReminder(reminder, operator);
                return;
            }

            reminder.setIdentifier(UUID.randomUUID().toString());
            _reminders.add(reminder);
            operator.operationComplete(true);
        }

        @Override
        public void deleteReminder(Reminder reminder, ReminderDBOperator operator) {
            int index = ReminderDatabase.indexOf(reminder, _reminders);
            if (index < 0) {
                operator.operationComplete(false);
                return;
            }

            _reminders.remove(index);
            operator.operationComplete(true);
        }

        @Override
        public void updateReminder(Reminder reminder, ReminderDBOperator operator) {
            int index = ReminderDatabase.indexOf(reminder, _reminders);
            if (index < 0 || index >= _reminders.size()) {
                operator.operationComplete(false);
                return;
            }

            _reminders.set(index, reminder);
            operator.operationComplete(true);
        }

        @Override
        public void listReminders(ReminderDBLoadOperator operator) {
            operator.loadOperationComplete(_reminders, true);
        }

        @Override
        public void findReminder(Reminder reminder, ReminderDBFindOperator operator) {
            int index = ReminderDatabase.indexOf(reminder, _reminders);
            operator.findOperationComplete(index, index >= 0);
        }

        @Override
        public void addListener(ReminderDBListener listener) {
            // Do nothing
        }

        @Override
        public void removeListener(ReminderDBListener listener) {
            // Do nothing
        }
    }

    public static ReminderDatabaseStore defaultDatabase() {
        return new ReminderFirebaseStore();
    }

    public static ReminderDatabaseStore testDatabase() {
        return new ReminderArrayStore();

    }

    // Returns a negative number if not found
    private static int indexOf(Reminder reminder, List<Reminder> reminders) {
        int i;
        boolean found = false;
        for (i=0; i<reminders.size(); i++) {
            if (reminders.get(i).getIdentifier().equals(reminder.getIdentifier())) {
                break;
            }
            if (reminders.get(i).equals(reminder)) {
                break;
            }
        }

        if (!found) {
            return -1;
        }

        return i;
    }
}
