package edu.udel.patch

import com.sun.jdi.Field
import com.sun.jdi.VirtualMachine
import com.sun.jdi.request.EventRequest
import edu.udel.patch.EventHandler._

object WPInstaller {
	def install(f: Field) = {
	    val vm = f.virtualMachine
	    val eventRequestMgr = vm.eventRequestManager
	    val request = eventRequestMgr.createAccessWatchpointRequest(f)
	    
	    request.putProperty(classOf[EventHandler], EventHandler(fieldAccessHandler))
	    request.enable()
	}
}