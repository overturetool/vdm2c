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

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
}

TVP productExp()
{
	TVP tmp1 = newProduct(2);

	TVP tmp1_1 = newInt(1);
	productSet(tmp1,1,tmp1_1);
	vdmFree(tmp1_1);

	TVP tmp1_2 = newInt(2);
	productSet(tmp1,2,tmp1_2);
	vdmFree(tmp1_2);


	TVP tmp2_1 = newInt(1);
	TVP tmp2_2 = newInt(2);
	TVP args[2] = {tmp2_1,tmp2_2};
	TVP tmp2 = newProductWithValues(2,args);
	vdmFree(tmp2_1);
	vdmFree(tmp2_2);

//
//	TVP tmp2_1 = newInt(1);
//	productSet(tmp2,1,tmp2_1);
//	vdmFree(tmp2_1);
//
//	TVP tmp2_2 = newInt(2);
//	productSet(tmp2,2,tmp2_2);
//	vdmFree(tmp2_2);

	TVP res =newBool(equals(tmp1,tmp2));

	//scope cleanup
	vdmFree(tmp1);
	vdmFree(tmp2);

	return res;
}

TVP productExp2()
{
	TVP tmp1 = newProduct(2);

	TVP tmp1_1 = newInt(1);
	productSet(tmp1,1,tmp1_1);
	vdmFree(tmp1_1);

	TVP tmp1_2 = newInt(2);
	productSet(tmp1,2,tmp1_2);
	vdmFree(tmp1_2);


	TVP tmp2_1 = newInt(1);
	TVP tmp2_2 = newInt(2);

	TVP tmp2 = newProductVar(2,tmp2_1, tmp2_2);
	vdmFree(tmp2_1);
	vdmFree(tmp2_2);

//
//	TVP tmp2_1 = newInt(1);
//	productSet(tmp2,1,tmp2_1);
//	vdmFree(tmp2_1);
//
//	TVP tmp2_2 = newInt(2);
//	productSet(tmp2,2,tmp2_2);
//	vdmFree(tmp2_2);

	TVP res =newBool(equals(tmp1,tmp2));

	//scope cleanup
	vdmFree(tmp1);
	vdmFree(tmp2);

	return res;
}

TEST(ExpressionProduct, productExp)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = productExp();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	vdmFree(result);

	vdmFree (TEST_TRUE);
}



TEST(ExpressionProduct, productVariadic)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = productExp2();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	vdmFree(result);

	vdmFree (TEST_TRUE);
}
