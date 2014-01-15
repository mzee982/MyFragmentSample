package com.example.MyFragmentSample;

import android.R;
import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MasterFragment extends ListFragment {

    private OnMasterSelectedListener mMasterSelectedListener;
    private Cursor mMasterSlaveListCursor;

    //
    public interface OnMasterSelectedListener {
        public void onMasterSelected(int position, long id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mMasterSelectedListener = (OnMasterSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMasterSelectedListener");
        }
    }

    //
    @Override
    public void onResume() {
        super.onResume();

        MainActivity activity = (MainActivity) getActivity();

        if (activity != null) {

            if    (((activity.getLayout() == MainActivity.LAYOUT_PORTRAIT) && MainActivity.FRAGMENT_PORTRAIT_MASTER.equals(getTag()))
                || ((activity.getLayout() == MainActivity.LAYOUT_LANDSCAPE) && !MainActivity.FRAGMENT_PORTRAIT_MASTER.equals(getTag()))
            ) {

                // Setup list view
                SampleDataDbHelper sampleDataDbHelper = SampleDataDbHelper.getInstance(activity);
                mMasterSlaveListCursor = sampleDataDbHelper.queryMasterList();
                SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                        activity,
                        R.layout.simple_list_item_activated_1,
                        mMasterSlaveListCursor,
                        new String[]{SampleDataContract.SampleData.COLUMN_NAME_MASTER},
                        new int[]{R.id.text1},
                        0);
                setListAdapter(simpleCursorAdapter);

                // PORTRAIT LAYOUT
                if ((activity.getLayout() == MainActivity.LAYOUT_PORTRAIT) && (activity.getCurrentPosition() >= 0)) {

                    // Redirect to slave view
                    onListItemClick(null, null, activity.getCurrentPosition(), activity.getCurrentId());
                }

                // LANDSCAPE LAYOUT
                else if (activity.getLayout() == MainActivity.LAYOUT_LANDSCAPE) {
                    getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    getListView().setItemChecked(activity.getCurrentPosition(), true);
                }

            }

        }
    }

    @Override
    public void onPause() {
        if ((mMasterSlaveListCursor != null) && !mMasterSlaveListCursor.isClosed()) {
            mMasterSlaveListCursor.close();
        }

        super.onPause();
    }

    //
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //super.onListItemClick(l, v, position, id);

        // Notify the parent activity of selected item
        mMasterSelectedListener.onMasterSelected(position, id);

        MainActivity activity = (MainActivity) getActivity();

        // LANDSCAPE LAYOUT
        if ((activity != null) && (activity.getLayout() == MainActivity.LAYOUT_LANDSCAPE)) {
            getListView().setItemChecked(position, true);
        }
    }

}
