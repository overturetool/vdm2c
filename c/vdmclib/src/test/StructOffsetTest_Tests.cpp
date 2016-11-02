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
#include "classes/A.h"
#include "classes/B.h"
#include "classes/C.h"
#include <stdio.h>
}

struct K
{
	int a;
	int b;
	struct VTable* tbl;
};

static void calc1(void*)
{
}

struct VTable VTableArrayForK[] =
{
/*
 Vtable entry virtual function sum.
 */
{ 0, 0, (VirtualFunctionPointer) calc1 },

};

struct KK
{

	int a;
	int b;
	struct VTable* tbl;
	int c;
	struct VTable* tbl2;

};

TEST(KK, offsetTest)
{
	struct KK k =
	{ 1, 2, VTableArrayForK, 999, VTableArrayForK };

	struct KK* kp = &k;

	int c = *((int*) (((unsigned char*) kp) + offsetof(struct KK, c)));
	struct VTable* tbl2 = GET_STRUCT_FIELD(KK, kp, struct VTable*, tbl2);

	EXPECT_EQ (1,kp->a);

	EXPECT_EQ (kp->c,c);
}
