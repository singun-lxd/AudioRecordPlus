cmake_minimum_required(VERSION 3.4.1)

project(webrtc)

#notice the "recycling" of CMAKE_C_FLAGS
#this is necessary to pick up android flags
set( CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -pedantic" )
set( CMAKE_CXX_STANDARD 11 )

include_directories(${PROJECT_SOURCE_DIR}/src/main/cpp/include
                    ${PROJECT_SOURCE_DIR}/src/main/cpp/common
                   )

add_definitions(-DFIXED_POINT -DUSE_KISS_FFT -DHAVE_CONFIG_H)

add_library(webrtc SHARED
            # ns lib
            src/main/cpp/ns/ns_jni_main.cc
            src/main/cpp/ns/ns_wrapper.cc
            src/main/cpp/ns/ns_core.c
            src/main/cpp/ns/noise_suppression.c

            # agc lib
            src/main/cpp/agc/agc_jni_main.cc
            src/main/cpp/agc/agc_wrapper.cc
            src/main/cpp/agc/analog_agc.c
            src/main/cpp/agc/digital_agc.c

            # aecm lib
            src/main/cpp/aecm/aecm_jni_main.c
            src/main/cpp/aecm/aecm_core.cc
            src/main/cpp/aecm/aecm_core_c.cc
            src/main/cpp/aecm/echo_control_mobile.cc

            # common lib
            src/main/cpp/common/base_wrapper.cc
            ## for ns
            src/main/cpp/common/fft4g.c
            ## for agc
            src/main/cpp/common/copy_set_operations.c
            src/main/cpp/common/division_operations.c
            src/main/cpp/common/dot_product_with_scale.c
            src/main/cpp/common/resample_by_2.c
            src/main/cpp/common/spl_sqrt.c
            ## for aecm
            src/main/cpp/common/complex_bit_reverse.c
            src/main/cpp/common/complex_fft.c
            src/main/cpp/common/cross_correlation.c
            src/main/cpp/common/delay_estimator.cc
            src/main/cpp/common/delay_estimator_wrapper.cc
            src/main/cpp/common/downsample_fast.c
            src/main/cpp/common/min_max_operations.c
            src/main/cpp/common/randomization_functions.c
            src/main/cpp/common/ring_buffer.c
            src/main/cpp/common/real_fft.c
            src/main/cpp/common/spl_init.c
            src/main/cpp/common/spl_sqrt_floor.c
            src/main/cpp/common/vector_scaling_operations.c
           )

target_link_libraries(webrtc log m)