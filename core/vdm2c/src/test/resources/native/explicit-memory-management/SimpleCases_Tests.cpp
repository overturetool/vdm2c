
#include "gtest/gtest.h"
#include "TestFlowFunctions.h"


extern "C"
{
#include "Vdm.h"
#include <stdio.h>
#include "SimpleCases.h"
}

// Check that values do not leak
TEST_F(TestFlowFunctions, values)
{
	SimpleCases_const_init();
	SimpleCases_const_shutdown();
}

// Check that static instance variables do not leak
TEST_F(TestFlowFunctions, statics)
{
	SimpleCases_static_init();
	SimpleCases_static_shutdown();
}

// Check that instance variables do not leak when invoking the default
// constructor
TEST_F(TestFlowFunctions,emptyConstructor)
{
  TVP c = _Z11SimpleCasesEV(NULL);
  vdmFree(c);
}

// Check that instance variables do not leak when invoking a
// parameterised constructor
TEST_F(TestFlowFunctions,parameterisedConstructor)
{
  TVP val = newInt(42);
  TVP c = _Z11SimpleCasesEI(NULL, val);
  vdmFree(c);
  vdmFree(val);
}

