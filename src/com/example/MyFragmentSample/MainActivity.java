package com.example.MyFragmentSample;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity implements MasterFragment.OnMasterSelectedListener {

    //public final static int LAYOUT_UNKNOWN = 0;
    public final static int LAYOUT_PORTRAIT = 1;
    public final static int LAYOUT_LANDSCAPE = 2;
    public final static String ARG_POSITION = "position";
    public final static String ARG_ID = "id";
    public final static String EXTRA_MESSAGE_MODE = "com.example.MyFragmentSample.MODE";
    public final static String EXTRA_MESSAGE_MODE_NEW = "NEW";
    public final static String EXTRA_MESSAGE_MODE_EDIT = "EDIT";
    public final static String EXTRA_MESSAGE_ID = "ID";
    public final static String FRAGMENT_PORTRAIT_MASTER = "FRAGMENT_PORTRAIT_MASTER";
    public final static String FRAGMENT_PORTRAIT_SLAVE = "FRAGMENT_PORTRAIT_SLAVE";
    private final static int REQUEST_CODE_NEW_EDIT = 1;
    public final static int LOADER_MASTER_LIST = 1;
    public final static int LOADER_SLAVE = 2;
    private int mCurrentPosition = -1;
    private long mCurrentId = -1L;

    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore master selection
        if ((savedInstanceState != null) && (savedInstanceState.getInt(ARG_POSITION) >= 0)) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
            mCurrentId = savedInstanceState.getLong(ARG_ID);
        }

        // Set layout
        setContentView(R.layout.main);

        // PORTRAIT LAYOUT
        if (getLayout() == LAYOUT_PORTRAIT) {

            // Find master fragment
            MasterFragment masterFragment = (MasterFragment) getFragmentManager().findFragmentByTag(FRAGMENT_PORTRAIT_MASTER);

            // Not exist yet
            if (masterFragment == null) {

                // Create master fragment
                masterFragment = new MasterFragment();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, masterFragment, FRAGMENT_PORTRAIT_MASTER).commit();

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        MenuItem menuItemNew = menu.findItem(R.id.action_new);
        MenuItem menuItemEdit = menu.findItem(R.id.action_edit);

        // PORTRAIT LAYOUT
        if (getLayout() == LAYOUT_PORTRAIT) {

            // Slave view: enable edit button in action bar
            if (mCurrentPosition >= 0) {
                menuItemNew.setEnabled(false);
                menuItemNew.setVisible(false);
                menuItemEdit.setEnabled(true);
                menuItemEdit.setVisible(true);
            }

            // Master view: enable new button in action bar
            else {
                menuItemNew.setEnabled(true);
                menuItemNew.setVisible(true);
                menuItemEdit.setEnabled(false);
                menuItemEdit.setVisible(false);
            }
        }

        // LANDSCAPE LAYOUT
        else {

            // Slave view active
            if (mCurrentPosition >= 0) {
                menuItemNew.setEnabled(true);
                menuItemNew.setVisible(true);
                menuItemEdit.setEnabled(true);
                menuItemEdit.setVisible(true);
            }

            // Slave view inactive
            else {
                menuItemNew.setEnabled(true);
                menuItemNew.setVisible(true);
                menuItemEdit.setEnabled(false);
                menuItemEdit.setVisible(false);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {

            // New button
            case R.id.action_new:
                intent = new Intent(this, NewEditActivity.class);
                intent.putExtra(EXTRA_MESSAGE_MODE, EXTRA_MESSAGE_MODE_NEW);
                intent.putExtra(EXTRA_MESSAGE_ID, -1L);
                startActivityForResult(intent, REQUEST_CODE_NEW_EDIT);

                break;

            // Edit button
            case R.id.action_edit:
                intent = new Intent(this, NewEditActivity.class);
                intent.putExtra(EXTRA_MESSAGE_MODE, EXTRA_MESSAGE_MODE_EDIT);
                intent.putExtra(EXTRA_MESSAGE_ID, mCurrentId);
                startActivityForResult(intent, REQUEST_CODE_NEW_EDIT);

                break;

            // DEFAULT
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        // PORTRAIT LAYOUT
        if (getLayout() == LAYOUT_PORTRAIT) {

            // Reset master selection
            if (mCurrentPosition >= 0) {
                mCurrentPosition = -1;
                mCurrentId = -1L;

                // Refresh the action bar
                invalidateOptionsMenu();
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Save the current selection
        outState.putInt(ARG_POSITION, mCurrentPosition);
        outState.putLong(ARG_ID, mCurrentId);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_NEW_EDIT) {

            // RESULT OK
            if (resultCode == RESULT_OK) {

                // Slave view active
                if (mCurrentPosition >= 0) {

                    // Reset master selection
                    mCurrentPosition = -1;
                    mCurrentId = -1L;

                    // Refresh the action bar
                    invalidateOptionsMenu();

                    // PORTRAIT LAYOUT
                    if (getLayout() == LAYOUT_PORTRAIT) {

                        // Back to the master fragment
                        getFragmentManager().popBackStack();
                    }
                }

            }

            // RESULT CANCELLED
//            else if (resultCode == RESULT_CANCELED) {
//                // Nothing to do
//            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public long getCurrentId() {
        return mCurrentId;
    }

    public int getLayout() {
        //getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE

        if (findViewById(R.id.fragment_container) != null) {
            return LAYOUT_PORTRAIT;
        }
        else {
            return LAYOUT_LANDSCAPE;
        }
    }

    //
    public void onMasterSelected(int position, long id) {

        mCurrentPosition = position;
        mCurrentId = id;

        // LANDSCAPE LAYOUT
        if (getLayout() == LAYOUT_LANDSCAPE) {
            SlaveFragment slaveFragment = (SlaveFragment) getFragmentManager().findFragmentById(R.id.slave_fragment);

            // Update the content of slave pane
            slaveFragment.updateSlaveView();
        }

        // PORTRAIT LAYOUT
        else {
            SlaveFragment slaveFragment = new SlaveFragment();

            // Replace master to detail fragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, slaveFragment, FRAGMENT_PORTRAIT_SLAVE);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        // Refresh the action bar
        invalidateOptionsMenu();
    }

}
