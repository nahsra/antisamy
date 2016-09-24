	if( location.hash != '' && location.hash.indexOf( '#!' ) == 0 ) {
		var newHref = location.hash.substr( 2 );
		if( newHref.indexOf( '/' ) != 0 ) {
			newHref = '/' + newHref;
		}
		location.href = newHref;
	}

	try { Typekit.load(); } catch(e) {}



	(function(){
        var thirdpartyDisabled = gawker_parseQueryString(window.location.search.substring(1)).thirdparty === 'false';

		var setupWidgets = function() {
			
			jQuery( '#presence_external_templates' ).load( '/static/items/' + siteConfig['canonicalHost'] + '/presence_external_templates_static.html', function() {
				if (adRobot.feature.isOn(adRobot.feature.POWWOW)) {
					GawkerClientside.pushWidget('userwidget', jQuery('body').UserWidget().data('UserWidget'));
				} else {
					GawkerClientside.pushWidget('sandboxwidget', jQuery('body').SandboxWidget().data('SandboxWidget'));
				}
				presence_options = { isReloading : false };
				GawkerClientside.pushWidget('presence', jQuery('#auth').PresenceWidget(presence_options).data('PresenceWidget'));
				if ( jQuery( '#rightbar_pin_container' ).length ) {
					GawkerClientside.pushWidget('viewSelector', jQuery('#rightbar_pin_container').ViewSelectorWidget().data('ViewSelectorWidget'));
				}
				GawkerClientside.pushWidget('notificationControl', jQuery('#notifications').NotificationWidget().data('NotificationWidget'));
				GawkerClientside.pushWidget('commenter', jQuery('.gawkerwidget.commenter_area').CommenterWidget({}).data('CommenterWidget'));
				var comments = jQuery('#comments');
				if ( comments.length || jQuery('.comment').length > 0) {
					GawkerClientside.pushWidget('thread', comments.ThreadWidget( {} ).data('ThreadWidget'));
					GawkerClientside.pushWidget('threadadmin', jQuery('.gawkerwidget.threadadmin_area').ThreadAdminWidget({}).data('ThreadAdminWidget'));
					GawkerClientside.pushWidget('commenteradmin', jQuery('.gawkerwidget.commenteradmin_area').CommenterAdminWidget({}).data('CommenterAdminWidget'));
					GawkerClientside.pushWidget('commentform', jQuery('.commentform_container #postform_widget').CommentFormWidget().data('CommentFormWidget'));
				}
				var shareEmail = jQuery('.share_email');
				if ( shareEmail.length > 0 ) {
					GawkerClientside.widgets.simpleform_mail2 = [ shareEmail.SimpleFormWidget(settings.widgets.simpleform.byselector['#share_email'].options).data('SimpleFormWidget') ];
				}
				var inviteControl = jQuery('.cn_invite_dialog');
				if ( inviteControl.length > 0 ) {
					GawkerClientside.widgets.simpleform_invite = [ inviteControl.SimpleFormWidget(settings.widgets.simpleform.byselector['#invitecontainer'].options).data('SimpleFormWidget') ];
				}
				GawkerClientside.pushWidget('editorcontrols', jQuery('.gawkerwidget.editorcontrols_area').EditorControlsWidget().data('EditorControlsWidget'));
				GawkerClientside.pushWidget('republishtool', jQuery('.gawkerwidget.republish_area').RepublishToolWidget( republishOptions ).data('EditorControlsWidget'));
				GawkerClientside.pushWidget('roundupsnippet', jQuery('.gawkerwidget.roundup_area').RoundupSnippetWidget().data('EditorControlsWidget'));

				/** HACK WARNING
				 *
				 * widgets in static view are loaded in a funky way. if the forumloaded event is raised BEFORE
				 * the subscribing widgets are initialized, registerEventHandler will not work, meh.
				 */
				if (typeof forum_load_options !== 'undefined' && forum_load_options !== undefined) {
					adRobot.raiseEvent('maincontent.forumloaded', forum_load_options);
				}
			} );

		};

		var setupMainContent = function() {
			//force center aligned twitter embeds
			jQuery('.twitter-tweet').addClass('tw-align-center');

			if ( window.pageType == 'post' ) {
				adRobot.raiseEvent('maincontent.postloaded', {
					data: {
						id: window.post.id,
						post: window.post,
						action: 'post',
						originalPostId: window.post.originalPostId,
						tags: window.post.tags,
						sponsored: window.post.sponsored
					}
				});
				jQuery('#rbpost_' + window.post.id).addClass('current');
			}
			if ( window.pageType == 'tagpage' ) {
				adRobot.raiseEvent('maincontent.tagpageloaded', {
					data: {
						id: window.post.id,
						action: 'tag',
						originalPostId: window.post.originalPostId
					}
				});
			}

			if ( window.pageType === 'static_commenter_flow' ) {
				adRobot.raiseEvent('maincontent.profileloaded', {
					data: {
					}
				});
			}
			if ( window.pageType == 'search' )
			{
				jQuery('#search_term').focus( function( e ) {
					o = jQuery( this );
					if ( o.hasClass('empty') ) {
						o.val('');
						o.removeClass('empty');
					}
				});
				jQuery('#search_term').focusout( function( e ) {
					o = jQuery( this );
					if ( o.val() == '' ) {
						o.val('Search');
						o.addClass('empty');
					}
				});
				jQuery('#search #settings ul li a').click( function( e ) {
					e.stopPropagation();
					e.preventDefault();
					o = jQuery( this );
					setting = o.parent().parent().attr('id');
					option = o.parent().attr('id');
					jQuery('#search input[name=' + setting + ']').val( option );
					jQuery( '#search #settings ul#' + setting + ' li' ).removeClass('selected');
					o.parent().addClass('selected');
					q = jQuery('#search_term');
					if ( !q.hasClass('empty') && q.val() != '' ) {
						jQuery('#search').submit();
					}
				} );
			}

			if ( jQuery('.splashposts').length ) {
				GawkerClientside.pushWidget('splashpostControl', jQuery('.splashposts').eq(0).SplashPostWidget( { static_version : true } ).data('SplashPostWidget'));
				var urlParams = adRobot.getUrlParams();
				var sponsored_params = {};
				if (urlParams['sponsor_preview'] !== undefined) {
					sponsored_params.sponsor_preview = parseInt(urlParams['sponsor_preview'], 10);
				}
				adRobot.raiseEvent( 'splashpost.sponsoredpost.show', sponsored_params );
			}

			if ( jQuery( '#post_list' ).length ) {
				GawkerClientside.pushWidget( 'minirightbar', jQuery('#post_list').MiniRightbarWidget({}).data('MiniRightbarWidget'));
			}

			//google analytics event tracker support
			jQuery('.frontpage .gaqtrack').bind('click', function(){
				_gaq.push(['_trackEvent', jQuery(this).data('gaqevent'), jQuery(this).data('gaqid') ]);
			});
			jQuery('.author-contact .gaqtrack').bind('click', function(){
				_gaq.push(['_trackEvent', jQuery(this).data('gaqevent'), jQuery(this).data('gaqid') ]);
			});
		};

		var setupFacebook = function() {
			if(!thirdpartyDisabled) {
				window.fbAsyncInit = function() {
					FB.init({ 'appId': '236575159691634', status: true, cookie: true,xfbml: true } );
					var fbObject = document.getElementById( 'facebook_like' );
					if( fbObject !== null ) {
						window.FB.XFBML.parse( fbObject );
					}
				};
				gawker_getScript('//connect.facebook.net/en_US/all.js');
			}
		};

		var addBlogviewHandler = function() {
			jQuery( '#switch_blogview' ).click( function( e ) {
				var parts = location.hostname.match( /([^\.]+\.[^\.]+)$/ );
				var cookieValue = 'classic';
				var newUrl = 'http://' + ( location.hostname.indexOf( 'blog.' ) == -1 ? 'blog.' : '' ) + location.hostname + '/';
				if( window.pageType == 'classic_frontpage' ) {
					cookieValue = 'top7';
					newUrl = 'http://' + location.hostname.replace( 'blog.', '' ) + '/';
				}
				jQuery.cookie('____GCV', cookieValue, {expires: 365, path: '/', domain: parts[1]});
				location.href = newUrl;
				e.stopPropagation();
				e.preventDefault();
			} );
			jQuery( '#switch_blogview' ).attr('title', 'Switch to top stories view' );
		};

		var initTrackers = function() {
			var tracker_params = {};
			if( window.originalPostId !== undefined ) {
				tracker_params.post_id = window.originalPostId;
			}
			else if (window.postId !== undefined) {
				tracker_params.post_id = window.postId;
			} else {
				tracker_params.post_id = 'SITE:' + siteConfig.id;
			}
			trackers.reload(tracker_params);
		};

		var setupShareMenus = function() {
			if ( jQuery('.share').length ) {
				var fade;

				jQuery('.share_button').click( function( e ) {
					e.stopPropagation();
					e.preventDefault();
					var shareMenu = jQuery( e.target ).next( '.share_menu' );
					shareMenu.fadeIn(100);
					shareMenu.bind( "clickoutside", function() { shareMenu.hide(); } );
					enableGooglePlusButton(shareMenu);
				});
				jQuery('.share_menu .heading span').click( function( e ) {
					e.stopPropagation();
					e.preventDefault();
					jQuery( e.target ).parents( '.share_menu' ).hide();
				});
			}

		};

		var enableGooglePlusButton = function(shareMenu) {
			var gplusDiv = shareMenu.find('.gplus');
			var gplusUrl = gplusDiv.data('href');
			var gplusEnabled = (gplusDiv.data('on') == 'true');
			if((gplusDiv.length == 1) && gplusUrl && !gplusEnabled) {
				gplusDiv.html('<g:plusone size="medium" href="' + gplusUrl + '"></g:plusone>');
				gplusDiv.data('on','true');
				window.gapi.load('googleapis.client:plusone', {'callback': window['__bsld']  });
			}
		}

		var setupRightMargin = function() {
			var imgs = jQuery('.v10_medium, .video_300');
			var adp = jQuery('.post-supp');

			if (imgs.length > 0 && adp.length > 0) {
				var lowerp = adp.offset().top + adp.height(); // the bottom edge of the meta/adunit

				imgs.each(function () {
					var currentimage = jQuery(this);
					if (currentimage.offset().top > lowerp) {
						/* if the image is NOT next to the ad unit, give it back its margin */
						if (!currentimage.hasClass('right')) {
							currentimage.css('margin-right', '40px');
						}
					}
				});
			}
		};

		var blogView = {
			
			spongesettings: {'retries': 20, 'interval': 1},
			
			spongestats: null,
			
			getStatlessPostUrls: function() {
				var urls = [], post;
				var posts = jQuery('#page > .post, #content > .post');
				for (var i = 0, l = posts.length; i < l; i++ ) {
					post = posts.eq(i);
					if (post.data('postid') > 0 && post.data('statready') != true && (post.data('spongetries') === undefined || post.data('spongetries') < this.spongesettings.retries)) {
						urls.push(post.data('permalink'));
					}
				}
				return urls;
			},
			
			loadSpongeStats: function () {
				//load urls that don't have their stats filled out yet
				var urls = this.getStatlessPostUrls();
				if (typeof sponge_client !== 'undefined' && GawkerClientside.widgets.spongestatsclient !== undefined) { //global config
					if (!this.spongestats) {
						//initialize the spongestatsclient widget
						this.spongestats = GawkerClientside.widgets.spongestatsclient[0];
					}
					//pass the url this and this.insertSpongeStatsResults as callback
					this.spongestats.getStats(urls, this, this.insertSpongeStatsResults);
				}
			},

			insertSpongeStatsResults: function(response) {

				//mark this request as ready
				this.spongestats.requests_running--;

				var i, l, data, post, ready, tries, title;
				//do nothing if the response contains no data at all
				if (response.data === undefined) {
					return false;
				}
				
				var posts = jQuery('#page > .post, #content > .post');

				//iterate through the posts in the post lists
				for (i = 0, l = posts.length; i < l; i++ ) {
					post = posts.eq(i);
					data = response.data[post.data('permalink')];
					//check if we have new data relevant to this post
					if (data !== undefined) {
						
						if(post.hasClass('top')) {
							views_html = post.find('.data .total_views');
							views_showhide = post.find('.data .nothing');
							comments_html = post.find('.data .comments');
							comments_showhide = post.find('.data .nothing');
							alt_and_title = post.find('.data');
						} else {
							views_html = post.find('.views span');
							views_showhide = post.find('.views');
							comments_html = post.find('.comments span');
							comments_showhide = post.find('.comments');
							alt_and_title = post.find('.views, .comments');
						}
						
						ready = true;
						title = [];
						tries = post.data('spongetries') > 0 ? post.data('spongetries') + 1 : 1;

						//check if we have view stats returned
						if (data.views !== undefined && data.views.ready == true) {
							views_html.html(gawker_add_commas(data.views.totalViews));
							title.push(data.views.totalViews + ' pageviews, ' + data.views.totalUniqueViews + ' visitors');
							views_showhide.show();
						} else {
							ready = false;
						}

						//check if we have comment stats returned
						if (data.comments !== undefined && data.comments.ready == true) {
							comments_html.html(gawker_add_commas(data.comments.numComments));
							title.push(data.comments.numComments + ' comments');
							comments_showhide.show();
						} else {
							ready = false;
						}

						//set ready and refresh status for this post
						post.data('statready', ready);
						alt_and_title.attr('title', title.join(', '));
						alt_and_title.attr('alt', title.join(', '));

						//count the tries, don't try to load data endlessly
						if(ready === false) {
							post.data('spongetries', (post.data('spongetries') === undefined ? 1 : Math.floor(post.data('spongetries')) + 1));
						}
					}
				}

				//if no requests are running and there are still posts with no data, let's refresh them in 2 seconds
				if((this.spongestats.requests_running == 0) && (this.getStatlessPostUrls().length > 0)) {
					setTimeout(jQuery.proxy(this.loadSpongeStats, this), (this.spongesettings.interval * 1000));
				}
			}
		};

		jQuery( document ).ready( function() {
            var rePrettyScrollbarSupport = /Mac OS X 10_([0-9_]+)/,
                prettyScrollbarSupport = navigator.userAgent.match(rePrettyScrollbarSupport),
                osVersion;
            if (prettyScrollbarSupport && prettyScrollbarSupport.length == 2) {
                osVersion = parseInt(prettyScrollbarSupport[1].split('_')[0]);
                if (!isNaN(osVersion) && osVersion >= 7 && navigator.userAgent.toLowerCase().indexOf('webkit')) {
					jQuery('html').addClass('pretty-scrollbars');
					jQuery(".scrollwrap").mouseenter(function(){jQuery("#hidescroll").fadeOut(100);});
					jQuery(".scrollwrap").mouseleave(function(){jQuery("#hidescroll").fadeIn(500);});
                }
            }
            // rightbar should default to static
            var rightbarStatic = jQuery.cookie('gawkermedia_rightbar') == 'staticstyle' || jQuery.cookie('gawkermedia_rightbar') == null;
            // set or unset class controlling rightbar scrolling based on cookie presence
            jQuery('body').toggleClass('rightbar-no-scroll', rightbarStatic);
			setupWidgets();
			setupMainContent();
			setupFacebook();
			setupShareMenus();
			
			if( window.checkHashForErrors !== undefined ) {
				window.checkHashForErrors();
			}

			if(jQuery('body.classic_frontpage, body.adlist.legacy, body.adlist.modern').length > 0) {
				blogView.loadSpongeStats();
			}

			addBlogviewHandler();
			initTrackers();
			//setupRightMargin();

			// start lytebox plugin
			jQuery(document).lytebox();

			//uncomment this to enable the autoplay feature
			//bindAutoplayEvent();
			if (window.post && window.post.tags) {
				adRobot.initAdsense(window.google_hints, window.post.tags);
			} else {
				adRobot.initAdsense(window.google_hints);
			}

			//remove alt and title from fb avatars
			jQuery('.fb_profile_pic_rendered img').live("mouseenter", function() {
				jQuery(this).removeAttr('alt');
				jQuery(this).removeAttr('title');
			});

		} );
	})();
