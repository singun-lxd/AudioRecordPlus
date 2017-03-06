#include <stdio.h>
#include "ns_jni_main.h"
#include "ns_wrapper.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL
Java_com_singun_wrapper_WebRTC_NoiseSuppress_initNoiseSuppress(JNIEnv *env, jobject instance, jint sample_rate, jint mode) {
    ns_wrapper* wrapper = new ns_wrapper();
    int init = wrapper->ns_init(sample_rate, mode);
    if (init != 0) {
        delete wrapper;
        return 0;
    }
    return (int) wrapper;
}

JNIEXPORT jshortArray JNICALL
Java_com_singun_wrapper_WebRTC_NoiseSuppress_processNoiseSuppress(JNIEnv *env, jobject instance, jint handle, jshortArray sample, jint length) {

    jshort *in_sample = env->GetShortArrayElements(sample, 0);

    short out_sample[length];
    ns_wrapper* wrapper = (ns_wrapper*) handle;
    int ret = wrapper->ns_proc(in_sample, out_sample, length);

    env->ReleaseShortArrayElements(sample, in_sample, 0);

    if (ret == 0) {
        jshortArray ret_array = env->NewShortArray(length);
        env->SetShortArrayRegion(ret_array, 0, length, out_sample);

        return ret_array;
    } else {
        return NULL;
    }
}

JNIEXPORT void JNICALL
Java_com_singun_wrapper_WebRTC_NoiseSuppress_releaseNoiseSuppress(JNIEnv *env, jobject instance, jint handle) {

    if (handle) {
        ns_wrapper* wrapper = (ns_wrapper*) handle;
        delete wrapper;
    }
}

#ifdef __cplusplus
}
#endif
