package com.singun.wrapper.WebRTC;

/**
 * Created by singun on 2017/3/6 0006.
 */

public class GainControl {
    private int mHandle;

    public boolean init(int sampleRate, int db, int dbfs) {
        mHandle = initGainControl(sampleRate, db, dbfs);
        return mHandle != 0;
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
     * @param db    增益倍数，0~31，越大声音越大，一般设为20
     * @param dbfs  相对于full scale的db，0~31，越小声音越大，一般设为3
     * @return 是否初始化成功
     */
    private native int initGainControl(int sampleRate, int db, int dbfs);

    /**
     * 处理增益
     * @param data
     * @return
     */
    private native short[] processGainControl(int handle, short[] data, int length);

    private native void releaseGainControl(int handle);
}
