# VDM2C

The VDM2C project aims to develop a VDM-to-C translation that allows generated VDM specifications to be executed on smaller devices which has a C or C++ compiler available.

The translation aims to support most of the common constructs in VDM and therefore will have an overhead compared to dedicated C code. 

The following contributes to this overhead:

* the nature of the VDM language requires runtime type information similar to RTTI for C++
* ...

## Installation

VDM2C can be installed as a plugin for the [Overture](http://overturetool.org/) tool. Stable releases are available via the p2 update site:

* http://overture.au.dk/vdm2c/master/repository/

For those who want to try out the newest VDM2C features it is possible to obtain development builds via the following p2 site:

* http://overture.au.dk/vdm2c/development/repository/

## Release procedure

This section covers the steps of the release procedure. For the rest of this section, `${RELEASE_VER}` is the next release version (that we are about to release), `${NEW_DEV_VER}` is the next development version, and `${FUTURE_RELEASE_VER}` is a future release version that immediately follows `${RELEASE_VER}`.

Every milestone is named according to a VDM2C release version. By convention, the milestone corresponding to `${RELEASE_VER}` is named `v${RELEASE_VER}`.

Note that in order to perform a release you must have login access to Aarhus University's (AUs) public [build server](https://build.overture.au.dk/jenkins/).

### Closing the current milestone

Before releasing `${RELEASE_VER}` the corresponding milestone, `v${RELEASE_VER}`, must be closed. To do that, go to the list of [milestones](https://github.com/overturetool/vdm2c/milestones) and close `v${RELEASE_VER}`. Afterwards, go to the [issue tracker](https://github.com/overturetool/vdm2c/issues) and update all issues to milestone `${FUTURE_RELEASE_VER}`. When that's done the progress of milestone `v${RELEASE_VER}` will change to 100% since no open issues are associated with this milestone anymore.

### Generate the release notes

The next step is to generate the `${RELEASE_VER}` release notes. To do that, go to the `release` folder and execute the `gen-release-notes.py` script:

```bash
vdm2c $ cd release/
vdm2c/release $ ./gen-release-notes.py 
```

This script generates release notes for closed milestones based on a template (`ReleaseNotes-template.md`). So assuming that `v${RELEASE_VER}` has been closed then this script will generate the file `ReleaseNotes_${RELEASE_VER}.md`. Now, open `ReleaseNotes_${RELEASE_VER}.md` and add a description of the release.

Note that the directory also contains the release notes for previous VDM2C releases:

```bash
vdm2c/release $ ls release
./release/gen-release-notes.py
ReleaseNotes_0.0.10.md
ReleaseNotes_0.0.12.md
ReleaseNotes_0.0.14.md
ReleaseNotes_0.0.16.md
...
ReleaseNotes-template.md
```

Now, commit the release notes:

```bash 
vdm2c/release $ git add ReleaseNotes_${RELEASE_VER}.md
vdm2c/release $ git commit -m "Add VDM2C ${RELEASE_VER} release notes"
```

### Setting the release version and the next development version

Before performing the release, the release version and the next development version must be set by updating `overture.release.properties`, which is located in the root of the repository:

```
Release version = ${RELEASE_VER}
New development version = ${RELEASE_VER}-SNAPSHOT
```

Now, commit/push your `overture.release.properties` changes:

```bash
vdm2c $ git add overture.release.properties
vdm2c $ git commit -m "Prepare release of VDM2C ${RELEASE_VER}"
vdm2c $ git push
```

### Performing the release

AUs public build server is configured to run a script that performs most of the release tasks. To perform the release go to the [build server](https://build.overture.au.dk/jenkins/) and execute the `vdm2c-release` build job. This will automatically generate and upload the VDM2C plugin to a publicly accessible p2 repository (see the [installation instructions](#installation)). Once the build job has completed you'll notice that the build server has added a tag, Release/`v${RELEASE_VER}`, to the commit that marks the release.

Finally, by convention the `master` branch must always point to latest release:

```bash
vdm2c $ git checkout master
vdm2c $ git merge Release/v${RELEASE_VER}
vdm2c $ git push
```

### Publish the release on Github

The last step is to publish the release on Github. To do that, go to [releases](https://github.com/overturetool/vdm2c/releases) and draft a new release by following the instructions. Specifically, set the tag that marks the release (Release/`v${RELEASE_VER}`), add the content of the release notes (`ReleaseNotes_${RELEASE_VER}.md`) and upload a zip file that contains the contents of the p2 repository. This zip file is generated by the AU build server and is available at http://overture.au.dk/vdm2c/master/repository/p2.zip immediately after the `vdm2c-release` build job has finished. If you're in doubt then check the contents of previous published releases.

That's it - you're done!

## Development environment prerequisites

* `cmake` is needed in order to run the tests

### Compiler tool dependencies

#### Install glib

* ubuntu

```bash
sudo apt-get install libglib2.0
```

* OSX

```bash
brew install glib
```

#### Install gettext

`gettext` is not present on OSX so install it with

```bash
brew install gettext
brew link --force gettext
```

### Using Eclipse

Please install: CDT + *C/C++ Unit Testing Support*


### Other packages

* ubuntu

```bash
sudo apt-get install cmake make gcc g++
```

## Compiling

The generated code is tested using googletest, which is available via a git submodule. This submodule must be initialised after a successful checkout:

1. `git submodule update --init`
2. Either:
 * for release `cmake .`
 * for debug 

```bash
cmake -DCMAKE_BUILD_TYPE=Debug .
```

## Development

The translation is developed in two parts:

1. A C library, stored in `<root>/c/vdmclib`, containing:
 * Library support for all operations that can be carried our on numbers, sets, seqs, maps etc.
 * Library support providing a number of macros to ease class encoding
2. A translation of VDM into C using this library

### Import the entire runtime in Eclipse

1. execute cmake in the root  (and make sure you have initialised the googletest submodule)
2. Create a new C++-project in Eclipse based on a standard makefile project using your avaliable tool chain.
 * Name the project with the name of the cmake project you want to import
 * Select source location to be the location of the cmake project
3. Build the project to see if Eclipse picks up the imports
 * If it do not pickup the includes then use the fix below

### Update *CDT GCC Build Output Parser* to work with *CMake* on OSX

1. Goto `Project Properties -> C/C++ General -> Preprocessor Include Paths, Macros etc.`
2. Enable `CDT GCC Build Output Parser`
3. Change the *Compiler Command Pattern* to `(.*gcc)|(.*[gc]\+\+)|(.*cc)|(clang)`

Source: https://developer.mozilla.org/en-US/docs/Eclipse_CDT

#### Running tests

* To run the tests one must pass the location of the VDM library as a property `-DVDM_LIB_PATH="/path/to/vdm2c-exploration/c-examples/lib"`
* Pass the `-DTEST_OUTPUT` property to get the googletest output

#### Test the runtime library

All the code used to test the runtime library is stored in `<root>/c/vdmclib/src/tests` and written using the googletest framework. The tests are run automatically as part of the Maven build, but may also be executed manually using the steps below:

```bash
## Go into runtime library folder
cd <root>/c
## Generate the make file
cmake .
## Build the google test binary
make -j<no-of-cpu-cores-plus-one>
## Go to the folder that contains the tests
cd vdmclib/
## Run the generated google tests 
make test
```
The test report is available in `report.xml`

## Notes

### Using googletest

https://bjornarnelid.wordpress.com/2014/03/10/how-to-get-started-with-google-test-in-eclipse-cdt-on-linux/


### Name mangling

https://en.wikipedia.org/wiki/Name_mangling
http://www.avabodh.com/cxxin/namemangling.html

### Eclipse editor for velocity

* Remember to install `Eclipse 2.0 Style Plugin Support` before trying to install the editor *

https://marketplace.eclipse.org/content/veloeclipse

### Translation Notes

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

### Transformation Rules

All nodes that is shared between VDM and C must have a C plain C template. This means it **cannot** use the VDM library for code generation.

### Implementing classes

* http://www.pvv.ntnu.no/~hakonhal/main.cgi/c/classes/
* http://www.eventhelix.com/RealtimeMantra/basics/ComparingCPPAndCPerformance2.htm#.VmbPCuODFBc
* http://www.go4expert.com/articles/virtual-table-vptr-multiple-inheritance-t16616/
* http://www.go4expert.com/articles/virtual-table-vptr-t16544/

### C/C++ language related stuff

* http://stackoverflow.com/questions/3523145/pointer-arithmetic-for-void-pointer-in-c
* https://www.cs.uaf.edu/2010/fall/cs301/lecture/10_15_struct_and_class.html
* http://www.eventhelix.com/RealtimeMantra/basics/ComparingCPPAndCPerformance2.htm#.VpmaslMrLUr
* https://en.wikipedia.org/wiki/Virtual_method_table
* http://www.go4expert.com/articles/virtual-table-vptr-multiple-inheritance-t16616/
* http://www.go4expert.com/articles/virtual-table-vptr-t16544/
* http://www.learncpp.com/cpp-tutorial/125-the-virtual-table/
* http://www.drdobbs.com/cpp/single-inheritance-classes-in-c/184406396?pgno=1
* http://www.pvv.ntnu.no/~hakonhal/main.cgi/c/classes

### C Macro

* Compare arguments (`#a==#b`) http://stackoverflow.com/questions/33491170/whats-the-difference-in-the-and-in-the-following-c-code
* Statements inline eval http://stackoverflow.com/questions/4712720/typechecking-macro-arguments-in-c

### Compiler -- platform dependent stuff

Remember that:

* `malloc` leaves the memory uninitialised
* `calloc` zero-initializes the buffer (which takes more time)

This is important since `free` only frees the memory if `ptr != NULL`  

### Other debugging related stuff

#### Core dump in terminal only

Enable the core to be dumped by:

```bash
ulimit -c unlimited
```

On Linux the core is dumped in the application folder, while OSX uses `/cores/`
