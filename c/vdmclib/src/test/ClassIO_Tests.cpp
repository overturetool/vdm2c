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
#include "IOLib.h"
#include "VdmSeq.h"
#include "VdmSet.h"
#include <stdio.h>
#include <math.h>
}

#ifndef NO_IO
#ifndef NO_SEQS
TEST(ClassIO, print)
{
	vdm_IO_echo(newSeqVar(3, newChar('a'), newChar('b'), newChar('c')));
	vdm_IO_print(newSeqVar(4, newChar('a'), newChar('b'), newChar('c'), newChar('d')));
	vdm_IO_println(newSeqVar(4, newChar('a'), newInt(33), newChar('c'), newChar('d')));

	vdm_IO_print(newInt(1234));
	vdm_IO_print(newInt(-1234));
	vdm_IO_print(newBool(true));
	vdm_IO_print(newBool(false));
	vdm_IO_print(newChar('z'));
	vdm_IO_print(newReal(12.34));
	vdm_IO_print(newSetVar(2, newInt(1), newInt(2)));

	vdm_IO_println(newInt(1234));
	vdm_IO_println(newInt(-1234));
	vdm_IO_println(newBool(true));
	vdm_IO_println(newBool(false));
	vdm_IO_println(newChar('z'));
	vdm_IO_println(newReal(12.34));
	vdm_IO_println(newSetVar(2, newInt(1), newInt(2)));
	vdm_IO_println(newSetVar(2, newSetVar(2, newInt(3), newInt(4)), newSetVar(2, newChar('b'), newChar('z'))));
	vdm_IO_println(newSeqVar(2, newSetVar(2, newInt(3), newInt(4)), newSeqVar(2, newChar('b'), newChar('z'))));
}
#endif /* NO_SEQS */
#endif /* NO_IO */
