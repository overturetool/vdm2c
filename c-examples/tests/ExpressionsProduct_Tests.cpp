#include "gtest/gtest.h"

extern "C"
{
#include "lib/Vdm.h"
#include <stdio.h>
}

TVP productExp()
{
	TVP tmp1 = newProduct(2);

	TVP tmp1_1 = newInt(1);
	productSet(tmp1,1,tmp1_1);
	recursiveFree(tmp1_1);

	TVP tmp1_2 = newInt(2);
	productSet(tmp1,2,tmp1_2);
	recursiveFree(tmp1_2);


	TVP tmp2_1 = newInt(1);
	TVP tmp2_2 = newInt(2);
	TVP args[2] = {tmp2_1,tmp2_2};
	TVP tmp2 = newProductWithValues(2,args);
	recursiveFree(tmp2_1);
	recursiveFree(tmp2_2);

//
//	TVP tmp2_1 = newInt(1);
//	productSet(tmp2,1,tmp2_1);
//	recursiveFree(tmp2_1);
//
//	TVP tmp2_2 = newInt(2);
//	productSet(tmp2,2,tmp2_2);
//	recursiveFree(tmp2_2);

	TVP res =newBool(equals(tmp1,tmp2));

	//scope cleanup
	recursiveFree(tmp1);
	recursiveFree(tmp2);

	return res;
}

TEST(ExpressionProduct, productExp)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = productExp();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	recursiveFree(result);

	recursiveFree (TEST_TRUE);
}

