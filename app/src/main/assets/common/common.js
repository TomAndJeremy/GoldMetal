function killerrors() {
    return true;
} 
function closeWindow(){try{external.createObject("Quote").actionCommand(57616)}catch(a){window.close()}}
window.onerror = killerrors; 
var httphost = "http://zixun.gp.andun.org/zixunserver/index.php/";
var wkstock = {
    thsQuote:null,
    _set:{
        debug:false
    },
    opentype:0,
    ajaxdata:null,
    pageInfo:{
        pageNum:"pageNum",
        numPerPage:"numPerPage",
        orderField:"orderField",
        orderDirection:"orderDirection"
    },
    statusCode:{
        ok:200,
        error:300,
        timeout:301
    },
    jsonEval:function(data) {
        try {
            if ($.type(data) == "string") return eval("(" + data + ")"); else return data;
        } catch (e) {
        	return;
        }
    },
    debug:function(msg) {
        if (this._set.debug) {
            if (typeof console != "undefined") console.log(msg); else alert(msg);
        }
    },
    regsearch:function(re, str, index) {
        var arr = null;
        var s = [];
        while (true) {
            arr = re.exec(str);
            if (arr == null) break;
            s.push(arr[index]);
        }
        return s;
    },
    ajaxError:function(xhr, ajaxOptions, thrownError) {
        
    },
    ajaxDone:function(json) {
        
    },
    host:"",
    getYqts:function() {
        if ($("#yqts") && $("#yqts").length > 0) $.ajax({
            type:"post",
            url:this.host + "common/yqts/",
            dataType:"jsonp",
            success:function(json) {
                $("#yqts").html(json.content);
            }
        });
    },
    init:function(options) {
        var op = $.extend({
            host:"",
            debug:false
        }, options);
        this.host = op.host;
        this._set.debug = op.debug;
        this.opentype = 1;
        this.thsQuote = external.createObject("Quote");
        this.getYqts();
    },
    getcode:function(code) {
        var c = code.substring(0, 2);
        if (c == "60") {
            return "sh" + code;
        } else if (c == "30" || c == "00") {
            return "sz" + code;
        } else return "";
    },
    getTop:function(e) {
        var offset = e.offsetTop;
        if (e.offsetParent != null) offset += getTop(e.offssetParent);
        return offset;
    },
    getLeft:function(e) {
        var offset = e.offsetLeft; 
        if (e.offsetParent != null) offset += getLeft(e.offssetParent);
        return offset;
    },
    jumppage:function(id, code) {
        var thsQuote = external.createObject("Quote");
        thsQuote.switchPage({
            id:id,
            code:code
        });
        thsQuote = null;
    },
    DateDiff:function(begindate, enddate) {
        var tempdate, obegindate, oenddate, days;
        tempdate = begindate.split("-");
        obegindate = new Date(tempdate[1] + "-" + (tempdate[2] - 1) + "-" + tempdate[0]);
        tempdate = enddate.split("-");
        oenddate = new Date(tempdate[1] + "-" + (tempdate[2] - 1) + "-" + tempdate[0]);
        days = parseInt(Math.abs(oenddate - obegindate) / 1e3 / 60 / 60 / 24);
        return days;
    },
    getTodayDate:function(d) {
        return d.getYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate();
    },
    openUrl:function(purl) {
        if (this.opentype == 0) purl = purl.substring(0, 7) == "http://" ? purl :this.host + purl;
        try {
            var thsUtil = external.createObject("Util");
            thsUtil.openURL(purl, 1);
            thsUtil = null;
        } catch (e) {
            window.open(purl);
        }
    },
    getTc:function(co) {
        this.openUrl(this.host + "tcss/content/" + co);
    },
    getData:function(options) {
        var op = $.extend({
            page:1,
            stockCode:"",
            title:"",
            title1:"",
            ctime:1,
            orgid:"",
            orders:"1",
            url:"",
            sid:"16",
            url:"",
            ps:15,
            callback:null
        }, options);
        if (this.ajaxdata != null) {
            this.ajaxdata.abort();
            this.ajaxdata = null;
        }
        this.ajaxdata = $.post(this.host + op.url + "/" + op.page, {
            title:op.title,
            page:op.page,
            stockcode:op.stockCode,
            title1:op.title1,
            ctime:op.ctime,
            orders:op.orders,
            ps:op.ps
        }, function(json) {
            if (jQuery.isFunction(op.callback)) op.callback(json); else {
                if (json.list.length > 0) {
                    if (json.page.totalpage > 1) {
                        strFy = "【共" + json.page.totalpage + "页 当前第" + json.page.page + '页】<a href="#" onclick=getData({page:1,stockCode:$("#stockCode").val(),title:$("#title").val(),ctime:$("#ctime").val(),orders:$("#orders").val()})>首页</a><a onclick=getData({page:' + json.page.uppage + ',stockCode:$("#stockCode").val(),title:$("#title").val(),ctime:$("#ctime").val(),orders:$("#orders").val()}) href="#">上一页</a><a href="#" onclick=getData({page:' + json.page.nextpage + ',stockCode:$("#stockCode").val(),title:$("#title").val(),ctime:$("#ctime").val(),orders:$("#orders").val()})>下一页</a><a href="#" onclick=getData({page:' + json.page.totalpage + ',stockCode:$("#stockCode").val(),title:$("#title").val(),ctime:$("#ctime").val(),orders:$("#orders").val()})>末页</a>';
                        $("#fenye").html(strFy).show();
                    } else {
                        $("#fenye").hide();
                    }
                }
            }
        }, "jsonp");
    },
    autocloseTip:true,
    appendTip:function() {
        if ($("tip").length == 0) {
            $("body").append("<div id='tip' style='display:none;position:absolute;z-index:9099;float:left;'></div>");
            if (this.autocloseTip) $("#tip").mouseover(function() {
                $(this).hide();
            });
            $(document).bind("click", function() {
                $("#tip").hide();
            });
        }
    },
    tipdiv:"<div id='tip' style='display:none;position:absolute;z-index:9099;float:left;'></div>",
    tip:function(obj, con) {
        obj.wrap(this.tipdiv);
        obj.parent.append(con);
    },
    loaddata:function() {
        $("#tip").html("<div><img src='" + $("#img").val() + "'/>正在加载...</div>");
        $("#tip").show();
    },
    getStockInfo:function(code, opt, obj) {
        var thsQuote = external.createObject("Quote");
        this.loaddata;
        var offset = obj.offset();
        var left = offset.left + obj.width() + "px";
        var top = offset.top + obj.height() + "px";
        var con = "现价:- 涨跌幅:-";
        var reqObj = {
            code:code,
            type:"new,zhangdiefu",
            onready:function() {
                var typeArr = [ "zqmc", "new", "zhangdiefu" ];
                var retObj = [];
                for (var i in typeArr) {
                    var param = {
                        code:code,
                        type:typeArr[i]
                    };
                    retObj.push(eval(thsQuote.getData(param)));
                }
                if (typeof retObj[1][code] != "undefined" && retObj[1][code]["new"] != "NUL" && retObj[1][code]["new"] && !isNaN(parseFloat(retObj[1][code]["new"]))) {
                    var newPrice = parseFloat(retObj[1][code]["new"]).toFixed(2);
                    var zhangDiefu = parseFloat(retObj[2][code]["zhangdiefu"]).toFixed(2);
                    var color = "";
                    if (zhangDiefu > 0) color = opt.zhangcolor; else if (zhangDiefu < 0) color = opt.diecolor; else color = opt.color;
                    con = "现价:<label style='font-weight:600;color:" + color + "'>" + newPrice + "</label>" + " 涨跌幅:<label style='font-weight:600;color:" + color + "'>" + zhangDiefu + "%</label>";
                    $("#" + opt.tipid).attr("style", "left:" + left + ";top:" + top + ";position:absolute;z-index:9099;float:left;border:1px solid #333333;background:#FFFFFF;text-align:left;font-size:14px;padding:2px;");
                    $("#" + opt.tipid).html(con);
                    $("#" + opt.tipid).show();
                } else {
                    $("#" + opt.tipid).attr("style", "left:" + left + ";top:" + top + ";position:absolute;z-index:9099;float:left;border:1px solid #333333;background:#FFFFFF;text-align:left;font-size:14px;padding:2px;");
                    $("#" + opt.tipid).html(con);
                    $("#" + opt.tipid).show();
                }
            }
        };
        var flag = thsQuote.request(reqObj);
    },
    userinfo:function(ser) {
        $.post(this.host + "user/" + ser, function(json) {
            $("#userinfo").html(json.userinfo);
        }, "jsonp");
    },
    addselfstock:function(a) {
        a instanceof Array && (a = a.join(","));
        try {
            var b = external.createObject("Quote").setSelfStock({
                code:a,
                mode:"add"
            });
            b ? alert("已添加"):alert("添加失败，请稍后再试。");
        } catch (b) {
            alert("添加失败!");
        }
    },
    _GET:function(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    },
    stockPlugin:function(options) {
        var defaults = {
            code:null,
            zhangcolor:"#ff3232",
            diecolor:"#00A500",
            color:"#000000",
            fontcolor:"#000000",
            tipid:"tip",
            jumpflag:true,
            pid:"5100",
            format:null
        };
        var opts = $.extend({}, defaults, options);
        $this = $(this);
        var o = $.metadata ? $.extend({}, opts, $this.metadata()) :opts;
        $("[stockCode2]").mouseout(function(e) {
            $("#" + o.tipid).hide();
        });
        $("[stockCode2]").bind("mouseover", function() {
            var code = $(this).attr("stockCode2");
            wkstock.getStockInfo(code, o, $(this));
        });
        $("[stockCode1]").bind("mouseover", function() {
            var code = $(this).attr("stockCode1");
            wkstock.getStockInfo(code, o, $(this));
            
        });
        $("[stockCode1]").bind("mouseover", function() {
            var code = $(this).attr("stockCode1");
            wkstock.getStockInfo(code, o, $(this));
        });
        if (o.jumpflag) {
            $("[stockCode1]").bind("click", function() {
                wkstock.jumppage(o.pid, $(this).attr("stockCode1"));
                return false;
            });
            $("[stockCode2]").bind("click", function() {
                wkstock.jumppage(o.pid, $(this).attr("stockCode2"));
                return false;
            });
        }
        $(".star").bind("mouseover", function() {
            wkstock.loaddata;
            var offset = $(this).offset();
            var left = offset.left + $(this).width() + "px";
            var top = offset.top + $(this).height() + "px";
            $("#tip").attr("style", "left:" + left + ";top:" + top + ";position:absolute;z-index:9099;float:left;background:#C82925;text-align:left;font-size:14px;padding:2px;");
            $("#tip").html($(this).find("label").html()).show();
        });
        $(".star").bind("mouseout", function() {
            $("#tip").hide();
        });
    }
};

