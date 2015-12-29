#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include "classes/ModelVarBOOL.h"
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

TEST(ModelVarBOOL, _new)
{
	struct ModelVarBOOL* c=ModelVarBOOL._new();//ModelVarBOOL._newB(true);
	show(c);
	c->setCoil( c);
	show(c);


	struct TypedValue* tclassT = ModelVarBOOL._encapsulate(c);
	recursiveFree(tclassT);
}

TEST(ModelVarBOOL, _newB)
{
	struct ModelVarBOOL* c=ModelVarBOOL._newB(true);
	show(c);
	c->setCoil( c);
	show(c);

	struct TypedValue* tclassT = ModelVarBOOL._encapsulate(c);
	recursiveFree(tclassT);
}

