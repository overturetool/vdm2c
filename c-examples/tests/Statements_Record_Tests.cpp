#include "gtest/gtest.h"

#define FATAL_ERROR(message) fprintf (stderr, "%s\n",message);throw message

extern "C"
{
#include "lib/TypedValue.h"
#include "lib/VdmProduct.h"
#include "lib/PatternBindMatch.h"
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
}

TEST(Statements, recordR1)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = letIdentifierExp();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	recursiveFree(result);

	recursiveFree (TEST_TRUE);
}
