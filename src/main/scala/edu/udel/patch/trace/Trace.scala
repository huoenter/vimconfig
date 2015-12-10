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
import com.sun.jdi.ObjectReference
import edu.udel.patch.util.Types
import com.sun.jdi.ReferenceType
import com.sun.jdi.Location
import com.sun.jdi.event.WatchpointEvent


object H {
    def location(e: Event) = e match {
        case l: LocatableEvent => Some(l.location)
        case _                 => None
    }

    def getObjectReference(e: Event) = e match {
        case wp: WatchpointEvent  => Some(wp.`object`())
        case me: MethodEntryEvent => Some(me.thread.frame(0).thisObject)
        case mx: MethodExitEvent  => Some(mx.thread.frame(0).thisObject)
        case _                    => None
    }

    def isTest(refType: ReferenceType) = refType.compareTo(Types.testTypes(0)) < 0
}

trait Observation 
object OtherEvents extends Observation {
    override def toString = ""
}

case class MethodInvocation(objRef: ObjectReference, m: Method, isTest: Boolean) extends Observation {
    def this(objRef: ObjectReference, m: Method) = this(objRef, m, if (objRef != null) H.isTest(objRef.referenceType) else false)
    def this(e: MethodEntryEvent) = this(H.getObjectReference(e).get, e.method)

    override def toString = "\nEnter method: " + m
}

case class MethodExit(objRef: ObjectReference, m: Method, isTest: Boolean) extends Observation {
    def this(objRef: ObjectReference, m: Method) = this(objRef, m, if (objRef != null) H.isTest(objRef.referenceType) else false)
    def this(e: MethodExitEvent) = this(H.getObjectReference(e).get, e.method)

    override def toString = "\nExit method: " + m
}


case class FieldAccess(objRef: ObjectReference, refType: ReferenceType, val f: Field, val loc: Location) extends Observation {
    def this(objRef: ObjectReference, f: Field, loc: Location) = this(objRef, if(objRef!=null)objRef.referenceType else null, f, loc)
    def this(e: AccessWatchpointEvent) = this(e.`object`(), e.field, e.location)

    override def toString = {
        if (objRef == null) 
            "Accessing %s.%s @ %s\n" format (f.declaringType.name, f.name, loc)
        else 
            "Accessing %s[%d].%s @ %s\n" format (refType.name, objRef.uniqueID, f.name, loc)
    }
}

case class FieldModification(objRef: ObjectReference, refType: ReferenceType, val f: Field, val loc: Location) extends Observation {
    def this(objRef: ObjectReference, f: Field, loc: Location) = this(objRef, if(objRef!=null)objRef.referenceType else null, f, loc)
    def this(e: ModificationWatchpointEvent) = this(e.`object`(), e.field, e.location)

    override def toString = {
        if (objRef == null) 
            "Accessing %s.%s @ %s\n" format (f.declaringType.name, f.name, loc)
        else 
            "Accessing %s[%d].%s @ %s\n" format (refType.name, objRef.uniqueID, f.name, loc)
    }
}