#!/bin/bash

cmake .
make -j5 &> /dev/null
./vdmclib/src/main/vdmclib