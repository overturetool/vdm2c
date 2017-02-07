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

/*
TEST(GC, test1)
{
	TVP a;

	vdm_gc_init();

	a = newIntGC(3, &a);

	vdm_gc();

	vdm_gc_shutdown();
}

TEST(GC, test2)
{
	TVP a;

	vdm_gc_init();

	a = newIntGC(3, NULL);

	vdm_gc();

	vdm_gc_shutdown();
}


TEST(GC, test3)
{
	TVP a;

	vdm_gc_init();

	a = newIntGC(3, NULL);
	vdmFree(a);

	vdm_gc();

	vdm_gc_shutdown();
}


TEST(GC, test4)
{
	TVP a;

	vdm_gc_init();

	a = newIntGC(3, &a);

	vdm_gc();
	vdmFree(a);

	vdm_gc_shutdown();
}


TEST(GC, test5)
{
	TVP a;

	vdm_gc_init();

	a = newIntGC(3, &a);

	vdmFree(a);
	vdm_gc();

	vdm_gc_shutdown();
}


TEST(GC, test6)
{
	TVP a;

	vdm_gc_init();

	a = newIntGC(3, &a);
	a = newBoolGC(true, &a);

	vdmFree(a);
	vdm_gc();

	vdm_gc_shutdown();
}


TEST(GC, test7)
{
	TVP a;

	vdm_gc_init();

	a = newIntGC(3, &a);
	a = newBoolGC(true, &a);

	vdm_gc();
	vdmFree(a);

	vdm_gc_shutdown();
}



TEST(GC, test8)
{
	TVP a;

	vdm_gc_init();

	a = newIntGC(3, &a);

	vdmFree(a);

	a = newBoolGC(true, NULL);

	vdm_gc();
//	vdmFree(a);

	vdm_gc_shutdown();
}
*/
