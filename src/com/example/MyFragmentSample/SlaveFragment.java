package com.example.MyFragmentSample;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;

public class SlaveFragment extends Fragment {

    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout
        return inflater.inflate(R.layout.slave_fragment, container, false);
    }

    //
    @Override
    public void onResume() {
        super.onResume();

        MainActivity activity = (MainActivity) getActivity();

        // Refresh detail view content
        if (activity != null) {
            updateSlaveView(activity.getCurrentId());
        }
    }

    //
    public void updateSlaveView(long id) {
        MainActivity activity = (MainActivity) getActivity();
        TextView slaveFragmentTextView;

        if (activity != null) {

            // LANDSCAPE LAYOUT
            if (activity.getLayout() == MainActivity.LAYOUT_LANDSCAPE) {
                slaveFragmentTextView = (TextView) activity.findViewById(R.id.slave_fragment);
            }

            // PORTRAIT LAYOUT
            else {
                slaveFragmentTextView = (TextView) activity.findViewById(R.id.slave_fragment_text_view);
            }

            // Set the content
            if (id >= 0) {

                // Query from DB
                SampleDataDbHelper sampleDataDbHelper = SampleDataDbHelper.getInstance(activity);
                Cursor slaveCursor = sampleDataDbHelper.querySlave(id);

                slaveCursor.moveToFirst();
                String slaveValue = slaveCursor.getString(slaveCursor.getColumnIndexOrThrow(SampleDataContract.SampleData.COLUMN_NAME_SLAVE));

                // Set the text view content
                slaveFragmentTextView.setText(slaveValue);

                // Close DB cursor
                if (!slaveCursor.isClosed()) {
                    slaveCursor.close();
                }
            }

            // Clear the content
            else {

                // Set the text view content
                slaveFragmentTextView.setText("");
            }

        }
    }

}
