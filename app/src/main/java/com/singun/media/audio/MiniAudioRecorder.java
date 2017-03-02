package com.singun.media.audio;

import android.media.AudioRecord;

/**
 * Created by singun on 2017/3/1 0001.
 */

public class MiniAudioRecorder {
    private AudioConfig mAudioConfig;

    private AudioRecord mAudioRecord;

    private boolean mIsRecording;

    public MiniAudioRecorder(AudioConfig audioConfig) throws IllegalArgumentException {
        mAudioConfig = audioConfig;

        init();
    }

    private void init() throws IllegalArgumentException {
        int minBufferSize = AudioRecord.getMinBufferSize(
                mAudioConfig.sampleRateInHz,
                mAudioConfig.channelInConfig,
                mAudioConfig.audioFormat);

        mAudioRecord = new AudioRecord(
                mAudioConfig.audioSource,
                mAudioConfig.sampleRateInHz,
                mAudioConfig.channelInConfig,
                mAudioConfig.audioFormat,
                minBufferSize);
    }

    public void startRecording() {
        if (mIsRecording) {
            return;
        }

        mAudioRecord.startRecording();

        mIsRecording = true;
    }

    public int readAudioData() {
        return mAudioRecord.read(mAudioConfig.audioDataIn, 0, mAudioConfig.audioDataIn.length);
    }

    public void stop() {
        if (!mIsRecording) {
            return;
        }

        mAudioRecord.stop();

        mIsRecording = false;
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public int getChannelCount() {
        return mAudioRecord.getChannelCount();
    }

    public int getSampleRate() {
        return mAudioRecord.getSampleRate();
    }

    public void release() {
        mAudioRecord.release();
        mAudioRecord = null;
    }
}
