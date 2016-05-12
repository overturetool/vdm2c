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
#include "ClassStaticUser.h"
}

#define CHECK(methodId) TVP c=_Z15ClassStaticUserEV(NULL);\
TVP res=CALL_FUNC(ClassStaticUser,ClassStaticUser,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)

#define CHECK_FALSE(methodId) TVP c=_Z15ClassStaticUserEV(NULL);\
TVP res=CALL_FUNC(ClassStaticUser,ClassStaticUser,c,methodId);\
EXPECT_EQ (false,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST(Class_StaticMethodAccess, getStaticOperation)
{
	CHECK(CLASS_ClassStaticUser__Z18getStaticOperationEV);
}

TEST(Class_StaticMethodAccess, getOverriddenStaticOperation)
{
	CHECK_FALSE(CLASS_ClassStaticUser__Z28getOverriddenStaticOperationEV);
}

