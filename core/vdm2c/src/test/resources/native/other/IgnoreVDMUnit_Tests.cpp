/*
 * IgnoreVDMUnit_Tests.cpp
 *
 *  Created on: June 6, 2016
 *      Author: Victor Bandur
 */


#include "gtest/gtest.h"
#include "TestFlowFunctions.h"

#include <stdio.h>

extern "C"
{
#include <stdio.h>

}

TEST_F(TestFlowFunctions, fileNotGenerated)
{
	FILE *i = NULL;
	i = fopen("VDMUnitTest.c", "r");
	EXPECT_EQ(true, i == NULL);
}

