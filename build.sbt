// set the name of the project
name := "patch"

version := "0.1"

organization := "University of Delaware"

scalaVersion := "2.11.6"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

unmanagedJars in Compile ~= {uj => 
    Seq(Attributed.blank(file(System.getProperty("java.home").dropRight(3)+"lib/tools.jar"))) ++ uj
	}

libraryDependencies ++= Seq("com.typesafe" % "config" % "0.5.+")
