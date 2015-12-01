# Notes

* never use any C++ keywords in C. It leads to trouble later...

# Debug

Run the following to build in debug mode:

```bash
cmake -DCMAKE_BUILD_TYPE=Debug
```

# Eclipse

Please install: CDT + *C/C++ Unit Testing Support*


https://bjornarnelid.wordpress.com/2014/03/10/how-to-get-started-with-google-test-in-eclipse-cdt-on-linux/

## To import

1. execute cmake in the root, before that the complete repo is there (if not run `git submodule update --init`)
2. Create a new c++-project in Eclipse based on a standard makefile project using your avaliable tool chain.
 * Name the project with the name of the cmake project you want to import
 * Select source location to be the location of the cmake project
3. Build the project to see if Eclipse picksup the imports
 * It may miss the google test import (it did for me) The go and add it manually: Project->Properties->C/C++ general->Paths and Symbols->Includes->GNU C++ add the following include here (specific for you ofcause): `/.../third_party/googletest/googletest/include` 
