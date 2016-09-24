/**
 * @fileOverview Powwow commenting bootstrapper code for Ganja sites
 * @author Bodnar Istvan <istvan@gawker.com>
 * @version 0.1
 */

/**
 * Powwow in global namespace
 * @class Powwow
 * @namespace Powwow
 * @public
 */
var Powwow = {};

(function ($) {
	/**
	 * Comment data object
	 * Holds comment url, selected ID, etc
	 * @private
	 */
	var comment_data = {};

	/**
	 * The Powwow iframe loader object
	 * @type jQuery nodeset
	 * @private
	 */
	var $iframe = null;

	/**
	 * Is Powwow loaded?
	 * @type Boolean
	 * @private
	 */
	var is_loaded = false;

	/**
	 * Deferred iframe messages
	 * @type Array
	 * @private
	 */
	var deferred_messages = [];

	/**
	 * Comment list type
	 * @type String
	 * @private
	 */
	var type = null;

	/**
	 * Iframe DOM id
	 * @type String
	 * @private
	 */
	var iframe_id = 'pw-comments';

	/**
	 * Hidden mode
	 * @type Boolean
	 * @private
	 */
	var hidden_mode = false;

	/**
	 * Set debug mode
	 * @type Boolean
	 * @private
	 */
	var debugAssets = false;

	/**
	 * Set mobile mode
	 * @type Boolean
	 * @private
	 */
	var mobile = false;

	/**
	 * Comment loader spinner element
	 * @type jQuery
	 * @private
	 */
	var $spinner = null;

	// use native json or jquery.json
	var stringify = window.JSON ? JSON.stringify : $.toJSON;
	var parse = window.JSON ? JSON.parse : $.evalJSON;

	/**
	 * Init powwow comment iframe
	 * @public
	 */
	Powwow.init = function (options) {
		// reset powwow
		Powwow.reset();
		// set comment type
		type = options.type || 'featured';
		// iframe id
		iframe_id = options.iframe_id || 'pw-comments';
		// options.config
		options.config = options.config || {};
		// set debug mode
		debugAssets = options.config['debug-assets'] === 'true';
		// set mobile mode
		mobile = options.config.mobile === true;
		// hidden mode
		hidden_mode = options.hidden || false;
		// overwrite config options
		Powwow.setConfig(options.config || {});
		// load external css
		Powwow.loadCss(options.css || undefined);
		// start progress indicator spinner
		this.spinner = new Image();
		this.spinner.src = "http://ganja.gawkerassets.com/assets/base.v10/img/spinner_bar.gif";
		this.spinner.className = "spinner";
		if (!hidden_mode) {
			$('.comment-loader').html(this.spinner);
		}
		// load comments
		switch (type) {
		case 'forum':
			Powwow.loadForum(options);
			break;
		case 'profile':
			Powwow.loadProfile(options);
			break;
		case 'domaincomments':
			Powwow.loadDomainComments(options);
			break;
		case 'interview':
			Powwow.loadInterview(options);
			break;
		case 'admin':
			Powwow.loadAdmin(options);
			break;
		default:
			if (!mobile) {
				// on desktop site use featured node browser
				Powwow.loadFeaturedNode(options);
			} else {
				// on mobile use the old featured panels
				Powwow.loadFeatured(options);
			}
			break;
		}

		// throttle scrolling events
		if ($.debounce !== undefined) {
			$(window).scroll($.debounce(100, Powwow.onScroll));
		}

	};

	Powwow.onScroll = function() {
		sendMessage({
			type: 'scroll',
			scrollTop: $(window).scrollTop() - getIframe().offset().top,
			scrollBottom: $(window).scrollTop() + $(window).height()
				- getIframe().offset().top - getIframe().height()
		});
	};

	/**
	 * Powwow.loadFeatured loads featured comments for the given url or actual url
	 * @param {Object} [params] parameters passed to api request
	 * @param {String} [params.url=window.location.href] the URL passed to Powwow to load comments
	 * @param {Number} [params.comment_id] the selected comment id to show
	 * @public
	 */
	Powwow.loadFeatured = function (options) {
		// comment content url
		var params = options.params;
		var config = options.config;
		comment_data.url = params.url || window.location.href;
		// comment id
		comment_data.id = parseInt(params.comment_id, 10) || null;
		// load forum comments
		comment_data.mode = 'featured';
		comment_data.domain = config.domain;
		comment_data.host = config.host;
		if (typeof params.author !== 'undefined') {
			comment_data.author = params.author;
		}

		if (typeof params.inviteToken !== 'undefined') {
			comment_data.inviteToken = params.inviteToken;
		}

		// powwow iframe src
		loadIframe();
	};

	/**
	 * Powwow.loadFeaturedNode loads featured node for the given url or actual url
	 * @param {Object} [params] parameters passed to api request
	 * @param {String} [params.url=window.location.href] the URL passed to Powwow to load comments
	 * @param {Number} [params.comment_id] the selected comment id to show
	 * @public
	 */
	Powwow.loadFeaturedNode = function (options) {
		// comment content url
		var params = options.params;
		var config = options.config;
		comment_data.url = params.url || window.location.href;
		// comment id
		comment_data.id = parseInt(params.comment_id, 10) || null;
		// load forum comments
		comment_data.mode = 'featurednode';
		comment_data.domain = config.domain;
		comment_data.host = config.host;
		if (typeof params.author !== 'undefined') {
			comment_data.author = params.author;
		}

		if (typeof params.inviteToken !== 'undefined') {
			comment_data.inviteToken = params.inviteToken;
		}

		// powwow iframe src
		loadIframe();
	};

	/**
	 * Powwow.loadInterview loads interview app for the given url or actual url
	 * @param {Object} [params] parameters passed to api request
	 * @param {String} [params.url=window.location.href] the URL passed to Powwow to load comments
	 * @public
	 */
	Powwow.loadInterview = function (options) {
		// comment content url
		var params = options.params;
		var config = options.config;
		comment_data.url = params.url || window.location.href;
		// comment id
		comment_data.id = parseInt(params.comment_id, 10) || null;
		comment_data.closed = params.closed;
		// load forum comments
		comment_data.mode = 'interview';
		comment_data.domain = config.domain;
		comment_data.host = config.host;

		// powwow iframe src
		loadIframe();
	};

	/**
	 * Powwow.loadAdmin loads admin app for the given url or actual url
	 * @param {Object} [params] parameters passed to api request
	 * @param {String} [params.url=window.location.href] the URL passed to Powwow to load comments
	 * @public
	 */
	Powwow.loadAdmin = function (options) {
		// comment content url
		var params = options.params;
		var config = options.config;
		comment_data.url = params.url || null; //window.location.href;
		// comment id
		comment_data.id = parseInt(params.comment_id, 10) || null;
		comment_data.closed = params.closed;
		// load forum comments
		comment_data.mode = 'admin';
		comment_data.domain = config.domain;
		comment_data.host = config.host;

		// powwow iframe src
		loadIframe();
	};

	/**
	 * Powwow.loadForum loads comments for the selected forum tag
	 * @param {Object} [params] parameters passed to api request
	 * @param {String} [params.url] the forum page url passed to Powwow to load comments
	 * @public
	 */
	Powwow.loadForum = function (options) {
		// forum url
		var params = options.params;
		var config = options.config;
		comment_data.url = params.url || window.location.href;
		// comment id
		comment_data.id = parseInt(params.comment_id, 10) || null;
		// load forum comments
		comment_data.mode = 'forum';
		comment_data.domain = config.domain;
		comment_data.host = config.host;

		// powwow iframe src
		loadIframe();
	};

	/**
	 * Powwow.loadDomainComments loads comments for the selected domain (/comments page)
	 * @param {Object} [params] parameters passed to api request
	 * @param {String} [params.url] the url of the site, domain id will be calculated on serverside
	 * @public
	 */
	Powwow.loadDomainComments = function (options) {
		var params = options.params;
		var config = options.config;
		// forum url
		comment_data.url = params.url || window.location.href;
		// load domain comments
		comment_data.mode = 'domaincomments';
		comment_data.domain = config.domain;
		comment_data.host = config.host;

		// powwow iframe src
		loadIframe();
	};

	/**
	 * Powwow.loadProfile loads profile panels for selected user
	 * @param {Object} [params] parameters passed to api request
	 * @param {String} [params.url] the forum page url passed to Powwow to load comments
	 * @public
	 */
	Powwow.loadProfile = function(options) {
		var config = options.config;
		comment_data.url = window.location.href;
		comment_data.domain = config.domain;
		comment_data.host = config.host;
		comment_data.profile_type = (window.location.pathname.indexOf('/me') === 0 ? 'private' : 'public');
		comment_data.mode = 'profile';
		loadIframe();
	};

	/**
	 * Powwow.hide hides comment iframe by setting the src to 'about:blank' and setting
	 * the height to 0
	 * @public
	 */
	Powwow.hide = function () {
		getIframe().css({height: 0}).attr({src: 'about:blank'});
	};

	/**
	 * Powwow.setConfig overwrites api configuration options in Powwow
	 * @param {Object} config Configuration option overrides
	 * @public
	 */
	Powwow.setConfig = function (config) {
		sendMessage({type: 'setConfig', config: config});
	};

	/**
	 * Powwow.loadCss loads a CSS file inside the Powwow iframe
	 * @param {Array} css_urls array of CSS file URLs to load in the Poowow iframe
	 * @public
	 */
	Powwow.loadCss = function (css_urls) {
		if (css_urls !== undefined) {
			if (typeof(css_urls) !== "object") {
				css_urls = [css_urls];
			}
			jQuery.each(css_urls, function (index, css_url) {
				sendMessage({type: 'cssLoad', url: css_url});
			});
		}
	};

	/**
	 * Powwow.loadUser loads user state from SSO
	 * @public
	 */
	Powwow.loadUser = function () {
		sendMessage({type: 'loadUser'});
	};

	/**
	 * Powwow.loadRoot loads root object from powwow db
	 * @public
	 */
	Powwow.loadRoot = function (data) {
		sendMessage({type: 'loadRoot', data: data});
	};

	Powwow.appProxy = function (method, params) {
		sendMessage({type: 'appProxy', data: { method: method, params: params || [] }});
	};
	/**
	 * Powwow.openCommentForm opens the commentform
	 * @public
	 */
	Powwow.openCommentForm = function () {
		sendMessage({type: 'openCommentForm'});
	};

	/**
	 * Powwow.getIframeOffset returns the left and top offset of the iframe
	 * @public
	 */
	Powwow.getIframeOffset = function () {
		return getIframe().offset();
	};

	/**
	 * Powwow.getCommentOffset returns the left and top offset of a specified comment inside the iframe
	 * @public
	 */
	Powwow.getCommentOffset = function (comment_id, callback) {
		if ($.isFunction(callback)) {
			var callback_fn = 'powwow_getCommentOffset_' + Math.floor(Math.random() * 1000000000000);
			window[callback_fn] = callback;
			sendMessage({type: 'getCommentOffset', comment_id: comment_id, callback: callback_fn});
		}

	};

	/**
	 * Powwow.scrollTop scrolls to the top of the iframe
	 * @public
	 */
	Powwow.scrollTop = function (config) {
		sendMessage({type: 'scrollTop'});
	};

	/**
	 * Powwow.reset resets powwow state to default
	 * @public
	 */
	Powwow.reset = function () {
		is_loaded = false;
		comment_data = {};
		deferred_messages = [];
		$iframe = null;
	};

	/**
	 * Returns the Powwow iframe object
	 * @returns {jQuery nodeset} Powwow iframe object
	 * @private
	 */
	function getIframe() {
		return $iframe || $('#' + iframe_id);
	}

	/**
	 * Returns the iframe src attribute to be used in iframe postMessage
	 * Passes the parent page url in the location.hash for older browsers to work
	 * @returns {String} Powwow iframe source URL
	 * @private
	 */
	function getIframeSrc() {
		var suffix = [],
			indexPage = 'conversation.html';

		if (debugAssets) { suffix.push('debug-assets=true'); }
		if (mobile) { suffix.push('mobile=true'); }
		if (type === 'admin') { indexPage = 'admin.html'; }

		return getIframe().data('src') + indexPage + (suffix.length ? '?' + suffix.join('&') : '') + '#' + encodeURIComponent(window.location.href);
	}

	/**
	 * Wrapper for jQuery.postMessage plugin, sends a message to Powwow iframe
	 * Store the message in deferred_messages array if Powwow is not loaded yet,
	 * and run it when Powwow starts up
	 * @param {Object} params
	 * @private
	 */
	function sendMessage(params) {
		if (is_loaded) {
			$.postMessage(stringify(params || {}), getIframeSrc(), getIframe().get(0).contentWindow);
		} else {
			deferred_messages.push(params);
		}
	}

	function getIframeOrigin() {
		var url = getIframe().data('src');
		var match = url.match(/https?:\/\/[^\/]+/i);
		var origin = match.length ? match[0] : null;
		return origin;
	}

	/**
	 * Callback to handle iframe postMessage (MessageEvent) events
	 */
	function onMessage(e) {
		var message_data = (e.data !== undefined) ? parse(e.data) : {};

		// passthrough if not a message for us
		if (message_data === undefined) {
			return true;
		}

		// stop event propagation if message was catched
		var pass_event = true,
			i;

		// trigger an old-school widget event
		if (typeof(GawkerClientside) !== "undefined" && typeof(GawkerClientside.widgets.notificationControl) !== "undefined") {
			if (message_data.type !== undefined) {
				GawkerClientside.widgets.notificationControl[0].raiseEvent('powwow.' + message_data.type, message_data);
			}
		}

		switch (message_data.type) {

		// handle iframe size change messages
		// if hidden mode setting is true, iframe should remain hidden, so we don't update the iframes height
		case 'iframeUpdated':
			if (!hidden_mode) {
				getIframe().css({height: message_data.height});
			}
			pass_event = false;
			break;

		// handle powwow app loaded messages
		case 'powwowLoaded':
			// hide spinner
			$('#comments .spinner').remove();

			// set is_loaded to true and run deferred calls
			is_loaded = true;
			// send deferred messages to powwow before loading comments
			if (deferred_messages.length > 0) {
				for (i in deferred_messages) {
					if (deferred_messages.hasOwnProperty(i)) {
						sendMessage(deferred_messages[i]);
					}
				}
				deferred_messages = [];
			}
			// load comments
			sendMessage({type: 'loadComments', data: comment_data});
			pass_event = false;
			break;

		// handle external link messages
		case 'gotoLink':
			if (message_data.url !== undefined) {
				window.location.href = message_data.url;
			}
			pass_event = false;
			break;

		// handle iframe takeover clicks
		case 'takeoverClickWatch':
			if (message_data.action === 'start') {
				$(window).click(
					function (e) {
						sendMessage({type: 'removeOverlay'});
					}
				);
			} else if (message_data.action === 'stop') {
				$(window).unbind("click");
			}
			pass_event = false;
			break;

		// scroll to top
		case 'scrollTop':
			jQuery.scrollTo('#comments');
			pass_event = false;
			break;

		// display email invite dialog
		case 'inviteRequest':
			/**
			 * Raise a "inviteRequest" event on parent window to start up email invite panel
			 */
			$('body').trigger('inviteRequest', [message_data.comment, message_data.rootUrl, message_data.rootTitle]);
			pass_event = false;
			break;

		// start guest panel
		case 'loginRequired':
			/**
			 * Raise a "loginRequest" event on parent window to start up presence login panel
			 */
			$('body').trigger('loginRequest', {});
			pass_event = false;
			break;

		// handle image load events
		case 'showImage':
			// open lytebox and store the reference to overlay object
			Powwow.lytebox_overlay = jQuery('#comments').lytebox('display', {url: message_data.url});
			pass_event = false;
			break;

		// handle image close events
		case 'closeImage':
			// close existing lytebox overlay
			if (Powwow.lytebox_overlay !== undefined) {
				jQuery('#comments').lytebox('destroy', {overlay: Powwow.lytebox_overlay});
				Powwow.lytebox_overlay = null;
			}
			pass_event = false;
			break;

		// handle video overlay
		case 'showVideo':
			var video_overlay = null;
			var video_element = null;
			var removeVideo = function () {
				// undelegate and remove
				$(document).undelegate('body', 'keydown', keyDownHandler);
				video_element.undelegate('.glinside, .glclose', 'click', removeVideo);
				if (video_overlay !== null) {
					video_overlay.remove();
					video_overlay = null;
				}
				if (video_element !== null) {
					video_element.remove();
					video_element = null;
				}
				$('object, embed, iframe').css('visibility', '');
			};
			var keyDownHandler = function (e) {
				if (e.keyCode === 27 || e.keyCode === 67 || e.keyCode === 88) {
					removeVideo();
					return false;
				}
			};

			// prepare elements to add
			if (video_overlay === null) {
				video_overlay = $('<div id="glOverlay" style="opacity: 0.6;"></div>');
			}
			if (video_element === null) {
				var video_top = Math.floor(window.innerHeight - 360) / 2;
				if (video_top < 0) video_top = 0;
				video_element = $('<div id="glBox"><div class="glinside"><span class="glicon glclose"></span><div class="glimagewrapper" style="position:relative; top:' + video_top + 'px">' + message_data.url + '</div></div></div>');
			}
			// hide flash objects
			$('object, embed, iframe').css('visibility', 'hidden');

			// add elements to DOM and add close handlers
			video_overlay.appendTo($('body')).fadeIn(100, function () {
				video_element.appendTo($('body'));
				video_element.delegate('.glinside, .glclose', 'click', removeVideo);
			});
			window.focus();
			$(document).delegate('body', 'keydown', keyDownHandler);

			pass_event = false;
			break;

		// handle comment offset replies
		case 'commentOffset':
			if (message_data.callback && $.isFunction(window[message_data.callback])) {
				window[message_data.callback](message_data.offset);
				delete window[message_data.callback];
			}
			pass_event = false;
			break;

		// handle comment scrollto event
		case 'scrollToComment':
			window.setTimeout(function() {
				if (message_data.id !== undefined) {
					Powwow.getCommentOffset(message_data.id, function (offset) {
						if (offset) {
							var top = Powwow.getIframeOffset().top + offset.top - 60;
							jQuery(document).stop().scrollTo(top, {
								duration: message_data.scroll_time || 125,
								easing: message_data.easing || 'linear'
							});
						}
					});
				}
			}, message_data.timeout || 1000);
			pass_event = false;
			break;

		// handle comment scrollto event
		case 'scrollToOffset':
				var target = null;
				if (typeof message_data !== undefined) {
					// scroll window only when the replyform would go outside of our viewport
					if (message_data.isreply) {
						var formTop = Powwow.getIframeOffset().top + message_data.offset.top;
						var viewPortBottom = window.pageYOffset + window.innerHeight;
						if (viewPortBottom < formTop + 142) {
							target = formTop - window.innerHeight + 150;
						}
					} else {
						//scroll to element
						target = Powwow.getIframeOffset().top + message_data.offset.top;
					}
				}
				if (target !== null) {
					jQuery(document).stop().scrollTo(target, 125);
				}

			pass_event = false;
			break;

		// handle permalink change events
		case 'setUrl':
			if (typeof history.pushState !== 'undefined' && message_data.url !== undefined) {
				if (message_data.mode === 'redirect') {
					// when empty get rid of commment parameter only, keep everything else
					window.location.href = (message_data.url === '') ?  window.location.pathname : message_data.url;
				} else {
					var url = '';
					var leftover = jQuery.map(window.location.search.substring(1).split('&'), function (e) {
						return (e.indexOf('comment=') !== -1 || e.indexOf('post=') !== -1) ? '': e;
					});
					// filter empty values from array
					var urlparts = [];
					for (var i in leftover) {
						if (leftover.hasOwnProperty(i) && leftover[i] !== '') {
							urlparts.push(leftover[i]);
						}
					}
					// create url to set
					if (message_data.url !== '') {
						urlparts.push((message_data.url.indexOf('?') === 0) ? message_data.url.substring(1) : message_data.url);
					} else {
						url = window.location.pathname;
					}
					// re-append existing parameters
					if (urlparts.length > 0) {
						url = url + '?' + urlparts.join('&');
					}
					if (message_data.mode === 'push') {
						history.pushState(url, document.title, url);
					} else {
						history.replaceState(url, document.title, url);
					}
				}
			}
			pass_event = false;
			break;

        // track GA datas comes from iframe (twitter/fb/url share, bottom link click)
        case 'social_track':
            if (typeof _gaq !== 'undefined') { _gaq.push(['_trackEvent', message_data.cat, message_data.action]); }

        case 'chartbeat':
			var _cbq = window._cbq || [];
            if (typeof _cbq !== 'undefined') { 
				_cbq.push([message_data.cat, message_data.action]); 
			}
            break;

		case 'adminErrorMessage':
			if (typeof showError === "function") {
				showError(message_data.message || "Powwow error. Please, try again");
			}
			break;

		// remove site recommending module
		case 'removeSitesModule':
			jQuery('#sitetags').remove();
			break;
		}

		// pass event if needed
		return pass_event;


	}

	function loadIframe() {
		$.receiveMessage(onMessage, getIframeOrigin());
		getIframe().attr('src', getIframeSrc());
	}

	/**
	 * Ganja login and logout event handler
	 */
	$(window).bind('user.auth.success user.auth.failure', Powwow.loadUser);

	/**
	 * Popstate change event
	 * Add an initial timeout, because Webkit based browsers fire an empty popstate event right after the load event
	 * and there's no way to differentiate this initial event from a user initiated one
	 */
	window.addEventListener('load', function() {
		setTimeout(function() {
			$(window).bind('popstate', function (e) {
				var original_event = e.originalEvent;
				sendMessage({
					type: original_event.type,
					state: original_event.state,
					timeStamp: original_event.timeStamp
				});
			});
		}, 100);
	});
	

}(jQuery));
