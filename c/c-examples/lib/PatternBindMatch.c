/*
 * #%~
 * The VDM to C Code Generator
 * %%
 * Copyright (C) 2015 - 2016 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */


/*
 * PatternBindMatch.c
 *
 *  Created on: Dec 3, 2015
 *      Author: kel
 */
#include "PatternBindMatch.h"

#define SAMETYPE(a,b) a->type ==b->type

//this is for match or bind and not for pattern identifier, this should be generated as p= value instead
bool patternMatchBind(TVP patternBind, TVP value)
{
	switch(patternBind->type)
	{
		case VDM_BOOL:
		case VDM_CHAR:
		case VDM_INT:
		case VDM_NAT:
		case VDM_NAT1:
		case VDM_QUOTE:
		case VDM_REAL:
		case VDM_RAT:
		//this is match value
		return equals(patternBind,value);

//		case VDM_OPTIONAL:
//		//TODO not sure what should happen here
//		break;

		case VDM_SET:
		break;
		case VDM_SEQ:
		break;
		case VDM_MAP:
		break;
		case VDM_PRODUCT:
		{ //this is a tuple pattern
			if(!SAMETYPE(patternBind,value))
			return false;

			UNWRAP_PRODUCT(pc,patternBind);
			UNWRAP_PRODUCT(pv,value);

			if(!pc->size==pv->size)
			return false;

			bool match = true;
			for(int i = 0; i< pc->size;i++)
			{
				if(pc->value[i]!=NULL)
				{
					match &= patternMatchBind(pc->value[i],pv->value[i]);
				} else
				{
					//bind
					productSet(patternBind,i+1,pv->value[i]);
				}
			}

			return match;
		}
		case VDM_RECORD:
		break;
		case VDM_CLASS:
		break;
	}
	return false;
}
