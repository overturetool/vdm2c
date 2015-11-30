#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include "classes/ModelVarBool.h"
#include <stdio.h>
}

void show(struct ModelVarBOOL* c)
{
	printf("ModelVarBOOL state is:\n");
	printf("\t.value = %i", c->value);
	printf("\t.past = %i", c->past);
	printf("\t.lock = %i", c->lock);
	printf("\t.memory_adresse = %s\n", c->memory_adresse);
}

TEST(FmiIpc, shmbasetest)
{
	struct ModelVarBOOL c=ModelVarBOOL._newB(true);
	show(&c);
	c.setCoil( &c);
	show(&c);

	struct ClassType* classT = newClassValue(c._id,&c._refs,&c);
	struct TypedValue* tclassT = newTypeValue(VDM_CLASS,classT);
	recursiveFree(tclassT);
}

