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


/*
 * C.h
 *
 *  Created on: Dec 7, 2015
 *      Cuthor: kel
 */

#ifndef CLCSSES_C_H_
#define CLCSSES_C_H_

#include "../lib/TypedValue.h"
#include "../lib/VdmClass.h"
#include "../lib/VdmBasicTypes.h"

#define CLASS_ID_C_ID 126

#define CCLASS struct C*

#define CLASS_C_getField1 0

struct C
{
	VDM_CLASS_BASE_DEFINITIONS(C);
	/*vtable
	 * getField1
	 * */

	VDM_CLASS_FIELD_DEFINITION(C,field1c);
};

TVP C_ctor(CCLASS ptr);

void C_free_fields(CCLASS);
CCLASS C_Constructor(CCLASS);


extern const struct CClass
{
	TVP (*_new)();
} C;

#endif /* CLCSSES_C_H_ */

