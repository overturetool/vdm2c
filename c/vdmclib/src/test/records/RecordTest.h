#ifndef CLASSES_RecordTest_H_
#define CLASSES_RecordTest_H_

#define VDM_CG

#include "Vdm.h"

#include "MyRec.h"
#include "MyRec2.h"
#include "RecordTest.h"


#ifndef NO_RECORDS

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
 

#define CLASS_ID_RecordTest_ID 0

#define RecordTestCLASS struct RecordTest*

#define CLASS_RecordTest__Z5test1EV 0
#define CLASS_RecordTest__Z5test2EV 1
#define CLASS_RecordTest__Z5test3EV 2
#define CLASS_RecordTest__Z5test4EV 3
#define CLASS_RecordTest__Z5test5EV 4

struct RecordTest
{
	
/* Definition of Class: 'RecordTest' */
	VDM_CLASS_BASE_DEFINITIONS(RecordTest);
	 
	VDM_CLASS_FIELD_DEFINITION(RecordTest,numFields);

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
#endif /* NO_RECORDS */
