package com.singun.wrapper.WebRTC;

import android.util.Log;

/**
 * Created by singun on 17/3/3 0001.
 */

public class WebRTCWrapper {
    private static final int NS_MODE = 2;
    private static final int AGC_DB = 20;
    private static final int AGC_DBFS = 3;

    private boolean mInit;
    private NoiseSuppress mNoiseSuppress;
//    private EchoCancel mEchoCancel;
    private GainControl mGainControl;

    public WebRTCWrapper() {
        mInit = false;
        mNoiseSuppress = new NoiseSuppress();
//        mEchoCancel = new EchoCancel();
        mGainControl = new GainControl();
    }

    public boolean init(int sampleRate) {
        boolean libLoaded = loadWebRTC();
        if (!libLoaded) {
            return false;
        }

        boolean noiseInit = mNoiseSuppress.init(sampleRate, NS_MODE);
        if (!noiseInit) {
            return false;
        }
//        boolean echoCancel = mEchoCancel.init(sampleRate);
//        if (!echoCancel) {
//            return false;
//        }
        boolean gainInit = mGainControl.init(sampleRate, AGC_DB, AGC_DBFS);
        if (!gainInit) {
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
        return null;//mEchoCancel.process(nearendNoisy, nearendClean);
    }

    public short[] processGainControl(short[] data, int length) {
        return mGainControl.process(data, length);
    }

    public void release() {
        mNoiseSuppress.release();
//        mEchoCancel.release();
    }

    public static boolean isSupportNoiseSuppress() {
        return true;
    }

    public static boolean isSupportGainControl() {
        return true;
    }

    public static boolean isSupportEchoCancel() {
        return false;
    }
}
