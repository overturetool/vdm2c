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
 * VdmBasicTypes.c
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */

#include "Vdm.h"
#include <math.h>



#define ASSERT_CHECK_BOOL(s) assert(s->type == VDM_BOOL && "Value is not a boolean")
#define ASSERT_CHECK_NUMERIC(s) assert((s->type == VDM_INT||s->type == VDM_NAT||s->type == VDM_NAT1||s->type == VDM_REAL||s->type == VDM_RAT) && "Value is not numeric")
#define ASSERT_CHECK_REAL(s) assert((s->type ==  VDM_REAL) && "Value is not real")
#define ASSERT_CHECK_INT(s) assert((s->type ==  VDM_INT) && "Value is not integer")
#define ASSERT_CHECK_CHAR(s) assert((s->type ==  VDM_CHAR) && "Value is not a character")


/*
 * Boolean
 */

TVP vdmNot(TVP arg)
{
	if(arg == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(arg);

	return newBool(!arg->value.boolVal);
}

TVP vdmNotGC(TVP arg, TVP *from)
{
	if(arg == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(arg);

	return newBoolGC(!arg->value.boolVal, from);
}

TVP vdmAnd(TVP a,TVP b)
{
	if(a == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(a);
	if(!a->value.boolVal)
		return newBool(false);

	if(b == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(b);
	return newBool(b->value.boolVal);
}

TVP vdmAndGC(TVP a, TVP b, TVP *from)
{
	if(a == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(a);
	if(!a->value.boolVal)
		return newBoolGC(false, from);

	if(b == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(b);
	return newBoolGC(b->value.boolVal, from);
}

TVP vdmOr(TVP a,TVP b)
{
	if(a == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(a);
	if(a->value.boolVal)
		return newBool(true);

	if(b == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(b);
	return newBool(b->value.boolVal);
}

TVP vdmOrGC(TVP a, TVP b, TVP *from)
{
	if(a == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(a);
	if(a->value.boolVal)
		return newBoolGC(true, from);

	if(b == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(b);
	return newBoolGC(b->value.boolVal, from);
}

TVP vdmXor(TVP a,TVP b)
{
	if(a == NULL || b == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(a);
	ASSERT_CHECK_BOOL(b);
	return newBool((!(a->value.boolVal) && b->value.boolVal) || ((a->value.boolVal) && !(b->value.boolVal)));
}

TVP vdmXorGC(TVP a, TVP b, TVP *from)
{
	if(a == NULL || b == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(a);
	ASSERT_CHECK_BOOL(b);
	return newBoolGC((!(a->value.boolVal) && b->value.boolVal) || ((a->value.boolVal) && !(b->value.boolVal)), from);
}

TVP vdmImplies(TVP a,TVP b)
{
	if(a == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(a);
	if(!a->value.boolVal)
		return newBool(true);

	if(b == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(b);
	return newBool(b->value.boolVal);
}

TVP vdmImpliesGC(TVP a, TVP b, TVP *from)
{
	if(a == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(a);
	if(!a->value.boolVal)
		return newBoolGC(true, from);

	if(b == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(b);
	return newBoolGC(b->value.boolVal, from);
}

TVP vdmBiimplication(TVP a,TVP b)
{
	if(a == NULL || b == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(a);
	ASSERT_CHECK_BOOL(b);
	return newBool((!a->value.boolVal || b->value.boolVal) && (!b->value.boolVal || a->value.boolVal));
}

TVP vdmBiimplicationGC(TVP a, TVP b, TVP *from)
{
	if(a == NULL || b == NULL)
		return NULL;

	ASSERT_CHECK_BOOL(a);
	ASSERT_CHECK_BOOL(b);
	return newBoolGC((!a->value.boolVal || b->value.boolVal) && (!b->value.boolVal || a->value.boolVal), from);
}

bool isNumber(TVP val)
{
	switch(val->type)
	{
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
	case VDM_REAL:
	case VDM_RAT:
		return true;
	default:
		return false;
	}
}

TVP isInt(TVP v)
{
	if(v->type == VDM_INT)
		return newBool(true);
	return newBool(false);
}

TVP isIntGC(TVP v, TVP *from)
{
	if(v->type == VDM_INT)
		return newBoolGC(true, from);
	return newBoolGC(false, from);
}

TVP isReal(TVP v)
{
	if(v->type == VDM_REAL)
		return newBool(true);
	return newBool(false);
}

TVP isRealGC(TVP v, TVP *from)
{
	if(v->type == VDM_REAL)
		return newBoolGC(true, from);
	return newBoolGC(false, from);
}

TVP isBool(TVP v)
{
	if(v->type == VDM_BOOL)
		return newBool(true);
	return newBool(false);
}

TVP isBoolGC(TVP v, TVP *from)
{
	if(v->type == VDM_BOOL)
		return newBoolGC(true, from);
	return newBoolGC(false, from);
}

/*
 * Numeric
 *
 * Note that the following functions are defined for all numbers
 */

double toDouble(TVP a)
{
	switch(a->type)
	{
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
		return (double) a->value.intVal;
	case VDM_REAL:
	case VDM_RAT:
		return a->value.doubleVal;
	default:
		FATAL_ERROR("Invalid type");
		return 0;
	}
}

int toInteger(TVP a)
{
	switch(a->type)
	{
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
		return a->value.intVal;
	case VDM_REAL:
		return a->value.doubleVal;
	default:
		FATAL_ERROR("Invalid type");
		return 0;
	}
}

bool toBool(TVP a)
{
	ASSERT_CHECK_BOOL(a);
	return a->value.boolVal;
}

TVP vdmMinus(TVP arg)
{
	ASSERT_CHECK_NUMERIC(arg);

	switch(arg->type)
	{
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
		return newInt(-arg->value.intVal);
	case VDM_REAL:
		return newReal(-arg->value.doubleVal);
	default:
		FATAL_ERROR("Invalid type");
		return NULL;
	}
}

TVP vdmMinusGC(TVP arg, TVP *from)
{
	ASSERT_CHECK_NUMERIC(arg);

	switch(arg->type)
	{
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
		return newIntGC(-arg->value.intVal, from);
	case VDM_REAL:
		return newRealGC(-arg->value.doubleVal, from);
	default:
		FATAL_ERROR("Invalid type");
		return NULL;
	}
}

TVP vdmAbs(TVP arg)
{
	ASSERT_CHECK_NUMERIC(arg);

	switch(arg->type)
	{
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
		return newInt(abs(arg->value.intVal));
	case VDM_REAL:
		return newReal(fabs(arg->value.doubleVal));
	default:
		FATAL_ERROR("Invalid type");
		return NULL;
	}
}

TVP vdmAbsGC(TVP arg, TVP *from)
{
	ASSERT_CHECK_NUMERIC(arg);

	switch(arg->type)
	{
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
		return newIntGC(abs(arg->value.intVal), from);
	case VDM_REAL:
		return newRealGC(fabs(arg->value.doubleVal), from);
	default:
		FATAL_ERROR("Invalid type");
		return NULL;
	}
}

TVP vdmFloor(TVP arg)
{
	ASSERT_CHECK_REAL(arg);

	return newInt(floor(arg->value.doubleVal));
}

TVP vdmFloorGC(TVP arg, TVP *from)
{
	ASSERT_CHECK_REAL(arg);

	/* TODO: Why do we return a Real, when floor is int in VDM?  */
	return newIntGC(floor(arg->value.doubleVal), from);
}

TVP vdmSum(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	if((a->type == VDM_INT || a->type == VDM_NAT || a->type == VDM_NAT1) &&
			(b->type == VDM_INT || b->type == VDM_NAT || b->type == VDM_NAT1))
		return newInt((int)(av + bv));

	return newReal(av+bv);
}

TVP vdmSumGC(TVP a,TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv=toDouble(b);

	if((a->type == VDM_INT || a->type == VDM_NAT || a->type == VDM_NAT1) &&
				(b->type == VDM_INT || b->type == VDM_NAT || b->type == VDM_NAT1))
			return newIntGC((int)(av + bv), from);

		return newRealGC(av+bv, from);
}

TVP vdmDifference(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv=toDouble(b);

	if((a->type == VDM_INT || a->type == VDM_NAT || a->type == VDM_NAT1) &&
			(b->type == VDM_INT || b->type == VDM_NAT || b->type == VDM_NAT1))
		return newInt((int)(av - bv));

	return newReal(av - bv);
}

TVP vdmDifferenceGC(TVP a,TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv=toDouble(b);

	if((a->type == VDM_INT || a->type == VDM_NAT || a->type == VDM_NAT1) &&
				(b->type == VDM_INT || b->type == VDM_NAT || b->type == VDM_NAT1))
			return newIntGC((int)(av - bv), from);

		return newRealGC(av - bv, from);
}

TVP vdmProduct(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv=toDouble(b);

	if((a->type == VDM_INT || a->type == VDM_NAT || a->type == VDM_NAT1) &&
			(b->type == VDM_INT || b->type == VDM_NAT || b->type == VDM_NAT1))
		return newInt((int)(av * bv));

	return newReal(av * bv);
}

TVP vdmProductGC(TVP a, TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv=toDouble(b);

	if((a->type == VDM_INT || a->type == VDM_NAT || a->type == VDM_NAT1) &&
				(b->type == VDM_INT || b->type == VDM_NAT || b->type == VDM_NAT1))
			return newIntGC((int)(av * bv), from);

		return newRealGC(av * bv, from);
}

TVP vdmDivision(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newReal(av/bv);
}

TVP vdmDivisionGC(TVP a,TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newRealGC(av/bv, from);
}

static long divi(double lv, double rv)
{
	/*
	 * There is often confusion on how integer division, remainder and modulus work on negative numbers. In fact,
	 * there are two valid answers to -14 div 3: either (the intuitive) -4 as in the Toolbox, or -5 as in e.g.
	 * Standard ML [Paulson91]. It is therefore appropriate to explain these operations in some detail. Integer
	 * division is defined using floor and real number division: x/y < 0: x div y = -floor(abs(-x/y)) x/y >= 0: x
	 * div y = floor(abs(x/y)) Note that the order of floor and abs on the right-hand side makes a difference, the
	 * above example would yield -5 if we changed the order. This is because floor always yields a smaller (or
	 * equal) integer, e.g. floor (14/3) is 4 while floor (-14/3) is -5.
	 */

	if (lv / rv < 0)
	{
		return (long) -floor(fabs(lv / rv));
	} else
	{
		return (long) floor(fabs(-lv / rv));
	}
}

TVP vdmDiv(TVP a, TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	/* See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L444  */

	ASSERT_CHECK_INT(a);
	ASSERT_CHECK_INT(b);

	int av = toDouble(a);
	int bv = toDouble(b);

	return newInt(divi(av,bv));
}

TVP vdmDivGC(TVP a, TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	/* See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L444  */

	ASSERT_CHECK_INT(a);
	ASSERT_CHECK_INT(b);

	int av = toDouble(a);
	int bv = toDouble(b);

	return newIntGC(divi(av,bv), from);
}

TVP vdmRem(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	/* See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L628  */
	ASSERT_CHECK_INT(a);
	ASSERT_CHECK_INT(b);

	int av = toDouble(a);
	int bv = toDouble(b);

	return newInt(av-bv*divi(av,bv));
}

TVP vdmRemGC(TVP a, TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	/* See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L628  */
	ASSERT_CHECK_INT(a);
	ASSERT_CHECK_INT(b);

	int av = toDouble(a);
	int bv = toDouble(b);

	return newIntGC(av-bv*divi(av,bv), from);
}

TVP vdmMod(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	/* See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L575  */
	ASSERT_CHECK_INT(a);
	ASSERT_CHECK_INT(b);

	double lv =(int) toDouble(a);
	double rv = (int)toDouble(b);

	if((a->type == VDM_INT || a->type == VDM_NAT || a->type == VDM_NAT1) &&
			(b->type == VDM_INT || b->type == VDM_NAT || b->type == VDM_NAT1))
		return newInt((int)(lv-rv*(long) floor(lv/rv)));

	return newReal(lv-rv*(long) floor(lv/rv));
}

TVP vdmModGC(TVP a, TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	/* See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L575  */
	ASSERT_CHECK_INT(a);
	ASSERT_CHECK_INT(b);

	double lv =(int) toDouble(a);
	double rv = (int)toDouble(b);

	if((a->type == VDM_INT || a->type == VDM_NAT || a->type == VDM_NAT1) &&
				(b->type == VDM_INT || b->type == VDM_NAT || b->type == VDM_NAT1))
			return newIntGC((int)(lv-rv*(long) floor(lv/rv)), from);

		return newRealGC(lv-rv*(long) floor(lv/rv), from);
}

TVP vdmPower(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newReal(pow(av,bv));
}

TVP vdmPowerGC(TVP a, TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newRealGC(pow(av,bv), from);
}

TVP vdmNumericEqual(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av==bv);
}

TVP vdmNumericEqualGC(TVP a, TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBoolGC(av==bv, from);
}

TVP vdmGreaterThan(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av>bv);
}

TVP vdmGreaterThanGC(TVP a,TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBoolGC(av>bv, from);
}

TVP vdmGreaterOrEqual(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av>=bv);
}

TVP vdmGreaterOrEqualGC(TVP a, TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBoolGC(av>=bv, from);
}

TVP vdmLessThan(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av<bv);
}

TVP vdmLessThanGC(TVP a, TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBoolGC(av<bv, from);
}

TVP vdmLessOrEqual(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av<=bv);
}

TVP vdmLessOrEqualGC(TVP a, TVP b, TVP *from)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBoolGC(av <= bv, from);
}
