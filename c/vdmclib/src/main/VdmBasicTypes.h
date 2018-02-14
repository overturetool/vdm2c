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
 * VdmBasicTypes.h
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */


/*  VERSION: For the version of VDM2C used to generate this project, refer to one of the generated files.  */


#ifndef LIB_VDMBASICTYPES_H_
#define LIB_VDMBASICTYPES_H_

#include "Vdm.h"
#include "VdmGC.h"

#define ASSERT_CHECK_CHAR(s) assert((s->type ==  VDM_CHAR) && "Value is not a character")

/*
 * Boolean
 */

TVP vdmNot(TVP arg);
TVP vdmNotGC(TVP arg);
TVP vdmAnd(TVP a,TVP b);
TVP vdmAndGC(TVP a, TVP b);
TVP vdmOr(TVP a,TVP b);
TVP vdmOrGC(TVP a, TVP b);
TVP vdmXor(TVP a,TVP b);
TVP vdmXorGC(TVP a, TVP b);
TVP vdmImplies(TVP a,TVP b);
TVP vdmImpliesGC(TVP a, TVP b);
TVP vdmBiimplication(TVP a,TVP b);
TVP vdmBiimplicationGC(TVP a, TVP b);

/*
 * Numeric
 */
TVP vdmMinus(TVP arg);
TVP vdmMinusGC(TVP arg);
TVP vdmAbs(TVP arg);
TVP vdmAbsGC(TVP arg);
TVP vdmFloor(TVP arg);
TVP vdmFloorGC(TVP arg);
TVP vdmSum(TVP a,TVP b);
TVP vdmSumGC(TVP a,TVP b);
TVP vdmDifference(TVP a,TVP b);
TVP vdmDifferenceGC(TVP a,TVP b);
TVP vdmProduct(TVP a,TVP b);
TVP vdmProductGC(TVP a,TVP b);
TVP vdmDivision(TVP a,TVP b);
TVP vdmDivisionGC(TVP a,TVP b);
TVP vdmDiv(TVP a,TVP b);
TVP vdmDivGC(TVP a, TVP b);
TVP vdmRem(TVP a,TVP b);
TVP vdmRemGC(TVP a, TVP b);
TVP vdmMod(TVP a,TVP b);
TVP vdmModGC(TVP a, TVP b);
TVP vdmPower(TVP a,TVP b);
TVP vdmPowerGC(TVP a, TVP b);
TVP vdmNumericEqual(TVP a,TVP b);
TVP vdmNumericEqualGC(TVP a, TVP b);
TVP vdmGreaterThan(TVP a,TVP b);
TVP vdmGreaterThanGC(TVP a, TVP b);
TVP vdmGreaterOrEqual(TVP a,TVP b);
TVP vdmGreaterOrEqualGC(TVP a, TVP b);
TVP vdmLessThan(TVP a,TVP b);
TVP vdmLessThanGC(TVP a, TVP b);
TVP vdmLessOrEqual(TVP a,TVP b);
TVP vdmLessOrEqualGC(TVP a, TVP b);

/*
 * internal use
 */
bool isNumber(TVP val);
#ifndef NO_IS
TVP isInt(TVP val);
TVP isIntGC(TVP val);
TVP isReal(TVP val);
TVP isRealGC(TVP val);
TVP isBool(TVP val);
TVP isBoolGC(TVP val);
TVP isNat(TVP val);
TVP isNatGC(TVP val);
TVP isNat1(TVP val);
TVP isNat1GC(TVP val);
TVP isRat(TVP val);
TVP isRatGC(TVP val);
TVP isChar(TVP val);
TVP isCharGC(TVP val);
TVP isToken(TVP val);
TVP isTokenGC(TVP val);
#ifndef NO_RECORDS
TVP isRecord(TVP val, int recID);
TVP isRecordGC(TVP val, int recID);
#endif
TVP is(TVP v, char ot[]);
TVP isGC(TVP v, char ot[]);
#endif  /*  NO_IS  */
#ifndef NO_INHERITANCE
TVP isOfBaseClass(TVP val, int baseID);
TVP isOfBaseClassGC(TVP val, int baseID);
TVP sameBaseClass(TVP a, TVP b);
TVP sameBaseClassGC(TVP a, TVP b);
TVP sameClass(TVP a, TVP b);
TVP sameClassGC(TVP a, TVP b);
#endif
#if !defined(NO_IS) || !defined(NO_INHERITANCE)
TVP isOfClass(TVP val, int classID);
TVP isOfClassGC(TVP val, int classID);
#endif
int toInteger(TVP a);
double toDouble(TVP a);
bool toBool(TVP a);

#endif /* LIB_VDMBASICTYPES_H_ */
