//
// Created by singun on 2017/3/6 0006.
//

#include <stdio.h>
#include <string.h>
#include "aecm_wrapper.h"
#include "echo_control_mobile.h"

aecm_wrapper::aecm_wrapper() {
    aecm          = NULL;
    aecmNearNoisy = NULL;
    aecmNearClean = NULL;
    aecmOut       = NULL;
}

aecm_wrapper::~aecm_wrapper() {
    if(aecm != NULL) {
        WebRtcAecm_Free(aecm);
        aecm = NULL;
    }
    free_buffer((void**)aecmNearNoisy);
    free_buffer((void**)aecmNearClean);
    free_buffer((void**)aecmOut);
}

/*
 *input:
 *    sampleHz 采样率，只支持8k/16k/32k/48k
 *output:
 *    int      0-success，1-fail
 */
int aecm_wrapper::aecm_init(int sampleHz) {
    if (base_wrapper::init(sampleHz) < 0) {
        return -1;
    }
    int iBand, status;
    aecm = WebRtcAecm_Create();
    status = WebRtcAecm_Init(aecm, sampleHz);
    if(status != 0) {
        fprintf(stderr, "[AecmInit]: failed in WebRtcAecm_Init\n");
        return -1;
    }
    AecmConfig aecmConfig;
    aecmConfig.cngMode = 1;
    aecmConfig.echoMode = 3;
    status = WebRtcAecm_set_config(aecm, aecmConfig);
    if(status != 0) {
        fprintf(stderr, "[AecmInit]: failed in WebRtcAecm_set_config\n");
        return -1;
    }
    aecmNearNoisy  = (short**)malloc(nBands*sizeof(short*));
    aecmOut = (short**)malloc(nBands*sizeof(short*));
    for(iBand = 0; iBand < nBands; iBand++) {
        aecmNearNoisy [iBand] = (short*)malloc(frameSh*sizeof(short));
        aecmOut[iBand] = (short*)malloc(frameSh*sizeof(short));
    }
    return 0;
}

int aecm_wrapper::aecm_proc(const short *nearNoisy, const short *nearClean, short *output, int pcmLen) {
    reset_data();

    int iFrame, iShort, status;
    int nFrames = pcmLen / frameSh;               //帧数
    int leftLen = pcmLen % frameSh;               //最后一帧大小
    int onceLen = frameSh;                        //一帧大小
    nFrames = (leftLen > 0) ? nFrames+1 : nFrames;
    short farend[pcmLen];
    memset(farend, 0, pcmLen*sizeof(short));
    status = WebRtcAecm_BufferFarend(aecm, farend, pcmLen);    // todo farend data?
    if(status != 0) {
        fprintf(stderr, "[AecmProc]: failed in WebRtcAecm_BufferFarend\n");
        return -1;
    }
    for(iFrame = 0; iFrame < nFrames; iFrame++) {
        if(iFrame == nFrames-1 && leftLen != 0) {
            onceLen = leftLen;
        }
        for(iShort = 0; iShort < onceLen; iShort++) {
            aecmNearNoisy[0][iShort] = (float)nearNoisy[iFrame*frameSh+iShort];
        }
        for(iShort = 0; iShort < onceLen; iShort++) {
            aecmNearClean[0][iShort] = (float)nearClean[iFrame*frameSh+iShort];
        }
        status = WebRtcAecm_Process(aecm, (const int16_t *) aecmNearNoisy,
                                    (const int16_t *) aecmNearClean, aecmOut[0], pcmLen, 0);
        if(status != 0) {
            fprintf(stderr, "[AecmProc]: failed in WebRtcAecm_Process\n");
            return -1;
        }
        for(iShort = 0; iShort < onceLen; iShort++) {
            output[iFrame*frameSh+iShort] = (short)aecmOut[0][iShort];
        }
    }
    return 0;
}

void aecm_wrapper:: reset_data() {
    int iBand;
    for(iBand = 0; iBand < nBands; iBand++) {
        memset(aecmNearNoisy [iBand], 0, frameSh*sizeof(short));
        memset(aecmNearClean [iBand], 0, frameSh*sizeof(short));
        memset(aecmOut[iBand], 0, frameSh*sizeof(short));
    }
}
