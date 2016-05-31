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
#include "lib/Vdm.h"
#include "records/R1.h"
#include "records/R1.h"
#include <stdio.h>
}

TVP recordR1()
{
	/*
	 types

	 R1 ::
	 a : int
	 b : seq of char
	 c : bool;

	 operations

	 public recordR1: ()==>bool
	 recordR1()==(

	 dcl r : R1 := mk_R1(0,"ab",false);

	 let r2 = r,
	 mk_R1(-,-,b)=r
	 in
	 return r2.c or true or b;

	 );
	 */

	//dcl r : R1 := mk_R1(0,"ab",false);
	TVP r = mk_R1();
//	RECORD_FIELD_ACCESS(r,struct R1*,a,tmp1);

	// 0
	TVP tmp1 = newInt(0);
	RECORD_FIELD_SET(r,RECORD_R1,a,tmp1);
	vdmFree(tmp1);

	TVP tmp2 = newSeq(2);

	TVP tmp2_1 = newChar('a');
	SET_SEQ(tmp2,1,tmp2_1);
	vdmFree(tmp2_1);

	TVP tmp2_2 = newChar('b');
	SET_SEQ(tmp2,2,tmp2_2);
	vdmFree(tmp2_2);

	RECORD_FIELD_SET(r,RECORD_R1,b,tmp2);
	vdmFree(tmp2);

	TVP tmp3 = newBool(false);
	RECORD_FIELD_SET(r,RECORD_R1,c,tmp3);
	vdmFree(tmp3);

	//let r2 = r;
	TVP r2 = vdmClone(r);

//	mk_R1(-,-,b)=r
	TVP tmp4 = mk_R1();
	patternMatchBind(tmp4,r);
	RECORD_FIELD_ACCESS(tmp4,RECORD_R1,c,b);

//	 in
//		 return r2.c or true or b;
	RECORD_FIELD_ACCESS(r2,RECORD_R1,c,tmp5);
	TVP tmp6 = newBool(true);

	TVP tmp7 =newBool( tmp5->value.boolVal || tmp6->value.boolVal || b->value.boolVal);

	TVP res = vdmClone(tmp7);
	vdmFree(tmp7);
	vdmFree(tmp6);
	vdmFree(tmp5);
	vdmFree(tmp4);

	return res;
}

TEST(Statements, recordR1)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = recordR1();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	recursiveFree(result);

	recursiveFree (TEST_TRUE);
}
