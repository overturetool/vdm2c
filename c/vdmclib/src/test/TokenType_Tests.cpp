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
#include "VdmSeq.h"
#include "VdmGC.h"
#include <stdio.h>
}



TEST(TokenTypes, basic)
{
	TVP t1 = newToken(newSeqVar(3, newChar('a'), newChar('a'), newChar(0)));
	TVP t2 = newToken(newSeqVar(3, newChar('a'), newChar('a'), newChar(0)));
	TVP t3 = newToken(newSeqVar(3, newChar('a'), newChar('b'), newChar(0)));

	EXPECT_EQ (t1->value.intVal, t2->value.intVal);

	EXPECT_NE (t1->value.intVal, t3->value.intVal);
	EXPECT_NE (t2->value.intVal, t3->value.intVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(t3);
}

//These GC tests are commented out for now until GC tests can be added for the entire runtime library.
//In the meantime we rely on the integration tests from the generator proper.

/*
TEST(TokenTypes, basicGC)
{
	vdm_gc_init();

	TVP t1 = newTokenGC(newSeqVar(3, newCharGC('a', NULL), newCharGC('a', NULL), newCharGC(0, NULL)), &t1);
	TVP t2 = newTokenGC(newSeqVar(3, newCharGC('a', NULL), newCharGC('a', NULL), newCharGC(0, NULL)), &t2);
	TVP t3 = newTokenGC(newSeqVar(3, newCharGC('a', NULL), newCharGC('b', NULL), newCharGC(0, NULL)), &t3);

	EXPECT_EQ (t1->value.intVal, t2->value.intVal);

	EXPECT_NE (t1->value.intVal, t3->value.intVal);
	EXPECT_NE (t2->value.intVal, t3->value.intVal);

	vdm_gc();
	vdm_gc_shutdown();
}
*/

TEST(TokenTypes, cloning)
{
	TVP t1 = newToken(newSeqVar(3, newChar('a'), newChar('a'), newChar(0)));
	TVP t2 = newToken(newSeqVar(3, newChar('a'), newChar('b'), newChar(0)));
	TVP t3 = vdmClone(t1);
	TVP t4 = vdmClone(t2);

	EXPECT_EQ (t1->value.intVal, t3->value.intVal);
	EXPECT_EQ (t2->value.intVal, t4->value.intVal);

	EXPECT_NE (t3->value.intVal, t4->value.intVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(t3);
	vdmFree(t4);
}

/*
TEST(TokenTypes, cloningGC)
{
	vdm_gc_init();

	TVP t1 = newTokenGC(newSeqVar(3, newCharGC('a', NULL), newCharGC('a', NULL), newCharGC(0, NULL)), &t1);
	TVP t2 = newTokenGC(newSeqVar(3, newCharGC('a', NULL), newCharGC('b', NULL), newCharGC(0, NULL)), &t2);
	TVP t3 = vdmCloneGC(t1, &t3);
	TVP t4 = vdmCloneGC(t2, &t4);

	EXPECT_EQ (t1->value.intVal, t3->value.intVal);
	EXPECT_EQ (t2->value.intVal, t4->value.intVal);

	EXPECT_NE (t3->value.intVal, t4->value.intVal);

	vdm_gc();

	vdm_gc_shutdown();
}
*/

TEST(TokenTypes, equality)
{
	TVP t1 = newToken(newSeqVar(3, newChar('a'), newChar('a'), newChar(0)));
	TVP t2 = newToken(newSeqVar(3, newChar('a'), newChar('a'), newChar(0)));
	TVP res = vdmEquals(t1, t2);

	EXPECT_EQ (t1->value.intVal, t2->value.intVal);
	EXPECT_TRUE (res->value.boolVal);

	vdmFree(t1);
	vdmFree(t2);
	vdmFree(res);
}
