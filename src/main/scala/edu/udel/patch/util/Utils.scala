package edu.udel.patch.util

import java.io.File

import scala.collection.JavaConversions.iterableAsScalaIterable

import com.ibm.wala.classLoader.IBytecodeMethod
import com.ibm.wala.classLoader.IClass
import com.ibm.wala.classLoader.IMethod
import com.ibm.wala.ipa.cha.ClassHierarchy
import com.ibm.wala.types.ClassLoaderReference
import com.ibm.wala.types.MethodReference
import com.sun.jdi.ReferenceType
import com.sun.jdi.VirtualMachine
import com.sun.jdi.request.ClassPrepareRequest
import com.sun.jdi.request.EventRequest
import com.sun.jdi.request.MethodEntryRequest
import com.sun.jdi.request.MethodExitRequest
import com.sun.jdi.request.WatchpointRequest
import com.typesafe.config.ConfigFactory

import edu.udel.patch.AnalysisOptions
import edu.udel.patch.utils.ImplicitConversions.SubClassWrapper
import edu.udel.patch.utils.WTypes.JUnitRunWith
import edu.udel.patch.utils.WTypes.JUnitTest
import edu.udel.patch.utils.WTypes.JUnitTestMethod

object Types {
    type VM = VirtualMachine
    val exclusives = List("java.*", "sun.*", "com.sun.*", "org.junit.*", "junit.*")
    def addExclusions(eq: EventRequest) = eq match {
        case wp: WatchpointRequest   => exclusives foreach wp.addClassExclusionFilter
        case cp: ClassPrepareRequest => exclusives foreach cp.addClassExclusionFilter
        case me: MethodEntryRequest  => exclusives foreach me.addClassExclusionFilter
        case mx: MethodExitRequest   => exclusives foreach mx.addClassExclusionFilter
        case _                       =>
    }
    
    var testTypes = List[ReferenceType]()
}

object Utils {

	def isSubOfJUnitOrRunWithJUnit(c: IClass)(implicit cha: ClassHierarchy) =
		(c.getName isSubOf JUnitTest) || ((c.getAnnotations map (_.getType)).toList contains JUnitRunWith)

	def getTestMethods(subject: String)(implicit cha: ClassHierarchy): Seq[MethodReference] = {
		val classes = Utils.getIClasses(cha).toList

		val fromMethods = classes flatMap { _.getDeclaredMethods } collect {
			case m: IBytecodeMethod => m
		} filter {
			m => (m.getAnnotations() map (_.getType)).toList contains JUnitTestMethod
		} map { m => m.getReference }

		val (junits, nonjunits) = classes partition { _.getName isSubOf JUnitTest }
		val (runWithRoots, nonRootsNonJUnits) = nonjunits partition { c => (c.getAnnotations map (_.getType)).toList contains JUnitRunWith }
		val subsOfRoots = nonRootsNonJUnits filter { c => runWithRoots exists { r => c.getName isSubOf r.getName } }

		val testClasses = junits ++ runWithRoots ++ subsOfRoots

		val subjectTestClasses = testClasses //filter { cls => cls.getName.toString.toLowerCase contains hint(subject)}

		def filteringMethods(ms: Seq[IMethod]) = ms collect {
			case m: IBytecodeMethod => m
		} filterNot { _.isStatic } filter { _.isPublic } filterNot { m => m.isClinit || m.isInit }

		val fromExplicitClasses = subjectTestClasses flatMap { c =>
			if (c.isAbstract) {
				for (
					sc <- cha.computeSubClasses(c.getReference()) filterNot { _.isAbstract };
					m <- filteringMethods(c.getDeclaredMethods.toList)
				) yield {
					MethodReference.findOrCreate(sc.getReference, m.getName.toString, m.getDescriptor.toString)
				}

			} else {
				filteringMethods(c.getDeclaredMethods.toList) filterNot {
					_.getAnnotations() contains JUnitTestMethod //remove duplicates against 'fromMethods'
				} map { _.getReference }
			}
		} filter {
			_.getDescriptor.toString equals "()V"
		} filter {
			_.getName.toString startsWith "test"
		}

		(fromMethods ++ fromExplicitClasses).toSet.toList
	}

	def getOptions(configName: String) = {

		val file = new File("" + "/configs/" + configName)
		val confFile = ConfigFactory.parseFile(file)

		implicit val conf = ConfigFactory.load(confFile)
		AnalysisOptions()
	}

	def iS(cref: ClassLoaderReference): IClass => Boolean = {
		x: IClass => x.getClassLoader.getReference equals cref
	}

	def getIByteMethods(cha: ClassHierarchy) = {
		val appKlasses = cha filter iS(ClassLoaderReference.Application)
		appKlasses flatMap { _.getDeclaredMethods } collect { case m: IBytecodeMethod => m }
	}

	def getIClasses(cha: ClassHierarchy) = cha filter iS(ClassLoaderReference.Application)
}