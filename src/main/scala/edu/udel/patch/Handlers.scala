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

case class EventHandler(f: Function1[Event, Observation])

object EventHandler {
	val clsPrepHandler = (event: Event) => {
	    val e = event.asInstanceOf[ClassPrepareEvent]
	    println(e.referenceType.name)
	    e.referenceType.fields foreach Installer.install
//	    e.referenceType.methods foreach Installer.install
	    
	    new OtherEvents(e)
	}
	
	val fieldAccessHandler = (event: Event) => {
	    val e = event.asInstanceOf[AccessWatchpointEvent]
	    new FieldAccess(e)
	}
	
	val fieldModificationHandler = (event: Event) => {
	    val e = event.asInstanceOf[ModificationWatchpointEvent]
		new FieldModification(e)    
	}

	val methodEntryHandler = (event: Event) => {
	    val e = event.asInstanceOf[MethodEntryEvent]
		new MethodInvocation(e)    
	}

	val methodExitHandler = (event: Event) => {
	    val e = event.asInstanceOf[MethodExitEvent]
	    new MethodExit(e)
	}

	val assertEntryHandler = (event: Event) => {
	    val e = event.asInstanceOf[MethodEntryEvent]
	    if (e.method.name startsWith "assert")
			new MethodInvocation(e)    
	    else
	        new OtherEvents(e)
	}

	val assertExitHandler = (event: Event) => {
	    val e = event.asInstanceOf[MethodExitEvent]
	    if (e.method.name startsWith "assert")
			new MethodExit(e)    
	    else
	        new OtherEvents(e)
	}
}
