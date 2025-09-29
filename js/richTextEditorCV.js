function getTextFromRichTextEditorCV(cv) {
	var iframe = cv.context.element.querySelector('iframe');
	var textEditor = iframe.contentDocument.querySelector('.mce-content-body');
	return textEditor.innerHTML;
}

function setTextFromRichTextEditorCV(cv, text) {
	var iframe = cv.context.element.querySelector('iframe');
	var textEditor = iframe.contentDocument.querySelector('.mce-content-body');
	textEditor.textContent = text;
}
