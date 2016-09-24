function dclkToObject(id) {
		if(document.layers){
			return (document.layers[id])?eval(document.layers[id]):null;
		}
		else if(document.all && !document.getElementById){
			return (eval("window."+id))?eval("window."+id):null;
		}
		else if(document.getElementById && document.body.style) {
			return (document.getElementById(id))?eval(document.getElementById(id)):null;
		}
	}
  
function dclkFlashWrite(string){
  document.write(string);
  }

function dclkFlashInnerHTML(htmlElementId,code){
  var x=dclkToObject(htmlElementId);
  if(x){
    if(document.getElementById||document.all){
      x.innerHTML='';
      x.innerHTML=code;
      }
    else if(document.layers){
      x.document.open();
      x.document.write(code);
      x.document.close();
      }
    }
  }
