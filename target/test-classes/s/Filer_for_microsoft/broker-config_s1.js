/*
Copyright (c) 2013, comScore Inc. All rights reserved.
version: 5.0.3
*/
var SRtempCookie = document.cookie.toString();

function _set_SessionCookie(_url){	
  var c = 'captlinks=' + _url  	    	
  				+ '; path=/'
   			 	+ '; domain=microsoft.com';
	document.cookie = c;	
}
_set_SessionCookie("0");
if(/[\w\.]+\/en-us\//i.test(SR_url)){
var allLinks = document.getElementsByTagName("a");
 for (var i = 0, n = allLinks.length; i < n; i++) {
	if(/microsoftstore|store\.microsoft|clk\.atdmt\.com\/MRT\/go\/419363751\/direct/i.test(allLinks[i].href)){
		if(allLinks[i].addEventListener){
			allLinks[i].addEventListener('click',function(event){
				if(/microsoftstore|store\.microsoft|clk\.atdmt\.com\/MRT\/go\/419363751\/direct/i.test(this.href)){_set_SessionCookie("microsoftstore.com");}
				},false);
		}else{
			hrefURL = allLinks[i].href;
			allLinks[i].attachEvent('onclick',function(){
				_set_SessionCookie("microsoftstore.com");
				});
		}
 	}
 }
}
COMSCORE.SiteRecruit.Broker.config = {
	version: "5.0.3",
	//TODO:Karl extend cookie enhancements to ie userdata
		testMode: false,
	
	// cookie settings
	cookie:{
		name: 'msresearch',
		path: '/',
		domain:  '.microsoft.com' ,
		duration: 90,
		rapidDuration: 0,
		expireDate: ''
	},
	thirdPartyOptOutCookieEnabled : false,
	
	// optional prefix for pagemapping's pageconfig file
	prefixUrl: "",
	
	//events
	Events: {
		beforeRecruit: function() {
					}
	},
	
		mapping:[
	// m=regex match, c=page config file (prefixed with configUrl), f=frequency
	{m: '//[\\w\\.-]+/about((/)|(/((default)|(index))\\.((html?)|(aspx?)|(mspx))))?$', c: 'inv_c_3331mt2.js', f: 0.37, p: 0 	}
	,{m: '//[\\w\\.-]+/about/corporatecitizenship/en-us/youthspark/', c: 'inv_c_youthspark_qinvite.js', f: 0.25, p: 1 	}
	,{m: '//[\\w\\.-]+/about/corporatecitizenship/en-us/youthspark/youthsparkhub/', c: 'inv_c_youthsparkhub_qinvite.js', f: 0.25, p: 2 	}	
	,{m: '//[\\w\\.-]+/athome/', c: 'inv_c_3331mt3-Static.js', f: 0.041, p: 1 		
		,prereqs:{
			content: [
								  										{
						'element':'object'
												,'attrib':'data'						,'attribValue':'silverlight|oleobject'					}
									  							]
			,cookie: [
								  							]
			
											
			,externalDomain: [
																]
		}
	}
	,{m: '//[\\w\\.-]+/athome/', c: 'inv_c_3331mt3.js', f: 0.041, p: 0 	}
	,{m: '//[\\w\\.-]+/atwork/', c: 'inv_c_3331mt5-Static.js', f: 0.046, p: 2 		
		,prereqs:{
			content: [
								  										{
						'element':'object'
												,'attrib':'data'						,'attribValue':'silverlight|oleobject'					}
									  							]
			,cookie: [
								  							]
			
											
			,externalDomain: [
																]
		}
	}
	,{m: '//[\\w\\.-]+/atwork', c: 'inv_c_3331mt5.js', f: 0.046, p: 0 	}
	,{m: '//[\\w\\.-]+/de-de/cloud/', c: 'inv_c_p73639549-Germany.js', f: 0.5, p: 0 	}
	,{m: '//[\\w\\.-]+/de-de/windows/compatibility/.*/CompatCenter', c: 'inv_c_p176052898-DE-DE.js', f: 0.074, p: 4 	}
	,{m: '//[\\w\\.-]+/(en-us/download)|(download/(en/|.*?displaylang=en))', c: 'inv_c_3331mt_p105571867-1345.js', f: 0.0055, p: 1 	}
	,{m: '//[\\w\\.-]+/dynamics/asmartmove/default\\.mspx', c: 'inv_c_3331mt14-SL-fix_NEW-750.js', f: 0.5, p: 3 	}
	,{m: '//[\\w\\.-]+/dynamics/customer/en-us/', c: 'inv_c_p68785097-1461.js', f: 0.5, p: 1 	}
	,{m: '//[\\w\\.-]+/enable/', c: 'inv_c_p174575219-Accessibility.js', f: 0.127, p: 0 	}
	,{m: '//[\\w\\.-]+/en-au/(default\\.aspx)?$', c: 'inv_c_p162091074-EN-AU_HP.js', f: 0.7, p: 1 	}
	,{m: '//[\\w\\.-]+/en-au/windows/compatibility/.*/CompatCenter', c: 'inv_c_p176052898-EN-AU.js', f: 0.5, p: 0 	}
	,{m: '//[\\w\\.-]+/en-ca/windows/compatibility/.*/CompatCenter', c: 'inv_c_p176052898-EN-CA.js', f: 0.5, p: 4 	}
	,{m: '//(?!privacy)[\\w\\.-]+/en-gb/(default\\.aspx|$)', c: 'inv_c_p162091074-EN-GB_HP.js', f: 0.7, p: 0 	}
	,{m: '//[\\w\\.-]+/en-gb/windows/compatibility/.*/CompatCenter', c: 'inv_c_p176052898-EN-GB.js', f: 0.44, p: 0 	}
	,{m: '//[\\w\\.-]+/en-us/bi/', c: 'inv_c_p174651235_qInvite1727.js', f: 0.5, p: 2 	}
	,{m: '//(www|wwwstaging)[\\w\\.-]*/en-us/(default\\.aspx)?$', c: 'inv_c_p38796305-EN-US-PREVIEW.js', f: 0.0626, p: 1 	}
	,{m: '//[\\w\\.-]+/en-us/dynamics/(?!(customersource\\.aspx|partner-login\\.aspx|solution-finder\\.aspx|default\\.aspx|how-to-buy\\.aspx|default\\.aspx|contact-us-thanks\\.aspx|contact-us-cancel\\.aspx|search-results\\.aspx|go-to|overlays))', c: 'inv_c_3331mt14_NEW-750.js', f: 0.5, p: 0 	}
	,{m: '//[\\w\\.-]+/en-us/showcase/', c: 'inv_c_p23275586.js', f: 0.6, p: 0 	}
	,{m: '//[\\w\\.-]+/en-us/sqlserver', c: 'inv_c_p119307030-SQL-1161.js', f: 0.5, p: 1 	}
	,{m: '//[\\w\\.-]+/en-us/windows/compatibility/.*/CompatCenter', c: 'inv_c_p176052898-EN-US.js', f: 0.0089, p: 4 	}
	,{m: '//[\\w\\.-]+/(en-us/)?windows/enterprise/(?!(default\\.(aspx|html|mspx))|$)', c: 'inv_c_p38361073-DDS.js', f: 0.24, p: 0 	}
	,{m: '//[\\w\\.-]+/en-us/windows/windowsintune/', c: 'inv_c_p143857371-EN-US.js', f: 0.5, p: 0 	}
	,{m: '//[\\w\\.-]+/es-es/windows/compatibility/.*/CompatCenter', c: 'inv_c_p176052898-ES-ES.js', f: 0.169, p: 4 	}
	,{m: '//[\\w\\.-]+/fr-fr/windows/compatibility/.*/CompatCenter', c: 'inv_c_p176052898-FR-FR.js', f: 0.124, p: 4 	}
	,{m: '//[\\w\\.-]+/it-it/windows/compatibility/.*/CompatCenter', c: 'inv_c_p176052898-IT-IT.js', f: 0.5, p: 2 	}
	,{m: '//[\\w\\.-]+/ja-jp/atlife', c: 'inv_c_p15466742-JA-JP-ATLIFE.js', f: 0.01, p: 1 	}
	,{m: '//(wwwstaging|www\\.microsoft\\.com)/ja-jp/(default\\.aspx)?$', c: 'inv_c_p15466742-Japan-HP.js', f: 0.027, p: 1 	}
	,{m: '//[\\w\\.-]+/ja-jp/server-cloud/windows-server/((/)|(/((default)|(index))\\.((html?)|(aspx?)|(mspx))))?$', c: 'inv_c_JA-p15466742-server-cloud-WS.js', f: 0.5, p: 0 	}
	,{m: '//[\\w\\.-]+/ja-jp/windows/compatibility/.*/CompatCenter', c: 'inv_c_p176052898-JA-JP.js', f: 0.05, p: 4 	}
	,{m: '//[\\w\\.-]+/japan/business/', c: 'inv_c_JA-p15466742-business.js', f: 0.5, p: 1 	}
	,{m: '//[\\w\\.-]+/japan/windows(/(?!(downloads/ie/au\\.mspx)|(downloads/ie/iedelete\\.mspx))|$)', c: 'inv_c_JA-p15466742-p37131508-windows.js', f: 0.0315, p: 1 	}
	,{m: '//[\\w\\.-]+/ko-kr/windows/compatibility/.*/CompatCenter', c: 'inv_c_p176052898-KO-KR.js', f: 0.5, p: 0 	}
]
};
COMSCORE.SiteRecruit.Broker.run();