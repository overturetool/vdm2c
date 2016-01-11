cmake_minimum_required (VERSION 2.8.5)
project (generator-test C CXX)

set(CMAKE_INCLUDE_CURRENT_DIR on)

enable_testing()

FIND_PACKAGE(PkgConfig)
PKG_CHECK_MODULES(GLIB2 REQUIRED glib-2.0>=2.36.0)



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


#set(CMAKE_C_FLAGS_DEBUG " -O0 -g3 -Wall")
#set(CMAKE_CXX_FLAGS_DEBUG " -O0 -g3 -Wall")


#file(GLOB C_FILES ../c-examples/*.c ../c-examples/classes/*.c ../c-examples/lib/*.c ../c-examples/records/*.c ../c-examples/tests/*.c)
file(GLOB C_FILES ../c-examples/*.c  ../c-examples/lib/*.c ../c-examples/tests/*.c )
#file(GLOB CPP_FILES ../c-examples/tests/*.cpp)

#file(GLOB CPP_FILES ../c-examples/tests/ClassATest.cpp ../c-examples/tests/ClassCTest.cpp)
#file(GLOB CPP_FILES tests/*.cpp)

SET(EXT_C_FILES  CACHE PATH "location of external generates c files")

file(GLOB C_GEN_FILES ${EXT_C_FILES}/*.c)



foreach(filename ${C_GEN_FILES})

if(${filename} MATCHES ".*A.c$")
set(CPP_FILES ${CPP_FILES};../c-examples/tests/ClassATest.cpp)
endif()

if(${filename} MATCHES ".*C.c$")
set(CPP_FILES ${CPP_FILES};../c-examples/tests/ClassCTest.cpp)
endif()

if(${filename} MATCHES ".*ExpressionSeq.c$")
set(CPP_FILES ${CPP_FILES};tests/ExpressionsSeq_Tests.cpp)
endif()

endforeach()

message(STATUS "CPP files: ${CPP_FILES}")
message(STATUS "C_GEN_FILES:  ${C_GEN_FILES}")

add_executable(${PROJECT_NAME} ${C_FILES} ${C_GEN_FILES} ${CPP_FILES})

include_directories(../c-examples/;${EXT_C_FILES})

include_directories(${gtest_SOURCE_DIR}/include)
INCLUDE_DIRECTORIES(
    ${GLIB2_INCLUDE_DIRS}
)

target_link_libraries(${PROJECT_NAME} gtest_main)
TARGET_LINK_LIBRARIES(${PROJECT_NAME}
    ${GLIB2_LIBRARIES}
)


add_test(AllTestsIn-generator-test ${PROJECT_NAME})
