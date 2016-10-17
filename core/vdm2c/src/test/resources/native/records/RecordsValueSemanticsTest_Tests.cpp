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
#include "RecordTest.h"
#include <stdio.h>
}


#define CHECK(methodId) TVP a = _Z10RecordTestEV(NULL);\
TVP res=CALL_FUNC(RecordTest, RecordTest, a, methodId);\
EXPECT_EQ (true, res->value.boolVal);\
vdmFree(res);\
vdmFree(a)

TEST(Statements_Records, test1)
{
	CHECK(CLASS_RecordTest__Z5test1EV);
}

TEST(Statements_Records, test2)
{
	CHECK(CLASS_RecordTest__Z5test2EV);
}

TEST(Statements_Records, test3)
{
	CHECK(CLASS_RecordTest__Z5test3EV);
}

TEST(Statements_Records, test4)
{
	CHECK(CLASS_RecordTest__Z5test4EV);
}

TEST(Statements_Records, test5)
{
	CHECK(CLASS_RecordTest__Z5test5EV);
}

