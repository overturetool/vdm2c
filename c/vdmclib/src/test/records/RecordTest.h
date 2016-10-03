// The template for class header
#ifndef CLASSES_RecordTest_H_
#define CLASSES_RecordTest_H_

#define VDM_CG

#include "Vdm.h"

//include types used in the class
#include "MyRec.h"
#include "RecordTest.h"


/* -------------------------------
 *
 * Quotes
 *
 --------------------------------- */ 
 


/* -------------------------------
 *
 * values / global const
 *
 --------------------------------- */ 
 


/* -------------------------------
 *
 * The class
 *
 --------------------------------- */ 
 

//class id
#define CLASS_ID_RecordTest_ID 0

#define RecordTestCLASS struct RecordTest*

// The vtable ids
#define CLASS_RecordTest__Z5test1EV 0
#define CLASS_RecordTest__Z5test2EV 1

struct RecordTest
{
	
/* Definition of Class: 'RecordTest' */
	VDM_CLASS_BASE_DEFINITIONS(RecordTest);
	 
	
};


/* -------------------------------
 *
 * Constructors
 *
 --------------------------------- */ 
 

	/* ExpressionRecord.vdmrt 16:7 */
	TVP _Z10RecordTestEV(RecordTestCLASS this_);


/* -------------------------------
 *
 * public access functions
 *
 --------------------------------- */ 
 
	void RecordTest_const_init();
	void RecordTest_const_shutdown();
	void RecordTest_static_init();
	void RecordTest_static_shutdown();


/* -------------------------------
 *
 * Internal
 *
 --------------------------------- */ 
 

void RecordTest_free_fields(RecordTestCLASS);
RecordTestCLASS RecordTest_Constructor(RecordTestCLASS);



#endif /* CLASSES_RecordTest_H_ */