// The template for class header
#ifndef CLASSES_MyRec2_H_
#define CLASSES_MyRec2_H_

#define VDM_CG

#include "Vdm.h"

//include types used in the class
#include "VdmClass.h"


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
#define CLASS_ID_MyRec2_ID 2

#define MyRec2CLASS struct MyRec2*

// The vtable ids

struct MyRec2
{
	
/* Definition of Class: 'MyRec2' */
	VDM_CLASS_BASE_DEFINITIONS(MyRec2);
	 
	VDM_CLASS_FIELD_DEFINITION(MyRec2,numFields);
	VDM_CLASS_FIELD_DEFINITION(MyRec2,field1);
	VDM_CLASS_FIELD_DEFINITION(MyRec2,field2);
};


/* -------------------------------
 *
 * Constructors
 *
 --------------------------------- */ 
 

	
	TVP _Z6MyRec2EI1SI(MyRec2CLASS this_, TVP param_field1, TVP param_field2);


/* -------------------------------
 *
 * public access functions
 *
 --------------------------------- */ 
 
	void MyRec2_const_init();
	void MyRec2_const_shutdown();
	void MyRec2_static_init();
	void MyRec2_static_shutdown();


/* -------------------------------
 *
 * Internal
 *
 --------------------------------- */ 
 

void MyRec2_free_fields(MyRec2CLASS);
MyRec2CLASS MyRec2_Constructor(MyRec2CLASS);



#endif /* CLASSES_MyRec2_H_ */
