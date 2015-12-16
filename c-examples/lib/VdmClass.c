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
	ptr->classId = id;
	ptr->value = value;
	ptr->freeClass = freeClass;
	ptr->refs = refs;
	(*refs)++;
	return ptr;
}
