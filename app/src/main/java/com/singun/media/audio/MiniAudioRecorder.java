package com.singun.media.audio;

import android.media.AudioRecord;
import android.os.Build;

/**
 * Created by singun on 2017/3/1 0001.
 */

public class MiniAudioRecorder extends BaseAudioRecorder {
    private AudioFileWriter mAudioFileWriter;
    private AudioConfig mAudioConfig;
    private AudioRecord mAudioRecord;

    private boolean mIsRecording;

    public MiniAudioRecorder(AudioConfig audioConfig) throws IllegalArgumentException {
        mAudioConfig = audioConfig;
        mAudioFileWriter = new AudioFileWriter(mAudioConfig);

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

        mAudioConfig.audioDataSize = minBufferSize / 2;
        if (isSupportRecorderSession()) {
            mAudioConfig.sessionId = mAudioRecord.getAudioSessionId();
        }
    }

    private static boolean isSupportRecorderSession() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public void startRecording() {
        if (mIsRecording) {
            return;
        }

        mAudioFileWriter.createAudioFile(true);
        try {
            mAudioRecord.startRecording();
        } catch (IllegalStateException e) {
            mAudioRecord.release();
            init();
            mAudioRecord.startRecording();
        }

        mIsRecording = true;
    }

    public int readAudioData() {
        int length = mAudioRecord.read(mAudioConfig.audioDataIn, 0, mAudioConfig.audioDataIn.length);
        processAudioData(mAudioConfig, length);
        mAudioFileWriter.saveRecordData(length);
        return length;
    }

    public void stop() {
        if (!mIsRecording) {
            return;
        }

        mAudioRecord.stop();

        mAudioFileWriter.saveAudioFormat(mAudioRecord.getSampleRate(), mAudioRecord.getChannelCount());

        mIsRecording = false;
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public void release() {
        mAudioRecord.release();
        mAudioRecord = null;

        mAudioFileWriter.release();
        mAudioFileWriter = null;
    }
}
