package com.singun.media.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.Window;

import com.singun.media.audio.processor.AudioProcessConfig;
import com.singun.media.audio.processor.AudioProcessor;

/**
 * Created by singun on 2017/3/1 0001.
 */

public class AudioRecordPlayer {
    private static final int AUDIO_MODE = AudioManager.MODE_IN_COMMUNICATION;

    private AudioManager mAudioManager;
    private Window mCurrentWindow;

    private ExtraAudioRecorder mAudioRecorder;
    private MiniAudioTrack mAudioTrack;
    private AudioProcessor mAudioProcessor;
    private AudioConfig mAudioConfig;
    private Thread mRecordPlayThread;

    protected AudioProcessListener mListener;

    private int mOldMode;
    private boolean mWorking;
    private boolean mReleased;
    private boolean mTrackEnabled;

    public AudioRecordPlayer(Context context, Window window) {
        this(context, window, false);
    }

    public AudioRecordPlayer(Context context, Window window, boolean trackEnabled) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mCurrentWindow = window;
        mReleased = false;
        mTrackEnabled = trackEnabled;

        init();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void init() {
        if (mCurrentWindow != null) {
            mCurrentWindow.setVolumeControlStream(AUDIO_MODE);
        }

        mAudioConfig = new AudioConfig();
        mAudioConfig.streamType = AudioManager.MODE_IN_COMMUNICATION;
        mAudioConfig.audioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
        mAudioConfig.sampleRateInHz = 16000;
        mAudioConfig.channelInConfig = AudioFormat.CHANNEL_IN_MONO;
        mAudioConfig.channelOutConfig = AudioFormat.CHANNEL_OUT_MONO;
        mAudioConfig.audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        updateAudioConfig(mAudioConfig);

        mAudioRecorder = new ExtraAudioRecorder(mAudioConfig);
        mAudioTrack = new MiniAudioTrack(mAudioConfig);

        mAudioConfig.audioDataIn = new short[mAudioConfig.audioDataSize];

        AudioProcessConfig audioProcessConfig = new AudioProcessConfig();
        audioProcessConfig.sessionId = mAudioConfig.sessionId;
        audioProcessConfig.noiseSuppress = true;
        audioProcessConfig.echoCancel = true;
        audioProcessConfig.gainControl = true;

        updateAudioProcessConfig(audioProcessConfig);
        mAudioProcessor = new AudioProcessor(mAudioConfig, audioProcessConfig);
        mAudioRecorder.setAudioProcessor(mAudioProcessor);

        mRecordPlayThread = new AudioRecordPlayThread();
        mRecordPlayThread.start();
    }

    protected void updateAudioConfig(AudioConfig config) {

    }

    protected void updateAudioProcessConfig(AudioProcessConfig audioProcessConfig) {

    }

    public void setAudioProcessListener(AudioProcessListener listener) {
        mListener = listener;
    }

    public void setSpeakerOn(boolean speakerOn) {
        mAudioManager.setSpeakerphoneOn(speakerOn);
    }

    public boolean isSpeakerOn() {
        return mAudioManager.isSpeakerphoneOn();
    }

    public void setTrackEnabled(boolean trackEnabled) {
        if (mTrackEnabled == trackEnabled) {
            return;
        }
        this.mTrackEnabled = trackEnabled;
        if (trackEnabled) {
            mAudioTrack.startPlaying();
        } else {
            mAudioTrack.stop();
        }
    }

    public boolean isTrackEnabled() {
        return mTrackEnabled;
    }

    public void setTrackVolume(float volume) {
        mAudioTrack.setVolume(volume);
    }


    public void setNoiseSuppressEnabled(boolean enabled) {
        mAudioProcessor.setNoiseSuppressEnabled(enabled);
    }

    public boolean isNoiseSuppressEnabled() {
        return mAudioProcessor.isNoiseSuppressEnabled();
    }

    public void setGainControlEnabled(boolean enabled) {
        mAudioProcessor.setGainControlEnabled(enabled);
    }

    public boolean isGainControlEnabled() {
        return mAudioProcessor.isGainControlEnabled();
    }

    public void setEchoCancelEnabled(boolean enabled) {
        mAudioProcessor.setEchoCancelEnabled(enabled);
    }

    public boolean isEchoCancelEnabled() {
        return mAudioProcessor.isEchoCancelEnabled();
    }

    public void startWorking() {
        if (mWorking) {
            return;
        }
        mOldMode = mAudioManager.getMode();
        mAudioManager.setMode(AUDIO_MODE);

        mAudioRecorder.startRecording();
        if (mTrackEnabled) {
            mAudioTrack.startPlaying();
        }

        mWorking = true;
    }

    public void stop() {
        if (!mWorking) {
            return;
        }
        mAudioManager.setMode(mOldMode);
        mOldMode = 0;

        mAudioRecorder.stop();
        if (mTrackEnabled) {
            mAudioTrack.stop();
        }

        mWorking = false;
    }

    public boolean isWorking() {
        return mWorking;
    }

    public void release() {
        mReleased = true;
        mAudioRecorder.release();
        mAudioTrack.release();
        mAudioProcessor.release();
        mAudioRecorder = null;
        mAudioTrack = null;
        mAudioProcessor = null;
        mCurrentWindow = null;
        mAudioManager = null;
    }

    protected void processAudioData(AudioConfig audioConfig, int length) {
        if (!mAudioProcessor.processAudioData(audioConfig, length)) {
            audioConfig.audioDataOut = audioConfig.audioDataIn;
        }
    }

    private class AudioRecordPlayThread extends Thread {
        public AudioRecordPlayThread() {
            super("AudioRecordPlayThread");
//            setPriority();
        }

        @Override
        public void run() {
            while (!mReleased) {
                int length = 0;
                if (mAudioRecorder != null && mAudioRecorder.isRecording()) {
                    length = mAudioRecorder.readAudioData();
                }
                if (length > 0) {
//                    if (mListener != null) {
//                        mListener.onAudioDataProcessed(mAudioConfig.audioDataOut);
//                    }
                    if (mTrackEnabled && mAudioTrack != null) {
                        mAudioTrack.writeAudioData(length);
                    }
                }
            }
        }
    }

    public interface AudioProcessListener {
        void onAudioDataProcessed(byte[] audioData);
    }
}
