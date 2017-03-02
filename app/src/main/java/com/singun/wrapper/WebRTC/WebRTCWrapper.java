package com.singun.wrapper.WebRTC;

import android.util.Log;

/**
 * Created by hesong on 16/11/17.
 */

public class WebRTCWrapper {

    static {
        try {
            System.loadLibrary("webrtcwrap");
        } catch (UnsatisfiedLinkError e) {
            Log.e("TAG", "Couldn't load lib:   - " + e.getMessage());
        }

    }

    /**
     * 初始化降噪设置
     * @param sampleRate 采样率
     * @return 是否初始化成功
     */
    public native boolean init(int sampleRate);

    /**
     * 处理降噪
     * @param data
     * @return
     */
    public native boolean processNoise(short[] data);

    public native void release();

}
