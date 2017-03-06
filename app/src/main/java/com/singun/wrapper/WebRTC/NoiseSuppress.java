package com.singun.wrapper.WebRTC;

/**
 * Created by singun on 2017/3/3 0003.
 */

class NoiseSuppress {
    private int mHandle;

    public boolean init(int sampleRate, int mode) {
        mHandle = initNoiseSuppress(sampleRate, mode);
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
     * @param mode 模式 0 1 2 3，级别越高降噪效果越好，一般设置为2
     * @return 是否初始化成功
     */
    private native int initNoiseSuppress(int sampleRate, int mode);

    /**
     * 处理降噪
     * @param data
     * @return
     */
    private native short[] processNoiseSuppress(int handle, short[] data, int length);

    private native void releaseNoiseSuppress(int handle);
}
