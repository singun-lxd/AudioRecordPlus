package com.singun.media.audio.recorder;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.singun.audiorecordplus.BuildConfig;
import com.singun.media.audio.AudioConfig;

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init(AudioConfig audioConfig) {
        mAudioSource = new omrecorder.AudioSource.Smart(
                audioConfig.audioSource,
                audioConfig.audioFormat,
                audioConfig.channelInConfig,
                audioConfig.sampleRateInHz);

        int minBufferSize = mAudioSource.minimumBufferSize();
        mAudioConfig.audioDataSize = minBufferSize / 2;

        if (isSupportRecorderSession()) {
            audioConfig.sessionId = mAudioSource.audioRecorder().getAudioSessionId();
        }
    }

    private void prepare(AudioConfig audioConfig) throws IllegalArgumentException {
        if (mRecorder == null) {
            checkAndCreateDirectory(audioConfig);
            mRecorder = OmRecorder.wav(
                    new ExtraPullTransport(mAudioSource, this),
                    new File(audioConfig.audioDirPath, audioConfig.audioName + ".wav"));
        }
    }

    private void checkAndCreateDirectory(AudioConfig audioConfig) {
        File dir = new File(audioConfig.audioDirPath);
        if (dir.isFile()) {
            dir.delete();
        }
        if (!dir.isDirectory()) {
            dir.mkdirs();
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
            printDebugData("dataRecorded:", dataRecorded);
            short[] dataProcessed = processAudioData(dataRecorded, length);
            if (dataRecorded != dataProcessed) {
                for (int i = 0; i < length; i++) {
                    dataRecorded[i] = dataProcessed[i];
                }
            }
            printDebugData("dataProcessed:", dataProcessed);
            mCacheData = dataProcessed;
        }
    }

    private void printDebugData(String prefix, short[] audioData) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        String recorded = prefix;
        for (int i = 0; i < 10; i++) {
            recorded += "[";
            recorded += audioData[i];
            recorded += "]";
        }
        recorded += "{";
        recorded += audioData.length;
        recorded += "}";
        Log.e("singun", recorded);
    }
}
