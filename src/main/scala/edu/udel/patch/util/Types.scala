package edu.udel.patch.utils

import com.ibm.wala.classLoader.IBytecodeMethod
import com.ibm.wala.classLoader.IClass
import com.ibm.wala.classLoader.IMethod
import com.ibm.wala.classLoader.Language
import com.ibm.wala.ipa.cha.ClassHierarchy
import com.ibm.wala.ssa.ISSABasicBlock
import com.ibm.wala.ssa.SSAInstruction
import com.ibm.wala.ssa.SSAInvokeInstruction
import com.ibm.wala.types.ClassLoaderReference
import com.ibm.wala.types.Descriptor
import com.ibm.wala.types.MethodReference
import com.ibm.wala.types.TypeReference
import com.ibm.wala.util.strings.Atom

object Types {
	type BB = ISSABasicBlock
	type I = SSAInstruction
	type InvokeI = SSAInvokeInstruction
	
	type M = IMethod
	type MRef = MethodReference
	type C = IClass
	type CHA = ClassHierarchy

	val appCL = ClassLoaderReference.Application

	val JUnitTestRef = TypeReference.findOrCreate(appCL, "Ljunit/framework/TestCase")
	val JUnitTest = JUnitTestRef.getName

	val dataStructure = TypeReference.findOrCreate(appCL, "LdataStructures/intStackMaxArray")
	val dataStructureTest = TypeReference.findOrCreate(appCL, "LdataStructures/Teststack4")
	val identifierTest = TypeReference.findOrCreate(appCL, "LdataStructures/TestIdentifier")
	val identifierTable = TypeReference.findOrCreate(appCL, "LdataStructures/IdentifierTable")

	val JUnitRunWith = TypeReference.findOrCreate(appCL, "Lorg/junit/runner/RunWith")
	val JUnitTestMethod = TypeReference.findOrCreate(appCL, "Lorg/junit/Test")

	def ccName2TypeName(s: String) = TypeReference.findOrCreate(appCL, "L" + s).getName
	
	val JunitAssert = ccName2TypeName("Lorg/junit/Assert")

	def filteringMethods(ms: Seq[IMethod]) = ms collect {
		case m: IBytecodeMethod => m
	} filterNot { _.isStatic } filter { _.isPublic } filterNot { m => m.isClinit || m.isInit }

	def trace2IMethod(cls: String, m: String, des: String, cha: ClassHierarchy) = {
		val klass = TypeReference.findOrCreate(appCL, cls replace (".", "/"))
		val iklass = cha.lookupClass(klass)
		val atom = Atom.findOrCreateUnicodeAtom(m)
		val desc = Descriptor.findOrCreateUTF8(Language.JAVA, des)
		val ref = MethodReference.findOrCreate(klass, atom, desc)
		
		ref
	}

	//			val newOptions = new AnalysisOptions(opts.scope, entryPoints, cha, false, null, null)
	//
	//		val pushAtom = Atom.findOrCreateUnicodeAtom("push");
	//
	//		val pushDesc = Descriptor.findOrCreateUTF8(Language.JAVA, "(I)V");
	//
	//		val pushref = MethodReference.findOrCreate(Types.dataStructure, pushAtom, pushDesc);
	//
	//		val testAtom = Atom.findOrCreateUnicodeAtom("testEmptyPush");
	//
	//		val testDesc = Descriptor.findOrCreateUTF8(Language.JAVA, "()V");
	//
	//		val testref = MethodReference.findOrCreate(Types.dataStructureTest, testAtom, testDesc);
	//
	//		val testmAtom = Atom.findOrCreateUnicodeAtom("testGetReservedWord");
	//
	//		val testmDesc = Descriptor.findOrCreateUTF8(Language.JAVA, "()V");
	//
	//		val testmref = MethodReference.findOrCreate(Types.identifierTest, testmAtom, testmDesc);
	//
	//		val mrAtom = Atom.findOrCreateUnicodeAtom("makeReserved");
	//
	//		val mrDesc = Descriptor.findOrCreateUTF8(Language.JAVA, "(Ljava/lang/String;)LdataStructures/Identifier;");
	//
	//		val mrref = MethodReference.findOrCreate(Types.identifierTable, mrAtom, mrDesc);
	//		//				println(mref, cg.getNodes(mref))
	//		//		
	//		println(opts.cha.resolveMethod(pushref))
	//		println(opts.cha.resolveMethod(testref))
	//		println(opts.cha.resolveMethod(mrref))
	//		println(opts.cha.resolveMethod(testmref))
}