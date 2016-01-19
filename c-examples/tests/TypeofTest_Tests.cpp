#include "gtest/gtest.h"

extern "C"
{
#include "lib/Vdm.h"
#include "classes/A.h"
#include "classes/B.h"
#include "classes/C.h"
#include <stdio.h>
#include "typeoftest.h"
}

TEST(TypeofTest, test1)
{
	for (int i = 0; i < 9; i++)
		EXPECT_EQ(true, typeoftest(i));
}
