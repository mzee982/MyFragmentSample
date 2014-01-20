package com.example.MyFragmentSample;

import android.app.Activity;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class NewEditActivity extends Activity implements DiscardRemoveDialogFragment.DiscardRemoveDialogListener {

    public static final String DIALOG_DISCARD = "DIALOG_DISCARD";
    public static final String DIALOG_REMOVE = "DIALOG_REMOVE";
    private String mMode;
    private long mId = -1L;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout
        setContentView(R.layout.new_edit);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get EXTRA MESSAGES
        mMode = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE_MODE);
        mId = getIntent().getLongExtra(MainActivity.EXTRA_MESSAGE_ID, -1L);

        // Set activity title according to mode
        // MODE NEW
        if (MainActivity.EXTRA_MESSAGE_MODE_NEW.equals(mMode)) {
            setTitle(R.string.activity_label_new);
        }

        // MODE EDIT
        else {
            setTitle(R.string.activity_label_edit);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        // MODE EDIT
        if (MainActivity.EXTRA_MESSAGE_MODE_EDIT.equals(mMode)) {

            // Query from DB
            SampleDataDbHelper sampleDataDbHelper = SampleDataDbHelper.getInstance(this);
            Cursor slaveCursor = sampleDataDbHelper.querySlave(mId);

            slaveCursor.moveToFirst();
            String masterValue = slaveCursor.getString(slaveCursor.getColumnIndexOrThrow(SampleDataContract.SampleData.COLUMN_NAME_MASTER));
            String slaveValue = slaveCursor.getString(slaveCursor.getColumnIndexOrThrow(SampleDataContract.SampleData.COLUMN_NAME_SLAVE));

            // Populate edit text fields
            EditText masterEdit = (EditText)findViewById(R.id.new_edit_activity_master_edit);
            EditText slaveEdit = (EditText)findViewById(R.id.new_edit_activity_slave_edit);
            masterEdit.setText(masterValue);
            slaveEdit.setText(slaveValue);

            // Close DB cursor
            if (!slaveCursor.isClosed()) {
                slaveCursor.close();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_edit_activity_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Up navigation
            case android.R.id.home:
                // Hint: singleTop
                NavUtils.navigateUpFromSameTask(this);

                return true;

            // Remove button
            case R.id.action_remove:

                // MODE NEW
                if (MainActivity.EXTRA_MESSAGE_MODE_NEW.equals(mMode)) {
                    DiscardRemoveDialogFragment dialog = new DiscardRemoveDialogFragment();
                    dialog.show(getFragmentManager(), DIALOG_DISCARD);
                }

                // MODE EDIT
                else {
                    DiscardRemoveDialogFragment dialog = new DiscardRemoveDialogFragment();
                    dialog.show(getFragmentManager(), DIALOG_REMOVE);
                }

                return true;

            // Accept button
            case R.id.action_accept:

                // MODE NEW
                if (MainActivity.EXTRA_MESSAGE_MODE_NEW.equals(mMode)) {
                    add();
                }

                // MODE EDIT
                else {
                    modify();
                }

                return true;

            // Default
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        // DIALOG DISCARD
        if (DIALOG_DISCARD.equals(dialog.getTag())) {
            discard();
        }

        // DIALOG REMOVE
        else if (DIALOG_REMOVE.equals(dialog.getTag())) {
            remove();
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Nothing to do
    }

    private void discard() {

        // Result
        setResult(RESULT_CANCELED);
        finish();

    }

    private void remove() {
        SampleDataDbHelper sampleDataDbHelper = SampleDataDbHelper.getInstance(this);

        // Delete from DB
        sampleDataDbHelper.delete(mId);

        // Result
        setResult(RESULT_OK);
        finish();
    }

    private void add() {
        SampleDataDbHelper sampleDataDbHelper = SampleDataDbHelper.getInstance(this);

        // Get master/slave values to insert
        EditText masterEdit = (EditText)findViewById(R.id.new_edit_activity_master_edit);
        EditText slaveEdit = (EditText)findViewById(R.id.new_edit_activity_slave_edit);
        String masterValue = masterEdit.getText().toString();
        String slaveValue = slaveEdit.getText().toString();

        // Insert into DB
        sampleDataDbHelper.insert(masterValue, slaveValue);

        // Result
        setResult(RESULT_OK);
        finish();
    }

    private void modify() {
        SampleDataDbHelper sampleDataDbHelper = SampleDataDbHelper.getInstance(this);

        // Get master/slave values to update
        EditText masterEdit = (EditText)findViewById(R.id.new_edit_activity_master_edit);
        EditText slaveEdit = (EditText)findViewById(R.id.new_edit_activity_slave_edit);
        String masterValue = masterEdit.getText().toString();
        String slaveValue = slaveEdit.getText().toString();

        // Update DB
        sampleDataDbHelper.update(mId, masterValue, slaveValue);

        // Result
        setResult(RESULT_OK);
        finish();
   }

}