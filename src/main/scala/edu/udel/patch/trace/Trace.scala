package edu.udel.patch.trace

import com.sun.jdi.Method
import com.sun.jdi.Field
import com.sun.jdi.Mirror
import com.sun.jdi.event.Event
import com.sun.jdi.event.MethodEntryEvent
import com.sun.jdi.event.MethodExitEvent
import com.sun.jdi.event.AccessWatchpointEvent
import com.sun.jdi.event.ModificationWatchpointEvent
import com.sun.jdi.event.LocatableEvent

sealed trait Observation {
    val e: Event

    def location = e match {
        case l: LocatableEvent => Some(l.location)
        case _                 => None
    }
}

case class OtherEvents(e: Event) extends Observation {
    override def toString = ""
}

case class MethodInvocation(e: MethodEntryEvent) extends Observation {
    override def toString = "\nEnter method: " + e.method
}
case class MethodExit(e: MethodExitEvent) extends Observation {
    override def toString = "\nExit method: " + e.method
}

case class FieldAccess(e: AccessWatchpointEvent) extends Observation {
    override def toString = {
        val field = e.field

	    if (e.`object`==null) {
	    	"Accessing %s.%s @ %s\n" format (field.declaringType.name, field.name, e.location)
	    }
	    else {
	        val obj = e.`object`
	        "Accessing %s[%d].%s @ %s\n" format (obj.referenceType.name, obj.uniqueID, field.name, e.location)
	    }
    }
}

case class FieldModification(e: ModificationWatchpointEvent) extends Observation {
    override def toString = {
        val field = e.field

	    if (e.`object`==null) {
	    	"Modifying %s.%s @ %s\n" format (field.declaringType.name, field.name, e.location)
	    }
	    else {
	        val obj = e.`object`
	        "Modifying %s[%d].%s @ %s\n" format (obj.referenceType.name, obj.uniqueID, field.name, e.location)
	    }
    }
}

class Trace {
    var obs = List[Observation]()
    def addObservation(ob: Observation) = obs ::= ob
}