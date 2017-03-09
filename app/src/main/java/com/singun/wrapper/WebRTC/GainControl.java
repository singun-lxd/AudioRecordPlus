package com.singun.wrapper.WebRTC;

/**
 * Created by singun on 2017/3/6 0006.
 */

public class GainControl {
    private static final int AGC_DB_DEFAULT = 20;
    private static final int AGC_DBFS_DEFAULT = 3;

    private int mHandle;

    public boolean init(int sampleRate) {
        mHandle = initGainControl(sampleRate);
        setConfig(AGC_DB_DEFAULT, AGC_DBFS_DEFAULT);
        return mHandle != 0;
    }

    public void setConfig(int db, int dbfs) {
        setGainControlConfig(mHandle, db, dbfs);
    }

    public short[] process(short[] data, int length) {
        return processGainControl(mHandle, data, length);
    }

    public void release() {
        releaseGainControl(mHandle);
        mHandle = 0;
    }

    /**
     * 初始化增益设置
     * @param sampleRate 采样率
     * @return 是否初始化成功
     */
    private native int initGainControl(int sampleRate);

    /**
     * @param db    增益倍数，0~31，越大声音越大，一般设为20
     * @param dbfs  相对于full scale的db，0~31，越小声音越大，一般设为3
     */
    private native int setGainControlConfig(int handle, int db, int dbfs);

    /**
     * 处理增益
     * @param data
     * @return
     */
    private native short[] processGainControl(int handle, short[] data, int length);

    private native void releaseGainControl(int handle);
}