var stockData={ 
		getData:function (codelist, params, callback) {
			var thsQuote = external.createObject("Quote"); 
			var reqObj = {
				code		: codelist,
				type		: params.join(','),  
				onready		: function () {  
					var codeArr = codelist.split(',');
					var data = {};
					//循环获取各个请求参数数据
					for (var i = 0, pLength = params.length; i < pLength; i++) {
						var param = {
							code		: codelist,		 
							type		: params[i]  
						};
						var retStr = thsQuote.getData(param);   
						if (retStr != '({})') {
							retObj = eval('(' + retStr + ')');
						
						} else {
							retObj = null;
						} 
						if (retObj !== null) {
						//按股票代码分类获取的数据
							for (var j = 0, cdLength = codeArr.length; j < cdLength; j++) {
								if (typeof data[codeArr[j]] == 'undefined') {
									data[codeArr[j]] = {}; 
								}
								data[codeArr[j]][params[i]] = retObj[codeArr[j]][params[i]];
							}
						} else {
							for (var j = 0, cdLength = codeArr.length; j < cdLength; j++) {
								if (typeof data[codeArr[j]] == 'undefined') {
									data[codeArr[j]] = {};
								}
								data[codeArr[j]][params[i]] = null;
							}
						}
					}
					callback(data);
				}
			};
			var flag = thsQuote.request(reqObj);
		},
		getDataByObj:function (codelist, params,obj, callback) {
			var thsQuote = external.createObject("Quote"); 
			var reqObj = {
				code		: codelist,
				type		: params.join(','),  
				onready		: function () {  
					var codeArr = codelist.split(',');
					var data = {};
					//循环获取各个请求参数数据
					for (var i = 0, pLength = params.length; i < pLength; i++) {
						var param = {
							code		: codelist,		 
							type		: params[i]  
						};
						var retStr = thsQuote.getData(param);   
						if (retStr != '({})') {
							retObj = eval('(' + retStr + ')');
						
						} else {
							retObj = null;
						} 
						if (retObj !== null) {
						//按股票代码分类获取的数据
							for (var j = 0, cdLength = codeArr.length; j < cdLength; j++) {
								if (typeof data[codeArr[j]] == 'undefined') {
									data[codeArr[j]] = {}; 
								}
								data[codeArr[j]][params[i]] = retObj[codeArr[j]][params[i]];
							}
						} else {
							for (var j = 0, cdLength = codeArr.length; j < cdLength; j++) {
								if (typeof data[codeArr[j]] == 'undefined') {
									data[codeArr[j]] = {};
								}
								data[codeArr[j]][params[i]] = null;
							}
						}
					}
					callback(data,obj);
				}
			};
			var flag = thsQuote.request(reqObj);
		},
		getDataByDay:function (codelist, params,tr,b,e,callback) {
			var thsQuote = external.createObject("Quote");
			var period =0;
			var begin = b;
			var end = e;
			var reqObj = {
				code		: codelist,
				type		: params.join(','), 
				period:'day',
				begin:b,
				end:e,
				onready		: function () {  
					var codeArr = codelist.split(',');
					var data = {};
					//循环获取各个请求参数数据
					for (var i = 0, pLength = params.length; i < pLength; i++) {
						var param = {
							code		: codelist, 
							type		: params[i],
							period:'day',
							time:b,
							mode:'prev'
						};
						var retStr = thsQuote.getData(param);
						//$("#test1").append(params[i] + ":" + retStr + "<br/>");

						if (retStr != '({})') {
							retObj = eval('(' + retStr + ')');
						} else {
							retObj = null;
						}
						if (retObj !== null) {
						//按股票代码分类获取的数据
							for (var j = 0, cdLength = codeArr.length; j < cdLength; j++) {
								if (typeof data[codeArr[j]] == 'undefined') {
									data[codeArr[j]] = {};
								}
								data[codeArr[j]][params[i]] = retObj[codeArr[j]][params[i]];
							}
						} else {
							for (var j = 0, cdLength = codeArr.length; j < cdLength; j++) {
								if (typeof data[codeArr[j]] == 'undefined') {
									data[codeArr[j]] = {};
								}
								data[codeArr[j]][params[i]] = null;
							}
						}
					}
					callback(data,tr);
				}
			};
			var flag = thsQuote.request(reqObj);
		}
		}; 
