package com.singun.wrapper.WebRTC;

/**
 * Created by singun on 2017/3/9 0009.
 */

public class ProcessorConfig {
    public static final int NS_MODE_LEVEL_0 = 0; // |mode| = 0 is mild (6dB)
    public static final int NS_MODE_LEVEL_1 = 1; // |mode| = 1 is medium (10dB)
    public static final int NS_MODE_LEVEL_2 = 2; // |mode| = 2 is aggressive (15dB).
    public static final int NS_MODE_LEVEL_3 = 3;

    private static final int AGC_DB_DEFAULT = 20;
    private static final int AGC_DBFS_DEFAULT = 3;

    public int nsMode = NS_MODE_LEVEL_2;
    public int agcDb = AGC_DB_DEFAULT;
    public int agcDbfs = AGC_DBFS_DEFAULT;

    public void update(ProcessorConfig config) {
        nsMode = config.nsMode;
        agcDb = config.agcDb;
        agcDbfs = config.agcDbfs;
    }
}
