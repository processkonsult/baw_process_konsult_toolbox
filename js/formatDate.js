/*
	Use this function everywhere a date is displayed in a table using the onCustomCell event, or from an output or display 
	Text control to consistently provide the date in the format the business desires. Parameters:
		inputDate: Required input of either a String or Date that is to be formatted.
		dateOnly: Optional input that when provided as 'true' will not include the time portion of the date

	Usage:
		
		// Return date formatted as MM/dd/yyyy h:mm:ss A
		baw.formatDate(date); 			// With time component, i.e. "01/04/2025 8:17:30 AM"
		baw.formatDate(date, true); 	// Without time component, i.e. "01/04/2025"

		// Return date formatted as dd-MMM-yyyy HH:mm:ss
		baw.formatDateIntl(date); 		// With time component, i.e. "04-JAN-2025 18:17:30"
		baw.formatDateIntl(date, true); // Without time component, i.e. "04-JAN-2025"
*/

var scope = window;
scope.baw = scope.baw || {};

// Return date formatted as MM/dd/yyyy h:mm:ss A
function formatDate(inputDate, dateOnly, asUTC) {
	var formattedDate = null;
	var date = convertInputToDate(inputDate);
	if(date != null) {
		var options = {
			month: '2-digit', 
			day: '2-digit', 
			year: 'numeric'
		};
		if(asUTC) {
			options.timeZone = 'UTC';
		}			
		formattedDate = date.toLocaleDateString('en-EN', options);
		if(!dateOnly)
			formattedDate += ' ' + date.toLocaleTimeString('en-EN');
	}
	return formattedDate;
}
scope.baw.formatDate = formatDate;

// Return date formatted as dd-MMM-yyyy HH:mm:ss
function formatDateIntl(inputDate, dateOnly, asUTC) {
	var formattedDate = null;
	var date = convertInputToDate(inputDate);

	var months = ["JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"];
	if(date != null) {
		if(asUTC) {
			var day = date.getUTCDate()<10?"0"+String(date.getUTCDate()):String(date.getUTCDate());
		      formattedDate = day + "-" + months[date.getUTCMonth()] + "-" + date.getUTCFullYear();
		} else {
			var day = date.getDate()<10?"0"+String(date.getDate()):String(date.getDate());
			formattedDate = day + "-" + months[date.getMonth()] + "-" + date.getFullYear();
		}
		if(!dateOnly)
			formattedDate += ' ' + date.toLocaleTimeString('en-GB');
	}
	return formattedDate;
}
scope.baw.formatDateIntl = formatDateIntl;

function convertInputToDate(inputDate) {
	var date = null;
	if(inputDate != null) {
		// If inputDate is an actual date or in the ISO8601-compliant date format (i.e. JSON date), just convert easily
		var ISO_8601_FULL = /^\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(\.\d+)?(([+-]\d\d:\d\d)|Z)?$/i
		if(typeof inputDate == 'object' || ISO_8601_FULL.test(inputDate)) {
			date = new Date(inputDate);
		}
		// Handle an inputDate passed in as a String type
		else {
			var datePart = inputDate.split(',')[0];
			var year = datePart.split('/')[2];
			var month = Number(datePart.split('/')[0]);
			// However, because JS new Date() uses the monthIndex, we need to subtract 1
			month--;
			var day = datePart.split('/')[1];

			var timePart = inputDate.split(',')[1].trim();
			var timeOnly = timePart.split(' ')[0];
			var hours = Number(timeOnly.split(':')[0]);
			var minutes = timeOnly.split(':')[1];
			var seconds = timeOnly.split(':')[2];
			var amPm = timePart.split(' ')[1];
			if(amPm == 'PM')
				hours += 12;
			
			date = new Date(year, month, day, hours, minutes, seconds);
		}
	}
	return date;
}
