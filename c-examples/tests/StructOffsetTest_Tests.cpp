#include "gtest/gtest.h"

extern "C"
{
#include "lib/Vdm.h"
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




//Obtain a struct field
#define GET_STRUCT_FIELD(tname,ptr,fieldtype,fieldname) (*( (fieldtype*) (  ((unsigned char*)ptr) + offsetof(struct tname, fieldname) )  ))



struct KK
{

	int a;
	int b;
	struct VTable* tbl;
	int c;
	struct VTable* tbl2;

};

TEST(KK, offsetTest)
{
	struct KK k =
	{ 1, 2, VTableArrayForK, 999, VTableArrayForK };

	struct KK* kp = &k;

	int c = *((int*) (((unsigned char*) kp) + offsetof(struct KK, c)));
	struct VTable* tbl2 = GET_STRUCT_FIELD(KK, kp, struct VTable*, tbl2);

	EXPECT_EQ (1,kp->a);

	EXPECT_EQ (kp->c,c);
}
