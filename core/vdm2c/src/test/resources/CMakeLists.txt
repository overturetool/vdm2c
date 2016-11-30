cmake_minimum_required (VERSION 2.8.5)
project (##PROJECT_NAME## C CXX)

set(CMAKE_INCLUDE_CURRENT_DIR on)

## valgrind
find_program(CTEST_MEMORYCHECK_COMMAND NAMES valgrind)
set(MEMORYCHECK_COMMAND_OPTIONS "--xml=yes --xml-file=valgrind.xml")

enable_testing()

FIND_PACKAGE(PkgConfig)
PKG_CHECK_MODULES(GLIB2 REQUIRED glib-2.0>=2.36.0)

## google test download

include(CTest)

if (CMAKE_VERSION VERSION_LESS 3.2)
    set(UPDATE_DISCONNECTED_IF_AVAILABLE "")
else()
    set(UPDATE_DISCONNECTED_IF_AVAILABLE "UPDATE_DISCONNECTED 1")
endif()

#include(DownloadProject.cmake)
#download_project(PROJ                googletest
#                 GIT_REPOSITORY      https://github.com/google/googletest.git
#                 GIT_TAG             master
#                 ${UPDATE_DISCONNECTED_IF_AVAILABLE}
#)

# Prevent GoogleTest from overriding our compiler/linker options
# when building with Visual Studio
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)

## INSERT_GOOGLE_TEST_SUB_DIRECTORY ##

# When using CMake 2.8.11 or later, header path dependencies
# are automatically added to the gtest and gmock targets.
# For earlier CMake versions, we have to explicitly add the
# required directories to the header search path ourselves.
if (CMAKE_VERSION VERSION_LESS 2.8.11)
    include_directories("${gtest_SOURCE_DIR}/include"
                        "${gmock_SOURCE_DIR}/include")
endif()

## end google test download


include(CheckCXXCompilerFlag)
CHECK_CXX_COMPILER_FLAG("-std=c++11" COMPILER_SUPPORTS_CXX11)
CHECK_CXX_COMPILER_FLAG("-std=c++0x" COMPILER_SUPPORTS_CXX0X)
if(COMPILER_SUPPORTS_CXX11)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
elseif(COMPILER_SUPPORTS_CXX0X)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++0x")
else()
        message(STATUS "The compiler ${CMAKE_CXX_COMPILER} has no C++11 support. Please use a different C++ compiler.")
endif()


IF(CMAKE_COMPILER_IS_GNUCC)
  SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fmessage-length=0 -std=c11")
ENDIF(CMAKE_COMPILER_IS_GNUCC)
IF(CMAKE_COMPILER_IS_GNUCXX)
  SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fmessage-length=0")
ENDIF(CMAKE_COMPILER_IS_GNUCXX)

SET(CMAKE_VERBOSE_MAKEFILE ON)

file(GLOB C_FILES ${VDM_LIB_PATH}/*.c  *.c)
file(GLOB CPP_FILES *.cpp)

add_executable(${PROJECT_NAME} ${C_FILES} ${CPP_FILES})

include_directories(${VDM_LIB_PATH})


INCLUDE_DIRECTORIES(
    ${GLIB2_INCLUDE_DIRS}
)

target_link_libraries(${PROJECT_NAME} gtest_main)
TARGET_LINK_LIBRARIES(${PROJECT_NAME}
    ${GLIB2_LIBRARIES}
)


add_test(NAME AllTestsIn-##PROJECT_NAME## COMMAND ${PROJECT_NAME} "--gtest_output=xml:report.xml")
