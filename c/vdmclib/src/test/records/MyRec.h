#ifndef CLASSES_MyRec_H_
#define CLASSES_MyRec_H_

#define VDM_CG

#include "Vdm.h"
#include "VdmClass.h"

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



#define CLASS_ID_MyRec_ID 1

#define MyRecCLASS struct MyRec*

struct MyRec
{

	/* Definition of Class: 'MyRec' */
	VDM_CLASS_BASE_DEFINITIONS(MyRec);

	VDM_CLASS_FIELD_DEFINITION(MyRec,numFields);
	VDM_CLASS_FIELD_DEFINITION(MyRec,field1);
	VDM_CLASS_FIELD_DEFINITION(MyRec,field2);
};


/* -------------------------------
 *
 * Constructors
 *
 --------------------------------- */ 



TVP _Z5MyRecEIC(MyRecCLASS this_, TVP param_field1, TVP param_field2);


/* -------------------------------
 *
 * public access functions
 *
 --------------------------------- */ 

void MyRec_const_init();
void MyRec_const_shutdown();
void MyRec_static_init();
void MyRec_static_shutdown();


/* -------------------------------
 *
 * Internal
 *
 --------------------------------- */ 


void MyRec_free_fields(MyRecCLASS);
MyRecCLASS MyRec_Constructor(MyRecCLASS);



#endif /* CLASSES_MyRec_H_ */

#endif /* NO_RECORDS */
