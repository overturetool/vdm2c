#include "../main/Vdm.h"
#include <stdio.h>
#include <time.h>


//-----------  Basic Values  ---------------------

void test_newInt()
{
	int numRuns;
	int run;

	TVP theInt;

	srand(time(NULL));

	for(run = 0, numRuns = 1000; run < numRuns; run++)
	{
		theInt = newInt(rand());		
		vdmFree(theInt);
	}
}



void test_toInteger()
{
	TVP a;
	int i, numRuns, res;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		res = toInteger(a);
		vdmFree(a);

		a = newNat(rand());
		res = toInteger(a);
		vdmFree(a);

		a = newNat1(rand());
		res = toInteger(a);
		vdmFree(a);

		/*  Not yet implemented.
		a = newRat(rand());
		vdmFree(a);
		 */
	}
}



void test_toDouble()
{
	TVP a;
	int i, numRuns;
	double res;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		res = toDouble(a);
		vdmFree(a);

		a = newNat(rand());
		res = toDouble(a);
		vdmFree(a);

		a = newNat1(rand());
		res = toDouble(a);
		vdmFree(a);

		a = newReal(rand());
		res = toDouble(a);
		vdmFree(a);

		/*  Not yet implemented.
		a = newRat(rand());
		vdmFree(a);
		 */
	}
}



void test_newNat()
{
	int numRuns;
	int run;

	TVP theNat;

	srand(time(NULL));

	for(run = 0, numRuns = 1000; run < numRuns; run++)
	{
		theNat = newNat(rand());		
		vdmFree(theNat);
	}
}


void test_newNat1()
{
	int numRuns;
	int run;

	TVP theNat1;

	srand(time(NULL));

	for(run = 0, numRuns = 1000; run < numRuns; run++)
	{
		theNat1 = newNat1(rand());		
		vdmFree(theNat1);
	}
}



void test_vdmMinus()
{
	TVP a;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		res = vdmMinus(a);
		vdmFree(a);
		vdmFree(res);

		a = newNat(rand());
		res = vdmMinus(a);
		vdmFree(a);
		vdmFree(res);

		a = newNat1(rand());
		res = vdmMinus(a);
		vdmFree(a);
		vdmFree(res);

		a = newReal(rand());
		res = vdmMinus(a);
		vdmFree(a);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		vdmFree(a);
		 */
	}
}



void test_vdmAbs()
{
	TVP a;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		res = vdmAbs(a);
		vdmFree(a);
		vdmFree(res);

		a = newNat(rand());
		res = vdmAbs(a);
		vdmFree(a);
		vdmFree(res);

		a = newNat1(rand());
		res = vdmAbs(a);
		vdmFree(a);
		vdmFree(res);

		a = newReal(rand());
		res = vdmAbs(a);
		vdmFree(a);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		 */
	}
}



void test_vdmFloor()
{
	TVP a;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newReal(rand());
		res = vdmFloor(a);
		vdmFree(a);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		 */
	}
}



void test_vdmSum()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand());
		res = vdmSum(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand());
		b = newNat(rand());
		res = vdmSum(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand());
		b = newNat1(rand());
		res = vdmSum(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand());
		b = newReal(rand());
		res = vdmSum(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		res = toInteger(a);
		vdmFree(a);
		 */
	}
}


void test_vdmDifference()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand());
		res = vdmDifference(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand());
		b = newNat(rand());
		res = vdmDifference(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand());
		b = newNat1(rand());
		res = vdmDifference(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand());
		b = newReal(rand());
		res = vdmDifference(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		res = toInteger(a);
		vdmFree(a);
		 */
	}
}


void test_vdmProduct()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand());
		res = vdmProduct(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand());
		b = newNat(rand());
		res = vdmProduct(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand());
		b = newNat1(rand());
		res = vdmProduct(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand());
		b = newReal(rand());
		res = vdmProduct(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		res = toInteger(a);
		vdmFree(a);
		 */
	}
}


void test_vdmGreaterThan()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand());
		res = vdmGreaterThan(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand());
		b = newNat(rand());
		res = vdmGreaterThan(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand());
		b = newNat1(rand());
		res = vdmGreaterThan(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand());
		b = newReal(rand());
		res = vdmGreaterThan(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		res = toInteger(a);
		vdmFree(a);
		 */
	}
}



void test_vdmGreaterOrEqual()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand());
		res = vdmGreaterOrEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand());
		b = newNat(rand());
		res = vdmGreaterOrEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand());
		b = newNat1(rand());
		res = vdmGreaterOrEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand());
		b = newReal(rand());
		res = vdmGreaterOrEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		res = toInteger(a);
		vdmFree(a);
		 */
	}
}



void test_vdmEqual()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand());
		res = vdmEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand());
		b = newNat(rand());
		res = vdmEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand());
		b = newNat1(rand());
		res = vdmEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand());
		b = newReal(rand());
		res = vdmEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		res = toInteger(a);
		vdmFree(a);
		 */
	}
}




