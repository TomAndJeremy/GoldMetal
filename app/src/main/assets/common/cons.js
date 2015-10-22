$(document).ready(function() { 
	function getcode(code){ 
	var c=code.substring(0,2);
	if(c=="60"){
		return "sh"+code;
	}else
	if(c=="30"||c=="00"){
		return "sz"+code;
	}else return "";
	
};
var ajaxdata=null;
getContent = function(tid) { 
	if(ajaxdata!=null){
		ajaxdata.abort();
		ajaxdata=null;
	} 
	$.get('rp',function(data){ 
	ajaxdata=$.post(wkstock.host+"news/content/"+tid+"?yhid="+data,function(json) {  
		if (json.a['title'] !="") { 
			 document.title=json.a.title;
				$(".title").html(json.a['title']);
				$(".rq").html(json.a['createtime']);
	 		    $(".content").html(json.ac['content']);  
	 		    $("[stockCode1]").after(function(){  return "<span onclick=\"wkstock.addselfstock('"+$(this).attr("stockCode1")+"')\" title=\"\u52a0\u5165\u81ea\u9009\u80a1\" style=\"cursor:pointer;color:#464646;font-size:10px;\">\u81ea</span><span title=\"\u67e5\u770b\u9898\u6750\u6982\u5ff5\" style=\"cursor:pointer;color:#0000FF;font-size:12px;\" onclick=\"wkstock.getTc('"+$(this).attr("stockCode1")+"')\">\u9898</span>";});
	 			$("[stockCode2]").after(function(){ return "<span onclick=\"wkstock.addselfstock('"+$(this).attr("stockCode2")+"')\" title=\"\u52a0\u5165\u81ea\u9009\u80a1\" style=\"cursor:pointer;color:#464646;font-size:10px;\">\u81ea</span><span title=\"\u67e5\u770b\u9898\u6750\u6982\u5ff5\" style=\"cursor:pointer;color:#0000FF;font-size:12px;\" onclick=\"wkstock.getTc('"+$(this).attr("stockCode2")+"')\">\u9898</span>";});
	 		 wkstock.autocloseTip=true;
			 wkstock.appendTip();  
			 wkstock.stockPlugin({pid:'379'}); 
		}else $(".content").html('\u6682\u65e0\u4fe1\u606f');
		},"jsonp");
	});
 return false;
}; 
wkstock.autocloseTip=true;
wkstock.appendTip();  
wkstock.stockPlugin();  
 getContent(wkstock._GET('tid')); 
});