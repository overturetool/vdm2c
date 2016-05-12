# VDM 2 C

The VDM 2 C project aims to develop a VDM to C translation that allows generated VDM specifications to be executed on smaller devices which has a C or C++ compiler avaliable.

The translation aims to support most of the common constructs in VDM and therefore will have an overhead compared to dedicated C code. 

The following contributes to this overhead:

* the nature of the VDM language requires runtime type information similar to RTTI for C++
* ...


# Development

The translation is developed in two parts:

1. A C library containing:
 * Library support for all operations that can be carried our on numbers, sets, seqs, maps etc.
 * Library support providing a number of macros to ease class encoding
2. A translation of VDM into C using the above library

### Running tests

Please set this property to point to the tests in the repo: `git@gitlab.au.dk:into-cps/vdm2c-exploration.git`
`-Dcexamples.path=/path/to/vdm2c-exploration/vdm` and `-DVDM_LIB_PATH="/path/to/vdm2c-exploration/c-examples/lib"`

# Notes

# Name mangling

https://en.wikipedia.org/wiki/Name_mangling
http://www.avabodh.com/cxxin/namemangling.html

# Eclipse editor for velocity

* Remember to install `Eclipse 2.0 Style Plugin Support` before trying to install the editor *

https://marketplace.eclipse.org/content/veloeclipse

# Transformation Rules

All nodes that is shared between VDM and C must have a C plain C template. This means it **cannot** use the VDM library for code generation.

