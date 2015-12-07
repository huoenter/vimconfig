package edu.udel.patch

import edu.udel.patch.util._
import com.sun.jdi.VirtualMachine
import com.sun.jdi.Bootstrap
import scala.collection.JavaConversions._
import com.sun.jdi.event.VMDisconnectEvent
import com.sun.jdi.event.ClassPrepareEvent
import edu.udel.patch.EventHandler._

object Main {
    def main(args: Array[String]) = {
        val target = args(1)

        val vm = new VirtualMachineLauncher().setOptions("-cp bin").setMain(target).launch()
        
        val classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest()
        
        classPrepareRequest.addClassFilter(target)
        classPrepareRequest.putProperty(classOf[EventHandler], EventHandler(clsPrepHandler))
        classPrepareRequest.enable()
        
        vm.resume()
        
        val evtQueue = vm.eventQueue
        
        while (true) {
        	val evtSet = evtQueue.remove 
        	for (event <- evtSet.eventIterator) {
        	    event.request match {
        	        case disRequest: VMDisconnectEvent => System.exit(0)
        	        case req => req.getProperty(classOf[EventHandler]) match {
        	            case h: EventHandler => h.f(event)
        	        }
        	    }
        	}
        	evtSet.resume()
        }
    }
}
