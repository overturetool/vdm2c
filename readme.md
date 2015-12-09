# VDM 2 C - Exploration 

This project aims at clarifying how a mapping from VDM-PP (and eventually VDM-RT) can be made to C. 

The project is inspired by the vdm2java code generator but since C does not have classes nor RTTI (Runtime Type information) we need to do something to add this.

# Compiling

To check out the sources see the gitlab guide for the project.

After successful checkout run:

1. `git submodule update --init`
2. Either:
 * for release `cmake .`
 * for debug 

```bash
cmake -DCMAKE_BUILD_TYPE=Debug .
```

## Compiler tool dependencies
* glib
 * gettext - this is not present on OSX so install it with:
 
```bash
brew install gettext
brew link --force gettext
```

### Install glib

* ubuntu

```bash
sudo apt-get install libglib2.0
```

* OSX

```bash
brew install glib
```

# Using Eclipse

Please install: CDT + *C/C++ Unit Testing Support*


https://bjornarnelid.wordpress.com/2014/03/10/how-to-get-started-with-google-test-in-eclipse-cdt-on-linux/

## Import

1. execute cmake in the root, before that the complete repo is there (if not run `git submodule update --init`)
2. Create a new c++-project in Eclipse based on a standard makefile project using your avaliable tool chain.
 * Name the project with the name of the cmake project you want to import
 * Select source location to be the location of the cmake project
3. Build the project to see if Eclipse picksup the imports
 * If it do not pickup the includes then enable the fix below

## Update *CDT GCC Build Output Parser* to work with *CMake* on OSX

1. Goto `Project Properties -> C/C++ General -> Preprocessor Include Paths, Macros etc.`
2. Enable `CDT GCC Build Output Parser`
3. Change the *Compiler Command Pattern* to `(.*gcc)|(.*[gc]\+\+)|(.*cc)|(clang)`

Source: https://developer.mozilla.org/en-US/docs/Eclipse_CDT

# Translation Notes

If we want the C code to be usable from C++ we must make sure that it can be included like:

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

## Class mapping

http://www.pvv.ntnu.no/~hakonhal/main.cgi/c/classes/


# Other debugging related stuff

## Core dump in terminal only

Enable the core to be dumped by:

```bash
ulimit -c unlimited
```

on linux it will be in the folder of the application and on OSX in `/cores/`

# Compiler -- platform dependent stuff

Remember that:

* `malloc` allocated space in memory with random stuff - it may be 0 but we dont know. We know that linx and OSX e.g. OSX may choose 0 memory where linux may choose random memory, but it is random :-)
* `calloc` allocated space in memory that is 0 

Why is this importsnt: Well `free` only frees if `ptr != NULL`  
