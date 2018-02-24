//
//  Renato Goes to Work
//  (C) 2018 Renato Haro
//
 
definition(
    name:        "Renato Goes To Work V1.0",
    namespace:   "renatoharo",
    author:      "Renato Haro",
    description: "Automation app that orchestrate all the events I need happening to make my morning routine as effortless as possible.",
    category:    "My Apps",
    iconUrl:     "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:   "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url:   "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)


// -- PREFERENCES PAGES
//    Definition of the pages used to configure the application

preferences
{
	// -- PAGE 1
    //    Define when this automation script is to be applied
    
    page(name:"pageONE", title:"Apply this automation", nextPage:"whenDRESSING")
    	{
		section("People")
        	{
         	input "people", "capability.presenceSensor", title: "When these people are present:", required: true
			}
        section("Time")
        	{
        	input "days", "enum", title: "These days of the week:", required: true, multiple: true, options: ["Mon": "Mon", "Tue": "Tue", "Wed": "Wed", "Thu": "Thu", "Fri": "Fri", "Sat": "Sat", "Sun": "Sun"]
			input "fromTime", "time", title: "From", required: true
        	input "toTime",   "time", title: "To",   required: true 
			}
        section("Notifications")
        	{
            input "sendPush", "bool", required: false, title: "Push notifications?"
            }
    	}
    
    
    // -- PAGE 2
    //    Define behavior of state: DRESSING
    
    page(name:"whenDRESSING", title:"DRESSING", nextPage:"whenCATCHINGUP")
    	{
    	section("Triggered when:")
        	{
			input "triggerDRESSING", "capability.contactSensor", title: "This door opens:", required:true
    		}
               
        section("Actions")
        	{
            input "when_dressing_turn_off", "capability.switch", title: "Turn OFF these devices:", multiple:true, required:false
			input "when_dressing_turn_on",  "capability.switch", title: "Turn ON these devices:",  multiple:true, required:false
            input "when_dressing_lock",     "capability.lock",   title: "Lock these locks:",       multiple:true, required:false
            input "when_dressing_unlock",   "capability.lock",   title: "Unlock these locks:",     multiple:true, required:false
    		}
		}
    
    
    // -- PAGE 3
    //    Define behavior of state: CATCHINGUP
    
    page(name:"whenCATCHINGUP", title:"CATCHING UP", nextPage:"whenGOINGDOWN")
    	{
    	section("Triggered when:")
        	{
			input "triggerCATCHINGUP", "capability.contactSensor", title: "This door opens:", required:true
    		}
            
    	section("Actions")
        	{
    		input "when_catchingup_turn_off", "capability.switch", title: "Turn OFF these devices:", multiple:true, required:false
			input "when_catchingup_turn_on",  "capability.switch", title: "Turn ON these devices:",  multiple:true, required:false
            input "when_catchingup_lock",     "capability.lock",   title: "Lock these locks:",       multiple:true, required:false
            input "when_catchingup_unlock",   "capability.lock",   title: "Unlock these locks:",     multiple:true, required:false
    		}
		}
        
    
    // -- PAGE 4
    //    Define behavior of state: GOINGDOWN
    
    page(name:"whenGOINGDOWN", title: "GOING DOWN", nextPage:"whenLEAVING")
    	{
    	section("Triggered when:")
        	{
			input "triggerGOINGDOWN", "capability.contactSensor", title: "This door opens:"
        	}
        
        section("Actions")
        	{
            input "when_goingdown_turn_off", "capability.switch", title: "Turn OFF these devices:", multiple:true, required:false
			input "when_goingdown_turn_on",  "capability.switch", title: "Turn ON these devices:",  multiple:true, required:false
            input "when_goingdown_lock",     "capability.lock",   title: "Lock these locks:",       multiple:true, required:false
            input "when_goingdown_unlock",   "capability.lock",   title: "Unlock these locks:",     multiple:true, required:false
			}
    	}
        
        
    // -- PAGE 5
    //    Define behavior of state: LEAVING     
    
	page(name:"whenLEAVING", title: "LEAVING", nextPage:"whenGONE")
    	{
    	section("Triggered when:")
        	{
			input "triggerLEAVING", "capability.contactSensor", title: "This door opens:", required: false 		// eventually must be required
        	}
            
        section("Actions")
        	{
            input "when_leaving_turn_off", "capability.switch", title: "Turn OFF these devices:", multiple:true, required:false
			input "when_leaving_turn_on",  "capability.switch", title: "Turn ON these devices:",  multiple:true, required:false
            input "when_leaving_lock",     "capability.lock",   title: "Lock these locks:",       multiple:true, required:false
            input "when_leaving_unlock",   "capability.lock",   title: "Unlock these locks:",     multiple:true, required:false
    		}
    	}


	// -- PAGE 6
    //    Define clean-up actions after LEAVING (Once GONE)
    
	page(name:"whenGONE", title: "Once GONE", install:true, uninstall:true)
    	{
        /*
    	section("Triggered when:")
        	{
			input "triggerLEAVING", "capability.contactSensor", title: "This door opens:", required: false 		// eventually must be required
        	}
        */
        
        section("Actions")
        	{
            input "when_gone_turn_off", "capability.switch", title: "Turn OFF these devices:", multiple:true, required:false
			input "when_gone_turn_on",  "capability.switch", title: "Turn ON these devices:",  multiple:true, required:false
            input "when_gone_lock",     "capability.lock",   title: "Lock these locks:",       multiple:true, required:false
            input "when_gone_unlock",   "capability.lock",   title: "Unlock these locks:",     multiple:true, required:false
    		}
    	}
}


def installed()
{
	log.debug "Installed with settings: ${settings}"
	initialize()
}


def updated()
{
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}


def initialize()
{
    state.my_state = "UNKNOWN"
	subscribe(people, "presence", handlePresenceEvent)
    subscribe(triggerDRESSING, "contact.open", handleDRESSING)
    subscribe(triggerCATCHINGUP, "contact.open", handleCATCHINGUP)
    subscribe(triggerGOINGDOWN, "contact.open", handleGOINGDOWN)
    subscribe(triggerLEAVING, "contact.open", handleLEAVING)
}


private everyoneIsHere()
{
	def result = true
    for (person in people)
    	{
    	if (person.currentPresence == "not present")
        	{
        	result = false
            break
            }
     	}
     return result
}



//	Prescence Event 
//	-- A change in prescence has been detected, this should revert all actions taken and suspend the sequence

def handlePresenceEvent(evt) {
	log.trace "Executing: handlePrescenceEvent"
    if (evt.value == "not present")
    	{
    	log.trace "Renato is not present"
        CleanUpAfterDeparture()
        }
    else
    	log.trace "Renato is present"
}


// 		DRESSING
// -- 	The trigger condition for DRESSING has been detected, perform corresponding actions
//		DRESSING is the condition that triggers the entire automation sequence.
//		There is no other way of entering the sequence; for that reason, the validation of
//		presence, day of the week and time happens within this event handler.

def handleDRESSING(evt)
{
	log.debug "Entering: handleDRESSING()"
	log.debug "state.my_state: $state.my_state"

    def currentState = state.my_state
    if (currentState == "UNKNOWN")
    	{
		if (everyoneIsHere())
        	{
    		def df = new java.text.SimpleDateFormat("EEE")											// 		AND day of the week with three characters Mon, Tue, Wed
    		df.setTimeZone(location.timeZone)														//		ensure new date object is set to local time of the location
    		def day = df.format(new Date())															// 		get the current date in the desired format
        	def dayCheck = days.contains(day)
        	if (dayCheck)
            	{
        		def between = timeOfDayIsBetween(fromTime, toTime, new Date(), location.timeZone)
            	if (between)					
                	{																		
          			state.my_state = "DRESSING"
                    log.trace "Renato is now: $state.my_state"
                    
                    when_dressing_turn_off?.off()
					when_dressing_turn_on?.on()
					when_dressing_lock?.lock()
					when_dressing_unlock?.unlock()
        			}
    			}
			}
    	}
}



// CATCHINGUP
// -- The trigger condition for CATCHINGUP has been detected, perform corresponding actions

def handleCATCHINGUP(evt)
{
	log.debug "Entering: handleCATCHINGUP()"
    log.debug "state.my_state: $state.my_state"

    def currentState = state.my_state      
    if (currentState == "DRESSING")
    	{				
		state.my_state = "CATCHINGUP"
        log.trace "Renato is now: $state.my_state"
        
        when_catchingup_turn_off?.off()
		when_catchingup_turn_on?.on()
    	
    	when_catchingup_lock?.lock()
		when_catchingup_unlock?.unlock()
        }
}
 

// GOINGDOWN
// -- The trigger condition for GOINGDOWN has been detected, perform corresponding actions

def handleGOINGDOWN(evt)
{
	log.debug "Entering: handleGOINGDOWN()"
	log.debug "state.my_state: $state.my_state"
    
    def currentState = state.my_state      
    if (currentState == "CATCHINGUP")
    	{				
		state.my_state = "GOINGDOWN"
        log.trace "Renato is now: $state.my_state"
        
        when_goingdown_turn_off?.off()
		when_goingdown_turn_on?.on()
		when_goingdown_lock?.lock()
		when_goingdown_unlock?.unlock()
        }
}


// LEAVING
// -- The trigger condition for LEAVING has been detected, perform corresponding actions

def handleLEAVING(evt)
{
	log.debug "Entering: handleLEAVING()"
	log.debug "state.my_state: $state.my_state"
    
    def currentState = state.my_state      
    if (currentState == "GOINGDOWN")
    	{				
		state.my_state = "LEAVING"
        log.trace "Renato is now: $state.my_state"
        
        when_leaving_turn_off?.off()
		when_leaving_turn_on?.on()
		when_leaving_lock?.lock()
		when_leaving_unlock?.unlock()
        }
}



def CleanUpAfterDeparture()
{
	log.debug "Entering: CleanUpAfterDeparture()"
    log.debug "state.my_state: $state.my_state"  			
	
    // clean-up only when in context
    if (state.my_state != "UNKNOWN")
    	{
    	state.my_state = "UNKNOWN"
	    log.trace "Renato is now: $state.my_state"
        
        if (sendPush)
        	{
            sendPush("NOT PRESENT\r\nLights OFF - Doors Locked.")
            }
	
 	 	// revert actions taken during LEAVING
    	when_gone_unlock?.unlock()
    	when_gone_lock?.lock()
    	when_gone_turn_on?.on()
    	when_gone_turn_off?.off()
    	}
}