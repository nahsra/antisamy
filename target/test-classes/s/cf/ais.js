var cnnad_transactionID = null;
function cnnad_getTransactionID() {
	if (cnnad_transactionID == null) {
		cnnad_transactionID = "";
		cnnad_transactionID = Math.floor(Math.random()*9007199254740992);
	}
	return cnnad_transactionID;
}
function cnnad_readCookie( name ) {
        if ( document.cookie == '' ) { // there is no cookie, so go no further
		return null;
        } else { // there is a cookie
		var ca = document.cookie.split(';');
		var nameEQ = name + "=";
		for(var i=0; i < ca.length; i++) {
			var c = ca[i];
			while (c.charAt(0)==' ') c = c.substring(1, c.length); //delete spaces
			if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
		}
		return null;
        }
}

function cnnad_haveCookie(a){return document.cookie&&(-1<document.cookie.indexOf("; "+a+"=")||document.cookie.indexOf(a+"=")==0);}

function cnnad_ugsync() {
        if (!cnnad_haveCookie('ugs')) {
		document.write('<scr'+'ipt src="http://www.ugdt'+'urner.com/xd.sjs"></scr'+'ipt>');
        }
}

cnnad_getTransactionID();
cnnad_ugsync();
