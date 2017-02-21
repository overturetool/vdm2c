
cmake_minimum_required(VERSION 2.8.10)

set(CMAKE_DISABLE_SOURCE_CHANGES  ON)

enable_testing()


set(CMAKE_VERBOSE_MAKEFILE ON)
set(CMAKE_COLOR_MAKEFILE   ON)

include_directories(vdmclib/src/test/resources)

add_subdirectory(third_party/googletest/googletest)
add_subdirectory(vdmclib)
