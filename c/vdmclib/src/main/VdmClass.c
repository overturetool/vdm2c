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
 * <http:XXXwww.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

/*
 * VdmClass.c
 *
 *  Created on: Dec 14, 2015
 *      Author: kel
 */


#include "VdmClass.h"

struct ClassType* newClassValue(int id, unsigned int* refs, freeVdmClassFunction freeClass, void* value)
{
	struct ClassType* ptr = (struct ClassType*) malloc(sizeof(struct ClassType));
	assert(ptr != NULL);
	ptr->classId = id;
	ptr->value = value;
	ptr->freeClass = freeClass;
	ptr->refs = refs;
	(*refs)++;
	return ptr;
}
