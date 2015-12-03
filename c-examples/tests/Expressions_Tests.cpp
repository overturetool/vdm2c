#include "gtest/gtest.h"

#define FATAL_ERROR(message) fprintf (stderr, "%s\n",message);throw message

extern "C"
{
#include "lib/TypedValue.h"
#include "lib/VdmProduct.h"
#include "lib/PatternBindMatch.h"
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

TVP letPatternMatch3()
{
	//let mk_(a,2) = mk_(1,2) in true
	{
		//mk_(a,2)
		TVP var1 = newInt(2);
		TVP var2 = newProduct(2);
		productSet(var2,2,var1);
		recursiveFree(var1);

		//mk_(1,2)
		TVP var3 = newInt(1);
		TVP var4 = newInt(2);
		TVP var5 = newProduct(2);
		productSet(var5,1,var3);
		recursiveFree(var3);
		productSet(var5,2,var4);
		recursiveFree(var4);

		TVP res=NULL;
		//match
		if( patternMatchBind(var2,var5))
		{
			TVP local1 = productGet(var2,1);
			res= newBool(true);
			recursiveFree(local1);
		} else
		{
			recursiveFree(var2);
			recursiveFree(var5);
			FATAL_ERROR("Pattern match error"); //Pattern match error
		}

		recursiveFree(var2);
		recursiveFree(var5);

		return res;
	}

}

/*let b = 2 in
 let 1 = b in true;*/
TVP letFilter1Exp()
{
	TVP res =NULL;
	{ //let b = 2 in
	  // b
		TVP tmp1 = newInt(2);
		{
			//let 1 = b in
			TVP tmp2 = newInt(1);

			if( patternMatchBind(tmp2,tmp1))
			{
				TVP tmp3= newBool(true);
				res = clone(tmp3);
				recursiveFree(tmp3);
				return res;
			} else
			{
				recursiveFree(tmp2);
				recursiveFree(tmp1);
				FATAL_ERROR("Pattern match error"); //Pattern match error
			}

			recursiveFree(tmp2);
			recursiveFree(tmp1);
			return res;
		}
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

TEST(Expression, letPatternMatch3)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = letPatternMatch3();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	recursiveFree(result);

	recursiveFree (TEST_TRUE);
}

TEST(Expression, letFilter1Exp)
{
	TVP TEST_TRUE = newBool(true);

	bool failed = false;
	try
	{
		TVP result = letFilter1Exp();

		EXPECT_EQ (true,equals(TEST_TRUE,result));

		recursiveFree(result);
	} catch(...)
	{
		failed = true;
	}

	if(!failed)
		FAIL();
	recursiveFree (TEST_TRUE);
}
