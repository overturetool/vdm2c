//User has not provided their own IO library.

#ifndef IOLIB_H_
#define IOLIB_H_

#include "Vdm.h"
#include "PrettyPrint.h"


/*
 * #%~
 * The VDM to C Code Generator
 * %%
 * Copyright (C) 2015 - 2016 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 *
 */

/*
 * IO.h
 *
 *  Created on: February, 2015
 *      Author: Victor Bandur
 */

char* unpackString(TVP charseq);

#if !defined(NO_IO) || !defined(NO_CSV)

#ifndef NO_SEQS
TVP vdm_IO_echo(TVP text);
void vdm_IO_println(TVP arg);
void vdm_IO_print(TVP arg);
#endif

TVP vdm_IO_freadval(TVP filename);
TVP vdm_IO_fwriteval(TVP filename, TVP val, TVP fdir);
TVP vdm_IO_writeval(TVP val);
void vdm_IO_printf(TVP format, TVP args);
TVP vdm_IO_ferror();
TVP vdm_IO_fecho(TVP filename, TVP text, TVP fdir);


#endif /* NO_IO or NO_CSV */
#endif /* IOLIB_H_ */
