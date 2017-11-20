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

#if !defined(NO_IS) || !defined(NO_INHERITANCE)
#include "VdmClassHierarchy.h"
#endif



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
	TVP res;

	res = vdmNot(arg);
	add_allocd_mem_node(res, from);

	return res;
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
	TVP res;

	res = vdmAnd(a, b);
	add_allocd_mem_node(res, from);

	return res;
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
	TVP res;

	res = vdmOr(a, b);
	add_allocd_mem_node(res, from);

	return res;
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
	TVP res;

	res = vdmXor(a, b);
	add_allocd_mem_node(res, from);

	return res;
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
	TVP res;

	res = vdmImplies(a, b);
	add_allocd_mem_node(res, from);

	return res;
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
	TVP res;

	res = vdmBiimplication(a, b);
	add_allocd_mem_node(res, from);

	return res;
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


#if !defined(NO_IS) || !defined(NO_INHERITANCE)
TVP isOfClass(TVP val, int classID)
{
	int searchID;
	int i, assoclen = sizeof(assoc) / sizeof(int);

	searchID = ((struct ClassType *)val->value.ptr)->classId;

	if(searchID == classID)
		return newBool(true);

	for(i = 0; i < assoclen; i += 2)
	{
		if(assoc[i] == searchID)
		{
			if(assoc[i + 1] == classID)
				return newBool(true);
			else
			{
				searchID = assoc[i + 1];
				i = -2;
			}
		}
	}

	return newBool(false);
}

TVP isOfClassGC(TVP val, int classID, TVP *from)
{
	TVP res;

	res = isOfClass(val, classID);
	add_allocd_mem_node(res, from);

	return res;
}
#endif


#ifndef NO_IS
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

TVP isNat(TVP v)
{
	if(v->type == VDM_INT)
	{
		if(v->value.intVal >= 0)
		{
			return newBool(true);
		}
	}
	return newBool(false);
}

TVP isNatGC(TVP v, TVP *from)
{
	if(v->type == VDM_INT)
	{
		if(v->value.intVal >= 0)
		{
			return newBoolGC(true, from);
		}
	}
	return newBoolGC(false, from);
}

TVP isNat1(TVP v)
{
	if(v->type == VDM_INT)
	{
		if(v->value.intVal > 0)
		{
			return newBool(true);
		}
	}
	return newBool(false);
}

TVP isNat1GC(TVP v, TVP *from)
{
	if(v->type == VDM_INT)
	{
		if(v->value.intVal > 0)
		{
			return newBoolGC(true, from);
		}
	}
	return newBoolGC(false, from);
}

TVP isRat(TVP v)
{
	return isReal(v);
}

