/**
 *  Enphase Solar Array Power Meter for Hubitat
 *  Uses the local enphase envoy API
 */

metadata {
    definition (name: "Enphase Solar Array PowerMeter", namespace: "sleuth255", author: "Sleuth255") {
        capability "PowerMeter"
        capability "Sensor"
        capability "PushableButton"
        attribute "numberOfButtons","1"
        attribute "power","0"
	attribute "maxtoday","0"
        attribute "kwhToday","0"
	}
}

preferences() {    	
    section() {
        input "ApiUrl", "text", required: true, title: "Array API Url", defaultValue: "http://<your envoy IP address>/production.json"
        input "logEnable", "bool", title: "Enable logging", required: true, defaultValue: true
    }
}

// device commands


// General App Events
def initialize() {
	try {
        sendEvent(name: "numberOfButtons", value: 1, isStateChange: true);
		if(logEnable) log.debug "requesting array LiveData"
        httpGet(ApiUrl) { resp ->
            if (resp.success) {
                if (device.currentValue("maxtoday") == null)
                    def Integer maxToday = 0;
                else    
                    def Integer maxToday = (device.currentValue("maxtoday")).toInteger();
                def Integer power = resp.data.production.wNow[1];
                def whtoday = (resp.data.production.whToday[1]).toInteger() / 1000;
                if (power > maxToday)
                    sendEvent(name: "maxtoday", value: power, isStateChange: true);
                else
                if (power < 0 && maxToday > 0)
                    sendEvent(name: "maxtoday", value: 0, isStateChange: true);
                sendEvent(name: "power", value: power, isStateChange: true);
                sendEvent(name: "kwhToday", value: whtoday, isStateChange: true);
                if (logEnable) {
                      log.debug("response received: ${power}");
                }
            }
           else
                sendEvent(name: "power", value: "Offline", isStateChange: true);
        }
    } catch(e) {
		log.warn "Initialize Error: ${e.message}"
        sendEvent(name: "power", value: "Offline", isStateChange: true);
    }
}

def installed(){
	initialize()
}

def updated(){
	initialize()
}

def push(nbr){
	try {
		if(logEnable) log.debug "requesting array LiveData"
        httpGet(ApiUrl) { resp ->
            if (resp.success) {
                def Integer maxToday = (device.currentValue("maxtoday")).toInteger();
                def Integer power = resp.data.production.wNow[1];
                def whtoday = (resp.data.production.whToday[1]).toInteger() / 1000;
                if (power > maxToday)
                    sendEvent(name: "maxtoday", value: power, isStateChange: true);
                else
                if (power < 0 && maxToday > 0)
                    sendEvent(name: "maxtoday", value: 0, isStateChange: true);
                sendEvent(name: "power", value: power, isStateChange: true);
                sendEvent(name: "kwhToday", value: whtoday, isStateChange: true);
                if (logEnable) {
                      log.debug("response received: ${power}");
	        }
            }
            else
                sendEvent(name: "power", value: "Offline", isStateChange: true);
        }
    } catch(e) {
		log.warn "Push Request Error: ${e.message}"
        sendEvent(name: "power", value: "Offline", isStateChange: true);
    }
}

