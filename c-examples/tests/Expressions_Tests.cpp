#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include <stdio.h>
}

TEST(Expression, _new)
{
//	struct ModelVarBOOL* c=ModelVarBOOL._new();//ModelVarBOOL._newB(true);
//	show(c);
//	c->setCoil( c);
//	show(c);
//
//	struct ClassType* classT = newClassValue(c->_id,&c->_refs,c);
	struct TypedValue* tv = newInt(3);
//	printf("%d",tv);
//	recursiveFree(tclassT);
}

TEST(Expression, _newB)
{
//	struct ModelVarBOOL* c=ModelVarBOOL._newB(true);
//	show(c);
//	c->setCoil( c);
//	show(c);
//
//	struct ClassType* classT = newClassValue(c->_id,&c->_refs,c);
//	struct TypedValue* tclassT = newTypeValue(VDM_CLASS,classT);
//	recursiveFree(tclassT);
}

