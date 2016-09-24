(function() {
    if (typeof window.janrain.engage !== "object") window.janrain.engage = {};
    if (!janrain.settings.capture) janrain.settings.capture = {};
    if (!janrain.settings.common) janrain.settings.common = {};
    if (!janrain.settings.language) janrain.settings.language = 'en';
    //if (!janrain.settings.providers) janrain.settings.providers = ["aol","google","yahoo","openid"];
    if (!janrain.settings.packages) {
        janrain.settings.packages = ['login'];
    } else {
        if (janrain.settings.tokenUrl) janrain.settings.packages.push('login');
    }
    if (!janrain.settings.publish) janrain.settings.publish = {};
    if (!janrain.settings.share) janrain.settings.share = {};
    if (!janrain.settings.simpleshare) janrain.settings.simpleshare = {};
    if (!janrain.loadedPackages) janrain.loadedPackages = [];
    if (!janrain.settings.linkClass) janrain.settings.linkClass = 'janrainEngage';

	if (typeof janrain.settings.common.appUrl === 'undefined')janrain.settings.common.appUrl = 'https://login.slashdot.org';
    
	if (typeof janrain.settings.showAttribution === 'undefined')janrain.settings.showAttribution = false;
    if (typeof janrain.settings.type === 'undefined')janrain.settings.type = 'embed';
    if (typeof janrain.settings.format === 'undefined')janrain.settings.format = 'one column';
    if (typeof janrain.settings.width === 'undefined')janrain.settings.width = '308';
    if (typeof janrain.settings.providersPerPage === 'undefined')janrain.settings.providersPerPage = '3';
    if (!janrain.settings.actionText)janrain.settings.actionText = 'Sign in using your account with';
    if (typeof janrain.settings.fontColor === 'undefined')janrain.settings.fontColor = '#666666';
    if (typeof janrain.settings.fontFamily === 'undefined')janrain.settings.fontFamily = 'lucida grande, Helvetica, Verdana, sans-serif';
    if (typeof janrain.settings.backgroundColor === 'undefined')janrain.settings.backgroundColor = '#ffffff';
    if (typeof janrain.settings.buttonBorderColor === 'undefined')janrain.settings.buttonBorderColor = '#CCCCCC';
    if (typeof janrain.settings.buttonBorderRadius === 'undefined')janrain.settings.buttonBorderRadius = '5';
    if (typeof janrain.settings.buttonBackgroundStyle === 'undefined')janrain.settings.buttonBackgroundStyle = 'gradient';
    if (typeof janrain.settings.borderWidth === 'undefined')janrain.settings.borderWidth = '15';
    if (typeof janrain.settings.borderColor === 'undefined')janrain.settings.borderColor = '#C0C0C0';
    if (typeof janrain.settings.borderRadius === 'undefined')janrain.settings.borderRadius = '5';
    if (typeof janrain.settings.appId === 'undefined')janrain.settings.appId = 'ggidemlconlmjciiohla';
    if (typeof janrain.settings.appUrl === 'undefined')janrain.settings.appUrl = 'https://login.slashdot.org';
    janrain.settings.permissions = ["customizable_auth_widget_hide_attribution","customizable_auth_widget_styling"];
    if (typeof janrain.settings.providers === 'undefined')janrain.settings.providers = ["google","twitter","facebook"];
    if (typeof janrain.settings.noReturnExperience === 'undefined')janrain.settings.noReturnExperience = false;
    if (typeof janrain.settings.facebookAppId === 'undefined')janrain.settings.facebookAppId = '289506211170358';
    
	if (typeof janrain.settings.share.attributionDisplay === 'undefined')janrain.settings.share.attributionDisplay = true;
    if (typeof janrain.settings.share.elementColor === 'undefined')janrain.settings.share.elementColor = '#333333';
    if (typeof janrain.settings.share.elementHoverBackgroundColor === 'undefined')janrain.settings.share.elementHoverBackgroundColor = '#eeeeee';
    if (typeof janrain.settings.share.elementButtonBorderRadius === 'undefined')janrain.settings.share.elementButtonBorderRadius = '6';
    if (typeof janrain.settings.share.elementBorderColor === 'undefined')janrain.settings.share.elementBorderColor = '#cccccc';
    if (typeof janrain.settings.share.elementBackgroundColor === 'undefined')janrain.settings.share.elementBackgroundColor = '#f6f6f6';
    if (typeof janrain.settings.share.elementLinkColor === 'undefined')janrain.settings.share.elementLinkColor = '#009DDC';
    if (typeof janrain.settings.share.elementBorderRadius === 'undefined')janrain.settings.share.elementBorderRadius = '3';
    if (typeof janrain.settings.share.elementButtonBoxShadow === 'undefined')janrain.settings.share.elementButtonBoxShadow = '3';
    if (typeof janrain.settings.share.modalOpacity === 'undefined')janrain.settings.share.modalOpacity = '0.5';
    if (typeof janrain.settings.share.modalBorderRadius === 'undefined')janrain.settings.share.modalBorderRadius = '5';
    if (typeof janrain.settings.share.bodyColor === 'undefined')janrain.settings.share.bodyColor = '#333333';
    if (typeof janrain.settings.share.bodyTabBackgroundColor === 'undefined')janrain.settings.share.bodyTabBackgroundColor = '#f8f8f8';
    if (typeof janrain.settings.share.bodyTabColor === 'undefined')janrain.settings.share.bodyTabColor = '#000000';
    if (typeof janrain.settings.share.bodyContentBackgroundColor === 'undefined')janrain.settings.share.bodyContentBackgroundColor = '#ffffff';
    if (typeof janrain.settings.share.bodyBackgroundColorOverride === 'undefined')janrain.settings.share.bodyBackgroundColorOverride = false;
    if (typeof janrain.settings.share.bodyFontFamily === 'undefined')janrain.settings.share.bodyFontFamily = 'Helvetica';
    if (typeof janrain.settings.share.bodyBackgroundColor === 'undefined')janrain.settings.share.bodyBackgroundColor = '#009DDC';
    if (typeof janrain.settings.share.modalBackgroundColor === 'undefined')janrain.settings.share.modalBackgroundColor = '#000000';
    if (typeof janrain.settings.share.appUrl === 'undefined')janrain.settings.share.appUrl = 'https://login.slashdot.org';
    janrain.settings.share.permissions = ["customizable_share_widget_hide_attribution","customizable_share_widget_styling","customizable_share_widget_contact_mode"];
    if (typeof janrain.settings.share.providers === 'undefined')janrain.settings.share.providers = [];
    if (typeof janrain.settings.share.providersEmail === 'undefined')janrain.settings.share.providersEmail = [];
    if (typeof janrain.settings.share.modes === 'undefined')janrain.settings.share.modes = ["broadcast"];
    
	

    /*
*  _scriptLoader
*
*  Loads script dynamically and allows for callbacks and a timeout.
*
*  @return {Object} Public methods for _loadDyanmicScript
*  @private
*/
function _scriptLoader(src, callback) {
    var _callback = callback,
        _timeout = 200,
        _useTimeout = false,
        _timeoutCallback,
        _pollCount = 0,
        _pollTimeout, 
        _script = document.createElement('script'),
        _firstScript = document.getElementsByTagName('script')[0],
        _finished = false;

    _script.src = src;
    _script.setAttribute('type', 'text/javascript');

    _script.onload = _script.onerror = _script.onreadystatechange = function(event) {
        if (!_finished && (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
            _finish(event);
        }
    }

    function _load() {
        _firstScript.parentNode.insertBefore(_script, _firstScript);
        if (_useTimeout) _pollLoad();
    }

    function _finish(event) {
        _finished = true;
        if (typeof _pollTimeout !== 'undefined') {
            clearTimeout(_pollTimeout);
        }
        // event is a string when loading a script fails for any reason.
        if (typeof event === 'string') {
            if (typeof _timeoutCallback === 'function') _timeoutCallback(event);
            return true;
        }
        if (typeof event === 'object' || typeof event === 'undefined') {
            if (typeof event === 'object' && event.type === 'error') {
                if (typeof _timeoutCallback === 'function') _timeoutCallback(event);
            } else {
                if (typeof _callback === 'function') _callback();
            }
            return true;
        }
    }

    function _pollLoad() {
        _pollCount++;
        if (_finished) return true;
        if (_pollCount < _timeout) {
            _pollTimeout = setTimeout(_pollLoad, 50);
        } else {
            _finish("Load Timeout Error");
        }
    }

    return {
        setTimeoutCallback: function(callback) {
            _useTimeout = true;
            _timeoutCallback = callback;
            return this;
        },
        setCallback: function(callback) {
            _callback = callback;
            return this;
        },
        setTimeoutLimit: function(time) {
            _timeout = time;
            return this;
        },
        load: function() {
            _load();
        }
    }
}

function _loadDynamicScript(src, callback) {
    _scriptLoader(src, callback).load();
}

    function getPackagePath(packages) {
        var rootPath = document.location.protocol === 'https:' ? "https://d29usylhdk1xyu.cloudfront.net/" : "http://widget-cdn.rpxnow.com/";
        var path = rootPath + 'manifest/' + packages.join(':') + '?version=' + encodeURIComponent('2013.1_ws_widgets_rc8');
        return path;
    }
    function getTranslationPath(language, widget) {
        var rootPath = document.location.protocol === 'https:' ? "https://d29usylhdk1xyu.cloudfront.net/" : "http://widget-cdn.rpxnow.com/";
        var path = rootPath + 'translations/' + widget + '/' + language;
        return path;
    }
    function loadPackages(loaded, packages) {
        if (packages.length === 0) return false;
        if (loaded === packages.length) {
            var widgetPath = getPackagePath(packages);
            _loadDynamicScript(widgetPath);
        } else {
            if (!inArray(janrain.loadedPackages, packages[loaded])) {
                janrain.loadedPackages.push(packages[loaded]);
                if ((packages[loaded] === "login"&& janrain.settings.language === "en")
                    || packages[loaded] === "capture"
                    || packages[loaded] === "simpleshare") {
                    loadPackages(loaded + 1, packages);
                } else {
                    _loadDynamicScript(getTranslationPath(janrain.settings.language, packages[loaded]), function() {
                        loadPackages(loaded + 1, packages);
                    });
                }
            } else {
                deleteItemFromArray(packages, loaded);
                loadPackages(loaded, packages);
            }
        }
    }
    function arrayToObject(array) {
        var uniqueObject = {};
        for (var i = 0, l = array.length; i < l; i++) {
            uniqueObject[array[i]] = array[i];
        }
        return uniqueObject;
    }
    function deleteItemFromArray(array, from, to) {
        var rest = array.slice((to || from) + 1 || array.length);
        array.length = from < 0 ? array.length + from : from;
        return array.push.apply(array, rest);
    }
    function inArray(array, item) {
        var arrayObject = arrayToObject(array);
        return arrayObject.hasOwnProperty(item);
    }
    function unique(array){
        var uniqueObject = arrayToObject(array);
        var unique = [];
        for (var key in uniqueObject){
            if (uniqueObject.hasOwnProperty(key)) unique.push(key);
        }
        return unique;
    }

    janrain.settings.packages = unique(janrain.settings.packages);
    janrain.settings.packages.sort();
    loadPackages(0, janrain.settings.packages);
})();
