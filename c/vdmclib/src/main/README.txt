This source code is generated.  To compile, rename the file ProjectCMakeLists.txt to CMakeLists.txt and run

	cmake .

or
	cmake -DCMAKE_BUILD_TYPE=Debug .

if you intend to debug the generated code.  This should generate the required makefiles in order to be able to build the generated model with

	make

The main.c file is empty, it must be populated as required.  It is only provided to aid the initial compilation process.  PLEASE NOTE:  each time the VDM project is generated, this directory is completely erased.  Please consider carrying out development on the main.c file elsewhere and linking to it from this directory.
