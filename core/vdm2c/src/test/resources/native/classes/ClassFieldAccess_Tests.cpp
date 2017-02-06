/*
 * ClassInstanceVariableSeqAssign_Tests.cpp
 *
 *  Created on: June, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "ClassFieldAccessAccessor.h"
}

#define CHECK(methodId) TVP c=_Z24ClassFieldAccessAccessorEV(NULL);\
TVP res=CALL_FUNC(ClassFieldAccessAccessor,ClassFieldAccessAccessor,c,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(c)


TEST_F(TestFlowFunctions, call)
{
	CHECK(CLASS_ClassFieldAccessAccessor__Z8setFieldEV);
}

TEST_F(TestFlowFunctions, sameFieldName)
{
  ClassFieldAccessAccessor_static_init();
	CHECK(CLASS_ClassFieldAccessAccessor__Z13sameFieldNameEV);
}


