package com.singun.audiorecordplus;

import android.content.Context;
import android.os.Environment;
import android.view.Window;

import com.singun.media.audio.AudioConfig;
import com.singun.media.audio.AudioRecordPlayer;
import com.singun.media.audio.processor.AudioProcessConfig;
import com.singun.wrapper.WebRTC.WebRTCWrapper;

import java.io.File;

/**
 * Created by singun on 2017/3/2 0002.
 */

public class AudioRecordPlayerPlus extends AudioRecordPlayer {
    public AudioRecordPlayerPlus(Context context, Window window) {
        super(context, window);
    }

    public AudioRecordPlayerPlus(Context context, Window window, boolean trackEnabled) {
        super(context, window, trackEnabled);
    }

    @Override
    protected void updateAudioConfig(AudioConfig config) {
        config.audioDirPath = new File(Environment.getExternalStorageDirectory(), "record").getAbsolutePath();
        config.audioName = "testAudio";
    }

    @Override
    protected void updateAudioProcessConfig(AudioProcessConfig audioProcessConfig) {
        audioProcessConfig.noiseSuppress = true;
        audioProcessConfig.gainControl = false;
        audioProcessConfig.echoCancel = false;
    }
}
