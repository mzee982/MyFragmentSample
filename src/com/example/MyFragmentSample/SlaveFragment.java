package com.example.MyFragmentSample;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SlaveFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView mSlaveFragmentTextView;
    private boolean bRefreshSlaveValue;


    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);

        Log.d("SlaveFragment.onCreateView", "id = " + getId() + ", " + "tag = " + getTag());

        // Inflate the layout
        return inflater.inflate(R.layout.slave_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("SlaveFragment.onActivityCreated", "id = " + getId() + ", " + "tag = " + getTag());

        MainActivity activity = (MainActivity) getActivity();

        if (activity != null) {

            if (((activity.getLayout() == MainActivity.LAYOUT_PORTRAIT)
                            && MainActivity.FRAGMENT_PORTRAIT_SLAVE.equals(getTag()))
                    || ((activity.getLayout() == MainActivity.LAYOUT_LANDSCAPE)
                            && !MainActivity.FRAGMENT_PORTRAIT_SLAVE.equals(getTag()))) {

                // Find the proper TextView for the layout

                // LANDSCAPE LAYOUT
                if (activity.getLayout() == MainActivity.LAYOUT_LANDSCAPE) {
                    mSlaveFragmentTextView = (TextView) activity.findViewById(R.id.slave_fragment);
                }

                // PORTRAIT LAYOUT
                else {
                    mSlaveFragmentTextView = (TextView) activity.findViewById(R.id.slave_fragment_text_view);
                }

                // Prepare the loader

                // Already exists then restart
                if (getLoaderManager().getLoader(MainActivity.LOADER_SLAVE) != null) {
                    Log.d("SlaveFragment.onActivityCreated", "restartLoader LOADER_SLAVE");
                    getLoaderManager().restartLoader(MainActivity.LOADER_SLAVE, null, this);
                }

                // Not exists then initialize
                else {
                    Log.d("SlaveFragment.onActivityCreated", "initLoader LOADER_SLAVE");
                    getLoaderManager().initLoader(MainActivity.LOADER_SLAVE, null, this);
                }

            }

        }

        // Slave fragment is up-to-date and/or no need to be refreshed
        bRefreshSlaveValue = false;

    }

    @Override
    public void onStart() {
        super.onStart();

        // Refresh slave fragment if necessary
        if (bRefreshSlaveValue) {
            Log.d("SlaveFragment.onStart", "updateSlaveView" + ", " + "id = " + getId() + ", " + "tag = " + getTag());
            updateSlaveView();
        }

    }

    @Override
    public void onStop() {
        MainActivity activity = (MainActivity) getActivity();

        // Slave fragment have to be refreshed next time
        if (((activity.getLayout() == MainActivity.LAYOUT_PORTRAIT)
                        && MainActivity.FRAGMENT_PORTRAIT_SLAVE.equals(getTag()))
                || ((activity.getLayout() == MainActivity.LAYOUT_LANDSCAPE)
                        && !MainActivity.FRAGMENT_PORTRAIT_SLAVE.equals(getTag()))) {

            Log.d("SlaveFragment.onStop",
                    "bRefreshSlaveValue = true"
                    + ", " + "id = " + getId()
                    + ", " + "tag = "+ getTag());

            bRefreshSlaveValue = true;
        }

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d("SlaveFragment.onDestroyView", "id = " + getId() + ", " + "tag = " + getTag());

        // Release
        mSlaveFragmentTextView = null;

        super.onDestroyView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d("SlaveFragment.onCreateLoader",
                "queryType = " + SampleDataDbHelper.QUERY_TYPE_SLAVE + ", "
                        + "slaveId = " + ((MainActivity) getActivity()).getCurrentId());

        SampleDataDbHelper sampleDataDbHelper = SampleDataDbHelper.getInstance(getActivity());
        SampleDataCursorLoader loader =
                new SampleDataCursorLoader(
                    getActivity(),
                    sampleDataDbHelper,
                    SampleDataDbHelper.QUERY_TYPE_SLAVE,
                    ((MainActivity) getActivity()).getCurrentId());

        Log.d("SlaveFragment.onCreateLoader", "id = " + loader.getId());

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d("SlaveFragment.onLoadFinished", "id = " + cursorLoader.getId());

        MainActivity activity = (MainActivity) getActivity();

        if (activity != null) {
            Log.d("SlaveFragment.onLoadFinished", "cursor.getCount() = " + cursor.getCount());

            // Set the text view content
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String slaveValue = cursor.getString(
                        cursor.getColumnIndexOrThrow(SampleDataContract.SampleData.COLUMN_NAME_SLAVE));

                mSlaveFragmentTextView.setText(slaveValue);
            }

            // Clear the text view content
            else {
                mSlaveFragmentTextView.setText("");
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // Nothing to do
        Log.d("SlaveFragment.onLoaderReset", "id = " + cursorLoader.getId());
    }

    //
    public void updateSlaveView() {
        Log.d("SlaveFragment.updateSlaveView",
                "restartLoader LOADER_SLAVE" + ", " + "id = " + getId() + ", " + "tag = " + getTag());

        getLoaderManager().restartLoader(MainActivity.LOADER_SLAVE, null, this);
    }

}
