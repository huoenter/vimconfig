package edu.udel.patch

import scala.collection.JavaConversions._

import com.ibm.wala.classLoader.ClassLoaderFactoryImpl
import com.ibm.wala.ipa.callgraph.Entrypoint
import com.ibm.wala.ipa.cha.ClassHierarchy
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigList

import edu.udel.patch.Dependency
import edu.udel.patch.AnalysisScope
import edu.udel.patch.DependencyNature

class AnalysisOptions(val scope: AnalysisScope, entrypoints: java.lang.Iterable[Entrypoint], val cha: ClassHierarchy, val isSourceAnalysis: Boolean)
		extends com.ibm.wala.ipa.callgraph.AnalysisOptions(scope, entrypoints) {
}

object AnalysisOptions {

	def apply()(implicit config: Config = ConfigFactory.load): AnalysisOptions = {

		val binDep = if (config.hasPath("wala.dependencies.binary"))
			config.getList("wala.dependencies.binary") map { d => Dependency(d.unwrapped.asInstanceOf[String]) }
		else
			List()

		val srcDep = if (config.hasPath("wala.dependencies.source"))
			config.getList("wala.dependencies.source") map { d => Dependency(d.unwrapped.asInstanceOf[String], DependencyNature.SourceDirectory) }
		else
			List()

		val jarDep = if (config.hasPath("wala.dependencies.jar"))
			config.getList("wala.dependencies.jar") map { d => Dependency(d.unwrapped.asInstanceOf[String], DependencyNature.Jar) }
		else
			List()

		val dependencies = binDep ++ srcDep ++ jarDep

		val jreLibPath = if (config.hasPath("wala.jre-lib-path"))
			config.getString("wala.jre-lib-path")
		else
			System.getenv().get("JAVA_HOME") + "/jre/lib/rt.jar"

		implicit val scope = new AnalysisScope(jreLibPath, config.getString("wala.exclussions"), dependencies)

		val classLoaderImpl = new ClassLoaderFactoryImpl(scope.getExclusions())
		implicit val cha = ClassHierarchy.make(scope, classLoaderImpl)

		new AnalysisOptions(scope, null, cha, !srcDep.isEmpty)
	}

	//  def apply(klass: String, method: String)(implicit config: Config): AnalysisOptions = apply((klass, method), Seq())

	//  def apply(entrypoint: (String, String),
	//    dependencies: Iterable[Dependency])(implicit config: Config): AnalysisOptions = apply(Seq(entrypoint), dependencies)

	//  val mainMethod = "main([Ljava/lang/String;)V"

//	private def makeEntrypoint(entryClass: String, entryMethod: String)(implicit scope: AnalysisScope, cha: ClassHierarchy): Entrypoint = null

}