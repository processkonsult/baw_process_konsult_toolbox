/*
	This script is helpful for providing an easy way to incorporate debug-level logging in specific coachviews but also	
	disable all of the logging statements easily by initializing the function with the debugEnabled (true/false) setting.
	
	Follow these steps to incorporate this CSHS debugging/logging script for coachviews:

	1) Added debug.js as a Web file to the BAW solution
	2) In the project's main template add this file under Included Scripts. Add 
		to individual coachviews if there is no main project template.
	3) Initialize the debugger by adding this code at the top of the Inline Javascript of the coachview - 
		the boolean argument indicates whether logging should be enabled or disabled:
		
		var debug = scope.baw.debug.init(false);

	4) Once initialized, from anywhere in the coachview log to the brower console like this:
		debug.log("my debug log message");

*/

var scope = window;
scope.baw = scope.baw || {};
scope.baw.debug = scope.baw.debug || {
	debugEnabled: false,
	init: function(debugEnabled) {
		this.debugEnabled = debugEnabled;
		this.log("scope.baw.debugEnabled", this.debugEnabled);
		return scope.baw.debug;
	},
	log: function(msg, object) {
		if(this.debugEnabled) {
			if(object != null)
				console.log(msg, object);
			else
				console.log(msg);
		}
	}
}
