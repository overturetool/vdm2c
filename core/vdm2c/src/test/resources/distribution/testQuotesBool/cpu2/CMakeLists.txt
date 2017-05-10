cmake_minimum_required(VERSION 2.8.9)
project (cpu2)

#Include the sources
file(GLOB SOURCES1 "*.c")
file(GLOB SOURCES2 ${VDM_LIB_PATH}/*.c)
add_executable(cpu2Exe ${SOURCES1} ${SOURCES2})

#Include headers
include_directories(.)
include_directories(${VDM_LIB_PATH})