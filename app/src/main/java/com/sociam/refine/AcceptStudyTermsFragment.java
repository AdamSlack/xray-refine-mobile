package com.sociam.refine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

public class AcceptStudyTermsFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.study_opt_in_dialog,null));

        builder.setMessage("Accept Study Terms")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PreferencesActivity activity = (PreferencesActivity) getActivity();
                        activity.onStudyOptInSelection(true);
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PreferencesActivity activity = (PreferencesActivity) getActivity();
                        activity.onStudyOptInSelection(false);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
