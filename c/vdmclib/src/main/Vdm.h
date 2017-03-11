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
 */

/*
 * Vdm.h
 *
 *  Created on: Jan 19, 2016
 *      Author: kel
 */

#ifndef LIB_VDM_H_
#define LIB_VDM_H_

/*
 * Support for google test in e.g. pattern matching
 * The VDM semantics requires the program to SEQ-fault,
 *  but this is not appreciated in the testing framework
 */
#ifndef FATAL_ERROR
#define FATAL_ERROR(message) exit(EXIT_FAILURE)
#endif

#include <string.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include "VdmModelFeatures.h"
#include "TypedValue.h"
#include "VdmBasicTypes.h"
#include "VdmSet.h"
#include "VdmSeq.h"
#include "VdmMap.h"
#include "VdmClass.h"
#include "VdmProduct.h"

#endif /* LIB_VDM_H_ */
