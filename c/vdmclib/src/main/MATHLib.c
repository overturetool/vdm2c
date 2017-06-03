/*
 * MATHLib.c
 *
 *  Created on: May 26, 2016
 *      Author: Victor Bandur
 */


#include "MATHLib.h"
#include <math.h>

#ifndef CUSTOM_MATH
#ifndef NO_MATH

TVP vdm_MATH_fac(TVP a)
{
	TVP res;
	int tmp;
	int tmpres = 1;

	if(a->type != VDM_NAT && a->type != VDM_NAT1)
		return NULL;

	if(a->value.intVal < 0)
		return NULL;

	if(a->value.intVal == 0)
	{
		res = newInt(1);
		return res;
	}

	for(tmp = a->value.intVal;  tmp > 0; tmp--)
	{
		tmpres *= tmp;
	}

	res = newInt(tmpres);
	return res;
}



TVP vdm_MATH_log(TVP a)
{
	if(a->type == VDM_NAT)
	{
		if(a->value.intVal <= 0)
			return NULL;
		return newReal(log10((double)(a->value.intVal)));
	}

	if(a->type == VDM_NAT1)
	{
		if(a->value.intVal <= 0)
			return NULL;
		return newReal(log10((double)(a->value.intVal)));
	}

	if(a->type == VDM_REAL)
	{
		if(a->value.doubleVal <= 0)
			return NULL;
		return newReal(log10(a->value.doubleVal));
	}

	if(a->type == VDM_INT)
	{
		if(a->value.intVal <= 0)
			return NULL;
		return newReal(log10((double)(a->value.intVal)));
	}

	/* 	RAT not yet implemented completely.  */
	/* 	if(a->type != VDM_RAT)  */
	/* 		return NULL;  */
	/* 	else  */
	/* 		return newReal(log10((double)(a->value.intVal)))  */

	return NULL;
}



TVP vdm_MATH_ln(TVP a)
{
	if(a->type == VDM_NAT)
	{
		if(a->value.intVal <= 0)
			return NULL;
		return newReal(log((double)(a->value.intVal)));
	}

	if(a->type == VDM_NAT1)
	{
		if(a->value.intVal <= 0)
			return NULL;
		return newReal(log((double)(a->value.intVal)));
	}

	if(a->type == VDM_REAL)
	{
		if(a->value.doubleVal <= 0)
			return NULL;
		return newReal(log(a->value.doubleVal));
	}

	if(a->type == VDM_INT)
	{
		if(a->value.intVal <= 0)
			return NULL;
		return newReal(log((double)(a->value.intVal)));
	}

	return NULL;
}



TVP vdm_MATH_exp(TVP a)
{
	/* not yet implemented.  */
	return NULL;
}



TVP vdm_MATH_pi_f()
{
	return newReal(3.14159265358979323846);
}



TVP vdm_MATH_sqrt(TVP a)
{
	if(a->type == VDM_NAT)
	{
		if(a->value.intVal < 0)
			return NULL;
		return newReal(sqrt((double)(a->value.intVal)));
	}

	if(a->type == VDM_NAT1)
	{
		if(a->value.intVal < 0)
			return NULL;
		return newReal(sqrt((double)(a->value.intVal)));
	}

	if(a->type == VDM_REAL)
	{
		if(a->value.doubleVal < 0)
			return NULL;
		return newReal(sqrt(a->value.doubleVal));
	}

	if(a->type == VDM_INT)
	{
		if(a->value.intVal < 0)
			return NULL;
		return newReal(sqrt((double)(a->value.intVal)));
	}

	return NULL;
}



TVP vdm_MATH_acot(TVP a)
{
	/* not yet implemented.  */
	return NULL;
}



TVP vdm_MATH_atan(TVP v)
{
	if(v->type == VDM_NAT || v->type == VDM_NAT1 || v->type == VDM_INT)
	{
		return newReal(atan((double)(v->value.intVal)));
	}


	if(v->type == VDM_REAL)
	{
		return newReal(atan(v->value.doubleVal));
	}

	return NULL;
}



TVP vdm_MATH_acos(TVP a)
{
	/* not yet implemented.  */
	return NULL;
}



TVP vdm_MATH_asin(TVP a)
{
	/* not yet implemented.  */
	return NULL;
}



TVP vdm_MATH_cot(TVP a)
{
	/* not yet implemented.  */
	return NULL;
}



TVP vdm_MATH_tan(TVP a)
{
	/* not yet implemented.  */
	return NULL;
}


TVP vdm_MATH_cos(TVP v)
{
	if(v->type == VDM_NAT || v->type == VDM_NAT1 || v->type == VDM_INT)
	{
		return newReal(cos((double)(v->value.intVal)));
	}


	if(v->type == VDM_REAL)
	{
		return newReal(cos(v->value.doubleVal));
	}

	return NULL;
}



TVP vdm_MATH_sin(TVP v)
{
	if(v->type == VDM_NAT || v->type == VDM_NAT1 || v->type == VDM_INT)
	{
		return newReal(sin((double)(v->value.intVal)));
	}


	if(v->type == VDM_REAL)
	{
		return newReal(sin(v->value.doubleVal));
	}

	return NULL;
}



TVP vdm_MATH_srand2(TVP a)
{
	/* not yet implemented.  */
	return NULL;
}



TVP vdm_MATH_rand(TVP a)
{
	/* not yet implemented.  */
	return NULL;
}



void vdm_MATH_srand(TVP a)
{
	/* not yet implemented.  */
}


#endif /* NO_MATH */
#endif /* CUSTOM_MATH */
