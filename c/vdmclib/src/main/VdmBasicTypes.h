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
#include "VdmGC.h"

#define ASSERT_CHECK_CHAR(s) assert((s->type ==  VDM_CHAR) && "Value is not a character")

/*
 * Boolean
 */

TVP vdmNot(TVP arg);
TVP vdmNotGC(TVP arg, TVP *from);
TVP vdmAnd(TVP a,TVP b);
TVP vdmAndGC(TVP a, TVP b, TVP *from);
TVP vdmOr(TVP a,TVP b);
TVP vdmOrGC(TVP a, TVP b, TVP *from);
TVP vdmXor(TVP a,TVP b);
TVP vdmXorGC(TVP a, TVP b, TVP *from);
TVP vdmImplies(TVP a,TVP b);
TVP vdmImpliesGC(TVP a, TVP b, TVP *from);
TVP vdmBiimplication(TVP a,TVP b);
TVP vdmBiimplicationGC(TVP a, TVP b, TVP *from);

/*
 * Numeric
 */
TVP vdmMinus(TVP arg);
TVP vdmMinusGC(TVP arg, TVP *from);
TVP vdmAbs(TVP arg);
TVP vdmAbsGC(TVP arg, TVP *from);
TVP vdmFloor(TVP arg);
TVP vdmFloorGC(TVP arg, TVP *from);
TVP vdmSum(TVP a,TVP b);
TVP vdmSumGC(TVP a,TVP b, TVP *from);
TVP vdmDifference(TVP a,TVP b);
TVP vdmDifferenceGC(TVP a,TVP b, TVP *from);
TVP vdmProduct(TVP a,TVP b);
TVP vdmProductGC(TVP a,TVP b, TVP *from);
TVP vdmDivision(TVP a,TVP b);
TVP vdmDivisionGC(TVP a,TVP b, TVP *from);
TVP vdmDiv(TVP a,TVP b);
TVP vdmDivGC(TVP a, TVP b, TVP *from);
TVP vdmRem(TVP a,TVP b);
TVP vdmRemGC(TVP a, TVP b, TVP *from);
TVP vdmMod(TVP a,TVP b);
TVP vdmModGC(TVP a, TVP b, TVP *from);
TVP vdmPower(TVP a,TVP b);
TVP vdmPowerGC(TVP a, TVP b, TVP *from);
TVP vdmNumericEqual(TVP a,TVP b);
TVP vdmNumericEqualGC(TVP a, TVP b, TVP *from);
TVP vdmGreaterThan(TVP a,TVP b);
TVP vdmGreaterThanGC(TVP a, TVP b, TVP *from);
TVP vdmGreaterOrEqual(TVP a,TVP b);
TVP vdmGreaterOrEqualGC(TVP a, TVP b, TVP *from);
TVP vdmLessThan(TVP a,TVP b);
TVP vdmLessThanGC(TVP a, TVP b, TVP *from);
TVP vdmLessOrEqual(TVP a,TVP b);
TVP vdmLessOrEqualGC(TVP a, TVP b, TVP *from);

/*
 * internal use
 */
bool isNumber(TVP val);
TVP isInt(TVP);
TVP isReal(TVP);
TVP isBool(TVP);
int toInteger(TVP a);
double toDouble(TVP a);
bool toBool(TVP a);

#endif /* LIB_VDMBASICTYPES_H_ */
