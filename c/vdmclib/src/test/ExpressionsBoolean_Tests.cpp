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
#include <stdio.h>
}


TEST(Expression_Boolean, notExp)
{
	TVP t = newBool(false);

	assert(t->type == VDM_BOOL && "Value is not a bool");
	TVP res = vdmNot(t);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	t = NULL;
	res = vdmNot(t);
	EXPECT_EQ(NULL, res);

}

TEST(Expression_Boolean, andExp)
{
	TVP t = newBool(false);
	TVP t1 = newBool(false);

	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TVP res = vdmAnd(t,t1);
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);

	vdmFree(t);

	t = NULL;
	res = vdmAnd(t, t1);
	EXPECT_EQ(NULL, res);
	vdmFree(res);

	vdmFree(t1);
	t = newBool(true);
	t1 = NULL;
	res = vdmAnd(t, t1);
	EXPECT_EQ(NULL, res);
	vdmFree(res);

	t1 = newBool(false);
	res = vdmAnd(t, t1);
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	t = NULL;
	res = vdmAnd(t1, t);
	EXPECT_FALSE(res->value.boolVal);

	vdmFree(res);
	vdmFree(t);
	vdmFree(t1);

}

TEST(Expression_Boolean, orExp)
{
	TVP t = newBool(true);
	TVP t1 = newBool(false);

	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TVP res = vdmOr(t,t1);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	t = NULL;
	res = vdmOr(t, t1);
	EXPECT_EQ(NULL, res);
	vdmFree(res);

	vdmFree(t1);
	t = newBool(false);
	t1 = NULL;
	res = vdmOr(t, t1);
	EXPECT_EQ(NULL, res);
	vdmFree(res);

	t1 = newBool(true);
	res = vdmOr(t, t1);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	t = NULL;
	res = vdmOr(t1, t);
	EXPECT_TRUE(res->value.boolVal);

	vdmFree(res);
	vdmFree(t);
	vdmFree(t1);
}

TEST(Expression_Boolean, xorExp)
{
	TVP t = newBool(true);
	TVP t1 = newBool(false);

	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TVP res = vdmXor(t,t1);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	t = NULL;
	res = vdmXor(t, t1);
	EXPECT_EQ(NULL, res);

	vdmFree(res);
	vdmFree(t);
	vdmFree(t1);
}

TEST(Expression_Boolean, implicationExp)
{
	TVP t = newBool(true);
	TVP t1 = newBool(false);

	// A=> B == not A or B

	TVP res =vdmImplies(t,t1);
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	t = NULL;
	res = vdmImplies(t, t1);
	EXPECT_EQ(NULL, res);
	vdmFree(res);

	vdmFree(t1);
	t = newBool(true);
	t1 = NULL;
	res = vdmImplies(t, t1);
	EXPECT_EQ(NULL, res);
	vdmFree(res);

	t1 = newBool(false);
	res = vdmImplies(t, t1);
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	t = NULL;
	res = vdmImplies(t1, t);
	EXPECT_TRUE(res->value.boolVal);

	vdmFree(res);
	vdmFree(t);
	vdmFree(t1);
}


TEST(Expression_Boolean, biimplicationExp)
{
	TVP t = newBool(true);
	TVP t1 = newBool(false);

	// A <=> B == p iff q ==(p → q) ∧ (q → p), or the XNOR (exclusive nor) === It is equivalent to "(not p or q) and (not q or p)
	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TVP res =vdmBiimplication(t,t1); //newBool((!t->value.boolVal || t1->value.boolVal) && (!t1->value.boolVal || t->value.boolVal));
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	vdmFree(t1);
	t = NULL;
	t1 = NULL;
	res = vdmBiimplication(t, t1);
	EXPECT_EQ(NULL, res);
	res = vdmBiimplication(t1, t);
}

TEST(Expression_Boolean, equalityExp)
{
	TVP t = newBool(false);
	TVP t1 = newBool(false);

	// A = B
	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TVP res = vdmEquals(t,t1);
	EXPECT_TRUE(res->value.boolVal);
  vdmFree(res);

  vdmFree(t1);
  t1 = newUnknown();
	res = vdmEquals(t,t1);
	EXPECT_FALSE(res->value.boolVal);
  vdmFree(res);
  
  vdmFree(t);
  t = newUnknown();
	res = vdmEquals(t,t1);
	EXPECT_TRUE(res->value.boolVal);

  vdmFree(res);
	vdmFree(t);
	vdmFree(t1);
}

TEST(Expression_Boolean, inequalityExp)
{
	TVP t = newBool(true);
	TVP t1 = newBool(false);

	// A = B
	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TVP res = vdmInEquals(t,t1);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	vdmFree(t1);
}
