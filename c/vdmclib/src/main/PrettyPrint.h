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
 * <http:XXXwww.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

/*
 * PrettyPrint.h
 *
 *  Created on: Nov 20, 2015
 *      Author: Victor Bandur
 */

#ifndef PRETTYPRINT_H_
#define PRETTYPRINT_H_

#if !defined(NO_IO) || !defined(NO_CSV) || defined(CUSTOM_IO)

#include <stdlib.h>
#include <stdbool.h>

#include "Vdm.h"

/* Eclipse hack  */
#if !defined(va_arg)
#define va_arg(ap,TVP) NULL /* just for Eclipse must not be defined  */
#endif


char* toString(TVP val);

#endif /* NO_IO */
#endif /* PRETTYPRINT_H_ */
