package com.singun.wrapper.WebRTC;

/**
 * Created by singun on 2017/3/3 0003.
 */

class NoiseSuppress {
    /**
     * 初始化降噪设置
     * @param sampleRate 采样率
     * @return 是否初始化成功
     */
    public native boolean initNoiseSuppress(int sampleRate);

    /**
     * 处理降噪
     * @param data
     * @return
     */
    public native boolean processNoiseSuppress(short[] data);

    public native void releaseNoiseSuppress();
}
