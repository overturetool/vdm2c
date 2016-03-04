#ifndef CUSTOM_IO

#include "TypedValue.h"
#include "Vdm.h"

#ifndef CUSTOM_IO_H_
#define CUSTOM_IO_H_

TVP vdm_IO_freadval(TVP filename);
TVP vdm_IO_fwriteval(TVP filename, TVP val, TVP fdir);
TVP vdm_IO_writeval(TVP val);
void vdm_IO_printf(TVP format, TVP args);
void vdm_IO_println(TVP arg);
void vdm_IO_print(TVP arg);
TVP vdm_IO_ferror();
TVP vdm_IO_fecho(TVP filename, TVP text, TVP fdir);
TVP vdm_IO_echo(TVP text);

#endif

#endif
