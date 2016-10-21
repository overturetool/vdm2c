/*
 * ExpressionForLoop.cpp
 *
 *  Created on: Jan 20, 2016
 *      Author: kel
 */

#include "gtest/gtest.h"

#define FATAL_ERROR(message) fprintf (stderr, "%s\n",message);throw message

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
}

TVP forindex()
{
	TVP tmp = newInt(0);

	TVP e1 = newInt(1);
	TVP e2 = newInt(2);
	TVP e3 = newInt(10);
	{
		int _id = toInteger(e1);

		while( _id <=toInteger(e3))
		{
			TVP id = newInt(_id);
			//stm
			TVP _tmp = tmp;
			tmp = vdmSum(tmp,id);
			vdmFree(_tmp);
			vdmFree(id);
			_id +=toInteger(e2);
		}
	}

	vdmFree(e1);
	vdmFree(e2);
	vdmFree(e3);

	TVP tmp1 = newInt(25);
	TVP ret = vdmEqual(tmp,tmp1);
	vdmFree(tmp1);
	vdmFree(tmp);
	return ret;
}

TVP forset()
{
	/*public forset : ()==>bool
	 forset()==(
	 dcl tmp :int :=0;
	 for all s in set {1,2,3,2} do tmp := tmp+s;
	 return tmp=6;
	 );
	 * */
	TVP tmp = newInt(0);

	TVP e1 = newInt(1);
	TVP e2 = newInt(2);
	TVP e3 = newInt(3);
	TVP e4 = newInt(2);

	TVP S = newSetVar(4,e1,e2,e3,e4);

	{
		ASSERT_CHECK_COLLECTION(S);
		UNWRAP_COLLECTION(col,S);

		int _i=0;
		/*for (int i = 0; i < col->size; i++)
		 {
		 TVP s = col->value[i];

		 TVP _tmp = tmp;
		 tmp = vdmSum(tmp,s);
		 vdmFree(_tmp);
		 }*/

		while( _i < col->size)
		{
			TVP s = col->value[_i];
			//stm
			TVP _tmp = tmp;
			tmp = vdmSum(tmp,s);
			vdmFree(_tmp);
			_i ++;
		}
	}

	vdmFree(e1);
	vdmFree(e2);
	vdmFree(e3);
	vdmFree(e4);

	vdmFree(S);

	TVP tmp1 = newInt(6);
	TVP ret = vdmEqual(tmp,tmp1);
	vdmFree(tmp1);
	vdmFree(tmp);
	return ret;

}

TEST(ExpressionForLoop, forindex)
{
	TVP TEST_TRUE = newBool(true);
	TVP result = forindex();
	EXPECT_EQ (true,equals(TEST_TRUE,result));
	vdmFree(result);
	vdmFree (TEST_TRUE);
}

TEST(ExpressionForLoop, forset)
{
	TVP TEST_TRUE = newBool(true);
	TVP result = forset();
	EXPECT_EQ (true,equals(TEST_TRUE,result));
	vdmFree(result);
	vdmFree (TEST_TRUE);
}
