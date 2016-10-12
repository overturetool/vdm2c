// The template for class
#include "MyRec2.h"
#include <stdio.h>
#include <string.h>


/* -------------------------------
 *
 * Memory management methods
 *
 --------------------------------- */

void MyRec2_free_fields(struct MyRec2 *this)
{
		vdmFree(this->m_MyRec2_field1);
			vdmFree(this->m_MyRec2_field2);
	}

static void MyRec2_free(struct MyRec2 *this)
{
	--this->_MyRec2_refs;
	if (this->_MyRec2_refs < 1)
	{
		MyRec2_free_fields(this);
		free(this);
	}
}


/* -------------------------------
 *
 * Member methods 
 *
 --------------------------------- */
 

 void MyRec2_const_init()	{

return ;
}



 void MyRec2_const_shutdown()	{

return ;
}



 void MyRec2_static_init()	{

return ;
}



 void MyRec2_static_shutdown()	{

return ;
}




/* -------------------------------
 *
 * VTable
 *
 --------------------------------- */
 
// VTable for this class
 static  struct VTable VTableArrayForMyRec2  [0]  ;

// Overload VTables


/* -------------------------------
 *
 * Internal memory constructor
 *
 --------------------------------- */
 
 
MyRec2CLASS MyRec2_Constructor(MyRec2CLASS this_ptr)
{

	if(this_ptr==NULL)
	{
		this_ptr = (MyRec2CLASS) malloc(sizeof(struct MyRec2));
	}

	if(this_ptr!=NULL)
	{
	
			
		// MyRec2 init
		this_ptr->_MyRec2_id = CLASS_ID_MyRec2_ID;
		this_ptr->_MyRec2_refs = 0;
		this_ptr->_MyRec2_pVTable=VTableArrayForMyRec2;

				this_ptr->m_MyRec2_field1= NULL ;
						this_ptr->m_MyRec2_field2= NULL ;
						this_ptr->m_MyRec2_numFields= NULL ;
			}

	return this_ptr;
}

// Method for creating new "class"
static TVP new()
{
	MyRec2CLASS ptr=MyRec2_Constructor(NULL);

	return newTypeValue(VDM_RECORD, (TypedValueType)
			{	.ptr=newClassValue(ptr->_MyRec2_id, &ptr->_MyRec2_refs, (freeVdmClassFunction)&MyRec2_free, ptr)});
}



/* -------------------------------
 *
 * Public class constructors
 *
 --------------------------------- */ 
 


 TVP _Z6MyRec2EI1SI(MyRec2CLASS this, TVP param_field1, TVP param_field2)	{

 TVP __buf = NULL;

if ( this == NULL )
	
	{

__buf = new();

this = TO_CLASS_PTR(__buf, MyRec2);
}
;

 TVP field_tmp_3 = param_field1;

SET_FIELD_PTR(MyRec2, MyRec2, this, field1, field_tmp_3);

vdmFree(field_tmp_3);

 TVP field_tmp_4 = param_field2;

SET_FIELD_PTR(MyRec2, MyRec2, this, field2, field_tmp_4);

vdmFree(field_tmp_4);

SET_FIELD_PTR(MyRec2, MyRec2, this, numFields, newInt(2));

return __buf;
}




/* -------------------------------
 *
 * Global class fields
 *
 --------------------------------- */
 
// initialize globals - this is done last since they are declared in the header but uses init functions which are printet in any order
		
