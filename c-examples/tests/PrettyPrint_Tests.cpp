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
#include "lib/Vdm.h"
#include <stdio.h>
#include <math.h>
}



TEST(PrettyPrint, print)
{
	TVP val;

	val = newBool(true);
	vdm_IO_print(val);
	printf("\n");
	vdmFree(val);

	val = newBool(false);
	vdm_IO_print(val);
	printf("\n");
	vdmFree(val);

	val = newInt(1234);
	vdm_IO_print(val);
	printf("\n");
	vdmFree(val);

	val = newInt(-12345);
	vdm_IO_print(val);
	printf("\n");
	vdmFree(val);

	val = newChar('z');
	vdm_IO_print(val);
	printf("\n");
	vdmFree(val);

	val = newReal(12.34);
	vdm_IO_print(val);
	printf("\n");
	vdmFree(val);

//	val = newSetVar(2, newInt(1), newInt(2));
//	vdm_IO_print(val);
//	printf("\n");
//	vdmFree(val);
}
