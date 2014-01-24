package com.example.MyFragmentSample;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MasterFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnMasterSelectedListener mMasterSelectedListener;
    private SimpleCursorAdapter mListAdapter;
    private boolean bRefreshMasterList;

    //
    public interface OnMasterSelectedListener {
        public void onMasterSelected(int position, long id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("MasterFragment.onActivityCreated", "id = " + getId() + ", " + "tag = " + getTag());

        // Initialize the list
        MainActivity activity = (MainActivity) getActivity();

        if (activity != null) {

            if (((activity.getLayout() == MainActivity.LAYOUT_PORTRAIT)
                            && MainActivity.FRAGMENT_PORTRAIT_MASTER.equals(getTag()))
                    || ((activity.getLayout() == MainActivity.LAYOUT_LANDSCAPE)
                            && !MainActivity.FRAGMENT_PORTRAIT_MASTER.equals(getTag()))) {

                // Setup the list view
                mListAdapter = new SimpleCursorAdapter(
                        activity,
                        android.R.layout.simple_list_item_activated_1,
                        null,
                        new String[]{SampleDataContract.SampleData.COLUMN_NAME_MASTER},
                        new int[]{android.R.id.text1},
                        0);
                setListAdapter(mListAdapter);

                // Prepare the loader

                // Already exists then restart
                if (getLoaderManager().getLoader(MainActivity.LOADER_MASTER_LIST) != null) {
                    Log.d("MasterFragment.onActivityCreated", "restartLoader LOADER_MASTER_LIST");
                    getLoaderManager().restartLoader(MainActivity.LOADER_MASTER_LIST, null, this);
                }

                // Not exists then initialize
                else {
                    Log.d("MasterFragment.onActivityCreated", "initLoader LOADER_MASTER_LIST");
                    getLoaderManager().initLoader(MainActivity.LOADER_MASTER_LIST, null, this);
                }

                // PORTRAIT LAYOUT
                if ((activity.getLayout() == MainActivity.LAYOUT_PORTRAIT) && (activity.getCurrentPosition() >= 0)) {

                    // Redirect to slave view
                    Log.d("MasterFragment.onActivityCreated",
                            "Redirect to slave view" + ", " + "position = "
                                    + activity.getCurrentPosition() + ", " + "id = " + activity.getCurrentId());
                    onListItemClick(null, null, activity.getCurrentPosition(), activity.getCurrentId());
                }

            }

        }

        // Master list is up-to-date and/or no need to be refreshed
        bRefreshMasterList = false;

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

    @Override
    public void onStart() {
        super.onStart();

        // Refresh master list if necessary
        if (bRefreshMasterList) {
            Log.d("MasterFragment.onStart",
                    "restartLoader LOADER_MASTER_LIST"
                    + ", " + "id = " + getId()
                    + ", " + "tag = "+ getTag());
            getLoaderManager().restartLoader(MainActivity.LOADER_MASTER_LIST, null, this);
        }

    }

    @Override
    public void onStop() {
        MainActivity activity = (MainActivity) getActivity();

        // Master list have to be refreshed next time
        if (((activity.getLayout() == MainActivity.LAYOUT_PORTRAIT)
                        && MainActivity.FRAGMENT_PORTRAIT_MASTER.equals(getTag()))
                || ((activity.getLayout() == MainActivity.LAYOUT_LANDSCAPE)
                        && !MainActivity.FRAGMENT_PORTRAIT_MASTER.equals(getTag()))) {

            Log.d("MasterFragment.onStop",
                    "bRefreshMasterList = true"
                    + ", " + "id = " + getId()
                    + ", " + "tag = "+ getTag());

            bRefreshMasterList = true;
        }

        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d("MasterFragment.onCreateLoader",
                "queryType = " + SampleDataDbHelper.QUERY_TYPE_MASTER_LIST + ", " + "slaveId = -1");

        SampleDataDbHelper sampleDataDbHelper = SampleDataDbHelper.getInstance(getActivity());
        SampleDataCursorLoader loader =
                new SampleDataCursorLoader(getActivity(),
                        sampleDataDbHelper,
                        SampleDataDbHelper.QUERY_TYPE_MASTER_LIST, -1L);

        Log.d("MasterFragment.onCreateLoader", "id = " + loader.getId());

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d("MasterFragment.onLoadFinished", "id = " + cursorLoader.getId());

        if (mListAdapter != null) {
            mListAdapter.swapCursor(cursor);

            MainActivity activity = (MainActivity) getActivity();

            // LANDSCAPE LAYOUT
            if ((activity != null) && (activity.getLayout() == MainActivity.LAYOUT_LANDSCAPE)) {
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                getListView().setItemChecked(activity.getCurrentPosition(), true);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d("MasterFragment.onLoaderReset", "id = " + cursorLoader.getId());

        if (mListAdapter != null) {
            mListAdapter.swapCursor(null);
        }
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
