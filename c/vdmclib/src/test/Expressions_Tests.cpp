/*
 * #%~
 * The VDM to C Code Generator
 * %%
 * Copyright (C) 2015 - 2016 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

#include "gtest/gtest.h"

#define FATAL_ERROR(message) fprintf (stderr, "%s\n",message);throw message

extern "C"
{
#include "Vdm.h"
#include "VdmProduct.h"
#include "PatternBindMatch.h"
#include <stdio.h>

extern TVP newProduct(size_t size);
}

TVP letIdentifierExp()
{
	//let a = true in a
	{
		TVP a = newBool(true);

		TVP ret = vdmClone(a);

		//clean local malloc
		vdmFree(a);
		return ret;
	}

}

TVP letIdentifierDontExp()
{

	//let a = true in a
	{
		TVP dont_1 = newBool(true);

		TVP tmp1 = newBool(true);
		TVP ret = vdmClone(tmp1);

		//clean local malloc
		vdmFree(dont_1);
		vdmFree(tmp1);
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

		TVP ret = vdmClone(tmp3);
		vdmFree(tmp1);
		vdmFree(tmp2);
		vdmFree(tmp3);
		return ret;
	}
}

#if !defined(NO_PATTERNS) && !defined(NO_PRODUCTS)

TVP letPatternMatch3()
{
	//let mk_(a,2) = mk_(1,2) in true
	{
		//mk_(a,2)
		TVP var1 = newInt(2);
		TVP var2 = newProduct(2);
		productSet(var2,2,var1);
		vdmFree(var1);

		//mk_(1,2)
		TVP var3 = newInt(1);
		TVP var4 = newInt(2);
		TVP var5 = newProduct(2);
		productSet(var5,1,var3);
		vdmFree(var3);
		productSet(var5,2,var4);
		vdmFree(var4);

		TVP res=NULL;
		//match
		if( patternMatchBind(var2,var5))
		{
			TVP local1 = productGet(var2,1);
			res= newBool(true);
			vdmFree(local1);
		} else
		{
			vdmFree(var2);
			vdmFree(var5);
			FATAL_ERROR("Pattern match error"); //Pattern match error
		}

		vdmFree(var2);
		vdmFree(var5);

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
				res = vdmClone(tmp3);
				vdmFree(tmp3);
				return res;
			} else
			{
				vdmFree(tmp2);
				vdmFree(tmp1);
				FATAL_ERROR("Pattern match error"); //Pattern match error
			}

			vdmFree(tmp2);
			vdmFree(tmp1);
			return res;
		}
	}
}

#endif /* NO_PATTERNS and NO_PRODUCTS*/

TVP ifExp()
{
	/*if true or true
	 then
	 true or false
	 else
	 true;*/
	TVP tmp1 = newBool(true);
	TVP tmp2 = newBool(true);
	TVP res = NULL;

	if(tmp1->value.boolVal || tmp2->value.boolVal) //should I make a lib for bool operators
	{
		TVP tmp3 = newBool(true);
		TVP tmp4 = newBool(false);
		TVP tmp5 = newBool(tmp3->value.boolVal ||tmp4->value.boolVal);
		res = vdmClone(tmp5);

		//local scope cleanup
		vdmFree(tmp5);
		vdmFree(tmp4);
		vdmFree(tmp3);

	} else
	{
		TVP tmp3 = newBool(true);
		res = vdmClone(tmp3);

		//local scope cleanup
		vdmFree(tmp3);
	}
	//if condition scope cleanup
	vdmFree(tmp2);
	vdmFree(tmp1);

	return res;
}

TEST(Expression, letIdentifierExp)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = letIdentifierExp();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	vdmFree(result);

	vdmFree (TEST_TRUE);
}

TEST(Expression, letIdentifierDontExp)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = letIdentifierDontExp();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	vdmFree(result);

	vdmFree (TEST_TRUE);
}

#if !defined(NO_PATTERNS) && !defined(NO_PRODUCTS)

TEST(Expression, letPatternMatch1)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = letPatternMatch1();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	vdmFree(result);

	vdmFree (TEST_TRUE);
}

TEST(Expression, letPatternMatch3)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = letPatternMatch3();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	vdmFree(result);

	vdmFree (TEST_TRUE);
}


