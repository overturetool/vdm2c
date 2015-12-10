/*
 * VdmBasicTypes.h
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */

#ifndef LIB_VDMBASICTYPES_H_
#define LIB_VDMBASICTYPES_H_

#include "TypedValue.h"


/*
 * Boolean
 */

TVP vdmNot(TVP arg);
TVP vdmAnd(TVP a,TVP b);
TVP vdmOr(TVP a,TVP b);
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


#endif /* LIB_VDMBASICTYPES_H_ */
