package com.cs246.team1.spacedrepetition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class EditOrDeletePopUp extends DialogFragment {
    public interface EditOrDeletePopUpListener {
        public void onDialogEditClick(DialogFragment dialog);
        public void onDialogDeleteClick(DialogFragment dialog);
    }

    EditOrDeletePopUpListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (EditOrDeletePopUpListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Must implement EditOrDeletePopUpListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.filler)
                .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogEditClick(EditOrDeletePopUp.this);
                    }
                })
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogDeleteClick(EditOrDeletePopUp.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
