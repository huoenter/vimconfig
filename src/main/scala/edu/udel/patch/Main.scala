package edu.udel.patch

import edu.udel.patch.util._
import edu.udel.patch.util.Types._
import com.sun.jdi.VirtualMachine
import com.sun.jdi.Bootstrap
import scala.collection.JavaConversions._
import com.sun.jdi.event.VMDisconnectEvent
import com.sun.jdi.event.ClassPrepareEvent
import edu.udel.patch.EventHandler._

object Main {
    def main(args: Array[String]) = {
        val target = "edu.udel.patch.test.Test1"
        //        val target = args(1)

        val vm = new VirtualMachineLauncher().setOptions("-cp /home/huoc/Projects/scala/patch/target/scala-2.11/classes").setMain(target).launch()

        val classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest()

        classPrepareRequest.putProperty(classOf[EventHandler], EventHandler(clsPrepHandler))
        addExclusions(classPrepareRequest)
        classPrepareRequest.enable()

        //        vm.resume()

        val evtQueue = vm.eventQueue

        while (true) {
            val evtSet = evtQueue.remove
            for (event <- evtSet.eventIterator) {
                Option(event.request) match {
                    case Some(req) => {
                        req.getProperty(classOf[EventHandler]) match {
                            case h: EventHandler => println(h.f(event))
                        }
                    }
                    case None =>
                }
            }
            evtSet.resume()
        }
    }
}
