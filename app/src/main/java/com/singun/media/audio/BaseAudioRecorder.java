package com.singun.media.audio;

import com.singun.media.audio.processor.AudioProcessor;

/**
 * Created by singun on 2017/3/8 0008.
 */

public class BaseAudioRecorder {
    private AudioProcessor mAudioProcessor;


    public void setAudioProcessor(AudioProcessor audioProcessor) {
        mAudioProcessor = audioProcessor;
    }

    protected void processAudioData(AudioConfig audioConfig, int length) {
        if (!mAudioProcessor.processAudioData(audioConfig, length)) {
            audioConfig.audioDataOut = audioConfig.audioDataIn;
        }
    }

    protected short[] processAudioData(short[] dataIn, int length) {
        short[] dataOut = mAudioProcessor.processAudioData(dataIn, length);
        if (dataOut == null) {
            dataOut = dataIn;
        }
        return dataOut;
    }
}
