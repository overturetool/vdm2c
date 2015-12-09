#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include "classes/A.h"
#include "classes/B.h"
#include <stdio.h>
}


TEST(A, _new)
{
	TVP c=A._new();//ModelVarBOOL._newB(true);
	UNWRAP_CLASS_A(l,c);

//	l->print(l);

	TVP res = l->sum(l);

	vdmFree(c);

	EXPECT_EQ (4,res->value.intVal);
	vdmFree(res);
}


TEST(B, _new)
{
	TVP c=B._new();//ModelVarBOOL._newB(true);
	UNWRAP_CLASS_B(l,c);

	TVP castToA =newTypeValue(VDM_CLASS,
			(TypedValueType){.ptr=
					newClassValue(l->_id, &l->_refs, 0, &l->A)
							});

	UNWRAP_CLASS_A(la,castToA);

//		la->print(la);

		TVP res = la->sum(la);

		vdmFree(c);

		EXPECT_EQ (9,res->value.intVal);
		vdmFree(res);
}


