package com.singun.audiorecordplus;

import android.content.Context;
import android.os.Environment;
import android.view.Window;

import com.singun.media.audio.AudioConfig;
import com.singun.media.audio.AudioRecordPlayer;
import com.singun.wrapper.WebRTC.WebRTCWrapper;

import java.io.File;

/**
 * Created by singun on 2017/3/2 0002.
 */

public class AudioRecordPlayerPlus extends AudioRecordPlayer {
    private WebRTCWrapper mWebRTCWrapper;
    private boolean mNoiseProcessEnabled;

    public AudioRecordPlayerPlus(Context context, Window window) {
        super(context, window);
    }

    public void setNoiseProcessEnabled(boolean enabled) {
        mNoiseProcessEnabled = enabled;
    }

    public boolean isNoiseProcessEnabled() {
        return mNoiseProcessEnabled;
    }

    @Override
    protected void updateAudioConfig(AudioConfig config) {
        config.audioDirPath = new File(Environment.getExternalStorageDirectory(), "record").getAbsolutePath();
        config.audioName = "testAudio";

        initWebRTC(config);
    }

    private void initWebRTC(AudioConfig config) {
        mWebRTCWrapper = new WebRTCWrapper();
        mWebRTCWrapper.init(config.sampleRateInHz);
    }

    @Override
    protected void processAudioData(AudioConfig audioConfig, int length) {
        if (mNoiseProcessEnabled && mWebRTCWrapper != null) {
            byte[] bufferOut = new byte[length];
            System.arraycopy(audioConfig.audioDataIn, 0, bufferOut, 0, length);
            mWebRTCWrapper.processNoise(bufferOut);
            audioConfig.audioDataOut = bufferOut;
        } else {
            super.processAudioData(audioConfig, length);
        }
    }

    @Override
    public void release() {
        super.release();

        mWebRTCWrapper.release();
    }
}
