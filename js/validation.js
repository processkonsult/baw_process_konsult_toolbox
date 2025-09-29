/*
	Setting the scope like this allows contents of this script to be called from anywhere
	a CSHS - Inline Javascript, Events, Inline Events, and CSHS script blocks - AFTER
	the template within the coach has loaded (i.e. after the initial coach screen is presented
	to the user).

	The order these functions should be called would be:

	1) OnLoad of the page, call the init() function like this:

		scope.baw.validation.init(_this);

	   Or like this to enable debugging:

		scope.baw.validation.init(_this, true);

	2) OnClick of the "Next" button, call resetInvalidViews()
	3) After resetInvalidViews(), implement any page-specific validation rules
	4) After any page-specific validation rules, call validatePage() which will
	take into account any page-specific validation errors and then evaluate
	any visible required fields to ensure they are populated before determining
	whether or not the boundary event should fire.
*/

var scope = window;
scope.baw = scope.baw || {};

scope.baw.validation = scope.baw.validation || {
	view: null,
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
	},

	evaluateRequiredViews: function(optionalStartingView) {
		this.debug("evaluateRequiredViews(optionalStartingView)", optionalStartingView);

		var requiredViews = bpmext.ui.getRequiredViews(false, optionalStartingView);
		this.debug("requiredViews", requiredViews);

		var rootViewId = this.view.context.viewid;
		this.debug("rootViewId", rootViewId);

		if(requiredViews.length > 0) {
			for(var i=0; i<requiredViews.length; i++) {
				this.debug("requiredViews[" + i + "]", requiredViews[i]);
				this.debug("requiredViews[" + i + "].context.viewid", requiredViews[i].context.viewid);
				// First check to make sure the view and all parent views up to the root view are visible
				var viewVisible = true;
				var parentView = requiredViews[i].ui.getParent(true);
				while(viewVisible && parentView.context.viewid != rootViewId) {
					if(parentView.isVisible() == false) {
						this.debug("view not visible", requiredViews[i].context.viewid);
						viewVisible = false;
					}
					parentView = parentView.ui.getParent(true);
				}
				this.debug("requiredViews[" + i + "].getData(): [" + requiredViews[i].getData() + "]");
				var viewData = requiredViews[i].getData();
				if(viewData == null) {
					this.debug(requiredViews[i].context.viewid + " is null");
				} else if(viewData == "") {
					this.debug(requiredViews[i].context.viewid + " is empty");
				} else if(viewData == undefined) {
					this.debug(requiredViews[i].context.viewid + " is undefined");
				// If control is bound to an object - such as a SingleSelect - evaluate the control's "Item selection data" 
				// config option and get the "Value property", then ensure that property has a value.
				} else if(typeof viewData == "object") {
					this.debug(requiredViews[i].context.viewid + " is bound to a Complex Type");
					if(requiredViews[i].getOption("dataMapping") && requiredViews[i].getOption("dataMapping").optionValueProperty
					   && requiredViews[i].getOption("dataMapping").optionValueProperty != "") {
						var boundProperty = requiredViews[i].getOption("dataMapping").optionValueProperty;
						this.debug(requiredViews[i].context.viewid + " is bound to the property: [" + boundProperty + "]");
						this.debug("viewData[boundProperty]: " + viewData[boundProperty]);
						viewData = viewData[boundProperty];
					}
				}
				
				if(viewVisible && (viewData == null || viewData == "")) {
					this.debug(requiredViews[i].context.viewid + " required, visible, but no value");
					requiredViews[i].setValid(false, "This is a required field");
				}
			}
		}
	},

	// This function will reset all invalid views back to valid and should be called before
	// screen-specific validation rules are evaluated and before validatePage() is invoked
	resetInvalidViews: function() {
		this.debug("resetInvalidViews()");
		var invalidViews = bpmext.ui.getInvalidViews();
		if(invalidViews != null && invalidViews.length > 0) {
			for(var i=0; i<invalidViews.length; i++) {
				this.debug("invalidViews[" + i + "]", invalidViews[i]);
				invalidViews[i].setValid(true);
			}
		}
	},

	validatePage: function(optionalStartingView) {
		this.debug("validatePage(optionalStartingView)", optionalStartingView);

		// Evaluate all views on the page with Required visibility
		// and for those that do not have a value, set to invalid
		this.evaluateRequiredViews(optionalStartingView);

		var fireBoundary = false;
		var invalidViews = bpmext.ui.getInvalidViews();
		this.debug("invalidViews", invalidViews);
		if(invalidViews.length > 0) {
			fireBoundary = false;
		} else {
			fireBoundary = true;
		}
		this.debug("fireBoundary", fireBoundary);
		return fireBoundary;
	},

	maxLengthCheck: function(viewContext, isValid, coachViewControlId, maxLength) {
		var cv = viewContext.ui.get(coachViewControlId);
		// Only evaluate the rule if the CV is currently valid
		if(cv.isValid() == true) {
			var text = null;

			// Different control types may need to be checked differently
			var classes = cv.context.element.className.split(' ');
			if(classes.includes('Text_Editor')) {
				text = getTextFromRichTextEditorCV(cv);
			} else {
				text = cv.getData();
			}

			if(text != null && text.length > maxLength) {
				isValid = false;
				cv.setValid(false, "Must be less than " + maxLength + " characters.");
			} else {
				cv.setValid(true);
			}
		}
		return isValid;
	},
	
	minLengthCheck: function(viewContext, isValid, coachViewControlId, minLength) {
		var cv = viewContext.ui.get(coachViewControlId);
		// Only evaluate the rule if the CV is currently valid
		if(cv.isValid() == true) {
			var text = null;

			// Different control types may need to be checked differently
			var classes = cv.context.element.className.split(' ');
			if(classes.includes('Text_Editor')) {
				text = getTextFromRichTextEditorCV(cv);
			} else {
				text = cv.getData();
			}

			if(text != null && text.length < minLength) {
				isValid = false;
				cv.setValid(false, "Must be more than " + minLength + " characters.");
			} else {
				cv.setValid(true);
			}
		}
		return isValid;
	}
}
