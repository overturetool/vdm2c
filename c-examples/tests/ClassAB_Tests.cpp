#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include "classes/A.h"
#include "classes/B.h"
#include "classes/C.h"
#include <stdio.h>
}



//#define GET_VTABLE_FUNC(tname,ptr,id)   (*( (struct VTable**) (  ((unsigned char*)ptr) + offsetof(struct tname, _##tname##_pVTable) )  ))     [id].pFunc
#define GET_VTABLE_FUNC(tname,ptr,id)   GET_STRUCT_FIELD(tname,ptr,struct VTable*,_##tname##_pVTable)[id].pFunc



//*((int*) (((void*) kp) + offsetof(struct KK, c)))

//#define GET_VTABLE_FUNC_CAST(tname,tname2,ptr,id)  ((struct VTable*) *(void**)(ptr+offsetof(struct tname, _##tname##_pVTable )))[id].pFunc


//Obtain a struct field
#define GET_STRUCT_FIELD(tname,ptr,fieldtype,fieldname) (*( (fieldtype*) (  ((unsigned char*)ptr) + offsetof(struct tname, fieldname) )  ))

//Obtain VTable function
#define GET_VTABLE_FUNC2(thisTypeName,funcTname,ptr,id)   GET_STRUCT_FIELD(thisTypeName,ptr,struct VTable*,_##funcTname##_pVTable)[id].pFunc

/*Cast to class pointer by moving it forward to the class specific VTable
 * Note that we only adjust the pointer if a subtype is given (i.e. not the type of it self)
*/
#define CLASS_CAST(ptr,from,to) ((unsigned char*)ptr) + (typeid(from)==typeid(to)?0: offsetof(struct from, _##to##_pVTable))
//Call function from VTable and change ptr to the correct offset. With arguments
#define CCCALL(thisTypeName,funcTname,ptr,id,...) GET_VTABLE_FUNC2( thisTypeName,funcTname,ptr,id)(CLASS_CAST(ptr,thisTypeName,funcTname), ## __VA_ARGS__))
//Call function from VTable and change ptr to the correct offset. With arguments
//#define CCCALL_VOID(thisTypeName,funcTname,ptr,id) GET_VTABLE_FUNC2( thisTypeName,funcTname,ptr,id)(CLASS_CAST(ptr,thisTypeName,funcTname)))

#define CALL_FUNC(thisTypeName,funcTname,classValue,id, args... )     GET_VTABLE_FUNC2( thisTypeName,funcTname,TO_CLASS_PTR(classValue,thisTypeName),id)(CLASS_CAST(TO_CLASS_PTR(classValue,thisTypeName),thisTypeName,funcTname), ## args)
//#define CALL_FUNC_ARGS(thisTypeName,funcTname,classValue,id,...) GET_VTABLE_FUNC2( thisTypeName,funcTname,TO_CLASS_PTR(classValue,thisTypeName),id)(CLASS_CAST(TO_CLASS_PTR(classValue,thisTypeName),thisTypeName,funcTname,__VA_ARGS__))

#define TO_CLASS_PTR(tv,type) ((struct type *) ( ((struct ClassType*)tv->value.ptr)->value))
//

/**
 * Utility methods
 */
void checkFreeInt(const char* name, int expected, TVP value)
{
	printf("%s is %d\n",name,value->value.intVal);
	EXPECT_EQ (expected,value->value.intVal);
	vdmFree(value);
}

void checkFreeDouble(const char* name, double expected, TVP value)
{
	printf("%s is %f\n",name,value->value.doubleVal);
	EXPECT_EQ (expected,value->value.doubleVal);
	vdmFree(value);
}

/**
 * Tests
 */

TEST(A, _newDirectVTable)
{
	TVP c=A._new();
	UNWRAP_CLASS_A(l, c);

	TVP a = newInt(1);
	TVP b = newInt(4);

	struct VTable* tbl = GET_STRUCT_FIELD(A,l,struct VTable*,_A_pVTable);

	TVP res = GET_VTABLE_FUNC( A,l,CLASS_A_calc)(l,a,b);

	printf("res is %f\n",res->value.doubleVal);
	EXPECT_EQ (5,res->value.doubleVal);
	vdmFree(res);

	TVP res2 = GET_VTABLE_FUNC( A,l,CLASS_A_sum)(l);
	printf("res2 is %d\n",res2->value.intVal);
	EXPECT_EQ (4,res2->value.intVal);
	vdmFree(res2);

	EXPECT_EQ (4,l->field1->value.intVal);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);
}

TEST(A, _new)
{
	TVP c=A._new();
	UNWRAP_CLASS_A(l, c);

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation calc",5,CALL_FUNC(A,A,c,CLASS_A_calc,a,b));

	checkFreeInt("calculation sum",4,CALL_FUNC(A,A,c,CLASS_A_sum));

	EXPECT_EQ (4,l->field1->value.intVal);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);
}




TEST(B, _new)
{
	TVP c=B._new();
	UNWRAP_CLASS_B(l,c);

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation sum",4,CALL_FUNC(B,A,c,CLASS_A_calc,a,b));

	checkFreeInt("calculation sum",9, CALL_FUNC( B,A,c,CLASS_A_sum));

	EXPECT_EQ (4,l->field1->value.intVal);

	checkFreeInt("calculation sum2",5, CALL_FUNC(B, B,c,CLASS_B_sum2));

	EXPECT_EQ (5,l->field2->value.intVal);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}

TEST(B, _newAsA)
{
	TVP c=B._new();
	UNWRAP_CLASS_B(l,c);

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation sum",4,CALL_FUNC(B,A,c,CLASS_A_calc,a,b));

	checkFreeInt("calculation sum",9,CALL_FUNC(B,A,c,CLASS_A_sum));

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}


TEST(B, _newAsC)
{
	TVP c=B._new();
	UNWRAP_CLASS_B(l,c);

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calculation sum",17.34,CALL_FUNC(B,C,c,CLASS_C_calc,a,b));

	checkFreeDouble("calculation field1c",12.34, CCCALL( B,C,l,CLASS_C_getField1);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}


TEST(C, _new)
{
	TVP c=C._new();
	UNWRAP_CLASS_C(l,c);

	TVP a = newInt(1);
	TVP b = newInt(4);

	checkFreeDouble("calc c as C",17.34, CALL_FUNC( C,C,c,CLASS_C_calc,a,b));

	checkFreeDouble("getfield1c",12.34, CALL_FUNC( C,C,c,CLASS_C_getField1));

	TVP f1 = CALL_FUNC(C,C,c,CLASS_C_getField1);
	printf("field one with macro is: %f\n",f1->value.doubleVal);

	EXPECT_EQ (12.34,l->field1c->value.doubleVal);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}

