//
// Created by singun on 2017/3/6 0006.
//

#include <stdio.h>
#include <string.h>
#include "agc_wrapper.h"
#include "gain_control.h"

agc_wrapper::agc_wrapper() {
    agc        = NULL;
    agcIn      = NULL;
    agcOut     = NULL;
}

agc_wrapper::~agc_wrapper() {
    if(agc != NULL) {
        WebRtcAgc_Free(agc);
        agc = NULL;
    }
    free_buffer((void**)agcIn);
    free_buffer((void**)agcOut);
}

/*
 *input:
 *    sampleHz 采样率，只支持8k/16k/32k/48k
 *output:
 *    int      0-success，1-fail
 */
int agc_wrapper::agc_init(int agcDb, int agcDbfs) {
    if (base_wrapper::init(sampleHz) < 0) {
        return -1;
    }
    int iBand, status;
    if(agcDbfs < 0 || agcDbfs > 31 || agcDb < 0 || agcDb > 31) {
        fprintf(stderr, "[AgcInit]: agcDb and agcDbfs should be 0~31\n");
        return -1;
    }
    agc = WebRtcAgc_Create();
    int minLevel = 0;
    int maxLevel = 255;
    int agcMode  = kAgcModeFixedDigital;
    status = WebRtcAgc_Init(agc, minLevel, maxLevel, agcMode, sampleHz);
    if(status != 0) {
        fprintf(stderr, "[AgcInit]: failed in WebRtcAgc_Init\n");
        return -1;
    }
    WebRtcAgcConfig agcConfig;
    agcConfig.limiterEnable = 1;
    agcConfig.compressionGaindB = agcDb;   //在Fixed模式下，越大声音越大
    agcConfig.targetLevelDbfs = agcDbfs;   //dbfs表示相对于full scale的下降值，0表示full scale，越小声音越大
    status = WebRtcAgc_set_config(agc, agcConfig);
    if(status != 0) {
        fprintf(stderr, "[AgcInit]: failed in WebRtcAgc_set_config\n");
        return -1;
    }
    agcIn  = (short**)malloc(nBands*sizeof(short*));
    agcOut = (short**)malloc(nBands*sizeof(short*));
    for(iBand = 0; iBand < nBands; iBand++) {
        agcIn [iBand] = (short*)malloc(frameSh*sizeof(short));
        agcOut[iBand] = (short*)malloc(frameSh*sizeof(short));
    }
    return 0;
}

int agc_wrapper::agc_proc(const short *input, short *output, int pcmLen) {
    reset_data();

    int iFrame, status;
    int nFrames = pcmLen / frameSh;               //帧数
    int leftLen = pcmLen % frameSh;               //最后一帧大小
    int onceLen = frameSh;                        //一帧大小
    nFrames = (leftLen > 0) ? nFrames+1 : nFrames;
    int micLevelIn  = 0;                          //麦克风输入级别，将上一次的输出级别作为本次的输入级别
    int micLevelOut = 0;                          //麦克风的输出级别
    uint8_t saturationWarning;                    //是否有溢出发生，增益放大以后的最大值超过了65536
    int echo = 0;                                 //增益放大是否考虑回声影响
    for(iFrame = 0; iFrame < nFrames; iFrame++) {
        if(iFrame == nFrames-1 && leftLen != 0) {
            onceLen = leftLen;
        }
        memcpy(agcIn[0], input+iFrame*frameSh, onceLen*sizeof(short));
        status = WebRtcAgc_Process(agc, (const int16_t *const *) agcIn, nBands, frameSh, agcOut,
                                   micLevelIn, &micLevelOut, echo, &saturationWarning);
        if(status != 0) {
            fprintf(stderr, "[AgcProc]: failed in WebRtcAgc_Process\n");
            return -1;
        }
        if(saturationWarning != 0) {
            fprintf(stdout, "[AgcProc]: saturationWarning occured\n");
        }
        memcpy(output+iFrame*frameSh, agcOut[0], onceLen*sizeof(short));
        micLevelIn = micLevelOut;
    }
    return 0;
}

void agc_wrapper:: reset_data() {
    int iBand;
    for(iBand = 0; iBand < nBands; iBand++) {
        memset(agcIn [iBand], 0, frameSh*sizeof(short));
        memset(agcOut[iBand], 0, frameSh*sizeof(short));
    }
}
