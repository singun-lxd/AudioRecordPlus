cmake_minimum_required(VERSION 3.4.1)

project(webrtc)

#notice the "recycling" of CMAKE_C_FLAGS
#this is necessary to pick up android flags
set( CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -pedantic" )

include_directories(${PROJECT_SOURCE_DIR}/src/main/cpp/include)

add_definitions(-DFIXED_POINT -DUSE_KISS_FFT -DHAVE_CONFIG_H)

add_library(webrtc SHARED

            src/main/cpp/common/base_wrapper.cc

            src/main/cpp/ns/ns_jni_main.cc
            src/main/cpp/ns/ns_wrapper.cc
            src/main/cpp/ns/ns_core.c
            src/main/cpp/ns/fft4g.c
            src/main/cpp/ns/noise_suppression.c

            src/main/cpp/aecm/aecm_jni_main.c
            src/main/cpp/aecm/aecm_core.c
            src/main/cpp/aecm/complex_bit_reverse.c
            src/main/cpp/aecm/complex_fft.c
            src/main/cpp/aecm/delay_estimator_wrapper.c
            src/main/cpp/aecm/delay_estimator.c
            src/main/cpp/aecm/division_operations.c
            src/main/cpp/aecm/echo_control_mobile.c
            src/main/cpp/aecm/min_max_operations.c
            src/main/cpp/aecm/randomization_functions.c
            src/main/cpp/aecm/ring_buffer.c
            src/main/cpp/aecm/spl_sqrt_floor.c
            src/main/cpp/aecm/spl_init.c
            src/main/cpp/aecm/real_fft.c
            src/main/cpp/aecm/cross_correlation.c
            src/main/cpp/aecm/downsample_fast.c
            src/main/cpp/aecm/vector_scaling_operations.c
           )

target_link_libraries(webrtc log m)