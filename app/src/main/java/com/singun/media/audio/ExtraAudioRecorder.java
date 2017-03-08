package com.singun.media.audio;

import com.singun.media.audio.processor.AudioProcessor;

import java.io.File;

import omrecorder.AudioChunk;
import omrecorder.AudioSource;
import omrecorder.ExtraPullTransport;
import omrecorder.OmRecorder;
import omrecorder.Recorder;

/**
 * Created by singun on 2017/3/7 0007.
 */

public class ExtraAudioRecorder extends BaseAudioRecorder implements ExtraPullTransport.ProcessAction {
    private AudioConfig mAudioConfig;
    private AudioSource mAudioSource;
    private Recorder mRecorder;
    private int mCacheLength;
    private short[] mCacheData;

    private boolean mIsRecording;

    public ExtraAudioRecorder(AudioConfig audioConfig) throws IllegalArgumentException {
        mAudioConfig = audioConfig;

        init(audioConfig);
    }

    private void init(AudioConfig audioConfig) {
        mAudioSource = new omrecorder.AudioSource.Smart(
                audioConfig.audioSource,
                audioConfig.audioFormat,
                audioConfig.channelInConfig,
                audioConfig.sampleRateInHz);

        int minBufferSize = mAudioSource.minimumBufferSize();
        mAudioConfig.audioDataSize = minBufferSize / 2;
    }

    private void prepare(AudioConfig audioConfig) throws IllegalArgumentException {
        if (mRecorder == null) {
            mRecorder = OmRecorder.wav(
                    new ExtraPullTransport(mAudioSource, this),
                    new File(audioConfig.audioDirPath, audioConfig.audioName + ".wav"));
        }
    }

    public void startRecording() {
        if (mIsRecording) {
            return;
        }

        prepare(mAudioConfig);
        mRecorder.startRecording();

        mIsRecording = true;
    }

    public int readAudioData() {
        int length;
        synchronized (this) {
            if (mCacheData == null || mCacheLength > mCacheData.length) {
                return 0;
            }
            length = mCacheLength;
            mAudioConfig.audioDataOut = mCacheData;
            mCacheData = null;
            mCacheLength = 0;
        }

        return length;
    }

    public void stop() {
        if (!mIsRecording) {
            return;
        }

        mRecorder.stopRecording();
        mRecorder = null;

        mIsRecording = false;
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public void release() {
        if (mRecorder != null) {
            mRecorder.stopRecording();
            mRecorder = null;
        }
    }

    @Override
    public void processData(AudioChunk audioChunk, int length) {
        synchronized (this) {
            mCacheLength = length;
            short[] dataRecorded = audioChunk.toShorts();
            short[] dataProcessed = processAudioData(dataRecorded, length);
            if (dataRecorded != dataProcessed) {
                for (int i = 0; i < dataRecorded.length; i++) {
                    dataRecorded[i] = dataProcessed[i];
                }
            }
            mCacheData = dataProcessed;
        }
    }
}
