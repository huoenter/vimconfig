package edu.udel.patch.util

import com.sun.jdi.VirtualMachine
import com.sun.jdi.request.EventRequest
import com.sun.jdi.request.WatchpointRequest
import com.sun.jdi.request.MethodEntryRequest
import com.sun.jdi.request.MethodExitRequest
import com.sun.jdi.request.ClassPrepareRequest

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
}