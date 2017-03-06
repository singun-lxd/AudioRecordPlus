//
// Created by singun on 2017/3/6 0006.
//

#ifndef AECM_WRAPPER_H
#define AECM_WRAPPER_H

#include "../common/base_wrapper.h"

#ifdef __cplusplus
extern "C" {
#endif

class aecm_wrapper : base_wrapper {
public:
    aecm_wrapper();
    ~aecm_wrapper();

    int aecm_init(int sampleHz);
    int aecm_proc(const short *nearNoisy, const short *nearClean, short *output, int pcmLen);
private:
    void reset_data();

    // aecm part
    void* aecm;      //aecm instance
    short **aecmNearNoisy;  //aecm nearNoisy[band][data]
    short **aecmNearClean;  //aecm nearClean[band][data]
    short **aecmOut; //aecm output[band][data]
};

#ifdef __cplusplus
}
#endif

#endif //AECM_WRAPPER_H
