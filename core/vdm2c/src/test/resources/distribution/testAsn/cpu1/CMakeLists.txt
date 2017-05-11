cmake_minimum_required(VERSION 2.8.9)
project (cpu1)

#Include headers
include_directories(.)
include_directories(${VDM_LIB_PATH})

SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c11 -Wall -D_GNU_SOURCE")


#Include the sources
#Include the sources
file(GLOB SOURCES1 "*.c")
file(GLOB SOURCES2 ${VDM_LIB_PATH}/*.c)
add_executable(cpu1Exe ${SOURCES1} ${SOURCES2})

target_link_libraries(cpu1Exe m)
