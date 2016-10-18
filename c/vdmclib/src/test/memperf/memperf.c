#include "../main/Vdm.h"
#include <stdio.h>
#include <time.h>

void testNewInt()
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


void testNewNat()
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


void testNewNat1()
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



void testNewBool()
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



void testNewReal()
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



void testNewChar()
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



void testNewQuote()
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



void testNewSetWithValues()
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


void testNewSeqWithValues()
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
	testNewInt();
	testNewNat();
	testNewNat1();
	testNewBool();
	testNewReal();
	testNewChar();
	testNewQuote();
	testNewSetWithValues();
	testNewSeqWithValues();

	return 0;
}

