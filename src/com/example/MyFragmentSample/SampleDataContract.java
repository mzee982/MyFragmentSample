package com.example.MyFragmentSample;

import android.provider.BaseColumns;

public final class SampleDataContract {

    public SampleDataContract() {}

    public static abstract class SampleData implements BaseColumns {
        public static final String TABLE_NAME = "sampledata";
        public static final String COLUMN_NAME_MASTER = "master";
        public static final String COLUMN_NAME_SLAVE = "slave";
    }

}
