package com.singun.media.audio;

/**
 * Created by singun on 2017/3/1 0001.
 */

public class AudioConfig {
    public AudioConfig() {

    }

    public AudioConfig(AudioConfig audioConfig) {
        streamType = audioConfig.streamType;
        audioSource = audioConfig.audioSource;
        sampleRateInHz = audioConfig.sampleRateInHz;
        channelInConfig = audioConfig.channelInConfig;
        channelOutConfig = audioConfig.channelOutConfig;
        audioFormat = audioConfig.audioFormat;
        sessionId = audioConfig.sessionId;
        audioDataSize = audioConfig.audioDataSize;
        audioDataIn = audioConfig.audioDataIn;
        audioDataOut = audioConfig.audioDataOut;
        audioDirPath = audioConfig.audioDirPath;
        audioName = audioConfig.audioName;
    }

    public int streamType;
    public int audioSource;
    public int sampleRateInHz;
    public int channelInConfig;
    public int channelOutConfig;
    public int audioFormat;

    public int sessionId;
    public int audioDataSize;
    public short[] audioDataIn;
    public short[] audioDataOut;
    public String audioDirPath;
    public String audioName;
}
