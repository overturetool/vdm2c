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
#include "BBBB.h"
}

#define CHECK(methodId)\
TVP b=_Z4BBBBEV(NULL);\
TVP res=CALL_FUNC(BBBB,BBBB,b,methodId);\
EXPECT_EQ (true,res->value.boolVal);\
vdmFree(res);\
vdmFree(b)


TEST(ClassStaticInstanceVariableComplex, testInitialisedField)
{
	AAAA_static_init();
	CHECK(CLASS_BBBB__Z20testInitialisedFieldEV);
}

TEST(ClassStaticInstanceVariableComplex, testUpdatedField)
{
	AAAA_static_init();
	CHECK(CLASS_BBBB__Z16testUpdatedFieldEV);
}

TEST(ClassStaticInstanceVariableComplex, testUpdateStaticField)
{
	AAAA_static_init();
	CHECK(CLASS_BBBB__Z21testUpdateStaticFieldEV);
}

TEST(ClassStaticInstanceVariableComplex, testUpdateStaticEnclosingField)
{
	BBBB_static_init();
	CHECK(CLASS_BBBB__Z30testUpdateStaticEnclosingFieldEV);
}


