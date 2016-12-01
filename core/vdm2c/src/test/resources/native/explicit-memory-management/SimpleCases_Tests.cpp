
#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "SimpleCases.h"
}

// Check that values do not leak
TEST(SimpleCases, values)
{
	SimpleCases_const_init();
	SimpleCases_const_shutdown();
}

// Check that static instance variables do not leak
TEST(SimpleCases, statics)
{
	SimpleCases_static_init();
	SimpleCases_static_shutdown();
}


