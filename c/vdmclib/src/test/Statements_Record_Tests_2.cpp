
#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "records/RecordTest.h"
}

#define CHECK(methodId) TVP a = _Z10RecordTestEV(NULL);\
TVP res=CALL_FUNC(RecordTest, RecordTest, a, methodId);\
EXPECT_EQ (true, res->value.boolVal);\
vdmFree(res);\
vdmFree(a)

TEST(Statements_Records, test1)
{
	CHECK(CLASS_RecordTest__Z5test1EV);
}

TEST(Statements_Records, test2)
{
	CHECK(CLASS_RecordTest__Z5test2EV);
}
