
#include <gtest/gtest.h>

using namespace std;

extern "C"
{
#include "Vdm.h"
}

class TestFlowFunctions : public testing::Test
{
public:
  TestFlowFunctions()
  {
    
  }
  ~TestFlowFunctions()
  {
    
  }
  void SetUp()
  {
    vdm_gc_init();
  }
  void TearDown(){
    vdm_gc();
    vdm_gc_shutdown();
  }
};
