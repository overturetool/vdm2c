#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"

#include "lib/VdmMap.h"
#include "lib/VdmSet.h"
#include <stdio.h>
}


TVP createMap1()
{
	//{}
	TVP map = newMap();

	//maplet1 1|->2
	TVP key1 = newInt(1);
	TVP val1 =newInt(2);
	vdmMapAdd(map,key1,val1);
	vdmFree(key1);
	vdmFree(val1);

	//maplet2 3|->4
	TVP key2 = newInt(3);
	TVP val2 =newInt(4);
	vdmMapAdd(map,key2,val2);
	vdmFree(key2);
	vdmFree(val2);

	//maplet3 6|->7
	TVP key3 = newInt(6);
	TVP val3 =newInt(7);
	vdmMapAdd(map,key3,val3);
	vdmFree(key3);
	vdmFree(val3);


	return map;
}

TVP createMap2()
{
	//{}
	TVP map = newMap();

	//maplet1 6|->8
	TVP key1 = newInt(6);
	TVP val1 =newInt(8);
	vdmMapAdd(map,key1,val1);
	vdmFree(key1);
	vdmFree(val1);

	//maplet2 9|->11
	TVP key2 = newInt(9);
	TVP val2 =newInt(11);
	vdmMapAdd(map,key2,val2);
	vdmFree(key2);
	vdmFree(val2);

	return map;
}

TVP createMap3()
{
	//{}
	TVP map = newMap();

	//maplet1 7|->8
	TVP key1 = newInt(7);
	TVP val1 =newInt(8);
	vdmMapAdd(map,key1,val1);
	vdmFree(key1);
	vdmFree(val1);

	//maplet2 9|->11
	TVP key2 = newInt(9);
	TVP val2 =newInt(11);
	vdmMapAdd(map,key2,val2);
	vdmFree(key2);
	vdmFree(val2);

	return map;
}

TVP mapApply()
{
	TVP res = NULL;
	//{1|->2,3|->4}(1)=2

	//{}
	TVP map = newMap();

	//maplet1 1|->2
	TVP key1 = newInt(1);
	TVP val1 =newInt(2);
	vdmMapAdd(map,key1,val1);
	vdmFree(key1);
	vdmFree(val1);

	//maplet2 3|->4
	TVP key2 = newInt(3);
	TVP val2 =newInt(4);
	vdmMapAdd(map,key2,val2);
	vdmFree(key2);
	vdmFree(val2);

	//apply id
	TVP tmp1 = newInt(1);

	TVP tmp2 = vdmMapApply(map,tmp1);
	vdmFree(tmp1);

	TVP tmp3 = newInt(2);

	TVP tmp4 = newBool(equals(tmp2,tmp3));


	res = vdmClone(tmp4);
	vdmFree(tmp4);
	vdmFree(tmp3);
	vdmFree(tmp2);

	return res;
}

TEST(Expression_Map, mapApply)
{
	TVP TEST_TRUE = newBool(true);

	TVP result = mapApply();

	EXPECT_EQ (true,equals(TEST_TRUE,result));

	recursiveFree(result);

	recursiveFree (TEST_TRUE);
}


TEST(Expression_Map, mapDom)
{
	//map1: {1|->2,3|->4,6|->7}
	TVP map = createMap1();

	//Get domain (returns a set)
	TVP map_dom = vdmMapDom(map);

	TVP res = vdmSetMemberOf(map_dom,newInt(3));
	EXPECT_EQ(true, res->value.boolVal);

	recursiveFree(map_dom);
	recursiveFree(res);
}

TEST(Expression_Map, mapRng)
{
	//map1: {1|->2,3|->4,6|->7}
	TVP map = createMap1();

	//Get domain
	TVP map_rng = vdmMapRng(map);

	TVP res = vdmSetMemberOf(map_rng,newInt(4));
	EXPECT_EQ(true, res->value.boolVal);
}


TEST(Expression_Map, mapMunion)
{
	//map1: {1|->2,3|->4,6|->7}
	TVP map1 = createMap1();

	//map2: {7|->8, 9|->11}
	TVP map2 = createMap3();

	//Get domain
	TVP map = vdmMapMunion(map1,map2);

	TVP res = vdmMapApply(map,newInt(6));

	EXPECT_EQ(7, res->value.intVal);
}

TEST(Expression_Map, mapOverride)
{
	//map1: {1|->2,3|->4,6|->7}
	TVP map1 = createMap1();

	//map2: {6|->8, 9|->11}
	TVP map2 = createMap2();

	//Get domain
	TVP map = vdmMapOverride(map1,map2);

	TVP res = vdmMapApply(map,newInt(6));

	EXPECT_EQ(8, res->value.intVal);
}

