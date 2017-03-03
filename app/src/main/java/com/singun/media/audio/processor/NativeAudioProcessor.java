package com.singun.media.audio.processor;

import com.singun.media.audio.AudioConfig;
import com.singun.wrapper.WebRTC.WebRTCWrapper;

/**
 * Created by singun on 2017/3/3 0003.
 */

class NativeAudioProcessor {
    private WebRTCWrapper mWebRTCWrapper;

    private AudioConfig mAudioConfig;
    private AudioProcessConfig mAudioProcessConfig;

    public NativeAudioProcessor(AudioConfig audioConfig, AudioProcessConfig audioProcessConfig) {
        mAudioConfig = audioConfig;
        mAudioProcessConfig = audioProcessConfig;

        mWebRTCWrapper = new WebRTCWrapper();
        if (mAudioProcessConfig.noiseSuppress && !AndroidAudioProcessorSupport.isSupportNoiseSuppressor()) {
            enableNativeProcessor();
        }
    }

    public void enableNativeProcessor() {
        if (!mWebRTCWrapper.isInit()) {
            mWebRTCWrapper.init(mAudioConfig.sampleRateInHz);
        }
    }

    public boolean processAudioData(AudioConfig audioConfig, int length) {
        if (needProcessData()) {
            short[] bufferOut = new short[length];
            System.arraycopy(audioConfig.audioDataIn, 0, bufferOut, 0, length);

            processData(bufferOut);

            audioConfig.audioDataOut = bufferOut;

            return true;
        } else {
            return false;
        }
    }

    private boolean needProcessData() {
        return (mAudioProcessConfig.noiseSuppress && NativeAudioProcessorSupport.isSupportNoiseSuppressor()) ||
                (mAudioProcessConfig.gainControl && NativeAudioProcessorSupport.isSupportGainControl()) ||
                (mAudioProcessConfig.echoCancel && NativeAudioProcessorSupport.isSupportEchoCanceler());
    }

    private void processData(short[] data) {
        if (mAudioProcessConfig.noiseSuppress) {
            mWebRTCWrapper.processNoiseSuppress(data);
        }
        //// TODO: 2017/3/3 0003 GainControl && EchoCanceler 
    }

    public void release() {
        if (mWebRTCWrapper.isInit()) {
            mWebRTCWrapper.release();
        }
    }
}
