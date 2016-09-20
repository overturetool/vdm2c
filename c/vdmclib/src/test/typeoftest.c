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
 * typeoftest.c
 *
 *  Created on: Dec 22, 2015
 *      Author: kel
 */
#include "classes/A.h"
#include "classes/B.h"
#include "classes/C.h"

#include <stdio.h>

#ifdef __cplusplus
#else
//#define X(a,b) (&a==&b?1:0)
//#define X(a,b) (sizeof(a)==sizeof(b)?1:0)

#define min(x,y) ({ \
    #x == #y; })

#define SAME_ARGS(x,y) #x==#y
#endif

bool typeoftest(int id)
{
	bool i = 0;
	switch (id)
	{
	case 0:
		i = true;
		break;
	case 1:
		printf("A = A:");
		i = SAME_ARGS(A, A);
		printf(" %s\n", i ? "true" : "false");
		break;

	case 2:
		printf("A = B:");
		i = SAME_ARGS(A, B);
		printf(" %s\n", i ? "true" : "false");
		i = !i;
		break;
	case 3:
		printf("A = B:");
		i = SAME_ARGS(A, B);
		printf(" %s\n", i ? "true" : "false");
		i = !i;
		break;
	case 4:
		printf("A = A:");
		i = SAME_ARGS(A, A);
		printf(" %s\n", i ? "true" : "false");
		break;
	case 5:
		printf("C = A:");
		i = SAME_ARGS(C, A);
		printf(" %s\n", i ? "true" : "false");
		i = !i;
		break;
	case 6:
		printf("C = C:");
		i = SAME_ARGS(C, C);
		printf(" %s\n", i ? "true" : "false");
		break;
	case 7:
		printf("C = B:");
		i = SAME_ARGS(C, B);
		printf(" %s\n", i ? "true" : "false");
		i = !i;
		break;
	case 8:
		printf("B = B:");
		i = SAME_ARGS(B, B);
		printf(" %s\n", i ? "true" : "false");

		break;
	}

	return i;

}
