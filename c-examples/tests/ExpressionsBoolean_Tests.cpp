#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include <stdio.h>
}
#define TP struct TypedValue*

TEST(Expression_Boolean, notExp)
{
	TP t = newBool(false);

	assert(t->type == VDM_BOOL && "Value is not a bool");
	TP res = newBool(!t->value.boolVal);
	EXPECT_EQ (true,res->value.boolVal);
	recursiveFree(res);

	recursiveFree(t);
}

TEST(Expression_Boolean, andExp)
{
	TP t = newBool(false);
	TP t1 = newBool(false);

	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TP res = newBool(t->value.boolVal && t1->value.boolVal);
	EXPECT_EQ (false,res->value.boolVal);
	recursiveFree(res);

	recursiveFree(t);
	recursiveFree(t1);
}

TEST(Expression_Boolean, orExp)
{
	TP t = newBool(true);
	TP t1 = newBool(false);

	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TP res = newBool(t->value.boolVal || t1->value.boolVal);
	EXPECT_EQ (true,res->value.boolVal);
	recursiveFree(res);

	recursiveFree(t);
	recursiveFree(t1);
}


TEST(Expression_Boolean, implicationExp)
{
	TP t = newBool(true);
	TP t1 = newBool(false);

	// A=> B == not A or B
	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TP res = newBool(!t->value.boolVal || t1->value.boolVal);
	EXPECT_EQ (false,res->value.boolVal);
	recursiveFree(res);

	recursiveFree(t);
	recursiveFree(t1);
}


TEST(Expression_Boolean, biimplicationExp)
{
	TP t = newBool(true);
	TP t1 = newBool(false);

	// A <=> B == p iff q ==(p → q) ∧ (q → p), or the XNOR (exclusive nor) === It is equivalent to "(not p or q) and (not q or p)
	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TP res = newBool((!t->value.boolVal || t1->value.boolVal) && (!t1->value.boolVal || t->value.boolVal));
	EXPECT_EQ (false,res->value.boolVal);
	recursiveFree(res);

	recursiveFree(t);
	recursiveFree(t1);
}

TEST(Expression_Boolean, equalityExp)
{
	TP t = newBool(false);
	TP t1 = newBool(false);

	// A = B
	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TP res = newBool(t->value.boolVal == t1->value.boolVal);
	EXPECT_EQ (true,res->value.boolVal);
	recursiveFree(res);

	recursiveFree(t);
	recursiveFree(t1);
}

TEST(Expression_Boolean, inequalityExp)
{
	TP t = newBool(true);
	TP t1 = newBool(false);

	// A = B
	assert(t->type == VDM_BOOL && "Value is not a bool");
	assert(t1->type == VDM_BOOL && "Value is not a bool");
	TP res = newBool(t->value.boolVal != t1->value.boolVal);
	EXPECT_EQ (true,res->value.boolVal);
	recursiveFree(res);

	recursiveFree(t);
	recursiveFree(t1);
}
