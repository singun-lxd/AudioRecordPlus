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

    jsize arrLen = env->GetArrayLength(sample);

    jshort *sam = env->GetShortArrayElements(sample, 0);

    short in_sample[arrLen];
    for(int i=0; i<arrLen; i++){
        in_sample[i] = sam[i];
    }

    ns_wrapper* wrapper = (ns_wrapper*) handle;
    wrapper->ns_proc(in_sample, length);

    env->ReleaseShortArrayElements(sample, sam, 0);

    return sample;
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
