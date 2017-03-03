package com.singun.wrapper.WebRTC;

import android.util.Log;

/**
 * Created by singun on 17/3/3 0001.
 */

public class WebRTCWrapper {
    private boolean mInit;
    private NoiseSuppress mNoiseSuppress;

    public WebRTCWrapper() {
        mInit = false;
        mNoiseSuppress = new NoiseSuppress();
    }

    public boolean init(int sampleRate) {
        boolean libLoaded = loadWebRTC();
        if (!libLoaded) {
            return false;
        }

        boolean noiseInit = mNoiseSuppress.initNoiseSuppress(sampleRate);
        if (!noiseInit) {
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

    public boolean processNoiseSuppress(short[] data) {
        return mNoiseSuppress.processNoiseSuppress(data);
    }

    public void release() {
        mNoiseSuppress.releaseNoiseSuppress();
    }

    public static boolean isSupportNoiseSuppress() {
        return true;
    }

    public static boolean isSupportGainControl() {
        return false;
    }

    public static boolean isSupportEchoCancel() {
        return false;
    }
}