TEST(Expression, letFilter1Exp)
{
	TVP TEST_TRUE = newBool(true);

	bool failed = false;
	try
	{
		TVP result = letFilter1Exp();

		EXPECT_EQ (true,equals(TEST_TRUE,result));

		vdmFree(result);
	} catch(...)
	{
		failed = true;
	}

	if(!failed)
		FAIL();
	vdmFree (TEST_TRUE);
}

#endif /*  NO_PATTERNS & NO_PRODUCTS */


TEST(Expression, ifExp)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = ifExp();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	vdmFree(result);

	vdmFree (TEST_TRUE);
}



TEST(Expression, isInt)
{
	char ot[] = "i";
	TVP res;

	res = isInt(newInt(3));
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = is(newInt(3), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isReal)
{
	char ot[] = "d";
	TVP res;

	res = isReal(newReal(3));
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = is(newReal(3), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isBool)
{
	char ot[] = "b";
	TVP res;

	res = isBool(newBool(false));
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = is(newBool(false), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}


TEST(Expression, isNat)
{
	char ot[] = "j";
	TVP res;

	res = isNat(newInt(0));
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = is(newInt(0), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = isNat(newInt(-1));
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);

	res = is(newInt(-1), ot);
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isNat1)
{
	char ot[] = "k";
	TVP res;

	res = isNat1(newInt(1));
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = is(newInt(1), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = isNat1(newInt(0));
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);

	res = is(newInt(0), ot);
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isRat)
{
	char ot[] = "e";
	TVP res;

	res = isRat(newReal(1));
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = is(newReal(1), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = isRat(newInt(0));
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);

	res = is(newInt(0), ot);
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isChar)
{
	char ot[] = "c";
	TVP res;

	res = isChar(newChar('a'));
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = is(newChar('a'), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isToken)
{
	char ot[] = "t";
	TVP res;

	res = isToken(newToken(newSeqVar(1, newChar('a'))));
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = is(newToken(newSeqVar(1, newChar('a'))), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isSeqOfInt)
{
	char ot[] = "Qi";
	TVP res;

	res = is(newSeqVar(2, newInt(1), newInt(-2)), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isSeqOfSeqOfInt)
{
	char ot[] = {'Q', 'Q', 'i'};
	TVP res;

	res = is(newSeqVar(2, newSeqVar(2, newInt(1), newInt(-2)), newSeqVar(2, newInt(3), newInt(-4))), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isSetOfInt)
{
	char ot[] = "Ti";
	TVP res;

	res = is(newSetVar(2, newInt(1), newInt(-2)), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isSetOfSetOfInt)
{
	char ot[] = "TTi";
	TVP res;

	res = is(newSetVar(2, newSetVar(2, newInt(1), newInt(-2)), newSetVar(2, newInt(-1), newInt(2))), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isSetOfSeqOfNat1)
{
	char ot[] = "TQk";
	TVP res;

	res = is(newSetVar(2, newSeqVar(2, newInt(1), newInt(2)), newSeqVar(2, newInt(1), newInt(2))), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);

	res = is(newSetVar(2, newSeqVar(2, newInt(1), newInt(-2)), newSeqVar(2, newInt(1), newInt(2))), ot);
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isSetOfSeqOfToken)
{
	char ot[] = "TQt";
	TVP res;

	res = is(newSetVar(2, newSeqVar(2, newToken(newSeqVar(1, newChar('a'))), newToken(newSeqVar(1, newChar('a')))), newSeqVar(2, newToken(newSeqVar(1, newChar('a'))), newToken(newSeqVar(1, newChar('a'))))), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

TEST(Expression, isSeqOfSetOfToken)
{
	char ot[] = "QTt";
	TVP res;

	res = is(newSeqVar(2, newSetVar(2, newToken(newSeqVar(1, newChar('a'))), newToken(newSeqVar(1, newChar('a')))), newSetVar(2, newToken(newSeqVar(1, newChar('a'))), newToken(newSeqVar(1, newChar('a'))))), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}

/*
TEST(Expression, isRecIntInt)
{
	char ot[] = "dlk";
	TVP res;

	res = is(newSeqVar(2, newSetVar(2, newToken(newSeqVar(1, newChar('a'))), newToken(newSeqVar(1, newChar('a')))), newSetVar(2, newToken(newSeqVar(1, newChar('a'))), newToken(newSeqVar(1, newChar('a'))))), ot);
	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
}
*/
