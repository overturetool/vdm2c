#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include <stdio.h>
}

TVP letIdentifierExp()
{
	//let a = true in a
	{
		TVP a = newBool(true);

		TVP ret = clone(a);

		//clean local malloc
		recursiveFree(a);
		return ret;
	}

}

TVP letIdentifierDontExp()
{

	//let a = true in a
	{
		TVP dont_1 = newBool(true);

		TVP tmp1 = newBool(true);
		TVP ret = clone(tmp1);

		//clean local malloc
		recursiveFree(dont_1);
		recursiveFree(tmp1);
		return ret;
	}

}

TVP letPatternMatch1()
{
	//let 1 = 1 in true
	{
		TVP tmp1 = newInt(1);
		TVP tmp2 = newInt(1);

		assert(equals(tmp1,tmp2) && "pattern dont not match");

		TVP tmp3 = newBool(true);

		TVP ret = clone(tmp3);
		recursiveFree(tmp1);
		recursiveFree(tmp2);
		recursiveFree(tmp3);
		return ret;
	}
}

TEST(Expression, letIdentifierExp)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = letIdentifierExp();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	recursiveFree(result);

	recursiveFree (TEST_TRUE);
}

TEST(Expression, letIdentifierDontExp)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = letIdentifierDontExp();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	recursiveFree(result);

	recursiveFree (TEST_TRUE);
}

TEST(Expression, letPatternMatch1)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = letPatternMatch1();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	recursiveFree(result);

	recursiveFree (TEST_TRUE);
}
