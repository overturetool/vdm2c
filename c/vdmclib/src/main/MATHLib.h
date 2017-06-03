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
 * <http:XXXwww.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

/*
 * MATHLib.h
 *
 *  Created on: May 26, 2015
 *      Author: Victor Bandur
 */

#ifndef MATHLIB_H_
#define MATHLIB_H_

#include "Vdm.h"

#ifndef NO_MATH

TVP vdm_MATH_fac(TVP a);
TVP vdm_MATH_log(TVP a);
TVP vdm_MATH_ln(TVP a);
TVP vdm_MATH_exp(TVP a);
TVP vdm_MATH_pi_f();
TVP vdm_MATH_sqrt(TVP a);
TVP vdm_MATH_acot(TVP a);
TVP vdm_MATH_atan(TVP v);
TVP vdm_MATH_acos(TVP a);
TVP vdm_MATH_asin(TVP a);
TVP vdm_MATH_cot(TVP a);
TVP vdm_MATH_tan(TVP a);
TVP vdm_MATH_cos(TVP v);
TVP vdm_MATH_sin(TVP v);
TVP vdm_MATH_srand2(TVP a);
TVP vdm_MATH_rand(TVP a);
void vdm_MATH_srand(TVP a);

#endif /* NO_MATH */
#endif /* MATHLIB_H_ */
