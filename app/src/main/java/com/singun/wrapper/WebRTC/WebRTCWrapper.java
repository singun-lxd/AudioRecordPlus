package com.singun.wrapper.WebRTC;

import android.util.Log;

/**
 * Created by singun on 17/3/3 0001.
 */

public class WebRTCWrapper {
    private static final int NS_MODE = 2;

    private boolean mInit;
    private NoiseSuppress mNoiseSuppress;
    private EchoCancel mEchoCancel;

    public WebRTCWrapper() {
        mInit = false;
        mNoiseSuppress = new NoiseSuppress();
        mEchoCancel = new EchoCancel();
    }

    public boolean init(int sampleRate) {
        boolean libLoaded = loadWebRTC();
        if (!libLoaded) {
            return false;
        }

        boolean noiseInit = mNoiseSuppress.init(NS_MODE, sampleRate);
        if (!noiseInit) {
            return false;
        }
        boolean echoCancel = mEchoCancel.init(sampleRate);
        if (!echoCancel) {
            return false;
        }

        mInit = true;
        return true;
    }

    public boolean isInit() {
        return mInit;
    }

    private boolean loadWebRTC() {
        boolean libLoaded = false;
        try {
            System.loadLibrary("webrtc");
            libLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            Log.e("TAG", "Couldn't load lib:   - " + e.getMessage());
        }
        return libLoaded;
    }

    public short[] processNoiseSuppress(short[] data, int length) {
        return mNoiseSuppress.process(data, length);
    }

    public short[] processEchoCancel(short[] nearendNoisy ,short[] nearendClean) {
        return mEchoCancel.process(nearendNoisy, nearendClean);
    }

    public void release() {
        mNoiseSuppress.release();
        mEchoCancel.release();
    }

    public static boolean isSupportNoiseSuppress() {
        return true;
    }

    public static boolean isSupportGainControl() {
        return false;
    }

    public static boolean isSupportEchoCancel() {
        return true;
    }
}
