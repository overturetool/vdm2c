/*
 * ModelVarBOOL.h
 *
 *  Created on: Nov 20, 2015
 *      Author: kel
 */



#ifndef MODELVARBOOL_H_
#define MODELVARBOOL_H_

#include <stdbool.h>

#include <stdlib.h>


#include <assert.h>
#include "../Globals.h"



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
	bool (*openContact)(struct ModelVarBOOL *self);
	bool (*closedContact)(struct ModelVarBOOL *self);
	void (*coil)(struct ModelVarBOOL *self);
	void (*setCoil)(struct ModelVarBOOL *self);
	void (*resetCoil)(struct ModelVarBOOL *self);
	void (*notCoil)(struct ModelVarBOOL *self);
	bool (*PContact)(struct ModelVarBOOL *self);
	bool (*NContact)(struct ModelVarBOOL *self);


	struct TypedValue* (*ff)(struct ModelVarBOOL *self);
	bool (*gg)(struct ModelVarBOOL *self);
	bool (*dd)(struct ModelVarBOOL *self, struct TypedValue* x);


	//void (*ModelVarBOOL_free)(struct ModelVarBOOL *self);
	freeVdmClassFunction _free;
};

extern const struct ModelVarBOOLClass
{
	//class members
	struct ModelVarBOOL* (*_newB)(bool x);
	struct ModelVarBOOL* (*_newCharPtr)(char* x);
	struct ModelVarBOOL* (*_new)();
	struct TypedValue* (*_encapsulate)(struct ModelVarBOOL* x);

} ModelVarBOOL;

#endif /* MODELVARBOOL_H_ */
