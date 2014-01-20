package com.example.MyFragmentSample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DiscardRemoveDialogFragment extends DialogFragment {

    public interface DiscardRemoveDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    DiscardRemoveDialogListener mListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Alert dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle(null);

        // DIALOG DISCARD
        if (NewEditActivity.DIALOG_DISCARD.equals(getTag())) {
            builder.setMessage(R.string.dialog_discard_message);
        }

        // DIALOG REMOVE
        else if (NewEditActivity.DIALOG_REMOVE.equals(getTag())) {
            builder.setMessage(R.string.dialog_remove_message);
        }

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogPositiveClick(DiscardRemoveDialogFragment.this);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(DiscardRemoveDialogFragment.this);
                    }
                });

        // Create the alert dialog object
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (DiscardRemoveDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement DiscardRemoveDialogListener");
        }
    }


}
