function ShowDetail(obj, CountryCode, columnCode, table, objrowindex) { 
	var flagdiv=$("#d1"+objrowindex).is(":hidden");
	$(".xtrcon").hide();
 	if($("#d1"+objrowindex).length>0){
			if(flagdiv){
			$("#d1"+objrowindex).show();
			}else $("#d1"+objrowindex).hide();
			 return ;
		} 
		$("#d1 tbody tr").removeClass("yjl_caijingyueli_detelBox_tdBg");
		$(obj).addClass("yjl_caijingyueli_detelBox_tdBg");
	    datarow=objrowindex;
        strDetail = "<tr id='d1"+objrowindex+"' class='xtrcon'>";
        strDetail += "<td colspan=\"6\" class=\"yjl_caijingyueli_detelBox_td\" style='padding-left:5px;'>";
        strDetail += "<div class=\"yjl_caijingyueli_detelBox\">";
        strDetail += "<div class=\"yjl_caijingyueli_detelBox_head\">";
        strDetail += "<div class=\"head_l\">";
        strDetail += "<div>";
        strDetail += "<p><span>公布值：</span><span>" + obj.cells[5].innerHTML + "</span></p>";
        strDetail += "<p><span>预测值：</span><span>" + obj.cells[4].innerHTML + "</span></p>";
        strDetail += "<p><span>前&nbsp;&nbsp;值：</span><span>" + obj.cells[3].innerHTML.split('<')[0] + "</span></p>";
        strDetail += "<p><span>重要性：</span><span>" + obj.cells[1].innerHTML + "</span></p>";
        strDetail += "</div></div>";
        strDetail += "<div class=\"head_t\">" + obj.cells[2].innerHTML + "</div>";
        strDetail += "<div class=\"head_r\"></div>";
        //strDetail += "<h3 id=newh3tr><a href=\"replaceurl\" target=\"_blank\" title=\"" + titltxt + "\">" + titltxt + "</a></h3>";
        //strDetail += "<h3 id=newh3tr>" + titltxt + "</h3>";
        strDetail += "</td></tr>";
        $(obj).after(strDetail);
        SetContentDetail(obj, columnCode, strDetail,objrowindex);
}
function SetContentDetail(obj, columnCode, strDetail,objrowindex) {
    $.ajax({
        type: "get",
        async: false,
        url: dataurl + "/IDataPlatform/FinancialCalendarNew/IFinancialCalendarDetial.ashx",
        data: "user=" + datauser + "&columnCode=" + columnCode + "&dataCount=10&succ_callback=CallbackContentDetail&rd=" + (new Date()).valueOf(),
        dataType: "jsonp",
        jsonp: "callbackfun",
        jsonpCallback: CallbackContentDetail,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            //alert(XMLHttpRequest.status);
            //alert(XMLHttpRequest.readyState);
            //alert(textStatus);
        }
    });
}

//财经详细内容
function CallbackContentDetail(jsonObj) {
	SetDetailHtml(jsonObj);
}
function SetDetailHtml(jsonObj) {  
	if(jsonObj!=null&&jsonObj["List"].length>0){  
		$("#d1"+datarow+" td:eq(0)").find(".head_r").html(jsonObj["List"][0].Define);
	}
}
function ShowRevised(obj, show) {
    var a = $(obj).find("a");
    if (show == "none")
        $(obj).removeClass("yjl_caijingyueli_detelBox_tdBg1");
    else
        $(obj).addClass("yjl_caijingyueli_detelBox_tdBg1");

    if (a != null) {
        a.css("display", show);
    }
}
function SetContent(beginDate) { 
	currdate=beginDate; 
	var datetemp=new Date(currdate.replace(/-/g,'/'));
	$('#showtime').html('('+datetemp.getFullYear()+'年'+(datetemp.getMonth()+1)+'月'+datetemp.getDate()+'日)');
	if($("[ty="+currdate+"]").length>0){
//		alert($("[ty="+currdate+"]").html());
		$("[ty]").removeClass("tt");
		$("[ty="+currdate+"]").addClass("tt");
	}
    $.ajax({
        type: "get",
        async: true,
        url: dataurl + "/IDataPlatform/FinancialCalendarNew/IFinancialCalendarData.ashx",
        data: "user=" + datauser + "&beginDate=" + beginDate + "&rd=" + (new Date()).valueOf(),
        dataType: "jsonp",
        jsonp: "succ_callback",
        jsonpCallback: "CallbackContent",
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            //alert(XMLHttpRequest.status);
            //alert(XMLHttpRequest.readyState);
            //alert(textStatus);
        }
    });
}
function CallbackContent(jsonObj) {
    //回调json数据成功后的处理
    CreateFinanceCalendarHtmlData(jsonObj);
}

