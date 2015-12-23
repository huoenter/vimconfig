package edu.udel.patch

import com.sun.jdi.Field
import com.sun.jdi.VirtualMachine
import com.sun.jdi.request.EventRequest
import edu.udel.patch.EventHandler._

import edu.udel.patch.util.Types._

import com.sun.jdi.Method

object Installer {
    def install(f: Field) = {
        val vm = f.virtualMachine
        val eventRequestMgr = vm.eventRequestManager

        val accessrequest = eventRequestMgr.createAccessWatchpointRequest(f)
        accessrequest.putProperty(classOf[EventHandler], EventHandler(fieldAccessHandler))
        accessrequest.addClassExclusionFilter("org.junit.*")
        accessrequest.enable()

        val modificationRequest = eventRequestMgr.createModificationWatchpointRequest(f)
        modificationRequest.putProperty(classOf[EventHandler], EventHandler(fieldModificationHandler))
        modificationRequest.addClassExclusionFilter("org.junit.*")
        modificationRequest.enable()
    }
    
    def createFieldCPE(vm: VirtualMachine) = {
        val classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest()

        classPrepareRequest.putProperty(classOf[EventHandler], EventHandler(fieldPrepHandler))
        addExclusions(classPrepareRequest)
        classPrepareRequest.enable()
    }

    def createTestCPE(vm: VirtualMachine) = {
        val classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest()

        classPrepareRequest.putProperty(classOf[EventHandler], EventHandler(testPrepHandler))
        classPrepareRequest.addClassFilter("junit.framework.TestCase")
        classPrepareRequest.enable()
    }
    
    def createApplicationMethodEvents(vm: VirtualMachine) = {
        val evtReqMgr = vm.eventRequestManager
        val entryrequest = evtReqMgr.createMethodEntryRequest()

        addExclusions(entryrequest)
        entryrequest.putProperty(classOf[EventHandler], EventHandler(methodEntryHandler))
        entryrequest.enable()

        val exitRequest = evtReqMgr.createMethodExitRequest()

        exitRequest.putProperty(classOf[EventHandler], EventHandler(methodExitHandler))
        addExclusions(exitRequest)
        exitRequest.enable()
    }
    
    def createAssertionEvents(vm: VirtualMachine) = {
        val evtReqMgr = vm.eventRequestManager
        val entryrequest = evtReqMgr.createMethodEntryRequest()

        entryrequest.addClassFilter("junit.framework.Assert")
        entryrequest.putProperty(classOf[EventHandler], EventHandler(assertEntryHandler))
        entryrequest.enable()

        val exitRequest = evtReqMgr.createMethodExitRequest()

        exitRequest.putProperty(classOf[EventHandler], EventHandler(assertExitHandler))
        exitRequest.addClassFilter("junit.framework.Assert")
        exitRequest.enable()
    }
}