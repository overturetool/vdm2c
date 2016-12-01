
#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "SimpleCases.h"
}

// Check that values do not leak
TEST(SimpleCases, initShutdown)
{
	SimpleCases_const_init();
	SimpleCases_const_shutdown();
}
