if(typeof(async_site_pixel_request_sent) == "undefined") {
    
var fmJsHost = (("https:" == document.location.protocol) ? "https://" : "http://");


var fm_query_string = window.location.search.substr(1).split('&');
var fm_pairs = {};
for (var i = 0; i < fm_query_string.length; i++) {
  var pair = fm_query_string[i].split("=");
  fm_pairs[pair[0]] = pair[1];
}


if (typeof __fm_enc_u === "undefined") {
  var __fm_url = document.URL;
  if (top !== self) {
    if (typeof fm_pairs['fm_url'] === "string") {
      __fm_url = fm_pairs['fm_url'];
    } else if (typeof document.referrer === "string" && document.referrer !== "") {
      __fm_url = document.referrer;
    }
  }
  __fm_enc_u = (typeof encodeURIComponent === 'function') ? encodeURIComponent(__fm_url) : escape(__fm_url);
}
if (typeof(fm_pairs['federated_media_section']) == "string") {
	var federated_media_section = fm_pairs['federated_media_section'];
}

var federated_media_section_source = '';
if (typeof(federated_media_section) == "string") {
  federated_media_section_source = federated_media_section.replace(/([^a-zA-Z0-9_\-\/])|(^\/)/g, "");
  var federated_media_sections = ["business"];
  var section_match = 0;
  for (i = 0; i < federated_media_sections.length; i++) {
    if (federated_media_section_source.toLowerCase() == federated_media_sections[i].toLowerCase()) {
      federated_media_section_source = federated_media_sections[i];
      section_match = 1;
      break;
    }
  }
  if (!section_match) {
    federated_media_section_source = '';
  }
}
var __fmx = __fmx || '';
if (federated_media_section_source!="") {__fmx='&s='+federated_media_section_source;} if (typeof(___fm_kw)!="undefined"&&___fm_kw!=""){__fmx+='&keywords='+___fm_kw.replace(/fmkw=/g,"").replace(/\;/g,"|");} if (typeof(__fm_enc_u !== "undefined")) { __fmx += '&u='+__fm_enc_u;}
var __fms = document.createElement('script'); __fms.type = 'text/javascript'; __fms.async = true; __fms.src = fmJsHost + 'tenzing.fmpub.net/?t=s&n=26' + __fmx;
var _fmss = document.getElementsByTagName('script')[0]; _fmss.parentNode.insertBefore(__fms, _fmss);


// comScore publisher tag
var _comscore = _comscore || [];
_comscore.push({ c1: "2", c2: "3005693", c3: "2", c4: "http%3A%2F%2Fwww.fark.com%2F" });

(function() {
    var s = document.createElement("script"), el = document.getElementsByTagName("script")[0]; s.async = true;
    s.src = (document.location.protocol == "https:" ? "https://sb" : "http://b") + ".scorecardresearch.com/beacon.js";
    el.parentNode.insertBefore(s, el);
})();


    var async_site_pixel_request_sent = 1;
}