$(document).ready(function() {
    wkstock.init({
        host:httphost
    });
});

Date.prototype.Format = function(fmt) 
{ //author: meizz 
var o = { 
 "M+" : this.getMonth()+1,                 //月份 
 "d+" : this.getDate(),                    //日 
 "h+" : this.getHours(),                   //小时 
 "m+" : this.getMinutes(),                 //分 
 "s+" : this.getSeconds(),                 //秒 
 "q+" : Math.floor((this.getMonth()+3)/3), //季度 
 "S"  : this.getMilliseconds()             //毫秒 
}; 
if(/(y+)/.test(fmt)) 
 fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
for(var k in o) 
 if(new RegExp("("+ k +")").test(fmt)) 
fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
return fmt; 
}

try{
external.createObject("Quote");
}catch(e){
 window.location='../norole.html';
}

function getTequan(){
  var file=external.getModulePath()+"zgl\\l.dat"; 
 var fis = external.createObject("FileInputStream");
 fis.open(file);
str=fis.read();
fis.close();
return str.split("@");
}

function tiaox(idx){
  if(idx==37586){
  window.location='http://jsynew.gjscn.com/money/index.php';
  }else
  if(idx==37361){
  window.location='http://a.gjscn.com/tequan/sytq.htm';
  }
} 
var tequan=getTequan(); 
