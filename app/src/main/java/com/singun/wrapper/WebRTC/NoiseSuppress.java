package com.singun.wrapper.WebRTC;

/**
 * Created by singun on 2017/3/3 0003.
 */

class NoiseSuppress {
    private int mHandle;

    public boolean init(int mode, int sampleRate) {
        mHandle = initNoiseSuppress(mode, sampleRate);
        return mHandle != 0;
    }

    public short[] process(short[] data, int length) {
        return processNoiseSuppress(mHandle, data, length);
    }

    public void release() {
        releaseNoiseSuppress(mHandle);
        mHandle = 0;
    }

    /**
     * 初始化降噪设置
     * @param sampleRate 采样率
     * @return 是否初始化成功
     */
    private native int initNoiseSuppress(int mode, int sampleRate);

    /**
     * 处理降噪
     * @param data
     * @return
     */
    private native short[] processNoiseSuppress(int handle, short[] data, int length);

    private native void releaseNoiseSuppress(int handle);
}
