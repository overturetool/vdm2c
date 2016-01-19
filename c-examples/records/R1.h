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
