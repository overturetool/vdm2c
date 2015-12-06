# Notes

* never use any C++ keywords in C. It leads to trouble later...

# Dependencies
* glib
 * gettext - this is not present on OSX so install it with:
```bash
brew install gettext
brew link --force gettext
```

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
 * If it do not pickup the includes then enable the fix below

## Fix wired Eclipse setup for CDT discovery of includes for the indexer

1. Goto `Project Properties -> C/C++ General -> Preprocessor Include Paths, Macros etc.`
2. Enable `CDT GCC Build Output Parser`
3. Change the *Compiler Command Pattern* to `(.*gcc)|(.*[gc]\+\+)|(.*cc)|(clang)`

Source: https://developer.mozilla.org/en-US/docs/Eclipse_CDT