TVP isRatGC(TVP v, TVP *from)
{
	return isRealGC(v, from);
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

TVP isChar(TVP v)
{
	if(v->type == VDM_CHAR)
		return newBool(true);
	return newBool(false);
}

TVP isCharGC(TVP v, TVP *from)
{
	if(v->type == VDM_CHAR)
		return newBoolGC(true, from);
	return newBoolGC(false, from);
}

TVP isToken(TVP v)
{
	if(v->type == VDM_TOKEN)
		return newBool(true);
	return newBool(false);
}

TVP isTokenGC(TVP v, TVP *from)
{
	if(v->type == VDM_TOKEN)
		return newBoolGC(true, from);
	return newBoolGC(false, from);
}

#ifndef NO_RECORDS
TVP isRecord(TVP val, int recID)
{
	if(val->type == VDM_RECORD)
		if(((struct ClassType *)val->value.ptr)->classId == recID)
			return newBool(true);
	return newBool(false);
}

TVP isRecordGC(TVP val, int recID, TVP *from)
{
	if(val->type == VDM_RECORD)
		if(((struct ClassType *)val->value.ptr)->classId == recID)
			return newBoolGC(true, from);
	return newBoolGC(false, from);
}
#endif

TVP is(TVP v, char ot[])
{
	int i;
	bool res = false;
	TVP isres;

	if(ot[0] == '0')
	{
		if(v == NULL)
		{
			res = true;
		}
		else
		{
			res = true;
			ot[0] = '1';
			isres = is(v, ot);
			res &= isres->value.boolVal;
			vdmFree(isres);
		}
	}
	else
	{
		/* No nesting inside sequence, set, record or map. */
		if(ot[1] != 'Q' && ot[1] != 'T' && ot[1] != 'P' && ot[1] != 'Z' && ot[1] != 'Y' && ot[1] != 'M')
		{
			switch(ot[1])
			{
			case 'i':  /*  VDM_INT  */
				res = (isInt(v))->value.boolVal;
				break;
			case 'd':  /*  VDM_REAL  */
				res = (isReal(v))->value.boolVal;
				break;
			case 'b':  /*  VDM_BOOL  */
				res = (isBool(v))->value.boolVal;
				break;
			case 'j':  /*  VDM_NAT  */
				res = (isNat(v))->value.boolVal;
				break;
			case 'k':  /*  VDM_NAT1  */
				res = (isNat1(v))->value.boolVal;
				break;
			case 'e':  /*  VDM_RAT  */
				res = (isRat(v))->value.boolVal;
				break;
			case 'c':  /*  VDM_CHAR  */
				res = (isChar(v))->value.boolVal;
				break;
			case 't':  /*  VDM_TOKEN  */
				res = (isToken(v))->value.boolVal;
				break;
			case 'W':  /*  VDM_CLASS  */
				res = (isOfClass(v, ot[2]))->value.boolVal;
				break;
#ifndef NO_RECORDS
			case 'R':  /*  VDM_CLASS  */
				res = (isRecord(v, ot[2]))->value.boolVal;
				break;
#endif
			default:
				break;
			};
		}
#ifndef NO_SEQS
		else if(ot[1] == 'Q')
		{
			if(v->type == VDM_SEQ)
			{
				res = true;

				for(i = 0; i < ((struct Collection *)(v->value.ptr))->size; i++)
				{
					isres = is(((struct Collection *)(v->value.ptr))->value[i], &(ot[2]));
					res &= isres->value.boolVal;
					vdmFree(isres);
				}
			}
		}
		else if(ot[1] == 'Z') /*  seq1  */
		{
			if(v->type == VDM_SEQ)
			{
				if(((struct Collection *)(v->value.ptr))->size > 0)
				{
					res = true;

					for(i = 0; i < ((struct Collection *)(v->value.ptr))->size; i++)
					{
						isres = is(((struct Collection *)(v->value.ptr))->value[i], &(ot[2]));
						res &= isres->value.boolVal;
						vdmFree(isres);
					}
				}
			}
		}
#endif
#ifndef NO_SETS
		else if(ot[1] == 'T')
		{
			if(v->type == VDM_SET)
			{
				res = true;

				for(i = 0; i < ((struct Collection *)(v->value.ptr))->size; i++)
				{
					isres = is(((struct Collection *)(v->value.ptr))->value[i], &(ot[2]));
					res &= isres->value.boolVal;
					vdmFree(isres);
				}
			}
		}
		else if(ot[1] == 'Y')  /*  set1  */
		{
			if(v->type == VDM_SET)
			{
				if(((struct Collection *)(v->value.ptr))->size > 0)
				{
					res = true;

					for(i = 0; i < ((struct Collection *)(v->value.ptr))->size; i++)
					{
						isres = is(((struct Collection *)(v->value.ptr))->value[i], &(ot[2]));
						res &= isres->value.boolVal;
						vdmFree(isres);
					}
				}
			}
		}
#endif
#ifndef NO_PRODUCTS
		else if(ot[1] == 'P')
		{
			if(v->type == VDM_PRODUCT)
			{
				res = true;

				char numFields = ot[2];
				int i = 0, field = 0;

				ot = &ot[3];

				while(field < numFields)
				{
					if(ot[i] == '*')
					{
						isres = is(((struct Collection *)(v->value.ptr))->value[field], &(ot[i + 1]));
						res &= isres->value.boolVal;
						vdmFree(isres);
						field++;
					}

					i++;
				}
			}
		}
#endif
#ifndef NO_MAPS
		else if(ot[1] == 'M')
		{
			if(v->type == VDM_MAP)
			{
				res = true;

				char numFields = 2;
				int i = 0, field = 0;

				ot = &ot[2];

				while(field < numFields)
				{
					if(ot[i] == '*')
					{
						isres = is(((struct Collection *)(v->value.ptr))->value[field], &(ot[i + 1]));
						res &= isres->value.boolVal;
						vdmFree(isres);
						field++;
					}

					i++;
				}
			}
		}
#endif
		else
		{}
	}

	return newBool(res);
}

TVP isGC(TVP v, char ot[], TVP *from)
{
	TVP res;

	res = is(v, ot);
	add_allocd_mem_node(res, from);

	return res;
}
#endif  /*  NO_IS  */

#ifndef NO_INHERITANCE
TVP isOfBaseClass(TVP val, int baseID)
{
	int searchID;
	int i, assoclen = sizeof(assoc) / sizeof(int);

	searchID = ((struct ClassType *)val->value.ptr)->classId;

	/*  First, base class must not inherit from any other.  */
	for(i = 0; i < assoclen; i += 2)
	{
		if(assoc[i] == baseID)
			return newBool(false);
	}

	/*  Current class is always base class.  */
	if(searchID == baseID)
		return newBool(true);



	for(i = 0; i < assoclen; i += 2)
	{
		if(assoc[i] == searchID)
		{
			if(assoc[i + 1] == baseID)
				return newBool(true);
			else
			{
				searchID = assoc[i + 1];
				i = 0;
			}
		}
	}

	return newBool(false);
}

TVP isOfBaseClassGC(TVP val, int baseID, TVP *from)
{
	TVP res;

	res = isOfBaseClass(val, baseID);
	add_allocd_mem_node(res, from);

	return res;
}

TVP sameClass(TVP a, TVP b)
{
	return newBool(((struct ClassType *)a->value.ptr)->classId == ((struct ClassType *)b->value.ptr)->classId);
}

TVP sameClassGC(TVP a, TVP b, TVP *from)
{
	return newBoolGC(((struct ClassType *)a->value.ptr)->classId == ((struct ClassType *)b->value.ptr)->classId, from);
}

TVP sameBaseClass(TVP a, TVP b)
{
	int i, assoclen = sizeof(assoc) / sizeof(int);
	TVP ares;
	TVP bres;
	bool res = false;

	for(i = 1; i < assoclen; i += 2)
	{
		ares = isOfClass(a, assoc[i]);
		bres = isOfClass(b, assoc[i]);

		res = res || (ares->value.boolVal && bres->value.boolVal);

		vdmFree(ares);
		vdmFree(bres);
	}

	return newBool(res);
}

TVP sameBaseClassGC(TVP a, TVP b, TVP *from)
{
	TVP res;

	res = sameBaseClass(a, b);
	add_allocd_mem_node(res, from);

	return res;
}
#endif

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
	TVP res;

	res = vdmMinus(arg);
	add_allocd_mem_node(res, from);

	return res;
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
	TVP res;

	res = vdmAbs(arg);
	add_allocd_mem_node(res, from);

	return res;

	return res;
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
	TVP res;

	res = vdmSum(a, b);
	add_allocd_mem_node(res, from);

	return res;

	return res;
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
	TVP res;

	res = vdmDifference(a, b);
	add_allocd_mem_node(res, from);

	return res;
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
	TVP res;

	res = vdmProduct(a, b);
	add_allocd_mem_node(res, from);

	return res;
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
	TVP res;

	res = vdmDiv(a, b);
	add_allocd_mem_node(res, from);

	return res;
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
	TVP res;

	res = vdmRem(a, b);
	add_allocd_mem_node(res, from);

	return res;
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
	TVP res;

	res = vdmMod(a, b);
	add_allocd_mem_node(res, from);

	return res;
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