void test_vdmNotEqual()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand());
		res = vdmNotEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand());
		b = newNat(rand());
		res = vdmNotEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand());
		b = newNat1(rand());
		res = vdmNotEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand());
		b = newReal(rand());
		res = vdmNotEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		res = toInteger(a);
		vdmFree(a);
		 */
	}
}



void test_vdmLessThan()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand());
		res = vdmLessThan(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand());
		b = newNat(rand());
		res = vdmLessThan(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand());
		b = newNat1(rand());
		res = vdmLessThan(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand());
		b = newReal(rand());
		res = vdmLessThan(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		res = toInteger(a);
		vdmFree(a);
		 */
	}
}



void test_vdmLessOrEqual()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand());
		res = vdmLessOrEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand());
		b = newNat(rand());
		res = vdmLessOrEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand());
		b = newNat1(rand());
		res = vdmLessOrEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand());
		b = newReal(rand());
		res = vdmLessOrEqual(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		res = toInteger(a);
		vdmFree(a);
		 */
	}
}



void test_vdmPower()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand() % 10);
		b = newInt(rand() % 10);
		res = vdmPower(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand() % 10);
		b = newNat(rand() % 10);
		res = vdmPower(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand() % 10);
		b = newNat1(rand() % 10);
		res = vdmPower(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand() % 10);
		b = newReal(rand() % 10);
		res = vdmPower(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		vdmFree(a);
		 */
	}
}



void test_vdmDivision()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand() + 1);
		res = vdmDivision(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat(rand());
		b = newNat(rand() + 1);
		res = vdmDivision(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newNat1(rand());
		b = newNat1(rand() + 1);
		res = vdmDivision(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		a = newReal(rand());
		b = newReal(rand() + 1);
		res = vdmDivision(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);

		/*  Not yet implemented.
		a = newRat(rand());
		res = toInteger(a);
		vdmFree(a);
		 */
	}
}



void test_vdmDiv()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand() + 1);
		res = vdmDiv(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);
	}
}




void test_vdmRem()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand() + 1);
		res = vdmRem(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);
	}
}



void test_vdmMod()
{
	TVP a;
	TVP b;
	TVP res;
	int i, numRuns;

	srand(time(NULL));

	for(i = 0, numRuns = 1000;  i < numRuns;  i++)
	{
		a = newInt(rand());
		b = newInt(rand() + 1);
		res = vdmMod(a, b);
		vdmFree(a);
		vdmFree(b);
		vdmFree(res);
	}
}



void test_newBool()
{
	int numRuns;
	int run;

	TVP theBool;

	srand(time(NULL));

	for(run = 0, numRuns = 1000; run < numRuns; run++)
	{
		theBool = newBool(rand() % 2);		
		vdmFree(theBool);
	}
}



void test_vdmNot()
{
	TVP a = newBool(true);
	TVP res = vdmNot(a);

	vdmFree(a);
	vdmFree(res);
}



void test_vdmOr()
{
	TVP a = newBool(true);
	TVP b = newBool(false);
	TVP res = vdmOr(a, b);

	vdmFree(a);
	vdmFree(b);
	vdmFree(res);
}



void test_vdmAnd()
{
	TVP a = newBool(true);
	TVP b = newBool(false);
	TVP res = vdmAnd(a, b);

	vdmFree(a);
	vdmFree(b);
	vdmFree(res);
}



void test_vdmXor()
{
	TVP a = newBool(true);
	TVP b = newBool(false);
	TVP res = vdmXor(a, b);

	vdmFree(a);
	vdmFree(b);
	vdmFree(res);
}



void test_vdmImplies()
{
	TVP a = newBool(true);
	TVP b = newBool(false);
	TVP res = vdmImplies(a, b);

	vdmFree(a);
	vdmFree(b);
	vdmFree(res);
}



void test_vdmBiimplication()
{
	TVP a = newBool(true);
	TVP b = newBool(false);
	TVP res = vdmBiimplication(a, b);

	vdmFree(a);
	vdmFree(b);
	vdmFree(res);
}


void test_toBool()
{
	TVP a;
	bool b;

	a = newBool(false);
	b = toBool(a);
	vdmFree(a);

	a = newBool(true);
	b = toBool(a);
	vdmFree(a);
}


void test_newReal()
{
	int numRuns;
	int run;

	TVP theReal;

	srand(time(NULL));

	for(run = 0, numRuns = 1000; run < numRuns; run++)
	{
		theReal = newReal(3.3);
		vdmFree(theReal);
	}
}



void test_newChar()
{
	int numRuns;
	int run;

	TVP theChar;

	srand(time(NULL));

	for(run = 0, numRuns = 1000; run < numRuns; run++)
	{
		theChar = newChar('a');
		vdmFree(theChar);
	}
}



void test_newQuote()
{
	int numRuns;
	int run;

	TVP theQuote;

	srand(time(NULL));

	for(run = 0, numRuns = 1000; run < numRuns; run++)
	{
		theQuote = newQuote(rand());		
		vdmFree(theQuote);
	}
}



