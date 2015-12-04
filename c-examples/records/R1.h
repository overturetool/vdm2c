/*
 * R1.h
 *
 *  Created on: Dec 4, 2015
 *      Author: kel
 */

#ifndef RECORDS_R1_H_
#define RECORDS_R1_H_

#include "../Globals.h"
#include "../lib/TypedValue.h"

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
