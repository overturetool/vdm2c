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
	double bv=toDouble(b);

	return newReal(av/bv);
}

TVP vdmDiv(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	//See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L444

	//FIXME: not implemented
	return NULL;
}

TVP vdmRem(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	//See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L628

	//FIXME: not implemented
	return NULL;
}

TVP vdmMod(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	//See https://github.com/overturetool/overture/blob/development/core/interpreter/src/main/java/org/overture/interpreter/eval/BinaryExpressionEvaluator.java#L575

	//FIXME: not implemented
	return NULL;
}

TVP vdmPower(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	//FIXME: not implemented
	return NULL;
}

TVP vdmLessThan(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	//FIXME: not implemented
	return NULL;
}

TVP vdmGreaterThan(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	//FIXME: not implemented
	return NULL;
}

TVP vdmLessOrEqual(TVP a,TVP b)
{	ASSERT_CHECK_NUMERIC(a);
	ASSERT_CHECK_NUMERIC(b);

	double av = toDouble(a);
	double bv=toDouble(b);

	return newBool(av<=bv);
}
