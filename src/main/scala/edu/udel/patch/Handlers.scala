package edu.udel.patch

import scala.collection.JavaConversions.asScalaBuffer
import com.sun.jdi.event.AccessWatchpointEvent
import com.sun.jdi.event.ClassPrepareEvent
import com.sun.jdi.event.Event
import com.sun.jdi.event.MethodEntryEvent
import com.sun.jdi.event.MethodExitEvent
import com.sun.jdi.event.ModificationWatchpointEvent
import edu.udel.patch.trace.FieldAccess
import edu.udel.patch.trace.FieldModification
import edu.udel.patch.trace.MethodExit
import edu.udel.patch.trace.MethodInvocation
import edu.udel.patch.trace.Observation
import edu.udel.patch.trace.OtherEvents
import scala.collection.JavaConversions._
import edu.udel.patch.util.Types
import java.io.FileWriter

object Logger {
    val fw = new FileWriter("/home/huoc/Projects/scala/patch/data/trace", true)
    def log(s: String) = {
       	fw.write(s+"\n") 
    }
}

case class EventHandler(f: Function1[Event, Unit])

object EventHandler {
	val fieldPrepHandler = (event: Event) => {
	    val e = event.asInstanceOf[ClassPrepareEvent]
//	    println("Install wp for: " + e.referenceType.name) 
	    e.referenceType.fields foreach Installer.install
//	    e.referenceType.methods foreach Installer.install
	    
//	    OtherEvents
	}
	
	val testPrepHandler = (event: Event) => {
	    val e = event.asInstanceOf[ClassPrepareEvent]
	    println(e.referenceType.name)
	    Types.testTypes ::= e.referenceType()
//	    OtherEvents
	}
	
	val fieldAccessHandler = (event: Event) => {
	    val e = event.asInstanceOf[AccessWatchpointEvent]
	    
	    val f = e.field
	    Logger.log("FA:"+f.toString)
//	    new FieldAccess(e)
	}
	
	val fieldModificationHandler = (event: Event) => {
	    val e = event.asInstanceOf[ModificationWatchpointEvent]
	    val f = e.field
	    Logger.log("FM:"+f.toString)
//		new FieldModification(e)    
	}

	val methodEntryHandler = (event: Event) => {
	    val e = event.asInstanceOf[MethodEntryEvent]
        Logger.log("ME:"+e.method)
//		new MethodInvocation(e)    
	}

	val methodExitHandler = (event: Event) => {
	    val e = event.asInstanceOf[MethodExitEvent]
        Logger.log("MX:"+e.method)
//	    new MethodExit(e)
	}

	val assertEntryHandler = (event: Event) => {
	    val e = event.asInstanceOf[MethodEntryEvent]
	    if (e.method.name startsWith "assert")
	        Logger.log("AE:"+e.method)
	}

	val assertExitHandler = (event: Event) => {
	    val e = event.asInstanceOf[MethodExitEvent]
	    if (e.method.name startsWith "assert")
	        Logger.log("AX:"+e.method)
//			new MethodExit(e)    
//	    else
//	        OtherEvents
	}
}
