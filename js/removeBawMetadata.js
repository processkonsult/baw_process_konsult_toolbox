function removeBawMetadata(obj) {
	if(obj) {
		// First check if this object is a BAW list and convert if needed
		if(obj && obj.hasOwnProperty("items")) {
			delete obj["selected"];
			obj = obj["items"];
		}

		// Remove any of the following BAW-specific properties
		delete obj["@metadata"];
		delete obj.childrenCache;
		delete obj._inherited;
		delete obj._objectPath;
		delete obj._systemCallbackHandle;
		delete obj._watchCallbacks;

		// Check each property for nested lists of objects
		for(var prop in obj) {
			if(typeof obj[prop] == 'object') {
				var childObj = obj[prop];
				// Check this object for 'items' attribute - which indicates a list - and move list items to parent level
				if(childObj && childObj.hasOwnProperty("items")) {
					delete childObj["selected"];
					obj[prop] = childObj["items"];
				}
				removeBawMetadata(obj[prop]);
			}
		}
	}
	return obj;
}
