package com.singun.media.audio;

/**
 * Created by singun on 2017/3/1 0001.
 */

public class AudioConfig {
    public int streamType;
    public int audioSource;
    public int sampleRateInHz;
    public int channelInConfig;
    public int channelOutConfig;
    public int audioFormat;

    public short[] audioCache;
    public String filePath;
}
