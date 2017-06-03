/*
 * ClassStaticMethodAccess_Tests.cpp
 *
 *  Created on: Jan 10, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "BBBB.h"
}

#define CHECK(methodId)\
TVP b=_Z4BBBBEV(NULL);\
TVP res=CALL_FUNC(BBBB,BBBB,b,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(b)


TEST_F(TestFlowFunctions, testInitialisedField)
{
	AAAA_static_init();
	CHECK(CLASS_BBBB__Z20testInitialisedFieldEV);
}

TEST_F(TestFlowFunctions, testUpdatedField)
{
	AAAA_static_init();
	CHECK(CLASS_BBBB__Z16testUpdatedFieldEV);
}

TEST_F(TestFlowFunctions, testUpdateStaticField)
{
	AAAA_static_init();
	CHECK(CLASS_BBBB__Z21testUpdateStaticFieldEV);
}

TEST_F(TestFlowFunctions, testUpdateStaticEnclosingField)
{
	BBBB_static_init();
	CHECK(CLASS_BBBB__Z30testUpdateStaticEnclosingFieldEV);
}


