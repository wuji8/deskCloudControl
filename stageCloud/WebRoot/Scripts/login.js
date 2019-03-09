$(function(){
    $(document).on('click', '.toolbar a[data-target]', function (e) {
        e.preventDefault();
        var target = $(this).data('target');
        $('.widget-box.visible').removeClass('visible');//hide others
        $(target).addClass('visible');//show target
    });
    //login
//    if (!parseInt(access)) {
//        console.log(access);
//        $("#authModal").modal('show');
//        $(".main-container").hide();
//    }

    $(".btn-login").on("click", function () {
        $(this).attr('disabled','');
        var username = $("#username").val().trim();
        var password = $("#password").val().trim();
        var obj = {
           /* geetest_challenge: $("input[name=geetest_challenge]").val(),
            geetest_validate: $("input[name=geetest_validate]").val(),
            geetest_seccode: $("input[name=geetest_seccode]").val(),*/
        		userName: username,
          /*  cuid: cuid,*/
        		userPwd: password
        };

        if (!username) {
            $(".tisi").text("请正确输入用户名！");
        } else if (!password) {
            $(".tisi").text("请正确输入密码！");
        } else {
            $(".btn-login").attr("disabled", "disabled").find("span").text("登录中");
            $.post(el+"/toLogin.action", obj, function (res) {
                $(".btn-login").removeAttr("disabled").find("span").text("登录");
                var json=JSON.parse(res)
                if (json.code == 1) {
//                    $("#smallModal").modal('show');
                    setTimeout(function () { window.location.href =el+ '/Views/Index.html' }, 100);
                } else if (json.code == 4) {
                alert("你还没项目，不能登录！")
              }else {
                    $(".tisi").text(json.data);
                }
                $(".btn-login").removeAttr('disabled');
            });
        }
        $(".btn-login").removeAttr('disabled');
    });
})
 var el=getRootPath();
function getRootPath(){
    var curWwwPath=window.document.location.href;
    var pathName=window.document.location.pathname;
    var pos=curWwwPath.indexOf(pathName);
    var localhostPaht=curWwwPath.substring(0,pos);
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    return(localhostPaht+projectName);
//    return(localhostPaht);

}   