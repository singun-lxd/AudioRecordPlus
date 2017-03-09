package com.singun.wrapper.WebRTC;

import android.util.Log;

/**
 * Created by singun on 17/3/3 0001.
 */

public class WebRTCWrapper {
    private boolean mInit;
    private ProcessorConfig mConfig;
    private NoiseSuppress mNoiseSuppress;
    private EchoCancel mEchoCancel;
    private GainControl mGainControl;

    public WebRTCWrapper() {
        mInit = false;
        mConfig = new ProcessorConfig();
        mNoiseSuppress = new NoiseSuppress();
        mEchoCancel = new EchoCancel();
        mGainControl = new GainControl();
    }

    public boolean init(int sampleRate) {
        boolean libLoaded = loadWebRTC();
        if (!libLoaded) {
            return false;
        }

        boolean noiseInit = mNoiseSuppress.init(sampleRate);
        if (!noiseInit) {
            return false;
        }
        mNoiseSuppress.setMode(mConfig.nsMode);

        boolean echoCancel = mEchoCancel.init(sampleRate);
        if (!echoCancel) {
            return false;
        }

        boolean gainInit = mGainControl.init(sampleRate);
        if (!gainInit) {
            return false;
        }
        mGainControl.setConfig(mConfig.agcDb, mConfig.agcDbfs);

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

    public void setNoiseSuppressMode(int mode) {
        mConfig.nsMode = mode;
        mNoiseSuppress.setMode(mode);
    }

    public void setGainControlConfig(int db, int dbfs) {
        mConfig.agcDb = db;
        mConfig.agcDbfs = dbfs;
        mGainControl.setConfig(db, dbfs);
    }

    public ProcessorConfig getConfig() {
        return mConfig;
    }

    public short[] processNoiseSuppress(short[] data, int length) {
        short[] dataOut = mNoiseSuppress.process(data, length);
        if (dataOut == null) {
            dataOut = data;
        }
        return dataOut;
    }

    public short[] processEchoCancel(short[] nearendNoisy ,short[] nearendClean, int length) {
        short[] dataOut = mEchoCancel.process(nearendNoisy, nearendClean, length);
        if (dataOut == null) {
            dataOut = nearendClean == null ? nearendNoisy : nearendClean;
        }
        return dataOut;
    }

    public short[] processGainControl(short[] data, int length) {
        short[] dataOut = mGainControl.process(data, length);
        if (dataOut == null) {
            dataOut = data;
        }
        return dataOut;
    }

    public void release() {
        mNoiseSuppress.release();
        mEchoCancel.release();
        mGainControl.release();
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
