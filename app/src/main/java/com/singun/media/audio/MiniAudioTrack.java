package com.singun.media.audio;

import android.annotation.TargetApi;
import android.media.AudioTrack;
import android.os.Build;

/**
 * Created by singun on 2017/3/1 0001.
 */

public class MiniAudioTrack {
    private AudioConfig mAudioConfig;

    private AudioTrack mAudioTrack;

    private boolean mIsPlaying;

    public MiniAudioTrack(AudioConfig audioConfig) throws IllegalArgumentException {
        mAudioConfig = audioConfig;

        init();
    }

    private void init() throws IllegalArgumentException {
        int minBufferSize = AudioTrack.getMinBufferSize(
                mAudioConfig.sampleRateInHz,
                mAudioConfig.channelOutConfig,
                mAudioConfig.audioFormat);

        mAudioTrack = new AudioTrack(
                mAudioConfig.streamType,
                mAudioConfig.sampleRateInHz,
                mAudioConfig.channelOutConfig,
                mAudioConfig.audioFormat,
                minBufferSize,
                AudioTrack.MODE_STREAM);
    }

    public void startPlaying() {
        if (mIsPlaying) {
            return;
        }
        mAudioTrack.play();

        mIsPlaying = true;
    }

    public void writeAudioData(int length) {
        mAudioTrack.write(mAudioConfig.audioDataOut, 0, length);
    }

    public void stop() {
        if (!mIsPlaying) {
            return;
        }

        mAudioTrack.stop();

        mIsPlaying = false;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setVolume(float volume) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAudioTrack.setVolume(volume);
        } else {
            mAudioTrack.setStereoVolume(volume, volume);
        }
    }

    public void release() {
        mAudioTrack.release();
        mAudioTrack = null;
    }
}
