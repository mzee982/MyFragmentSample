package com.example.MyFragmentSample;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public class SampleDataCursorLoader extends AsyncTaskLoader<Cursor> {
    final ForceLoadContentObserver mObserver;

    SampleDataDbHelper mSampleDataDbHelper;
    String mQueryType;
    long mSlaveId;
    Cursor mCursor;
    CancellationSignal mCancellationSignal;

    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        Log.d("SampleDataCursorLoader.loadInBackground",
                "mQueryType = " + mQueryType + ", " + "mSlaveId = " + mSlaveId);

        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }
        try {
            Cursor cursor;

            // QUERY TYPE MASTER LIST
            if (SampleDataDbHelper.QUERY_TYPE_MASTER_LIST.equals(mQueryType)) {
                cursor = mSampleDataDbHelper.queryMasterList();
            }

            // QUERY TYPE SLAVE
            else if (SampleDataDbHelper.QUERY_TYPE_SLAVE.equals(mQueryType)) {
                cursor = mSampleDataDbHelper.querySlave(mSlaveId);
            }

            // QUERY TYPE Undefined
            else {
                cursor = null;
            }

            if (cursor != null) {
                try {
                    // Ensure the cursor window is filled.
                    cursor.getCount();
                    cursor.registerContentObserver(mObserver);
                } catch (RuntimeException ex) {
                    cursor.close();
                    throw ex;
                }
            }
            return cursor;

        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    public SampleDataCursorLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    public SampleDataCursorLoader(Context context, SampleDataDbHelper sampleDataDbHelper, String queryType, long slaveId) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mSampleDataDbHelper = sampleDataDbHelper;
        mQueryType = queryType;
        mSlaveId = slaveId;
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     *
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix); writer.print("mSampleDataDbHelper="); writer.println(mSampleDataDbHelper);
        writer.print(prefix); writer.print("mQueryType="); writer.println(mQueryType);
        writer.print(prefix); writer.print("mSlaveId="); writer.println(mSlaveId);
        writer.print(prefix); writer.print("mCursor="); writer.println(mCursor);
    }

}
