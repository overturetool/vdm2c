/*
 * VdmBasicTypes.c
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */

#include "VdmBasicTypes.h"
#include <math.h>

#define ASSERT_CHECK_BOOL(s) assert(s->type == VDM_BOOL && "Value is not a boolean")
#define ASSERT_CHECK_NUMERIC(s) assert((s->type == VDM_INT||s->type == VDM_INT1||s->type == VDM_REAL) && "Value is not numeric")
#define ASSERT_CHECK_REAL(s) assert((s->type ==  VDM_REAL) && "Value is not real")
#define ASSERT_CHECK_INT(s) assert((s->type ==  VDM_INT) && "Value is not integer")

/*
 * Boolean
 */

TVP vdmNot(TVP arg)
{
	ASSERT_CHECK_BOOL(arg);
	return newBool(!arg->value.boolVal);
}

TVP vdmAnd(TVP a,TVP b)
{
	ASSERT_CHECK_BOOL(a);
	ASSERT_CHECK_BOOL(b);
	return newBool(a->value.boolVal && b->value.boolVal);
}
TVP vdmOr(TVP a,TVP b)
{
	ASSERT_CHECK_BOOL(a);
	ASSERT_CHECK_BOOL(b);
	return newBool(a->value.boolVal || b->value.boolVal);
}

TVP vdmImplies(TVP a,TVP b)
{
	ASSERT_CHECK_BOOL(a);
	ASSERT_CHECK_BOOL(b);
	return newBool(!a->value.boolVal || b->value.boolVal);
}

TVP vdmBiimplication(TVP a,TVP b)
{
	ASSERT_CHECK_BOOL(a);
	ASSERT_CHECK_BOOL(b);
	return newBool((!a->value.boolVal || b->value.boolVal) && (!b->value.boolVal || a->value.boolVal));
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
		case VDM_INT1:
		return (double) a->value.intVal;
		case VDM_REAL:
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
		case VDM_INT1:
		return a->value.intVal;
		case VDM_REAL:
		//return a->value.doubleVal;
		default:
		FATAL_ERROR("Invalid type");
		return 0;
	}
}

TVP vdmMinus(TVP arg)
{
	ASSERT_CHECK_NUMERIC(arg);

	switch(arg->type)
	{
		case VDM_INT:
		case VDM_INT1:
		return newInt(-arg->value.intVal);
		case VDM_REAL:
		return newReal(-arg->value.doubleVal);
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
		case VDM_INT1:
		return newInt(abs(arg->value.intVal));
		case VDM_REAL:
		return newReal(fabs(arg->value.doubleVal));
		default:
		FATAL_ERROR("Invalid type");
		return NULL;
	}
}

TVP vdmFloor(TVP arg)
{
	ASSERT_CHECK_REAL(arg);

	switch(arg->type)
	{
		case VDM_REAL:
		return newReal(floor(arg->value.doubleVal));
		default:
		FATAL_ERROR("Invalid type");
		return NULL;
	}

}

TVP vdmSum(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv=toDouble(b);

	return newReal(av+bv);
}

TVP vdmDifference(TVP a,TVP b)
{
	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv=toDouble(b);

	return newReal(av-bv);
}

TVP vdmProduct(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv=toDouble(b);

	return newReal(av*bv);
}

TVP vdmDivision(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newReal(av/bv);
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
	}
	else
	{
		return (long) floor(fabs(-lv / rv));
	}
}

TVP vdmDiv(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	//See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L444

	ASSERT_CHECK_INT(a);
	ASSERT_CHECK_INT(b);

	int av = toDouble(a);
	int bv = toDouble(b);

	return newInt(divi(av,bv));
}

TVP vdmRem(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	//See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L628
	ASSERT_CHECK_INT(a);
	ASSERT_CHECK_INT(b);

	int av = toDouble(a);
	int bv = toDouble(b);

	return newInt(av-bv*divi(av,bv));
}

TVP vdmMod(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	//See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L575
	ASSERT_CHECK_INT(a);
	ASSERT_CHECK_INT(b);

	int av = toDouble(a);
	int bv = toDouble(b);

	return newInt(bv-av*(long) floor(av/bv));
}

TVP vdmPower(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newReal(pow(av,bv));
}

TVP vdmEqual(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av==bv);
}

TVP vdmNotEqual(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av!=bv);
}

TVP vdmGreaterThan(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av>bv);
}

TVP vdmGreaterOrEqual(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av>=bv);
}

TVP vdmLessThan(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av<bv);
}

TVP vdmLessOrEqual(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv = toDouble(b);

	return newBool(av<=bv);
}


