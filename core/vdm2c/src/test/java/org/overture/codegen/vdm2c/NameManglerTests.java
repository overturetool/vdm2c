package org.overture.codegen.vdm2c;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.types.ASet1SetType;
import org.overture.ast.types.ASetSetType;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMapMapTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AQuoteTypeIR;
import org.overture.codegen.ir.types.ARatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;
import org.overture.codegen.ir.types.ATemplateTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.vdm2c.utils.NameMangler;
import org.overture.codegen.vdm2c.utils.NameMangler.NameGenerator;

public class NameManglerTests
{
	private NameGenerator n = new NameGenerator();
	
	@Test
	public void map() throws AnalysisException 
	{
		// map bool to rat
		AMapMapTypeIR mapType = new AMapMapTypeIR();
		mapType.setFrom(new ABoolBasicTypeIR());
		mapType.setTo(new ARatNumericBasicTypeIR());
		
		Assert.assertEquals("2MBJ", mapType.apply(n));
	}
	
	@Test
	public void set1() throws AnalysisException
	{
		// set1 of char
		ASetSetTypeIR set1Type = new ASetSetTypeIR();
		set1Type.setSourceNode(new SourceNode(new ASet1SetType()));
		set1Type.setSetOf(new ACharBasicTypeIR());
		
		Assert.assertEquals("1GC", set1Type.apply(n));
	}
	
	@Test
	public void set() throws AnalysisException
	{
		// set of char
		ASetSetTypeIR setType = new ASetSetTypeIR();
		setType.setSourceNode(new SourceNode(new ASetSetType()));
		setType.setSetOf(new ACharBasicTypeIR());
		
		Assert.assertEquals("1SC", setType.apply(n));
	}
	
	@Test
	public void seq1() throws AnalysisException
	{
		// seq1 of nat
		ASeqSeqTypeIR seq1Type = new ASeqSeqTypeIR();
		seq1Type.setSeq1(true);
		seq1Type.setSeqOf(new ANatNumericBasicTypeIR());
		
		Assert.assertEquals("1HN", seq1Type.apply(n));
	}
	
	@Test
	public void seq() throws AnalysisException
	{
		// seq of nat
		ASeqSeqTypeIR seqType = new ASeqSeqTypeIR();
		seqType.setSeq1(false);
		seqType.setSeqOf(new ANatNumericBasicTypeIR());
		
		Assert.assertEquals("1QN", seqType.apply(n));
	}
	
	@Test
	public void methodName() throws AnalysisException
	{
		// The name of a function or operation
		AMethodDeclIR method = new AMethodDeclIR();
		method.setName("op");
		
		Assert.assertEquals("_Z2opEV", NameMangler.mangle(method));
	}
	
	@Test
	public void classType() throws AnalysisException
	{
		// class 'A'
		AClassTypeIR classType = new AClassTypeIR();
		classType.setName("A");
		
		Assert.assertEquals("1CA", classType.apply(n));
	}
	
	@Test
	public void template() throws AnalysisException
	{
		ATemplateTypeIR template = new ATemplateTypeIR();
		template.setName("U");
		
		Assert.assertEquals("1TU", template.apply(n));
	}

	@Test
	public void namedType() throws AnalysisException
	{
		// Type 'T' in class 'A'
		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setDefiningClass("A");
		typeName.setName("T");
		
		ANamedTypeDeclIR name = new ANamedTypeDeclIR();
		name.setName(typeName);
		
		Assert.assertEquals("3WA_T", name.apply(n));
	}
	
	@Test
	public void unionOfNatChar() throws AnalysisException
	{
		// nat | char
		AUnionTypeIR unionType = new AUnionTypeIR();
		unionType.getTypes().add(new ANatNumericBasicTypeIR());
		unionType.getTypes().add(new ACharBasicTypeIR());
		
		Assert.assertEquals("2XNC", unionType.apply(n));
	}
	
	@Test
	public void quote() throws AnalysisException
	{
		// <Green>
		AQuoteTypeIR quote = new AQuoteTypeIR();
		quote.setValue("Green");
		
		Assert.assertEquals("5YGreen", quote.apply(n));
	}
	
	
	@Test
	public void ratType() throws AnalysisException
	{
		ARatNumericBasicTypeIR rat = new ARatNumericBasicTypeIR();
		
		Assert.assertEquals("J", rat.apply(n));
	}
	
	@Test
	public void real() throws AnalysisException
	{
		ARealNumericBasicTypeIR real = new ARealNumericBasicTypeIR();
		
		Assert.assertEquals("R", real.apply(n));
	}
	
	@Test
	public void integer() throws AnalysisException
	{
		AIntNumericBasicTypeIR integer = new AIntNumericBasicTypeIR();
		
		Assert.assertEquals("I", integer.apply(n));
	}
	
	@Test
	public void character() throws AnalysisException
	{
		ACharBasicTypeIR character = new ACharBasicTypeIR();
		
		Assert.assertEquals("C", character.apply(n));
	}
	
	@Test
	public void bool() throws AnalysisException
	{
		ABoolBasicTypeIR bool = new ABoolBasicTypeIR();
		
		Assert.assertEquals("B", bool.apply(n));
	}
	
	@Test
	public void nat1() throws AnalysisException
	{
		ANat1NumericBasicTypeIR nat1 = new ANat1NumericBasicTypeIR();
		
		Assert.assertEquals("K", nat1.apply(n));
	}
	
	@Test
	public void nat() throws AnalysisException
	{
		ANatNumericBasicTypeIR nat = new ANatNumericBasicTypeIR();
		
		Assert.assertEquals("N", nat.apply(n));
	}
	
	@Test
	public void voidType() throws AnalysisException
	{
		AVoidTypeIR voidType = new AVoidTypeIR();
		
		Assert.assertEquals("V", voidType.apply(n));
	}

	@Test
	public void unknownType() throws AnalysisException
	{
		AUnknownTypeIR unknownType = new AUnknownTypeIR();
		
		Assert.assertEquals("U", unknownType.apply(n));
	}
}
