/*
 * ModelVarBOOL.h
 *
 *  Created on: Nov 20, 2015
 *      Author: kel
 */

typedef int bool;
#define true 1
#define false 0

#ifndef MODELVARBOOL_H_
#define MODELVARBOOL_H_

#include <stdlib.h>


#include <assert.h>



struct ModelVarBOOL
{
	//needs if to identify it
	int _id ;
	//needs refsCount for garbage collection
	unsigned int _refs;

	//instance variables
	bool value, past, lock;
	char* memory_adresse;

	//member functions
	bool (*openContact)(struct ModelVarBOOL *this);
	bool (*closedContact)(struct ModelVarBOOL *this);
	void (*coil)(struct ModelVarBOOL *this);
	void (*setCoil)(struct ModelVarBOOL *this);
	void (*resetCoil)(struct ModelVarBOOL *this);
	void (*notCoil)(struct ModelVarBOOL *this);
	bool (*PContact)(struct ModelVarBOOL *this);
	bool (*NContact)(struct ModelVarBOOL *this);


	struct TypedValue* (*ff)(struct ModelVarBOOL *this);
	bool (*gg)(struct ModelVarBOOL *this);
	bool (*dd)(struct ModelVarBOOL *this, struct TypedValue* x);
};

extern const struct ModelVarBOOLClass
{
	//class members
	struct ModelVarBOOL (*newB)(bool x);
	struct ModelVarBOOL (*newCharPtr)(char* x);
	struct ModelVarBOOL (*new)();

} ModelVarBOOL;

#endif /* MODELVARBOOL_H_ */
