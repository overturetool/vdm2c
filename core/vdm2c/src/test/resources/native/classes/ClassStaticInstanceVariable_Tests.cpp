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
#include "C.h"
#include "B.h"
}

#define CHECK(methodId, obj,clazz)\
TVP res=CALL_FUNC(clazz,clazz,obj,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(obj)


TEST(ClassStaticInstanceVariable, testInitialisedField)
{
	A_static_init();
	CHECK(CLASS_C__Z20testInitialisedFieldEV, _Z1CEV(NULL), C);
}

TEST(ClassStaticInstanceVariable, testUpdatedField)
{
	A_static_init();
	CHECK(CLASS_C__Z16testUpdatedFieldEV, _Z1CEV(NULL), C);
}

TEST(ClassStaticInstanceVariable, testInitialisedFieldSubClass)
{
	A_static_init();
	CHECK(CLASS_B__Z28testInitialisedFieldSubClassEV, _Z1BEV(NULL), B);
}

