#!/bin/bash

cmake .
make -j5 &> /dev/null
./vdmclib/vdmclib
