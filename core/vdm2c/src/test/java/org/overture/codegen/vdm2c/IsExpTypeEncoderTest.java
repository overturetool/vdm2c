package org.overture.codegen.vdm2c;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.types.ASet1SetType;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.vdm2c.transformations.IsExpTypeEncoder;

public class IsExpTypeEncoderTest {

    @Test
    public void integer() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new AIntNumericBasicTypeIR());
        String expected = "{'1','i'}";

        check(actual, expected);
    }

    @Test
    public void nat() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ANatNumericBasicTypeIR());
        String expected = "{'1','j'}";

        check(actual, expected);
    }

    @Test
    public void real() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ARealNumericBasicTypeIR());
        String expected = "{'1','d'}";

        check(actual, expected);
    }

    @Test
    public void bool() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ABoolBasicTypeIR());
        String expected = "{'1','b'}";

        check(actual, expected);
    }

    @Test
    public void character() throws  AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ACharBasicTypeIR());
        String expected = "{'1','c'}";

        check(actual, expected);
    }

    @Test
    public void nat1() throws  AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ANat1NumericBasicTypeIR());
        String expected = "{'1','k'}";

        check(actual, expected);
    }

    @Test
    public void rat() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ARatNumericBasicTypeIR());
        String expected = "{'1','e'}";

        check(actual, expected);
    }

    @Test
    public void token() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ATokenBasicTypeIR());
        String expected = "{'1','t'}";

        check(actual, expected);
    }

    @Test
    public void seqOf() throws AnalysisException {

        ASeqSeqTypeIR seqType = new ASeqSeqTypeIR();
        seqType.setSeqOf(new AIntNumericBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(seqType);
        String expected = "{'1','Q','1','i'}";

        check(actual, expected);
    }

    @Test
    public void seq1Of() throws AnalysisException {

        ASeqSeqTypeIR seqType = new ASeqSeqTypeIR();
        seqType.setSeq1(true);
        seqType.setSeqOf(new ACharBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(seqType);
        String expected = "{'1','Z','1','c'}";

        check(actual, expected);
    }

    @Test
    public void setOf() throws AnalysisException {

        ASetSetTypeIR setType = new ASetSetTypeIR();
        setType.setSetOf(new ABoolBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(setType);
        String expected = "{'1','T','1','b'}";

        check(actual, expected);
    }

    @Test
    public void set1Of() throws AnalysisException {

        ASetSetTypeIR setType = new ASetSetTypeIR();
        setType.setSourceNode(new SourceNode(new ASet1SetType()));
        setType.setSetOf(new ANatNumericBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(setType);
        String expected = "{'1','Y','1','j'}";

        check(actual, expected);
    }

    @Test
    public void product() throws AnalysisException {

        ATupleTypeIR tup = new ATupleTypeIR();
        tup.getTypes().add(new ANat1NumericBasicTypeIR());
        tup.getTypes().add(new ARatNumericBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(tup);
        String expected = "{'1','P',2,'*','1','k','*','1','e','*'}";

        check(actual, expected);
    }

    @Test
    public void record() throws AnalysisException {

        ATypeNameIR name = new ATypeNameIR();
        name.setDefiningClass("A");
        name.setName("Rec");

        ARecordTypeIR recType = new ARecordTypeIR();
        recType.setName(name);

        String actual = IsExpTypeEncoder.encodeType(recType);
        String expected = "{'1','R',CLASS_ID_Rec_ID}";

        check(actual, expected);
    }

    @Test
    public void map() throws AnalysisException {

        AMapMapTypeIR mapType = new AMapMapTypeIR();
        mapType.setFrom(new AIntNumericBasicTypeIR());
        mapType.setTo(new ACharBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(mapType);
        String expected = "{'1','M','*','1','i','*','1','c','*'}";

        check(actual, expected);
    }

    @Test
    public void clazz() throws AnalysisException {

        AClassTypeIR classType = new AClassTypeIR();
        classType.setName("A");

        String actual = IsExpTypeEncoder.encodeType(classType);
        String expected = "{'1','W',CLASS_ID_A_ID}";

        check(actual, expected);
    }

    @Test
    public void seqOfOptionalInt() throws AnalysisException {
        // seq of [int]
        ASeqSeqTypeIR seq = new ASeqSeqTypeIR();
        AIntNumericBasicTypeIR elemType = new AIntNumericBasicTypeIR();
        elemType.setOptional(true);
        seq.setSeqOf(elemType);

        String actual = IsExpTypeEncoder.encodeType(seq);
        String expected = "{'1','Q','0','i'}";

        check(actual, expected);
    }

    @Test
    public void optionalSeqOfInt() throws AnalysisException {

        // [seq of int]
        ASeqSeqTypeIR seq = new ASeqSeqTypeIR();
        seq.setOptional(true);
        AIntNumericBasicTypeIR elemType = new AIntNumericBasicTypeIR();
        seq.setSeqOf(elemType);

        String actual = IsExpTypeEncoder.encodeType(seq);
        String expected = "{'0','Q','1','i'}";

        check(actual, expected);
    }

    @Test
    public void seqOfSeqOfInt() throws AnalysisException {

        // seq of seq of int
        AIntNumericBasicTypeIR intType = new AIntNumericBasicTypeIR();
        ASeqSeqTypeIR innerSeq = new ASeqSeqTypeIR();
        innerSeq.setSeqOf(intType);
        ASeqSeqTypeIR outerSeq = new ASeqSeqTypeIR();
        outerSeq.setSeqOf(innerSeq);

        String actual = IsExpTypeEncoder.encodeType(outerSeq);
        String expected = "{'1','Q','1','Q','1','i'}";

        check(actual, expected);
    }

    @Test
    public void seqOfSetOfChar() throws AnalysisException {

        // seq of set of char
        ACharBasicTypeIR charType = new ACharBasicTypeIR();
        ASetSetTypeIR set = new ASetSetTypeIR();
        set.setSetOf(charType);
        ASeqSeqTypeIR seq = new ASeqSeqTypeIR();
        seq.setSeqOf(set);

        String actual = IsExpTypeEncoder.encodeType(seq);
        String expected = "{'1','Q','1','T','1','c'}";

        check(actual, expected);
    }

    @Test
    public void complexProductFirst() throws AnalysisException
    {
        // int * (int * [char])
        AIntNumericBasicTypeIR first = new AIntNumericBasicTypeIR();
        ACharBasicTypeIR second = new ACharBasicTypeIR();
        second.setOptional(true);

        ATupleTypeIR innerTuple = new ATupleTypeIR();
        innerTuple.getTypes().add(first);
        innerTuple.getTypes().add(second);

        ATupleTypeIR outerTuple = new ATupleTypeIR();
        outerTuple.getTypes().add(first.clone());
        outerTuple.getTypes().add(innerTuple);

        String actual = IsExpTypeEncoder.encodeType(outerTuple);
        String expected = "{'1','P',2,'*','1','i','*','1','P',2,'*','1','i','*','0','c','*','*'}";

        check(actual, expected);
    }

    @Test
    public void complexProductSecond() throws AnalysisException {

        // (int * char) * int
        ATupleTypeIR innerTuple = new ATupleTypeIR();
        innerTuple.getTypes().add(new AIntNumericBasicTypeIR());
        innerTuple.getTypes().add(new ACharBasicTypeIR());

        ATupleTypeIR outerTuple = new ATupleTypeIR();
        outerTuple.getTypes().add(innerTuple);
        outerTuple.getTypes().add(new AIntNumericBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(outerTuple);
        String expected = "{'1','P',2,'*','1','P',2,'*','1','i','*','1','c','*','*','1','i','*'}";

        check(actual, expected);
    }

    @Test
    public void mapIntToSeqOfChar() throws AnalysisException {

        // map int to seq of char
        ASeqSeqTypeIR seq = new ASeqSeqTypeIR();
        seq.setSeqOf(new ACharBasicTypeIR());
        AMapMapTypeIR map = new AMapMapTypeIR();
        map.setFrom(new AIntNumericBasicTypeIR());
        map.setTo(seq);

        String actual = IsExpTypeEncoder.encodeType(map);
        String expected = "{'1','M','*','1','i','*','1','Q','1','c','*'}";

        check(actual, expected);
    }

    private void check(String actual, String expected) {
        Assert.assertEquals("Got unexpected C encoding", expected, actual);
    }
}
