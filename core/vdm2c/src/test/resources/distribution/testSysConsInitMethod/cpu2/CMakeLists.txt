cmake_minimum_required(VERSION 2.8.9)
project (cpu2)

#Include the sources
#file(GLOB HEADERS1 "include1/*.h")
#file(GLOB HEADERS2 "include2/*.h")
file(GLOB SOURCES1 "*.c")
file(GLOB SOURCES2 "/Users/Miran/Documents/C_codegen/vdm2c/c/vdmclib/src/main/*.c")
add_executable(cpu2Exe ${SOURCES1} ${SOURCES2})

#Include headers
include_directories(/Users/Miran/Documents/C_codegen/vdm2c/c/vdmclib/src/main)