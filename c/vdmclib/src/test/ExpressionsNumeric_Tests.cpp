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

#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include "VdmGC.h"
#include <stdio.h>
}


TEST(Expression_Numeric, minusExp)
{
	TVP t = newInt(5);

	assert(t->type == VDM_INT && "Value is not a integer");
	TVP res = vdmMinus(t);
	EXPECT_EQ(-5,res->value.intVal);

	vdmFree(t);
	vdmFree(res);
}

TEST(Expression_Numeric, minusExpGC)
{
	vdm_gc_init();

	TVP t = newIntGC(5);

	assert(t->type == VDM_INT && "Value is not a integer");
	TVP res = vdmMinusGC(t);


	EXPECT_EQ(-5,res->value.intVal);

	vdm_gc();

	vdm_gc_shutdown();
}

TEST(Expression_Numeric, absExp)
{
	TVP t = newInt(-5);

	assert(t->type == VDM_INT && "Value is not a integer");
	TVP res = vdmMinus(t);
	EXPECT_EQ(5,res->value.intVal);

	vdmFree(t);
	vdmFree(res);
}

TEST(Expression_Numeric, absExpGC)
{
	vdm_gc_init();

	TVP t = newIntGC(-5);

	assert(t->type == VDM_INT && "Value is not a integer");

	TVP res = vdmMinusGC(t);

	EXPECT_EQ(5,res->value.intVal);

	vdm_gc();
}

TEST(Expression_Numeric, floorExp)
{
	TVP t = newReal(5.6);

	assert(t->type == VDM_REAL && "Value is not a real");
	TVP res = vdmFloor(t);
	EXPECT_EQ(5,res->value.intVal);
}

TEST(Expression_Numeric, sumExp)
{
	TVP t1 = newInt(5);
	TVP t2 = newInt(4);

	assert(t1->type == VDM_INT && "Value is not a integer");
	assert(t2->type == VDM_INT && "Value is not a integer");
	TVP res = vdmSum(t1,t2);
	EXPECT_EQ(9,res->value.intVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);

	t1 = newReal(6);
	t2 = newReal(5);
	res = vdmSum(t1, t2);

	EXPECT_EQ(11, res->value.doubleVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, differenceExp)
{
	TVP t1 = newInt(5);
	TVP t2 = newInt(4);

	assert(t1->type == VDM_INT && "Value is not a integer");
	assert(t2->type == VDM_INT && "Value is not a integer");
	TVP res = vdmDifference(t1,t2);
	EXPECT_EQ(1,res->value.intVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);

	t1 = newReal(6);
	t2 = newReal(5);
	res = vdmDifference(t1, t2);

	EXPECT_EQ(1, res->value.doubleVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, productExp)
{
	TVP t1 = newReal(6);
	TVP t2 = newReal(7);

	assert(t1->type == VDM_REAL && "Value is not a real");
	assert(t2->type == VDM_REAL && "Value is not a real");
	TVP res = vdmProduct(t1,t2);
	EXPECT_EQ(42,res->value.doubleVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);

	t1 = newReal(6);
	t2 = newReal(5);
	res = vdmProduct(t1, t2);

	EXPECT_EQ(30, res->value.doubleVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, divisionExp)
{
	TVP t1 = newInt(32);
	TVP t2 = newInt(8);

	assert(t1->type == VDM_INT && "Value is not a integer");
	assert(t2->type == VDM_INT && "Value is not a integer");
	TVP res = vdmDivision(t1,t2);
	EXPECT_EQ(4,res->value.doubleVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, divExp)
{
	TVP t1 = newInt(20);
	TVP t2 = newInt(4);

	assert(t1->type == VDM_INT && "Value is not a integer");
	assert(t2->type == VDM_INT && "Value is not a integer");
	TVP res = vdmDiv(t1,t2);
	EXPECT_EQ(5,res->value.intVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, remExp)
{
	TVP t1 = newInt(20);
	TVP t2 = newInt(6);

	assert(t1->type == VDM_INT && "Value is not a integer");
	assert(t2->type == VDM_INT && "Value is not a integer");
	TVP res = vdmRem(t1,t2);
	EXPECT_EQ(2,res->value.intVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, modExp)
{
	TVP t1 = newInt(23);
	TVP t2 = newInt(6);

	assert(t1->type == VDM_INT && "Value is not a integer");
	assert(t2->type == VDM_INT && "Value is not a integer");
	TVP res = vdmRem(t1,t2);
	EXPECT_EQ(5,res->value.intVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, powerExp)
{
	TVP t1 = newReal(4);
	TVP t2 = newReal(3);

	assert(t1->type == VDM_REAL && "Value is not a real");
	assert(t2->type == VDM_REAL && "Value is not a real");
	TVP res = vdmPower(t1,t2);
	EXPECT_EQ(64,res->value.doubleVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

// Numeric boolean

TEST(Expression_Numeric, equalExp)
{
	TVP t1 = newReal(6);
	TVP t2 = newReal(6);

	assert(t1->type == VDM_REAL && "Value is not a real");
	assert(t2->type == VDM_REAL && "Value is not a real");
	TVP res = vdmNumericEqual(t1,t2);
	EXPECT_EQ(true,res->value.boolVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, greaterExp)
{
	TVP t1 = newReal(12);
	TVP t2 = newReal(6);

	assert(t1->type == VDM_REAL && "Value is not a real");
	assert(t2->type == VDM_REAL && "Value is not a real");
	TVP res = vdmGreaterThan(t1,t2);
	EXPECT_EQ(true,res->value.boolVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, greaterOrEqualExp1)
{
	TVP t1 = newReal(8);
	TVP t2 = newReal(6);

	assert(t1->type == VDM_REAL && "Value is not a real");
	assert(t2->type == VDM_REAL && "Value is not a real");
	TVP res = vdmGreaterOrEqual(t1,t2);
	EXPECT_EQ(true,res->value.boolVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, greaterOrEqualExp2)
{
	TVP t1 = newReal(8);
	TVP t2 = newReal(8);

	assert(t1->type == VDM_REAL && "Value is not a real");
	assert(t2->type == VDM_REAL && "Value is not a real");
	TVP res = vdmGreaterOrEqual(t1,t2);
	EXPECT_EQ(true,res->value.boolVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, lessExp)
{
	TVP t1 = newReal(12);
	TVP t2 = newReal(18);

	assert(t1->type == VDM_REAL && "Value is not a real");
	assert(t2->type == VDM_REAL && "Value is not a real");
	TVP res = vdmLessThan(t1,t2);
	EXPECT_EQ(true,res->value.boolVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, lessOrEqualExp1)
{
	TVP t1 = newReal(18);
	TVP t2 = newReal(23);

	assert(t1->type == VDM_REAL && "Value is not a real");
	assert(t2->type == VDM_REAL && "Value is not a real");
	TVP res = vdmLessOrEqual(t1,t2);
	EXPECT_EQ(true,res->value.boolVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}

TEST(Expression_Numeric, lessOrEqualExp2)
{
	TVP t1 = newReal(11);
	TVP t2 = newReal(11);

	assert(t1->type == VDM_REAL && "Value is not a real");
	assert(t2->type == VDM_REAL && "Value is not a real");
	TVP res = vdmLessOrEqual(t1,t2);
	EXPECT_EQ(true,res->value.boolVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}
