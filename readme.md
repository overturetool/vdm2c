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


