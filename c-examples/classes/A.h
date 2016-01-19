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
 * A.h
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */

#ifndef CLASSES_A_H_
#define CLASSES_A_H_

#include "../lib/TypedValue.h"
#include "../lib/VdmClass.h"
#include "../lib/VdmBasicTypes.h"

#define CLASS_ID_A_ID 124

#define ACLASS struct A*

#define CLASS_A_calc 0
#define CLASS_A_sum 1

struct A
{
	VDM_CLASS_BASE_DEFINITIONS(A);
	/*vtable
	 * calc
	 * sum
	 * */
	VDM_CLASS_FIELD_DEFINITION(A,field1);

};

TVP A_ctor(ACLASS ptr);

void A_free_fields(ACLASS);
ACLASS A_Constructor(ACLASS);

extern const struct AClass
{
	TVP (*_new)();
} A;

#endif /* CLASSES_A_H_ */
