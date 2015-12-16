#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include "classes/A.h"
#include "classes/B.h"
#include "classes/C.h"
#include <stdio.h>
}

struct K
{
	int a;
	int b;
	struct VTable* tbl;
};

static void calc1(void*)
{
}

struct VTable VTableArrayForK[] =
{
/*
 Vtable entry virtual function sum.
 */
{ 0, 0, (VirtualFunctionPointer) calc1 },

};

#define GET_STRUCT_FIELD(tname,ptr,fieldtype,fieldname) (*( (fieldtype*) (  ((unsigned char*)ptr) + offsetof(struct tname, fieldname) )  ))
//#define GET_VTABLE_FUNC(tname,ptr,id)   (*( (struct VTable**) (  ((unsigned char*)ptr) + offsetof(struct tname, _##tname##_pVTable) )  ))     [id].pFunc
#define GET_VTABLE_FUNC(tname,ptr,id)   GET_STRUCT_FIELD(tname,ptr,struct VTable*,_##tname##_pVTable)[id].pFunc

//*((int*) (((void*) kp) + offsetof(struct KK, c)))

#define GET_VTABLE_FUNC_CAST(tname,tname2,ptr,id)  ((struct VTable*) *(void**)(ptr+offsetof(struct tname, _##tname##_pVTable )))[id].pFunc

typedef void (*function)(void);
TEST(A, _new)
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

struct KK
{

	int a;
	int b;
	struct VTable* tbl;
	int c;
	struct VTable* tbl2;

};

TEST(KK, _new)
{

	struct KK k =
	{ 1, 2, VTableArrayForK, 999, VTableArrayForK };

	struct KK* kp = &k;

	printf("K.a = %d\n", kp->a);

	int c = *((int*) (((unsigned char*) kp) + offsetof(struct KK, c)));
	struct VTable* tbl2 = GET_STRUCT_FIELD(KK, kp, struct VTable*, tbl2);

	printf("K.a = %d\n", kp->a);

}

TEST(B, _new)
{
	TVP c=B._new();
	UNWRAP_CLASS_B(l,c);

	TVP a = newInt(1);
	TVP b = newInt(4);

	TVP res = GET_VTABLE_FUNC( A,l,CLASS_A_calc)(l,a,b);

	printf("res is %f\n",res->value.doubleVal);
	EXPECT_EQ (4,res->value.doubleVal);
	vdmFree(res);

	TVP res2 = GET_VTABLE_FUNC( B,l,CLASS_A_sum)(l);
	printf("res2 is %d\n",res2->value.intVal);
	EXPECT_EQ (9,res2->value.intVal);
	vdmFree(res2);

	EXPECT_EQ (4,l->field1->value.intVal);

	TVP res3 = GET_VTABLE_FUNC( B,l,CLASS_B_sum2)(l);
	printf("res3 is %d\n",res2->value.intVal);
	EXPECT_EQ (5,res3->value.intVal);

	EXPECT_EQ (5,l->field2->value.intVal);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}

#define CALL_FUNC_ARGS(classValue,type,func_id,var,...) TVP var = NULL;{UNWRAP_CLASS_C(l,classValue); var = GET_VTABLE_FUNC( type,l,func_id)(l,__VA_ARGS__);}
#define CALL_FUNC(classValue,type,func_id,var) TVP var = NULL;{UNWRAP_CLASS_C(l,classValue); var = GET_VTABLE_FUNC( type,l,func_id)(l);}

TEST(C, _new)
{
	TVP c=C._new();
	UNWRAP_CLASS_C(l,c);

	TVP a = newInt(1);
	TVP b = newInt(4);

	struct VTable* tbl = GET_STRUCT_FIELD(C,l,struct VTable*,_C_pVTable);

	TVP res = GET_VTABLE_FUNC( C,l,CLASS_C_calc)(l,a,b);

	printf("res is %f\n",res->value.doubleVal);
	EXPECT_EQ (17.34,res->value.doubleVal);
	vdmFree(res);

	TVP res2 = GET_VTABLE_FUNC( C,l,CLASS_C_getField1)(l);
	printf("res2 is %f\n",res2->value.doubleVal);
	EXPECT_EQ (12.34,res2->value.doubleVal);
	vdmFree(res2);

	CALL_FUNC(c,C,CLASS_C_getField1,f1);
	printf("field one with macro is: %f\n",f1->value.doubleVal);

	EXPECT_EQ (12.34,l->field1c->value.doubleVal);

	vdmFree(a);
	vdmFree(b);
	vdmFree(c);

}

