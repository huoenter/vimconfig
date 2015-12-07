package edu.udel.patch

import com.sun.jdi.request.EventRequest
import com.sun.jdi.event.ClassPrepareEvent
import scala.collection.JavaConversions._
import com.sun.jdi.event.AccessWatchpointEvent
import com.sun.jdi.event.Event

case class EventHandler(f: Function1[Event, Unit])

object EventHandler {
	val clsPrepHandler = (event: Event) => {
	    val e = event.asInstanceOf[ClassPrepareEvent]
	    e.referenceType.allFields foreach WPInstaller.install
	}
	
	val fieldAccessHandler = (event: Event) => {
	    val e = event.asInstanceOf[AccessWatchpointEvent]
	    
	    val field = e.field
	    
	    if (e.`object`()==null) {
	    	println("Accessing %s.%s @ %s\n" format (field.declaringType.name, field.name, e.location))
	    }
	    else {
	        val obj = e.`object`
	        println("Accessing %s[%d].%s @ %s\n" format (obj.referenceType.name, obj.uniqueID, field.name, e.location))
	    }
	}
}
