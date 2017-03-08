package com.singun.media.audio.processor;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;

import com.singun.media.audio.AudioConfig;

/**
 * Created by singun on 2017/3/3 0003.
 */

public class AudioProcessor {

    private NoiseSuppressor mNoiseSuppressor;
    private AutomaticGainControl mGainControl;
    private AcousticEchoCanceler mEchoCanceler;
    private NativeAudioProcessor mNativeProcessor;

    private AudioProcessConfig mAudioProcessConfig;

    public AudioProcessor(AudioConfig audioConfig, AudioProcessConfig audioProcessConfig) {
        mAudioProcessConfig = audioProcessConfig;

        init();
        initNative(audioConfig);
    }

    public void init() {
        if (mAudioProcessConfig.sessionId == 0) {
            return;
        }

        if (AndroidAudioProcessorSupport.isSupportNoiseSuppressor()) {
            mNoiseSuppressor = NoiseSuppressor.create(mAudioProcessConfig.sessionId);
            if (mAudioProcessConfig.noiseSuppress) {
                mNoiseSuppressor.setEnabled(true);
            }
        }

        if (AndroidAudioProcessorSupport.isSupportGainControl()) {
            mGainControl = AutomaticGainControl.create(mAudioProcessConfig.sessionId);
            if (mAudioProcessConfig.gainControl) {
                mGainControl.setEnabled(true);
            }
        }

        if (AndroidAudioProcessorSupport.isSupportEchoCanceler()) {
            mEchoCanceler = AcousticEchoCanceler.create(mAudioProcessConfig.sessionId);
            if (mAudioProcessConfig.echoCancel) {
                mEchoCanceler.setEnabled(true);
            }
        }
    }

    public void initNative(AudioConfig audioConfig) {
        mNativeProcessor = new NativeAudioProcessor(audioConfig, mAudioProcessConfig);
    }

    public boolean processAudioData(AudioConfig audioConfig, int length) {
        if (isNativeNoiseSuppressEnabled() || isNativeGainControlEnabled() || isNativeEchoCancellerEnabled()) {
            return mNativeProcessor.processAudioData(audioConfig, length);
        }
        return false;
    }

    public short[] processAudioData(short[] data, int length) {
        if (isNativeNoiseSuppressEnabled() || isNativeGainControlEnabled() || isNativeEchoCancellerEnabled()) {
            return mNativeProcessor.processAudioData(data, length);
        }
        return null;
    }

    private boolean isNativeNoiseSuppressEnabled() {
        return mAudioProcessConfig.noiseSuppress && !AndroidAudioProcessorSupport.isSupportNoiseSuppressor();
    }

    private boolean isNativeGainControlEnabled() {
        return mAudioProcessConfig.gainControl && !AndroidAudioProcessorSupport.isSupportGainControl();
    }

    private boolean isNativeEchoCancellerEnabled() {
        return mAudioProcessConfig.echoCancel && !AndroidAudioProcessorSupport.isSupportEchoCanceler();
    }

    public void setNoiseSuppressEnabled(boolean enabled) {
        mAudioProcessConfig.noiseSuppress = enabled;
        if (AndroidAudioProcessorSupport.isSupportNoiseSuppressor()) {
            mNoiseSuppressor.setEnabled(enabled);
        }
        if (enabled && NativeAudioProcessorSupport.isSupportNoiseSuppressor()) {
            mNativeProcessor.enableNativeProcessor();
        }
    }

    public boolean isNoiseSuppressEnabled() {
        return mAudioProcessConfig.noiseSuppress &&
                isNoiseSuppressSupported();
    }

    public static boolean isNoiseSuppressSupported() {
        return AndroidAudioProcessorSupport.isSupportNoiseSuppressor() ||
                NativeAudioProcessorSupport.isSupportNoiseSuppressor();
    }

    public void setGainControlEnabled(boolean enabled) {
        mAudioProcessConfig.gainControl = enabled;
        if (AndroidAudioProcessorSupport.isSupportGainControl()) {
            mGainControl.setEnabled(enabled);
        }
        if (enabled && NativeAudioProcessorSupport.isSupportGainControl()) {
            mNativeProcessor.enableNativeProcessor();
        }
    }

    public boolean isGainControlEnabled() {
        return mAudioProcessConfig.gainControl &&
                isGainControlSupported();
    }

    public static boolean isGainControlSupported() {
        return AndroidAudioProcessorSupport.isSupportGainControl() ||
                NativeAudioProcessorSupport.isSupportGainControl();
    }

    public void setEchoCancelEnabled(boolean enabled) {
        mAudioProcessConfig.echoCancel = enabled;
        if (AndroidAudioProcessorSupport.isSupportEchoCanceler()) {
            mEchoCanceler.setEnabled(enabled);
        }
        if (enabled && NativeAudioProcessorSupport.isSupportEchoCanceler()) {
            mNativeProcessor.enableNativeProcessor();
        }
    }

    public boolean isEchoCancelEnabled() {
        return mAudioProcessConfig.echoCancel &&
                isEchoCancelSupported();
    }

    public static boolean isEchoCancelSupported() {
        return AndroidAudioProcessorSupport.isSupportEchoCanceler() ||
                NativeAudioProcessorSupport.isSupportEchoCanceler();
    }

    public void release() {
        if (mNoiseSuppressor != null) {
            mNoiseSuppressor.release();
            mNoiseSuppressor = null;
        }
        if (mGainControl != null) {
            mGainControl.release();
            mGainControl = null;
        }
        if (mEchoCanceler != null) {
            mEchoCanceler.release();
            mEchoCanceler = null;
        }
        if (mNativeProcessor != null) {
            mNativeProcessor.release();
            mNativeProcessor = null;
        }
    }
}
