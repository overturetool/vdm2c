
# [VDM2C 0.1.0 - Release Notes - 03 March 2017](https://github.com/overturetool/vdm2c/milestone/10)

## What's New?

This release of VDM2C changes the way memory is managed in the generated code.

Most notably, the VDM2C runtime library has been extended with support for garbage collection. In the generated code values are now constructed using special C functions such as `newIntCG` or `newBoolCG` that enable automatic allocation and deallocation of heap memory. When a value is constructed in this way, it does not need to be explicitly freed in the usual way using `vdmFree`. For now the garbage collection strategy replaces the old approach to memory management.

Note that the garbage collector must be initialised/shutdown before/after the generated code is executed:

```
int main()
{
  vdm_gc_init();
  /* Your code */
  vdm_gc_shutdown();
  return 0;
}
```

To free memory, the garbage collection routine must be explicitly invoked by the user using `vdm_gc`. Therefore, make sure you invoke this function whenever appropriate.

In terms of VDM feature coverage, VDM2C has been extended with partial support for the CSV library.  The CSV library now implements functionality to get a CSV file line count and to read numeric values.  The CSV library can be used both in source code FMUs, where the CSV files are read from the FMU "resources" directory, or in non-FMU scenarios, where absolute or relative file paths can be used.


## Reporting Problems and Troubleshooting

Please report bugs, problems, and other issues with VDM2C at <https://github.com/overturetool/vdm2c/issues>.

## Other Resources and Links

VDM2C is documented in the [Overture tool's user manual](http://overturetool.org/documentation/manuals.html).


## Issues closed

Please note that the interactive list is at <https://github.com/overturetool/vdm2c/milestone/10>
* [#72 closed - Reduce the size of the runtime library](https://github.com/overturetool/vdm2c/issues/72)
* [#71 closed - Naive identification of default super constructor](https://github.com/overturetool/vdm2c/issues/71)
* [#70 closed - CSV library to read file from "resources" directory](https://github.com/overturetool/vdm2c/issues/70)
* [#68 closed - Update VDM2C to use garbage collection](https://github.com/overturetool/vdm2c/issues/68)
* [#64 closed - Simplify Continuous Integration](https://github.com/overturetool/vdm2c/issues/64)
* [#61 closed - Case study models for compilation regression testing](https://github.com/overturetool/vdm2c/issues/61)
* [#6 closed - Create new release to support Overture FMU export](https://github.com/overturetool/vdm2c/issues/6)
