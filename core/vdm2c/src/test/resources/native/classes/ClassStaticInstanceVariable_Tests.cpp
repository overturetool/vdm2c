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
#include "UseClassStaticInstanceVariable.h"
}

#define CHECK(methodId) TVP c=_Z30UseClassStaticInstanceVariableEV(NULL);\
TVP res=CALL_FUNC(UseClassStaticInstanceVariable,UseClassStaticInstanceVariable,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

//#define CHECK_FALSE(methodId) TVP c=_Z15ClassStaticUserEV(NULL);\
//TVP res=CALL_FUNC(ClassStaticUser,ClassStaticUser,c,methodId);\
//EXPECT_EQ (false,res->value.boolVal);\
//vdmFree(res);\
//vdmFree(c)


TEST(Class_StaticInstanceVariable, getStaticInstanceVariable)
{
	CHECK(CLASS_UseClassStaticInstanceVariable__Z3op2EV);
}
