;var TypeOf;(function(){var U=void(0),N=null,W=window,ots=Object.prototype.toString,TRIMTK_RE=/^\[.+ |\]$/g,FN_TK='[object Function]',NAN_TK='[type NaN]',NUM_TK='[object Number]',OBJ_TK='[object Object]',DOC_T='document',EL_T='element',EVT_T='event',FN_T='function',LIST_T='list',OBJ_T='object',WIN_T='window',KNOWN_TYPE={},LIST_TYPE={},NODE_TYPE=[],SCALAR_TYPE={},CHECKED_TYPE={},FN_TYPE={};function typekey(o){var tk=ots.call(o);return tk in CHECKED_TYPE&&(o===U&&'[type undefined]'||o===N&&'[type null]'||tk===NUM_TK&&isNaN(o)&&NAN_TK)||tk;}
function distinct_typekey(o){var tk=typekey(o);return tk!==OBJ_TK&&tk;}
function trim(tk){return tk&&tk.replace(TRIMTK_RE,'');}
function qualify_number(o){var	tk=typekey(o),how;if(tk===NUM_TK){how='';}else if(isNaN(o/=1)){return false;}else{how=' ('+KNOWN_TYPE[tk]+')';}
return(isFinite(o)?'number':o.toString())+how;}
function maybe_fn(o){return false;}
function qualify_fn(o){var qt=FN_TYPE[typekey(o)];return(qt===OBJ_T?maybe_fn(o):qt)||false;}
function maybe_event(o){return(typekey(o.cancelBubble)==='[object Boolean]'||qualify_fn(o.stopPropagation))&&EVT_T;}
function maybe_list(o){try{return qualify_number(n=o.length)==='number'&&(!n||n-1 in o)&&LIST_T;}catch(e){}}
function maybe_node(o){try{return o.nodeName&&NODE_TYPE[o.nodeType];}catch(e){}}
function qualify_node(o){var t;return!!o&&((t=maybe_node(o))===DOC_T&&t||t===EL_T&&o.nodeName.toLowerCase()||t&&o.nodeName||t);}
TypeOf=function(o){var tk=typekey(o);return KNOWN_TYPE[tk]||o===W&&WIN_T||qualify_fn(o.__typeOf)&&o.__typeOf()||maybe_node(o)||maybe_event(o)||maybe_list(o)||tk===OBJ_TK&&maybe_fn(o)||OBJ_T;}
TypeOf.debug=KNOWN_TYPE;TypeOf.element=function(o){return!!o&&maybe_node(o)===EL_T&&qualify_node(o);};TypeOf.event=function(o){var qt;if(!o||!maybe_event(o))
return false;try{qt=o.type;}catch(e){}
return qt||trim(distinct_typekey(o))||EVT_T;};TypeOf.fn=qualify_fn;TypeOf.list=function(o){var tk=typekey(o);return tk in LIST_TYPE?LIST_TYPE[tk]:!!o&&maybe_list(o);};TypeOf.node=qualify_node;TypeOf.number=qualify_number;TypeOf.object=function(o){return trim(typekey(o).replace(NAN_TK,NUM_TK));};TypeOf.scalar=function(o){return SCALAR_TYPE[typekey(o)]||false;};(function(){var EL_NT=1,DOC_NT=9,LAST_NT=12;FN_TYPE[FN_TK]='function';CHECKED_TYPE[ots.call(U)]=true;CHECKED_TYPE[ots.call(N)]=true;CHECKED_TYPE[NUM_TK]=false;for(var i=EL_NT;i<=LAST_NT;++i){NODE_TYPE[i]='node';}
NODE_TYPE[EL_NT]=EL_T;NODE_TYPE[DOC_NT]=DOC_T;function define(o,scalar,list,tn){var tk=distinct_typekey(o);if(tk){tn||(tn=trim(tk).toLowerCase());KNOWN_TYPE[tk]=tn;scalar!==U&&(SCALAR_TYPE[tk]=tn);list!==U&&(LIST_TYPE[tk]=list&&tn);}
return tk;}
define(void(0),true,false);define(null,true,false);define(false,true,false);define(0,true,false);define(0/0,true,false,'NaN');define('',true,true);define(function(){},false,false);define([],false,true);define(/./,false,false);define(new Date(),false,false);define(new Error(),false,false);define(document,false,false,DOC_T);if(!define(window,false,false,WIN_T)){TypeOf.list=function(o){var tk;return o!==window&&((tk=typekey(o))in LIST_TYPE?LIST_TYPE[tk]:!!o&&maybe_list(o));};}
define(document.childNodes,false,true,LIST_T);define(arguments,false,true,LIST_T);if(document.createEvent){define(document.createEvent('UIEvents'),false,false,EVT_T);define(document.createEvent('MouseEvents'),false,false,EVT_T);define(document.createEvent('MutationEvents'),false,false,EVT_T);define(document.createEvent('HTMLEvents'),false,false,EVT_T);}
if(!qualify_fn(document.getElementById)){FN_TYPE[OBJ_TK]=OBJ_T;maybe_fn=function(o){return FN_TYPE[typekey(o.call)]&&FN_TYPE[typekey(o.apply)]&&FN_T;};}})();})();;(function(){var A=Array.prototype,S=String.prototype;if(!A.indexOf)
{A.indexOf=function(elt)
{var len=this.length;var from=Number(arguments[1])||0;from=(from<0)?Math.ceil(from):Math.floor(from);if(from<0)
from+=len;for(;from<len;from++)
{if(from in this&&this[from]===elt)
return from;}
return-1;};}
if(!A.lastIndexOf)
{A.lastIndexOf=function(elt)
{var len=this.length;var from=Number(arguments[1]);if(isNaN(from))
{from=len-1;}
else
{from=(from<0)?Math.ceil(from):Math.floor(from);if(from<0)
from+=len;else if(from>=len)
from=len-1;}
for(;from>-1;from--)
{if(from in this&&this[from]===elt)
return from;}
return-1;};}
if(!A.every)
{A.every=function(fun)
{var len=this.length;if(typeof fun!="function")
throw new TypeError();var thisp=arguments[1];for(var i=0;i<len;i++)
{if(i in this&&!fun.call(thisp,this[i],i,this))
return false;}
return true;};}
if(!A.filter)
{A.filter=function(fun)
{var len=this.length;if(typeof fun!="function")
throw new TypeError();var res=new Array();var thisp=arguments[1];for(var i=0;i<len;i++)
{if(i in this)
{var val=this[i];if(fun.call(thisp,val,i,this))
res.push(val);}}
return res;};}
if(!A.forEach)
{A.forEach=function(fun)
{var len=this.length;if(typeof fun!="function")
throw new TypeError();var thisp=arguments[1];for(var i=0;i<len;i++)
{if(i in this)
fun.call(thisp,this[i],i,this);}};}
if(!A.map)
{A.map=function(fun)
{var len=this.length;if(typeof fun!="function")
throw new TypeError();var res=new Array(len);var thisp=arguments[1];for(var i=0;i<len;i++)
{if(i in this)
res[i]=fun.call(thisp,this[i],i,this);}
return res;};}
if(!A.some)
{A.some=function(fun)
{var len=this.length;if(typeof fun!="function")
throw new TypeError();var thisp=arguments[1];for(var i=0;i<len;i++)
{if(i in this&&fun.call(thisp,this[i],i,this))
return true;}
return false;};}
if(!A.reduce)
{A.reduce=function(fun)
{var len=this.length;if(typeof fun!="function")
throw new TypeError();if(len==0&&arguments.length==1)
throw new TypeError();var i=0;if(arguments.length>=2)
{var rv=arguments[1];}
else
{do
{if(i in this)
{rv=this[i++];break;}
if(++i>=len)
throw new TypeError();}
while(true);}
for(;i<len;i++)
{if(i in this)
rv=fun.call(null,rv,this[i],i,this);}
return rv;};}
if(!A.reduceRight)
{A.reduceRight=function(fun)
{var len=this.length;if(typeof fun!="function")
throw new TypeError();if(len==0&&arguments.length==1)
throw new TypeError();var i=len-1;if(arguments.length>=2)
{var rv=arguments[1];}
else
{do
{if(i in this)
{rv=this[i--];break;}
if(--i<0)
throw new TypeError();}
while(true);}
for(;i>=0;i--)
{if(i in this)
rv=fun.call(null,rv,this[i],i,this);}
return rv;};}
if(!S.trim)
{var trim_regexp=/^\s+|\s+$/g;S.trim=function()
{return this.replace(trim_regexp,'');};}
if(!S.trimLeft)
{var trimLeft_regexp=/^\s+/;S.trimLeft=function()
{return this.replace(trimLeft_regexp,'');};}
if(!S.trimRight)
{var trimRight_regexp=/\s+$/;S.trimLeft=function()
{return this.replace(trimRight_regexp,'');};}
function make_generic(name){var fn;name in this||typeof(fn=this.prototype[name])!=='function'||(this[name]=function(o){return fn.apply(o,A.slice.call(arguments,1));});}
['concat','every','filter','forEach','indexOf','join','lastIndexOf','map','pop','push','reduce','reduceRight','reverse','shift','slice','some','sort','splice','unshift'].forEach(make_generic,Array);['charAt','charCodeAt','concat','indexOf','lastIndexOf','match','replace','search','slice','split','substr','substring','toLowerCase','toUpperCase','trim','trimLeft','trimRight'].forEach(make_generic,String);})();;var slashcore=(function(){function ordered(o){return o&&'length'in o&&(o.length-1 in o||o.length===0&&typeof(o)!=='function');}
function flatten(array){return Array.prototype.concat.apply([],array);}
function each(o,fn){if(ordered(o)){Array.every(o,function(v,i){return fn.call(v,i,v)!==false;});}else{for(var name in o){if(fn.call(o[name],name,o[name])===false){break;}}}
return o;}
function reduce(o,accumulated,fn){var step;switch(typeof(accumulated)){case'function':if(arguments.length>2)
break;fn=accumulated;case'undefined':accumulated=ordered(o)?[]:{};}
each(o,function(k,v){(step=fn.call(accumulated,k,v,accumulated))!==undefined&&(accumulated=step);});return accumulated;}
return{each:each,reduce:reduce,map:function(o,fn){var step,mapped=[];reduce(o,mapped,function(k,v){(step=fn.call(v,k,v))!==undefined&&this.push(step);});return flatten(mapped);},keys:function(o){return reduce(o,[],function(key){this.push(key);});},values:function(o){return reduce(o,[],function(key,value){this.push(value);});},grep:function(o,fn,invert){return reduce(o,[],function(k,v){!invert!=!fn.call(v,k,v)&&this.push(v);});},merge:function(o){Array.prototype.push.apply(o,flatten(Array.slice(arguments,1)));return o;},unique:function(o){var seen={};return reduce(o,[],function(k,v){v in seen||(seen[v]=true)&&this.push(v);});}};})();;(function(){var
window=this,undefined,_jQuery=window.jQuery,_$=window.$,jQuery=window.jQuery=window.$=function(selector,context){return new jQuery.fn.init(selector,context);},quickExpr=/^[^<]*(<(.|\s)+>)[^>]*$|^#([\w-]+)$/,isSimple=/^.[^:#\[\.,]*$/;jQuery.fn=jQuery.prototype={init:function(selector,context){selector=selector||document;if(selector.nodeType){this[0]=selector;this.length=1;this.context=selector;return this;}
if(typeof selector==="string"){var match=quickExpr.exec(selector);if(match&&(match[1]||!context)){if(match[1])
selector=jQuery.clean([match[1]],context);else{var elem=document.getElementById(match[3]);if(elem&&elem.id!=match[3])
return jQuery().find(selector);var ret=jQuery(elem||[]);ret.context=document;ret.selector=selector;return ret;}
}else
return jQuery(context).find(selector);}else if(jQuery.isFunction(selector))
return jQuery(document).ready(selector);if(selector.selector&&selector.context){this.selector=selector.selector;this.context=selector.context;}
return this.setArray(jQuery.isArray(selector)?selector:jQuery.makeArray(selector));},selector:"",jquery:"1.3.2",size:function(){return this.length;},get:function(num){return num===undefined?Array.prototype.slice.call(this):this[num];},pushStack:function(elems,name,selector){var ret=jQuery(elems);ret.prevObject=this;ret.context=this.context;if(name==="find")
ret.selector=this.selector+(this.selector?" ":"")+selector;else if(name)
ret.selector=this.selector+"."+name+"("+selector+")";return ret;},setArray:function(elems){this.length=0;Array.prototype.push.apply(this,elems);return this;},each:function(callback,args){return jQuery.each(this,callback,args);},index:function(elem){return jQuery.inArray(elem&&elem.jquery?elem[0]:elem,this);},attr:function(name,value,type){var options=name;if(typeof name==="string")
if(value===undefined)
return this[0]&&jQuery[type||"attr"](this[0],name);else{options={};options[name]=value;}
return this.each(function(i){for(name in options)
jQuery.attr(type?this.style:this,name,jQuery.prop(this,options[name],type,i,name));});},css:function(key,value){if((key=='width'||key=='height')&&parseFloat(value)<0)
value=undefined;return this.attr(key,value,"curCSS");},text:function(text){if(typeof text!=="object"&&text!=null)
return this.empty().append((this[0]&&this[0].ownerDocument||document).createTextNode(text));var ret="";jQuery.each(text||this,function(){jQuery.each(this.childNodes,function(){if(this.nodeType!=8)
ret+=this.nodeType!=1?this.nodeValue:jQuery.fn.text([this]);});});return ret;},wrapAll:function(html){if(this[0]){var wrap=jQuery(html,this[0].ownerDocument).clone();if(this[0].parentNode)
wrap.insertBefore(this[0]);wrap.map(function(){var elem=this;while(elem.firstChild)
elem=elem.firstChild;return elem;}).append(this);}
return this;},wrapInner:function(html){return this.each(function(){jQuery(this).contents().wrapAll(html);});},wrap:function(html){return this.each(function(){jQuery(this).wrapAll(html);});},append:function(){return this.domManip(arguments,true,function(elem){if(this.nodeType==1)
this.appendChild(elem);});},prepend:function(){return this.domManip(arguments,true,function(elem){if(this.nodeType==1)
this.insertBefore(elem,this.firstChild);});},before:function(){return this.domManip(arguments,false,function(elem){this.parentNode.insertBefore(elem,this);});},after:function(){return this.domManip(arguments,false,function(elem){this.parentNode.insertBefore(elem,this.nextSibling);});},end:function(){return this.prevObject||jQuery([]);},push:[].push,sort:[].sort,splice:[].splice,find:function(selector){if(this.length===1){var ret=this.pushStack([],"find",selector);ret.length=0;jQuery.find(selector,this[0],ret);return ret;}else{return this.pushStack(jQuery.unique(jQuery.map(this,function(elem){return jQuery.find(selector,elem);})),"find",selector);}},clone:function(events){var ret=this.map(function(){if(!jQuery.support.noCloneEvent&&!jQuery.isXMLDoc(this)){var html=this.outerHTML;if(!html){var div=this.ownerDocument.createElement("div");div.appendChild(this.cloneNode(true));html=div.innerHTML;}
return jQuery.clean([html.replace(/ jQuery\d+="(?:\d+|null)"/g,"").replace(/^\s*/,"")])[0];}else
return this.cloneNode(true);});if(events===true){var orig=this.find("*").andSelf(),i=0;ret.find("*").andSelf().each(function(){if(this.nodeName!==orig[i].nodeName)
return;var events=jQuery.data(orig[i],"events");for(var type in events){for(var handler in events[type]){jQuery.event.add(this,type,events[type][handler],events[type][handler].data);}}
i++;});}
return ret;},filter:function(selector){return this.pushStack(jQuery.isFunction(selector)&&jQuery.grep(this,function(elem,i){return selector.call(elem,i);})||jQuery.multiFilter(selector,jQuery.grep(this,function(elem){return elem.nodeType===1;})),"filter",selector);},closest:function(selector){var pos=jQuery.expr.match.POS.test(selector)?jQuery(selector):null,closer=0;return this.map(function(){var cur=this;while(cur&&cur.ownerDocument){if(pos?pos.index(cur)>-1:jQuery(cur).is(selector)){jQuery.data(cur,"closest",closer);return cur;}
cur=cur.parentNode;closer++;}});},not:function(selector){if(typeof selector==="string")
if(isSimple.test(selector))
return this.pushStack(jQuery.multiFilter(selector,this,true),"not",selector);else
selector=jQuery.multiFilter(selector,this);var isArrayLike=selector.length&&selector[selector.length-1]!==undefined&&!selector.nodeType;return this.filter(function(){return isArrayLike?jQuery.inArray(this,selector)<0:this!=selector;});},add:function(selector){return this.pushStack(jQuery.unique(jQuery.merge(this.get(),typeof selector==="string"?jQuery(selector):jQuery.makeArray(selector))));},is:function(selector){return!!selector&&jQuery.multiFilter(selector,this).length>0;},hasClass:function(selector){return!!selector&&this.is("."+selector);},val:function(value){if(value===undefined){var elem=this[0];if(elem){if(jQuery.nodeName(elem,'option'))
return(elem.attributes.value||{}).specified?elem.value:elem.text;if(jQuery.nodeName(elem,"select")){var index=elem.selectedIndex,values=[],options=elem.options,one=elem.type=="select-one";if(index<0)
return null;for(var i=one?index:0,max=one?index+1:options.length;i<max;i++){var option=options[i];if(option.selected){value=jQuery(option).val();if(one)
return value;values.push(value);}}
return values;}
return(elem.value||"").replace(/\r/g,"");}
return undefined;}
if(typeof value==="number")
value+='';return this.each(function(){if(this.nodeType!=1)
return;if(jQuery.isArray(value)&&/radio|checkbox/.test(this.type))
this.checked=(jQuery.inArray(this.value,value)>=0||jQuery.inArray(this.name,value)>=0);else if(jQuery.nodeName(this,"select")){var values=jQuery.makeArray(value);jQuery("option",this).each(function(){this.selected=(jQuery.inArray(this.value,values)>=0||jQuery.inArray(this.text,values)>=0);});if(!values.length)
this.selectedIndex=-1;}else
this.value=value;});},html:function(value){return value===undefined?(this[0]?this[0].innerHTML.replace(/ jQuery\d+="(?:\d+|null)"/g,""):null):this.empty().append(value);},replaceWith:function(value){return this.after(value).remove();},eq:function(i){return this.slice(i,+i+1);},slice:function(){return this.pushStack(Array.prototype.slice.apply(this,arguments),"slice",Array.prototype.slice.call(arguments).join(","));},map:function(callback){return this.pushStack(jQuery.map(this,function(elem,i){return callback.call(elem,i,elem);}));},andSelf:function(){return this.add(this.prevObject);},domManip:function(args,table,callback){if(this[0]){var fragment=(this[0].ownerDocument||this[0]).createDocumentFragment(),scripts=jQuery.clean(args,(this[0].ownerDocument||this[0]),fragment),first=fragment.firstChild;if(first)
for(var i=0,l=this.length;i<l;i++)
callback.call(root(this[i],first),this.length>1||i>0?fragment.cloneNode(true):fragment);if(scripts)
jQuery.each(scripts,evalScript);}
return this;function root(elem,cur){return table&&jQuery.nodeName(elem,"table")&&jQuery.nodeName(cur,"tr")?(elem.getElementsByTagName("tbody")[0]||elem.appendChild(elem.ownerDocument.createElement("tbody"))):elem;}}};jQuery.fn.init.prototype=jQuery.fn;function evalScript(i,elem){if(elem.src)
jQuery.ajax({url:elem.src,async:false,dataType:"script"});else
jQuery.globalEval(elem.text||elem.textContent||elem.innerHTML||"");if(elem.parentNode)
elem.parentNode.removeChild(elem);}
function now(){return+new Date;}
jQuery.extend=jQuery.fn.extend=function(){var target=arguments[0]||{},i=1,length=arguments.length,deep=false,options;if(typeof target==="boolean"){deep=target;target=arguments[1]||{};i=2;}
if(typeof target!=="object"&&!jQuery.isFunction(target))
target={};if(length==i){target=this;--i;}
for(;i<length;i++)
if((options=arguments[i])!=null)
for(var name in options){var src=target[name],copy=options[name];if(target===copy)
continue;if(deep&&copy&&typeof copy==="object"&&!copy.nodeType)
target[name]=jQuery.extend(deep,src||(copy.length!=null?[]:{}),copy);else if(copy!==undefined)
target[name]=copy;}
return target;};var	exclude=/z-?index|font-?weight|opacity|zoom|line-?height/i,defaultView=document.defaultView||{},toString=Object.prototype.toString;jQuery.extend({noConflict:function(deep){window.$=_$;if(deep)
window.jQuery=_jQuery;return jQuery;},isFunction:function(obj){return toString.call(obj)==="[object Function]";},isArray:function(obj){return toString.call(obj)==="[object Array]";},isXMLDoc:function(elem){return elem.nodeType===9&&elem.documentElement.nodeName!=="HTML"||!!elem.ownerDocument&&jQuery.isXMLDoc(elem.ownerDocument);},globalEval:function(data){if(data&&/\S/.test(data)){var head=document.getElementsByTagName("head")[0]||document.documentElement,script=document.createElement("script");script.type="text/javascript";if(jQuery.support.scriptEval)
script.appendChild(document.createTextNode(data));else
script.text=data;head.insertBefore(script,head.firstChild);head.removeChild(script);}},nodeName:function(elem,name){return elem.nodeName&&elem.nodeName.toUpperCase()==name.toUpperCase();},each:function(object,callback,args){var name,i=0,length=object.length;if(args){if(length===undefined){for(name in object)
if(callback.apply(object[name],args)===false)
break;}else
for(;i<length;)
if(callback.apply(object[i++],args)===false)
break;}else{if(length===undefined){for(name in object)
if(callback.call(object[name],name,object[name])===false)
break;}else
for(var value=object[0];i<length&&callback.call(value,i,value)!==false;value=object[++i]){}}
return object;},prop:function(elem,value,type,i,name){if(jQuery.isFunction(value))
value=value.call(elem,i);return typeof value==="number"&&type=="curCSS"&&!exclude.test(name)?value+"px":value;},className:{add:function(elem,classNames){jQuery.each((classNames||"").split(/\s+/),function(i,className){if(elem.nodeType==1&&!jQuery.className.has(elem.className,className))
elem.className+=(elem.className?" ":"")+className;});},remove:function(elem,classNames){if(elem.nodeType==1)
elem.className=classNames!==undefined?jQuery.grep(elem.className.split(/\s+/),function(className){return!jQuery.className.has(classNames,className);}).join(" "):"";},has:function(elem,className){return elem&&jQuery.inArray(className,(elem.className||elem).toString().split(/\s+/))>-1;}},swap:function(elem,options,callback){var old={};for(var name in options){old[name]=elem.style[name];elem.style[name]=options[name];}
callback.call(elem);for(var name in options)
elem.style[name]=old[name];},css:function(elem,name,force,extra){if(name=="width"||name=="height"){var val,props={position:"absolute",visibility:"hidden",display:"block"},which=name=="width"?["Left","Right"]:["Top","Bottom"];function getWH(){val=name=="width"?elem.offsetWidth:elem.offsetHeight;if(extra==="border")
return;jQuery.each(which,function(){if(!extra)
val-=parseFloat(jQuery.curCSS(elem,"padding"+this,true))||0;if(extra==="margin")
val+=parseFloat(jQuery.curCSS(elem,"margin"+this,true))||0;else
val-=parseFloat(jQuery.curCSS(elem,"border"+this+"Width",true))||0;});}
if(elem.offsetWidth!==0)
getWH();else
jQuery.swap(elem,props,getWH);return Math.max(0,Math.round(val));}
return jQuery.curCSS(elem,name,force);},curCSS:function(elem,name,force){var ret,style=elem.style;if(name=="opacity"&&!jQuery.support.opacity){ret=jQuery.attr(style,"opacity");return ret==""?"1":ret;}
if(name.match(/float/i))
name=styleFloat;if(!force&&style&&style[name])
ret=style[name];else if(defaultView.getComputedStyle){if(name.match(/float/i))
name="float";name=name.replace(/([A-Z])/g,"-$1").toLowerCase();var computedStyle=defaultView.getComputedStyle(elem,null);if(computedStyle)
ret=computedStyle.getPropertyValue(name);if(name=="opacity"&&ret=="")
ret="1";}else if(elem.currentStyle){var camelCase=name.replace(/\-(\w)/g,function(all,letter){return letter.toUpperCase();});ret=elem.currentStyle[name]||elem.currentStyle[camelCase];if(!/^\d+(px)?$/i.test(ret)&&/^\d/.test(ret)){var left=style.left,rsLeft=elem.runtimeStyle.left;elem.runtimeStyle.left=elem.currentStyle.left;style.left=ret||0;ret=style.pixelLeft+"px";style.left=left;elem.runtimeStyle.left=rsLeft;}}
return ret;},clean:function(elems,context,fragment){context=context||document;if(typeof context.createElement==="undefined")
context=context.ownerDocument||context[0]&&context[0].ownerDocument||document;if(!fragment&&elems.length===1&&typeof elems[0]==="string"){var match=/^<(\w+)\s*\/?>$/.exec(elems[0]);if(match)
return[context.createElement(match[1])];}
var ret=[],scripts=[],div=context.createElement("div");jQuery.each(elems,function(i,elem){if(typeof elem==="number")
elem+='';if(!elem)
return;if(typeof elem==="string"){elem=elem.replace(/(<(\w+)[^>]*?)\/>/g,function(all,front,tag){return tag.match(/^(abbr|br|col|img|input|link|meta|param|hr|area|embed)$/i)?all:front+"></"+tag+">";});var tags=elem.replace(/^\s+/,"").substring(0,10).toLowerCase();var wrap=!tags.indexOf("<opt")&&[1,"<select multiple='multiple'>","</select>"]||!tags.indexOf("<leg")&&[1,"<fieldset>","</fieldset>"]||tags.match(/^<(thead|tbody|tfoot|colg|cap)/)&&[1,"<table>","</table>"]||!tags.indexOf("<tr")&&[2,"<table><tbody>","</tbody></table>"]||(!tags.indexOf("<td")||!tags.indexOf("<th"))&&[3,"<table><tbody><tr>","</tr></tbody></table>"]||!tags.indexOf("<col")&&[2,"<table><tbody></tbody><colgroup>","</colgroup></table>"]||!jQuery.support.htmlSerialize&&[1,"div<div>","</div>"]||[0,"",""];div.innerHTML=wrap[1]+elem+wrap[2];while(wrap[0]--)
div=div.lastChild;if(!jQuery.support.tbody){var hasBody=/<tbody/i.test(elem),tbody=!tags.indexOf("<table")&&!hasBody?div.firstChild&&div.firstChild.childNodes:wrap[1]=="<table>"&&!hasBody?div.childNodes:[];for(var j=tbody.length-1;j>=0;--j)
if(jQuery.nodeName(tbody[j],"tbody")&&!tbody[j].childNodes.length)
tbody[j].parentNode.removeChild(tbody[j]);}
if(!jQuery.support.leadingWhitespace&&/^\s/.test(elem))
div.insertBefore(context.createTextNode(elem.match(/^\s*/)[0]),div.firstChild);elem=jQuery.makeArray(div.childNodes);}
if(elem.nodeType)
ret.push(elem);else
ret=jQuery.merge(ret,elem);});if(fragment){for(var i=0;ret[i];i++){if(jQuery.nodeName(ret[i],"script")&&(!ret[i].type||ret[i].type.toLowerCase()==="text/javascript")){scripts.push(ret[i].parentNode?ret[i].parentNode.removeChild(ret[i]):ret[i]);}else{if(ret[i].nodeType===1)
ret.splice.apply(ret,[i+1,0].concat(jQuery.makeArray(ret[i].getElementsByTagName("script"))));fragment.appendChild(ret[i]);}}
return scripts;}
return ret;},attr:function(elem,name,value){if(!elem||elem.nodeType==3||elem.nodeType==8)
return undefined;var notxml=!jQuery.isXMLDoc(elem),set=value!==undefined;name=notxml&&jQuery.props[name]||name;if(elem.tagName){var special=/href|src|style/.test(name);if(name=="selected"&&elem.parentNode)
elem.parentNode.selectedIndex;if(name in elem&&notxml&&!special){if(set){if(name=="type"&&jQuery.nodeName(elem,"input")&&elem.parentNode)
throw"type property can't be changed";elem[name]=value;}
if(jQuery.nodeName(elem,"form")&&elem.getAttributeNode(name))
return elem.getAttributeNode(name).nodeValue;if(name=="tabIndex"){var attributeNode=elem.getAttributeNode("tabIndex");return attributeNode&&attributeNode.specified?attributeNode.value:elem.nodeName.match(/(button|input|object|select|textarea)/i)?0:elem.nodeName.match(/^(a|area)$/i)&&elem.href?0:undefined;}
return elem[name];}
if(!jQuery.support.style&&notxml&&name=="style")
return jQuery.attr(elem.style,"cssText",value);if(set)
elem.setAttribute(name,""+value);var attr=!jQuery.support.hrefNormalized&&notxml&&special
?elem.getAttribute(name,2):elem.getAttribute(name);return attr===null?undefined:attr;}
if(!jQuery.support.opacity&&name=="opacity"){if(set){elem.zoom=1;elem.filter=(elem.filter||"").replace(/alpha\([^)]*\)/,"")+
(parseInt(value)+''=="NaN"?"":"alpha(opacity="+value*100+")");}
return elem.filter&&elem.filter.indexOf("opacity=")>=0?(parseFloat(elem.filter.match(/opacity=([^)]*)/)[1])/100)+'':"";}
name=name.replace(/-([a-z])/ig,function(all,letter){return letter.toUpperCase();});if(set)
elem[name]=value;return elem[name];},trim:function(text){return(text||"").replace(/^\s+|\s+$/g,"");},makeArray:function(array){var ret=[];if(array!=null){var i=array.length;if(i==null||typeof array==="string"||jQuery.isFunction(array)||array.setInterval)
ret[0]=array;else
while(i)
ret[--i]=array[i];}
return ret;},inArray:function(elem,array){for(var i=0,length=array.length;i<length;i++)
if(array[i]===elem)
return i;return-1;},merge:function(first,second){var i=0,elem,pos=first.length;if(!jQuery.support.getAll){while((elem=second[i++])!=null)
if(elem.nodeType!=8)
first[pos++]=elem;}else
while((elem=second[i++])!=null)
first[pos++]=elem;return first;},unique:function(array){var ret=[],done={};try{for(var i=0,length=array.length;i<length;i++){var id=jQuery.data(array[i]);if(!done[id]){done[id]=true;ret.push(array[i]);}}}catch(e){ret=array;}
return ret;},grep:function(elems,callback,inv){var ret=[];for(var i=0,length=elems.length;i<length;i++)
if(!inv!=!callback(elems[i],i))
ret.push(elems[i]);return ret;},map:function(elems,callback){var ret=[];for(var i=0,length=elems.length;i<length;i++){var value=callback(elems[i],i);if(value!=null)
ret[ret.length]=value;}
return ret.concat.apply([],ret);}});var userAgent=navigator.userAgent.toLowerCase();jQuery.browser={version:(userAgent.match(/.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/)||[0,'0'])[1],safari:/webkit/.test(userAgent),opera:/opera/.test(userAgent),msie:/msie/.test(userAgent)&&!/opera/.test(userAgent),mozilla:/mozilla/.test(userAgent)&&!/(compatible|webkit)/.test(userAgent)};jQuery.each({parent:function(elem){return elem.parentNode;},parents:function(elem){return jQuery.dir(elem,"parentNode");},next:function(elem){return jQuery.nth(elem,2,"nextSibling");},prev:function(elem){return jQuery.nth(elem,2,"previousSibling");},nextAll:function(elem){return jQuery.dir(elem,"nextSibling");},prevAll:function(elem){return jQuery.dir(elem,"previousSibling");},siblings:function(elem){return jQuery.sibling(elem.parentNode.firstChild,elem);},children:function(elem){return jQuery.sibling(elem.firstChild);},contents:function(elem){return jQuery.nodeName(elem,"iframe")?elem.contentDocument||elem.contentWindow.document:jQuery.makeArray(elem.childNodes);}},function(name,fn){jQuery.fn[name]=function(selector){var ret=jQuery.map(this,fn);if(selector&&typeof selector=="string")
ret=jQuery.multiFilter(selector,ret);return this.pushStack(jQuery.unique(ret),name,selector);};});jQuery.each({appendTo:"append",prependTo:"prepend",insertBefore:"before",insertAfter:"after",replaceAll:"replaceWith"},function(name,original){jQuery.fn[name]=function(selector){var ret=[],insert=jQuery(selector);for(var i=0,l=insert.length;i<l;i++){var elems=(i>0?this.clone(true):this).get();jQuery.fn[original].apply(jQuery(insert[i]),elems);ret=ret.concat(elems);}
return this.pushStack(ret,name,selector);};});jQuery.each({removeAttr:function(name){jQuery.attr(this,name,"");if(this.nodeType==1)
this.removeAttribute(name);},addClass:function(classNames){jQuery.className.add(this,classNames);},removeClass:function(classNames){jQuery.className.remove(this,classNames);},toggleClass:function(classNames,state){if(typeof state!=="boolean")
state=!jQuery.className.has(this,classNames);jQuery.className[state?"add":"remove"](this,classNames);},remove:function(selector){if(!selector||jQuery.filter(selector,[this]).length){jQuery("*",this).add([this]).each(function(){jQuery.event.remove(this);jQuery.removeData(this);});if(this.parentNode)
this.parentNode.removeChild(this);}},empty:function(){jQuery(this).children().remove();while(this.firstChild)
this.removeChild(this.firstChild);}},function(name,fn){jQuery.fn[name]=function(){return this.each(fn,arguments);};});function num(elem,prop){return elem[0]&&parseInt(jQuery.curCSS(elem[0],prop,true),10)||0;}
var expando="jQuery"+now(),uuid=0,windowData={};jQuery.extend({cache:{},data:function(elem,name,data){elem=elem==window?windowData:elem;var id=elem[expando];if(!id)
id=elem[expando]=++uuid;if(name&&!jQuery.cache[id])
jQuery.cache[id]={};if(data!==undefined)
jQuery.cache[id][name]=data;return name?jQuery.cache[id][name]:id;},removeData:function(elem,name){elem=elem==window?windowData:elem;var id=elem[expando];if(name){if(jQuery.cache[id]){delete jQuery.cache[id][name];name="";for(name in jQuery.cache[id])
break;if(!name)
jQuery.removeData(elem);}
}else{try{delete elem[expando];}catch(e){if(elem.removeAttribute)
elem.removeAttribute(expando);}
delete jQuery.cache[id];}},queue:function(elem,type,data){if(elem){type=(type||"fx")+"queue";var q=jQuery.data(elem,type);if(!q||jQuery.isArray(data))
q=jQuery.data(elem,type,jQuery.makeArray(data));else if(data)
q.push(data);}
return q;},dequeue:function(elem,type){var queue=jQuery.queue(elem,type),fn=queue.shift();if(!type||type==="fx")
fn=queue[0];if(fn!==undefined)
fn.call(elem);}});jQuery.fn.extend({data:function(key,value){var parts=key.split(".");parts[1]=parts[1]?"."+parts[1]:"";if(value===undefined){var data=this.triggerHandler("getData"+parts[1]+"!",[parts[0]]);if(data===undefined&&this.length)
data=jQuery.data(this[0],key);return data===undefined&&parts[1]?this.data(parts[0]):data;}else
return this.trigger("setData"+parts[1]+"!",[parts[0],value]).each(function(){jQuery.data(this,key,value);});},removeData:function(key){return this.each(function(){jQuery.removeData(this,key);});},queue:function(type,data){if(typeof type!=="string"){data=type;type="fx";}
if(data===undefined)
return jQuery.queue(this[0],type);return this.each(function(){var queue=jQuery.queue(this,type,data);if(type=="fx"&&queue.length==1)
queue[0].call(this);});},dequeue:function(type){return this.each(function(){jQuery.dequeue(this,type);});}});(function(){var chunker=/((?:\((?:\([^()]+\)|[^()]+)+\)|\[(?:\[[^[\]]*\]|['"][^'"]*['"]|[^[\]'"]+)+\]|\\.|[^ >+~,(\[\\]+)+|[>+~])(\s*,\s*)?/g,done=0,toString=Object.prototype.toString;var Sizzle=function(selector,context,results,seed){results=results||[];context=context||document;if(context.nodeType!==1&&context.nodeType!==9)
return[];if(!selector||typeof selector!=="string"){return results;}
var parts=[],m,set,checkSet,check,mode,extra,prune=true;chunker.lastIndex=0;while((m=chunker.exec(selector))!==null){parts.push(m[1]);if(m[2]){extra=RegExp.rightContext;break;}}
if(parts.length>1&&origPOS.exec(selector)){if(parts.length===2&&Expr.relative[parts[0]]){set=posProcess(parts[0]+parts[1],context);}else{set=Expr.relative[parts[0]]?[context]:Sizzle(parts.shift(),context);while(parts.length){selector=parts.shift();if(Expr.relative[selector])
selector+=parts.shift();set=posProcess(selector,set);}}}else{var ret=seed?{expr:parts.pop(),set:makeArray(seed)}:Sizzle.find(parts.pop(),parts.length===1&&context.parentNode?context.parentNode:context,isXML(context));set=Sizzle.filter(ret.expr,ret.set);if(parts.length>0){checkSet=makeArray(set);}else{prune=false;}
while(parts.length){var cur=parts.pop(),pop=cur;if(!Expr.relative[cur]){cur="";}else{pop=parts.pop();}
if(pop==null){pop=context;}
Expr.relative[cur](checkSet,pop,isXML(context));}}
if(!checkSet){checkSet=set;}
if(!checkSet){throw"Syntax error, unrecognized expression: "+(cur||selector);}
if(toString.call(checkSet)==="[object Array]"){if(!prune){results.push.apply(results,checkSet);}else if(context.nodeType===1){for(var i=0;checkSet[i]!=null;i++){if(checkSet[i]&&(checkSet[i]===true||checkSet[i].nodeType===1&&contains(context,checkSet[i]))){results.push(set[i]);}}}else{for(var i=0;checkSet[i]!=null;i++){if(checkSet[i]&&checkSet[i].nodeType===1){results.push(set[i]);}}}}else{makeArray(checkSet,results);}
if(extra){Sizzle(extra,context,results,seed);if(sortOrder){hasDuplicate=false;results.sort(sortOrder);if(hasDuplicate){for(var i=1;i<results.length;i++){if(results[i]===results[i-1]){results.splice(i--,1);}}}}}
return results;};Sizzle.matches=function(expr,set){return Sizzle(expr,null,null,set);};Sizzle.find=function(expr,context,isXML){var set,match;if(!expr){return[];}
for(var i=0,l=Expr.order.length;i<l;i++){var type=Expr.order[i],match;if((match=Expr.match[type].exec(expr))){var left=RegExp.leftContext;if(left.substr(left.length-1)!=="\\"){match[1]=(match[1]||"").replace(/\\/g,"");set=Expr.find[type](match,context,isXML);if(set!=null){expr=expr.replace(Expr.match[type],"");break;}}}}
if(!set){set=context.getElementsByTagName("*");}
return{set:set,expr:expr};};Sizzle.filter=function(expr,set,inplace,not){var old=expr,result=[],curLoop=set,match,anyFound,isXMLFilter=set&&set[0]&&isXML(set[0]);while(expr&&set.length){for(var type in Expr.filter){if((match=Expr.match[type].exec(expr))!=null){var filter=Expr.filter[type],found,item;anyFound=false;if(curLoop==result){result=[];}
if(Expr.preFilter[type]){match=Expr.preFilter[type](match,curLoop,inplace,result,not,isXMLFilter);if(!match){anyFound=found=true;}else if(match===true){continue;}}
if(match){for(var i=0;(item=curLoop[i])!=null;i++){if(item){found=filter(item,match,i,curLoop);var pass=not^!!found;if(inplace&&found!=null){if(pass){anyFound=true;}else{curLoop[i]=false;}}else if(pass){result.push(item);anyFound=true;}}}}
if(found!==undefined){if(!inplace){curLoop=result;}
expr=expr.replace(Expr.match[type],"");if(!anyFound){return[];}
break;}}}
if(expr==old){if(anyFound==null){throw"Syntax error, unrecognized expression: "+expr;}else{break;}}
old=expr;}
return curLoop;};var Expr=Sizzle.selectors={order:["ID","NAME","TAG"],match:{ID:/#((?:[\w\u00c0-\uFFFF_-]|\\.)+)/,CLASS:/\.((?:[\w\u00c0-\uFFFF_-]|\\.)+)/,NAME:/\[name=['"]*((?:[\w\u00c0-\uFFFF_-]|\\.)+)['"]*\]/,ATTR:/\[\s*((?:[\w\u00c0-\uFFFF_-]|\\.)+)\s*(?:(\S?=)\s*(['"]*)(.*?)\3|)\s*\]/,TAG:/^((?:[\w\u00c0-\uFFFF\*_-]|\\.)+)/,CHILD:/:(only|nth|last|first)-child(?:\((even|odd|[\dn+-]*)\))?/,POS:/:(nth|eq|gt|lt|first|last|even|odd)(?:\((\d*)\))?(?=[^-]|$)/,PSEUDO:/:((?:[\w\u00c0-\uFFFF_-]|\\.)+)(?:\((['"]*)((?:\([^\)]+\)|[^\2\(\)]*)+)\2\))?/},attrMap:{"class":"className","for":"htmlFor"},attrHandle:{href:function(elem){return elem.getAttribute("href");}},relative:{"+":function(checkSet,part,isXML){var isPartStr=typeof part==="string",isTag=isPartStr&&!/\W/.test(part),isPartStrNotTag=isPartStr&&!isTag;if(isTag&&!isXML){part=part.toUpperCase();}
for(var i=0,l=checkSet.length,elem;i<l;i++){if((elem=checkSet[i])){while((elem=elem.previousSibling)&&elem.nodeType!==1){}
checkSet[i]=isPartStrNotTag||elem&&elem.nodeName===part?elem||false:elem===part;}}
if(isPartStrNotTag){Sizzle.filter(part,checkSet,true);}},">":function(checkSet,part,isXML){var isPartStr=typeof part==="string";if(isPartStr&&!/\W/.test(part)){part=isXML?part:part.toUpperCase();for(var i=0,l=checkSet.length;i<l;i++){var elem=checkSet[i];if(elem){var parent=elem.parentNode;checkSet[i]=parent.nodeName===part?parent:false;}}}else{for(var i=0,l=checkSet.length;i<l;i++){var elem=checkSet[i];if(elem){checkSet[i]=isPartStr?elem.parentNode:elem.parentNode===part;}}
if(isPartStr){Sizzle.filter(part,checkSet,true);}}},"":function(checkSet,part,isXML){var doneName=done++,checkFn=dirCheck;if(!part.match(/\W/)){var nodeCheck=part=isXML?part:part.toUpperCase();checkFn=dirNodeCheck;}
checkFn("parentNode",part,doneName,checkSet,nodeCheck,isXML);},"~":function(checkSet,part,isXML){var doneName=done++,checkFn=dirCheck;if(typeof part==="string"&&!part.match(/\W/)){var nodeCheck=part=isXML?part:part.toUpperCase();checkFn=dirNodeCheck;}
checkFn("previousSibling",part,doneName,checkSet,nodeCheck,isXML);}},find:{ID:function(match,context,isXML){if(typeof context.getElementById!=="undefined"&&!isXML){var m=context.getElementById(match[1]);return m?[m]:[];}},NAME:function(match,context,isXML){if(typeof context.getElementsByName!=="undefined"){var ret=[],results=context.getElementsByName(match[1]);for(var i=0,l=results.length;i<l;i++){if(results[i].getAttribute("name")===match[1]){ret.push(results[i]);}}
return ret.length===0?null:ret;}},TAG:function(match,context){return context.getElementsByTagName(match[1]);}},preFilter:{CLASS:function(match,curLoop,inplace,result,not,isXML){match=" "+match[1].replace(/\\/g,"")+" ";if(isXML){return match;}
for(var i=0,elem;(elem=curLoop[i])!=null;i++){if(elem){if(not^(elem.className&&(" "+elem.className+" ").indexOf(match)>=0)){if(!inplace)
result.push(elem);}else if(inplace){curLoop[i]=false;}}}
return false;},ID:function(match){return match[1].replace(/\\/g,"");},TAG:function(match,curLoop){for(var i=0;curLoop[i]===false;i++){}
return curLoop[i]&&isXML(curLoop[i])?match[1]:match[1].toUpperCase();},CHILD:function(match){if(match[1]=="nth"){var test=/(-?)(\d*)n((?:\+|-)?\d*)/.exec(match[2]=="even"&&"2n"||match[2]=="odd"&&"2n+1"||!/\D/.test(match[2])&&"0n+"+match[2]||match[2]);match[2]=(test[1]+(test[2]||1))-0;match[3]=test[3]-0;}
match[0]=done++;return match;},ATTR:function(match,curLoop,inplace,result,not,isXML){var name=match[1].replace(/\\/g,"");if(!isXML&&Expr.attrMap[name]){match[1]=Expr.attrMap[name];}
if(match[2]==="~="){match[4]=" "+match[4]+" ";}
return match;},PSEUDO:function(match,curLoop,inplace,result,not){if(match[1]==="not"){if(match[3].match(chunker).length>1||/^\w/.test(match[3])){match[3]=Sizzle(match[3],null,null,curLoop);}else{var ret=Sizzle.filter(match[3],curLoop,inplace,true^not);if(!inplace){result.push.apply(result,ret);}
return false;}}else if(Expr.match.POS.test(match[0])||Expr.match.CHILD.test(match[0])){return true;}
return match;},POS:function(match){match.unshift(true);return match;}},filters:{enabled:function(elem){return elem.disabled===false&&elem.type!=="hidden";},disabled:function(elem){return elem.disabled===true;},checked:function(elem){return elem.checked===true;},selected:function(elem){elem.parentNode.selectedIndex;return elem.selected===true;},parent:function(elem){return!!elem.firstChild;},empty:function(elem){return!elem.firstChild;},has:function(elem,i,match){return!!Sizzle(match[3],elem).length;},header:function(elem){return/h\d/i.test(elem.nodeName);},text:function(elem){return"text"===elem.type;},radio:function(elem){return"radio"===elem.type;},checkbox:function(elem){return"checkbox"===elem.type;},file:function(elem){return"file"===elem.type;},password:function(elem){return"password"===elem.type;},submit:function(elem){return"submit"===elem.type;},image:function(elem){return"image"===elem.type;},reset:function(elem){return"reset"===elem.type;},button:function(elem){return"button"===elem.type||elem.nodeName.toUpperCase()==="BUTTON";},input:function(elem){return/input|select|textarea|button/i.test(elem.nodeName);}},setFilters:{first:function(elem,i){return i===0;},last:function(elem,i,match,array){return i===array.length-1;},even:function(elem,i){return i%2===0;},odd:function(elem,i){return i%2===1;},lt:function(elem,i,match){return i<match[3]-0;},gt:function(elem,i,match){return i>match[3]-0;},nth:function(elem,i,match){return match[3]-0==i;},eq:function(elem,i,match){return match[3]-0==i;}},filter:{PSEUDO:function(elem,match,i,array){var name=match[1],filter=Expr.filters[name];if(filter){return filter(elem,i,match,array);}else if(name==="contains"){return(elem.textContent||elem.innerText||"").indexOf(match[3])>=0;}else if(name==="not"){var not=match[3];for(var i=0,l=not.length;i<l;i++){if(not[i]===elem){return false;}}
return true;}},CHILD:function(elem,match){var type=match[1],node=elem;switch(type){case'only':case'first':while(node=node.previousSibling){if(node.nodeType===1)return false;}
if(type=='first')return true;node=elem;case'last':while(node=node.nextSibling){if(node.nodeType===1)return false;}
return true;case'nth':var first=match[2],last=match[3];if(first==1&&last==0){return true;}
var doneName=match[0],parent=elem.parentNode;if(parent&&(parent.sizcache!==doneName||!elem.nodeIndex)){var count=0;for(node=parent.firstChild;node;node=node.nextSibling){if(node.nodeType===1){node.nodeIndex=++count;}}
parent.sizcache=doneName;}
var diff=elem.nodeIndex-last;if(first==0){return diff==0;}else{return(diff%first==0&&diff/first>=0);}}},ID:function(elem,match){return elem.nodeType===1&&elem.getAttribute("id")===match;},TAG:function(elem,match){return(match==="*"&&elem.nodeType===1)||elem.nodeName===match;},CLASS:function(elem,match){return(" "+(elem.className||elem.getAttribute("class"))+" ")
.indexOf(match)>-1;},ATTR:function(elem,match){var name=match[1],result=Expr.attrHandle[name]?Expr.attrHandle[name](elem):elem[name]!=null?elem[name]:elem.getAttribute(name),value=result+"",type=match[2],check=match[4];return result==null?type==="!=":type==="="?value===check:type==="*="?value.indexOf(check)>=0:type==="~="?(" "+value+" ").indexOf(check)>=0:!check?value&&result!==false:type==="!="?value!=check:type==="^="?value.indexOf(check)===0:type==="$="?value.substr(value.length-check.length)===check:type==="|="?value===check||value.substr(0,check.length+1)===check+"-":false;},POS:function(elem,match,i,array){var name=match[2],filter=Expr.setFilters[name];if(filter){return filter(elem,i,match,array);}}}};var origPOS=Expr.match.POS;for(var type in Expr.match){Expr.match[type]=RegExp(Expr.match[type].source+/(?![^\[]*\])(?![^\(]*\))/.source);}
var makeArray=function(array,results){array=Array.prototype.slice.call(array);if(results){results.push.apply(results,array);return results;}
return array;};try{Array.prototype.slice.call(document.documentElement.childNodes);}catch(e){makeArray=function(array,results){var ret=results||[];if(toString.call(array)==="[object Array]"){Array.prototype.push.apply(ret,array);}else{if(typeof array.length==="number"){for(var i=0,l=array.length;i<l;i++){ret.push(array[i]);}}else{for(var i=0;array[i];i++){ret.push(array[i]);}}}
return ret;};}
var sortOrder;if(document.documentElement.compareDocumentPosition){sortOrder=function(a,b){var ret=a.compareDocumentPosition(b)&4?-1:a===b?0:1;if(ret===0){hasDuplicate=true;}
return ret;};}else if("sourceIndex"in document.documentElement){sortOrder=function(a,b){var ret=a.sourceIndex-b.sourceIndex;if(ret===0){hasDuplicate=true;}
return ret;};}else if(document.createRange){sortOrder=function(a,b){var aRange=a.ownerDocument.createRange(),bRange=b.ownerDocument.createRange();aRange.selectNode(a);aRange.collapse(true);bRange.selectNode(b);bRange.collapse(true);var ret=aRange.compareBoundaryPoints(Range.START_TO_END,bRange);if(ret===0){hasDuplicate=true;}
return ret;};}
(function(){var form=document.createElement("form"),id="script"+(new Date).getTime();form.innerHTML="<input name='"+id+"'/>";var root=document.documentElement;root.insertBefore(form,root.firstChild);if(!!document.getElementById(id)){Expr.find.ID=function(match,context,isXML){if(typeof context.getElementById!=="undefined"&&!isXML){var m=context.getElementById(match[1]);return m?m.id===match[1]||typeof m.getAttributeNode!=="undefined"&&m.getAttributeNode("id").nodeValue===match[1]?[m]:undefined:[];}};Expr.filter.ID=function(elem,match){var node=typeof elem.getAttributeNode!=="undefined"&&elem.getAttributeNode("id");return elem.nodeType===1&&node&&node.nodeValue===match;};}
root.removeChild(form);})();(function(){var div=document.createElement("div");div.appendChild(document.createComment(""));if(div.getElementsByTagName("*").length>0){Expr.find.TAG=function(match,context){var results=context.getElementsByTagName(match[1]);if(match[1]==="*"){var tmp=[];for(var i=0;results[i];i++){if(results[i].nodeType===1){tmp.push(results[i]);}}
results=tmp;}
return results;};}
div.innerHTML="<a href='#'></a>";if(div.firstChild&&typeof div.firstChild.getAttribute!=="undefined"&&div.firstChild.getAttribute("href")!=="#"){Expr.attrHandle.href=function(elem){return elem.getAttribute("href",2);};}})();if(document.querySelectorAll)(function(){var oldSizzle=Sizzle,div=document.createElement("div");div.innerHTML="<p class='TEST'></p>";if(div.querySelectorAll&&div.querySelectorAll(".TEST").length===0){return;}
Sizzle=function(query,context,extra,seed){context=context||document;if(!seed&&context.nodeType===9&&!isXML(context)){try{return makeArray(context.querySelectorAll(query),extra);}catch(e){}}
return oldSizzle(query,context,extra,seed);};Sizzle.find=oldSizzle.find;Sizzle.filter=oldSizzle.filter;Sizzle.selectors=oldSizzle.selectors;Sizzle.matches=oldSizzle.matches;})();if(document.getElementsByClassName&&document.documentElement.getElementsByClassName)(function(){var div=document.createElement("div");div.innerHTML="<div class='test e'></div><div class='test'></div>";if(div.getElementsByClassName("e").length===0)
return;div.lastChild.className="e";if(div.getElementsByClassName("e").length===1)
return;Expr.order.splice(1,0,"CLASS");Expr.find.CLASS=function(match,context,isXML){if(typeof context.getElementsByClassName!=="undefined"&&!isXML){return context.getElementsByClassName(match[1]);}};})();function dirNodeCheck(dir,cur,doneName,checkSet,nodeCheck,isXML){var sibDir=dir=="previousSibling"&&!isXML;for(var i=0,l=checkSet.length;i<l;i++){var elem=checkSet[i];if(elem){if(sibDir&&elem.nodeType===1){elem.sizcache=doneName;elem.sizset=i;}
elem=elem[dir];var match=false;while(elem){if(elem.sizcache===doneName){match=checkSet[elem.sizset];break;}
if(elem.nodeType===1&&!isXML){elem.sizcache=doneName;elem.sizset=i;}
if(elem.nodeName===cur){match=elem;break;}
elem=elem[dir];}
checkSet[i]=match;}}}
function dirCheck(dir,cur,doneName,checkSet,nodeCheck,isXML){var sibDir=dir=="previousSibling"&&!isXML;for(var i=0,l=checkSet.length;i<l;i++){var elem=checkSet[i];if(elem){if(sibDir&&elem.nodeType===1){elem.sizcache=doneName;elem.sizset=i;}
elem=elem[dir];var match=false;while(elem){if(elem.sizcache===doneName){match=checkSet[elem.sizset];break;}
if(elem.nodeType===1){if(!isXML){elem.sizcache=doneName;elem.sizset=i;}
if(typeof cur!=="string"){if(elem===cur){match=true;break;}}else if(Sizzle.filter(cur,[elem]).length>0){match=elem;break;}}
elem=elem[dir];}
checkSet[i]=match;}}}
var contains=document.compareDocumentPosition?function(a,b){return a.compareDocumentPosition(b)&16;}:function(a,b){return a!==b&&(a.contains?a.contains(b):true);};var isXML=function(elem){return elem.nodeType===9&&elem.documentElement.nodeName!=="HTML"||!!elem.ownerDocument&&isXML(elem.ownerDocument);};var posProcess=function(selector,context){var tmpSet=[],later="",match,root=context.nodeType?[context]:context;while((match=Expr.match.PSEUDO.exec(selector))){later+=match[0];selector=selector.replace(Expr.match.PSEUDO,"");}
selector=Expr.relative[selector]?selector+"*":selector;for(var i=0,l=root.length;i<l;i++){Sizzle(selector,root[i],tmpSet);}
return Sizzle.filter(later,tmpSet);};jQuery.find=Sizzle;jQuery.filter=Sizzle.filter;jQuery.expr=Sizzle.selectors;jQuery.expr[":"]=jQuery.expr.filters;Sizzle.selectors.filters.hidden=function(elem){return elem.offsetWidth===0||elem.offsetHeight===0;};Sizzle.selectors.filters.visible=function(elem){return elem.offsetWidth>0||elem.offsetHeight>0;};Sizzle.selectors.filters.animated=function(elem){return jQuery.grep(jQuery.timers,function(fn){return elem===fn.elem;}).length;};jQuery.multiFilter=function(expr,elems,not){if(not){expr=":not("+expr+")";}
return Sizzle.matches(expr,elems);};jQuery.dir=function(elem,dir){var matched=[],cur=elem[dir];while(cur&&cur!=document){if(cur.nodeType==1)
matched.push(cur);cur=cur[dir];}
return matched;};jQuery.nth=function(cur,result,dir,elem){result=result||1;var num=0;for(;cur;cur=cur[dir])
if(cur.nodeType==1&&++num==result)
break;return cur;};jQuery.sibling=function(n,elem){var r=[];for(;n;n=n.nextSibling){if(n.nodeType==1&&n!=elem)
r.push(n);}
return r;};return;window.Sizzle=Sizzle;})();jQuery.event={add:function(elem,types,handler,data){if(elem.nodeType==3||elem.nodeType==8)
return;if(elem.setInterval&&elem!=window)
elem=window;if(!handler.guid)
handler.guid=this.guid++;if(data!==undefined){var fn=handler;handler=this.proxy(fn);handler.data=data;}
var events=jQuery.data(elem,"events")||jQuery.data(elem,"events",{}),handle=jQuery.data(elem,"handle")||jQuery.data(elem,"handle",function(){return typeof jQuery!=="undefined"&&!jQuery.event.triggered?jQuery.event.handle.apply(arguments.callee.elem,arguments):undefined;});handle.elem=elem;jQuery.each(types.split(/\s+/),function(index,type){var namespaces=type.split(".");type=namespaces.shift();handler.type=namespaces.slice().sort().join(".");var handlers=events[type];if(jQuery.event.specialAll[type])
jQuery.event.specialAll[type].setup.call(elem,data,namespaces);if(!handlers){handlers=events[type]={};if(!jQuery.event.special[type]||jQuery.event.special[type].setup.call(elem,data,namespaces)===false){if(elem.addEventListener)
elem.addEventListener(type,handle,false);else if(elem.attachEvent)
elem.attachEvent("on"+type,handle);}}
handlers[handler.guid]=handler;jQuery.event.global[type]=true;});elem=null;},guid:1,global:{},remove:function(elem,types,handler){if(elem.nodeType==3||elem.nodeType==8)
return;var events=jQuery.data(elem,"events"),ret,index;if(events){if(types===undefined||(typeof types==="string"&&types.charAt(0)=="."))
for(var type in events)
this.remove(elem,type+(types||""));else{if(types.type){handler=types.handler;types=types.type;}
jQuery.each(types.split(/\s+/),function(index,type){var namespaces=type.split(".");type=namespaces.shift();var namespace=RegExp("(^|\\.)"+namespaces.slice().sort().join(".*\\.")+"(\\.|$)");if(events[type]){if(handler)
delete events[type][handler.guid];else
for(var handle in events[type])
if(namespace.test(events[type][handle].type))
delete events[type][handle];if(jQuery.event.specialAll[type])
jQuery.event.specialAll[type].teardown.call(elem,namespaces);for(ret in events[type])break;if(!ret){if(!jQuery.event.special[type]||jQuery.event.special[type].teardown.call(elem,namespaces)===false){if(elem.removeEventListener)
elem.removeEventListener(type,jQuery.data(elem,"handle"),false);else if(elem.detachEvent)
elem.detachEvent("on"+type,jQuery.data(elem,"handle"));}
ret=null;delete events[type];}}});}
for(ret in events)break;if(!ret){var handle=jQuery.data(elem,"handle");if(handle)handle.elem=null;jQuery.removeData(elem,"events");jQuery.removeData(elem,"handle");}}},trigger:function(event,data,elem,bubbling){var type=event.type||event;if(!bubbling){event=typeof event==="object"?event[expando]?event:jQuery.extend(jQuery.Event(type),event):jQuery.Event(type);if(type.indexOf("!")>=0){event.type=type=type.slice(0,-1);event.exclusive=true;}
if(!elem){event.stopPropagation();if(this.global[type])
jQuery.each(jQuery.cache,function(){if(this.events&&this.events[type])
jQuery.event.trigger(event,data,this.handle.elem);});}
if(!elem||elem.nodeType==3||elem.nodeType==8)
return undefined;event.result=undefined;event.target=elem;data=jQuery.makeArray(data);data.unshift(event);}
event.currentTarget=elem;var handle=jQuery.data(elem,"handle");if(handle)
handle.apply(elem,data);if((!elem[type]||(jQuery.nodeName(elem,'a')&&type=="click"))&&elem["on"+type]&&elem["on"+type].apply(elem,data)===false)
event.result=false;if(!bubbling&&elem[type]&&!event.isDefaultPrevented()&&!(jQuery.nodeName(elem,'a')&&type=="click")){this.triggered=true;try{elem[type]();}catch(e){}}
this.triggered=false;if(!event.isPropagationStopped()){var parent=elem.parentNode||elem.ownerDocument;if(parent)
jQuery.event.trigger(event,data,parent,true);}},handle:function(event){var all,handlers;event=arguments[0]=jQuery.event.fix(event||window.event);event.currentTarget=this;var namespaces=event.type.split(".");event.type=namespaces.shift();all=!namespaces.length&&!event.exclusive;var namespace=RegExp("(^|\\.)"+namespaces.slice().sort().join(".*\\.")+"(\\.|$)");handlers=(jQuery.data(this,"events")||{})[event.type];for(var j in handlers){var handler=handlers[j];if(all||namespace.test(handler.type)){event.handler=handler;event.data=handler.data;var ret=handler.apply(this,arguments);if(ret!==undefined){event.result=ret;if(ret===false){event.preventDefault();event.stopPropagation();}}
if(event.isImmediatePropagationStopped())
break;}}},props:"altKey attrChange attrName bubbles button cancelable charCode clientX clientY ctrlKey currentTarget data detail eventPhase fromElement handler keyCode metaKey newValue originalTarget pageX pageY prevValue relatedNode relatedTarget screenX screenY shiftKey srcElement target toElement view wheelDelta which".split(" "),fix:function(event){if(event[expando])
return event;var originalEvent=event;event=jQuery.Event(originalEvent);for(var i=this.props.length,prop;i;){prop=this.props[--i];event[prop]=originalEvent[prop];}
if(!event.target)
event.target=event.srcElement||document;if(event.target.nodeType==3)
event.target=event.target.parentNode;if(!event.relatedTarget&&event.fromElement)
event.relatedTarget=event.fromElement==event.target?event.toElement:event.fromElement;if(event.pageX==null&&event.clientX!=null){var doc=document.documentElement,body=document.body;event.pageX=event.clientX+(doc&&doc.scrollLeft||body&&body.scrollLeft||0)-(doc.clientLeft||0);event.pageY=event.clientY+(doc&&doc.scrollTop||body&&body.scrollTop||0)-(doc.clientTop||0);}
if(!event.which&&((event.charCode||event.charCode===0)?event.charCode:event.keyCode))
event.which=event.charCode||event.keyCode;if(!event.metaKey&&event.ctrlKey)
event.metaKey=event.ctrlKey;if(!event.which&&event.button)
event.which=(event.button&1?1:(event.button&2?3:(event.button&4?2:0)));return event;},proxy:function(fn,proxy){proxy=proxy||function(){return fn.apply(this,arguments);};proxy.guid=fn.guid=fn.guid||proxy.guid||this.guid++;return proxy;},special:{ready:{setup:bindReady,teardown:function(){}}},specialAll:{live:{setup:function(selector,namespaces){jQuery.event.add(this,namespaces[0],liveHandler);},teardown:function(namespaces){if(namespaces.length){var remove=0,name=RegExp("(^|\\.)"+namespaces[0]+"(\\.|$)");jQuery.each((jQuery.data(this,"events").live||{}),function(){if(name.test(this.type))
remove++;});if(remove<1)
jQuery.event.remove(this,namespaces[0],liveHandler);}}}}};jQuery.Event=function(src){if(!this.preventDefault)
return new jQuery.Event(src);if(src&&src.type){this.originalEvent=src;this.type=src.type;}else
this.type=src;this.timeStamp=now();this[expando]=true;};function returnFalse(){return false;}
function returnTrue(){return true;}
jQuery.Event.prototype={preventDefault:function(){this.isDefaultPrevented=returnTrue;var e=this.originalEvent;if(!e)
return;if(e.preventDefault)
e.preventDefault();e.returnValue=false;},stopPropagation:function(){this.isPropagationStopped=returnTrue;var e=this.originalEvent;if(!e)
return;if(e.stopPropagation)
e.stopPropagation();e.cancelBubble=true;},stopImmediatePropagation:function(){this.isImmediatePropagationStopped=returnTrue;this.stopPropagation();},isDefaultPrevented:returnFalse,isPropagationStopped:returnFalse,isImmediatePropagationStopped:returnFalse};var withinElement=function(event){var parent=event.relatedTarget;while(parent&&parent!=this)
try{parent=parent.parentNode;}
catch(e){parent=this;}
if(parent!=this){event.type=event.data;jQuery.event.handle.apply(this,arguments);}};jQuery.each({mouseover:'mouseenter',mouseout:'mouseleave'},function(orig,fix){jQuery.event.special[fix]={setup:function(){jQuery.event.add(this,orig,withinElement,fix);},teardown:function(){jQuery.event.remove(this,orig,withinElement);}};});jQuery.fn.extend({bind:function(type,data,fn){return type=="unload"?this.one(type,data,fn):this.each(function(){jQuery.event.add(this,type,fn||data,fn&&data);});},one:function(type,data,fn){var one=jQuery.event.proxy(fn||data,function(event){jQuery(this).unbind(event,one);return(fn||data).apply(this,arguments);});return this.each(function(){jQuery.event.add(this,type,one,fn&&data);});},unbind:function(type,fn){return this.each(function(){jQuery.event.remove(this,type,fn);});},trigger:function(type,data){return this.each(function(){jQuery.event.trigger(type,data,this);});},triggerHandler:function(type,data){if(this[0]){var event=jQuery.Event(type);event.preventDefault();event.stopPropagation();jQuery.event.trigger(event,data,this[0]);return event.result;}},toggle:function(fn){var args=arguments,i=1;while(i<args.length)
jQuery.event.proxy(fn,args[i++]);return this.click(jQuery.event.proxy(fn,function(event){this.lastToggle=(this.lastToggle||0)%i;event.preventDefault();return args[this.lastToggle++].apply(this,arguments)||false;}));},hover:function(fnOver,fnOut){return this.mouseenter(fnOver).mouseleave(fnOut);},ready:function(fn){bindReady();if(jQuery.isReady)
fn.call(document,jQuery);else
jQuery.readyList.push(fn);return this;},live:function(type,fn){var proxy=jQuery.event.proxy(fn);proxy.guid+=this.selector+type;jQuery(document).bind(liveConvert(type,this.selector),this.selector,proxy);return this;},die:function(type,fn){jQuery(document).unbind(liveConvert(type,this.selector),fn?{guid:fn.guid+this.selector+type}:null);return this;}});function liveHandler(event){var check=RegExp("(^|\\.)"+event.type+"(\\.|$)"),stop=true,elems=[];jQuery.each(jQuery.data(this,"events").live||[],function(i,fn){if(check.test(fn.type)){var elem=jQuery(event.target).closest(fn.data)[0];if(elem)
elems.push({elem:elem,fn:fn});}});elems.sort(function(a,b){return jQuery.data(a.elem,"closest")-jQuery.data(b.elem,"closest");});jQuery.each(elems,function(){if(this.fn.call(this.elem,event,this.fn.data)===false)
return(stop=false);});return stop;}
function liveConvert(type,selector){return["live",type,selector.replace(/\./g,"`").replace(/ /g,"|")].join(".");}
jQuery.extend({isReady:false,readyList:[],ready:function(){if(!jQuery.isReady){jQuery.isReady=true;if(jQuery.readyList){jQuery.each(jQuery.readyList,function(){this.call(document,jQuery);});jQuery.readyList=null;}
jQuery(document).triggerHandler("ready");}}});var readyBound=false;function bindReady(){if(readyBound)return;readyBound=true;if(document.addEventListener){document.addEventListener("DOMContentLoaded",function(){document.removeEventListener("DOMContentLoaded",arguments.callee,false);jQuery.ready();},false);}else if(document.attachEvent){document.attachEvent("onreadystatechange",function(){if(document.readyState==="complete"){document.detachEvent("onreadystatechange",arguments.callee);jQuery.ready();}});if(document.documentElement.doScroll&&window==window.top)(function(){if(jQuery.isReady)return;try{document.documentElement.doScroll("left");}catch(error){setTimeout(arguments.callee,0);return;}
jQuery.ready();})();}
jQuery.event.add(window,"load",jQuery.ready);}
jQuery.each(("blur,focus,load,resize,scroll,unload,click,dblclick,"+
"mousedown,mouseup,mousemove,mouseover,mouseout,mouseenter,mouseleave,"+
"change,select,submit,keydown,keypress,keyup,error").split(","),function(i,name){jQuery.fn[name]=function(fn){return fn?this.bind(name,fn):this.trigger(name);};});jQuery(window).bind('unload',function(){for(var id in jQuery.cache)
if(id!=1&&jQuery.cache[id].handle)
jQuery.event.remove(jQuery.cache[id].handle.elem);});(function(){jQuery.support={};var root=document.documentElement,script=document.createElement("script"),div=document.createElement("div"),id="script"+(new Date).getTime();div.style.display="none";div.innerHTML='   <link/><table></table><a href="/a" style="color:red;float:left;opacity:.5;">a</a><select><option>text</option></select><object><param/></object>';var all=div.getElementsByTagName("*"),a=div.getElementsByTagName("a")[0];if(!all||!all.length||!a){return;}
jQuery.support={leadingWhitespace:div.firstChild.nodeType==3,tbody:!div.getElementsByTagName("tbody").length,objectAll:!!div.getElementsByTagName("object")[0]
.getElementsByTagName("*").length,htmlSerialize:!!div.getElementsByTagName("link").length,style:/red/.test(a.getAttribute("style")),hrefNormalized:a.getAttribute("href")==="/a",opacity:a.style.opacity==="0.5",cssFloat:!!a.style.cssFloat,scriptEval:false,noCloneEvent:true,boxModel:null};script.type="text/javascript";try{script.appendChild(document.createTextNode("window."+id+"=1;"));}catch(e){}
root.insertBefore(script,root.firstChild);if(window[id]){jQuery.support.scriptEval=true;delete window[id];}
root.removeChild(script);if(div.attachEvent&&div.fireEvent){div.attachEvent("onclick",function(){jQuery.support.noCloneEvent=false;div.detachEvent("onclick",arguments.callee);});div.cloneNode(true).fireEvent("onclick");}
jQuery(function(){var div=document.createElement("div");div.style.width=div.style.paddingLeft="1px";document.body.appendChild(div);jQuery.boxModel=jQuery.support.boxModel=div.offsetWidth===2;document.body.removeChild(div).style.display='none';});})();var styleFloat=jQuery.support.cssFloat?"cssFloat":"styleFloat";jQuery.props={"for":"htmlFor","class":"className","float":styleFloat,cssFloat:styleFloat,styleFloat:styleFloat,readonly:"readOnly",maxlength:"maxLength",cellspacing:"cellSpacing",rowspan:"rowSpan",tabindex:"tabIndex"};jQuery.fn.extend({_load:jQuery.fn.load,load:function(url,params,callback){if(typeof url!=="string")
return this._load(url);var off=url.indexOf(" ");if(off>=0){var selector=url.slice(off,url.length);url=url.slice(0,off);}
var type="GET";if(params)
if(jQuery.isFunction(params)){callback=params;params=null;}else if(typeof params==="object"){params=jQuery.param(params);type="POST";}
var self=this;jQuery.ajax({url:url,type:type,dataType:"html",data:params,complete:function(res,status){if(status=="success"||status=="notmodified")
self.html(selector?jQuery("<div/>")
.append(res.responseText.replace(/<script(.|\s)*?\/script>/g,""))
.find(selector):res.responseText);if(callback)
self.each(callback,[res.responseText,status,res]);}});return this;},serialize:function(){return jQuery.param(this.serializeArray());},serializeArray:function(){return this.map(function(){return this.elements?jQuery.makeArray(this.elements):this;})
.filter(function(){return this.name&&!this.disabled&&(this.checked||/select|textarea/i.test(this.nodeName)||/text|hidden|password|search/i.test(this.type));})
.map(function(i,elem){var val=jQuery(this).val();return val==null?null:jQuery.isArray(val)?jQuery.map(val,function(val,i){return{name:elem.name,value:val};}):{name:elem.name,value:val};}).get();}});jQuery.each("ajaxStart,ajaxStop,ajaxComplete,ajaxError,ajaxSuccess,ajaxSend".split(","),function(i,o){jQuery.fn[o]=function(f){return this.bind(o,f);};});var jsc=now();jQuery.extend({get:function(url,data,callback,type){if(jQuery.isFunction(data)){callback=data;data=null;}
return jQuery.ajax({type:"GET",url:url,data:data,success:callback,dataType:type});},getScript:function(url,callback){return jQuery.get(url,null,callback,"script");},getJSON:function(url,data,callback){return jQuery.get(url,data,callback,"json");},post:function(url,data,callback,type){if(jQuery.isFunction(data)){callback=data;data={};}
return jQuery.ajax({type:"POST",url:url,data:data,success:callback,dataType:type});},ajaxSetup:function(settings){jQuery.extend(jQuery.ajaxSettings,settings);},ajaxSettings:{url:location.href,global:true,type:"GET",contentType:"application/x-www-form-urlencoded",processData:true,async:true,xhr:function(){return window.ActiveXObject?new ActiveXObject("Microsoft.XMLHTTP"):new XMLHttpRequest();},accepts:{xml:"application/xml, text/xml",html:"text/html",script:"text/javascript, application/javascript",json:"application/json, text/javascript",text:"text/plain",_default:"*/*"}},lastModified:{},ajax:function(s){s=jQuery.extend(true,s,jQuery.extend(true,{},jQuery.ajaxSettings,s));var jsonp,jsre=/=\?(&|$)/g,status,data,type=s.type.toUpperCase();if(s.data&&s.processData&&typeof s.data!=="string")
s.data=jQuery.param(s.data);if(s.dataType=="jsonp"){if(type=="GET"){if(!s.url.match(jsre))
s.url+=(s.url.match(/\?/)?"&":"?")+(s.jsonp||"callback")+"=?";}else if(!s.data||!s.data.match(jsre))
s.data=(s.data?s.data+"&":"")+(s.jsonp||"callback")+"=?";s.dataType="json";}
if(s.dataType=="json"&&(s.data&&s.data.match(jsre)||s.url.match(jsre))){jsonp="jsonp"+jsc++;if(s.data)
s.data=(s.data+"").replace(jsre,"="+jsonp+"$1");s.url=s.url.replace(jsre,"="+jsonp+"$1");s.dataType="script";window[jsonp]=function(tmp){data=tmp;success();complete();window[jsonp]=undefined;try{delete window[jsonp];}catch(e){}
if(head)
head.removeChild(script);};}
if(s.dataType=="script"&&s.cache==null)
s.cache=false;if(s.cache===false&&type=="GET"){var ts=now();var ret=s.url.replace(/(\?|&)_=.*?(&|$)/,"$1_="+ts+"$2");s.url=ret+((ret==s.url)?(s.url.match(/\?/)?"&":"?")+"_="+ts:"");}
if(s.data&&type=="GET"){s.url+=(s.url.match(/\?/)?"&":"?")+s.data;s.data=null;}
if(s.global&&!jQuery.active++)
jQuery.event.trigger("ajaxStart");var parts=/^(\w+:)?\/\/([^\/?#]+)/.exec(s.url);if(s.dataType=="script"&&type=="GET"&&parts&&(parts[1]&&parts[1]!=location.protocol||parts[2]!=location.host)){var head=document.getElementsByTagName("head")[0];var script=document.createElement("script");script.src=s.url;if(s.scriptCharset)
script.charset=s.scriptCharset;if(!jsonp){var done=false;script.onload=script.onreadystatechange=function(){if(!done&&(!this.readyState||this.readyState=="loaded"||this.readyState=="complete")){done=true;success();complete();script.onload=script.onreadystatechange=null;head.removeChild(script);}};}
head.appendChild(script);return undefined;}
var requestDone=false;var xhr=s.xhr();if(s.username)
xhr.open(type,s.url,s.async,s.username,s.password);else
xhr.open(type,s.url,s.async);try{if(s.data)
xhr.setRequestHeader("Content-Type",s.contentType);if(s.ifModified)
xhr.setRequestHeader("If-Modified-Since",jQuery.lastModified[s.url]||"Thu, 01 Jan 1970 00:00:00 GMT");xhr.setRequestHeader("X-Requested-With","XMLHttpRequest");xhr.setRequestHeader("Accept",s.dataType&&s.accepts[s.dataType]?s.accepts[s.dataType]+", */*":s.accepts._default);}catch(e){}
if(s.beforeSend&&s.beforeSend(xhr,s)===false){if(s.global&&!--jQuery.active)
jQuery.event.trigger("ajaxStop");xhr.abort();return false;}
if(s.global)
jQuery.event.trigger("ajaxSend",[xhr,s]);var onreadystatechange=function(isTimeout){if(xhr.readyState==0){if(ival){clearInterval(ival);ival=null;if(s.global&&!--jQuery.active)
jQuery.event.trigger("ajaxStop");}
}else if(!requestDone&&xhr&&(xhr.readyState==4||isTimeout=="timeout")){requestDone=true;if(ival){clearInterval(ival);ival=null;}
status=isTimeout=="timeout"?"timeout":!jQuery.httpSuccess(xhr)?"error":s.ifModified&&jQuery.httpNotModified(xhr,s.url)?"notmodified":"success";if(status=="success"){try{data=jQuery.httpData(xhr,s.dataType,s);}catch(e){status="parsererror";}}
if(status=="success"){var modRes;try{modRes=xhr.getResponseHeader("Last-Modified");}catch(e){}
if(s.ifModified&&modRes)
jQuery.lastModified[s.url]=modRes;if(!jsonp)
success();}else
jQuery.handleError(s,xhr,status);complete();if(isTimeout)
xhr.abort();if(s.async)
xhr=null;}};if(s.async){var ival=setInterval(onreadystatechange,13);if(s.timeout>0)
setTimeout(function(){if(xhr&&!requestDone)
onreadystatechange("timeout");},s.timeout);}
try{xhr.send(s.data);}catch(e){jQuery.handleError(s,xhr,null,e);}
if(!s.async)
onreadystatechange();function success(){if(s.success)
s.success(data,status);if(s.global)
jQuery.event.trigger("ajaxSuccess",[xhr,s]);}
function complete(){if(s.complete)
s.complete(xhr,status);if(s.global)
jQuery.event.trigger("ajaxComplete",[xhr,s]);if(s.global&&!--jQuery.active)
jQuery.event.trigger("ajaxStop");}
return xhr;},handleError:function(s,xhr,status,e){if(s.error)s.error(xhr,status,e);if(s.global)
jQuery.event.trigger("ajaxError",[xhr,s,e]);},active:0,httpSuccess:function(xhr){try{return!xhr.status&&location.protocol=="file:"||(xhr.status>=200&&xhr.status<300)||xhr.status==304||xhr.status==1223;}catch(e){}
return false;},httpNotModified:function(xhr,url){try{var xhrRes=xhr.getResponseHeader("Last-Modified");return xhr.status==304||xhrRes==jQuery.lastModified[url];}catch(e){}
return false;},httpData:function(xhr,type,s){var ct=xhr.getResponseHeader("content-type"),xml=type=="xml"||!type&&ct&&ct.indexOf("xml")>=0,data=xml?xhr.responseXML:xhr.responseText;if(xml&&data.documentElement.tagName=="parsererror")
throw"parsererror";if(s&&s.dataFilter)
data=s.dataFilter(data,type);if(typeof data==="string"){if(type=="script")
jQuery.globalEval(data);if(type=="json")
data=window["eval"]("("+data+")");}
return data;},param:function(a){var s=[];function add(key,value){s[s.length]=encodeURIComponent(key)+'='+encodeURIComponent(value);};if(jQuery.isArray(a)||a.jquery)
jQuery.each(a,function(){add(this.name,this.value);});else
for(var j in a)
if(jQuery.isArray(a[j]))
jQuery.each(a[j],function(){add(j,this);});else
add(j,jQuery.isFunction(a[j])?a[j]():a[j]);return s.join("&").replace(/%20/g,"+");}});var elemdisplay={},timerId,fxAttrs=[["height","marginTop","marginBottom","paddingTop","paddingBottom"],["width","marginLeft","marginRight","paddingLeft","paddingRight"],["opacity"]];function genFx(type,num){var obj={};jQuery.each(fxAttrs.concat.apply([],fxAttrs.slice(0,num)),function(){obj[this]=type;});return obj;}
jQuery.fn.extend({show:function(speed,callback){if(speed){return this.animate(genFx("show",3),speed,callback);}else{for(var i=0,l=this.length;i<l;i++){var old=jQuery.data(this[i],"olddisplay");this[i].style.display=old||"";if(jQuery.css(this[i],"display")==="none"){var tagName=this[i].tagName,display;if(elemdisplay[tagName]){display=elemdisplay[tagName];}else{var elem=jQuery("<"+tagName+" />").appendTo("body");display=elem.css("display");if(display==="none")
display="block";elem.remove();elemdisplay[tagName]=display;}
jQuery.data(this[i],"olddisplay",display);}}
for(var i=0,l=this.length;i<l;i++){this[i].style.display=jQuery.data(this[i],"olddisplay")||"";}
return this;}},hide:function(speed,callback){if(speed){return this.animate(genFx("hide",3),speed,callback);}else{for(var i=0,l=this.length;i<l;i++){var old=jQuery.data(this[i],"olddisplay");if(!old&&old!=="none")
jQuery.data(this[i],"olddisplay",jQuery.css(this[i],"display"));}
for(var i=0,l=this.length;i<l;i++){this[i].style.display="none";}
return this;}},_toggle:jQuery.fn.toggle,toggle:function(fn,fn2){var bool=typeof fn==="boolean";return jQuery.isFunction(fn)&&jQuery.isFunction(fn2)?this._toggle.apply(this,arguments):fn==null||bool?this.each(function(){var state=bool?fn:jQuery(this).is(":hidden");jQuery(this)[state?"show":"hide"]();}):this.animate(genFx("toggle",3),fn,fn2);},fadeTo:function(speed,to,callback){return this.animate({opacity:to},speed,callback);},animate:function(prop,speed,easing,callback){var optall=jQuery.speed(speed,easing,callback);return this[optall.queue===false?"each":"queue"](function(){var opt=jQuery.extend({},optall),p,hidden=this.nodeType==1&&jQuery(this).is(":hidden"),self=this;for(p in prop){if(prop[p]=="hide"&&hidden||prop[p]=="show"&&!hidden)
return opt.complete.call(this);if((p=="height"||p=="width")&&this.style){opt.display=jQuery.css(this,"display");opt.overflow=this.style.overflow;}}
if(opt.overflow!=null)
this.style.overflow="hidden";opt.curAnim=jQuery.extend({},prop);jQuery.each(prop,function(name,val){var e=new jQuery.fx(self,opt,name);if(/toggle|show|hide/.test(val))
e[val=="toggle"?hidden?"show":"hide":val](prop);else{var parts=val.toString().match(/^([+-]=)?([\d+-.]+)(.*)$/),start=e.cur(true)||0;if(parts){var end=parseFloat(parts[2]),unit=parts[3]||"px";if(unit!="px"){self.style[name]=(end||1)+unit;start=((end||1)/e.cur(true))*start;self.style[name]=start+unit;}
if(parts[1])
end=((parts[1]=="-="?-1:1)*end)+start;e.custom(start,end,unit);}else
e.custom(start,val,"");}});return true;});},stop:function(clearQueue,gotoEnd){var timers=jQuery.timers;if(clearQueue)
this.queue([]);this.each(function(){for(var i=timers.length-1;i>=0;i--)
if(timers[i].elem==this){if(gotoEnd)
timers[i](true);timers.splice(i,1);}});if(!gotoEnd)
this.dequeue();return this;}});jQuery.each({slideDown:genFx("show",1),slideUp:genFx("hide",1),slideToggle:genFx("toggle",1),fadeIn:{opacity:"show"},fadeOut:{opacity:"hide"}},function(name,props){jQuery.fn[name]=function(speed,callback){return this.animate(props,speed,callback);};});jQuery.extend({speed:function(speed,easing,fn){var opt=typeof speed==="object"?speed:{complete:fn||!fn&&easing||jQuery.isFunction(speed)&&speed,duration:speed,easing:fn&&easing||easing&&!jQuery.isFunction(easing)&&easing};opt.duration=jQuery.fx.off?0:typeof opt.duration==="number"?opt.duration:jQuery.fx.speeds[opt.duration]||jQuery.fx.speeds._default;opt.old=opt.complete;opt.complete=function(){if(opt.queue!==false)
jQuery(this).dequeue();if(jQuery.isFunction(opt.old))
opt.old.call(this);};return opt;},easing:{linear:function(p,n,firstNum,diff){return firstNum+diff*p;},swing:function(p,n,firstNum,diff){return((-Math.cos(p*Math.PI)/2)+0.5)*diff+firstNum;}},timers:[],fx:function(elem,options,prop){this.options=options;this.elem=elem;this.prop=prop;if(!options.orig)
options.orig={};}});jQuery.fx.prototype={update:function(){if(this.options.step)
this.options.step.call(this.elem,this.now,this);(jQuery.fx.step[this.prop]||jQuery.fx.step._default)(this);if((this.prop=="height"||this.prop=="width")&&this.elem.style)
this.elem.style.display="block";},cur:function(force){if(this.elem[this.prop]!=null&&(!this.elem.style||this.elem.style[this.prop]==null))
return this.elem[this.prop];var r=parseFloat(jQuery.css(this.elem,this.prop,force));return r&&r>-10000?r:parseFloat(jQuery.curCSS(this.elem,this.prop))||0;},custom:function(from,to,unit){this.startTime=now();this.start=from;this.end=to;this.unit=unit||this.unit||"px";this.now=this.start;this.pos=this.state=0;var self=this;function t(gotoEnd){return self.step(gotoEnd);}
t.elem=this.elem;if(t()&&jQuery.timers.push(t)&&!timerId){timerId=setInterval(function(){var timers=jQuery.timers;for(var i=0;i<timers.length;i++)
if(!timers[i]())
timers.splice(i--,1);if(!timers.length){clearInterval(timerId);timerId=undefined;}},13);}},show:function(){this.options.orig[this.prop]=jQuery.attr(this.elem.style,this.prop);this.options.show=true;this.custom(this.prop=="width"||this.prop=="height"?1:0,this.cur());jQuery(this.elem).show();},hide:function(){this.options.orig[this.prop]=jQuery.attr(this.elem.style,this.prop);this.options.hide=true;this.custom(this.cur(),0);},step:function(gotoEnd){var t=now();if(gotoEnd||t>=this.options.duration+this.startTime){this.now=this.end;this.pos=this.state=1;this.update();this.options.curAnim[this.prop]=true;var done=true;for(var i in this.options.curAnim)
if(this.options.curAnim[i]!==true)
done=false;if(done){if(this.options.display!=null){this.elem.style.overflow=this.options.overflow;this.elem.style.display=this.options.display;if(jQuery.css(this.elem,"display")=="none")
this.elem.style.display="block";}
if(this.options.hide)
jQuery(this.elem).hide();if(this.options.hide||this.options.show)
for(var p in this.options.curAnim)
jQuery.attr(this.elem.style,p,this.options.orig[p]);this.options.complete.call(this.elem);}
return false;}else{var n=t-this.startTime;this.state=n/this.options.duration;this.pos=jQuery.easing[this.options.easing||(jQuery.easing.swing?"swing":"linear")](this.state,n,0,1,this.options.duration);this.now=this.start+((this.end-this.start)*this.pos);this.update();}
return true;}};jQuery.extend(jQuery.fx,{speeds:{slow:600,fast:200,_default:400},step:{opacity:function(fx){jQuery.attr(fx.elem.style,"opacity",fx.now);},_default:function(fx){if(fx.elem.style&&fx.elem.style[fx.prop]!=null)
fx.elem.style[fx.prop]=fx.now+fx.unit;else
fx.elem[fx.prop]=fx.now;}}});if(document.documentElement["getBoundingClientRect"])
jQuery.fn.offset=function(){if(!this[0])return{top:0,left:0};if(this[0]===this[0].ownerDocument.body)return jQuery.offset.bodyOffset(this[0]);var box=this[0].getBoundingClientRect(),doc=this[0].ownerDocument,body=doc.body,docElem=doc.documentElement,clientTop=docElem.clientTop||body.clientTop||0,clientLeft=docElem.clientLeft||body.clientLeft||0,top=box.top+(self.pageYOffset||jQuery.boxModel&&docElem.scrollTop||body.scrollTop)-clientTop,left=box.left+(self.pageXOffset||jQuery.boxModel&&docElem.scrollLeft||body.scrollLeft)-clientLeft;return{top:top,left:left};};else
jQuery.fn.offset=function(){if(!this[0])return{top:0,left:0};if(this[0]===this[0].ownerDocument.body)return jQuery.offset.bodyOffset(this[0]);jQuery.offset.initialized||jQuery.offset.initialize();var elem=this[0],offsetParent=elem.offsetParent,prevOffsetParent=elem,doc=elem.ownerDocument,computedStyle,docElem=doc.documentElement,body=doc.body,defaultView=doc.defaultView,prevComputedStyle=defaultView.getComputedStyle(elem,null),top=elem.offsetTop,left=elem.offsetLeft;while((elem=elem.parentNode)&&elem!==body&&elem!==docElem){computedStyle=defaultView.getComputedStyle(elem,null);top-=elem.scrollTop,left-=elem.scrollLeft;if(elem===offsetParent){top+=elem.offsetTop,left+=elem.offsetLeft;if(jQuery.offset.doesNotAddBorder&&!(jQuery.offset.doesAddBorderForTableAndCells&&/^t(able|d|h)$/i.test(elem.tagName)))
top+=parseInt(computedStyle.borderTopWidth,10)||0,left+=parseInt(computedStyle.borderLeftWidth,10)||0;prevOffsetParent=offsetParent,offsetParent=elem.offsetParent;}
if(jQuery.offset.subtractsBorderForOverflowNotVisible&&computedStyle.overflow!=="visible")
top+=parseInt(computedStyle.borderTopWidth,10)||0,left+=parseInt(computedStyle.borderLeftWidth,10)||0;prevComputedStyle=computedStyle;}
if(prevComputedStyle.position==="relative"||prevComputedStyle.position==="static")
top+=body.offsetTop,left+=body.offsetLeft;if(prevComputedStyle.position==="fixed")
top+=Math.max(docElem.scrollTop,body.scrollTop),left+=Math.max(docElem.scrollLeft,body.scrollLeft);return{top:top,left:left};};jQuery.offset={initialize:function(){if(this.initialized)return;var body=document.body,container=document.createElement('div'),innerDiv,checkDiv,table,td,rules,prop,bodyMarginTop=body.style.marginTop,html='<div style="position:absolute;top:0;left:0;margin:0;border:5px solid #000;padding:0;width:1px;height:1px;"><div></div></div><table style="position:absolute;top:0;left:0;margin:0;border:5px solid #000;padding:0;width:1px;height:1px;" cellpadding="0" cellspacing="0"><tr><td></td></tr></table>';rules={position:'absolute',top:0,left:0,margin:0,border:0,width:'1px',height:'1px',visibility:'hidden'};for(prop in rules)container.style[prop]=rules[prop];container.innerHTML=html;body.insertBefore(container,body.firstChild);innerDiv=container.firstChild,checkDiv=innerDiv.firstChild,td=innerDiv.nextSibling.firstChild.firstChild;this.doesNotAddBorder=(checkDiv.offsetTop!==5);this.doesAddBorderForTableAndCells=(td.offsetTop===5);innerDiv.style.overflow='hidden',innerDiv.style.position='relative';this.subtractsBorderForOverflowNotVisible=(checkDiv.offsetTop===-5);body.style.marginTop='1px';this.doesNotIncludeMarginInBodyOffset=(body.offsetTop===0);body.style.marginTop=bodyMarginTop;body.removeChild(container);this.initialized=true;},bodyOffset:function(body){jQuery.offset.initialized||jQuery.offset.initialize();var top=body.offsetTop,left=body.offsetLeft;if(jQuery.offset.doesNotIncludeMarginInBodyOffset)
top+=parseInt(jQuery.curCSS(body,'marginTop',true),10)||0,left+=parseInt(jQuery.curCSS(body,'marginLeft',true),10)||0;return{top:top,left:left};}};jQuery.fn.extend({position:function(){var left=0,top=0,results;if(this[0]){var offsetParent=this.offsetParent(),offset=this.offset(),parentOffset=/^body|html$/i.test(offsetParent[0].tagName)?{top:0,left:0}:offsetParent.offset();offset.top-=num(this,'marginTop');offset.left-=num(this,'marginLeft');parentOffset.top+=num(offsetParent,'borderTopWidth');parentOffset.left+=num(offsetParent,'borderLeftWidth');results={top:offset.top-parentOffset.top,left:offset.left-parentOffset.left};}
return results;},offsetParent:function(){var offsetParent=this[0].offsetParent||document.body;while(offsetParent&&(!/^body|html$/i.test(offsetParent.tagName)&&jQuery.css(offsetParent,'position')=='static'))
offsetParent=offsetParent.offsetParent;return jQuery(offsetParent);}});jQuery.each(['Left','Top'],function(i,name){var method='scroll'+name;jQuery.fn[method]=function(val){if(!this[0])return null;return val!==undefined?this.each(function(){this==window||this==document?window.scrollTo(!i?val:jQuery(window).scrollLeft(),i?val:jQuery(window).scrollTop()):this[method]=val;}):this[0]==window||this[0]==document?self[i?'pageYOffset':'pageXOffset']||jQuery.boxModel&&document.documentElement[method]||document.body[method]:this[0][method];};});jQuery.each(["Height","Width"],function(i,name){var tl=i?"Left":"Top",br=i?"Right":"Bottom",lower=name.toLowerCase();jQuery.fn["inner"+name]=function(){return this[0]?jQuery.css(this[0],lower,false,"padding"):null;};jQuery.fn["outer"+name]=function(margin){return this[0]?jQuery.css(this[0],lower,false,margin?"margin":"border"):null;};var type=name.toLowerCase();jQuery.fn[type]=function(size){return this[0]==window?document.compatMode=="CSS1Compat"&&document.documentElement["client"+name]||document.body["client"+name]:this[0]==document?Math.max(document.documentElement["client"+name],document.body["scroll"+name],document.documentElement["scroll"+name],document.body["offset"+name],document.documentElement["offset"+name]):size===undefined?(this.length?jQuery.css(this[0],type):null):this.css(type,typeof size==="string"?size:size+"px");};});})();;(function($){function evalMetadata(json){var key,results,result;if(json&&/\S/.test(json)){key='evalMetadata_'+new Date().getTime();results=window.evalMetadata_results||(window.evalMetadata_results={});$.globalEval('window.evalMetadata_results.'+key+' = '+json);if(key in results){result=results[key];delete results[key];}}
return result;}
$.extend({metadata:{defaults:{type:'class',name:'metadata',cre:/({.*})/,single:'metadata'},setType:function(type,name){this.defaults.type=type;this.defaults.name=name;},get:function(elem,opts){var settings=$.extend({},this.defaults,opts);if(!settings.single.length)settings.single='metadata';var data=$.data(elem,settings.single);if(data)return data;data="{}";if(settings.type=="class"){var m=settings.cre.exec(elem.className);if(m)
data=m[1];}else if(settings.type=="elem"){if(!elem.getElementsByTagName)return;var e=elem.getElementsByTagName(settings.name);if(e.length)
data=$.trim(e[0].innerHTML);}else if(elem.getAttribute!=undefined){var attr=elem.getAttribute(settings.name);if(attr)
data=attr;}
if(data.indexOf('{')<0)
data="{"+data+"}";data=evalMetadata("("+data+")");$.data(elem,settings.single,data);return data;}}});$.fn.metadata=function(opts){return $.metadata.get(this[0],opts);};})(jQuery);;(function($){window.Slash||(window.Slash={});Slash.jQuery=$;$.ajaxSetup({url:'/ajax.pl',type:'POST',contentType:'application/x-www-form-urlencoded'});})(jQuery);window.evalExpr=function(json){var key,results=window.evalExpr,result;if(json&&/\S/.test(json)){key='evalExpr_'+new Date().getTime();$.globalEval('window.evalExpr.'+key+' = '+json);if(key in results){result=results[key];delete results[key];}}
return result;}
var Qw;(function(){var ANY_WS=/\s+/,OUTER_WS=/^\s+|\s+$/g;function clean(qw){if(typeof(qw)==='string'&&(qw=qw.replace(OUTER_WS,''))){qw=qw.split(ANY_WS);}
return qw;}
function make_array(qw){if(!(qw=clean(qw))){return[];}
if(!TypeOf.list(qw)){qw=slashcore.reduce(qw,[],function(k,v){v&&this.push(k);});}
return qw;}
function make_set(qw){if(!(qw=clean(qw))){return{};}
if(TypeOf.list(qw)){qw=slashcore.reduce(qw,{},function(i,v){this[v]=true;});}
return qw;}
function make_string(qw){return typeof(qw)==='string'?qw.replace(OUTER_WS,''):make_array(qw).join(' ');}
Qw=$.extend(make_array,{as_array:make_array,as_set:make_set,as_string:make_string});})();var fhitem_info,fhitem_key;(function($){var KEY_TYPE=/\bsd-key-([-a-z]+)/i;fhitem_info=function(item,type){return $('span.sd-info-block span.'+type,item).text();}
fhitem_key=function(item){var result;$('span.sd-info-block span[class^=sd-key-]',item).each(function(){result={key:$(this).text(),key_type:KEY_TYPE.exec(this.className)[1]};return false;});return result;}})(jQuery);$.fn.extend({getClass:function(){return this.attr('className');},setClass:function(expr){if(!expr||!expr.call){return this.attr('className',expr);}else{return this.each(function(){this.className=Qw.as_string(expr.call(this,Qw.as_set(this.className)));});}}});function sign(o){return TypeOf.number(o)&&o<0&&-1||(o?1:0);}
function between(lo,o,hi){if(lo<=hi){return o<lo&&-1||o>hi&&1||0;}}
function pin_between(lo,o,hi){var b=between(lo,o,hi);if(b!==undefined){return arguments[1+between(lo,o,hi)];}}
function applyToggle(map){return function(names){$.each(map,function(k,v){names[k]=(v=sign(v))<0?!names[k]:v;});return names;};}
function applyMap(){var map={},N=arguments.length;if(N>1){for(var i=0;i<N;++i){map[arguments[i]]=arguments[(i+1)%N];}}else{map=arguments[0];}
return function(old_names){var new_names={};$.each(old_names,function(k,v){new_names[map[k]||k]=v;});return new_names;};}
function $any(expr){var el;return!expr&&$([])||typeof(expr)==='string'&&(el=document.getElementById(expr))&&$(el)||$(expr);}
function elemAny(expr){return $any(expr)[0];}
var $dom=elemAny;function original_target(e,selector){var	old_target=e.originalTarget||e.originalEvent&&e.originalEvent.target||e.target,new_target=selector?$(old_target).closest(selector)[0]:old_target;old_target!==new_target&&(e.originalTarget=new_target);return new_target;};function Size(){var	bare=this.__isa!==Size,self=bare?new Size:this,args=bare&&!arguments.length?[this]:arguments;return Size.prototype.assign.apply(self,args);}
function Position(){var	bare=this.__isa!==Position,self=bare?new Position:this,args=bare&&!arguments.length?[this]:arguments;return Position.prototype.assign.apply(self,args);}
function Bounds(){var	bare=this.__isa!==Bounds,self=bare?new Bounds:this,args=bare&&!arguments.length?[this]:arguments;return Bounds.prototype.assign.apply(self,args);}
(function(){function _unwrap(o,allow_lists){if(TypeOf(o)==='string'){var el=document.getElementById(o);o=el?el:$(o);}
return allow_lists||!TypeOf.list(o)?o:o[0];}
function _isSize(o){var t=TypeOf(o),isNum=TypeOf.number;if(t==='size'||o&&isNum(o.height)&&isNum(o.width)){return t;}}
function _hasSize(o){var t=TypeOf(o),isFn=TypeOf.fn;if(o&&isFn(o.height)&&isFn(o.width)){return t;}}
function _isPosition(o){var t=TypeOf(o),isNum=TypeOf.number;if(t==='position'||t==='bounds'||t!=='window'&&o&&isNum(o.top)&&isNum(o.left)){return t;}}
function _isBounds(o){var t=TypeOf(o),isNum=TypeOf.number;if(t==='bounds'||t!=='window'&&o&&isNum(o.top)&&isNum(o.left)&&isNum(o.bottom)&&isNum(o.right)){return t;}}
Size.prototype={__isa:Size,__typeOf:function(){return'size';},assign:function(o){switch(!!o&&TypeOf(o=_unwrap(o))){case'document':case'element':case'window':o=$(o);default:if(_isSize(o)){break;}
if(_hasSize(o)){o={height:o.height(),width:o.width()};break;}
if(_isBounds(o)){o={height:o.bottom-o.top,width:o.right-o.left};break;}
case'undefined':case'null':case false:o={height:0,width:0};}
this.height=o.height;this.width=o.width;return this;},toString:function(){return'{ height:'+this.height+', width:'+this.width+' }';}};Size._expected=function(o){return _isSize(o)?o:new Size(o);};Position.prototype={__isa:Position,__typeOf:function(){return'position';},assign:function(o){if(!_isPosition(o)){switch(!!o&&TypeOf(o=_unwrap(o))){case'window':o=$(o);o={top:o.scrollTop(),left:o.scrollLeft()};break;case'element':o=$(o).offset();break;default:o={top:0,left:0};}}
this.top=o.top;this.left=o.left;return this;},toString:function(){return'{ top:'+this.top+', left:'+this.left+' }';}};Position._expected=function(o){return _isPosition(o)?o:new Position(o);};Bounds.prototype={__isa:Bounds,__typeOf:function(){return'bounds';},assign:function(o1,o2){if(_isBounds(o1)){this.top=o1.top;this.left=o1.left;this.bottom=o1.bottom;this.right=o1.right;}else{var po1=Position._expected(o1);this.top=this.bottom=po1.top;this.left=this.right=po1.left;arguments.length==1&&(o2=Size(o1));if(_isPosition(o2)){this.bottom=o2.top;this.right=o2.left;}else if(_isSize(o2)){this.bottom+=o2.height;this.right+=o2.width;}}
return this;},toString:function(){return'{ top:'+this.top+', left:'+this.left+', bottom:'+this.bottom+', right:'+this.right+' }';},height:function(){return this.bottom-this.top;},width:function(){return this.right-this.left;},union:function(o){o=Bounds._expected(o);o.top<this.top&&(this.top=o.top);o.left<this.left&&(this.left=o.left);o.bottom>this.bottom&&(this.bottom=o.bottom);o.right>this.right&&(this.right=o.right);return this;},intersect:function(o){o=Bounds._expected(o);o.top>this.top&&(this.top=o.top);o.left>this.left&&(this.left=o.left);o.bottom<this.bottom&&(this.bottom=o.bottom);o.right<this.right&&(this.right=o.right);return this;}};Bounds._expected=function(o){return _isBounds(o)?o:new Bounds(o);};Bounds.empty=function(o){o=Bounds._expected(o);return o.bottom<=o.top||o.right<=o.left;};Bounds.equal=function(a,b){a=Bounds._expected(a);b=Bounds._expected(b);return a.top==b.top&&a.left==b.left&&a.bottom==b.bottom&&a.right==b.right;};function _each_op(a,b){var	result=new Bounds(a=_unwrap(a,true)),A=arguments.length==1&&TypeOf.list(a)?a:arguments;for(var i=1;i<A.length;++i){result[this](A[i]);}
return result;}
Bounds.union=function(){return _each_op.apply('union',arguments);};Bounds.intersection=function(){return _each_op.apply('intersect',arguments);};Bounds.intersect=function(a,b){return!Bounds.empty(Bounds.intersection(a,b));};Bounds.contain=function(a,b){return Bounds.equal(a,Bounds.union(a,b));};Bounds.y=function(o){var bounds=new Bounds(o);bounds.left=-Infinity;bounds.right=Infinity;return bounds;}
Bounds.x=function(o){var bounds=new Bounds(o);bounds.top=-Infinity;bounds.bottom=Infinity;return bounds;}})();;(function($){$.TextSelection=function(el,r){if(this.field!==$.TextSelection.prototype.field){return new $.TextSelection(el,r);}
spull(this,el);r&&spush(this.range(r));return this;};$.TextSelection.Error=function(description,obj){this._description=description;this._obj=obj;return this;};$.TextSelection.Error.prototype=new Error;$.TextSelection.get=function(el){if(!el){throw new $.TextSelection.Error('$.TextSelection.get(el): argument is required',el);}
try{if(el.selectionStart!==undefined){return{selectionStart:el.selectionStart,selectionEnd:el.selectionEnd};}else if(el.createTextRange){var START=true,END=false;var bound=function(at_start){var tr=document.selection.createRange();if(tr.compareEndPoints('StartToEnd',tr)){tr.collapse(at_start);}
return tr.getBookmark().charCodeAt(2)-2;};return{selectionStart:bound(START),selectionEnd:bound(END)};}}catch(e){}
throw new $.TextSelection.Error('$.TextSelection.get(el): no range operations available on el',el);};$.TextSelection.set=function(el,r){if(!(el&&r)){throw new $.TextSelection.Error('$.TextSelection.set(el, r): both arguments are required',el);}
try{if(el.createTextRange){var tr=el.createTextRange();tr.collapse(true);tr.moveStart('character',r.selectionStart);tr.moveEnd('character',r.selectionEnd);tr.select();}else if(el.setSelectionRange){el.setSelectionRange(r.selectionStart,r.selectionEnd);}else if(el.selectionStart!==undefined){el.selectionStart=r.selectionStart;el.selectionEnd=r.selectionEnd;}
return;}catch(e){}
throw new $.TextSelection.Error('$.TextSelection.set(el, r): no range operations available on el',el);};function spull(ts,el){try{ts._r=$.TextSelection.get(ts._el=(el||ts._el));}catch(e){ts._el=null;}
return ts;}
function spush(ts,el){try{$.TextSelection.set(el||ts._el,ts._r);}catch(e){}
return ts;}
$.TextSelection.prototype={field:function(el){return el?spull(this,el):this._el;},range:function(r,dont_select){if(r){this._r=r;return dont_select?this:spush(this);}else{return this._r;}},focus:function(){$(this._el||[]).filter(':enabled:visible').focus();},save:function(el){return spull(this,el);},restore:function(){return spush(this);}};})(jQuery);;(function($){$.fn.extend({autocomplete:function(urlOrData,options){var isUrl=typeof urlOrData=="string";options=$.extend({},$.Autocompleter.defaults,{url:isUrl?urlOrData:null,data:isUrl?null:urlOrData,delay:isUrl?$.Autocompleter.defaults.delay:10,max:options&&!options.scroll?10:150},options);options.highlight=options.highlight||function(value){return value;};options.formatMatch=options.formatMatch||options.formatItem;return this.each(function(){new $.Autocompleter(this,options);});},result:function(handler){return this.bind("result",handler);},search:function(handler){return this.trigger("search",[handler]);},flushCache:function(){return this.trigger("flushCache");},setOptions:function(options){return this.trigger("setOptions",[options]);},unautocomplete:function(){return this.trigger("unautocomplete");}});$.Autocompleter=function(input,options){var KEY={UP:38,DOWN:40,DEL:46,TAB:9,RETURN:13,ESC:27,COMMA:188,PAGEUP:33,PAGEDOWN:34,BACKSPACE:8};var $input=$(input).attr("autocomplete","off").addClass(options.inputClass);var timeout;var previousValue="";var cache=$.Autocompleter.Cache(options);var hasFocus=0;var lastKeyPressCode;var config={mouseDownOnSelect:false};var select=$.Autocompleter.Select(options,input,selectCurrent,config);var blockSubmit;$.browser.opera&&$(input.form).bind("submit.autocomplete",function(){if(blockSubmit){blockSubmit=false;return false;}});$input.bind(($.browser.opera?"keypress":"keydown")+".autocomplete",function(event){hasFocus=1;lastKeyPressCode=event.keyCode;switch(event.keyCode){case KEY.UP:event.preventDefault();if(select.visible()){select.prev();}else{onChange(0,true);}
break;case KEY.DOWN:event.preventDefault();if(select.visible()){select.next();}else{onChange(0,true);}
break;case KEY.PAGEUP:event.preventDefault();if(select.visible()){select.pageUp();}else{onChange(0,true);}
break;case KEY.PAGEDOWN:event.preventDefault();if(select.visible()){select.pageDown();}else{onChange(0,true);}
break;case options.multiple&&$.trim(options.multipleSeparator)==","&&KEY.COMMA:case KEY.TAB:case KEY.RETURN:if(selectCurrent()){event.preventDefault();blockSubmit=true;return false;}
break;case KEY.ESC:select.hide();break;default:clearTimeout(timeout);timeout=setTimeout(onChange,options.delay);break;}}).focus(function(){hasFocus++;}).blur(function(){hasFocus=0;if(!config.mouseDownOnSelect){hideResults();}}).click(function(){if(hasFocus++>1&&!select.visible()){onChange(0,true);}}).bind("search",function(){var fn=(arguments.length>1)?arguments[1]:null;function findValueCallback(q,data){var result;if(data&&data.length){for(var i=0;i<data.length;i++){if(data[i].result.toLowerCase()==q.toLowerCase()){result=data[i];break;}}}
if(typeof fn=="function")fn(result);else $input.trigger("result",result&&[result.data,result.value]);}
$.each(trimWords($input.val()),function(i,value){request(value,findValueCallback,findValueCallback);});}).bind("flushCache",function(){cache.flush();}).bind("setOptions",function(){$.extend(options,arguments[1]);if("data"in arguments[1])
cache.populate();}).bind("unautocomplete",function(){select.unbind();$input.unbind();$(input.form).unbind(".autocomplete");});function selectCurrent(){var selected=select.selected();if(!selected)
return false;var v=selected.result;previousValue=v;if(options.multiple){var words=trimWords($input.val());if(words.length>1){var seperator=options.multipleSeparator.length;var cursorAt=$(input).selection().start;var wordAt,progress=0;$.each(words,function(i,word){progress+=word.length;if(cursorAt<=progress){wordAt=i;return false;}
progress+=seperator;});words[wordAt]=v;v=words.join(options.multipleSeparator);}
v+=options.multipleSeparator;}
$input.val(v);hideResultsNow();$input.trigger("result",[selected.data,selected.value]);return true;}
function onChange(crap,skipPrevCheck){if(lastKeyPressCode==KEY.DEL){select.hide();return;}
var currentValue=$input.val();if(!skipPrevCheck&&currentValue==previousValue)
return;previousValue=currentValue;currentValue=lastWord(currentValue);if(currentValue.length>=options.minChars){$input.addClass(options.loadingClass);if(!options.matchCase)
currentValue=currentValue.toLowerCase();request(currentValue,receiveData,hideResultsNow);}else{stopLoading();select.hide();}};function trimWords(value){if(!value)
return[""];if(!options.multiple)
return[$.trim(value)];return $.map(value.split(options.multipleSeparator),function(word){return $.trim(value).length?$.trim(word):null;});}
function lastWord(value){if(!options.multiple)
return value;var words=trimWords(value);if(words.length==1)
return words[0];var cursorAt=$(input).selection().start;if(cursorAt==value.length){words=trimWords(value)}else{words=trimWords(value.replace(value.substring(cursorAt),""));}
return words[words.length-1];}
function autoFill(q,sValue){if(options.autoFill&&(lastWord($input.val()).toLowerCase()==q.toLowerCase())&&lastKeyPressCode!=KEY.BACKSPACE){$input.val($input.val()+sValue.substring(lastWord(previousValue).length));$(input).selection(previousValue.length,previousValue.length+sValue.length);}};function hideResults(){clearTimeout(timeout);timeout=setTimeout(hideResultsNow,200);};function hideResultsNow(){var wasVisible=select.visible();select.hide();clearTimeout(timeout);stopLoading();if(options.mustMatch){$input.search(function(result){if(!result){if(options.multiple){var words=trimWords($input.val()).slice(0,-1);$input.val(words.join(options.multipleSeparator)+(words.length?options.multipleSeparator:""));}
else{$input.val("");$input.trigger("result",null);}}});}};function receiveData(q,data){if(data&&data.length&&hasFocus){stopLoading();select.display(data,q);autoFill(q,data[0].value);select.show();}else{hideResultsNow();}};function request(term,success,failure){if(!options.matchCase)
term=term.toLowerCase();var data=cache.load(term);if(data&&data.length){success(term,data);}else if((typeof options.url=="string")&&(options.url.length>0)){var extraParams={timestamp:+new Date()};$.each(options.extraParams,function(key,param){extraParams[key]=typeof param=="function"?param():param;});$.ajax({mode:"abort",port:"autocomplete"+input.name,dataType:options.dataType,url:options.url,data:$.extend({q:lastWord(term),limit:options.max},extraParams),success:function(data){var parsed=options.parse&&options.parse(data)||parse(data);cache.add(term,parsed);success(term,parsed);}});}else{select.emptyList();failure(term);}};function parse(data){var parsed=[];var rows=data.split("\n");for(var i=0;i<rows.length;i++){var row=$.trim(rows[i]);if(row){row=row.split("|");parsed[parsed.length]={data:row,value:row[0],result:options.formatResult&&options.formatResult(row,row[0])||row[0]};}}
return parsed;};function stopLoading(){$input.removeClass(options.loadingClass);};};$.Autocompleter.defaults={inputClass:"ac_input",resultsClass:"ac_results",loadingClass:"ac_loading",minChars:1,delay:400,matchCase:false,matchSubset:true,matchContains:false,cacheLength:10,max:100,mustMatch:false,extraParams:{},selectFirst:true,formatItem:function(row){return row[0];},formatMatch:null,autoFill:false,width:0,multiple:false,multipleSeparator:", ",highlight:function(value,term){return value.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)("+term.replace(/([\^\$\(\)\[\]\{\}\*\.\+\?\|\\])/gi,"\\$1")+")(?![^<>]*>)(?![^&;]+;)","gi"),"<strong>$1</strong>");},scroll:true,scrollHeight:180};$.Autocompleter.Cache=function(options){var data={};var length=0;function matchSubset(s,sub){if(!options.matchCase)
s=s.toLowerCase();var i=s.indexOf(sub);if(options.matchContains=="word"){i=s.toLowerCase().search("\\b"+sub.toLowerCase());}
if(i==-1)return false;return i==0||options.matchContains;};function add(q,value){if(length>options.cacheLength){flush();}
if(!data[q]){length++;}
data[q]=value;}
function populate(){if(!options.data)return false;var stMatchSets={},nullData=0;if(!options.url)options.cacheLength=1;stMatchSets[""]=[];for(var i=0,ol=options.data.length;i<ol;i++){var rawValue=options.data[i];rawValue=(typeof rawValue=="string")?[rawValue]:rawValue;var value=options.formatMatch(rawValue,i+1,options.data.length);if(value===false)
continue;var firstChar=value.charAt(0).toLowerCase();if(!stMatchSets[firstChar])
stMatchSets[firstChar]=[];var row={value:value,data:rawValue,result:options.formatResult&&options.formatResult(rawValue)||value};stMatchSets[firstChar].push(row);if(nullData++<options.max){stMatchSets[""].push(row);}};$.each(stMatchSets,function(i,value){options.cacheLength++;add(i,value);});}
setTimeout(populate,25);function flush(){data={};length=0;}
return{flush:flush,add:add,populate:populate,load:function(q){if(!options.cacheLength||!length)
return null;if(!options.url&&options.matchContains){var csub=[];for(var k in data){if(k.length>0){var c=data[k];$.each(c,function(i,x){if(matchSubset(x.value,q)){csub.push(x);}});}}
return csub;}else
if(data[q]){return data[q];}else
if(options.matchSubset){for(var i=q.length-1;i>=options.minChars;i--){var c=data[q.substr(0,i)];if(c){var csub=[];$.each(c,function(i,x){if(matchSubset(x.value,q)){csub[csub.length]=x;}});return csub;}}}
return null;}};};$.Autocompleter.Select=function(options,input,select,config){var CLASSES={ACTIVE:"ac_over"};var listItems,active=-1,data,term="",needsInit=true,element,list;function init(){if(!needsInit)
return;element=$("<div/>")
.hide()
.addClass(options.resultsClass)
.css("position","absolute")
.appendTo(document.body);list=$("<ul/>").appendTo(element).mouseover(function(event){if(target(event).nodeName&&target(event).nodeName.toUpperCase()=='LI'){active=$("li",list).removeClass(CLASSES.ACTIVE).index(target(event));$(target(event)).addClass(CLASSES.ACTIVE);}}).click(function(event){$(target(event)).addClass(CLASSES.ACTIVE);select();input.focus();return false;}).mousedown(function(){config.mouseDownOnSelect=true;}).mouseup(function(){config.mouseDownOnSelect=false;});if(options.width>0)
element.css("width",options.width);needsInit=false;}
function target(event){var element=event.target;while(element&&element.tagName!="LI")
element=element.parentNode;if(!element)
return[];return element;}
function moveSelect(step){listItems.slice(active,active+1).removeClass(CLASSES.ACTIVE);movePosition(step);var activeItem=listItems.slice(active,active+1).addClass(CLASSES.ACTIVE);if(options.scroll){var offset=0;listItems.slice(0,active).each(function(){offset+=this.offsetHeight;});if((offset+activeItem[0].offsetHeight-list.scrollTop())>list[0].clientHeight){list.scrollTop(offset+activeItem[0].offsetHeight-list.innerHeight());}else if(offset<list.scrollTop()){list.scrollTop(offset);}}};function movePosition(step){active+=step;if(active<0){active=listItems.size()-1;}else if(active>=listItems.size()){active=0;}}
function limitNumberOfItems(available){return options.max&&options.max<available?options.max:available;}
function fillList(){list.empty();var max=limitNumberOfItems(data.length);for(var i=0;i<max;i++){if(!data[i])
continue;var formatted=options.formatItem(data[i].data,i+1,max,data[i].value,term);if(formatted===false)
continue;var li=$("<li/>").html(options.highlight(formatted,term)).addClass(i%2==0?"ac_even":"ac_odd").appendTo(list)[0];$.data(li,"ac_data",data[i]);}
listItems=list.find("li");if(options.selectFirst){listItems.slice(0,1).addClass(CLASSES.ACTIVE);active=0;}
if($.fn.bgiframe)
list.bgiframe();}
return{display:function(d,q){init();data=d;term=q;fillList();},next:function(){moveSelect(1);},prev:function(){moveSelect(-1);},pageUp:function(){if(active!=0&&active-8<0){moveSelect(-active);}else{moveSelect(-8);}},pageDown:function(){if(active!=listItems.size()-1&&active+8>listItems.size()){moveSelect(listItems.size()-1-active);}else{moveSelect(8);}},hide:function(){element&&element.hide();listItems&&listItems.removeClass(CLASSES.ACTIVE);active=-1;},visible:function(){return element&&element.is(":visible");},current:function(){return this.visible()&&(listItems.filter("."+CLASSES.ACTIVE)[0]||options.selectFirst&&listItems[0]);},show:function(){var offset=$(input).offset();element.css({width:typeof options.width=="string"||options.width>0?options.width:$(input).width(),top:offset.top+input.offsetHeight,left:offset.left}).show();if(options.scroll){list.scrollTop(0);list.css({maxHeight:options.scrollHeight,overflow:'auto'});if($.browser.msie&&typeof document.body.style.maxHeight==="undefined"){var listHeight=0;listItems.each(function(){listHeight+=this.offsetHeight;});var scrollbarsVisible=listHeight>options.scrollHeight;list.css('height',scrollbarsVisible?options.scrollHeight:listHeight);if(!scrollbarsVisible){listItems.width(list.width()-parseInt(listItems.css("padding-left"))-parseInt(listItems.css("padding-right")));}}}},selected:function(){var selected=listItems&&listItems.filter("."+CLASSES.ACTIVE).removeClass(CLASSES.ACTIVE);return selected&&selected.length&&$.data(selected[0],"ac_data");},emptyList:function(){list&&list.empty();},unbind:function(){element&&element.remove();}};};$.fn.selection=function(start,end){if(start!==undefined){return this.each(function(){if(this.createTextRange){var selRange=this.createTextRange();if(end===undefined||start==end){selRange.move("character",start);selRange.select();}else{selRange.collapse(true);selRange.moveStart("character",start);selRange.moveEnd("character",end);selRange.select();}}else if(this.setSelectionRange){this.setSelectionRange(start,end);}else if(this.selectionStart){this.selectionStart=start;this.selectionEnd=end;}});}
var field=this[0];if(field.createTextRange){var range=document.selection.createRange(),orig=field.value,teststring="<->",textLength=range.text.length;range.text=teststring;var caretAt=field.value.indexOf(teststring);field.value=orig;this.selection(caretAt,caretAt+textLength);return{start:caretAt,end:caretAt+textLength}}else if(field.selectionStart!==undefined){return{start:field.selectionStart,end:field.selectionEnd}}};})(jQuery);;jQuery.ui||(function($){var _remove=$.fn.remove,isFF2=$.browser.mozilla&&(parseFloat($.browser.version)<1.9);$.ui={version:"1.7.2",plugin:{add:function(module,option,set){var proto=$.ui[module].prototype;for(var i in set){proto.plugins[i]=proto.plugins[i]||[];proto.plugins[i].push([option,set[i]]);}},call:function(instance,name,args){var set=instance.plugins[name];if(!set||!instance.element[0].parentNode){return;}
for(var i=0;i<set.length;i++){if(instance.options[set[i][0]]){set[i][1].apply(instance.element,args);}}}},contains:function(a,b){return document.compareDocumentPosition?a.compareDocumentPosition(b)&16:a!==b&&a.contains(b);},hasScroll:function(el,a){if($(el).css('overflow')=='hidden'){return false;}
var scroll=(a&&a=='left')?'scrollLeft':'scrollTop',has=false;if(el[scroll]>0){return true;}
el[scroll]=1;has=(el[scroll]>0);el[scroll]=0;return has;},isOverAxis:function(x,reference,size){return(x>reference)&&(x<(reference+size));},isOver:function(y,x,top,left,height,width){return $.ui.isOverAxis(y,top,height)&&$.ui.isOverAxis(x,left,width);},keyCode:{BACKSPACE:8,CAPS_LOCK:20,COMMA:188,CONTROL:17,DELETE:46,DOWN:40,END:35,ENTER:13,ESCAPE:27,HOME:36,INSERT:45,LEFT:37,NUMPAD_ADD:107,NUMPAD_DECIMAL:110,NUMPAD_DIVIDE:111,NUMPAD_ENTER:108,NUMPAD_MULTIPLY:106,NUMPAD_SUBTRACT:109,PAGE_DOWN:34,PAGE_UP:33,PERIOD:190,RIGHT:39,SHIFT:16,SPACE:32,TAB:9,UP:38}};if(isFF2){var attr=$.attr,removeAttr=$.fn.removeAttr,ariaNS="http://www.w3.org/2005/07/aaa",ariaState=/^aria-/,ariaRole=/^wairole:/;$.attr=function(elem,name,value){var set=value!==undefined;return(name=='role'?(set?attr.call(this,elem,name,"wairole:"+value):(attr.apply(this,arguments)||"").replace(ariaRole,"")):(ariaState.test(name)?(set?elem.setAttributeNS(ariaNS,name.replace(ariaState,"aaa:"),value):attr.call(this,elem,name.replace(ariaState,"aaa:"))):attr.apply(this,arguments)));};$.fn.removeAttr=function(name){return(ariaState.test(name)?this.each(function(){this.removeAttributeNS(ariaNS,name.replace(ariaState,""));}):removeAttr.call(this,name));};}
$.fn.extend({remove:function(){$("*",this).add(this).each(function(){$(this).triggerHandler("remove");});return _remove.apply(this,arguments);},enableSelection:function(){return this
.attr('unselectable','off')
.css('MozUserSelect','')
.unbind('selectstart.ui');},disableSelection:function(){return this
.attr('unselectable','on')
.css('MozUserSelect','none')
.bind('selectstart.ui',function(){return false;});},scrollParent:function(){var scrollParent;if(($.browser.msie&&(/(static|relative)/).test(this.css('position')))||(/absolute/).test(this.css('position'))){scrollParent=this.parents().filter(function(){return(/(relative|absolute|fixed)/).test($.curCSS(this,'position',1))&&(/(auto|scroll)/).test($.curCSS(this,'overflow',1)+$.curCSS(this,'overflow-y',1)+$.curCSS(this,'overflow-x',1));}).eq(0);}else{scrollParent=this.parents().filter(function(){return(/(auto|scroll)/).test($.curCSS(this,'overflow',1)+$.curCSS(this,'overflow-y',1)+$.curCSS(this,'overflow-x',1));}).eq(0);}
return(/fixed/).test(this.css('position'))||!scrollParent.length?$(document):scrollParent;}});$.extend($.expr[':'],{data:function(elem,i,match){return!!$.data(elem,match[3]);},focusable:function(element){var nodeName=element.nodeName.toLowerCase(),tabIndex=$.attr(element,'tabindex');return(/input|select|textarea|button|object/.test(nodeName)?!element.disabled:'a'==nodeName||'area'==nodeName?element.href||!isNaN(tabIndex):!isNaN(tabIndex))
&&!$(element)['area'==nodeName?'parents':'closest'](':hidden').length;},tabbable:function(element){var tabIndex=$.attr(element,'tabindex');return(isNaN(tabIndex)||tabIndex>=0)&&$(element).is(':focusable');}});function getter(namespace,plugin,method,args){function getMethods(type){var methods=$[namespace][plugin][type]||[];return(typeof methods=='string'?methods.split(/,?\s+/):methods);}
var methods=getMethods('getter');if(args.length==1&&typeof args[0]=='string'){methods=methods.concat(getMethods('getterSetter'));}
return($.inArray(method,methods)!=-1);}
$.widget=function(name,prototype){var namespace=name.split(".")[0];name=name.split(".")[1];$.fn[name]=function(options){var isMethodCall=(typeof options=='string'),args=Array.prototype.slice.call(arguments,1);if(isMethodCall&&options.substring(0,1)=='_'){return this;}
if(isMethodCall&&getter(namespace,name,options,args)){var instance=$.data(this[0],name);return(instance?instance[options].apply(instance,args):undefined);}
return this.each(function(){var instance=$.data(this,name);(!instance&&!isMethodCall&&$.data(this,name,new $[namespace][name](this,options))._init());(instance&&isMethodCall&&$.isFunction(instance[options])&&instance[options].apply(instance,args));});};$[namespace]=$[namespace]||{};$[namespace][name]=function(element,options){var self=this;this.namespace=namespace;this.widgetName=name;this.widgetEventPrefix=$[namespace][name].eventPrefix||name;this.widgetBaseClass=namespace+'-'+name;this.options=$.extend({},$.widget.defaults,$[namespace][name].defaults,$.metadata&&$.metadata.get(element)[name],options);this.element=$(element)
.bind('setData.'+name,function(event,key,value){if(event.target==element){return self._setData(key,value);}})
.bind('getData.'+name,function(event,key){if(event.target==element){return self._getData(key);}})
.bind('remove',function(){return self.destroy();});};$[namespace][name].prototype=$.extend({},$.widget.prototype,prototype);$[namespace][name].getterSetter='option';};$.widget.prototype={_init:function(){},destroy:function(){this.element.removeData(this.widgetName)
.removeClass(this.widgetBaseClass+'-disabled'+' '+this.namespace+'-state-disabled')
.removeAttr('aria-disabled');},option:function(key,value){var options=key,self=this;if(typeof key=="string"){if(value===undefined){return this._getData(key);}
options={};options[key]=value;}
$.each(options,function(key,value){self._setData(key,value);});},_getData:function(key){return this.options[key];},_setData:function(key,value){this.options[key]=value;if(key=='disabled'){this.element
[value?'addClass':'removeClass'](this.widgetBaseClass+'-disabled'+' '+
this.namespace+'-state-disabled')
.attr("aria-disabled",value);}},enable:function(){this._setData('disabled',false);},disable:function(){this._setData('disabled',true);},_trigger:function(type,event,data){var callback=this.options[type],eventName=(type==this.widgetEventPrefix?type:this.widgetEventPrefix+type);event=$.Event(event);event.type=eventName;if(event.originalEvent){for(var i=$.event.props.length,prop;i;){prop=$.event.props[--i];event[prop]=event.originalEvent[prop];}}
this.element.trigger(event,data);return!($.isFunction(callback)&&callback.call(this.element[0],event,data)===false||event.isDefaultPrevented());}};$.widget.defaults={disabled:false};$.ui.mouse={_mouseInit:function(){var self=this;this.element
.bind('mousedown.'+this.widgetName,function(event){return self._mouseDown(event);})
.bind('click.'+this.widgetName,function(event){if(self._preventClickEvent){self._preventClickEvent=false;event.stopImmediatePropagation();return false;}});if($.browser.msie){this._mouseUnselectable=this.element.attr('unselectable');this.element.attr('unselectable','on');}
this.started=false;},_mouseDestroy:function(){this.element.unbind('.'+this.widgetName);($.browser.msie&&this.element.attr('unselectable',this._mouseUnselectable));},_mouseDown:function(event){event.originalEvent=event.originalEvent||{};if(event.originalEvent.mouseHandled){return;}
(this._mouseStarted&&this._mouseUp(event));this._mouseDownEvent=event;var self=this,btnIsLeft=(event.which==1),elIsCancel=(typeof this.options.cancel=="string"?$(event.target).parents().add(event.target).filter(this.options.cancel).length:false);if(!btnIsLeft||elIsCancel||!this._mouseCapture(event)){return true;}
this.mouseDelayMet=!this.options.delay;if(!this.mouseDelayMet){this._mouseDelayTimer=setTimeout(function(){self.mouseDelayMet=true;},this.options.delay);}
if(this._mouseDistanceMet(event)&&this._mouseDelayMet(event)){this._mouseStarted=(this._mouseStart(event)!==false);if(!this._mouseStarted){event.preventDefault();return true;}}
this._mouseMoveDelegate=function(event){return self._mouseMove(event);};this._mouseUpDelegate=function(event){return self._mouseUp(event);};$(document)
.bind('mousemove.'+this.widgetName,this._mouseMoveDelegate)
.bind('mouseup.'+this.widgetName,this._mouseUpDelegate);($.browser.safari||event.preventDefault());event.originalEvent.mouseHandled=true;return true;},_mouseMove:function(event){if($.browser.msie&&!event.button){return this._mouseUp(event);}
if(this._mouseStarted){this._mouseDrag(event);return event.preventDefault();}
if(this._mouseDistanceMet(event)&&this._mouseDelayMet(event)){this._mouseStarted=(this._mouseStart(this._mouseDownEvent,event)!==false);(this._mouseStarted?this._mouseDrag(event):this._mouseUp(event));}
return!this._mouseStarted;},_mouseUp:function(event){$(document)
.unbind('mousemove.'+this.widgetName,this._mouseMoveDelegate)
.unbind('mouseup.'+this.widgetName,this._mouseUpDelegate);if(this._mouseStarted){this._mouseStarted=false;this._preventClickEvent=(event.target==this._mouseDownEvent.target);this._mouseStop(event);}
return false;},_mouseDistanceMet:function(event){return(Math.max(Math.abs(this._mouseDownEvent.pageX-event.pageX),Math.abs(this._mouseDownEvent.pageY-event.pageY))>=this.options.distance);},_mouseDelayMet:function(event){return this.mouseDelayMet;},_mouseStart:function(event){},_mouseDrag:function(event){},_mouseStop:function(event){},_mouseCapture:function(event){return true;}};$.ui.mouse.defaults={cancel:null,distance:1,delay:0};})(jQuery);(function($){$.widget("ui.draggable",$.extend({},$.ui.mouse,{_init:function(){if(this.options.helper=='original'&&!(/^(?:r|a|f)/).test(this.element.css("position")))
this.element[0].style.position='relative';(this.options.addClasses&&this.element.addClass("ui-draggable"));(this.options.disabled&&this.element.addClass("ui-draggable-disabled"));this._mouseInit();},destroy:function(){if(!this.element.data('draggable'))return;this.element
.removeData("draggable")
.unbind(".draggable")
.removeClass("ui-draggable"
+" ui-draggable-dragging"
+" ui-draggable-disabled");this._mouseDestroy();},_mouseCapture:function(event){var o=this.options;if(this.helper||o.disabled||$(event.target).is('.ui-resizable-handle'))
return false;this.handle=this._getHandle(event);if(!this.handle)
return false;return true;},_mouseStart:function(event){var o=this.options;this.helper=this._createHelper(event);this._cacheHelperProportions();if($.ui.ddmanager)
$.ui.ddmanager.current=this;this._cacheMargins();this.cssPosition=this.helper.css("position");this.scrollParent=this.helper.scrollParent();this.offset=this.element.offset();this.offset={top:this.offset.top-this.margins.top,left:this.offset.left-this.margins.left};$.extend(this.offset,{click:{left:event.pageX-this.offset.left,top:event.pageY-this.offset.top},parent:this._getParentOffset(),relative:this._getRelativeOffset()});this.originalPosition=this._generatePosition(event);this.originalPageX=event.pageX;this.originalPageY=event.pageY;if(o.cursorAt)
this._adjustOffsetFromHelper(o.cursorAt);if(o.containment)
this._setContainment();this._trigger("start",event);this._cacheHelperProportions();if($.ui.ddmanager&&!o.dropBehaviour)
$.ui.ddmanager.prepareOffsets(this,event);this.helper.addClass("ui-draggable-dragging");this._mouseDrag(event,true);return true;},_mouseDrag:function(event,noPropagation){this.position=this._generatePosition(event);this.positionAbs=this._convertPositionTo("absolute");if(!noPropagation){var ui=this._uiHash();this._trigger('drag',event,ui);this.position=ui.position;}
if(!this.options.axis||this.options.axis!="y")this.helper[0].style.left=this.position.left+'px';if(!this.options.axis||this.options.axis!="x")this.helper[0].style.top=this.position.top+'px';if($.ui.ddmanager)$.ui.ddmanager.drag(this,event);return false;},_mouseStop:function(event){var dropped=false;if($.ui.ddmanager&&!this.options.dropBehaviour)
dropped=$.ui.ddmanager.drop(this,event);if(this.dropped){dropped=this.dropped;this.dropped=false;}
if((this.options.revert=="invalid"&&!dropped)||(this.options.revert=="valid"&&dropped)||this.options.revert===true||($.isFunction(this.options.revert)&&this.options.revert.call(this.element,dropped))){var self=this;$(this.helper).animate(this.originalPosition,parseInt(this.options.revertDuration,10),function(){self._trigger("stop",event);self._clear();});}else{this._trigger("stop",event);this._clear();}
return false;},_getHandle:function(event){var handle=!this.options.handle||!$(this.options.handle,this.element).length?true:false;$(this.options.handle,this.element)
.find("*")
.andSelf()
.each(function(){if(this==event.target)handle=true;});return handle;},_createHelper:function(event){var o=this.options;var helper=$.isFunction(o.helper)?$(o.helper.apply(this.element[0],[event])):(o.helper=='clone'?this.element.clone():this.element);if(!helper.parents('body').length)
helper.appendTo((o.appendTo=='parent'?this.element[0].parentNode:o.appendTo));if(helper[0]!=this.element[0]&&!(/(fixed|absolute)/).test(helper.css("position")))
helper.css("position","absolute");return helper;},_adjustOffsetFromHelper:function(obj){if(obj.left!=undefined)this.offset.click.left=obj.left+this.margins.left;if(obj.right!=undefined)this.offset.click.left=this.helperProportions.width-obj.right+this.margins.left;if(obj.top!=undefined)this.offset.click.top=obj.top+this.margins.top;if(obj.bottom!=undefined)this.offset.click.top=this.helperProportions.height-obj.bottom+this.margins.top;},_getParentOffset:function(){this.offsetParent=this.helper.offsetParent();var po=this.offsetParent.offset();if(this.cssPosition=='absolute'&&this.scrollParent[0]!=document&&$.ui.contains(this.scrollParent[0],this.offsetParent[0])){po.left+=this.scrollParent.scrollLeft();po.top+=this.scrollParent.scrollTop();}
if((this.offsetParent[0]==document.body)||(this.offsetParent[0].tagName&&this.offsetParent[0].tagName.toLowerCase()=='html'&&$.browser.msie))
po={top:0,left:0};return{top:po.top+(parseInt(this.offsetParent.css("borderTopWidth"),10)||0),left:po.left+(parseInt(this.offsetParent.css("borderLeftWidth"),10)||0)};},_getRelativeOffset:function(){if(this.cssPosition=="relative"){var p=this.element.position();return{top:p.top-(parseInt(this.helper.css("top"),10)||0)+this.scrollParent.scrollTop(),left:p.left-(parseInt(this.helper.css("left"),10)||0)+this.scrollParent.scrollLeft()};}else{return{top:0,left:0};}},_cacheMargins:function(){this.margins={left:(parseInt(this.element.css("marginLeft"),10)||0),top:(parseInt(this.element.css("marginTop"),10)||0)};},_cacheHelperProportions:function(){this.helperProportions={width:this.helper.outerWidth(),height:this.helper.outerHeight()};},_setContainment:function(){var o=this.options;if(o.containment=='parent')o.containment=this.helper[0].parentNode;if(o.containment=='document'||o.containment=='window')this.containment=[0-this.offset.relative.left-this.offset.parent.left,0-this.offset.relative.top-this.offset.parent.top,$(o.containment=='document'?document:window).width()-this.helperProportions.width-this.margins.left,($(o.containment=='document'?document:window).height()||document.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top];if(!(/^(document|window|parent)$/).test(o.containment)&&o.containment.constructor!=Array){var ce=$(o.containment)[0];if(!ce)return;var co=$(o.containment).offset();var over=($(ce).css("overflow")!='hidden');this.containment=[co.left+(parseInt($(ce).css("borderLeftWidth"),10)||0)+(parseInt($(ce).css("paddingLeft"),10)||0)-this.margins.left,co.top+(parseInt($(ce).css("borderTopWidth"),10)||0)+(parseInt($(ce).css("paddingTop"),10)||0)-this.margins.top,co.left+(over?Math.max(ce.scrollWidth,ce.offsetWidth):ce.offsetWidth)-(parseInt($(ce).css("borderLeftWidth"),10)||0)-(parseInt($(ce).css("paddingRight"),10)||0)-this.helperProportions.width-this.margins.left,co.top+(over?Math.max(ce.scrollHeight,ce.offsetHeight):ce.offsetHeight)-(parseInt($(ce).css("borderTopWidth"),10)||0)-(parseInt($(ce).css("paddingBottom"),10)||0)-this.helperProportions.height-this.margins.top];}else if(o.containment.constructor==Array){this.containment=o.containment;}},_convertPositionTo:function(d,pos){if(!pos)pos=this.position;var mod=d=="absolute"?1:-1;var o=this.options,scroll=this.cssPosition=='absolute'&&!(this.scrollParent[0]!=document&&$.ui.contains(this.scrollParent[0],this.offsetParent[0]))?this.offsetParent:this.scrollParent,scrollIsRootNode=(/(html|body)/i).test(scroll[0].tagName);return{top:(pos.top
+this.offset.relative.top*mod
+this.offset.parent.top*mod
-($.browser.safari&&this.cssPosition=='fixed'?0:(this.cssPosition=='fixed'?-this.scrollParent.scrollTop():(scrollIsRootNode?0:scroll.scrollTop()))*mod)),left:(pos.left
+this.offset.relative.left*mod
+this.offset.parent.left*mod
-($.browser.safari&&this.cssPosition=='fixed'?0:(this.cssPosition=='fixed'?-this.scrollParent.scrollLeft():scrollIsRootNode?0:scroll.scrollLeft())*mod))};},_generatePosition:function(event){var o=this.options,scroll=this.cssPosition=='absolute'&&!(this.scrollParent[0]!=document&&$.ui.contains(this.scrollParent[0],this.offsetParent[0]))?this.offsetParent:this.scrollParent,scrollIsRootNode=(/(html|body)/i).test(scroll[0].tagName);if(this.cssPosition=='relative'&&!(this.scrollParent[0]!=document&&this.scrollParent[0]!=this.offsetParent[0])){this.offset.relative=this._getRelativeOffset();}
var pageX=event.pageX;var pageY=event.pageY;if(this.originalPosition){if(this.containment){if(event.pageX-this.offset.click.left<this.containment[0])pageX=this.containment[0]+this.offset.click.left;if(event.pageY-this.offset.click.top<this.containment[1])pageY=this.containment[1]+this.offset.click.top;if(event.pageX-this.offset.click.left>this.containment[2])pageX=this.containment[2]+this.offset.click.left;if(event.pageY-this.offset.click.top>this.containment[3])pageY=this.containment[3]+this.offset.click.top;}
if(o.grid){var top=this.originalPageY+Math.round((pageY-this.originalPageY)/o.grid[1])*o.grid[1];pageY=this.containment?(!(top-this.offset.click.top<this.containment[1]||top-this.offset.click.top>this.containment[3])?top:(!(top-this.offset.click.top<this.containment[1])?top-o.grid[1]:top+o.grid[1])):top;var left=this.originalPageX+Math.round((pageX-this.originalPageX)/o.grid[0])*o.grid[0];pageX=this.containment?(!(left-this.offset.click.left<this.containment[0]||left-this.offset.click.left>this.containment[2])?left:(!(left-this.offset.click.left<this.containment[0])?left-o.grid[0]:left+o.grid[0])):left;}}
return{top:(pageY
-this.offset.click.top
-this.offset.relative.top
-this.offset.parent.top
+($.browser.safari&&this.cssPosition=='fixed'?0:(this.cssPosition=='fixed'?-this.scrollParent.scrollTop():(scrollIsRootNode?0:scroll.scrollTop())))),left:(pageX
-this.offset.click.left
-this.offset.relative.left
-this.offset.parent.left
+($.browser.safari&&this.cssPosition=='fixed'?0:(this.cssPosition=='fixed'?-this.scrollParent.scrollLeft():scrollIsRootNode?0:scroll.scrollLeft())))};},_clear:function(){this.helper.removeClass("ui-draggable-dragging");if(this.helper[0]!=this.element[0]&&!this.cancelHelperRemoval)this.helper.remove();this.helper=null;this.cancelHelperRemoval=false;},_trigger:function(type,event,ui){ui=ui||this._uiHash();$.ui.plugin.call(this,type,[event,ui]);if(type=="drag")this.positionAbs=this._convertPositionTo("absolute");return $.widget.prototype._trigger.call(this,type,event,ui);},plugins:{},_uiHash:function(event){return{helper:this.helper,position:this.position,absolutePosition:this.positionAbs,offset:this.positionAbs};}}));$.extend($.ui.draggable,{version:"1.7.2",eventPrefix:"drag",defaults:{addClasses:true,appendTo:"parent",axis:false,cancel:":input,option",connectToSortable:false,containment:false,cursor:"auto",cursorAt:false,delay:0,distance:1,grid:false,handle:false,helper:"original",iframeFix:false,opacity:false,refreshPositions:false,revert:false,revertDuration:500,scope:"default",scroll:true,scrollSensitivity:20,scrollSpeed:20,snap:false,snapMode:"both",snapTolerance:20,stack:false,zIndex:false}});$.ui.plugin.add("draggable","connectToSortable",{start:function(event,ui){var inst=$(this).data("draggable"),o=inst.options,uiSortable=$.extend({},ui,{item:inst.element});inst.sortables=[];$(o.connectToSortable).each(function(){var sortable=$.data(this,'sortable');if(sortable&&!sortable.options.disabled){inst.sortables.push({instance:sortable,shouldRevert:sortable.options.revert});sortable._refreshItems();sortable._trigger("activate",event,uiSortable);}});},stop:function(event,ui){var inst=$(this).data("draggable"),uiSortable=$.extend({},ui,{item:inst.element});$.each(inst.sortables,function(){if(this.instance.isOver){this.instance.isOver=0;inst.cancelHelperRemoval=true;this.instance.cancelHelperRemoval=false;if(this.shouldRevert)this.instance.options.revert=true;this.instance._mouseStop(event);this.instance.options.helper=this.instance.options._helper;if(inst.options.helper=='original')
this.instance.currentItem.css({top:'auto',left:'auto'});}else{this.instance.cancelHelperRemoval=false;this.instance._trigger("deactivate",event,uiSortable);}});},drag:function(event,ui){var inst=$(this).data("draggable"),self=this;var checkPos=function(o){var dyClick=this.offset.click.top,dxClick=this.offset.click.left;var helperTop=this.positionAbs.top,helperLeft=this.positionAbs.left;var itemHeight=o.height,itemWidth=o.width;var itemTop=o.top,itemLeft=o.left;return $.ui.isOver(helperTop+dyClick,helperLeft+dxClick,itemTop,itemLeft,itemHeight,itemWidth);};$.each(inst.sortables,function(i){this.instance.positionAbs=inst.positionAbs;this.instance.helperProportions=inst.helperProportions;this.instance.offset.click=inst.offset.click;if(this.instance._intersectsWith(this.instance.containerCache)){if(!this.instance.isOver){this.instance.isOver=1;this.instance.currentItem=$(self).clone().appendTo(this.instance.element).data("sortable-item",true);this.instance.options._helper=this.instance.options.helper;this.instance.options.helper=function(){return ui.helper[0];};event.target=this.instance.currentItem[0];this.instance._mouseCapture(event,true);this.instance._mouseStart(event,true,true);this.instance.offset.click.top=inst.offset.click.top;this.instance.offset.click.left=inst.offset.click.left;this.instance.offset.parent.left-=inst.offset.parent.left-this.instance.offset.parent.left;this.instance.offset.parent.top-=inst.offset.parent.top-this.instance.offset.parent.top;inst._trigger("toSortable",event);inst.dropped=this.instance.element;inst.currentItem=inst.element;this.instance.fromOutside=inst;}
if(this.instance.currentItem)this.instance._mouseDrag(event);}else{if(this.instance.isOver){this.instance.isOver=0;this.instance.cancelHelperRemoval=true;this.instance.options.revert=false;this.instance._trigger('out',event,this.instance._uiHash(this.instance));this.instance._mouseStop(event,true);this.instance.options.helper=this.instance.options._helper;this.instance.currentItem.remove();if(this.instance.placeholder)this.instance.placeholder.remove();inst._trigger("fromSortable",event);inst.dropped=false;}};});}});$.ui.plugin.add("draggable","cursor",{start:function(event,ui){var t=$('body'),o=$(this).data('draggable').options;if(t.css("cursor"))o._cursor=t.css("cursor");t.css("cursor",o.cursor);},stop:function(event,ui){var o=$(this).data('draggable').options;if(o._cursor)$('body').css("cursor",o._cursor);}});$.ui.plugin.add("draggable","iframeFix",{start:function(event,ui){var o=$(this).data('draggable').options;$(o.iframeFix===true?"iframe":o.iframeFix).each(function(){$('<div class="ui-draggable-iframeFix" style="background: #fff;"></div>')
.css({width:this.offsetWidth+"px",height:this.offsetHeight+"px",position:"absolute",opacity:"0.001",zIndex:1000})
.css($(this).offset())
.appendTo("body");});},stop:function(event,ui){$("div.ui-draggable-iframeFix").each(function(){this.parentNode.removeChild(this);});}});$.ui.plugin.add("draggable","opacity",{start:function(event,ui){var t=$(ui.helper),o=$(this).data('draggable').options;if(t.css("opacity"))o._opacity=t.css("opacity");t.css('opacity',o.opacity);},stop:function(event,ui){var o=$(this).data('draggable').options;if(o._opacity)$(ui.helper).css('opacity',o._opacity);}});$.ui.plugin.add("draggable","scroll",{start:function(event,ui){var i=$(this).data("draggable");if(i.scrollParent[0]!=document&&i.scrollParent[0].tagName!='HTML')i.overflowOffset=i.scrollParent.offset();},drag:function(event,ui){var i=$(this).data("draggable"),o=i.options,scrolled=false;if(i.scrollParent[0]!=document&&i.scrollParent[0].tagName!='HTML'){if(!o.axis||o.axis!='x'){if((i.overflowOffset.top+i.scrollParent[0].offsetHeight)-event.pageY<o.scrollSensitivity)
i.scrollParent[0].scrollTop=scrolled=i.scrollParent[0].scrollTop+o.scrollSpeed;else if(event.pageY-i.overflowOffset.top<o.scrollSensitivity)
i.scrollParent[0].scrollTop=scrolled=i.scrollParent[0].scrollTop-o.scrollSpeed;}
if(!o.axis||o.axis!='y'){if((i.overflowOffset.left+i.scrollParent[0].offsetWidth)-event.pageX<o.scrollSensitivity)
i.scrollParent[0].scrollLeft=scrolled=i.scrollParent[0].scrollLeft+o.scrollSpeed;else if(event.pageX-i.overflowOffset.left<o.scrollSensitivity)
i.scrollParent[0].scrollLeft=scrolled=i.scrollParent[0].scrollLeft-o.scrollSpeed;}}else{if(!o.axis||o.axis!='x'){if(event.pageY-$(document).scrollTop()<o.scrollSensitivity)
scrolled=$(document).scrollTop($(document).scrollTop()-o.scrollSpeed);else if($(window).height()-(event.pageY-$(document).scrollTop())<o.scrollSensitivity)
scrolled=$(document).scrollTop($(document).scrollTop()+o.scrollSpeed);}
if(!o.axis||o.axis!='y'){if(event.pageX-$(document).scrollLeft()<o.scrollSensitivity)
scrolled=$(document).scrollLeft($(document).scrollLeft()-o.scrollSpeed);else if($(window).width()-(event.pageX-$(document).scrollLeft())<o.scrollSensitivity)
scrolled=$(document).scrollLeft($(document).scrollLeft()+o.scrollSpeed);}}
if(scrolled!==false&&$.ui.ddmanager&&!o.dropBehaviour)
$.ui.ddmanager.prepareOffsets(i,event);}});$.ui.plugin.add("draggable","snap",{start:function(event,ui){var i=$(this).data("draggable"),o=i.options;i.snapElements=[];$(o.snap.constructor!=String?(o.snap.items||':data(draggable)'):o.snap).each(function(){var $t=$(this);var $o=$t.offset();if(this!=i.element[0])i.snapElements.push({item:this,width:$t.outerWidth(),height:$t.outerHeight(),top:$o.top,left:$o.left});});},drag:function(event,ui){var inst=$(this).data("draggable"),o=inst.options;var d=o.snapTolerance;var x1=ui.offset.left,x2=x1+inst.helperProportions.width,y1=ui.offset.top,y2=y1+inst.helperProportions.height;for(var i=inst.snapElements.length-1;i>=0;i--){var l=inst.snapElements[i].left,r=l+inst.snapElements[i].width,t=inst.snapElements[i].top,b=t+inst.snapElements[i].height;if(!((l-d<x1&&x1<r+d&&t-d<y1&&y1<b+d)||(l-d<x1&&x1<r+d&&t-d<y2&&y2<b+d)||(l-d<x2&&x2<r+d&&t-d<y1&&y1<b+d)||(l-d<x2&&x2<r+d&&t-d<y2&&y2<b+d))){if(inst.snapElements[i].snapping)(inst.options.snap.release&&inst.options.snap.release.call(inst.element,event,$.extend(inst._uiHash(),{snapItem:inst.snapElements[i].item})));inst.snapElements[i].snapping=false;continue;}
if(o.snapMode!='inner'){var ts=Math.abs(t-y2)<=d;var bs=Math.abs(b-y1)<=d;var ls=Math.abs(l-x2)<=d;var rs=Math.abs(r-x1)<=d;if(ts)ui.position.top=inst._convertPositionTo("relative",{top:t-inst.helperProportions.height,left:0}).top-inst.margins.top;if(bs)ui.position.top=inst._convertPositionTo("relative",{top:b,left:0}).top-inst.margins.top;if(ls)ui.position.left=inst._convertPositionTo("relative",{top:0,left:l-inst.helperProportions.width}).left-inst.margins.left;if(rs)ui.position.left=inst._convertPositionTo("relative",{top:0,left:r}).left-inst.margins.left;}
var first=(ts||bs||ls||rs);if(o.snapMode!='outer'){var ts=Math.abs(t-y1)<=d;var bs=Math.abs(b-y2)<=d;var ls=Math.abs(l-x1)<=d;var rs=Math.abs(r-x2)<=d;if(ts)ui.position.top=inst._convertPositionTo("relative",{top:t,left:0}).top-inst.margins.top;if(bs)ui.position.top=inst._convertPositionTo("relative",{top:b-inst.helperProportions.height,left:0}).top-inst.margins.top;if(ls)ui.position.left=inst._convertPositionTo("relative",{top:0,left:l}).left-inst.margins.left;if(rs)ui.position.left=inst._convertPositionTo("relative",{top:0,left:r-inst.helperProportions.width}).left-inst.margins.left;}
if(!inst.snapElements[i].snapping&&(ts||bs||ls||rs||first))
(inst.options.snap.snap&&inst.options.snap.snap.call(inst.element,event,$.extend(inst._uiHash(),{snapItem:inst.snapElements[i].item})));inst.snapElements[i].snapping=(ts||bs||ls||rs||first);};}});$.ui.plugin.add("draggable","stack",{start:function(event,ui){var o=$(this).data("draggable").options;var group=$.makeArray($(o.stack.group)).sort(function(a,b){return(parseInt($(a).css("zIndex"),10)||o.stack.min)-(parseInt($(b).css("zIndex"),10)||o.stack.min);});$(group).each(function(i){this.style.zIndex=o.stack.min+i;});this[0].style.zIndex=o.stack.min+group.length;}});$.ui.plugin.add("draggable","zIndex",{start:function(event,ui){var t=$(ui.helper),o=$(this).data("draggable").options;if(t.css("zIndex"))o._zIndex=t.css("zIndex");t.css('zIndex',o.zIndex);},stop:function(event,ui){var o=$(this).data("draggable").options;if(o._zIndex)$(ui.helper).css('zIndex',o._zIndex);}});})(jQuery);(function($){$.widget("ui.sortable",$.extend({},$.ui.mouse,{_init:function(){var o=this.options;this.containerCache={};this.element.addClass("ui-sortable");this.refresh();this.floating=this.items.length?(/left|right/).test(this.items[0].item.css('float')):false;this.offset=this.element.offset();this._mouseInit();},destroy:function(){this.element
.removeClass("ui-sortable ui-sortable-disabled")
.removeData("sortable")
.unbind(".sortable");this._mouseDestroy();for(var i=this.items.length-1;i>=0;i--)
this.items[i].item.removeData("sortable-item");},_mouseCapture:function(event,overrideHandle){if(this.reverting){return false;}
if(this.options.disabled||this.options.type=='static')return false;this._refreshItems(event);var currentItem=null,self=this,nodes=$(event.target).parents().each(function(){if($.data(this,'sortable-item')==self){currentItem=$(this);return false;}});if($.data(event.target,'sortable-item')==self)currentItem=$(event.target);if(!currentItem)return false;if(this.options.handle&&!overrideHandle){var validHandle=false;$(this.options.handle,currentItem).find("*").andSelf().each(function(){if(this==event.target)validHandle=true;});if(!validHandle)return false;}
this.currentItem=currentItem;this._removeCurrentsFromItems();return true;},_mouseStart:function(event,overrideHandle,noActivation){var o=this.options,self=this;this.currentContainer=this;this.refreshPositions();this.helper=this._createHelper(event);this._cacheHelperProportions();this._cacheMargins();this.scrollParent=this.helper.scrollParent();this.offset=this.currentItem.offset();this.offset={top:this.offset.top-this.margins.top,left:this.offset.left-this.margins.left};this.helper.css("position","absolute");this.cssPosition=this.helper.css("position");$.extend(this.offset,{click:{left:event.pageX-this.offset.left,top:event.pageY-this.offset.top},parent:this._getParentOffset(),relative:this._getRelativeOffset()});this.originalPosition=this._generatePosition(event);this.originalPageX=event.pageX;this.originalPageY=event.pageY;if(o.cursorAt)
this._adjustOffsetFromHelper(o.cursorAt);this.domPosition={prev:this.currentItem.prev()[0],parent:this.currentItem.parent()[0]};if(this.helper[0]!=this.currentItem[0]){this.currentItem.hide();}
this._createPlaceholder();if(o.containment)
this._setContainment();if(o.cursor){if($('body').css("cursor"))this._storedCursor=$('body').css("cursor");$('body').css("cursor",o.cursor);}
if(o.opacity){if(this.helper.css("opacity"))this._storedOpacity=this.helper.css("opacity");this.helper.css("opacity",o.opacity);}
if(o.zIndex){if(this.helper.css("zIndex"))this._storedZIndex=this.helper.css("zIndex");this.helper.css("zIndex",o.zIndex);}
if(this.scrollParent[0]!=document&&this.scrollParent[0].tagName!='HTML')
this.overflowOffset=this.scrollParent.offset();this._trigger("start",event,this._uiHash());if(!this._preserveHelperProportions)
this._cacheHelperProportions();if(!noActivation){for(var i=this.containers.length-1;i>=0;i--){this.containers[i]._trigger("activate",event,self._uiHash(this));}}
if($.ui.ddmanager)
$.ui.ddmanager.current=this;if($.ui.ddmanager&&!o.dropBehaviour)
$.ui.ddmanager.prepareOffsets(this,event);this.dragging=true;this.helper.addClass("ui-sortable-helper");this._mouseDrag(event);return true;},_mouseDrag:function(event){this.position=this._generatePosition(event);this.positionAbs=this._convertPositionTo("absolute");if(!this.lastPositionAbs){this.lastPositionAbs=this.positionAbs;}
if(this.options.scroll){var o=this.options,scrolled=false;if(this.scrollParent[0]!=document&&this.scrollParent[0].tagName!='HTML'){if((this.overflowOffset.top+this.scrollParent[0].offsetHeight)-event.pageY<o.scrollSensitivity)
this.scrollParent[0].scrollTop=scrolled=this.scrollParent[0].scrollTop+o.scrollSpeed;else if(event.pageY-this.overflowOffset.top<o.scrollSensitivity)
this.scrollParent[0].scrollTop=scrolled=this.scrollParent[0].scrollTop-o.scrollSpeed;if((this.overflowOffset.left+this.scrollParent[0].offsetWidth)-event.pageX<o.scrollSensitivity)
this.scrollParent[0].scrollLeft=scrolled=this.scrollParent[0].scrollLeft+o.scrollSpeed;else if(event.pageX-this.overflowOffset.left<o.scrollSensitivity)
this.scrollParent[0].scrollLeft=scrolled=this.scrollParent[0].scrollLeft-o.scrollSpeed;}else{if(event.pageY-$(document).scrollTop()<o.scrollSensitivity)
scrolled=$(document).scrollTop($(document).scrollTop()-o.scrollSpeed);else if($(window).height()-(event.pageY-$(document).scrollTop())<o.scrollSensitivity)
scrolled=$(document).scrollTop($(document).scrollTop()+o.scrollSpeed);if(event.pageX-$(document).scrollLeft()<o.scrollSensitivity)
scrolled=$(document).scrollLeft($(document).scrollLeft()-o.scrollSpeed);else if($(window).width()-(event.pageX-$(document).scrollLeft())<o.scrollSensitivity)
scrolled=$(document).scrollLeft($(document).scrollLeft()+o.scrollSpeed);}
if(scrolled!==false&&$.ui.ddmanager&&!o.dropBehaviour)
$.ui.ddmanager.prepareOffsets(this,event);}
this.positionAbs=this._convertPositionTo("absolute");if(!this.options.axis||this.options.axis!="y")this.helper[0].style.left=this.position.left+'px';if(!this.options.axis||this.options.axis!="x")this.helper[0].style.top=this.position.top+'px';for(var i=this.items.length-1;i>=0;i--){var item=this.items[i],itemElement=item.item[0],intersection=this._intersectsWithPointer(item);if(!intersection)continue;if(itemElement!=this.currentItem[0]&&this.placeholder[intersection==1?"next":"prev"]()[0]!=itemElement&&!$.ui.contains(this.placeholder[0],itemElement)&&(this.options.type=='semi-dynamic'?!$.ui.contains(this.element[0],itemElement):true)){this.direction=intersection==1?"down":"up";if(this.options.tolerance=="pointer"||this._intersectsWithSides(item)){this._rearrange(event,item);}else{break;}
this._trigger("change",event,this._uiHash());break;}}
this._contactContainers(event);if($.ui.ddmanager)$.ui.ddmanager.drag(this,event);this._trigger('sort',event,this._uiHash());this.lastPositionAbs=this.positionAbs;return false;},_mouseStop:function(event,noPropagation){if(!event)return;if($.ui.ddmanager&&!this.options.dropBehaviour)
$.ui.ddmanager.drop(this,event);if(this.options.revert){var self=this;var cur=self.placeholder.offset();self.reverting=true;$(this.helper).animate({left:cur.left-this.offset.parent.left-self.margins.left+(this.offsetParent[0]==document.body?0:this.offsetParent[0].scrollLeft),top:cur.top-this.offset.parent.top-self.margins.top+(this.offsetParent[0]==document.body?0:this.offsetParent[0].scrollTop)},parseInt(this.options.revert,10)||500,function(){self._clear(event);});}else{this._clear(event,noPropagation);}
return false;},cancel:function(){var self=this;if(this.dragging){this._mouseUp();if(this.options.helper=="original")
this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper");else
this.currentItem.show();for(var i=this.containers.length-1;i>=0;i--){this.containers[i]._trigger("deactivate",null,self._uiHash(this));if(this.containers[i].containerCache.over){this.containers[i]._trigger("out",null,self._uiHash(this));this.containers[i].containerCache.over=0;}}}
if(this.placeholder[0].parentNode)this.placeholder[0].parentNode.removeChild(this.placeholder[0]);if(this.options.helper!="original"&&this.helper&&this.helper[0].parentNode)this.helper.remove();$.extend(this,{helper:null,dragging:false,reverting:false,_noFinalSort:null});if(this.domPosition.prev){$(this.domPosition.prev).after(this.currentItem);}else{$(this.domPosition.parent).prepend(this.currentItem);}
return true;},serialize:function(o){var items=this._getItemsAsjQuery(o&&o.connected);var str=[];o=o||{};$(items).each(function(){var res=($(o.item||this).attr(o.attribute||'id')||'').match(o.expression||(/(.+)[-=_](.+)/));if(res)str.push((o.key||res[1]+'[]')+'='+(o.key&&o.expression?res[1]:res[2]));});return str.join('&');},toArray:function(o){var items=this._getItemsAsjQuery(o&&o.connected);var ret=[];o=o||{};items.each(function(){ret.push($(o.item||this).attr(o.attribute||'id')||'');});return ret;},_intersectsWith:function(item){var x1=this.positionAbs.left,x2=x1+this.helperProportions.width,y1=this.positionAbs.top,y2=y1+this.helperProportions.height;var l=item.left,r=l+item.width,t=item.top,b=t+item.height;var dyClick=this.offset.click.top,dxClick=this.offset.click.left;var isOverElement=(y1+dyClick)>t&&(y1+dyClick)<b&&(x1+dxClick)>l&&(x1+dxClick)<r;if(this.options.tolerance=="pointer"||this.options.forcePointerForContainers||(this.options.tolerance!="pointer"&&this.helperProportions[this.floating?'width':'height']>item[this.floating?'width':'height'])){return isOverElement;}else{return(l<x1+(this.helperProportions.width/2)&&x2-(this.helperProportions.width/2)<r&&t<y1+(this.helperProportions.height/2)&&y2-(this.helperProportions.height/2)<b);}},_intersectsWithPointer:function(item){var isOverElementHeight=$.ui.isOverAxis(this.positionAbs.top+this.offset.click.top,item.top,item.height),isOverElementWidth=$.ui.isOverAxis(this.positionAbs.left+this.offset.click.left,item.left,item.width),isOverElement=isOverElementHeight&&isOverElementWidth,verticalDirection=this._getDragVerticalDirection(),horizontalDirection=this._getDragHorizontalDirection();if(!isOverElement)
return false;return this.floating?(((horizontalDirection&&horizontalDirection=="right")||verticalDirection=="down")?2:1):(verticalDirection&&(verticalDirection=="down"?2:1));},_intersectsWithSides:function(item){var isOverBottomHalf=$.ui.isOverAxis(this.positionAbs.top+this.offset.click.top,item.top+(item.height/2),item.height),isOverRightHalf=$.ui.isOverAxis(this.positionAbs.left+this.offset.click.left,item.left+(item.width/2),item.width),verticalDirection=this._getDragVerticalDirection(),horizontalDirection=this._getDragHorizontalDirection();if(this.floating&&horizontalDirection){return((horizontalDirection=="right"&&isOverRightHalf)||(horizontalDirection=="left"&&!isOverRightHalf));}else{return verticalDirection&&((verticalDirection=="down"&&isOverBottomHalf)||(verticalDirection=="up"&&!isOverBottomHalf));}},_getDragVerticalDirection:function(){var delta=this.positionAbs.top-this.lastPositionAbs.top;return delta!=0&&(delta>0?"down":"up");},_getDragHorizontalDirection:function(){var delta=this.positionAbs.left-this.lastPositionAbs.left;return delta!=0&&(delta>0?"right":"left");},refresh:function(event){this._refreshItems(event);this.refreshPositions();},_connectWith:function(){var options=this.options;return options.connectWith.constructor==String?[options.connectWith]:options.connectWith;},_getItemsAsjQuery:function(connected){var self=this;var items=[];var queries=[];var connectWith=this._connectWith();if(connectWith&&connected){for(var i=connectWith.length-1;i>=0;i--){var cur=$(connectWith[i]);for(var j=cur.length-1;j>=0;j--){var inst=$.data(cur[j],'sortable');if(inst&&inst!=this&&!inst.options.disabled){queries.push([$.isFunction(inst.options.items)?inst.options.items.call(inst.element):$(inst.options.items,inst.element).not(".ui-sortable-helper"),inst]);}};};}
queries.push([$.isFunction(this.options.items)?this.options.items.call(this.element,null,{options:this.options,item:this.currentItem}):$(this.options.items,this.element).not(".ui-sortable-helper"),this]);for(var i=queries.length-1;i>=0;i--){queries[i][0].each(function(){items.push(this);});};return $(items);},_removeCurrentsFromItems:function(){var list=this.currentItem.find(":data(sortable-item)");for(var i=0;i<this.items.length;i++){for(var j=0;j<list.length;j++){if(list[j]==this.items[i].item[0])
this.items.splice(i,1);};};},_refreshItems:function(event){this.items=[];this.containers=[this];var items=this.items;var self=this;var queries=[[$.isFunction(this.options.items)?this.options.items.call(this.element[0],event,{item:this.currentItem}):$(this.options.items,this.element),this]];var connectWith=this._connectWith();if(connectWith){for(var i=connectWith.length-1;i>=0;i--){var cur=$(connectWith[i]);for(var j=cur.length-1;j>=0;j--){var inst=$.data(cur[j],'sortable');if(inst&&inst!=this&&!inst.options.disabled){queries.push([$.isFunction(inst.options.items)?inst.options.items.call(inst.element[0],event,{item:this.currentItem}):$(inst.options.items,inst.element),inst]);this.containers.push(inst);}};};}
for(var i=queries.length-1;i>=0;i--){var targetData=queries[i][1];var _queries=queries[i][0];for(var j=0,queriesLength=_queries.length;j<queriesLength;j++){var item=$(_queries[j]);item.data('sortable-item',targetData);items.push({item:item,instance:targetData,width:0,height:0,left:0,top:0});};};},refreshPositions:function(fast){if(this.offsetParent&&this.helper){this.offset.parent=this._getParentOffset();}
for(var i=this.items.length-1;i>=0;i--){var item=this.items[i];if(item.instance!=this.currentContainer&&this.currentContainer&&item.item[0]!=this.currentItem[0])
continue;var t=this.options.toleranceElement?$(this.options.toleranceElement,item.item):item.item;if(!fast){item.width=t.outerWidth();item.height=t.outerHeight();}
var p=t.offset();item.left=p.left;item.top=p.top;};if(this.options.custom&&this.options.custom.refreshContainers){this.options.custom.refreshContainers.call(this);}else{for(var i=this.containers.length-1;i>=0;i--){var p=this.containers[i].element.offset();this.containers[i].containerCache.left=p.left;this.containers[i].containerCache.top=p.top;this.containers[i].containerCache.width=this.containers[i].element.outerWidth();this.containers[i].containerCache.height=this.containers[i].element.outerHeight();};}},_createPlaceholder:function(that){var self=that||this,o=self.options;if(!o.placeholder||o.placeholder.constructor==String){var className=o.placeholder;o.placeholder={element:function(){var el=$(document.createElement(self.currentItem[0].nodeName))
.addClass(className||self.currentItem[0].className+" ui-sortable-placeholder")
.removeClass("ui-sortable-helper")[0];if(!className)
el.style.visibility="hidden";return el;},update:function(container,p){if(className&&!o.forcePlaceholderSize)return;if(!p.height()){p.height(self.currentItem.innerHeight()-parseInt(self.currentItem.css('paddingTop')||0,10)-parseInt(self.currentItem.css('paddingBottom')||0,10));};if(!p.width()){p.width(self.currentItem.innerWidth()-parseInt(self.currentItem.css('paddingLeft')||0,10)-parseInt(self.currentItem.css('paddingRight')||0,10));};}};}
self.placeholder=$(o.placeholder.element.call(self.element,self.currentItem));self.currentItem.after(self.placeholder);o.placeholder.update(self,self.placeholder);},_contactContainers:function(event){for(var i=this.containers.length-1;i>=0;i--){if(this._intersectsWith(this.containers[i].containerCache)){if(!this.containers[i].containerCache.over){if(this.currentContainer!=this.containers[i]){var dist=10000;var itemWithLeastDistance=null;var base=this.positionAbs[this.containers[i].floating?'left':'top'];for(var j=this.items.length-1;j>=0;j--){if(!$.ui.contains(this.containers[i].element[0],this.items[j].item[0]))continue;var cur=this.items[j][this.containers[i].floating?'left':'top'];if(Math.abs(cur-base)<dist){dist=Math.abs(cur-base);itemWithLeastDistance=this.items[j];}}
if(!itemWithLeastDistance&&!this.options.dropOnEmpty)
continue;this.currentContainer=this.containers[i];itemWithLeastDistance?this._rearrange(event,itemWithLeastDistance,null,true):this._rearrange(event,null,this.containers[i].element,true);this._trigger("change",event,this._uiHash());this.containers[i]._trigger("change",event,this._uiHash(this));this.options.placeholder.update(this.currentContainer,this.placeholder);}
this.containers[i]._trigger("over",event,this._uiHash(this));this.containers[i].containerCache.over=1;}}else{if(this.containers[i].containerCache.over){this.containers[i]._trigger("out",event,this._uiHash(this));this.containers[i].containerCache.over=0;}}};},_createHelper:function(event){var o=this.options;var helper=$.isFunction(o.helper)?$(o.helper.apply(this.element[0],[event,this.currentItem])):(o.helper=='clone'?this.currentItem.clone():this.currentItem);if(!helper.parents('body').length)
$(o.appendTo!='parent'?o.appendTo:this.currentItem[0].parentNode)[0].appendChild(helper[0]);if(helper[0]==this.currentItem[0])
this._storedCSS={width:this.currentItem[0].style.width,height:this.currentItem[0].style.height,position:this.currentItem.css("position"),top:this.currentItem.css("top"),left:this.currentItem.css("left")};if(helper[0].style.width==''||o.forceHelperSize)helper.width(this.currentItem.width());if(helper[0].style.height==''||o.forceHelperSize)helper.height(this.currentItem.height());return helper;},_adjustOffsetFromHelper:function(obj){if(obj.left!=undefined)this.offset.click.left=obj.left+this.margins.left;if(obj.right!=undefined)this.offset.click.left=this.helperProportions.width-obj.right+this.margins.left;if(obj.top!=undefined)this.offset.click.top=obj.top+this.margins.top;if(obj.bottom!=undefined)this.offset.click.top=this.helperProportions.height-obj.bottom+this.margins.top;},_getParentOffset:function(){this.offsetParent=this.helper.offsetParent();var po=this.offsetParent.offset();if(this.cssPosition=='absolute'&&this.scrollParent[0]!=document&&$.ui.contains(this.scrollParent[0],this.offsetParent[0])){po.left+=this.scrollParent.scrollLeft();po.top+=this.scrollParent.scrollTop();}
if((this.offsetParent[0]==document.body)||(this.offsetParent[0].tagName&&this.offsetParent[0].tagName.toLowerCase()=='html'&&$.browser.msie))
po={top:0,left:0};return{top:po.top+(parseInt(this.offsetParent.css("borderTopWidth"),10)||0),left:po.left+(parseInt(this.offsetParent.css("borderLeftWidth"),10)||0)};},_getRelativeOffset:function(){if(this.cssPosition=="relative"){var p=this.currentItem.position();return{top:p.top-(parseInt(this.helper.css("top"),10)||0)+this.scrollParent.scrollTop(),left:p.left-(parseInt(this.helper.css("left"),10)||0)+this.scrollParent.scrollLeft()};}else{return{top:0,left:0};}},_cacheMargins:function(){this.margins={left:(parseInt(this.currentItem.css("marginLeft"),10)||0),top:(parseInt(this.currentItem.css("marginTop"),10)||0)};},_cacheHelperProportions:function(){this.helperProportions={width:this.helper.outerWidth(),height:this.helper.outerHeight()};},_setContainment:function(){var o=this.options;if(o.containment=='parent')o.containment=this.helper[0].parentNode;if(o.containment=='document'||o.containment=='window')this.containment=[0-this.offset.relative.left-this.offset.parent.left,0-this.offset.relative.top-this.offset.parent.top,$(o.containment=='document'?document:window).width()-this.helperProportions.width-this.margins.left,($(o.containment=='document'?document:window).height()||document.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top];if(!(/^(document|window|parent)$/).test(o.containment)){var ce=$(o.containment)[0];var co=$(o.containment).offset();var over=($(ce).css("overflow")!='hidden');this.containment=[co.left+(parseInt($(ce).css("borderLeftWidth"),10)||0)+(parseInt($(ce).css("paddingLeft"),10)||0)-this.margins.left,co.top+(parseInt($(ce).css("borderTopWidth"),10)||0)+(parseInt($(ce).css("paddingTop"),10)||0)-this.margins.top,co.left+(over?Math.max(ce.scrollWidth,ce.offsetWidth):ce.offsetWidth)-(parseInt($(ce).css("borderLeftWidth"),10)||0)-(parseInt($(ce).css("paddingRight"),10)||0)-this.helperProportions.width-this.margins.left,co.top+(over?Math.max(ce.scrollHeight,ce.offsetHeight):ce.offsetHeight)-(parseInt($(ce).css("borderTopWidth"),10)||0)-(parseInt($(ce).css("paddingBottom"),10)||0)-this.helperProportions.height-this.margins.top];}},_convertPositionTo:function(d,pos){if(!pos)pos=this.position;var mod=d=="absolute"?1:-1;var o=this.options,scroll=this.cssPosition=='absolute'&&!(this.scrollParent[0]!=document&&$.ui.contains(this.scrollParent[0],this.offsetParent[0]))?this.offsetParent:this.scrollParent,scrollIsRootNode=(/(html|body)/i).test(scroll[0].tagName);return{top:(pos.top
+this.offset.relative.top*mod
+this.offset.parent.top*mod
-($.browser.safari&&this.cssPosition=='fixed'?0:(this.cssPosition=='fixed'?-this.scrollParent.scrollTop():(scrollIsRootNode?0:scroll.scrollTop()))*mod)),left:(pos.left
+this.offset.relative.left*mod
+this.offset.parent.left*mod
-($.browser.safari&&this.cssPosition=='fixed'?0:(this.cssPosition=='fixed'?-this.scrollParent.scrollLeft():scrollIsRootNode?0:scroll.scrollLeft())*mod))};},_generatePosition:function(event){var o=this.options,scroll=this.cssPosition=='absolute'&&!(this.scrollParent[0]!=document&&$.ui.contains(this.scrollParent[0],this.offsetParent[0]))?this.offsetParent:this.scrollParent,scrollIsRootNode=(/(html|body)/i).test(scroll[0].tagName);if(this.cssPosition=='relative'&&!(this.scrollParent[0]!=document&&this.scrollParent[0]!=this.offsetParent[0])){this.offset.relative=this._getRelativeOffset();}
var pageX=event.pageX;var pageY=event.pageY;if(this.originalPosition){if(this.containment){if(event.pageX-this.offset.click.left<this.containment[0])pageX=this.containment[0]+this.offset.click.left;if(event.pageY-this.offset.click.top<this.containment[1])pageY=this.containment[1]+this.offset.click.top;if(event.pageX-this.offset.click.left>this.containment[2])pageX=this.containment[2]+this.offset.click.left;if(event.pageY-this.offset.click.top>this.containment[3])pageY=this.containment[3]+this.offset.click.top;}
if(o.grid){var top=this.originalPageY+Math.round((pageY-this.originalPageY)/o.grid[1])*o.grid[1];pageY=this.containment?(!(top-this.offset.click.top<this.containment[1]||top-this.offset.click.top>this.containment[3])?top:(!(top-this.offset.click.top<this.containment[1])?top-o.grid[1]:top+o.grid[1])):top;var left=this.originalPageX+Math.round((pageX-this.originalPageX)/o.grid[0])*o.grid[0];pageX=this.containment?(!(left-this.offset.click.left<this.containment[0]||left-this.offset.click.left>this.containment[2])?left:(!(left-this.offset.click.left<this.containment[0])?left-o.grid[0]:left+o.grid[0])):left;}}
return{top:(pageY
-this.offset.click.top
-this.offset.relative.top
-this.offset.parent.top
+($.browser.safari&&this.cssPosition=='fixed'?0:(this.cssPosition=='fixed'?-this.scrollParent.scrollTop():(scrollIsRootNode?0:scroll.scrollTop())))),left:(pageX
-this.offset.click.left
-this.offset.relative.left
-this.offset.parent.left
+($.browser.safari&&this.cssPosition=='fixed'?0:(this.cssPosition=='fixed'?-this.scrollParent.scrollLeft():scrollIsRootNode?0:scroll.scrollLeft())))};},_rearrange:function(event,i,a,hardRefresh){a?a[0].appendChild(this.placeholder[0]):i.item[0].parentNode.insertBefore(this.placeholder[0],(this.direction=='down'?i.item[0]:i.item[0].nextSibling));this.counter=this.counter?++this.counter:1;var self=this,counter=this.counter;window.setTimeout(function(){if(counter==self.counter)self.refreshPositions(!hardRefresh);},0);},_clear:function(event,noPropagation){this.reverting=false;var delayedTriggers=[],self=this;if(!this._noFinalSort&&this.currentItem[0].parentNode)this.placeholder.before(this.currentItem);this._noFinalSort=null;if(this.helper[0]==this.currentItem[0]){for(var i in this._storedCSS){if(this._storedCSS[i]=='auto'||this._storedCSS[i]=='static')this._storedCSS[i]='';}
this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper");}else{this.currentItem.show();}
if(this.fromOutside&&!noPropagation)delayedTriggers.push(function(event){this._trigger("receive",event,this._uiHash(this.fromOutside));});if((this.fromOutside||this.domPosition.prev!=this.currentItem.prev().not(".ui-sortable-helper")[0]||this.domPosition.parent!=this.currentItem.parent()[0])&&!noPropagation)delayedTriggers.push(function(event){this._trigger("update",event,this._uiHash());});if(!$.ui.contains(this.element[0],this.currentItem[0])){if(!noPropagation)delayedTriggers.push(function(event){this._trigger("remove",event,this._uiHash());});for(var i=this.containers.length-1;i>=0;i--){if($.ui.contains(this.containers[i].element[0],this.currentItem[0])&&!noPropagation){delayedTriggers.push((function(c){return function(event){c._trigger("receive",event,this._uiHash(this));};}).call(this,this.containers[i]));delayedTriggers.push((function(c){return function(event){c._trigger("update",event,this._uiHash(this));};}).call(this,this.containers[i]));}};};for(var i=this.containers.length-1;i>=0;i--){if(!noPropagation)delayedTriggers.push((function(c){return function(event){c._trigger("deactivate",event,this._uiHash(this));};}).call(this,this.containers[i]));if(this.containers[i].containerCache.over){delayedTriggers.push((function(c){return function(event){c._trigger("out",event,this._uiHash(this));};}).call(this,this.containers[i]));this.containers[i].containerCache.over=0;}}
if(this._storedCursor)$('body').css("cursor",this._storedCursor);if(this._storedOpacity)this.helper.css("opacity",this._storedOpacity);if(this._storedZIndex)this.helper.css("zIndex",this._storedZIndex=='auto'?'':this._storedZIndex);this.dragging=false;if(this.cancelHelperRemoval){if(!noPropagation){this._trigger("beforeStop",event,this._uiHash());for(var i=0;i<delayedTriggers.length;i++){delayedTriggers[i].call(this,event);};this._trigger("stop",event,this._uiHash());}
return false;}
if(!noPropagation)this._trigger("beforeStop",event,this._uiHash());this.placeholder[0].parentNode.removeChild(this.placeholder[0]);if(this.helper[0]!=this.currentItem[0])this.helper.remove();this.helper=null;if(!noPropagation){for(var i=0;i<delayedTriggers.length;i++){delayedTriggers[i].call(this,event);};this._trigger("stop",event,this._uiHash());}
this.fromOutside=false;return true;},_trigger:function(){if($.widget.prototype._trigger.apply(this,arguments)===false){this.cancel();}},_uiHash:function(inst){var self=inst||this;return{helper:self.helper,placeholder:self.placeholder||$([]),position:self.position,absolutePosition:self.positionAbs,offset:self.positionAbs,item:self.currentItem,sender:inst?inst.element:null};}}));$.extend($.ui.sortable,{getter:"serialize toArray",version:"1.7.2",eventPrefix:"sort",defaults:{appendTo:"parent",axis:false,cancel:":input,option",connectWith:false,containment:false,cursor:'auto',cursorAt:false,delay:0,distance:1,dropOnEmpty:true,forcePlaceholderSize:false,forceHelperSize:false,grid:false,handle:false,helper:"original",items:'> *',opacity:false,placeholder:false,revert:false,scroll:true,scrollSensitivity:20,scrollSpeed:20,scope:"default",tolerance:"intersect",zIndex:1000}});})(jQuery);(function($){$.widget("ui.tabs",{_init:function(){if(this.options.deselectable!==undefined){this.options.collapsible=this.options.deselectable;}
this._tabify(true);},_setData:function(key,value){if(key=='selected'){if(this.options.collapsible&&value==this.options.selected){return;}
this.select(value);}
else{this.options[key]=value;if(key=='deselectable'){this.options.collapsible=value;}
this._tabify();}},_tabId:function(a){return a.title&&a.title.replace(/\s/g,'_').replace(/[^A-Za-z0-9\-_:\.]/g,'')||this.options.idPrefix+$.data(a);},_sanitizeSelector:function(hash){return hash.replace(/:/g,'\\:');},_cookie:function(){var cookie=this.cookie||(this.cookie=this.options.cookie.name||'ui-tabs-'+$.data(this.list[0]));return $.cookie.apply(null,[cookie].concat($.makeArray(arguments)));},_ui:function(tab,panel){return{tab:tab,panel:panel,index:this.anchors.index(tab)};},_cleanup:function(){this.lis.filter('.ui-state-processing').removeClass('ui-state-processing')
.find('span:data(label.tabs)')
.each(function(){var el=$(this);el.html(el.data('label.tabs')).removeData('label.tabs');});},_tabify:function(init){this.list=this.element.children('ul:first');this.lis=$('li:has(a[href])',this.list);this.anchors=this.lis.map(function(){return $('a',this)[0];});this.panels=$([]);var self=this,o=this.options;var fragmentId=/^#.+/;this.anchors.each(function(i,a){var href=$(a).attr('href');var hrefBase=href.split('#')[0],baseEl;if(hrefBase&&(hrefBase===location.toString().split('#')[0]||(baseEl=$('base')[0])&&hrefBase===baseEl.href)){href=a.hash;a.href=href;}
if(fragmentId.test(href)){self.panels=self.panels.add(self._sanitizeSelector(href));}
else if(href!='#'){$.data(a,'href.tabs',href);$.data(a,'load.tabs',href.replace(/#.*$/,''));var id=self._tabId(a);a.href='#'+id;var $panel=$('#'+id);if(!$panel.length){$panel=$(o.panelTemplate).attr('id',id).addClass('ui-tabs-panel ui-widget-content ui-corner-bottom')
.insertAfter(self.panels[i-1]||self.list);$panel.data('destroy.tabs',true);}
self.panels=self.panels.add($panel);}
else{o.disabled.push(i);}});if(init){this.element.addClass('ui-tabs ui-widget ui-widget-content ui-corner-all');this.list.addClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all');this.lis.addClass('ui-state-default ui-corner-top');this.panels.addClass('ui-tabs-panel ui-widget-content ui-corner-bottom');if(o.selected===undefined){if(location.hash){this.anchors.each(function(i,a){if(a.hash==location.hash){o.selected=i;return false;}});}
if(typeof o.selected!='number'&&o.cookie){o.selected=parseInt(self._cookie(),10);}
if(typeof o.selected!='number'&&this.lis.filter('.ui-tabs-selected').length){o.selected=this.lis.index(this.lis.filter('.ui-tabs-selected'));}
o.selected=o.selected||0;}
else if(o.selected===null){o.selected=-1;}
o.selected=((o.selected>=0&&this.anchors[o.selected])||o.selected<0)?o.selected:0;o.disabled=$.unique(o.disabled.concat($.map(this.lis.filter('.ui-state-disabled'),function(n,i){return self.lis.index(n);}))).sort();if($.inArray(o.selected,o.disabled)!=-1){o.disabled.splice($.inArray(o.selected,o.disabled),1);}
this.panels.addClass('ui-tabs-hide');this.lis.removeClass('ui-tabs-selected ui-state-active');if(o.selected>=0&&this.anchors.length){this.panels.eq(o.selected).removeClass('ui-tabs-hide');this.lis.eq(o.selected).addClass('ui-tabs-selected ui-state-active');self.element.queue("tabs",function(){self._trigger('show',null,self._ui(self.anchors[o.selected],self.panels[o.selected]));});this.load(o.selected);}
$(window).bind('unload',function(){self.lis.add(self.anchors).unbind('.tabs');self.lis=self.anchors=self.panels=null;});}
else{o.selected=this.lis.index(this.lis.filter('.ui-tabs-selected'));}
this.element[o.collapsible?'addClass':'removeClass']('ui-tabs-collapsible');if(o.cookie){this._cookie(o.selected,o.cookie);}
for(var i=0,li;(li=this.lis[i]);i++){$(li)[$.inArray(i,o.disabled)!=-1&&!$(li).hasClass('ui-tabs-selected')?'addClass':'removeClass']('ui-state-disabled');}
if(o.cache===false){this.anchors.removeData('cache.tabs');}
this.lis.add(this.anchors).unbind('.tabs');if(o.event!='mouseover'){var addState=function(state,el){if(el.is(':not(.ui-state-disabled)')){el.addClass('ui-state-'+state);}};var removeState=function(state,el){el.removeClass('ui-state-'+state);};this.lis.bind('mouseover.tabs',function(){addState('hover',$(this));});this.lis.bind('mouseout.tabs',function(){removeState('hover',$(this));});this.anchors.bind('focus.tabs',function(){addState('focus',$(this).closest('li'));});this.anchors.bind('blur.tabs',function(){removeState('focus',$(this).closest('li'));});}
var hideFx,showFx;if(o.fx){if($.isArray(o.fx)){hideFx=o.fx[0];showFx=o.fx[1];}
else{hideFx=showFx=o.fx;}}
function resetStyle($el,fx){$el.css({display:''});if($.browser.msie&&fx.opacity){$el[0].style.removeAttribute('filter');}}
var showTab=showFx?function(clicked,$show){$(clicked).closest('li').removeClass('ui-state-default').addClass('ui-tabs-selected ui-state-active');$show.hide().removeClass('ui-tabs-hide')
.animate(showFx,showFx.duration||'normal',function(){resetStyle($show,showFx);self._trigger('show',null,self._ui(clicked,$show[0]));});}:function(clicked,$show){$(clicked).closest('li').removeClass('ui-state-default').addClass('ui-tabs-selected ui-state-active');$show.removeClass('ui-tabs-hide');self._trigger('show',null,self._ui(clicked,$show[0]));};var hideTab=hideFx?function(clicked,$hide){$hide.animate(hideFx,hideFx.duration||'normal',function(){self.lis.removeClass('ui-tabs-selected ui-state-active').addClass('ui-state-default');$hide.addClass('ui-tabs-hide');resetStyle($hide,hideFx);self.element.dequeue("tabs");});}:function(clicked,$hide,$show){self.lis.removeClass('ui-tabs-selected ui-state-active').addClass('ui-state-default');$hide.addClass('ui-tabs-hide');self.element.dequeue("tabs");};this.anchors.bind(o.event+'.tabs',function(){var el=this,$li=$(this).closest('li'),$hide=self.panels.filter(':not(.ui-tabs-hide)'),$show=$(self._sanitizeSelector(this.hash));if(($li.hasClass('ui-tabs-selected')&&!o.collapsible)||$li.hasClass('ui-state-disabled')||$li.hasClass('ui-state-processing')||self._trigger('select',null,self._ui(this,$show[0]))===false){this.blur();return false;}
o.selected=self.anchors.index(this);self.abort();if(o.collapsible){if($li.hasClass('ui-tabs-selected')){o.selected=-1;if(o.cookie){self._cookie(o.selected,o.cookie);}
self.element.queue("tabs",function(){hideTab(el,$hide);}).dequeue("tabs");this.blur();return false;}
else if(!$hide.length){if(o.cookie){self._cookie(o.selected,o.cookie);}
self.element.queue("tabs",function(){showTab(el,$show);});self.load(self.anchors.index(this));this.blur();return false;}}
if(o.cookie){self._cookie(o.selected,o.cookie);}
if($show.length){if($hide.length){self.element.queue("tabs",function(){hideTab(el,$hide);});}
self.element.queue("tabs",function(){showTab(el,$show);});self.load(self.anchors.index(this));}
else{throw'jQuery UI Tabs: Mismatching fragment identifier.';}
if($.browser.msie){this.blur();}});this.anchors.bind('click.tabs',function(){return false;});},destroy:function(){var o=this.options;this.abort();this.element.unbind('.tabs')
.removeClass('ui-tabs ui-widget ui-widget-content ui-corner-all ui-tabs-collapsible')
.removeData('tabs');this.list.removeClass('ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all');this.anchors.each(function(){var href=$.data(this,'href.tabs');if(href){this.href=href;}
var $this=$(this).unbind('.tabs');$.each(['href','load','cache'],function(i,prefix){$this.removeData(prefix+'.tabs');});});this.lis.unbind('.tabs').add(this.panels).each(function(){if($.data(this,'destroy.tabs')){$(this).remove();}
else{$(this).removeClass(['ui-state-default','ui-corner-top','ui-tabs-selected','ui-state-active','ui-state-hover','ui-state-focus','ui-state-disabled','ui-tabs-panel','ui-widget-content','ui-corner-bottom','ui-tabs-hide'].join(' '));}});if(o.cookie){this._cookie(null,o.cookie);}},add:function(url,label,index){if(index===undefined){index=this.anchors.length;}
var self=this,o=this.options,$li=$(o.tabTemplate.replace(/#\{href\}/g,url).replace(/#\{label\}/g,label)),id=!url.indexOf('#')?url.replace('#',''):this._tabId($('a',$li)[0]);$li.addClass('ui-state-default ui-corner-top').data('destroy.tabs',true);var $panel=$('#'+id);if(!$panel.length){$panel=$(o.panelTemplate).attr('id',id).data('destroy.tabs',true);}
$panel.addClass('ui-tabs-panel ui-widget-content ui-corner-bottom ui-tabs-hide');if(index>=this.lis.length){$li.appendTo(this.list);$panel.appendTo(this.list[0].parentNode);}
else{$li.insertBefore(this.lis[index]);$panel.insertBefore(this.panels[index]);}
o.disabled=$.map(o.disabled,function(n,i){return n>=index?++n:n;});this._tabify();if(this.anchors.length==1){$li.addClass('ui-tabs-selected ui-state-active');$panel.removeClass('ui-tabs-hide');this.element.queue("tabs",function(){self._trigger('show',null,self._ui(self.anchors[0],self.panels[0]));});this.load(0);}
this._trigger('add',null,this._ui(this.anchors[index],this.panels[index]));},remove:function(index){var o=this.options,$li=this.lis.eq(index).remove(),$panel=this.panels.eq(index).remove();if($li.hasClass('ui-tabs-selected')&&this.anchors.length>1){this.select(index+(index+1<this.anchors.length?1:-1));}
o.disabled=$.map($.grep(o.disabled,function(n,i){return n!=index;}),function(n,i){return n>=index?--n:n;});this._tabify();this._trigger('remove',null,this._ui($li.find('a')[0],$panel[0]));},enable:function(index){var o=this.options;if($.inArray(index,o.disabled)==-1){return;}
this.lis.eq(index).removeClass('ui-state-disabled');o.disabled=$.grep(o.disabled,function(n,i){return n!=index;});this._trigger('enable',null,this._ui(this.anchors[index],this.panels[index]));},disable:function(index){var self=this,o=this.options;if(index!=o.selected){this.lis.eq(index).addClass('ui-state-disabled');o.disabled.push(index);o.disabled.sort();this._trigger('disable',null,this._ui(this.anchors[index],this.panels[index]));}},select:function(index){if(typeof index=='string'){index=this.anchors.index(this.anchors.filter('[href$='+index+']'));}
else if(index===null){index=-1;}
if(index==-1&&this.options.collapsible){index=this.options.selected;}
this.anchors.eq(index).trigger(this.options.event+'.tabs');},load:function(index){var self=this,o=this.options,a=this.anchors.eq(index)[0],url=$.data(a,'load.tabs');this.abort();if(!url||this.element.queue("tabs").length!==0&&$.data(a,'cache.tabs')){this.element.dequeue("tabs");return;}
this.lis.eq(index).addClass('ui-state-processing');if(o.spinner){var span=$('span',a);span.data('label.tabs',span.html()).html(o.spinner);}
this.xhr=$.ajax($.extend({},o.ajaxOptions,{url:url,success:function(r,s){$(self._sanitizeSelector(a.hash)).html(r);self._cleanup();if(o.cache){$.data(a,'cache.tabs',true);}
self._trigger('load',null,self._ui(self.anchors[index],self.panels[index]));try{o.ajaxOptions.success(r,s);}
catch(e){}
self.element.dequeue("tabs");}}));},abort:function(){this.element.queue([]);this.panels.stop(false,true);if(this.xhr){this.xhr.abort();delete this.xhr;}
this._cleanup();},url:function(index,url){this.anchors.eq(index).removeData('cache.tabs').data('load.tabs',url);},length:function(){return this.anchors.length;}});$.extend($.ui.tabs,{version:'1.7.2',getter:'length',defaults:{ajaxOptions:null,cache:false,cookie:null,collapsible:false,disabled:[],event:'click',fx:null,idPrefix:'ui-tabs-',panelTemplate:'<div></div>',spinner:'<em>Loading&#8230;</em>',tabTemplate:'<li><a href="#{href}"><span>#{label}</span></a></li>'}});$.extend($.ui.tabs.prototype,{rotation:null,rotate:function(ms,continuing){var self=this,o=this.options;var rotate=self._rotate||(self._rotate=function(e){clearTimeout(self.rotation);self.rotation=setTimeout(function(){var t=o.selected;self.select(++t<self.anchors.length?t:0);},ms);if(e){e.stopPropagation();}});var stop=self._unrotate||(self._unrotate=!continuing?function(e){if(e.clientX){self.rotate(null);}}:function(e){t=o.selected;rotate();});if(ms){this.element.bind('tabsshow',rotate);this.anchors.bind(o.event+'.tabs',stop);rotate();}
else{clearTimeout(self.rotation);this.element.unbind('tabsshow',rotate);this.anchors.unbind(o.event+'.tabs',stop);delete this._rotate;delete this._unrotate;}}});})(jQuery);(function(jQuery){var self=null;jQuery.fn.autogrow=function(o)
{return this.each(function(){new jQuery.autogrow(this,o);});};jQuery.autogrow=function(e,o)
{this.options=o||{};this.dummy=null;this.interval=null;this.line_height=this.options.lineHeight||parseInt(jQuery(e).css('line-height'));this.min_height=this.options.minHeight||parseInt(jQuery(e).css('min-height'));this.max_height=this.options.maxHeight||parseInt(jQuery(e).css('max-height'));;this.textarea=jQuery(e);if(this.line_height==NaN)
{this.line_height=0;}
if(this.min_height==NaN||this.min_height==0)
{this.min_height==this.textarea.height();}
this.init();};jQuery.autogrow.fn=jQuery.autogrow.prototype={autogrow:'1.2.2'};jQuery.autogrow.fn.extend=jQuery.autogrow.extend=jQuery.extend;jQuery.autogrow.fn.extend({init:function(){var self=this;this.textarea.css({overflow:'hidden',display:'block'});this.textarea.bind('focus',function(){self.startExpand()}).bind('blur',function(){self.stopExpand()});this.checkExpand();},startExpand:function(){var self=this;this.interval=window.setInterval(function(){self.checkExpand()},400);},stopExpand:function(){clearInterval(this.interval);},checkExpand:function(){if(this.dummy==null)
{this.dummy=jQuery('<div></div>');this.dummy.css({'font-size':this.textarea.css('font-size'),'font-family':this.textarea.css('font-family'),'width':this.textarea.css('width'),'padding':this.textarea.css('padding'),'line-height':this.line_height+'px','overflow-x':'hidden','position':'absolute','top':0,'left':-9999}).appendTo('body');}
var html=this.textarea.val().replace(/(<|>)/g,'');if($.browser.msie)
{html=html.replace(/\n/g,'<BR>new');}
else
{html=html.replace(/\n/g,'<br>new');}
if(this.dummy.html()!=html)
{this.dummy.html(html);if(this.max_height>0&&(this.dummy.height()+this.line_height>this.max_height))
{this.textarea.css('overflow-y','auto');}
else
{this.textarea.css('overflow-y','hidden');if(this.textarea.height()<this.dummy.height()+this.line_height||(this.dummy.height()<this.textarea.height()))
{var	new_height=this.dummy.height()+this.line_height,self=this;this.textarea.trigger('autogrow-will-grow',[new_height]).animate({height:new_height+'px'},100,function(){self.textarea.trigger('autogrow-did-grow',[new_height]);});}}}}});})(jQuery);;(function($){$.widget("slash.menu",$.extend({},$.ui.mouse,{_init:function(){this._mouseInit();this.triggers=$(this.options.triggers||[]);this.element.unbind('mousedown.'+this.widgetName);var self=this;this.triggers.bind('mousedown.'+this.widgetName,function(e){return self._menuMouseDown(e,{trigger:this});}).bind('click.'+this.widgetName,function(e){});(this.options.cssNamespace&&(this._hoverClass=this.options.cssNamespace+'-hover'));this._mouseStarted=false;},_reset:function(){this.hoverTimeoutExceeded=this._hoverStarted=this._mouseStarted=this._menuStarted=false;},context:function(e){this._reset();return this._menuMouseDown(e,{trigger:original_target(e)});},cancel:function(e){return this._inheritMouseUp(e);},destroy:function(){if(this.element.data('menu')){this.element.removeData('menu').unbind('.menu');this._mouseDestroy();}},tracking:function(action,e,ui){var track={'begin':true,'disable':false,'enable':true,'end':false,'start':true,'stop':false,'toggle':!this._tracking}[action];if(track===undefined){}else if(!track&&(this._tracking===undefined)){this._tracking=false;}else if(track!=(this._tracking||false)){var ui_for_event=track?'mouseenter':'mouseleave';(ui||(ui=this._uiHash(e,ui_for_event)));(this._tracking&&this._item('out',e,ui));this._tracking=track;(this._tracking&&this._item('over',e,ui));}},_mouseCapture:function(e){this._menuStarted&&this._inheritMouseUp(e);this.clickDurationExceeded=(this.options.clickDuration||0)<=0;if(!this.clickDurationExceeded){var self=this;this._clickDurationTimer=setTimeout(function(){self.clickDurationExceeded=true;self._mouseStart(e);},this.options.clickDuration);}
this._overTarget=this._tracking=undefined;this._hoverStarted=this.hoverTimeoutExceeded=false;this._menuStarted=(this.options.clickToHover&&this._mouseStart(e));return true;},_mouseStart:function(e){if(!this._menuStarted){var ui=this._uiHash(e);this._trigger('start',e,ui);((this._tracking===undefined)&&this.tracking('start',e,ui));var self=this;this.element.children().bind('mouseleave.'+this.widgetName,function(e){return self._item('out',e);}).bind('mouseenter.'+this.widgetName,function(e){return self._item('over',e);});this._menuStarted=true;}
return this._menuStarted;},_item:function(action,e,ui){(ui||(ui=this._uiHash(e)));(e&&(this._overTarget=ui.over));if(ui[action]&&this._tracking){((action==='out')&&this._highlight(action,e,ui));this._trigger(action,e,ui);((action==='over')&&this._highlight(action,e,ui));}},_mouseStop:function(e,ui){if(this._menuStarted){this.hoverTimeoutExceeded||this._item('select',e,ui||this._uiHash(e,'select'));ui||(ui=this._uiHash(e,'stop'));this.tracking('stop',e,ui);this._trigger('stop',e,ui);}
this._reset();if(this._clickDurationTimer){clearTimeout(this._clickDurationTimer);this._clickDurationTimer=undefined;}
if(this._hoverDurationTimer){clearTimeout(this._hoverDurationTimer);this._hoverDurationTimer=undefined;}
$(document).unbind('mousedown.'+this.widgetName).unbind('mouseup.'+this.widgetName);this.element.children().unbind('mouseenter.'+this.widgetName).unbind('mouseleave.'+this.widgetName);},_inheritMouseUp:function(e,ui){this._mouseDownEvent||(this._mouseDownEvent=this._menuMouseDownEvent||{});this._mouseStarted=true;return this._mouseUp(e,ui);},_menuMouseDown:function(e,ui){this._menuMouseDownEvent=e;var is_trigger=ui.trigger&&ui.trigger!==document&&(this.options.liveTriggers||this.triggers.index(ui.trigger)>=0);if(!is_trigger){var ui_stop=this._uiHash(e,'stop');if(!ui_stop.out){return this._inheritMouseUp(e,ui);}}
if(this._hoverStarted){clearTimeout(this._hoverDurationTimer);this._hoverDurationTimer=undefined;this.tracking('start',e);}else{(is_trigger&&(this._startTarget=ui.trigger));this._mouseDown(e);$(document).unbind('mouseup.'+this.widgetName,this._mouseUpDelegate);}
var self=this;$(document).one('mouseup.'+this.widgetName,function(e){return self._menuMouseUp(e);});return true;},_menuMouseUp:function(e){clearTimeout(this._clickDurationTimer);this._clickDurationTimer=undefined;if(this._startTarget&&!this._menuStarted){this._inheritMouseUp(e);this._trigger('click',e,{click:this._startTarget});return false;}
var ui;if(this._hoverStarted||this._mouseStarted||(this._tracking&&(ui=this._uiHash(e,'stop')).select)||this._clickDurationExceeded(e)){return this._inheritMouseUp(e,ui);}
this._hoverStarted=true;var self=this;if(this.options.hoverTimeout){this._hoverDurationTimer=setTimeout(function(){var e=$.Event('mouseup');e.target=self._overTarget;self.hoverTimeoutExceeded=true;self._inheritMouseUp(e);},this.options.hoverTimeout);}
$(document).unbind('mouseup.'+this.widgetName,this._mouseUpDelegate).
one('mousedown.'+this.widgetName,function(e){return self._menuMouseDown(e,{trigger:document});});e.preventDefault();return false;},_highlight:function(action,e,ui){var if_highlight={'over':true,'out':false}[action];if(this._hoverClass&&if_highlight!==undefined&&ui[action]){$(ui[action]).toggleClass(this._hoverClass,if_highlight);this.element.toggleClass(this._hoverClass,!!ui.over);}},_itemOf:function(el){var item=undefined;if(el){var	menu=this.element[0],$el=$(el),$path=$el.add($el.parents());$path.each(function(i){if(this===menu){item=$path[i-1];return false;}});}
return item;},_uiHash:function(event_or_type,event_type,ui){var	actual_event=(event_or_type&&event_or_type.type)?event_or_type:undefined,event_items=actual_event?{item:this._itemOf(actual_event.target),relatedItem:this._itemOf(actual_event.relatedTarget)}:{item:this._overTarget},requested_type=event_type||(actual_event?actual_event.type:event_or_type),map_event_to_ui={'mouseenter':{over:'item',out:'relatedItem'},'mouseleave':{out:'item',over:'relatedItem'},'select':{select:'item',over:'item'},'stop':{out:'item'}}[requested_type]||{over:'item'};(ui||(ui={}));$.each(map_event_to_ui,function(to,from){ui[to]=event_items[from];});ui.trigger=this._startTarget;ui.event=actual_event;return ui;},_clickDurationExceeded:function(e){return this.clickDurationExceeded;}}));$.extend($.slash.menu,{version:"0.5",eventPrefix:"menu",defaults:{distance:1,clickToHover:true,clickDuration:300,hoverTimeout:false,liveTriggers:false}});})(jQuery);;(function($){function save_slashboxes(){if(!check_logged_in()){return false;}
ajax_update({op:'page_save_user_boxes',reskey:reskey_static,bids:$('#slashboxes article header').map(function(){return this.id.slice(0,-6);}).get().join(',')});}
$(function(){$('#slashboxes').sortable({axis:'y',containment:'parent',handle:'header',items:'> article:not(.nosort)',update:save_slashboxes}).find('> article:not(.nosort) > header').
append('<div class="actions"><a class="ico close" href="#"><b class="ui-icon close"></b><span>Close</span></a></div>');$('#slashboxes article:not(.nosort) header .close').live('click',function(){$(this).closest('article').remove();save_slashboxes();after_article_moved();});});})(Slash.jQuery);;var Tags={},tag_admin=false,gFocusedText,$previous_context_trigger=$([]);(function(){var WS=/\s+/,NODNIX=/\b(?:meta)?(?:nod|nix)\b/i;function topics($tags,sort){var tags=$tags.filter('a.topic').map(function(){return $(this).text();}).get();sort&&tags.sort();return tags.join(' ');}
function inspect($tagbar){var $tags=$tagbar.children();return{skin:$tags.filter('a.main:first').text(),vote:$tags.filter('a.my[href$=/nod],a.my[href$=/nix],a.my[href$=/metanod],a.my[href$=/metanix]').text(),topics:topics($tags,'sorted'),datatype:$tags.filter('a.datatype')};}
function preprocess(fhitem,tags){var nodnix=NODNIX.test(tags)&&($(fhitem).is('.fhitem-comment')?firehose_handle_comment_nodnix:firehose_handle_nodnix);tags=tags.split(WS);firehose_handle_admin_commands&&(tags=firehose_handle_admin_commands.call(fhitem,tags));nodnix&&(tags=nodnix.call(fhitem,tags));return tags.join(' ');}
Tags.submit=function(fhitem_or_tagbar,tags){var	$target=$(fhitem_or_tagbar),$tagbar=$target,$fhitem=$target.closest('article.fhitem, div.fhitem'),fhitem=$fhitem[0],key=fhitem_key(fhitem),$spinner=$('span.tag-server-busy',fhitem).show();$tagbar.is('.tag-bar')||($tagbar=$fhitem.find('span.tag-bar'));tags&&(tags=preprocess(fhitem,tags));if(window.google_analytics_uacct){if(tags){_gaq.push(['_trackEvent','Tag','TagCreate',tags]);_gaq.push(['b._trackEvent','Tag','TagCreate',tags]);}}
$.ajax({type:'POST',dataType:'json',data:{op:'tags_setget_display',key:key.key,key_type:key.key_type,reskey:reskey_static,tags:tags||'',limit_fetch:'',include_topic_images:sign($fhitem.is('.fhitem-editor'))},success:function(nobj){var prev=inspect($tagbar),next=inspect($tagbar.html(nobj.markup));if(!nobj.status&&$tagbar.attr("id").match(/edit/)){$tagbar.parent().append("<div id='editor_tag_update_message'><span style='color:red; text-decoration:blink'>"+nobj.message+"</span></div>");}
function notice_changes(k,data){next[k]!==prev[k]&&$fhitem.trigger(k+'-assigned',data||next[k]);}
notice_changes('datatype');notice_changes('topics',topics($tagbar.children()));notice_changes('skin');notice_changes('vote');if(nobj.markup.search('tag img')>0){$('#editor').addClass('tagwithimg');}},complete:function(){}});};Tags.fetch=function(fhitem_or_tagbar){Tags.submit(fhitem_or_tagbar);};})();(function(){var	IS_AUTOCOMPLETE_READY='ac-ready',ENTER=13,ESC=27,SPACE=32,SUBMIT_FOR={},CLEAR_FOR={},CLOSE_FOR={};(function(){SUBMIT_FOR[ENTER]=SUBMIT_FOR[SPACE]=true;CLEAR_FOR[ENTER]=CLEAR_FOR[ESC]=CLEAR_FOR[SPACE]=true;CLOSE_FOR[ENTER]=CLOSE_FOR[ESC]=true;})();$('input.tag-entry').live('keydown',function(event){var $this=$(original_target(event)),key=event.which||event.keyCode;if(!$this.data(IS_AUTOCOMPLETE_READY)){$this.autocomplete('/ajax.pl',{autoFill:false,delay:750,minChars:3,selectFirst:false,max:25,loadingClass:'working',extraParams:{op:'tags_list_tagnames',reskey:reskey_static}}).data(IS_AUTOCOMPLETE_READY,true);}
if(key===ESC){event.preventDefault();event.stopImmediatePropagation();return false;}
return true;}).live('keyup',function(event){var $this=$(original_target(event)),key=event.which||event.keyCode;SUBMIT_FOR[key]&&Tags.submit($this.siblings('span.tag-bar'),$this.val());CLEAR_FOR[key]&&$this.val('');CLOSE_FOR[key]&&firehose_toggle_tag_ui_to(false,$this);return true;});$('a.edit-toggle').live('click',function(e){var	toggle=original_target(e),editing=$(toggle).closest('.fhitem-editor').length;(editing||check_logged_in())&&firehose_toggle_tag_ui(toggle);});$('input.tag-entry.default').live('click',function(){$(this).removeClass('default').val('');});$('input.tag-entry.default').live('keydown',function(){$(this).removeClass('default').val('');});})();;$.ajaxSetup({url:'/ajax.pl',type:'POST',contentType:'application/x-www-form-urlencoded'});window.Slash||(window.Slash={});Slash.Firehose||(Slash.Firehose={});var reskey_static='';var global_returnto='';var firehose_settings={startdate:'',duration:'',mode:'',color:'',orderby:'',orderdir:'',view:'',viewtitle:'',tab:'',fhfilter:'',base_filter:'',user_view_uid:'',issue:'',is_embedded:0,not_id:0,section:0,sectionname:'',more_num:0,metamod:0,admin_filters:0};var	firehose_sitename="",firehose_slogan="",firehose_smallscreen=0;var	firehose_item_count=0,firehose_future=null,firehose_more_increment=10;var	fh_play=0,fh_is_timed_out=0,fh_update_timerids=[],fh_is_admin=0,console_updating=0,fh_ticksize,fh_colors=[],fh_idle_skin=0,vendor_popup_timerids=[],vendor_popup_id=0,firehose_exists=0;var	fh_adTimerSecsMax=15,fh_adTimerClicksMax=0,fh_adTimerUrl='';var FHID_PREFIX=/^(firehose|editor)-/;function ajax_update(request_params,id,handlers,options){if(!options){options={};}
var opts={data:request_params};if(options.request_url){opts.url=options.request_url;}
if(options.async_off){opts.async=false;}
if(id){opts.success=function(html){$any(id).html(html);};}
if(handlers&&handlers.onComplete){opts.complete=handlers.onComplete;}
if(handlers&&handlers.onError){opts.error=handlers.onError;}
jQuery.ajax(opts);}
function firehose_set_disc_system(x){ajax_update({op:'firehose_set_disc_system',reskey:reskey_static,section:firehose_settings.section,disctype2:x},'',{onComplete:json_handler});}
function ajax_periodic_update(interval_in_seconds,request_params,id,handlers,options){setInterval(function(){ajax_update(request_params,id,handlers,options);},interval_in_seconds*1000);}
function eval_response(xhr){return evalExpr(xhr.responseText);}
function json_handler(xhr){var response=eval_response(xhr);response&&json_update(response);return response;}
function json_update(response){if(!response){return;}
$.globalEval(response.eval_first);$.each(response.html||[],function(elem_id,new_html){$any(elem_id).html(new_html);});$.each(response.value||[],function(elem_id,new_value){$any(elem_id).each(function(){if(this!==gFocusedText){$(this).val(new_value);}});});$.each(response.html_append||[],function(elem_id,new_html){$any(elem_id).append(new_html);});$.each(response.html_add_after||[],function(elem_id,add_html){$any(elem_id).after(add_html);});$.each(response.html_add_before||[],function(elem_id,add_html){$any(elem_id).before(add_html);});$.each(response.html_replace||[],function(elem_id,new_html){$any(elem_id).replaceWith(new_html).show();});$.each(response.html_append_substr||[],function(elem_id,new_html){$any(elem_id).each(function(){var	$this=$(this),old_html=$this.html(),truncate_at=old_html.search(/<span class="?substr"?> ?<\/span>[\s\S]*$/i);if(truncate_at!=-1){old_html=old_html.substr(0,truncate_at);}
$this.html(old_html+new_html);});});if(response.updates&&response.updates.length){$.each(response.updates,function(){if(this[0]=="add"){$("#firehoselist").append(this[2]);}else if(this[0]=="remove"){$("#firehose-"+this[1]).remove();}});}
$.each(response.events||[],function(){if(this.event){$(this.target||document).trigger(this.event,this.data);}});$.globalEval(response.eval_last);}
var view;(function(){var $body,$html_body,el_q=[];function DOM_descendant(ancestor,descendant){return $(descendant).eq(0).parents().index(ancestor)>=0;}
function offset(el,b,how){var $el=$(el),e=new Bounds($el);if(!Bounds.empty(e)){if(TypeOf.element($el[0])){$.each({top:-1,left:-1,bottom:1,right:1},function(edge,scale){e[edge]+=scale*parseInt($el.css('margin-'+edge));});}
if(how.axis!='y'&&!Bounds.contain(Bounds.x(b),e)){var dx=e.left<=b.left||b.width()<=e.width()?e.left-b.left:e.right-b.right;b.left+=dx;b.right+=dx;}
if(how.axis!='x'&&!Bounds.contain(Bounds.y(b),e)){var dy=e.top<=b.top||b.height()<=e.height()?e.top-b.top:e.bottom-b.bottom;b.top+=dy;b.bottom+=dy;}}
return b;}
view=function(what,how){var stop=(what===false),start=!stop,$el,el;if(start){how||(how={});'speed'in how||(how.speed='normal');if($.browser.opera){how.speed=0}
$el=$any(what);el=$el[0];if(Bounds.empty($el)){start=false;}else if(el_q.length&&(!how.speed||!DOM_descendant(el_q[el_q.length-1],el))){stop=true;}}
if(stop){$html_body.stop(true);el_q.length=0;}
if(start){el_q.push(el);$body.queue('fx',function(){var w=new Bounds(window);how.hint&&!Bounds.empty($el)&&offset(how.hint,w,how);offset($el,w,how);$html_body.animate({scrollTop:w.top,scrollLeft:w.left},how.speed,function(){how.focus&&$el.focus();el_q.shift();$body.dequeue('fx');});});}
return $el;}
$(function(){$body=$('body');$html_body=$('html,body');});})();function more_possible(text){$('#more-experiment a').trigger('more-possible');}
function createPopup(pos_selector,titlebar,name,contents,message,onmouseout){function div(kind,html){return $('<div id="'+name+'-'+kind+'" class="popup-'+kind+'">'+(html||'')+'</div>');}
var	pos=Position(pos_selector),$popup=$('<div id="'+name+'-popup" class="popup" style="position:absolute; top:'+pos.top+'px; left:'+pos.left+'px; z-index:100">').appendTo('body').append('<iframe>').append(div('title',titlebar)).append(div('contents',contents)).append(div('message',message));TypeOf.fn(onmouseout)&&$popup.mouseleave(onmouseout);return $popup[0];}
function createPopupButtons(){return'<span class="buttons"><span>'+$.makeArray(arguments).join('</span><span>')+'</span></span>';}
function closePopup(id,refresh){$any(id).remove();if(refresh){window.location.reload();}}
function handleEnter(ev,func,arg){if(!ev){ev=window.event;}
var code=ev.which||ev.keyCode;if(code==13){func(arg);ev.returnValue=true;return true;}
ev.returnValue=false;return false;}
function adsToggle(val){var params={};params.op='enable_maker_adless';if(!val){params.off=1;}
params.reskey=reskey_static;ajax_update(params,'',{onComplete:json_handler});}
function cached_parts(expr){if(!cached_parts[expr]){cached_parts[expr]=$(expr).insertBefore('#top_parent');}
return cached_parts[expr];}
function get_modal_parts(filter){var $parts=cached_parts('#modal_cover, #modal_box');if(filter){$parts=$parts.filter(filter);}
return $parts;}
function custom_modal_box(action_name){$(document).trigger(action_name+'.modal');var	custom_fn_name='_custom_'+action_name+'_fn',$all_parts=get_modal_parts(),$dialog=$all_parts.filter('#modal_box'),dialog_elem=$dialog[0],fn=dialog_elem[custom_fn_name]||function(){$all_parts[action_name]();};fn($all_parts);dialog_elem[custom_fn_name]=undefined;$all_parts.filter('#modal_cover').click(hide_modal_box);return $all_parts;}
function show_modal_box(){return custom_modal_box('show').keyup(function(e){e.which==$.ui.keyCode.ESCAPE&&hide_modal_box();});}
function hide_modal_box(){var retainclass=" ";if($('#modal_box').hasClass('push')){retainclasses='push';}
custom_modal_box('hide').hide().attr('style','display: none;').removeClass().addClass(retainclasses).removeData('tabbed').unbind();if(document.forms.modal_prefs&&document.forms.modal_prefs.refresh_onclose&&document.forms.modal_prefs.refresh_onclose.value){document.location=document.URL;}
return false;}
function get_login_parts(){return cached_parts('#login_cover, #login_box');}
function show_login_box(){$("#embbeded_login_modal").css("display","inline");$("#embbeded_login_modal form input[name=unickname]").focus();}
function hide_login_slider(e){$("#embbeded_login_modal").css("display","none");}
function hide_login_box(){get_login_parts().hide();}
var logged_in=1;function check_logged_in(){return logged_in||(show_login_box(),0);}
function has_hose(){return firehose_exists}
function getModalPrefSub(section){var params={};params['op']='getModalPrefSub';params['section']=section;params['opened_from']=$("form#modal_prefs input[name=opened_from]").val();var handlers={onComplete:function(transport){$('#modal-p ul.menu li').removeClass('selected');json_handler(transport);}};ajax_update(params,'',handlers);}
function getModalPrefs(section,title,tabbed,params){var BUSY_FETCHING_MODAL='modal-fetch',$still_open=get_modal_parts('#modal_box:visible'),$bg;$still_open.length&&$still_open.data('tabbed')!=tabbed&&hide_modal_box();var formname=section;var this_op='display_modal';var return_to=location.toString();if(formname==='sendPasswdModal'||formname==='newUserModal'||formname==='userlogin'){this_op='getModalPrefsAnonHC';if(formname==='userlogin'){return_to=location.toString();}}else if(formname=='submit'){this_op='getModalPrefsAnon';}else if(!reskey_static){return show_login_box();}
Slash.busy(BUSY_FETCHING_MODAL,true);$bg=get_modal_parts('#modal_cover').css('opacity',0.75).show();if(formname==='sendPasswdModal'||section.match(/^fh/)||section.match(/^submit/)||section.match(/^newUserModal/)||section.match(/modcommentlog/)){if(this_op=="display_modal"){this_op="getModalPrefs";}
$any('modal_box_content').load('/ajax.pl',$.extend({op:this_op,section:section,reskey:reskey_static,tabbed:tabbed,return_to:return_to},params||null),function(response,status,transport){if(status==='success'){$any('preference_title').html(title);$('#modal_box').removeClass('no_scrollbars');var $modal=show_modal_box().data('tabbed',tabbed);tabbed&&$modal.addClass("tabbed");}else{$bg.hide();}
Slash.busy(BUSY_FETCHING_MODAL,false);});}else{var passData=$.extend({op:this_op,section:section,reskey:reskey_static,tabbed:tabbed,return_to:return_to,opened_from:return_to},params||null);var myModal=$("#modal_box_content");myModal.empty();myModal.html("<iframe class='prefiframe' src='https://"+location.hostname+"/prefs.pl?"+jQuery.param(passData)+"' frameborder=0 width='100%' onload='showCompletedPrefsModal(this);'></iframe>");return false;}}
function showCompletedPrefsModal(myFrame)
{var BUSY_FETCHING_MODAL='modal-fetch';$('#modal_box').addClass('no_scrollbars');show_modal_box();$(myFrame).height('100%');Slash.busy(BUSY_FETCHING_MODAL,false);}
function serialize_multiple($form){var elems=$form.serializeArray();var uses={};$.map(elems,function(el){++uses[el.name]||(uses[el.name]=1);});var salt=1;return $.param($.map(elems,function(el){if(uses[el.name]>1){el.name+=salt++;}
return el;}));}
function resetModalPrefs(extra_param){var params={op:'saveModalPrefs',data:serialize_multiple($any('modal_prefs')),reset:1,reskey:(document.forms.modal_prefs&&document.forms.modal_prefs.reskey&&document.forms.modal_prefs.reskey.value)||reskey_static};if(extra_param){params[extra_param]=1;}
ajax_update(params,'',{onComplete:function(){hide_modal_box();document.location=document.URL;}});}
function saveModalPrefs(formname,this_data,this_reskey){formname=formname||(document.forms.modal_prefs&&document.forms.modal_prefs.formname&&document.forms.modal_prefs.formname.value);this_data=this_data||serialize_multiple($any('modal_prefs'));this_reskey=this_reskey||(document.forms.modal_prefs&&document.forms.modal_prefs.reskey&&document.forms.modal_prefs.reskey.value)||reskey_static;var this_op='saveModalPrefs';if(formname==='sendPasswdModal'||formname==='newUserModal'){this_op='saveModalPrefsAnonHC';}
if($("form#modal_prefs input[name=op]").size()>0){document.forms.modal_prefs.op.value=this_op;}
if($("form#modal_prefs input[name='opened_from']").size()<1){ajax_update({op:this_op,data:this_data,reskey:this_reskey},'',{onComplete:function(transport){var BUSY_FETCHING_MODAL='modal-fetch';var response=eval_response(transport);json_update(response);if(response===undefined||response.html_replace===undefined){try{hide_modal_box();}catch(e){}
if(document.forms.modal_prefs.refreshable&&document.forms.modal_prefs.refreshable.value){window.location.reload();}else{Slash.busy(BUSY_FETCHING_MODAL,false);}}}});return false;}else{$("form#modal_prefs").submit();return true;}}
function modalPrefsUnsubscribeAll(){var dropdowns=$('form#modal_prefs select');for(var i=0;i<dropdowns.length;i++){if(dropdowns.eq(i).val()==0){dropdowns.eq(i).val(-1);}}
$("#modal_prefs_save_button").click();return true;}
function createacct_check_nick(this_form){var params={};params.op='createacct_check_nick';params.nickname=this_form.newusernick.value;params.reskey=this_form.nick_rkey.value;if((this_form===undefined)||(params.nickname===undefined)||(params.nickname==='')){return false;}
ajax_update(params,'',{onComplete:function(transport){var response=eval_response(transport);if(response!==undefined&&response.html_replace!==undefined){json_update(response);}}});}
function displayModalPrefHelp(id,displayType){var el=$any(id);if(!displayType){var displayType="inline";}
el.css('display',el.css('display')!='none'?'none':displayType);}
function showHof(period){var BUSY_FETCHING_MODAL='modal-fetch';Slash.busy(BUSY_FETCHING_MODAL,true);$any('modal_box_content').load('/CurrentHof.pl?view=modal&period='+period,function(){show_modal_box();Slash.busy(BUSY_FETCHING_MODAL,false);});return false;}
function openInWindow(mylink,samewin){if(!samewin&&window.open(mylink,'_blank')){return false;}
window.location=mylink;return false;}
function logout(){ajax_update({op:'logout',reskey:reskey_static},'',{onComplete:json_handler});}
function prescan_user(){var params={};params['op']='comments_precheck';ajax_update(params,'');}
jQuery(document).ready(function(){$('.commentSub .ui-icon.flag').live('click',function(fe){if($('#flag_comment').length>0){$('#flag_comment').remove();}
$(this).parent().append('<form id="flag_comment"><input type="hidden" name="comment" value="'+this.id+'"><input type="button" class="s" value="Report" disabled="disabled" onclick="reportCommentAbuse();"><input type="text" name="reason" class="t" placeholder="Specify reason" ></form>').parent().parent().addClass('flag-in');$('input[name=reason]').focus();});$(".comment").hover(function(){},function(){if($(this).hasClass('flag-in')){$('#flag_comment').remove();$(this).removeClass('flag-in');}});$("input[name=reason]").live('keypress',function(kp){var code=(kp.keyCode?kp.keyCode:kp.which);if(code==13){$(this).prev().trigger('click');kp.preventDefault();}});$("#flag_comment .t").live('keyup',function(data){if($(this).val()!=""){$("#flag_comment .s").removeAttr("disabled");}else{$("#flag_comment .s").attr("disabled","disabled");}});});function reportCommentAbuse(){ajax_update({op:'reportCommentAbuse',comment:$("#flag_comment input[name=comment]").val(),reason:$("#flag_comment input[name=reason]").val()},'',{onComplete:function(){Slash.busy('modal-fetch',false);$("#flag_comment").hide();}});return false;}
function randStory(){var params={};params.op='semiRandStory';params.reskey=reskey_static;ajax_update(params,'',{onComplete:json_handler});}
function getTopStories(days){var params={};params.op='getTopStories';params.days=days;params.reskey=reskey_static;$('.btmrel, .btmday, .btmweek, .btmmonth').removeClass('selected');if(days==1){$('.btmday').addClass('selected');}else if(days==7){$('.btmweek').addClass('selected');}else if(days==30){$('.btmmonth').addClass('selected');}
ajax_update(params,'',{onComplete:json_handler});}
function getRelatedStoriesForStory(stoid){var params={};$('.btmday, .btmweek, .btmmonth').removeClass('selected');$('.btmrel').addClass('selected');params.op='getRelatedStoriesForStory';params.stoid=stoid;params.reskey=reskey_static;ajax_update(params,'',{onComplete:json_handler});}
function trackLink(link,category,action){_gaq.push(['_trackEvent','OutboundLinks',category,action]);_gaq.push(['b._trackEvent','OutboundLinks',category,action]);setTimeout('document.location = "'+link.href+'"',100);}
function after_article_moved(article){var data=article?$(article).nextAll(':visible').andSelf():null;$any('firehoselist').trigger('articlesMoved',data);}
function before_article_removed(article,if_also_trigger_moved){var next_article=article?$(article).next(':visible')[0]:null;$any('firehoselist').trigger('beforeArticleRemoved',article);if(if_also_trigger_moved){after_article_moved(next_article);}}
function firehose_toggle_advpref(){$any('fh_advprefs').toggleClass('hide');}
function firehose_open_prefs(){$any('fh_advprefs').removeClass();}
function toggleIntro(id,toggleid){var new_class='condensed';var new_html='[+]';if($any(id).setClass(applyMap('introhide','intro')).hasClass('intro')){new_class='expanded';new_html='[-]';}
$any(toggleid).setClass(new_class).html(new_html);}
function tagsToggleStoryDiv(id,is_admin,type){if($any('toggletags-body-'+id).hasClass('tagshide')){tagsShowBody(id,is_admin,'',type);}else{tagsHideBody(id);}}
function tagsHideBody(id){$any('toggletags-body-'+id).setClass('tagshide');$any('tagbox-title-'+id).setClass('tagtitleclosed');$any('tagbox-'+id).setClass('tags');$any('toggletags-button-'+id).html('[+]');after_article_moved(elemAny('firehose-'+id));}
function tagsShowBody(id,unused,newtagspreloadtext,type){type=type||"stories";if(type=="firehose"){setFirehoseAction();if(fh_is_admin){firehose_get_admin_extras(id);}}
$any('toggletags-button-'+id).html("[-]");$any('tagbox-'+id).setClass("tags");$any('tagbox-title-'+id).setClass("tagtitleopen");$any('toggletags-body-'+id).setClass("tagbody");after_article_moved(elemAny('firehose-'+id));}
function tagsOpenAndEnter(id,tagname,unused,type){tagsShowBody(id,unused,tagname,type);}
function reportError(request){alert("error");}
function is_body_expanded(el){return $(el).closest('.fhitem').is(':has(>[id^=fhbody-]:not(.empty,.hide))');}
function toggle_fh_body_wrap_return(el){return firehose_settings.view==='stories'&&is_body_expanded(el)||toggle_firehose_body.apply(null,arguments)&&false;}
function toggle_firehose_body(el,unused,toggle_to,dont_next){setFirehoseAction();var	$fhitem=$(el).closest('.fhitem'),fhid=$fhitem.attr('id').replace(FHID_PREFIX,''),$body=$fhitem.children('[id^=fhbody-]'),body_is_empty=$body.is('.empty'),toggle_from=sign(!body_is_empty&&!$body.is('.hide')||-1);if(toggle_to===false){toggle_to=-1;}else if(typeof(toggle_to)==='string'){toggle_to={show:1,hide:-1}[toggle_to];}
toggle_to=sign(toggle_to||-toggle_from);if(toggle_to==toggle_from){return;}
var showing=toggle_to>0;if(body_is_empty){var handlers={};fh_is_admin&&(handlers.onComplete=function(){firehose_get_admin_extras(fhid);});ajax_update({op:'firehose_fetch_text',id:fhid,reskey:reskey_static},$body.attr('id'),handlers);}else if(fh_is_admin&&showing){firehose_get_admin_extras(fhid);}
$body.removeClass('body empty hide').addClass(showing?'body':'hide');$fhitem.removeClass('article briefarticle adminmode usermode').addClass((showing?'article ':'briefarticle ')+(fh_is_admin?'adminmode':'usermode'));if(showing){$fhitem.removeAttr('onclick');}
if(!dont_next&&!showing&&$fhitem.is('.currfh')){firehose_go_next();}
after_article_moved($fhitem);inlineAdFirehose(showing&&$fhitem);return false;}
toggle_firehose_body.SHOW=1;toggle_firehose_body.TOGGLE=0;toggle_firehose_body.HIDE=-1;function toggleFirehoseTagbox(id){$any('fhtagbox-'+id).setClass(applyMap('tagbox','hide'));after_article_moved(elemAny('firehose-'+id));}
function use_skin(link){var $installed_skins=$('head link.data-skin').attr('disabled',true),$link,$new_skin;if(link){$link=$(link);$new_skin=$installed_skins.filter('[title='+$link.attr('title')+']');if(!$new_skin.length){$new_skin=$link.addClass('data-skin').
attr('disabled',true).
appendTo('head');}
$new_skin.attr('disabled',false);}}
function firehose_style_switch(section_id){var	$item=firehose_section_menu_item(section_id),section=$item.length&&$item.metadata();if('skin'in section||section_id==='unsaved'){use_skin(section.skin);return;}
ajax_update({op:'firehose_section_css',reskey:reskey_static,layout:'yui',section:section_id},'',{onComplete:function(xhr){var json=eval_response(xhr)||{};use_skin(json.css_includes);section&&(section.skin=json.css_includes);}});}
var sprite_rules,use_sprites;(function(){function need_rule($expr){return($expr.css('background-image')||'none')==='none';}
sprite_rules=function(rules){var $test=$('<div style="display:none">').appendTo('body');(rules=slashcore.grep(rules,function(classAttr){return need_rule($test.attr('className',classAttr));}).join('\n'))&&$('<style type="text/css">'+rules+'</style>').appendTo('head');$test.remove();};use_sprites=function(root){$('div.maybe-sprite',root).each(function(){var $div=$(this).removeClass('maybe-sprite');$div.children('span.no-sprite').each(function(){need_rule($div)&&$div.attr('style',$(this).text());}).remove();});};})();(function(){var ALL_VIEWS='stories-view recent-view popular-view daddypants-view search-view userhomepage-view';function reflect(v){$('body').removeClass(ALL_VIEWS).addClass(v+'-view');}
$(function(){reflect(firehose_settings.view);});$(document).bind('firehose-setting-view',function(e,view){reflect(view);});})();(function(){var	NEXT_ID=1,NONE={id:-Infinity,rank:-Infinity,content:''},DISPLAYED=NONE,AVAILABLE={},$DISPLAY,HINT_RE=/-(mode|warning|error)$/,RANK={mode:2,warning:3,error:4},CLASS_FOR_RANK=['banner-rank','message-rank','mode-rank','warning-rank','error-rank'],RANK_CLASSES=CLASS_FOR_RANK.join(' ');function Message(o){var id=NEXT_ID++,hint;(TypeOf.scalar(o)||o.content===void(0))&&(o={content:o});hint=(HINT_RE.exec(o.key)||{})[1];$.extend(this,{rank:RANK[hint]||1,key:id,content:''},o,{id:id});return AVAILABLE[this.key]=this;}
function display(){var best=NONE;slashcore.each(AVAILABLE,function(){(this.rank-best.rank||this.id-best.id)>0&&(best=this);});if(best!==DISPLAYED){$DISPLAY.html((DISPLAYED=best).content);$('#firehose-message-tray').removeClass(RANK_CLASSES).addClass(CLASS_FOR_RANK[best.rank]);}}
Slash.message=function(o){$DISPLAY||(DISPLAYED=new Message({rank:0,key:'default',content:($DISPLAY=$('#firehose-message-tray')).show().children()}));return o&&(o=new Message(o))&&(display(),o.key);};Slash.clear_message=function(key){try{delete AVAILABLE[key]&&display();}catch(e){}};Slash.has_message=function(key){return!!AVAILABLE[key];};})();function addfhfilter(text){if(has_hose()){firehose_set_options('addfhfilter',text);return false;}
return true;}
function setfhfilter(text){if(has_hose()){firehose_set_options('setfhfilter',text);return false;}
return true;}
var firehose_set_options;(function(){var	loading_msg={key:'loading',content:'<span class="loading_msg">Loading New Items...</span>'},removes_all=Qw.as_set('firehose_usermode mixedmode mode nocolors nothumbs section setfhfilter setsearchfilter tab view startdate issue'),start_over=$.extend(Qw.as_set('startdate color addfhfilter'),removes_all),uses_setfield=Qw.as_set('mixedmode nobylines nocolors nocommentcnt nodates nomarquee noslashboxes nothumbs'),sets_param=$.extend(Qw.as_set('color duration issue pagesize pause startdate tab tabtype usermode'),uses_setfield),flags_param={fhfilter:'filterchanged',more_num:'ask_more',section:'sectionchanged',addfhfilter:'filterchanged',setfhfilter:'filterchanged',setsearchfilter:'searchtriggered',tab:'tabchanged',usermode:'setusermode',view:'viewchanged'},sets_directly=Qw.as_set('color duration issue mode orderby orderdir section startdate tab view'),sets_indirectly={setfhfilter:'fhfilter',setsearchfilter:'fhfilter',tabsection:'section'},resets_pagemore=Qw.as_set('fhfilter view tab issue pagesize section setfhfilter setsearchfilter'),update_handlers={onComplete:function(transport){json_handler(transport);firehose_get_updates({oneupdate:1});fh_pag_update();}};var $fhl=$([]);$(function(){$fhl=$any('firehoselist');});function set_fhfilter_from(expr){$(expr).each(function(){firehose_settings.fhfilter=this.value;});}
function add_to_fhfilter(text){var seen={};var finaltext=$.map($.trim((firehose_settings.fhfilter||'')+' '+text).split(ws),function(term){if(!(term in seen)){return seen[term]=term;}}).join(' ')
firehose_settings.fhfilter=finaltext;set_filter_inputs(finaltext);}
function set_filter_inputs(text){$('form[name=firehoseform] input[name=fhfilter], #searchquery').each(function(){this.value=text;});}
firehose_set_options=function(name,value,context){if(name==='color'&&!value){return;}
if(!logged_in&&name=="color"){show_login_box();return;}
typeof(value)==='boolean'&&(value=sign(value));var params={};if(name==='setfhfilter searchfu'){name='setfhfilter';params.searchtriggered=1;}
uses_setfield[name]&&(params.setfield=1);sets_param[name]&&(params[name]=value);flags_param[name]&&(params[flags_param[name]]=1);sets_directly[name]&&(firehose_settings[name]=value);sets_indirectly[name]&&(firehose_settings[sets_indirectly[name]]=value);resets_pagemore[name]&&(firehose_settings.page=firehose_settings.more_num=0);switch(name){case'fhfilter':set_fhfilter_from('form[name=firehoseform] input[name=fhfilter]');break;case'issue':firehose_settings.startdate=value;firehose_settings.duration=1;break;case'mode':fh_view_mode=value;break;case'tabsection':params.tabtype='tabsection';break;case'view':set_fhfilter_from('#searchquery');break;case'addfhfilter':add_to_fhfilter(value);break;}
if(start_over[name]){view($('body'),{speed:0});params.start_over=1;}
if(removes_all[name]){$('div.paginate').hide().addClass('paginatehidden');$fhl.fadeOut().html('');$('#itemsreturned').html('');}
ajax_update($.extend({op:'firehose_set_options',reskey:reskey_static,setting_name:name,context:context,section:firehose_settings.section},params,firehose_settings),'',update_handlers);$(document).trigger('firehose-setting-'+name,value);};})();function firehose_fix_up_down(id,new_state){var $updown=$any('updown-'+id);if($updown.length&&!$updown.hasClass(new_state)){$updown.setClass(new_state);}}
(function(){var CLASS_FOR_VOTE={nod:'votedup',metanod:'votedup',nix:'voteddown',metanix:'voteddown',none:'vote'};$(document).bind('vote-assigned',function(event,vote){firehose_fix_up_down($(event.target).attr('id').replace(FHID_PREFIX,''),CLASS_FOR_VOTE[vote||'none']);});})();function firehose_click_nodnix_reason(event){var	$fhitem=$(event.target).closest('.fhitem'),fhid=$fhitem.attr('id').replace(FHID_PREFIX,'');if((fh_is_admin||firehose_settings.metamod)&&($any('updown-'+fhid).is('.voteddown')||$fhitem.is('.fhitem-comment'))){firehose_collapse_entry(fhid);}
return true;}
function firehose_remove_tab(tabid){setFirehoseAction();ajax_update({op:'firehose_remove_tab',tabid:tabid,reskey:reskey_static,section:firehose_settings.section},'',{onComplete:json_handler});}
var $related_trigger=$().filter();var kExpanded=true,kCollapsed=false;function tag_ui_in($fhitem){var $toolbar=$fhitem.find('menu.edit-bar'),$twisty=$toolbar.find('a.edit-toggle span.button');return{$toolbar:$toolbar,$input:$toolbar.find('input.tag-entry'),$toggle:$twisty,is_expanded:$twisty.is('.expand')};}
function firehose_toggle_tag_ui_to(want_expanded,el,dont_next){var	$fhitem=$(el).closest('.fhitem'),fhid=$fhitem.attr('id').replace(FHID_PREFIX,''),tag_ui=tag_ui_in($fhitem),toggle=tag_ui.is_expanded==!want_expanded;if(toggle){if(want_expanded){if($fhitem.find('div[id^=fhbody-]').is('.empty,.hide')){toggle_firehose_body($fhitem,0,true,dont_next);$fhitem.data('tags-opened-body',true);}}
setFirehoseAction();want_expanded&&Tags.fetch($fhitem[0]);tag_ui.$toolbar.toggleClass('expanded',!!want_expanded);tag_ui.$input.toggle(!!want_expanded);tag_ui.$toggle.setClass(applyToggle({expand:want_expanded,collapse:!want_expanded}));$fhitem.find('#toggletags-body-'+fhid).setClass(applyToggle({tagbody:want_expanded,tagshide:!want_expanded}));if(!want_expanded&&$fhitem.data('tags-opened-body')){toggle_firehose_body($fhitem,0,false);$fhitem.removeData('tags-opened-body');}
after_article_moved($fhitem[0]);}
want_expanded&&view(tag_ui.$input,{hint:$fhitem,focus:true,speed:50});return tag_ui.$toolbar;}
function firehose_toggle_tag_ui(el){var $fhitem=$(el).closest('.fhitem');firehose_toggle_tag_ui_to(!tag_ui_in($fhitem).is_expanded,$fhitem);}
var search_eligible;(function(){var context_search_eligible={user:true,top:true,system:true};search_eligible=function(tag_el){var $li=$(tag_el).closest('li'),context=$li.closest('span.tag-display').attr('context')||'unknown';return context_search_eligible[context]&&$li.is(':not(.p,.w,.b,.suggestion)');};})();function user_intent(intent,data){intent&&$(document).trigger('user-intent-'+intent,data);}
function shift_select(el){var	$fhitems=$('article.fhitem:visible'),id=$(el).closest('.fhitem').attr('id'),$bounds=$('article.currfh,#'+id),start_idx=$fhitems.index($bounds[0]),stop_idx=$fhitems.index($bounds[$bounds.length-1]);$fhitems.slice(start_idx,stop_idx+1).addClass('currfh');}
function apply_tags(item,tags,shift_key){var $target=$(item);fh_is_admin&&$target.is('.currfh')&&shift_key&&($target=$('article.fhitem.currfh:visible'));$target.each(function(){Tags.submit(this,tags);});}
$(function(){var PREFIX=/^user-intent-/;function interest(event,item,original_event){if(!item){}else if(!fh_is_admin||!original_event||!original_event.shiftKey){firehose_set_cur($(item),(event.type||'').replace(PREFIX,''));}else{shift_select(item);}
setTimeout(function(){inlineAdFirehose();},0);}
$(document).bind('user-intent-interest',interest).bind('user-intent-control',interest);});function page_click_handler(event){var	$target=$(event.target),$fhitem=$target.closest('.fhitem'),leaving=!!$target.closest('a[href]:not([href=#],[onclick],[rel=tag]),.advertisement').length,control=!leaving&&!!$target.closest('menu,a,[data-intent-control]').length,intent=!leaving&&(control?'control':'interest'),command,click_handled=false;$related_trigger=$target;if($target.is('a.up')){command='nod';}else if($target.is('a.down')){command='nix';}else if($target.is('.sodify,.skin,.topic *')){intent='search';}else{$related_trigger=$([]);}
if(leaving){user_intent(intent);}else{user_intent(intent,[$fhitem[0],event]);setTimeout(function(){inlineAdFirehose();},0);}
if(command&&(click_handled=true)&&check_logged_in()){apply_tags($fhitem[0],command,event.shiftKey);}
return!click_handled;}
function firehose_handle_nodnix(commands){if(commands.length){var fhitem=this;$.each(commands.slice(0).reverse(),function(i,cmd){if(cmd=='nod'||cmd=='nix'){firehose_fix_up_down(fhitem.getAttribute('data-fhid'),{nod:'votedup',nix:'voteddown'}[cmd]);return false;}});}
return commands;}
function firehose_handle_comment_nodnix(commands){if(commands.length){var fhitem=this,handled_underlying=false;commands=$.map(commands.reverse(),function(cmd){var match=/^([\-!]*)(nod|nix)$/.exec(cmd);if(match){var modifier=match[1],vote=match[2];cmd=modifier+'meta'+vote;if(!handled_underlying&&!modifier){var id=fhitem.getAttribute('data-fhid');firehose_fix_up_down(id,{nod:'votedup',nix:'voteddown'}[vote]);firehose_collapse_entry(id);handled_underlying=true;}}
return cmd;}).reverse();}
return commands;}
$(function(){var $FHL=$any('firehoselist');$.browser.chrome=$.browser.safari&&/chrome/.test(navigator.userAgent.toLowerCase());$('#fhsearch').show();if(!firehose_smallscreen){var $roots=$('div.fhroot');($roots.length?$roots:$('div.article')).click(page_click_handler);}
$('#firehoselist > article.fhitem').live('blur-article',function(){var $fhitem=$(this);if($fhitem.data('blur-closes-item')){toggle_firehose_body($fhitem,0,false,true);}else if($fhitem.data('blur-closes-tags')){firehose_toggle_tag_ui_to(false,$fhitem,true);}
$fhitem.removeData('blur-closes-item').removeData('blur-closes-tags');}).live('focus-article',function(){var $fhitem=$(this);$fhitem.data('blur-closes-tags',!tag_ui_in($fhitem).is_expanded).data('blur-closes-item',$fhitem.find('[id^=fhbody-]').is('.empty,.hide'));});});var update_firehose_content;(function(){var	MARK_ADDING='data-add-ready',MARK_REMOVING='data-remove-ready',MAX_OFFSCREEN_CHUNK=5,CHANGES_RE=/\bdata-(add|remove)-ready\b/;var	D=document,U=void(0),$FHL,FHL;$(function(){$FHL=$any('firehoselist');FHL=$FHL[0];});function Run(){return this;}
Run.prototype={head:function(){return this._run[0];},lhead:function(){return this.head();},headId:function(){return(this.lhead()||{}).id;},tail:function(){var len=this._run&&this._run.length;return len&&this._run[len-1];},ltail:function(){return this.tail();},tailId:function(){return(this.ltail()||{}).id;},_manip:function(parent,next){var el=next;while(el&&el.nodeType!==1){el=el.nextSibling;}
if(!el){this.appendTo(parent);}else if(el!==this.head()){this.insertBefore(next);}
return this;},prependTo:function(parent){return this._manip(parent,parent.firstChild);},insertBefore:function(next){this.tail().nextSibling!==next&&$(this._run).insertBefore(next);return this;},insertAfter:function(prev){return this._manip(prev.parentNode,prev.nextSibling);},appendTo:function(parent){var tail=this.tail();(tail.parentNode!==parent||tail.nextSibling)&&$(this._run).appendTo(parent);return this;}};function DocumentRun(){this._run=[];return this;};DocumentRun.prototype=$.extend(new Run,{lhead:function(){return this._lhead;},ltail:function(){return this._ltail;},push:function(el,logical){this._run.push(el);if(logical){this._lhead||(this._lhead=el);this._ltail=el;}
return this;}});function DocumentFragmentRun(){this._fragment=D.createDocumentFragment();this._run=this._fragment.childNodes;return this;}
DocumentFragmentRun.prototype=$.extend(new Run,{push:function(el){this._fragment.appendChild(el);return this;},insertBefore:function(next){next.parentNode.insertBefore(this._fragment,next);},insertLast:function(parent){parent.appendChild(this._fragment);}});function insert_runs_after(prev_run,runs){var next_run,tail_id,after_el;while(prev_run&&(tail_id=prev_run.tailId())&&(next_run=runs[tail_id])){next_run.insertAfter(prev_run.tail())
prev_run=next_run;}}
function prepare(html){return $(html).addClass(MARK_ADDING).css('display','none')[0];}
update_firehose_content=function(updates,sequence){if(!(updates&&updates.length||sequence&&sequence.length)){return;}
Slash.busy('firehose-content',true);var adding={};var removing_sx=$.map(updates,function(update){var op=update[0],fhid=update[1],html=update[2];switch(!!fhid&&op){case'remove':return'#firehose-'+fhid;case'add':adding[fhid]=prepare(html);}}).join(',');$(removing_sx,FHL).addClass(MARK_REMOVING);var loose_runs={},run,elid_before={},prev_elid=0;$.each(sequence,function(i,fhid){var item=adding[fhid];if(item){run||(run=loose_runs[prev_elid]=new DocumentFragmentRun());run.push(item);}else{run=U;}
var elid='firehose-'+fhid;elid_before[elid]=prev_elid;prev_elid=elid;});var	i=0,$fhitems=$FHL.children(),el=$fhitems[i],sequence_known=el&&el.id in elid_before,i2ad_pos=$fhitems.index($('#floating-slashbox-ad',FHL)),fixed_run;i2ad_pos<0&&(i2ad_pos=Infinity);while(el){run=new DocumentRun();prev_elid=U;do{sequence_known&&(prev_elid=el.id);run.push(el,sequence_known);}while((el=$fhitems[++i])&&(!(sequence_known=el.id in elid_before)||prev_elid===U||elid_before[el.id]===prev_elid));if(i>i2ad_pos){fixed_run=run;i2ad_pos=Infinity;}else{loose_runs[elid_before[run.headId()]]=run;}}
(run=loose_runs[0])&&insert_runs_after(run.prependTo(FHL),loose_runs);fixed_run&&insert_runs_after(fixed_run,loose_runs);use_sprites(FHL);Slash.busy('firehose-content',false);}})();(function(){var INIT_INTERVAL=400,$fhroots,timer;function step(){var $items=firehose_init_note_flags(1);if(!$item.length){clearInterval(timer);timer=0;}}
fh_is_admin&&$(document).bind('firehose-content-end',function(){timer||(timer=setInterval(step,INIT_INTERVAL));});$(function(){$fhroots=$('div.fhroot');});})();function user_wants_updates(when){setFirehoseAction();apply_updates();Slash.clear_message('updates-available');when&&check_logged_in()&&apply_updates_when(when);}
var apply_updates_when,apply_updates,updates_available,debug_ask,slashCMfn;(function(){var	$FHL,INSERT_SX='article.data-add-ready',REMOVE_SX='article.data-remove-ready:not(.currfh)',NEW_SX='article.fhitem.data-add-ready:hidden',OLD_SX='article.fhitem:not(.data-add-ready,.data-remove-ready:not(.currfh)):last',APPLY_WHEN='at-end',NUMBERS=['No','A','Two','Three','Four','Five','Six','Seven','Eight','Nine'];apply_updates_when=function(when,init){APPLY_WHEN=when;init||ajax_update({op:'firehose_save_autoupdate',reskey:reskey_static,autoupdate:when});};apply_updates=function($insert,$remove){$insert!==false&&(TypeOf.list($insert)?$insert.filter(INSERT_SX):$FHL.children(INSERT_SX)).removeClass('data-add-ready').css('display','');$remove!==false&&(TypeOf.list($remove)?$remove.filter(REMOVE_SX):$FHL.children(REMOVE_SX)).remove();firehose_future&&firehose_update_title_count(firehose_storyfuture(firehose_future).filter(':visible').length);};function ask(about){var	$items=TypeOf.list(about)?about:$([]),n=$items.length||about/1,how,kind;if(n){how=$FHL.children(NEW_SX+':last').prevAll('article.fhitem:not(.data-add-ready)').length?[' more ',' available.']:[' new ',' ready.'];kind=$items.is(':not(.fhitem-story)')?['item is','items are']:['story is ','stories are'];Slash.message({key:'updates-available',content:'<p>'+
'<a href="#" onclick="user_wants_updates(); return false" title="update now">'+
(NUMBERS[n]||n)+how[0]+kind[(n!==1)/1]+how[1]+
'</a>'+
'  '+
'<a href="#" onclick="user_wants_updates(\'always\'); return false" title="always update, never ask">'+
'(Automatically Update)'+
'</a>'+
'</p>'});}}
updates_available=function(n){var $last,$tail=true,$remaining;APPLY_WHEN==='at-end'&&($last=$FHL.children(OLD_SX)).length&&($tail=$last.nextAll());APPLY_WHEN!=='never'&&apply_updates($tail,$tail);($remaining=$FHL.children(NEW_SX)).length&&ask($remaining);};$(function(){$FHL=$any('firehoselist');});})();function firehose_handle_update(updates,sequence){var	saved_selection=new $.TextSelection(gFocusedText),$menu=$('div.ac_results:visible'),$fhl=$any('firehoselist'),add_behind_scenes=Slash.has_message('loading');add_behind_scenes&&$fhl.hide();update_firehose_content(updates,sequence);if(add_behind_scenes){apply_updates();$fhl.show().css({opacity:''});$('div.paginate').show().removeClass('paginatehidden');Slash.clear_message('loading');}else{updates_available();}
Slash.busy('firehose-update',false);fh_pag_update();firehose_add_update_timerid(setTimeout(firehose_get_updates,getFirehoseUpdateInterval()));saved_selection.restore().focus();$menu.show();}
function firehose_storyfuture(future){var if_not=['h2.future','h2.story'],class_if=['story','future'];return $('div.fhroot>article.fhitem').each(function(){var is_future=sign(future[this.id.replace(FHID_PREFIX,'')]);$(this).find(if_not[is_future]).attr('className',class_if[is_future]);});}
function firehose_update_title_count(num){var newtitle;var end;var sectionname="";if(firehose_settings.sectionname!="Main"){sectionname=" "+firehose_settings.sectionname;}
if(!num){num=$('div.fhroot>article.fhitem').length;}
if(num>0){end=" ("+num+")";}else{end=" "+firehose_slogan;}
if(firehose_settings.viewtitle=='Stories'){newtitle=firehose_sitename+": "+firehose_slogan;}else if(firehose_settings.viewtitle=='Daddypants'){newtitle=firehose_sitename+sectionname+" "+firehose_settings.viewtitle+end;}else if(firehose_settings.viewtitle=='Recent'||firehose_settings.viewtitle=='Popular'){if(num){newtitle=num+" ";}
newtitle=newtitle+firehose_settings.viewtitle+" "+firehose_sitename+" "+sectionname+" Submissions";}else{newtitle=firehose_sitename+sectionname+" "+firehose_settings.viewtitle+end;}
document.title=newtitle;}
(function(){var $D=$(document),$B,depth={},ROOT_RE=/^[^-]+(?=-)/,EVENT=['-end','-begin'];var $IB={};$IB["ajax"]=false;$IB["firehose-ajax"]=false;$IB["firehose"]=false;$IB["firehose-update"]=false;function busy(k,more,for_root){var N=depth[k]||0,was_busy=N>0,now_busy;if(arguments.length>1){if(TypeOf.number(more)==='number'){more===0&&(more=-N);}else{more=sign(more)||-1;}
(N+=more)?depth[k]=N:delete depth[k];now_busy=N>0;Slash.markBusy(k,now_busy);!for_root&&was_busy!==now_busy&&$D.trigger(k+EVENT[sign(now_busy)]);}
return was_busy;}
Slash.busy=function(k,more){var was_busy=busy.apply(null,arguments),m;arguments.length>1&&(m=ROOT_RE.exec(k))&&busy(m[0],more,true);return was_busy;};Slash.markBusy=function(k,state){var was_busy=$IB[k];var now_busy=!!state||arguments.length<2&&depth[k]>0;if(was_busy!==now_busy){$("div.busy.genericspinner").toggle();$IB[k]=!$IB[k];}
return was_busy;};})();$(function(){$(document).ajaxStart(function(){Slash.markBusy('ajax',true);}).ajaxStop(function(){Slash.markBusy('ajax',false);});});function dynamic_blocks_list(){var boxes=$('#slashboxes article header').map(function(){return this.id.slice(0,-6);}).get().join(',');return boxes;}
function dynamic_blocks_update(blocks){$.each(blocks,function(k,v){if(k==='userbio_self'){$('#'+k).html(v.block);}else{$('#'+k+'-title h2').replaceWith('<h2>'+(v.url?'<a href="'+v.url+'">'+v.title+'</a>':v.title)+'</h2>');v.block&&$any(k+'-content').html(v.block);}});}
function dynamic_blocks_delete_message(val,type){var params={};params.op='dynamic_blocks_delete_message';params.val=val;params.reskey=reskey_static;if(type==='user_bio_messages'){params.user_bio_messages=1;params.strip_list=1;}
ajax_update(params,'',{onComplete:function(transport){var response=eval_response(transport);var block_content='';if(response!=undefined){block_content=response.block;}
$('#userbio_self-messages').html(block_content);if((block_content==='')||(response===undefined)){$('#userbio_self-messages-begin').hide();}}});}
function firehose_toggle_picker_search(){var params={};params.op='firehose_toggle_picker_search';params.reskey=reskey_static;ajax_update(params,'',{onComplete:function(){$('#fh_filtercontrol_toggle').hide();$('#fh_picker_search').show();$('#hd').removeClass('nofilter');$('#fh_simpledesign_toggle').show();}});}
function firehose_toggle_smallscreen_mode(force_ss,is_anon){if(force_ss){var uri=document.location.search;var base=document.location.href.replace(/\?.*/,'');if(is_anon==1){if(uri.match("ss=1")){uri=uri.replace(/ss=1/,"ss=0");}else{if(uri.match(/\?/)){uri=uri+'&ss=0';}else{uri='?ss=0';}}}else{uri=uri.replace(/\&?ss=1/,"");if(uri==='?'){uri='';}
uri=uri.replace(/^\?\&/,'?');}
document.location=(base+uri);}else{var params={};params.op='firehose_toggle_smallscreen_mode';params.reskey=reskey_static;ajax_update(params,'',{onComplete:function(){document.location=document.URL;}});}}
function firehose_toggle_simpledesign_mode(force_sd,is_anon){if(force_sd){var uri=document.location.search;var base=document.location.href.replace(/\?.*/,'');if(is_anon==1){if(uri.match("sd=1")){uri=uri.replace(/sd=1/,"sd=0");}else{if(uri.match(/\?/)){uri=uri+'&sd=0';}else{uri='?sd=0';}}}else{uri=uri.replace(/\&?sd=1/,"");if(uri==='?'){uri='';}
uri=uri.replace(/^\?\&/,'?');}
document.location=(base+uri);}else{var params={};params.op='firehose_toggle_simpledesign_mode';params.reskey=reskey_static;ajax_update(params,'',{onComplete:function(){document.location=document.URL;}});}}
function setSlashCMCallback(callback){slashCMfn=callback;}
function slashCM(params){if(slashCMfn){slashCMfn(params);}}
function firehose_get_updates_handler(transport){var response=eval_response(transport);if(!response){return;}
firehose_future=response.future;var updated_tags=response.update_data.updated_tags;updated_tags&&$.each(updated_tags,function(id,content){$('#tagbar-'+id).html(content);});response.dynamic_blocks&&dynamic_blocks_update(response.dynamic_blocks);response.html&&json_update(response);response.sprite_rules&&sprite_rules(response.sprite_rules);response.updates&&firehose_handle_update(response.updates,response.ordered);if(firehose_settings.updateTypeCM){slashCM({updateType:firehose_settings.updateTypeCM,updateTerms:(firehose_settings.updateTermsCM||''),updateNum:(response.count||0)});firehose_settings.updateTypeCM=firehose_settings.updateTermsCM='';}}
function firehose_get_item_idstring(){return $('#firehoselist > [id]').map(function(){return this.id.replace(/firehose-(\S+)/,'$1');}).get().join(',');}
function firehose_get_updates(options){options=options||{};if((fh_play===0&&!options.oneupdate)||Slash.busy('firehose-ajax')){firehose_add_update_timerid(setTimeout(firehose_get_updates,2000));return;}
if(fh_update_timerids.length>0){var id=0;while((id=fh_update_timerids.pop())){clearTimeout(id);}}
Slash.busy('firehose-update',true);Slash.busy('firehose-ajax',true);ajax_update($.extend({op:'firehose_get_updates',ids:firehose_get_item_idstring(),updatetime:update_time,fh_pageval:firehose_settings.pageval,embed:firehose_settings.is_embedded,dynamic_blocks:dynamic_blocks_list()},firehose_settings),'',{onComplete:function(transport){Slash.busy('firehose-ajax',false);firehose_get_updates_handler(transport);$any("#firehoselist").show();},onError:firehose_updates_error_handler});}
function firehose_updates_error_handler(xhr,status){Slash.busy('firehose-update',false);$('.daddypants-view #firehose-message-tray').show();fh_is_admin&&Slash.message({key:'update-error',content:'<p><a href="#" onclick="firehose_reinit_updates()">[Admin] Slashdot update failed'
+(status&&status!=='error'?' ("'+status+'")':'')
+'.  Click to retry.</a></p>'});}
function setFirehoseAction(){var thedate=new Date();var newtime=thedate.getTime();firehose_action_time=newtime;if(fh_is_timed_out){fh_is_timed_out=0;firehose_play();firehose_get_updates();if(console_updating){console_update(1,0);}}}
function getSecsSinceLastFirehoseAction(){var thedate=new Date();var newtime=thedate.getTime();var diff=(newtime-firehose_action_time)/1000;return diff;}
function getFirehoseUpdateInterval(){var update_speed=1;if(firehose_settings.view=="daddypants"||firehose_settings.view=="recent"){update_speed=2;}
var interval=update_speed==2?45000:1200000;if(updateIntervalType==1){interval=update_speed==2?30000:800000;}
if(update_speed==2){interval=interval+(5*interval*getSecsSinceLastFirehoseAction()/inactivity_timeout);if(getSecsSinceLastFirehoseAction()>inactivity_timeout){interval=3600000;}}else{interval=1200000;if(getSecsSinceLastFirehoseAction()>7200){interval=1800000;}else if(getSecsSinceLastFirehoseAction()>10800){interval=3600000;}}
return interval;}
function start_up_hose(){firehose_set_options('pause',false);}
function firehose_play(context){fh_play=1;var wait=0;if(context&&context=="init"){wait=getFirehoseUpdateInterval();}
setFirehoseAction();if(context&&context=="init"){setTimeout(start_up_hose,wait);}else{firehose_set_options('pause',false,context);}
$any('message_area').html('');$any('pauseorplay').html('Updated');$any('play').setClass('hide');$any('pause').setClass('show');}
function firehose_pause(context){fh_play=0;$any('pause').setClass('hide');$any('play').setClass('show');$any('pauseorplay').html('Paused');firehose_set_options('pause',true,context);}
function firehose_add_update_timerid(timerid){fh_update_timerids.push(timerid);}
function firehose_collapse_entry(id){$('#firehoselist > #firehose-'+id).find('#fhbody-'+id+'.body').setClass('hide').end().removeClass('article').addClass('briefarticle');tagsHideBody(id);}
function firehose_remove_entry(id){$('#firehose-'+id).animate({height:0,opacity:0},500,function(){after_article_moved(this);$(this).remove();});}
var firehose_cal_select_handler=function(type,args,obj){var selected=args[0];firehose_settings.issue='';firehose_set_options('startdate',selected.startdate);firehose_set_options('duration',selected.duration);};function firehose_swatch_color(){}
function firehose_change_section_anon(section){window.location.href=window.location.protocol+"//"+window.location.host+"/firehose.pl?section="+encodeURIComponent(section)+"&tabtype=tabsection";}
function pausePopVendorStory(id){vendor_popup_id=id;closePopup('vendorStory-26-popup');vendor_popup_timerids[id]=setTimeout(vendorStoryPopup,500);}
function clearVendorPopupTimers(){clearTimeout(vendor_popup_timerids[26]);}
function vendorStoryPopup(){id=vendor_popup_id;var title="<a href='//intel.vendors.slashdot.org' onclick=\"javascript:pageTracker._trackPageview('/vendor_intel-popup/intel_popup_title');\">Intel's Opinion Center</a>";var buttons=createPopupButtons("<a href=\"#\" onclick=\"closePopup('vendorStory-"+id+"-popup')\">[X]</a>");title=title+buttons;var closepopup=function(e){if(!e){e=window.event;}
var relTarg=e.relatedTarget||e.toElement;if(relTarg&&relTarg.id=="vendorStory-26-popup"){closePopup("vendorStory-26-popup");}};createPopup('sponsorlinks',title,"vendorStory-"+id,"Loading","",closepopup);var params={};params.op='getTopVendorStory';params.skid=id;ajax_update(params,"vendorStory-"+id+"-contents");}
function pausePopVendorStory2(id){vendor_popup_id=id;closePopup('vendorStory-26-popup');vendor_popup_timerids[id]=setTimeout(vendorStoryPopup2,500);}
function vendorStoryPopup2(){id=vendor_popup_id;var title="<a href='//intel.vendors.slashdot.org' onclick=\"javascript:pageTracker._trackPageview('/vendor_intel-popup/intel_popup_title');\">Intel's Opinion Center</a>";var buttons=createPopupButtons("<a href=\"#\" onclick=\"closePopup('vendorStory-"+id+"-popup')\">[X]</a>");title=title+buttons;var closepopup=function(e){if(!e){e=window.event;}
var relTarg=e.relatedTarget||e.toElement;if(relTarg&&relTarg.id=="vendorStory-26-popup"){closePopup("vendorStory-26-popup");}};createPopup('sponsorlinks',title,"vendorStory-"+id,"Loading","",closepopup);var params={};params.op='getTopVendorStory';params.skid=id;ajax_update(params,"vendorStory-"+id+"-contents");}
function logToDiv(id,message){$any(id).append(message+'<br>');}
function firehose_open_tab(id){$any('tab-form-'+id).removeClass();$any('tab-input-'+id).focus();$any('tab-text-'+id).setClass('hide');}
function firehose_save_tab(id){var	$tab=$any('fhtab-'+id),new_name=$tab.find('#tab-input-'+id).val(),$title=$tab.find('#tab-text-'+id),$saved=$title.children().remove();$title.text(new_name).append($saved);ajax_update({op:'firehose_save_tab',tabname:new_name,section:firehose_settings.section,tabid:id},'',{onComplete:json_handler});$tab.find('#tab-form-'+id).setClass('hide');$title.removeClass();}
function firehose_get_media_popup(id){$any('preference_title').html('Media');$('#modal_box').removeClass('no_scrollbars');show_modal_box();$any('modal_box_content').html("<h4>Loading...</h4><img src='//a.fsdn.com/sd/spinner_large.gif'>");ajax_update({op:'firehose_get_media',id:id},'modal_box_content');}
function firehose_reinit_updates(){firehose_add_update_timerid(setTimeout(firehose_get_updates,5000));Slash.clear_message('update-error');$('.daddypants-view #firehose-message-tray').hide();}
function show_submit_box(id,type){var params={};if(id){params['from_id']=id;}
if(type){params['type']=type;}
getModalPrefs('submit','Submit',0,params);}
function show_submit_box_after(from_id,type){$('body').addClass('inline_editor_active')
$('#editor').remove();var params={op:'edit_submit_box_after',reskey:reskey_static};if(from_id){params['from_id']=from_id;}
if(type){params['type']=type;}
$('#firehose-'+from_id).fadeTo("slow",0.5);ajax_update(params,'',{onComplete:json_handler});}
function close_inline_editor(){$('.edithidden').show().removeClass('edithidden').fadeTo('fast',1);$('#editor').hide('slow').remove();}
function edit_editon(id,type,label){$('#firehose-'+id).hide();$('.editonly').removeClass('hide');$('.previewonly').addClass('hide');$('#editor').removeClass('step2').addClass('step1');Tags.fetch($('#editor'));$('#extra-warnings').addClass('hide');if(type!==""&&type!==undefined&&label!==""&&label!==undefined){$(type+"[name='"+label+"']").focus();}}
function editPreview(save){$('#edit-busy').toggle();$("form#slashstoryform .default").attr('value','');var elems=$('#slashstoryform').serializeArray();var params={};var multi={};$.map(elems,function(el){if(multi[el.name]===undefined){multi[el.name]=0;}else{multi[el.name]++;}});$.map(elems,function(el){if(multi[el.name]){if(!params[el.name])
params[el.name]=[];params[el.name].push(el.value);}else{params[el.name]=el.value;}});if(save){var d=new Date;params['submit_time']=d.getTime();}
params['op']=save?'edit_save':'edit_preview';ajax_update(params,'',{onComplete:json_handler});}
function editSave(){editPreview(1);}
function submit_reset(id,state,type){$('#edit-busy').toggle();var params={'op':'edit_reset','new':1};if(id&&state=='inline'){params['from_id']=id;}
if(state){params['state']=state;}
if(type){params['type']=type;}
ajax_update(params,'',{onComplete:json_handler});}
function submit_cancel(){$('body').removeClass('inline_editor_active');$('#edit-busy').toggle();try{hide_modal_box();}catch(e){}
close_inline_editor();}
function toggle_filter_prefs(){$('#filter_play_status, #filter_prefs').toggleClass('hide');}
function firehose_get_cur(){return $('#firehoselist > article.fhitem.currfh');}
function firehose_get_first(){return $('#firehoselist > article.fhitem:first');}
function firehose_set_cur($new_current,intent){if(!$new_current||!$new_current.length)
$new_current=firehose_get_first();$new_current=$new_current.eq(0);if($new_current.is('.currfh'))
return $new_current;var	$old_current=$new_current.siblings('article.fhitem.currfh'),event_data={blurring:$old_current,focusing:$new_current};$old_current.each(function(){$(this).trigger('blur-article',event_data).removeClass('currfh');});$new_current.addClass('currfh').
trigger('focus-article',event_data);if(!intent||intent==='interest'){var viewhint=false,$fhitems=$('#firehoselist>article.fhitem'),pos=$fhitems.index($new_current);if(pos==0){viewhint=$('body');}else if(pos==$fhitems.length-1){viewhint=$any('div#fh-paginate');}
view($new_current,{hint:viewhint,speed:50});}
return $new_current;}
function firehose_go_next($current){$current=$current||firehose_get_cur();if(fh_is_admin&&$current.length>1){return;}
$current=$current.eq($current.length-1);var $next=$current.nextAll('article.fhitem:first');if($next[0]||!$current[0]){return firehose_set_cur($next);}else{view($current,{hint:$any('div#fh-paginate')});firehose_more();}}
function firehose_go_prev($current){$current=$current||firehose_get_cur();if(fh_is_admin&&$current.length>1){return;}
$current=$current.eq(0);return firehose_set_cur($current.prevAll('article.fhitem:first'));}
function firehose_more(noinc){if(!noinc){firehose_settings.more_num=firehose_settings.more_num+firehose_more_increment;_gaq.push(['_trackEvent','Firehose','FirehoseMore',firehose_settings.more_num.toString()]);_gaq.push(['b._trackEvent','Firehose','FirehoseMore',firehose_settings.more_num.toString()]);}
if($.browser.msie){var version=parseInt($.browser.version);if(version<=8){return true;}}
if(((firehose_item_count+firehose_more_increment)>=200)&&!fh_is_admin){$any('firehose_more').hide();}
if(firehose_user_class){firehose_set_options('more_num',firehose_settings.more_num);}else{firehose_get_updates({oneupdate:1});}
inlineAdFirehose();return false;}
function firehose_section_menu_item(section_id){var id='fhsection-'+section_id;return section_id&&$('#links-sections-title,#'+id).filter(function(){return this.id===id||$(this).metadata().id==section_id;});}
function getSeconds(){return new Date().getTime()/1000;}
function nojscall(f){try{f();}catch(e){}
return false;}
var adTimerSeen={};var adTimerSecs=0;var adTimerClicks=0;var adTimerInsert=0;function inlineAdReset(id){if(id!==undefined)
adTimerSeen[id]=2;adTimerSecs=getSeconds();adTimerClicks=0;adTimerInsert=0;}
function inlineAdClick(id){adTimerClicks=adTimerClicks+1;}
function inlineAdInsertId(id){if(id!==undefined)
adTimerInsert=id;return adTimerInsert;}
function inlineAdVisibles(){var	visible=new Bounds(window),$visible_ads=$('li.inlinead').filter(function(){if(Bounds.intersect(visible,this))return this;});return $visible_ads.length;}
function inlineAdCheckTimer(id,url,clickMax,secsMax){if(!url||!id)
return 0;if(adTimerSeen[id]&&adTimerSeen[id]==2)
return 0;if(clickMax>0&&!adTimerSeen[id])
inlineAdClick(id);var ad=0;if(clickMax>0&&adTimerClicks>=clickMax)
ad=1;else{var secs=getSeconds()-adTimerSecs;if(secs>=secsMax)
ad=1;}
if(!ad)
return 0;return inlineAdInsertId(id);}
function inlineAdFirehose($article,show_on_article_regardless_of_visibility){var Fh=Slash.Firehose,Ad=Fh.floating_slashbox_ad,is_combined=Ad.combined_mode();if(!fh_adTimerUrl)
return 0;if($article){if(!show_on_article_regardless_of_visibility)
$article=Fh.ready_ad_space($article);}else{$article=Fh.choose_article_for_next_ad();}
if(!$article||!$article.length)
return 0;var id=fhitem_key($article).key;if(!id)
return 0;var old_id=inlineAdInsertId();if(!inlineAdCheckTimer(id,fh_adTimerUrl,fh_adTimerClicksMax,fh_adTimerSecsMax))
return 0;if(!is_combined&&Ad.is_visible())
return 0;var $list=$article.find('[context=system]'),topic=$list.find('.t2:not(.s1):first .tag').text(),skin=$list.find('.s1:first .tag').text(),adUrl=fh_adTimerUrl
+'?skin='+(skin?'pg_sect_index':'pg_index')
+(topic?'&topic='+topic:'')
+'&pos=84&cat=medrec',height=is_combined?250:300,ad_content='<iframe class="advertisement" src="'+adUrl+'" height="'+height+'" width="300" frameborder="0" border="0" scrolling="no" marginwidth="0" marginheight="0"></iframe>';Ad($article,ad_content);inlineAdReset(id);if(old_id)
adTimerSeen[old_id]=0;return id;};(function($){var	AD_HEIGHT=300,COMBINED_MODE=false,generation=0,$ad_position=$([]),ad_target_article=null,$ad_offset_parent,$slashboxes,is_ad_locked=false;$(function(){$slashboxes=$('#slashboxes, #userboxes').eq(0);$slashboxes.length&&$('#slug-Top,#slug-Bottom').show();$(document).bind('firehose-setting-noslashboxes',fix_ad_position);$any('firehoselist').bind('articlesMoved',fix_ad_position).bind('beforeArticleRemoved',notice_article_removed);});function notice_article_removed(event,removed_article){if(ad_target_article===removed_article){remove_ad();}}
function remove_ad(){ad_target_article=null;if(is_ad_locked){return false;}
$ad_position.remove();$ad_position=$([]);return true;}
function insert_ad($article,ad){if(!ad||!$article||$article.length!=1||!remove_ad()){return;}
++generation;ad=ad.replace(/&pos=84&/,'&pos=84&gen='+generation+'&');ad_target_article=$article[0];$ad_position=$article.before('<div id="floating-slashbox-ad" class="Empty" />').prev().append(ad);setTimeout(function(){is_ad_locked=false;if(!ad_target_article){remove_ad();}},10000);is_ad_locked=true;if(!$ad_offset_parent){$ad_offset_parent=$article.offsetParent();}
fix_ad_position();$ad_position.filter(':not(.Empty)').fadeIn('fast');}
function verticalAdSpace(){var bounds=Bounds.y('#slug-Bottom');bounds.top=Position('#slug-Top').top;return bounds;}
function pin_ad(){var new_top='';$('#slug-'+($ad_position.is('.Crown')?'Crown':$ad_position.attr('className'))).each(function(){new_top=Position(this).top-Position($ad_offset_parent).top;});$ad_position.css('top',new_top);}
var NO_SPACE=0,NOT_PINNED=2,pinClasses=['Empty','Top','No','Bottom'];function should_crown(){var answer=false;COMBINED_MODE&&$('#slug-Crown:visible').each(function(){answer=Bounds.intersect(window,this);});return answer;}
function scroll_crown(){var now_crown=should_crown();if(now_crown!==$ad_position.is('.Crown')){$ad_position.toggleClass('Crown',now_crown);pin_ad();}}
function fix_ad_position(){if($ad_position.length){var space=verticalAdSpace();if(!TypeOf.number(space.top)||!TypeOf.number(space.bottom)){return;}
space.bottom-=AD_HEIGHT;var natural_top=Position($ad_position.next()).top;if(natural_top===undefined){natural_top=Bounds($ad_position.prev()).bottom;}
var	pinning=$slashboxes.is(':visible')&&between(space.top,natural_top,space.bottom)+NOT_PINNED||NO_SPACE,now_empty=pinning===NO_SPACE,now_crown=!now_empty&&should_crown(),was_empty=$ad_position.is('.Empty');if(!(was_empty&&now_empty)){$ad_position.setClass(pinClasses[pinning]+(now_crown?' Crown':''));pin_ad();}}}
function ad_message(e){var match=/^p(.+):height=(\d+)$/.exec(e.data);if(match&&match[1]==='84'){$ad_position.children('iframe:first').attr('height',match[2]);}}
function combined_mode(enable){if(enable!==void(0)&&enable!=COMBINED_MODE){COMBINED_MODE=!COMBINED_MODE;$('#slug-Crown').toggle(COMBINED_MODE);$('#slug-Bottom, div.slug .content').css('height',AD_HEIGHT=COMBINED_MODE?250:300);$(window)[COMBINED_MODE?'bind':'unbind']('scroll',scroll_crown);fix_ad_position();}
return COMBINED_MODE;}
(function(){var M=Slash.Firehose.floating_slashbox_ad=insert_ad;M.is_visible=function(){return Bounds.intersect(window,$ad_position);};M.remove=remove_ad;M.combined_mode=combined_mode;M.fix_ad_position=fix_ad_position;})();Slash.Firehose.articles_on_screen=function(){var	visible=Bounds.y(window),lo,hi=0;var $articles=$('#firehoselist>article.fhitem:visible').
each(function(){var $this=$(this),this_top=$this.offset().top;if(this_top>=visible.bottom){return false;}
if(lo===undefined){var this_bottom=this_top+$this.height();if(this_bottom>visible.top){lo=hi;}
if(this_bottom>=visible.bottom){++hi;return false;}}
++hi;});if(lo===undefined){return $([]);}else if(lo===0&&hi==$articles.length){return $articles;}else{return $(Array.prototype.slice.call($articles,lo,hi));}}
Slash.Firehose.ready_ad_space=function($articles){var $result=$([]);try{if(!is_ad_locked&&$slashboxes.is(':visible')){if(COMBINED_MODE){return $articles;}
var visible=Bounds.intersection(Bounds.y(window),verticalAdSpace());visible.bottom-=AD_HEIGHT;$result=$articles.filter(function(){return Bounds.contain(visible,Position(this));});}}catch(e){}
return $result;}
Slash.Firehose.choose_article_for_next_ad=function(){var Fh=Slash.Firehose,$articles=Fh.ready_ad_space(Fh.articles_on_screen());return $articles.eq(Math.floor(Math.random()*$articles.length));}})(Slash.jQuery);$(function(){var validkeys={};if(!firehose_smallscreen&&$('div.fhroot').length&&!$('ul#commentlisting').length){validkeys={'X':{tags:1,signoff:1,noanon:1},'T':{tags:1,tag:1,noanon:1},187:{chr:'+',tags:1,tag:1,noanon:1,nod:1},189:{chr:'-',tags:1,tag:1,noanon:1,nix:1},'R':{open:1,readmore:1},'E':{open:1,edit:1},'O':{open:1,link:1},'G':{more:1},'Q':{toggle:1},'S':{next:1},'W':{prev:1},'F':{search:1},190:{chr:'.',slash:1},27:{form:1,unfocus:1}};validkeys['H']=validkeys['A']=validkeys['Q'];validkeys['L']=validkeys['D']=validkeys['Q'];validkeys['K']=validkeys['W'];validkeys['J']=validkeys['S'];validkeys['I']=validkeys['T'];validkeys[107]=validkeys[61]=validkeys[187];validkeys[109]=validkeys[189];validkeys[110]=validkeys[190];}
$(document).keydown(function(e){if(e.ctrlKey||e.metaKey||e.altKey)
return true;var shiftKey=e.shiftKey?1:0;var c=e.which;var key=validkeys[c]?c:String.fromCharCode(c);var keyo=validkeys[key];if(!keyo)
return true;var is_input=e.target&&$(e.target).is(':input');if(!keyo.form&&is_input)
return true;if(keyo.form&&!is_input)
return true;if(keyo.noanon&&!check_logged_in())
return false;if(keyo.admin&&!fh_is_admin)
return true;var cur=firehose_get_cur();var el,id;if(cur.length){el=cur[0];id=el.id.replace(FHID_PREFIX,'');}
if(keyo.tag&&el){firehose_toggle_tag_ui_to(true,el);if(keyo.nod){Tags.submit(el,'nod')}
if(keyo.nix){Tags.submit(el,'nix')}}
if(keyo.signoff&&el&&tag_admin){Tags.submit(el,'signoff');firehose_go_next($(el));}
if(keyo.slash){firehose_set_options('section',$any('links-sections-title').metadata().id);}
if(keyo.unfocus){$(e.target).blur()}
if(keyo.next){firehose_go_next()}
if(keyo.prev){firehose_go_prev()}
if(keyo.more){firehose_more()}
if(keyo.search){view($any('searchquery'),{hint:$('body'),focus:true});}
if(keyo.toggle&&id){toggle_firehose_body(el)}
if(keyo.open){var mylink='';var obj;if(keyo.link){obj=cur.find('span.external > a:first');}
if(keyo.readmore){obj=cur.find('a.datitle:first');}
if(keyo.edit){obj=cur.find('form.edit > a:first');}
if(!mylink.length&&obj.length){mylink=obj[0].href;}
if(mylink.length){return openInWindow(mylink,(shiftKey?1:0));}else{return true;}}
return false;});});function fh_pag_update(){try{var pag_controls=$('#pagination-controls>li>a');var old_pag=$('#fh-paginate>span>a');var old_more=$('#more-experiment>a');var old_more_span=$('#more-experiment>a>span');var fh_count=pag_controls.eq(1);if(fh_count.length){fh_count.html(old_more_span.html());fh_count.attr('href',old_more.attr('href'));fh_count.get(0).onclick=old_more.get(0).onclick;old_more.hide();pag_controls.eq(0).html(old_pag.eq(0).html());pag_controls.eq(0).attr('href',old_pag.eq(0).attr('href'));pag_controls.eq(0).get(0).onclick=old_pag.eq(0).get(0).onclick;for(i=1;i<old_pag.length&&(i+1)<pag_controls.length;i++){var n=i+1;pag_controls.eq(n).html(old_pag.eq(i).html());pag_controls.eq(n).attr('href',old_pag.eq(i).attr('href'));pag_controls.eq(n).get(0).onclick=old_pag.eq(i).get(0).onclick;}}}catch(e){}}
$(document).ready(function(){$(".editor-staff-favorite").click(function(){var scheck=this,cid=$(this).attr('id');ajax_update({op:'set_editor_fav',id:$(this).attr("id")},'',{onComplete:function(){$(".editor-staff-favorite").removeClass("selected");$("#"+cid).addClass('selected');}});});});;function configSectionPopup(){var title="<a href=\"#\" onclick=\"window.location.reload()\" style=\"color:#fff;\">Sectional&nbsp;Display&nbsp;Prefs</a>&nbsp;";var buttons=createPopupButtons("<a href=\"#\" onclick=\"window.location.reload()\">[X]</a>");title=title+buttons;createPopup('links-sections-title',title,"sectionprefs","","Loading...");var url='ajax.pl';var params={};params['op']='getSectionPrefsHTML';ajax_update(params,'sectionprefs-contents');}
function masterChange(el){swapClassColors('secpref_master','secpref_nexus_row');updateNexusAllTidPrefs(el);}
function individualChange(el){swapClassColors('secpref_nexus_row','secpref_master');}
function postSectionPrefChanges(el){var params={};params['op']='setSectionNexusPrefs';params[el.name]=el.value;$('#sectionprefs-message').text('Saving...');var url='ajax.pl';ajax_update(params,'sectionprefs-message');}
function swapClassColors(class_name_active,class_name_deactive){$('tr').filter('.'+class_name_active).css({color:'#000',background:'#fff'}).end().filter('.'+class_name_deactive).css({color:'#999',background:'#ccc'});}
function updateNexusAllTidPrefs(el){var v=el.value;$('form#modal_prefs [name^=nexustid]').each(function(){this.checked=(this.value==v);});}
function reportError(request){alert("error");};;function um_ajax(the_behaviors,the_events){ajax_update({op:'um_ajax',behaviors:the_behaviors,events:the_events},'links-vendors-content');}
function um_fetch_settings(){ajax_update({op:'um_fetch_settings'},'links-vendors-content');}
function um_set_settings(behavior){ajax_update({op:'um_set_settings',behavior:behavior},'links-vendors-content');}
function storyInfo(selector_fragment){var $where,$item=$('[data-fhid='+selector_fragment+']');var $W=$item.find('div.tag-widget.body-widget:first');$where=$related_trigger.
add($W.find('.edit-toggle')).
add($item.find('#updown-'+selector_fragment));var popup_id="storyinfo-"+selector_fragment;var popup=createPopup($where,'Story Info '+createPopupButtons('<a class="fright ico close" href="#" onclick="closePopup('+"'"+popup_id+"-popup'"+'); return false">[X]</a></span><span><a class="fright help ico ui-icon-help" href="#" onclick="return false">[?]</a>'),popup_id);$(popup).draggable();ajax_update({op:'admin_signoffbox',stoid:fhitem_info($item,'stoid')},popup_id+'-contents');}
function tagsHistory(selector_fragment,context){var $where,$item=$('[data-fhid='+selector_fragment+']');if(context=='firehose'){var $W=$item.find('div.tag-widget.body-widget:first');$where=$W.find('.history-button').
add($related_trigger).
add($W.find('.edit-toggle')).
add($item.find('#updown-'+selector_fragment));}else{$where=$any('taghist-'+selector_fragment);}
var popup_id="taghistory-"+selector_fragment;var popup=createPopup($where,'<h1>History</h1> '+createPopupButtons('<a class="fright ico close" href="#" onclick="closePopup('+"'"+popup_id+"-popup'"+'); return false">[X]</a></span><span><a  class="fright help ico ui-icon-help" href="#" onclick="return false">[?]</a>'),popup_id);$(popup).draggable({handle:'h1'}).addClass('popup-tag-history');var item_key=fhitem_key($item);ajax_update({op:'tags_history',type:fhitem_info($item,'type'),key:item_key.key,key_type:item_key.key_type},popup_id+'-contents');}
function signoff($fhitem,id){$.ajax({type:'POST',dataType:'text',data:{op:'admin_signoff',stoid:fhitem_info($fhitem,'stoid'),reskey:reskey_static,limit_fetch:''},success:function(server_response){$fhitem.find('a.signoff-button').remove();}});firehose_collapse_entry(id||$fhitem.attr('data-fhid'));}
$('a.signoff-button').live('click',function(e){signoff($(e.originalEvent.target).closest('.fhitem'));});function firehose_handle_admin_commands(commands){var entry=this,$entry=$(entry),id=$entry.attr('data-fhid');return $.map(commands,function(cmd){var user_cmd=null;switch(cmd){case'extras':firehose_get_admin_extras(id);break;case'taghistory':tagsHistory(id,'firehose');break;case'info':storyInfo(id);break;case'neverdisplay':if(confirm("Set story to neverdisplay?")){user_cmd=cmd;$.ajax({type:'POST',dataType:'text',data:{op:'admin_neverdisplay',stoid:'',fhid:id,reskey:reskey_static,limit_fetch:''},success:function(server_response){firehose_remove_entry(id);}});}
break;case'signed':case'signoff':case'unsigned':signoff($entry,id);break;case'betaedit':show_submit_box_after(id);break;case'oldedit':var loc=document.location+'';var match=loc.match('https?://[^/]*');openInWindow(match+'/firehose.pl?op=edit&amp;id='+id);break;break;case'binspam':if($entry.is('.fhitem-feed'))
break;case'hold':firehose_collapse_entry(id);default:user_cmd=cmd;break;}
return user_cmd;});}
function admin_neverdisplay(stoid,type,fhid){if(confirm("Set story to neverdisplay?")){ajax_update({op:'admin_neverdisplay',reskey:reskey_static,stoid:stoid,fhid:fhid},'nvd-'+stoid);if(type=="firehose"){firehose_remove_entry(fhid);}}}
function admin_submit_memory(fhid){ajax_update({op:'admin_submit_memory',reskey:reskey_static,submatch:$('#submatch-'+fhid).val(),subnote:$('#subnote-'+fhid).val(),penalty:$('#penalty-'+fhid).val()},'sub_mem_message-'+fhid);}
function admin_remove_memory(noid){$('#submem-'+noid).hide();ajax_update({op:'admin_remove_memory',reskey:reskey_static,noid:noid});}
function remarks_create(){var params={op:'remarks_create',reskey:$('#remarks_reskey').val(),remark:$('#remarks_new').val()};if(!params.remark||!params.reskey){return;}
var limit=$('#remarks_max').val();limit&&(params.limit=limit);ajax_update(params,'remarks_whole');}
function remarks_fetch(secs,limit){var params={};params.op='remarks_fetch';params.limit=limit;ajax_periodic_update(secs,params,'remarks_table');}
function remarks_popup(){var params={};params.op='remarks_config';var title="Remarks Config ";var buttons=createPopupButtons('<a href="#" onclick="closePopup(\'remarksconfig-popup\', 1); return false">[X]</a>');title=title+buttons;createPopup('remarks_table',title+buttons,'remarksconfig');ajax_update(params,'remarksconfig-contents');}
function remarks_config_save(){var params={op:'remarks_config_save',reskey:$('#remarks_reskey').val()};if(!params.reskey){return;}
var optional_params={min_priority:$('#remarks_min_priority').val(),limit:$('#remarks_limit').val(),filter:$('#remarks_filter').val()};$.each(optional_params,function(k,v){v&&(params[k]=v);});$('#remarksconfig-message').text('Saving...');ajax_update(params,'remarksconfig-message');}
function admin_slashdbox_fetch(secs){ajax_periodic_update(secs,{op:'admin_slashdbox'},"slashdbox-content");}
function admin_perfbox_fetch(secs){ajax_periodic_update(secs,{op:'admin_perfbox'},"performancebox-content");}
function admin_authorbox_fetch(secs){ajax_periodic_update(secs,{op:'admin_authorbox'},"authoractivity-content");}
function admin_storyadminbox_fetch(secs){ajax_periodic_update(secs,{op:'admin_storyadminbox'},"storyadmin-content");}
function admin_recenttagnamesbox_fetch(secs){ajax_periodic_update(secs,{op:'admin_recenttagnamesbox'},"recenttagnames-content");}
function console_update(use_fh_interval,require_fh_timeout){use_fh_interval=use_fh_interval||0;if(require_fh_timeout&&!fh_is_timed_out){return;}
ajax_update({op:'console_update'},'',{onComplete:json_handler});var interval=30000;if(use_fh_interval){interval=getFirehoseUpdateInterval();}
setTimeout(function(){console_update(use_fh_interval,fh_is_timed_out);},interval*2);}
function firehose_usage(){var interval=300000;ajax_update({op:'firehose_usage'},'firehose_usage-content');setTimeout(firehose_usage,interval);}
function make_spelling_correction(misspelled_word,form_element){var selected_key="select_"+form_element+'_'+misspelled_word;var selected_index=document.forms.slashstoryform.elements[selected_key].selectedIndex;if(selected_index===0){return(0);}
if(selected_index>=1){if(selected_index===1){var params={};params.op='admin_learnword';params.word=misspelled_word;ajax_update(params);}else{var pattern=misspelled_word+"(?![^<]*>)";var re=new RegExp(pattern,"g");var correction=document.forms.slashstoryform.elements[selected_key].value;document.forms.slashstoryform.elements[form_element].value=document.forms.slashstoryform.elements[form_element].value.replace(re,correction);}
var corrected_id=misspelled_word+'_'+form_element+'_correction';$('#'+corrected_id).remove();}
var correction_parent="spellcheck_"+form_element;if($('#'+correction_parent).children().children().length===1){$('#'+correction_parent).remove();}}
function firehose_reject(el){ajax_update({op:'firehose_reject',id:el.value,reskey:reskey_static},'reject_'+el.value);firehose_remove_entry(el.value);}
function firehose_init_note_flags(limit){var $items=$('article.fhitem:not(:has(>h2>span.note-flag))');limit&&($items=$items.filter(':lt('+limit+')'));return $items.each(function(){var	$item=$(this),$flag_parent=$item.find('>h2:first'),$note=$item.find('.note-wrapper'),has_note=$note.length&&!$note.is('.no-note'),text=has_note?$.trim($note.find('.admin-note a').text()):'';$('<span class="note-flag">note</a>').prependTo($flag_parent).attr('title',text).toggleClass('no-note',!has_note).click(firehose_open_note);$('.note-wrapper .admin-note span.ui-icon.ui-icon-note').click(function(){$(this).next().trigger('click');});});}
function firehose_open_note(o){$(this).find('.note-wrapper').children('adminnote').removeClass('hide').focus();}
function firehose_save_note(id){var $entry=$('#firehose-'+id);var note_text=$.trim($entry.find('#note-input-'+id).val());$entry.find('.note-flag, .note-wrapper').toggleClass('no-note',!note_text).filter('.note-flag').attr('title',note_text);ajax_update({op:'firehose_save_note',note:note_text,id:id});$entry.find('#note-form-'+id).addClass('hide');$entry.find('#note-text-'+id).text(note_text||'Note').removeClass('hide');return $entry;}
function firehose_get_admin_extras(id){ajax_update({op:'firehose_get_admin_extras',id:id},'',{onComplete:function(transport){json_handler(transport);view('firehose-'+id);}});}
function firehose_get_and_post(id){ajax_update({op:'firehose_get_form',id:id},'postform-'+id,{onComplete:function(){$('#postform-'+id).submit();}});}
function appendToBodytext(text){$('#admin-bodytext').each(function(){this.className="show";this.value+=text;});}
function appendToMedia(text){$('#admin-media').each(function(){this.className="show";this.value+=text;});}
function microbin_del_page(){var tag_map={'bin-fragment-1':'','bin-fragment-2':'follow','bin-fragment-3':'rss'};$('.microbin-tab').not('.ui-tabs-hide').each(function(){var tag=tag_map[this.id];var min=0;var max=0;$('.microbin:visible tr').each(function(){var match=this.id.match('microbin-([0123456789]+)');if(match[1]){if(!min){min=match[1]}
if(!max){max=match[1]}
if(match[1]>max){max=match[1];}
if(match[1]<min){min=match[1];}}});microbin_mass_del(tag,min,max);});}
function microbin_del_tag_all(){var tag_map={'bin-fragment-1':'','bin-fragment-2':'follow','bin-fragment-3':'rss'};$('.microbin-tab').not('.ui-tabs-hide').each(function(){var tag=tag_map[this.id];microbin_mass_del(tag);});}
function microbin_mass_del(tag,min,max){var params={};params['tag']=tag;params['min']=min;params['max']=max;params['op']='microbin_mass_del';ajax_update(params,'',{onComplete:function(){microbin_clear();microbin_refresh();}});}
function microbin_del(id){ajax_update({op:'microbin_del',id:id,reskey:reskey_static});$('#microbin-'+id).fadeOut().remove;}
function microbin_tosub(id){ajax_update({op:'microbin_tosub',id:id,reskey:reskey_static});$('#microbin-'+id).fadeOut().remove;}
function microbin_clear(){$('.microbin .tr').fadeOut('slow').remove();}
function microbin_refresh(pane){var params={};var tag_map={'bin-fragment-1':'','bin-fragment-2':'follow','bin-fragment-3':'rss'};if(pane){params['tag']=tag_map[pane];}else{$('.microbin-tab').not('.ui-tabs-hide').each(function(){params['tag']=tag_map[this.id];});}
params['op']='microbin_fetch';if($('#slashboxes .microbin').length>0){params['type']='narrow';}
ajax_update(params,'',{onComplete:json_handler});}
$(function(){if($.browser.safari||$.browser.opera){$('.edit a').css('margin-top','0pt');}});;(function($){var IS_SHOWN={};window.Falk||(window.Falk={is_shown:function(adPos){return IS_SHOWN[adPos];},show:function(adPos,enable){enable=enable===void(0)||!!enable;if(enable!=IS_SHOWN[adPos]){IS_SHOWN[adPos]=enable;$(document).trigger('falk',[adPos,enable]);}}});})(jQuery);$(document).ready(function(){$('h3#a2ns span.click,#a2footer .fadeout').click(function(e){window.location=$("#hrefns").attr('href');return false;});$('#embbeded_login_modal .ico.close').live('click',function(e){e.preventDefault();});$('li.comment > div > div > div > h4 > a').live('click',function(){return false;});$('li.comment.oneline > div > div.commentBody').live('click',function(){var cid=$(this).closest('li.comment').attr('id');return D2.setFocusComment(cid.replace('tree_',''));});if($('#fhtablist_new li').hasClass('active')&&$('#firehose-sections li').hasClass('active')){$('#fhtablist_new li').removeClass('active');}
$('.comment_share_toggle').live('click',function(e){$(this).next().toggleClass('hide');return false;});$('#faq div').hide();$('#faq h1').after('<p><span class="expand ui-icon faqtoggle off link">Expand All</span></p>');$('#faq h2, #faq h3').addClass('link');$('#faq h2').bind('click',function(){if($(this).next('div').is(':hidden')){$(this).next('div').show();$(this).removeClass('link');}else{$(this).next('div').hide();$(this).addClass('link');}});$('#faq h3').bind('click',function(){if($(this).next('div').is(':hidden')){$(this).next('div').show();$(this).removeClass('link');}else{$(this).next('div').hide();$(this).addClass('link');}});$('#faq .expand').bind('click',function(){if($('h2, h3').next('div').is(':hidden')){$('h2, h3').next('div').show();$('h2, h3').removeClass('link');$(this).html('Collapse All').addClass('on').removeClass('off');}else{$('h2, h3').next('div').hide();$('h2, h3').addClass('link');$(this).html('Expand All').addClass('off').removeClass('on');}});$(".ui-icon:not(.ui-state-disabled)")
.hover(function(){$(this).addClass("ui-state-hover");},function(){$(this).removeClass("ui-state-hover");})
.mousedown(function(){$(this).parents('.fg-buttonset-single:first').find(".ui-icon.ui-state-active").removeClass("ui-state-active");if($(this).is('.ui-state-active.fg-button-toggleable, .fg-buttonset-multi .ui-state-active')){$(this).removeClass("ui-state-active");}else{$(this).addClass("ui-state-active");}})
.mouseup(function(){if(!$(this).parent().is('.fg-buttonset-single ,  .fg-buttonset-multi ')||!$(this).is('.fg-button-toggleable')){$(this).removeClass("ui-state-active");}});$('.admin-note a').live('click',function(){$(this).parent().prev().toggleClass('hide');});$(".head .block .content").after("<div class='foot'>&nbsp;</div>");$('input[name="nothumbs"]').each(function(){if($(this).attr('checked','checked')){$('#firehose').addClass('vote_enabled');}else{$('#firehose').removeClass('vote_enabled');}});$('input[name="nocolors"]').each(function(){if($(this).attr('checked','checked')){$('#firehose').addClass('color_enabled');}else{$('#firehose').removeClass('color_enabled');}});$('.head .tags').appendTo('.head .article .body');$('.share_submission > a,.comment_share > a.slashpop,.cc a.slashpop').live('click',function(tfg){var popname="pn";popname=popname+$(this).closest('li.comment').attr("id");window.open($(this).attr('href'),popname,'width=560,height=390,status=0,toolbar=0,menubar=0,directories=0,channelmode=0');return false;});$('.sharethisa').live('click',function(tsa){var tsaid=$(this).attr('rel');$("#"+tsaid).toggleClass('h');return false;});adupdate();});function firehose_marquee(op){if(op=="hide"){firehose_set_options('nomarquee',1);$("#marquee a").addClass('collapsed');$(".head").hide();}else if(op=="show"){firehose_set_options('nomarquee',0);$("#marquee a").removeClass('collapsed');if($(".head").size()){$(".head").show();}else{setTimeout("window.location.reload()","2000");}}}
function adupdate(){$('#modal_box,#embbeded_login_modal').addClass('push');}
function firehose_toggle_prefs(){if($("#fh_advprefs").is(":hidden")){$("#fh_advprefs").fadeIn('fast');}else{$("#fh_advprefs").fadeOut('fast');}
return false;}
if(typeof COMSCORE=="undefined"){var COMSCORE={}}COMSCORE.beacon=function(d){if(!d){return}var a=1.7,e=document,h=e.location,g=512,c=function(i,j){if(i==null){return""}i=(encodeURIComponent||escape)(i);if(j){i=i.substr(0,j)}return i},f=[(h.protocol=="https:"?"https://sb":"http://b"),".scorecardresearch.com/b?","c1=",c(d.c1),"&c2=",c(d.c2),"&rn=",Math.random(),"&c7=",c(h.href,g),"&c3=",c(d.c3),"&c4=",c(d.c4,g),"&c5=",c(d.c5),"&c6=",c(d.c6),"&c10=",c(d.c10),"&c15=",c(d.c15),"&c16=",c(d.c16),"&c8=",c(e.title),"&c9=",c(e.referrer,g),"&cv=",a,d.r?"&r="+c(d.r,g):""].join("");f=f.length>2080?f.substr(0,2075)+"&ct=1":f;var b=new Image();b.onload=function(){};b.src=f;return f};