#include "gtest/gtest.h"

extern "C"
{
#include "lib/Vdm.h"
#include <stdio.h>
}


TEST(Expression_Boolean, notExp)
{
	TVP t = newBool(false);

	assert(t->type == VDM_BOOL && "Value is not a bool");
	TVP res = vdmNot(t);
	EXPECT_EQ (true,res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
}

TEST(Expression_Boolean, andExp)
{
	TVP t = newBool(false);
	TVP t1 = newBool(false);

	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TVP res = vdmAnd(t,t1);
	EXPECT_EQ (false,res->value.boolVal);
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
	EXPECT_EQ (true,res->value.boolVal);
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
	EXPECT_EQ (false,res->value.boolVal);
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
	EXPECT_EQ (false,res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	vdmFree(t1);
}

TEST(Expression_Boolean, equalityExp)
{
	TVP t = newBool(false);
	TVP t1 = newBool(false);

	// A = B
	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TVP res = vdmEquals(t,t1);
	EXPECT_EQ (true,res->value.boolVal);
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
	EXPECT_EQ (true,res->value.boolVal);
	vdmFree(res);

	vdmFree(t);
	vdmFree(t1);
}
