//
// Created by singun on 2017/3/6 0006.
//

#include <stdio.h>
#include <string.h>
#include "noise_suppression.h"
#include "ns_wrapper.h"

ns_wrapper::ns_wrapper() {
    ns         = NULL;
    nsIn       = NULL;
    nsOut      = NULL;
    temp       = NULL;
}

ns_wrapper::~ns_wrapper() {
    if (ns != NULL) {
        WebRtcNs_Free((NsHandle*)ns);
        ns = NULL;
    }
    if (temp != NULL) {
        free(temp);
        temp = NULL;
    }
    free_buffer((void**)nsIn);
    free_buffer((void**)nsOut);
}

void ns_wrapper:: reset_data() {
    int iBand;
    for(iBand = 0; iBand < nBands; iBand++) {
        memset(nsIn[iBand], 0, frameSh*sizeof(float));
        memset(nsOut[iBand], 0, frameSh*sizeof(float));
    }
    memset(temp, 0, BUFFERSIZE*sizeof(short));
}

/*
 *input:
 *    sampleHz 采样率，只支持8k/16k/32k/48k
 *output:
 *    int      0-success，1-fail
 */
int ns_wrapper::ns_init(int nsMode, int sampleHz) {
    if (base_wrapper::init(sampleHz) < 0) {
        return -1;
    }
    int status, iBand;
    if(nsMode < 0 || nsMode > 3) {
        fprintf(stderr, "[NsInit]: only support nsMode 0 1 2 3\n");
        return -1;
    }
    ns = WebRtcNs_Create();
    status = WebRtcNs_Init((NsHandle*)ns, sampleHz);
    if(status != 0) {
        fprintf(stderr, "[NsInit]: failed in WebRtcNs_Init\n");
        return -1;
    }
    status = WebRtcNs_set_policy((NsHandle*)ns, nsMode);
    if(status != 0) {
        fprintf(stderr, "[NsInit]: failed in WebRtcNs_set_policy\n");
        return -1;
    }
    temp  = (short *)malloc(BUFFERSIZE*sizeof(short));
    nsIn  = (float**)malloc(nBands*sizeof(float*));
    nsOut = (float**)malloc(nBands*sizeof(float*));
    for(iBand = 0; iBand < nBands; iBand++) {
        nsIn[iBand]  = (float*)malloc(frameSh*sizeof(float));
        nsOut[iBand] = (float*)malloc(frameSh*sizeof(float));
    }
    return 0;
}

int ns_wrapper::ns_proc(short *output, int pcmLen) {
    reset_data();

    int iFrame, iShort;
    int nFrames = pcmLen / frameSh;               //帧数
    int leftLen = pcmLen % frameSh;               //最后一帧大小
    int onceLen = frameSh;                        //一帧大小
    nFrames = (leftLen > 0) ? nFrames+1 : nFrames;
    for(iFrame = 0; iFrame < nFrames; iFrame++) {
        if(iFrame == nFrames-1 && leftLen != 0) {
            onceLen = leftLen;
        }
        for(iShort = 0; iShort < onceLen; iShort++) {
            nsIn[0][iShort] = (float)temp[iFrame*frameSh+iShort];
        }
        WebRtcNs_Analyze((NsHandle*)ns, nsIn[0]);
        WebRtcNs_Process((NsHandle*)ns, (const float *const *) nsIn, nBands, nsOut);
        for(iShort = 0; iShort < onceLen; iShort++) {
            output[iFrame*frameSh+iShort] = (short)nsOut[0][iShort];
        }
    }
    return 0;
}





