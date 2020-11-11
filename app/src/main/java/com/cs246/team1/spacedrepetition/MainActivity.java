package com.cs246.team1.spacedrepetition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements EditOrDeletePopUp.EditOrDeletePopUpListener {

    private static final String LOGTAG = "MainActivity";
    private static final int RC_SIGN_IN = 123;
    private FirebaseUser user;
    private List<String> _reminderList = new ArrayList<String>();
    private ArrayAdapter<String> _reminderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            TextView text = (TextView) findViewById(R.id.reminderLabel);
            text.setText("Hello, " + user.getDisplayName());
        }

        _reminderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _reminderList);
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
        _reminderAdapter.add("Test Reminder");

    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(Task<Void> task) {
                        // ...
                    }
                });
    }

    public void showReminder(View view) {
        Intent intent = new Intent(this, ReminderActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDialogEditClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        Log.d(LOGTAG, "Edit pressed");
        startActivity(new Intent(this, ReminderActivity.class));
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Log.d(LOGTAG, "Delete pressed");
    }
}