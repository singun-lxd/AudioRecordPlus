package com.singun.wrapper.WebRTC;

public class EchoCancel {
    private int mHandle;

    public boolean init(int sampleRate) {
        mHandle = initEchoCancel(sampleRate);
        return mHandle != 0;
    }

    public short[] process(short[] nearendNoisy ,short[] nearendClean, int length) {
        return processEchoCancel(mHandle, nearendNoisy, nearendClean, length);
    }

    public void release() {
        releaseEchoCancel(mHandle);
        mHandle = 0;
    }

    /**
     * 初始化消除回声
     * @param sampleRate 采样率
     * @return 句柄
     */
    private native int initEchoCancel(int sampleRate);

    /**
     * 处理消除回声
     * @param handle 句柄
     * @param nearendNoisy 未降噪的数据
     * @param nearendClean 降噪过的数据
     * @return
     */
    private native short[] processEchoCancel(int handle, short[] nearendNoisy ,short[] nearendClean, int length);

    private native int releaseEchoCancel(int handle);
}
