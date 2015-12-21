package edu.udel.patch.utils

import com.ibm.wala.ipa.cha.ClassHierarchy
import com.ibm.wala.types.ClassLoaderReference
import com.ibm.wala.types.TypeName
import com.ibm.wala.types.TypeReference
import com.ibm.wala.classLoader.IMethod
import com.ibm.wala.types.MethodReference
import com.ibm.wala.ssa.SSAInstruction
import com.ibm.wala.ssa.SSACFG
import com.ibm.wala.util.graph.dominators.Dominators
import com.ibm.wala.ssa.ISSABasicBlock

object ImplicitConversions {

	implicit def i2ref(m: IMethod) = m.getReference()
	implicit def ref2i(m: MethodReference)(implicit cha: ClassHierarchy) = cha.resolveMethod(m)

	implicit class TupleListWrapper[A, B](list: List[(A, Set[B])]) {
		def list2multimap = list.groupBy(e => e._1).mapValues(e => (Set.empty[B] /: (e map (_._2)))(_ union _)).toMap
	}

	implicit def toIClass(t: TypeName)(implicit cha: ClassHierarchy) = {
		val p = cha lookupClass TypeReference.findOrCreate(ClassLoaderReference.Primordial, t)
		val a = cha lookupClass TypeReference.findOrCreate(ClassLoaderReference.Application, t)
		if (a == null) p else a
	}

	implicit class MapWrapper[A, B <: Iterable[_]](m: collection.Map[A, B]) {
		//	implicit class MapWrapper(m: collection.mutable.Map[IMethod, Set[Int]]) {
		def cSize = (0 /: m) { case (acc, (k, v)) => acc + v.size }
	}

	implicit def insToBbl(ins : SSAInstruction)(implicit insns : Array[SSAInstruction], cfg : SSACFG) : ISSABasicBlock =
		cfg getBlockForInstruction insns.indexOf(ins)

	implicit class InstructionDomination(node: SSAInstruction)(implicit dom: Dominators[ISSABasicBlock], insns: Array[SSAInstruction], cfg: SSACFG) {
		def dominatedBy(master: SSAInstruction) = dom isDominatedBy (node, master)
		def dominates(child: SSAInstruction) = dom isDominatedBy (child, node)
	}

	implicit class SubClassWrapperForMethods(m1: MethodReference) {
		def isSubOf(m2: MethodReference)(implicit cha: ClassHierarchy) =
			m1.getDeclaringClass.getName isSubOf m2.getDeclaringClass.getName
	}
	implicit class SubClassWrapper(c1: TypeName) {
		def isSubOf(c2: TypeName)(implicit cha: ClassHierarchy) =
			if (c1 == null || c2 == null ||
				c1.isPrimitiveType() || c2.isPrimitiveType()) false
			else
				try {
					cha.isSubclassOf(c1, c2)
				} catch {
					case e: IllegalArgumentException => false
					//					case e : NullPointerException => println(c1, c2); false
				}
	}

}