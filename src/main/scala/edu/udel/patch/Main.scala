package edu.udel.patch

import edu.udel.patch.util._
import edu.udel.patch.util.Types._
import com.sun.jdi.VirtualMachine
import com.sun.jdi.Bootstrap
import scala.collection.JavaConversions._
import com.sun.jdi.event.VMDisconnectEvent
import com.sun.jdi.event.ClassPrepareEvent
import edu.udel.patch.EventHandler._
import edu.udel.patch.util.Config._
import edu.udel.patch.Installer._
import edu.udel.patch.trace.Observation
import com.sun.jdi.VMDisconnectedException
import edu.udel.patch.trace.MethodInvocation

object Main {
    def main(args: Array[String]) = {
        val target = "commons-cli2"
        //        val target = args(1)

        val vm = new VirtualMachineLauncher().
            setOptions("-cp " + junitLib + ":" + subCP + target + "/bin").setMain(junitMain + " " + testClass1).launch()

        createFieldCPE(vm)
        createTestCPE(vm)
        createApplicationMethodEvents(vm)
        //        createAssertionEvents(vm)

        //        vm.resume()

        val evtQueue = vm.eventQueue

        var connected = true

        while (connected) {
            val evtSet = evtQueue.remove
            for (event <- evtSet.eventIterator) {
                if (event != null && event.isInstanceOf[VMDisconnectEvent]) connected = false
                Option(event.request) match {
                    case Some(req) => {
                        req.getProperty(classOf[EventHandler]) match {
                            case h: EventHandler => h.f(event)
                        }
                    }
                    case None =>
                }
            }
            evtSet.resume()
        }
        
    }
}
