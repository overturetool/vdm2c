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
	TVP a = newBool(false);
	bool b = toBool(a);

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

	test_newBool();
	test_vdmNot();
	test_vdmOr();
	test_vdmAnd();
	test_vdmXor();
	test_vdmImplies();
	test_vdmBiimplication();

	test_newReal();
	test_newChar();
	test_newQuote();
	test_newSetWithValues();
	test_newSeqWithValues();

	return 0;
}

