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
#include "CCC.h"
#include "BBB.h"
}

#define CHECK(methodId, obj,clazz)\
TVP res=CALL_FUNC(clazz,clazz,obj,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(obj)


TEST(ClassStaticInstanceVariable, testInitialisedField)
{
	AAA_static_init();
	CHECK(CLASS_CCC__Z20testInitialisedFieldEV, _Z3CCCEV(NULL), CCC);
}

TEST(ClassStaticInstanceVariable, testUpdatedField)
{
	AAA_static_init();
	CHECK(CLASS_CCC__Z16testUpdatedFieldEV, _Z3CCCEV(NULL), CCC);
}

TEST(ClassStaticInstanceVariable, testInitialisedFieldSubClass)
{
	AAA_static_init();
	CHECK(CLASS_BBB__Z28testInitialisedFieldSubClassEV, _Z3BBBEV(NULL), BBB);
}

