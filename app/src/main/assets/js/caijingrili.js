var sjHtml = '';
var hqHtml = '';
var gb = true;
$(document).ready(function () {
    sjHtml = $("#LogoHotkeyword1_search1_txtSjHtml").val();
    hqHtml = $("#LogoHotkeyword1_search1_txtHqHtml").val();
});

$(function () {
    $("#textfield").click(function () {
        if ($("#textfield").val() == "请输入...") {
            $(this).val("").focus();
        }
        var n = $("#searchselect").val();
        if (n == 1) {
            $("#textfield").css("color", "black");
            $("#keyslist").css("display", "none");
        }

    });
    $("#keyslist").mouseover(function () {
        gb = false;
    });
    $("#keyslist").mouseout(function () {
        gb = true;
    });
    $("#textfield").blur(function () {
        if (gb) {
            $("#keyslist").css("display", "none");
        }
        if ($("#textfield").val() == "") {
            $("#textfield").val("请输入...");
        }
        if ($("#textfield").val() == "请输入...") {
            $("#textfield").css("color", "#777");
        }
    });
    $("#btnSubmint").click(function () {
        if ($("#searchselect").val() == 1 && $("#textfield").val() != '' && $("#textfield").val() != '请输入...' && $("#textfield").val() != '请输入...') {
            var MainPath = $("#LogoHotkeyword1_search1_txtMainPath").val();
            var url = MainPath + "search.aspx?key=" + encodeURIComponent($("#textfield").val());
            //var newwin = 
            window.open(url);
            //newwin.location.href = url;
            $.ajax({
                type: "get",
                cache: false,
                url: "/handler/AddSearchWord.ashx?sword=" + escape($("#textfield").val())
            });
        }
    });
    $("#keyslist").mouseup(function () {
        $("#keyslist").css("display", "none");
    });
    $("#searchselect").change(function () {
        var n = $("#searchselect").val();

        if (n == 1) {
            $("#textfield").removeAttr("disabled");
            $("#textfield").css("color", "black");
            $("#keyslist").css("display", "none");
            if ($("#textfield").val() == "") {
                $("#textfield").val("请输入...");
            }
            if ($("#textfield").val() == "请输入...") {
                $("#textfield").css("color", "#777");
            }
        }
        else if (n == 2) {
            if (hqHtml != '') {
                $("#textfield").val("");
                $("#textfield").attr("disabled", true);
                $("#keyslist").css("display", "block");
                $("#keyslist").html(hqHtml);
            }
        }
        else if (n == 3) {
            if (sjHtml != '') {
                $("#textfield").val("");
                $("#textfield").attr("disabled", true);
                $("#keyslist").css("display", "block");
                $("#keyslist").html(sjHtml);
            }
        }
    });
});

//    function show(event) {
//        var ev = event || window.event;
//        if (ev.keyCode == 13) {
//            if (event.srcElement == document.getElementById("txtkw")) {
//                
//            }
//            if (event.srcElement == document.getElementById("textfield")) { 
//                $("#btnSubmint").click();
//            }
//            
//        }
//    }

//回车键对应的ASCII是13，Tab对应的是9
$(document).keydown(function () {
    if (event.keyCode == 13) {
        if (event.srcElement == document.getElementById("txtkw")) {
            $("#code").val("calendar");
        }
        if (event.srcElement == document.getElementById("textfield")) {
            $("#code").val("news");
            $("#btnSubmint").click();
            return false;
        }
    }
})

//键盘按下事件   
// document.onkeydown = show;   