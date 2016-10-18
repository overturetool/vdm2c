all:
	gcc -I../../main -o memperf memperf.c vdmclib.a -lm
check:
	valgrind --leak-check=full ./memperf
massif:
	valgrind --tool=massif ./memperf
ar:
	rm memperf.o
	gcc -c -std=c11 ~/Overture/vdm2c/c/vdmclib/src/main/*.c
	ar rcs vdmclib.a *.o
