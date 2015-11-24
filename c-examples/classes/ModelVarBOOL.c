/*
 * ModelVarBOOL.c
 *
 *  Created on: Nov 20, 2015
 *      Author: kel
 */
#include "ModelVarBOOL.h"
#include "TypedValue.h"


//

#define ModelVarBOOL_ID_FIELDS ._id=ModelVarBOOL_ID, ._refs = 0
#define ModelVarBOOL_fun_map .openContact=&openContact, .closedContact=&closedContact,\
	.coil=&coil, .setCoil=&setCoil, .resetCoil=&resetCoil,.notCoil=&notCoil, .PContact=&PContact, .NContact=&NContact, .ff=&ff, .gg=&gg,.dd=&dd

//member functions
static bool openContact(struct ModelVarBOOL *this)
{
	return this->value;
}

static bool closedContact(struct ModelVarBOOL *this)
{
	return !this->value;
}
static void coil(struct ModelVarBOOL *this)
{
	this->value = true;
}
static void setCoil(struct ModelVarBOOL *this)
{
	this->lock = true;
	this->value = true;
}
static void resetCoil(struct ModelVarBOOL *this)
{
	this->lock = false;
	this->value = false;
}
static void notCoil(struct ModelVarBOOL *this)
{
	if (this->lock == false)
	{
		this->value = false;
	}
}
static bool PContact(struct ModelVarBOOL *this)
{
	return (this->past == false && this->value == true);
}
static bool NContact(struct ModelVarBOOL *this)
{
	return (this->past == true && this->value == false);
}

static struct TypedValue* ff(struct ModelVarBOOL *this)
{
	struct TypedValue* g = newSeq(2);
//	struct	 ModelVarBOOL** g = malloc(sizeof(struct ModelVarBOOL*)*2);

	struct Collection* c = g->value;

//	g[0]= this;
	*(c->value + 0) = newTypeValue(VDM_CLASS, newClassValue(this->_id, &this->_refs, this));

//	g[1]=this;
	*(c->value + 1) = newTypeValue(VDM_CLASS, newClassValue(this->_id, &this->_refs, this));
	return g;
}

static bool gg(struct ModelVarBOOL *this)
{
	//ff() =  ff()
	struct TypedValue* left = this->ff(this);
	struct TypedValue* right = this->ff(this);

	bool res = left->value == right->value; //should be function now we compare memory locations
	recursiveFree(left);
	recursiveFree(right);

	return res;
	//{this,this,NULL}
}

static bool dd(struct ModelVarBOOL *this, struct TypedValue* x)
{
	//x =  ff()
	struct TypedValue* left = x;
	struct TypedValue* right = this->ff(this); //this->

	bool res = left == right;

	recursiveFree(right);

	return res;
	//{this,this,NULL}
}

//static bool ddd(struct ModelVarBOOL *this)
//{
//	//return [self,self];
//	int size = 2;
//	struct Collection set = {malloc(sizeof(struct ModelVarBOOL*)*2),size};
//	struct TypedValue v = {&set,VDM_SET};
//
//}

static struct TypedValue* forlen(struct ModelVarBOOL *this, struct TypedValue* v)
{
	//DTC
	assert(v->type == VDM_SEQ && "Value is not a seq");
	struct Collection* inner = v->value;
	struct TypedValue* first = inner->value[0];
	assert(first->type == VDM_CHAR && "Value is not a char");

	return newTypeValue(VDM_INT, inner->size);

}

//static bool ll(struct ModelVarBOOL *this)
//{
//	void* x =0;
//
//	if(true)
//		x='c';
//	else
//		x= 7;
//
//}

static struct ModelVarBOOL new()
{
	return (struct ModelVarBOOL
			)
			{ ModelVarBOOL_ID_FIELDS, .value = false, .past = false, .lock = false, ModelVarBOOL_fun_map } ;
		}

//const struct ModelVarBOOLClass ModelVarBOOL =	{ .new = &new };

		static struct ModelVarBOOL newB(bool x)
		{
			return (struct ModelVarBOOL
					)
					{ ModelVarBOOL_ID_FIELDS, .value = x, .past = false, .lock = false, ModelVarBOOL_fun_map } ;
				}

//const struct ModelVarBOOLClass ModelVarBOOL =	{ .newB = &newB };

				static struct ModelVarBOOL newCharPtr(char * x)
				{
					return (struct ModelVarBOOL
							)
							{ ModelVarBOOL_ID_FIELDS, .value = false, .past = false, .lock = false, .memory_adresse = x,
							ModelVarBOOL_fun_map } ;
						}

						const struct ModelVarBOOLClass ModelVarBOOL =
						{ .new = &new, .newB = &newB, .newCharPtr = &newCharPtr };

