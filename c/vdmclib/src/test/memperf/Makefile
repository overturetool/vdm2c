all:
	gcc -g -I../../main -o memperf memperf.c vdmclib.a -lm
check:
	valgrind -v --leak-check=full --track-origins=yes ./memperf
massif:
	valgrind --tool=massif ./memperf
ar:
	gcc -g -c -std=c11 ~/Overture/vdm2c/c/vdmclib/src/main/*.c
	ar rcs vdmclib.a *.o