TEST(Expression_Map, mapMerge)
{
	//map1: {1|->2,3|->4,6|->7}
	TVP map1 = createMap1();
	//map2: {7|->8, 9|->11}
	TVP map2 = createMap3();

	// Create a set of maps
	TVP arr[2] = {map1, map2};
	TVP set = newSetWithValues(2, arr);

	//map1: {1|->2,3|->4,6|->7}
	TVP map = createMap1();

	TVP map_res = vdmMapMerge(set);

	// Get domain
	TVP map_dom = vdmMapDom(map_res);

	TVP res = vdmSetMemberOf(map_dom,newInt(3));
	EXPECT_EQ(true, res->value.boolVal);

	res = vdmSetMemberOf(map_dom,newInt(9));
	EXPECT_EQ(true, res->value.boolVal);
}

TEST(Expression_Map, mapDomRestrictTo)
{

	TVP arr[2] = {newInt(1), newInt(6)};

	TVP set = newSetWithValues(2, arr);

	//map1: {1|->2,3|->4,6|->7}
	TVP map = createMap1();

	TVP map_res = vdmMapDomRestrictTo(set,map);

	// Get domain
	TVP map_dom = vdmMapDom(map_res);
	TVP res = vdmSetMemberOf(map_dom,newInt(3));

	EXPECT_EQ(false, res->value.boolVal);
}

TEST(Expression_Map, mapDomRestrictBy)
{

	TVP arr[2] = {newInt(1), newInt(5)};

	TVP set = newSetWithValues(2, arr);

	//map1: {1|->2,3|->4,6|->7}
	TVP map = createMap1();

	TVP map_res = vdmMapDomRestrictBy(set,map);

	//Get domain
	TVP map_dom = vdmMapDom(map_res);

	//Test if 1 is removed from map domain
	TVP res = vdmSetMemberOf(map_dom,newInt(1));

	EXPECT_EQ(false, res->value.boolVal);
}

TEST(Expression_Map, mapRngRestrictTo)
{

	TVP arr[2] = {newInt(2), newInt(4)};

	TVP set = newSetWithValues(2, arr);

	//map1: {1|->2,3|->4,6|->7}
	TVP map = createMap1();

	TVP map_res = vdmMapRngRestrictTo(set,map);

	//Get domain
	TVP map_dom = vdmMapDom(map_res);

	//Test if 6 is removed from map domain, because it maps to 7
	TVP res = vdmSetMemberOf(map_dom,newInt(6));

	EXPECT_EQ(false, res->value.boolVal);
}

TEST(Expression_Map, mapRngRestrictBy)
{

	TVP arr[2] = {newInt(5), newInt(2)};

	TVP set = newSetWithValues(2, arr);

	//map1: {1|->2,3|->4,6|->7}
	TVP map = createMap1();

	TVP map_res = vdmMapRngRestrictBy(set,map);

	//Get domain
	TVP map_dom = vdmMapDom(map_res);

	//Test if 1 is removed from map domain, because it maps to 2
	TVP res = vdmSetMemberOf(map_dom,newInt(1));

	EXPECT_EQ(false, res->value.boolVal);
}

TEST(Expression_Map, mapInverse)
{

	//map1: {1|->2,3|->4,6|->7}
	TVP map = createMap1();

	TVP map_inv = vdmMapInverse(map);

	//Get domain
	TVP map_dom = vdmMapDom(map_inv);

	//Test if 1 is removed from map domain
	TVP res = vdmSetMemberOf(map_dom,newInt(1));

	EXPECT_EQ(false, res->value.boolVal);

	//Test if 4 is added to map domain
	res = vdmSetMemberOf(map_dom,newInt(4));

	EXPECT_EQ(true, res->value.boolVal);
}

TEST(Expression_Map, mapEquals)
{
	//map1: {1|->2,3|->4,6|->7}
	TVP map1 = createMap1();
	//map2: {1|->2,3|->4,6|->7}
	TVP map2 = createMap1();
	//map3: {6|->7,9|->11}
	TVP map3 = createMap2();

	bool map_eq1 = vdmMapEquals(map1,map2);
	EXPECT_EQ(true, map_eq1);

	bool map_eq2 = vdmMapEquals(map1,map3);
	EXPECT_EQ(false, map_eq2);
}


TEST(Expression_Map, mapInEquals)
{
	//map1: {1|->2,3|->4,6|->7}
	TVP map1 = createMap1();
	//map2: {1|->2,3|->4,6|->7}
	TVP map2 = createMap1();
	//map3: {6|->7,9|->11}
	TVP map3 = createMap2();

	bool map_not_eq1 = vdmMapInEquals(map1,map2);
	EXPECT_EQ(false, map_not_eq1);

	bool map_not_eq2 = vdmMapInEquals(map1,map3);
	EXPECT_EQ(true, map_not_eq2);
}

