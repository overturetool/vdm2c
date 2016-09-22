# VDM2C

The VDM2C project aims to develop a VDM-to-C translation that allows generated VDM specifications to be executed on smaller devices which has a C or C++ compiler available.

The translation aims to support most of the common constructs in VDM and therefore will have an overhead compared to dedicated C code. 

The following contributes to this overhead:

* the nature of the VDM language requires runtime type information similar to RTTI for C++
* ...

# Prerequisites

* `cmake` is needed in order to run the tests

## Compiler tool dependencies

### Install glib

* ubuntu

```bash
sudo apt-get install libglib2.0
```

* OSX

```bash
brew install glib
```

### Install gettext

`gettext` is not present on OSX so install it with

```bash
brew install gettext
brew link --force gettext
```

## Using Eclipse

Please install: CDT + *C/C++ Unit Testing Support*


## Other packages

* ubuntu

```bash
sudo apt-get install cmake make gcc g++
```

# Compiling

The generated code is tested using googletest, which is available via a git submodule. This submodule must be initialised after a successful checkout:

1. `git submodule update --init`
2. Either:
 * for release `cmake .`
 * for debug 

```bash
cmake -DCMAKE_BUILD_TYPE=Debug .
```

# Development

The translation is developed in two parts:

1. A C library, stored in `<root>/c/vdmclib`, containing:
 * Library support for all operations that can be carried our on numbers, sets, seqs, maps etc.
 * Library support providing a number of macros to ease class encoding
2. A translation of VDM into C using this library

## Import the entire runtime in Eclipse

1. execute cmake in the root  (and make sure you have initialised the googletest submodule)
2. Create a new C++-project in Eclipse based on a standard makefile project using your avaliable tool chain.
 * Name the project with the name of the cmake project you want to import
 * Select source location to be the location of the cmake project
3. Build the project to see if Eclipse picks up the imports
 * If it do not pickup the includes then use the fix below

## Update *CDT GCC Build Output Parser* to work with *CMake* on OSX

1. Goto `Project Properties -> C/C++ General -> Preprocessor Include Paths, Macros etc.`
2. Enable `CDT GCC Build Output Parser`
3. Change the *Compiler Command Pattern* to `(.*gcc)|(.*[gc]\+\+)|(.*cc)|(clang)`

Source: https://developer.mozilla.org/en-US/docs/Eclipse_CDT

### Running tests

* To run the tests one must pass the location of the VDM library as a property `-DVDM_LIB_PATH="/path/to/vdm2c-exploration/c-examples/lib"`
* Pass the `-DTEST_OUTPUT` property to get the googletest output

### Test the runtime library

All the code used to test the runtime library is stored in `<root>/c/vdmclib/src/tests` and written using the googletest framework. The tests can be executed as follows:

```bash
# Go into runtime library folder
cd <root>/c
# Build the google test binary
make -j<no-of-cpu-cores-plus-one
# Run the generated google tests
./vdmclib/src/vdmclib
```

The runtime tests should now run, and produce an output similar to that shown below.

```bash
...
[----------] 1 test from KK
[ RUN      ] KK.offsetTest
[       OK ] KK.offsetTest (0 ms)
[----------] 1 test from KK (0 ms total)

[----------] Global test environment tear-down
[==========] 121 tests from 22 test cases ran. (84 ms total)
[  PASSED  ] 121 tests.
```

# Notes

## Using googletest

https://bjornarnelid.wordpress.com/2014/03/10/how-to-get-started-with-google-test-in-eclipse-cdt-on-linux/


## Name mangling

https://en.wikipedia.org/wiki/Name_mangling
http://www.avabodh.com/cxxin/namemangling.html

## Eclipse editor for velocity

* Remember to install `Eclipse 2.0 Style Plugin Support` before trying to install the editor *

https://marketplace.eclipse.org/content/veloeclipse

## Translation Notes

If one wants to use the C code from C++  it can be included as shown below:

```c++
extern "C"
{
#include "lib/TypedValue.h"
#include "lib/VdmBasicTypes.h"
...
}
```

Therefore:
* never use any C++ keywords in C. It leads to trouble later...

## Transformation Rules

All nodes that is shared between VDM and C must have a C plain C template. This means it **cannot** use the VDM library for code generation.

## Implementing classes

* http://www.pvv.ntnu.no/~hakonhal/main.cgi/c/classes/
* http://www.eventhelix.com/RealtimeMantra/basics/ComparingCPPAndCPerformance2.htm#.VmbPCuODFBc
* http://www.go4expert.com/articles/virtual-table-vptr-multiple-inheritance-t16616/
* http://www.go4expert.com/articles/virtual-table-vptr-t16544/

## C/C++ language related stuff

* http://stackoverflow.com/questions/3523145/pointer-arithmetic-for-void-pointer-in-c
* https://www.cs.uaf.edu/2010/fall/cs301/lecture/10_15_struct_and_class.html
* http://www.eventhelix.com/RealtimeMantra/basics/ComparingCPPAndCPerformance2.htm#.VpmaslMrLUr
* https://en.wikipedia.org/wiki/Virtual_method_table
* http://www.go4expert.com/articles/virtual-table-vptr-multiple-inheritance-t16616/
* http://www.go4expert.com/articles/virtual-table-vptr-t16544/
* http://www.learncpp.com/cpp-tutorial/125-the-virtual-table/
* http://www.drdobbs.com/cpp/single-inheritance-classes-in-c/184406396?pgno=1
* http://www.pvv.ntnu.no/~hakonhal/main.cgi/c/classes

## C Macro

* Compare arguments (`#a==#b`) http://stackoverflow.com/questions/33491170/whats-the-difference-in-the-and-in-the-following-c-code
* Statements inline eval http://stackoverflow.com/questions/4712720/typechecking-macro-arguments-in-c

## Compiler -- platform dependent stuff

Remember that:

* `malloc` leaves the memory uninitialised
* `calloc` zero-initializes the buffer (which takes more time)

This is important since `free` only frees the memory if `ptr != NULL`  

## Other debugging related stuff

### Core dump in terminal only

Enable the core to be dumped by:

```bash
ulimit -c unlimited
```

On Linux the core is dumped in the application folder, while OSX uses `/cores/`
