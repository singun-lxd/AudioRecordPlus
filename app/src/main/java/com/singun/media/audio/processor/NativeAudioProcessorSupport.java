package com.singun.media.audio.processor;

import com.singun.wrapper.WebRTC.WebRTCWrapper;

/**
 * Created by singun on 2017/3/3 0003.
 */

public class NativeAudioProcessorSupport {
    public static boolean isSupportNoiseSuppressor() {
        return WebRTCWrapper.isSupportNoiseSuppress();
    }

    public static boolean isSupportGainControl() {
        return WebRTCWrapper.isSupportGainControl();
    }

    public static boolean isSupportEchoCanceler() {
        return WebRTCWrapper.isSupportEchoCancel();
    }
}
