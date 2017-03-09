package com.singun.wrapper.WebRTC;

/**
 * Created by singun on 2017/3/3 0003.
 */

class NoiseSuppress {
    public static final int NS_MODE_LEVEL_0 = 0; // |mode| = 0 is mild (6dB)
    public static final int NS_MODE_LEVEL_1 = 1; // |mode| = 1 is medium (10dB)
    public static final int NS_MODE_LEVEL_2 = 2; // |mode| = 2 is aggressive (15dB).
    public static final int NS_MODE_LEVEL_3 = 3;

    private int mHandle;

    public boolean init(int sampleRate) {
        mHandle = initNoiseSuppress(sampleRate);
        setMode(NS_MODE_LEVEL_2);
        return mHandle != 0;
    }

    public void setMode(int mode) {
        setNoiseSuppressMode(mHandle, mode);
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
    private native int initNoiseSuppress(int sampleRate);

    /**
     * @param mode 模式 0 1 2 3，级别越高降噪效果越好，一般设置为2
     */
    private native int setNoiseSuppressMode(int handle, int mode);

    /**
     * 处理降噪
     * @param data
     * @return
     */
    private native short[] processNoiseSuppress(int handle, short[] data, int length);

    private native void releaseNoiseSuppress(int handle);
}
