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
 * R1.h
 *
 *  Created on: Dec 4, 2015
 *      Author: kel
 */

#ifndef RECORDS_R1_H_
#define RECORDS_R1_H_

#include "../lib/Vdm.h"
#include "../lib/TypedValue.h"

//#define UNWRAP_R(var,map) struct Map* var = (struct Map*)map->value.ptr

#define RECORD_ID_R1 1

#define RECORD_R1 struct R1*

struct R1
{
	//needs if to identify it
	int _id;

	TVP a;
	TVP b;
	TVP c;
};


TVP mk_R1();

#endif /* RECORDS_R1_H_ */
