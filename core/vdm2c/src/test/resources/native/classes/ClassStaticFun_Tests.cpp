/*
 * ClassStaticMethodAccess_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassFunCall.h"
}

#define CHECK(methodId) TVP c=_Z12ClassFunCallEV(NULL);\
TVP res=CALL_FUNC(ClassFunCall,ClassFunCall,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

#define CHECK_FALSE(methodId) TVP c=_Z12ClassFunCallEV(NULL);\
TVP res=CALL_FUNC(ClassFunCall,ClassFunCall,c,methodId);\
EXPECT_EQ (false,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(Class_StaticMethodAccess, op)
{
	CHECK(CLASS_ClassFunCall__Z2opEV);
}

TEST(Class_StaticMethodAccess, op2)
{
	CHECK(CLASS_ClassFunCall__Z3op2EV);
}

TEST(Class_StaticMethodAccess, externalfn)
{
	CHECK(CLASS_ClassFunCall__Z10externalfnEV);
}

TEST(Class_StaticMethodAccess, localfn)
{
	CHECK_FALSE(CLASS_ClassFunCall__Z7localfnEV);
}