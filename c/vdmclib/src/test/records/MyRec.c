#include "MyRec.h"
#include <stdio.h>
#include <string.h>

#ifndef NO_RECORDS


/* -------------------------------
 *
 * Memory management methods
 *
 --------------------------------- */

void MyRec_free_fields(struct MyRec *this)
{
	vdmFree(this->m_MyRec_field1);
	vdmFree(this->m_MyRec_field2);
}

static void MyRec_free(struct MyRec *this)
{
	MyRec_free_fields(this);
	free(this);
}


/* -------------------------------
 *
 * Member methods 
 *
 --------------------------------- */


void MyRec_const_init()	{

	return ;
}



void MyRec_const_shutdown()	{

	return ;
}



void MyRec_static_init()	{

	return ;
}



void MyRec_static_shutdown()	{

	return ;
}




/* -------------------------------
 *
 * VTable
 *
 --------------------------------- */


static  struct VTable VTableArrayForMyRec  [0]  ;




/* -------------------------------
 *
 * Internal memory constructor
 *
 --------------------------------- */


MyRecCLASS MyRec_Constructor(MyRecCLASS this_ptr)
{

	if(this_ptr==NULL)
	{
		this_ptr = (MyRecCLASS) malloc(sizeof(struct MyRec));
	}

	if(this_ptr!=NULL)
	{


		this_ptr->_MyRec_id = CLASS_ID_MyRec_ID;
		this_ptr->_MyRec_pVTable=VTableArrayForMyRec;

		this_ptr->m_MyRec_field1= NULL ;
		this_ptr->m_MyRec_field2= NULL ;
		this_ptr->m_MyRec_numFields= NULL ;
	}

	return this_ptr;
}

static TVP new()
				{
	MyRecCLASS ptr=MyRec_Constructor(NULL);

	return newTypeValue(VDM_RECORD, (TypedValueType)
			{	.ptr=newClassValue(ptr->_MyRec_id, (freeVdmClassFunction)&MyRec_free, ptr)});
				}



/* -------------------------------
 *
 * Public class constructors
 *
 --------------------------------- */ 



TVP _Z5MyRecEIC(MyRecCLASS this, TVP param_field1, TVP param_field2)	{

	TVP __buf = NULL;

	if ( this == NULL )

	{

		__buf = new();

		this = TO_CLASS_PTR(__buf, MyRec);
	}
	;

	TVP field_tmp_1 = vdmClone(param_field1);

	SET_FIELD_PTR(MyRec, MyRec, this, field1, field_tmp_1);

	vdmFree(field_tmp_1);

	TVP field_tmp_2 = vdmClone(param_field2);

	SET_FIELD_PTR(MyRec, MyRec, this, field2, field_tmp_2);

	vdmFree(field_tmp_2);

	SET_FIELD_PTR(MyRec, MyRec, this, numFields, newInt(2));

	return __buf;
}




/* -------------------------------
 *
 * Global class fields
 *
 --------------------------------- */


#endif /* NO_RECORDS */
