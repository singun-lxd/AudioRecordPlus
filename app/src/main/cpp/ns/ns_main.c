#include "ns_main.h"
#include "audio_ns.h"
#include "noise_suppression.h"

#ifdef __cplusplus
extern "C" {
#endif

void innerProcess(int handle, short in_sample[], short out_sample[], int length){

    int curPosition = 0;

    while(curPosition < length){

        audio_ns_process((int) handle, in_sample + curPosition, out_sample + curPosition);

        curPosition += 160;

    }

}

JNIEXPORT jint JNICALL
Java_com_singun_wrapper_WebRTC_NoiseSuppress_initNoiseSuppress(JNIEnv *env, jobject instance, jint sample_rate) {

    NsHandle* handle = (NsHandle *) audio_ns_init(sample_rate);

    return handle;
}

JNIEXPORT jshortArray JNICALL
Java_com_singun_wrapper_WebRTC_NoiseSuppress_processNoiseSuppress(JNIEnv *env, jobject instance, jint handle, jshortArray sample) {

    jsize length = (*env)->GetArrayLength(env, sample);

    jshort *sam = (*env)->GetShortArrayElements(env, sample, 0);

    short in_sample[length];
    for(int i=0; i<length; i++){
        in_sample[i] = sam[i];
    }

    innerProcess(handle, in_sample, sam, length);

    (*env)->ReleaseShortArrayElements(env, sample, sam, 0);

    return sample;
}

JNIEXPORT void JNICALL
Java_com_singun_wrapper_WebRTC_NoiseSuppress_releaseNoiseSuppress(JNIEnv *env, jobject instance, jint handle) {

    if(handle){
        audio_ns_destroy((int) handle);
    }

}
#ifdef __cplusplus
}
#endif
