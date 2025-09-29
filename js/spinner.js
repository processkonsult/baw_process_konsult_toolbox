/*
	Setting the scope like this allows contents of this script to be called from anywhere
	a CSHS - Inline Javascript, Events, Inline Events, and CSHS script blocks - AFTER
	the template within the coach has loaded (i.e. after the initial coach screen is presented
	to the user).

	The order these functions should be called would be:

	1) OnLoad of the page, call the init() function like this. Here the control is the Spinner control:

		scope.baw.validation.init(control);

	2) To show spinner, use showSpinner()
	3) To hide spinner, use hideSpinner()
*/

var scope = window;
scope.baw = scope.baw || {};

scope.baw.spinner = scope.baw.spinner || {
	view: null,
	init: function(view) {
		this.view = view;
		spinnerCV = view;

	},
	showSpinner: function(){
		this.view.show();
	},
	hideSpinner: function(){
		this.view.hide();
	}
}
