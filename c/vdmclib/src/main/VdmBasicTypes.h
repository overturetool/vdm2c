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
 * VdmBasicTypes.h
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */

#ifndef LIB_VDMBASICTYPES_H_
#define LIB_VDMBASICTYPES_H_

#include "Vdm.h"

#define ASSERT_CHECK_CHAR(s) assert((s->type ==  VDM_CHAR) && "Value is not a character")

/*
 * Boolean
 */

TVP vdmNot(TVP arg);
TVP vdmAnd(TVP a,TVP b);
TVP vdmOr(TVP a,TVP b);
TVP vdmXor(TVP a,TVP b);
TVP vdmImplies(TVP a,TVP b);
TVP vdmBiimplication(TVP a,TVP b);

/*
 * Numeric
 */
TVP vdmMinus(TVP arg);
TVP vdmAbs(TVP arg);
TVP vdmFloor(TVP arg);
TVP vdmSum(TVP a,TVP b);
TVP vdmDifference(TVP a,TVP b);
TVP vdmProduct(TVP a,TVP b);
TVP vdmDivision(TVP a,TVP b);
TVP vdmDiv(TVP a,TVP b);
TVP vdmRem(TVP a,TVP b);
TVP vdmMod(TVP a,TVP b);
TVP vdmPower(TVP a,TVP b);
TVP vdmEqual(TVP a,TVP b);
TVP vdmNotEqual(TVP a,TVP b);
TVP vdmGreaterThan(TVP a,TVP b);
TVP vdmGreaterOrEqual(TVP a,TVP b);
TVP vdmLessThan(TVP a,TVP b);
TVP vdmLessOrEqual(TVP a,TVP b);

/*
 * internal use
 */
bool isNumber(TVP val);
int toInteger(TVP a);
double toDouble(TVP a);
bool toBool(TVP a);

#endif /* LIB_VDMBASICTYPES_H_ */