//------------  Sets  -------------------
TVP generateRandomSet()
{
	int numElems = 100;
	TVP elems[numElems];
	TVP theSet;
	TVP res;
	int elem, i;

	srand(time(NULL));

	//Test set containing basic values.
	for(elem = 0;  elem < numElems;  elem++)
	{
		elems[elem] = newInt(rand() % 10);  //Modulo 10 to force some duplicates.
	}

	theSet = newSetWithValues(numElems, elems);

	for(elem = 0;  elem < numElems;  elem++)
	{
		vdmFree(elems[elem]);
	}

	return theSet;
}



void test_newSetWithValues()
{
	int numElems = 100;
	int numSubElems;
	TVP elems[numElems];
	TVP *subElems;
	TVP theSet;
	int elem, i;

	srand(time(NULL));

	//Test set containing basic values.
	for(elem = 0;  elem < numElems;  elem++)
	{
		elems[elem] = newInt(rand() % 10);  //Modulo 10 to force some duplicates.
	}

	theSet = newSetWithValues(numElems, elems);

	for(elem = 0;  elem < numElems;  elem++)
	{
		vdmFree(elems[elem]);
	}

	vdmFree(theSet);

	//Test set containing sets.
	for(elem = 0;  elem < numElems;  elem++)
	{
		//Number of elements in current element.
		numSubElems = rand() %	100;
		subElems = malloc(numSubElems * sizeof(TVP));

		//Populate current element.
		for(i = 0;  i < numSubElems;  i++)
		{
			subElems[i] = newInt(rand());
		}

		elems[elem] = newSetWithValues(numSubElems, subElems);

		for(i = 0;  i < numSubElems;  i++)
		{
			vdmFree(subElems[i]);
		}
		free(subElems);
	}

	//Create test set.
	theSet = newSetWithValues(numElems, elems);

	//Clean up.
	for(elem = 0;  elem < numElems;  elem++)
	{
		vdmFree(elems[elem]);
	}

	vdmFree(theSet);
}



void test_vdmSetElementAt()
{
	TVP theSet;
	TVP res;
	int elem, i, numElems;

	theSet = generateRandomSet();

	res = vdmSetCard(theSet);
	numElems = res->value.intVal;
	vdmFree(res);

	for(elem = 0;  elem < numElems;  elem++)
	{
		res = vdmSetElementAt(theSet, elem);
		vdmFree(res);
	}

	vdmFree(theSet);
}



void test_vdmSetMemberOf()
{

}




//------------  Sequences  --------------

void test_newSeqWithValues()
{
	int numElems = 100;
	int numSubElems;
	TVP elems[numElems];
	TVP *subElems;
	TVP theSeq;
	int elem, i;

	srand(time(NULL));

	//Test sequence of basic values.
	for(elem = 0;  elem < numElems;  elem++)
	{
		elems[elem] = newInt(rand() % 10);  //Modulo 10 to force some duplicates.
	}

	theSeq = newSeqWithValues(numElems, elems);

	for(elem = 0;  elem < numElems;  elem++)
	{
		vdmFree(elems[elem]);
	}

	vdmFree(theSeq);

	//Test sequence of sets.
	for(elem = 0;  elem < numElems;  elem++)
	{
		//Number of elements in current element.
		numSubElems = rand() %	100;
		subElems = malloc(numSubElems * sizeof(TVP));

		//Populate current element.
		for(i = 0;  i < numSubElems;  i++)
		{
			subElems[i] = newInt(rand());
		}

		elems[elem] = newSetWithValues(numSubElems, subElems);

		for(i = 0;  i < numSubElems;  i++)
		{
			vdmFree(subElems[i]);
		}
		free(subElems);
	}

	//Create test sequence.
	theSeq = newSeqWithValues(numElems, elems);

	//Clean up.
	for(elem = 0;  elem < numElems;  elem++)
	{
		vdmFree(elems[elem]);
	}

	vdmFree(theSeq);
}


int main()
{
	test_newInt();
	test_newNat();
	test_newNat1();
	test_newReal();
	test_newChar();
	test_newQuote();

	test_toInteger();
	test_toDouble();
	test_vdmMinus();
	test_vdmAbs();
	test_vdmFloor();
	test_vdmSum();
	test_vdmDifference();
	test_vdmProduct();
	test_vdmDivision();
	test_vdmPower();
	test_vdmDiv();
	test_vdmRem();
	test_vdmMod();
	test_vdmGreaterThan();
	test_vdmGreaterOrEqual();
	test_vdmLessThan();
	test_vdmLessOrEqual();
	test_vdmEqual();
	test_vdmNotEqual();

	test_newBool();
	test_vdmNot();
	test_vdmOr();
	test_vdmAnd();
	test_vdmXor();
	test_vdmImplies();
	test_vdmBiimplication();

	test_newSetWithValues();
	test_vdmSetElementAt();


	test_newSeqWithValues();

	return 0;
}

