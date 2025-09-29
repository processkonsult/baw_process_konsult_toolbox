// Setting the scope like this allows contents of this script to be called from anywhere
// in a CSHS - Inline Javascript, Events, Inline Events, and CSHS script blocks - AFTER
// the template within the coach has loaded (i.e. after the initial coach screen is presented
// to the user).

var scope = window;
scope.baw = scope.baw || {};

scope.baw.cshsExceptionHandling = scope.baw.cshsExceptionHandling || {
	view: null,
	alertCV: null,
	debugEnabled: false,
	debug: function(msg, object) {
		if(this.debugEnabled) {
			if(object != null)
				console.log(msg, object);
			else
				console.log(msg);
		}
	},
	init: function(view, debugEnabled) {
		this.debugEnabled = debugEnabled;
		this.debug("this.debugEnabled", this.debugEnabled);
		this.view = view;
		this.debug("this.view", this.view);
		this.alertCV = view.ui.get("Alerts1");
		// Hide the Alerts coachview by default to eliminate any extra space at the top of the coach screen
		this.alertCV.hide(true);
		this.debug("alertCV", this.alertCV);

		// Set this scope to the Human Service Frame scope so functions can 
		// be called from javascript blocks within CSHS services.
		var hsScope = com_ibm_bpm_global.getHSFrame();
		this.debug("hsScope", hsScope);
		hsScope.baw = scope.baw;
		
		// Set the scope on the parent as well to handle situations when services are run from the portal
		hsScope.parent.baw = scope.baw;
	},
	throwException: function(error, optionalExceptionTitle) {
		this.alertCV.clear();
		if(scope.baw.spinner && scope.baw.spinner.hideSpinner)
			scope.baw.spinner.hideSpinner();
		if(optionalExceptionTitle == null || optionalExceptionTitle == "")
			optionalExceptionTitle = "Exception";
		// Show the Alerts coachview since it is hidden to preserve space
		this.alertCV.show();
		if(typeof error == 'string') {
			this.alertCV.appendAlert(optionalExceptionTitle, error, "D", 0);
		} else if(typeof error == 'object' && error.hasOwnProperty('errorCode')) {
			var errorText = "No error details.";
			if(error.errorText != null && error.errorText != "") {
				errorText = error.errorText;
			}
			this.alertCV.appendAlert(optionalExceptionTitle, error.errorCode + ": " + errorText, "D", 0);
		} else if(typeof error == 'object' && error.responseText != null) {
			this.alertCV.appendAlert(optionalExceptionTitle, error.responseText, "D", 0);
		} else if(typeof error == 'object' && error.response != null && error.response.text != null) {
			this.alertCV.appendAlert(optionalExceptionTitle, error.response.text, "D", 0);
		} else if(typeof error == 'object' && error.message != null) {
			this.alertCV.appendAlert(optionalExceptionTitle, error.message, "D", 0);
		} else {
			this.alertCV.appendAlert(optionalExceptionTitle, error, "D", 0);
		}
		
		// Scroll to the top (where the Alert resides) on a delay to accommodate modals closing
		setTimeout(() => {
		  window.scrollTo(0, 0);
		}, 1000);
	},
	clearExceptions: function() {
		this.alertCV.clear();
		// Hide the Alerts coachview by default to eliminate any extra space at the top of the coach screen
		this.alertCV.hide(true);
	}
}
