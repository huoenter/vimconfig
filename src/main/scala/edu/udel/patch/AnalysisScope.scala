package edu.udel.patch

import java.io.ByteArrayInputStream
import java.io.File
import java.util.Collections
import java.util.jar.JarFile

import scala.Array.canBuildFrom
import scala.collection.Iterable
import scala.collection.Seq

import com.ibm.wala.classLoader.BinaryDirectoryTreeModule
import com.ibm.wala.classLoader.JarFileModule
import com.ibm.wala.classLoader.Language
import com.ibm.wala.classLoader.Module
import com.ibm.wala.types.ClassLoaderReference
import com.ibm.wala.util.config.FileOfClasses
import com.ibm.wala.util.io.FileProvider
import com.ibm.wala.util.strings.Atom

import AnalysisScope.Application
import AnalysisScope.Extension
import AnalysisScope.Scope
//import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope

object AnalysisScope {
  type Scope = Atom
  val Primordial = com.ibm.wala.ipa.callgraph.AnalysisScope.PRIMORDIAL
  val Extension = com.ibm.wala.ipa.callgraph.AnalysisScope.EXTENSION
  val Application = com.ibm.wala.ipa.callgraph.AnalysisScope.APPLICATION
  val Synthetic = com.ibm.wala.ipa.callgraph.AnalysisScope.SYNTHETIC
//  val Source = JavaSourceAnalysisScope.SOURCE.getName()
  def apply(jreLibPath: String, exclusions: String) = new AnalysisScope(jreLibPath, exclusions)
  
  val allScopes = List(Application, 
//      Source, 
      Synthetic, Extension, Primordial)
}

object DependencyNature extends Enumeration {
  type DependencyNature = Value
  val Binary, BinaryDirectory, Jar, JarDirectory, SourceDirectory = Value
}

object Dependency {
  def apply(file: String): Dependency = apply(file, DependencyNature.BinaryDirectory, Application)
  def apply(file: String, nature: DependencyNature.DependencyNature): Dependency = apply(file, nature, Extension)
}

case class Dependency(file: String, nature: DependencyNature.DependencyNature, scope: Scope)

class AnalysisScope(jreLibPath: String, exclusions: String, dependencies: Iterable[Dependency]) extends com.ibm.wala.ipa.callgraph.AnalysisScope(Collections.singleton(Language.JAVA)) {
  val UNDER_ECLIPSE = false;
  import AnalysisScope._
  import DependencyNature._

  def this(jreLibPath: String, exclusions: String) = this(jreLibPath, exclusions, Seq())

  initForJava()

  addToScope(getLoader(Primordial), new JarFile(jreLibPath))

  setExclusions(new FileOfClasses(new ByteArrayInputStream(exclusions.getBytes("UTF-8"))))

  addDependencies(dependencies)

  def addDependencies(dependencies: Iterable[Dependency]) {
    for (d <- dependencies) d match {
      case Dependency(file, BinaryDirectory, scope: Scope) => addBinaryDependency(file, scope)
      case Dependency(file, JarDirectory, scope: Scope) => addJarDirectoryDependency(file, scope)
      case Dependency(file, Jar, scope: Scope) => addJarDependency(file, scope)
      case Dependency(file, Binary, scope: Scope) => throw new Exception("Unimplemented yet")
//      case Dependency(file, SourceDirectory, scope: Scope) => addSourceDependency(file, scope)
    }
  }

  def getFile(path: String) =
    if (UNDER_ECLIPSE)
      new FileProvider().getFile(path, getLoader())
    else
      new File(path)

  def addBinaryDependency(directory: String, analysisScope: Atom = Application) {
    //    debug("Binary: " + directory);
    val sd = getFile(directory);
    assert(sd.exists(), "dependency \"" + directory + "\" not found")
    assert(sd.isDirectory(), "dependency \"" + directory + "\" not a directory")
    addToScope(getLoader(analysisScope), new BinaryDirectoryTreeModule(sd));
  }

  // stuff for source frontend below


//  def addSourceDependency(directory: String, analysisScope: Atom = Application) {
//    loadersByName.put(JavaSourceAnalysisScope.SOURCE.getName(), JavaSourceAnalysisScope.SOURCE);
//    setLoaderImpl(JavaSourceAnalysisScope.SOURCE, "com.ibm.wala.cast.java.translator.polyglot.PolyglotSourceLoaderImpl");
//    initSynthetic(JavaSourceAnalysisScope.SOURCE)
//    //    debug("Binary: " + directory);
//    val sd = getFile(directory);
//    assert(sd.exists(), "dependency \"" + directory + "\" not found")
//    assert(sd.isDirectory(), "dependency \"" + directory + "\" not a directory")
//    addToScope(getLoader(analysisScope), new SourceDirectoryTreeModule(sd));
//  }

  override def addToScope(loader: ClassLoaderReference, m: Module) {
//    if (m.isInstanceOf[SourceDirectoryTreeModule] && loader.equals(ClassLoaderReference.Application)) {
//      super.addToScope(JavaSourceAnalysisScope.SOURCE, m);
//    } else {
      super.addToScope(loader, m);
//    }
  }

  // stuff for source front end above

  def getLoader() = AnalysisScope.this.getClass().getClassLoader();

  def addJarDirectoryDependency(path: String, scope: Scope = Extension) {
    //    debug("Jar folder: " + path);
    val dir = getFile(path);
    val delim = if (path.endsWith("/")) "" else "/"

    val files = dir.list();
    if (files == null) return

    for (fileName <- files) yield {
      if (fileName.endsWith(".jar"))
        addJarDependency(path + delim + fileName, scope)
      else {
        val file = new File(fileName)
        if (file.isDirectory())
          addJarDirectoryDependency(file.getAbsolutePath(), scope)
      }
    }
  }

  def addJarDependency(file: String, scope: Scope = Extension) {
    //    debug("Jar: " + file);
    val M = if (UNDER_ECLIPSE)
      new FileProvider().getJarFileModule(file, getLoader());
    else
      new JarFileModule(new JarFile(file, true));

    addToScope(getLoader(scope), M);
  }

}