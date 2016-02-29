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


TEST(PrettyPrint, printBool)
{
	char* str;
	TVP val;

	val = newBool(true);

	str = printBool(val);
	printf("%s\n", str);
	free(str);
	vdmFree(val);

	val = newBool(false);
	str = printBool(val);
	printf("%s\n", str);
	free(str);
	vdmFree(val);
}



TEST(PrettyPrint, printInt)
{
	char* str;
	TVP val;

	val = newInt(1234);

	str = printInt(val);
	printf("%s\n", str);
	free(str);
	vdmFree(val);

	val = newInt(-12345);
	str = printInt(val);
	printf("%s\n", str);
	free(str);
	vdmFree(val);
}



TEST(PrettyPrint, printChar)
{
	char* str;
	TVP val;

	val = newChar('z');

	str = printChar(val);
	printf("%s\n", str);
	free(str);
	vdmFree(val);
}



TEST(PrettyPrint, printDouble)
{
	char* str;
	TVP val;

	val = newReal(12.34);
	str = printDouble(val);
	printf("%s\n", str);
	free(str);
	vdmFree(val);
}