function CreateFinanceCalendarHtmlData(jsonObj) {
    var ftable = "";
	divid=currdate;
    var FinancialCalendarData = jsonObj["List"][0]["FinancialCalendarData"];
    var x_str = "";
    if (FinancialCalendarData != null) {
        if (FinancialCalendarData.length > 0) {
            var time;
            var country;
            var importnum;
            for (var i = 0; i < FinancialCalendarData.length; i++) {
                //重要性
                var x_connt = FinancialCalendarData[i]["Weightiness"];
                if (x_connt == "0")
                    x_str = "<b class=\"td_star2\"></b><b class=\"td_star2\"></b><b class=\"td_star2\"></b>";
                if (x_connt == "1")
                    x_str = "<b class=\"td_star\"></b><b class=\"td_star2\"></b><b class=\"td_star2\"></b>";
                if (x_connt == "2")
                    x_str = "<b class=\"td_star\"></b><b class=\"td_star\"></b><b class=\"td_star2\"></b>";
                if (x_connt == "3")
                    x_str = "<b class=\"td_star\"></b><b class=\"td_star\"></b><b class=\"td_star\"></b>";

                ftable += "<tr class=\"yjl_noLine\" onmouseout=\"ShowRevised(this,'none')\" onmouseover=\"ShowRevised(this,'inline-block')\" ";
                ftable += " onclick=\"ShowDetail(this,'" + FinancialCalendarData[i]["CountryCode"] + "','" + FinancialCalendarData[i]["ColumnCode"] + "','table_Search','" + i + "') \"";
                ftable += ">";

                //CSS红色Class
                var redClassString = "";
                if (x_connt == "3") {
                    redClassString = "class=\"yjl_caijingyueli_red\" style='padding-left:5px;'";
                }

                //时间,国家,重要性相同的数据合并显示
                if (time == FinancialCalendarData[i]["Time"] && importnum == FinancialCalendarData[i]["Weightiness"] && country == FinancialCalendarData[i]["CountryName"]) {
                    ftable += "<td></td><td></td><td " + redClassString + ">";
                }
                else {
                    ftable += "<td " + redClassString + " class='ti'>" + FinancialCalendarData[i]["Time"] + "</td>";
                    ftable += "<td " + redClassString + ">" + x_str + "</td>";
                    ftable += "<td " + redClassString + ">";
                }
                //国家,内容
                ftable += "<span>" + FinancialCalendarData[i]["CountryName"] + "</span>" + FinancialCalendarData[i]["Content"] + "";
                //前值,预测值,公布值
                var Previous = FinancialCalendarData[i]["Previous"] == "" ? "&nbsp;" : FinancialCalendarData[i]["Previous"];
                var Predict = FinancialCalendarData[i]["Predict"] == "" ? "&nbsp;" : FinancialCalendarData[i]["Predict"];
                var CurrentValue = FinancialCalendarData[i]["CurrentValue"] == "" ? "等待中" : FinancialCalendarData[i]["CurrentValue"]; //<b class=\"yjl_caijingyueli_waiting\">等待中</b>
                if (CurrentValue == "等待中" && divid == $("#Hidden_TodayDate").val() && FinancialCalendarData[i]["Time"]!="-----") {
                    CurrentValue = "<b class=\"yjl_caijingyueli_waiting\">等待中</b>";
                }
                //判断是否有修正值
                if (FinancialCalendarData[i]["Revised"] != "") {
                    ftable += "</td><td>" + Previous + "";
                    ftable += "<a style=\"display: none;width:10px\" class=yjl_caijingyueli_xiugai title=" + FinancialCalendarData[i]["Revised"] + " /></td>";
                } else
                    ftable += "</td><td>" + Previous + "</td>";
                ftable += "<td>" + Predict + "</td>";
                ftable += "<td>" + CurrentValue + "</td>"
                //ftable += "<td><b class=\"yjl_caijingyueli_tuxing\" title=\"详解\"></b></td>";
                ftable += "</tr>";

                //储存本条数据的时间,国家,重要性用于跟下条数据比较是否相同,相同既合并
                importnum = FinancialCalendarData[i]["Weightiness"];
                country = FinancialCalendarData[i]["CountryName"];
                time = FinancialCalendarData[i]["Time"];
            } 
            $("#d1 tbody").html(ftable);

            //指标行下划线显示控制 $("#table_2014-01-06 tr").eq(1).children().eq(1).html() 
            //通过时间划分 tr:has(span)
            
            for (var i = 0; i < $("#d1 tr:has(span)").length; i++) {
                //通过时间划分
                if ($("#d1 tbody tr:has(span)").eq(i).children().eq(0).html() != ($("#d1 tr:has(span)").eq(i + 1).children().eq(0).html() == "" ? $("#d1 tr:has(span)").eq(i).children().eq(0).html() : $("#d1 tr:has(span)").eq(i + 1).children().eq(0).html())) {
                    $("#d1 tbody tr:has(span)").eq(i).removeClass("yjl_noLine");
                }
                //通过国家划分
                if ($("#d1 tbody tr span").eq(i).html() != $("#d1 tr span").eq(i+1).html()) {
                    $("#d1 tbody tr:has(span)").eq(i).removeClass("yjl_noLine");
                }
                //通过重要性划分
                if ($("#d1 tbody tr:has(span)").eq(i).children().eq(1).html() != ($("#d1 tr:has(span)").eq(i + 1).children().eq(1).html() == "" ? $("#d1 tr:has(span)").eq(i).children().eq(1).html() : $("#d1 tr:has(span)").eq(i + 1).children().eq(1).html())) {
                    $("#d1 tbody tr:has(span)").eq(i).removeClass("yjl_noLine");
                }
            }
        }
        else { 
            $("#d1 tbody").html("<tr><td colspan=6>今日无财经数据公布！</td></tr>");
        } 
    }

    //事件
    var Eventtable = "";
    var FinancialEvent = jsonObj["List"][0]["FinancialEvent"];
    if (FinancialEvent != null) {
        for (var i = 0; i < FinancialEvent.length; i++) {
            Eventtable += "<tr><td class='ti'>" + FinancialEvent[i]["FinancialTime"] + "</td><td>" + FinancialEvent[i]["Area"] + "</td><td>" + FinancialEvent[i]["FinancialEvent"] + "</td></tr>";
        } 
        $("#d2 tbody").html(Eventtable);
    }
    else { 
        $("#d2 tbody").html("<tr><td colspan=3>今日无重要财经事件公布！</td></tr>");
    }


    //动态
    var CentralBankNewstable = "";
    var CentralBankNews = jsonObj["List"][0]["CentralBankNews"];
    if (CentralBankNews != null) {
        for (var i = 0; i < CentralBankNews.length; i++) {
            CentralBankNewstable += "<tr><td class='ti'>" + CentralBankNews[i]["FinancialTime"] + "</td><td>" + CentralBankNews[i]["Area"] + "</td><td>" + CentralBankNews[i]["FinancialEvent"] + "</td></tr>";
        } 
        $("#d3 tbody").html(CentralBankNewstable);
    }
    else { 
        $("#d3 tbody").html("<tr><td colspan=3>今日无重要央行动态公布！</td></tr>");
    }

    //假日
    var HolidayReporttable = "";
    var HolidayReport = jsonObj["List"][0]["HolidayReport"];
    if (HolidayReport != null) {
        for (var i = 0; i < HolidayReport.length; i++) {
            HolidayReporttable += "<tr>";
            HolidayReporttable += "<td>" + HolidayReport[i]["Area"] + "</td>";
            HolidayReporttable += "<td colspan=\"2\">" + HolidayReport[i]["FinancialEvent"] + "</td>";
            HolidayReporttable += "</tr>";
        } 
        $("#d4 tbody").html(HolidayReporttable);
    }
    else { 
        $("#d4 tbody").html("<tr><td colspan=3>今日无重要假期预告公布！</td></tr>");
    }
    
}
//拼html 
function SetHtml(jsonObj) {
    if (jsonObj == null) {
        return;
    } 
    var ftable = "";
    var FinancialCalendarData = jsonObj["List"][0]["FinancialCalendarData"];
    var x_str = "";
    if (FinancialCalendarData != null) {
        if (FinancialCalendarData.length > 0) {
            for (var i = 0; i < FinancialCalendarData.length; i++) {
                //重要性
                var x_connt = FinancialCalendarData[i]["Weightiness"]; 
                if (x_connt == "0")
                    x_str = "<b class=\"td_star2\"></b><b class=\"td_star2\"></b><b class=\"td_star2\"></b>";
                if (x_connt == "1")
                    x_str = "<b class=\"td_star\"></b><b class=\"td_star2\"></b><b class=\"td_star2\"></b>";
                if (x_connt == "2")
                    x_str = "<b class=\"td_star\"></b><b class=\"td_star\"></b><b class=\"td_star2\"></b>";
                if (x_connt == "3")
                    x_str = "<b class=\"td_star\"></b><b class=\"td_star\"></b><b class=\"td_star\"></b>";

                ftable += "<tr  onclick=\"ShowDetail(this,'" + FinancialCalendarData[i]["CountryCode"] + "','" + FinancialCalendarData[i]["ColumnCode"] + "','table_Search','" + i + "') \">";
                if (x_connt == "3") {
                    ftable += "<td class=\"yjl_caijingyueli_red ti\">" + FinancialCalendarData[i]["Time"] + "</td>";
                    ftable += "<td class=\"yjl_caijingyueli_red\">";
                    ftable += "" + x_str + "";
                    ftable += "</td>";
                    ftable += "<td class=\"yjl_caijingyueli_red\">";
                }
                else {
                    ftable += "<td>" + FinancialCalendarData[i]["Time"] + "</td>";
                    ftable += "<td>";
                    ftable += "" + x_str + "";
                    ftable += "</td>";
                    ftable += "<td align='left'>";
                }

                ftable += "<span>" + FinancialCalendarData[i]["CountryName"] + "</span>" + FinancialCalendarData[i]["Content"] + "";

                var Previous = FinancialCalendarData[i]["Previous"] == "" ? "&nbsp;" : FinancialCalendarData[i]["Previous"];
                var Predict = FinancialCalendarData[i]["Predict"] == "" ? "&nbsp;" : FinancialCalendarData[i]["Predict"];
                var CurrentValue = FinancialCalendarData[i]["CurrentValue"] == "" ? "&nbsp;" : FinancialCalendarData[i]["CurrentValue"];
                if (FinancialCalendarData[i]["Revised"] != "") {
                    ftable += "</td><td>" + Previous + "";
                    ftable += "<a style=\"display: none;width:10px\" class=yjl_caijingyueli_xiugai title=" + FinancialCalendarData[i]["Revised"] + " /></td>";
                } else
                    ftable += "</td><td>" + Previous + "</td>";
                ftable += "<td>" + Predict + "</td>";
                ftable += "<td>" + CurrentValue + "</td></tr>";
            }
            $("#d1").find("tbody").html(ftable);
        }else
        	{
        	$("#d1").find("tbody").html("<tr><td colspan=3>今日无重要假期预告公布！<td></tr>");
        	}
        
        
      //事件
        var Eventtable = "";
        var FinancialEvent = jsonObj["List"][0]["FinancialEvent"];
        if (FinancialEvent != null) {
            for (var i = 0; i < FinancialEvent.length; i++) {
                Eventtable += "<tr><td class='ti'>" + FinancialEvent[i]["FinancialTime"] + "</td><td>" + FinancialEvent[i]["Area"] + "</td><td>" + FinancialEvent[i]["FinancialEvent"] + "</td></tr>";
            }
           
            $("#d2").find("tbody").html(Eventtable);
        }
        else {
            $("#d2").find("tbody").html("<tr><td colspan=3>今日无重要财经事件公布！</td></tr>");
        }


        //动态
        var CentralBankNewstable = "";
        var CentralBankNews = jsonObj["List"][0]["CentralBankNews"];
        if (CentralBankNews != null) {
            for (var i = 0; i < CentralBankNews.length; i++) {
                CentralBankNewstable += "<tr><td class='ti'>" + CentralBankNews[i]["FinancialTime"] + "</td><td>" + CentralBankNews[i]["Area"] + "</td><td>" + CentralBankNews[i]["FinancialEvent"] + "</td></tr>";
            }
            $("#d3").find("tbody").html(CentralBankNewstable);
        }
        else {
            $("#d3").find("tbody").html("<tr><td colspan=3>今日无重要央行动态公布！</td></tr>");
        }

        //假日
        var HolidayReporttable = "";
        var HolidayReport = jsonObj["List"][0]["HolidayReport"];
        if (HolidayReport != null) {
            for (var i = 0; i < HolidayReport.length; i++) {
                HolidayReporttable += "<tr>";
                HolidayReporttable += "<td>" + HolidayReport[i]["Area"] + "</td>";
                HolidayReporttable += "<td colspan=\"2\">" + HolidayReport[i]["FinancialEvent"] + "</td>";
                HolidayReporttable += "</tr>";
            }
            $("#d4").find("tbody").html(HolidayReporttable);
        }
        else {
            $("#d4").find("tbody").html("<tr><td colspan=3>今日无重要假期预告公布！</td></tr>");
        }
    }
}
function CurrencyDate(tid, code) {
	for (var i = 1; i < 9; i++) {
        if (tid == i) {
            document.getElementById("guojia_" + i).className = "yjl_guojia_self_" + i;
        }
        else {
            document.getElementById("guojia_" + i).className = "yjl_guojia_" + i;
        }
    } 
    $.ajax({
        type: "get",
        async: false,
        url: dataurl + "/IDataPlatform/FinancialCalendarNew/IFinancialCalendarData.ashx",
        data: "user=" + datauser + "&beginDate=" + currdate + "&country=" + code + "&succ_callback=CallbackCurrencyContent&rd=" + (new Date()).valueOf(),
        dataType: "jsonp",
        jsonp: "callbackfun",
        jsonpCallback: CallbackCurrencyContent,
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            //alert(XMLHttpRequest.status);
            //alert(XMLHttpRequest.readyState);
            //alert(textStatus);
        }
    });
}
 
//货币筛选 财经内容
function CallbackCurrencyContent(jsonObj) {
    //回调json数据成功后的处理
	SetHtml(jsonObj);
} 
$(function(){
	SetContent($("#datetime").val());
	$("#showindex").find("li").click(function(){
		SetContent($(this).find("span").attr("ty"));
	});
});