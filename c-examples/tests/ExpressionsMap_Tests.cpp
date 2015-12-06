#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"

#include "lib/VdmMap.h"
#include <stdio.h>
}

TVP mapApply()
{
	TVP res = NULL;
	//{1|->2,3|->4}(1)=2

	//{}
	TVP map = newMap();

	//maplet1 1|->2
	TVP key1 = newInt(1);
	TVP val1 =newInt(2);
	vdmMapAdd(map,key1,val1);
	vdmFree(key1);
	vdmFree(val1);

	//maplet2 3|->4
	TVP key2 = newInt(3);
	TVP val2 =newInt(4);
	vdmMapAdd(map,key2,val2);
	vdmFree(key2);
	vdmFree(val2);

	//apply id
	TVP tmp1 = newInt(1);

	TVP tmp2 = vdmMapApply(map,tmp1);
	vdmFree(tmp1);

	TVP tmp3 = newInt(2);

	TVP tmp4 = newBool(equals(tmp2,tmp3));


	res = clone(tmp4);
	vdmFree(tmp4);
	vdmFree(tmp3);
	vdmFree(tmp2);

	return res;
}

TEST(Expression_Map, mapApply)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = mapApply();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	recursiveFree(result);

	recursiveFree (TEST_TRUE);
}
