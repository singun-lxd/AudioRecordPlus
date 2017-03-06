//
// Created by singun on 2017/3/6 0006.
//

#include <stdio.h>
#include <stdlib.h>
#include "base_wrapper.h"

base_wrapper::base_wrapper() {
    sampleHz = 16000;
    frameSh    = 160;
    nBands     = 1;
    frameMs    = 10;
}

base_wrapper::~base_wrapper() {

}

int base_wrapper::init(int sampleHzIn) {
    if(nBands < 1 || nBands > 2) {
        fprintf(stderr, "[FrontInit]: only support nBands 1 or 2\n");
        return -1;
    }
    sampleHz = sampleHzIn;
    switch(sampleHz) {
        case 8000:
        case 16000:
        case 32000:
        case 48000:
            frameSh = sampleHz * ((float)frameMs / 1000);
            break;
        default:
            fprintf(stderr, "[FrontInit]: only support sampleHz 8k 16k 32k 48k\n");
            return -1;
    }
    return 0;
}

void base_wrapper::free_buffer(void **buffer) {
    if(buffer != NULL) {
        for(int iBand = 0; iBand < nBands; iBand++) {
            if(buffer[iBand] != NULL) {
                free(buffer[iBand]);
                buffer[iBand] = NULL;
            }
        }
        free(buffer);
        buffer = NULL;
    }
}
