#ifndef CSVLIB_H_
#define CSVLIB_H_

#include "Vdm.h"
#include "IOLib.h"
#include "VdmProduct.h"
#include "VdmSeq.h"


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
 * CSVLib.h
 *
 *  Created on: Jan, 2017
 *      Author: Victor Bandur
 */

#ifndef NO_CSV

TVP vdm_CSV_flinecount(TVP f);
TVP vdm_CSV_freadval(TVP f, TVP index);
TVP vdm_CSV_fwriteval(TVP filename, TVP val, TVP fdir);
TVP vdm_CSV_ferror();

#endif /* CSVLIB_H_ */
#endif /* NO_CSV */
