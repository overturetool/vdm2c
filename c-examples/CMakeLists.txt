cmake_minimum_required (VERSION 2.8.5)
project (c-examples C)

enable_testing()

set(CMAKE_VERBOSE_MAKEFILE on)


set(CMAKE_C_FLAGS ${CMAKE_C_FLAGS} "-pthread")

file(GLOB C_FILES *.c)
file(GLOB CPP_FILES tests/*.cpp)

add_executable(${PROJECT_NAME} ${C_FILES} ${CPP_FILES})

#include_directories(${libshmfmi_INCLUDE_DIRS})
include_directories(${gtest_SOURCE_DIR}/include)

target_link_libraries(${PROJECT_NAME} gtest_main)
#target_link_libraries(${PROJECT_NAME} libshmfmi)
#target_link_libraries(${PROJECT_NAME} ${libshmfmi_LIBS})

add_test(AllTestsIn-c-examples ${PROJECT_NAME})
