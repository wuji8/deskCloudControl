$(function () {
    
    //二级联动
    $.post(el+"/getAllProject.action",function (res) {
    	var json=JSON.parse(res);
        if (json.data) {
        	if(Array.isArray(json.data)){
        		var currData = json.data, _html = "", firstCloudid = currData[0].cloudid;
        	}else{
        		var currData = new Array(), _html = "";
        		currData.push(json.data);
        		var firstCloudid = currData[0].cloudid
        		console.log(currData)
        	}
            
            for (var i = 0; i < currData.length; i++) {
                _html += "<option data-id='" + currData[i].cloudid + "' value='" + currData[i].cloudid + "'>" + currData[i].cloudname + "</option>";
            }
            $("#select-project").html(_html);
            getFirstTable(firstCloudid);
            setTimeout(function () {
                $(".btn-search").click();  // 初始数据表
            }, 800);
        } else if (json.code == 2) {
            $(".tip-content").text("登录超时，请点击跳转登录页面！");
            $("#smallModalClick").modal('show');
            $(".btn-queding").click(function () { location.href = "/Home/Login"; });
        } else {
            $("#smallModal").find(".tip-content").text(json.data);
            $("#smallModal").modal('show');
        }
    })
    $(".page-header").on("change", "#select-project", function () {
        var cloudid = "";
        $("#select-project option").each(function (i, o) {
            if ($(this).prop("selected")) {
                cloudid = $(this).val();
            }
        });
        getFirstTable(cloudid);
    });
    $(".page-header").on("change", "#select-table", function () {
        var tableid = "";
        $("#select-table option").each(function (i, o) {
            if ($(this).prop("selected")) {
                tableid = $(this).val();
                if ($(this).attr("data-delete") == 1) {
                    $(".btn-exclude").show();
                } else {
                    $(".btn-exclude").hide();
                }
            }
        });
        getFirstTableFields(tableid);
        $(".select-fields:gt(0)").remove();
        $(".fieldval:gt(0)").remove();
        setTimeout(function () {
            __index = $(".select-fields").eq(0).find("option").length - 2;
        }, 500)
    });

    //添加搜索字段
    setTimeout(function () {
        __index = $(".select-fields").eq(0).find("option").length - 2;
    }, 500)
    
    $(".btn-add-field").on("click", function () {
        console.log(__index);
        var cloneSelect = $(".select-fields").eq(0).clone();
        var cloneInput = $(".fieldval").eq(0).clone().val("");
        if(__index >= 0){
            $(".btn-add-field").before(cloneSelect);
            $(".btn-add-field").before(cloneInput);
        }
        __index--;
    });

    //查看数据表
    var totalPage = 0, currentPage = 0;
    $(".btn-search").on("click", function () {
        var cloudid = $("#select-project option:selected").val();
        var tableid = $("#select-table option:selected").val();
        $(".thead-box").html(""); //先清空
        $(".content-box").html("");
        $("#totalNum").text(0);
        var fieldObj = "", dataArr = [], keyArr = [];
        $(".select-fields").each(function (i, e) {
            keyArr.push($("option:selected", e).text());
        });
        var fromdata=new FormData();
        $(".fieldval").each(function (i, e) {
            fieldObj = '{ key: "' + keyArr[i] + '", val: "' + $(e).val().trim() + '" }';
//            dataArr.push(fieldObj);
           
            if($(e).val() == ""){
            	
            }else{
            	 fromdata.append("key",keyArr[i]);
            	fromdata.append("val",$(e).val().trim());
            }
            
        });
        //console.log(dataArr);
        //var key = $(".select-fields option:selected").text();  // ........................................
        //dataObj.key = key?key:"";
        //dataObj.key = fieldsArr;
        //dataObj.val = $("#fieldval").val();
        
        fromdata.append("tableid",tableid);
        
        fromdata.append("pindex","1");
        fromdata.append("pagesize","15");
        var xhr=new XMLHttpRequest();
        xhr.open("post",el+"/getTableData.action");
        xhr.setRequestHeader("contentType", "multipart/form-data");
        xhr.onreadystatechange=function () {
            if(xhr.readyState == 4 && xhr.status == 200){
            	var json=JSON.parse(xhr.responseText);
            	if (json.code == 1) {
                  var strList = randerList(json.data);
                  var dataLength = json.data.length;
                  $(".thead-box").append(strList.thead);
                  $(".content-box").append(strList.tbody);
                  $("#totalNum").text(json.Count);
                  totalPage = json.pageCount;
                  currentPage = 1;
                  $(".page-box").show();
                  //分页
                  $('#jqPage').jqPaginator({
                      totalPages: totalPage,
                      visiblePages: 5,
                      onPageChange: function (num, type) {
                          //翻页表
                          $(".thead-box").html(""); //先清空
                          $(".content-box").html("");
                          $.post("/api/console/getTableData", { token: token, tableid: $("#select-table option:selected").val(), search: JSON.stringify(dataArr), pindex: num, pagesize: 15 }, function (json) {
                              if (json.code == 1) {
                                  var strList = randerList(json.data);
                                  $(".thead-box").append(strList.thead);
                                  $(".content-box").append(strList.tbody);
                                  $("#totalNum").text(json.Count);
                                  totalPage = json.pageCount;
                                  currentPage = num;
                              } else if (json.code == 0) {
                                  $(".thead-box").html("");
                                  $(".content-box").html("");
                                  $(".content-box").append("<tr><td colspan='" + dataLength + "' class='text-center'>暂无数据</td></tr>");
                                  $(".page-box").hide();
                              } 
//                              else if (json.code == 2) {
//                                  $(".tip-content").text("登录超时，请点击跳转登录页面！");
//                                  $("#smallModalClick").modal('show');
//                                  $(".btn-queding").click(function () { location.href = "/Home/Login"; });
//                              }
                              else {
                                  $("#smallModal").find(".tip-content").text("错误!");
                                  $("#smallModal").modal('show');
                              }
                          })
                      },
                      first: '<li class="first" jp-role="first" jp-data="1"><a href="javascript:void(0);">首页</a></li>',
                      prev: '<li class="prev" jp-role="prev"><a href="javascript:void(0);"><i class="arrow arrow2"></i>上一页</a></li>',
                      next: '<li class="next" jp-role="next"><a href="javascript:void(0);">下一页<i class="arrow arrow3"></i></a></li>',
                      last: '<li class="last" jp-role="last" jp-data="{{totalPages}}"><a href="javascript:void(0);">末页</a></li>',
                      page: '<li class="page" jp-role="page"><a href="javascript:void(0);">{{page}}</a></li>'
                  });
  
              } else if (json.code == 0) {
                  $(".thead-box").html("");
                  $(".content-box").html("");
                  var _headHtml = "", _nhtml = "", tlength = json.head.length;
                  $(json.head).each(function (i, e) {
                      _nhtml += "<th>" + e.COLUMN_NAME + "</th>";
                  });
                  $(".thead-box").html("<tr>" + _nhtml + "</tr>");
                  $(".content-box").append("<tr><td colspan='" + tlength + "' class='text-center'>暂无数据</td></tr>");
                  $(".page-box").hide();
                  //$(".btn-loadmore").attr("disabled", "disabled");
              } else if (json.code == 2) {
                  $(".tip-content").text("登录超时，请点击跳转登录页面！");
                  $("#smallModalClick").modal('show');
                  $(".btn-queding").click(function () { location.href = "/Home/Login"; });
              } else {
                  $("#smallModal").find(".tip-content").text(json.data);
                  $("#smallModal").modal('show');
              }
            }
        };
        xhr.send(fromdata)
//        $.post(el+"/getTableData.action", {tableid: tableid, search: JSON.stringify(dataArr), pindex: 1, pagesize: 15 }, function (json) {
////        $.post(el+"/GetTableData.action", fromdata, function (json) {
//            if (json.code == 1) {
//                var strList = randerList(json.data);
//                var dataLength = json.data.length;
//                $(".thead-box").append(strList.thead);
//                $(".content-box").append(strList.tbody);
//                $("#totalNum").text(json.Count);
//                totalPage = json.pageCount;
//                currentPage = 1;
//                $(".page-box").show();
//                //分页
//                $('#jqPage').jqPaginator({
//                    totalPages: totalPage,
//                    visiblePages: 5,
//                    onPageChange: function (num, type) {
//                        //翻页表
//                        $(".thead-box").html(""); //先清空
//                        $(".content-box").html("");
//                        $.post("/api/console/getTableData", { token: token, tableid: $("#select-table option:selected").val(), search: JSON.stringify(dataArr), pindex: num, pagesize: 15 }, function (json) {
//                            if (json.code == 1) {
//                                var strList = randerList(json.data);
//                                $(".thead-box").append(strList.thead);
//                                $(".content-box").append(strList.tbody);
//                                $("#totalNum").text(json.Count);
//                                totalPage = json.pageCount;
//                                currentPage = num;
//                            } else if (json.code == 0) {
//                                $(".thead-box").html("");
//                                $(".content-box").html("");
//                                $(".content-box").append("<tr><td colspan='" + dataLength + "' class='text-center'>暂无数据</td></tr>");
//                                $(".page-box").hide();
//                            } 
////                            else if (json.code == 2) {
////                                $(".tip-content").text("登录超时，请点击跳转登录页面！");
////                                $("#smallModalClick").modal('show');
////                                $(".btn-queding").click(function () { location.href = "/Home/Login"; });
////                            }
//                            else {
//                                $("#smallModal").find(".tip-content").text("错误!");
//                                $("#smallModal").modal('show');
//                            }
//                        })
//                    },
//                    first: '<li class="first" jp-role="first" jp-data="1"><a href="javascript:void(0);">首页</a></li>',
//                    prev: '<li class="prev" jp-role="prev"><a href="javascript:void(0);"><i class="arrow arrow2"></i>上一页</a></li>',
//                    next: '<li class="next" jp-role="next"><a href="javascript:void(0);">下一页<i class="arrow arrow3"></i></a></li>',
//                    last: '<li class="last" jp-role="last" jp-data="{{totalPages}}"><a href="javascript:void(0);">末页</a></li>',
//                    page: '<li class="page" jp-role="page"><a href="javascript:void(0);">{{page}}</a></li>'
//                });
//
//            } else if (json.code == 0) {
//                $(".thead-box").html("");
//                $(".content-box").html("");
//                var _headHtml = "", _nhtml = "", tlength = json.head.length;
//                $(json.head).each(function (i, e) {
//                    _nhtml += "<th>" + e.COLUMN_NAME + "</th>";
//                });
//                $(".thead-box").html("<tr>" + _nhtml + "</tr>");
//                $(".content-box").append("<tr><td colspan='" + tlength + "' class='text-center'>暂无数据</td></tr>");
//                $(".page-box").hide();
//                //$(".btn-loadmore").attr("disabled", "disabled");
//            } else if (json.code == 2) {
//                $(".tip-content").text("登录超时，请点击跳转登录页面！");
//                $("#smallModalClick").modal('show');
//                $(".btn-queding").click(function () { location.href = "/Home/Login"; });
//            } else {
//                $("#smallModal").find(".tip-content").text(json.data);
//                $("#smallModal").modal('show');
//            }
//        })
    });

    //删除结果
    $(".btn-del-result").on('click', function () {
        //$(".tip-content").text("是否删除搜索结果？");
        //$("#smallModalClick").modal('show');
        //var tableid = $("#select-table option:selected").val();
        //$("#totalNum").text(0);
        //var fieldObj = "", dataArr = [], keyArr = [];
        //$(".select-fields").each(function (i, e) {
        //    keyArr.push($("option:selected", e).text());
        //});
        //$(".fieldval").each(function (i, e) {
        //    fieldObj = '{ "key": "' + keyArr[i] + '", "val": "' + $(e).val() + '" }';
        //    dataArr.push(fieldObj);
        //});
        //$(".btn-queding").click(function () {
        //    $(".btn-queding").attr('disabled','disabled');
        //    $(".thead-box").html(""); //先清空
        //    $(".content-box").html("");
        //    $.post("/api/console/DeleteSearchData", { token: token, tableid: tableid, search: JSON.stringify(dataArr) }, function (json) {
        //        $("#smallModalClick").modal('hide');
        //        if (json.code == 1) {
        //            $("#smallModal").find(".tip-content").text(json.data);
        //            $("#smallModal").modal('show');
        //            //reloadData();  //重载数据
        //            window.location.reload();
        //        } else if (json.code == 2) {
        //            $(".tip-content").text("登录超时，请点击跳转登录页面！");
        //            $("#smallModalClick").modal('show');
        //            $(".btn-queding").click(function () { location.href = "/Home/Login"; });
        //        } else {
        //            $("#smallModal").find(".tip-content").text(json.data);
        //            $("#smallModal").modal('show');
        //        }
        //        $(".btn-queding").removeAttr('disabled');
        //    });
        //});
        
        $("#myModifyModalLabel").modal('show');
    })
    $('.btn-passDel').on('click', function () {
        var pass = $("#login-pass").val().trim();
        var tableid = $("#select-table option:selected").val();
        $("#totalNum").text(0);
        var fieldObj = "", dataArr = [], keyArr = [];
        $(".select-fields").each(function (i, e) {
            keyArr.push($("option:selected", e).text());
        });
        $(".fieldval").each(function (i, e) {
            fieldObj = '{ "key": "' + keyArr[i] + '", "val": "' + $(e).val().trim() + '" }';
            dataArr.push(fieldObj);
        });
        if (pass) {
            $('.btn-passDel').attr('disabled', '');
            //$(".thead-box").html(""); //先清空
            //$(".content-box").html("");
            $.post("/api/console/DeleteSearchData", { token: token, tableid: tableid, search: JSON.stringify(dataArr), pass: pass }, function (json) {
                $("#myModifyModalLabel").modal('hide');
                if (json.code == 1) {
                    $("#smallModalClick").find(".tip-content").text(json.data);
                    $("#smallModalClick").modal('show');
                    //reloadData();  //重载数据
                    $(".btn-queding").click(function () { window.location.reload(); });
                } else if (json.code == 2) {
                    $(".tip-content").text("登录超时，请点击跳转登录页面！");
                    $("#smallModalClick").modal('show');
                    $(".btn-queding").click(function () { location.href = "/Home/Login"; });
                } else {
                    $("#smallModal").find(".tip-content").text(json.data);
                    $("#smallModal").modal('show');
                }
                $('.btn-passDel').removeAttr('disabled');
            });
        }
    });


    //添加数据弹框
    $(".btn-add").on("click", function () {
        $(this).attr('disabled','');
        var cloudid = $("#select-project option:selected").val();
        var tableid = $("#select-table option:selected").val();
        //$(".thead-box").html(""); //先清空
        //$(".content-box").html("");
        //window.location.href = "/Home/BatchAdd?cloudid=" + cloudid + "&tableid=" + tableid;
        $.post(el+"/getTableFiled.action", { tableId: tableid }, function (res) {
        	var json=JSON.parse(res);
            if (json.data) {
                addDatas(json.data);
            } 
//            else if (json.code == 0) {
//                addDatas(json.data);
//            } 
//            else if (json.code == 2) {
//                $(".tip-content").text("登录超时，请点击跳转登录页面！");
//                $("#smallModalClick").modal('show');
//                $(".btn-queding").click(function () { location.href = "/Home/Login"; });
//            } 
            else {
                $("#smallModal").find(".tip-content").text("错误！");
                $("#smallModal").modal('show');
            }
            $(".btn-add").removeAttr('disabled');
        })
    });

    //批量添加
    $(".btn-batch-add").on("click", function () {
        var cloudid = $("#select-project option:selected").val();
        var tableid = $("#select-table option:selected").val();
        window.location.href = el+"/Views/BatchAdd.html?cloudid=" + cloudid + "&tableid=" + tableid;
    });
    //批量修改
    $(".btn-batch-modify").on("click", function () {
        var cloudid = $("#select-project option:selected").val();
        var tableid = $("#select-table option:selected").val();
        window.location.href = el+"/Views/BatchModify.html?cloudid=" + cloudid + "&tableid=" + tableid;
    });

    //确认添加字段数据
    $(".btn-confirmadd").on('click', function () {
        $(this).attr('disabled','');
        var arr = [], dataJson = {};
        $(".field-form .form-group").each(function (i, e) {
            var key = $(".control-label", e).text();
            dataJson[key] = $("input.form-control", e).val().trim();
        });
        console.log(dataJson);
        var tableid = $("#select-table option:selected").val();
        $.post(el+"/addTableData.action", { tableid: tableid, fields: JSON.stringify(dataJson) }, function (res) {
        	var json=JSON.parse(res);
        	
            $("#myAddModalLabel").modal('hide');
            if (json.code == 1) {
                $(".btn-search").click();
                $("#smallModal").find(".tip-content").text("成功！");
                $("#smallModal").modal('show');
            } 
//            else if (json.code == 2) {
//                $(".tip-content").text("登录超时，请点击跳转登录页面！");
//                $("#smallModalClick").modal('show');
//                $(".btn-queding").click(function () { location.href = "/Home/Login"; });
//            } 
            else {
                $("#smallModal").find(".tip-content").text("失败！");
                $("#smallModal").modal('show');
            }
            $(".btn-confirmadd").removeAttr('disabled');
        })
    });

    // 修改数据弹框
    var rowID = 0;
    $(".table-devices").on("click", ".btn-modify", function () {
        $("#myOnlyModifyModalLabel").modal('show');
//        rowID = $(this).parent().parent().find("td").eq(1).text();
        var that = $(this);
        //获取类型
        var typeArr = [];
        var tableid = $("#select-table option:selected").val();
        $.post(el+"/getTableFiled.action", {tableId: tableid }, function (res) {
        	var json=JSON.parse(res);
            if(json.data){
                $(json.data).each(function (i, e) {
                    //console.log(e.DATA_TYPE);
                    typeArr.push(e.DATA_TYPE);
                });

                //遍历fields
                var _html = "";
                $(".table .thead-box tr th").each(function (i, elem) {
                    if (i >= 1 && i < $(".table .thead-box tr th").length - 1) {
                        var title = $(elem).text();
                        if(title == "id"){
                        	rowID=$(that).parent().parent().find("td").eq(i).text();
                        }else{
                        	if (typeArr[i - 1] == 'int') {
                                _html += '<div class="form-group">' +
                                   '<label for="recipient-name" class="control-label col-xs-2">' + title + '</label>' +
                                   '<div class="col-xs-10">' +
                                       '<input type="number" step="1" class="form-control filed-value-modify" id="" value="' + $(that).parent().parent().find("td").eq(i).text() + '" placeholder="">' +
                                   '</div></div>';
                            } else if (typeArr[i - 1] == 'datetime') {
                                _html += '<div class="form-group">' +
                                   '<label for="recipient-name" class="control-label col-xs-2">' + title + '</label>' +
                                   '<div class="col-xs-10">' +
                                       '<input type="text" class="form-control filed-value-modify" id="" value="' + $(that).parent().parent().find("td").eq(i).text() + '" placeholder="">' +
                                   '</div></div>';
                            } else if (typeArr[i - 1] == 'decimal') {
                                _html += '<div class="form-group">' +
                                    '<label for="recipient-name" class="control-label col-xs-2">' + title + '</label>' +
                                    '<div class="col-xs-10">' +
                                        '<input type="number" class="form-control filed-value-modify" id="" value="' + $(that).parent().parent().find("td").eq(i).text() + '" placeholder="">' +
                                    '</div></div>';
                            } else {
                                _html += '<div class="form-group">' +
                                    '<label for="recipient-name" class="control-label col-xs-2">' + title + '</label>' +
                                    '<div class="col-xs-10">' +
                                        '<input type="text" class="form-control filed-value-modify" id="" value="' + $(that).parent().parent().find("td").eq(i).text() + '" placeholder="">' +
                                    '</div></div>';
                            }
                        }
                        //console.log(typeArr[i-1]);
                        
                    }
                })
                $('.field-modify-only').html(_html);
            }
        })
    });

    //确认修改字段数据
    $(".btn-confirmmodify").on('click', function () {
        var arr = [], dataJson = {};
        $(".field-modify-only .form-group").each(function (i, e) {
            var key = $(".control-label", e).text();
            dataJson[key] = $("input.form-control", e).val().trim();
        });
        console.log(dataJson);
        var tableid = $("#select-table option:selected").val();
        $.post(el+"/updateTableData.action", {  tableid: tableid, id: rowID, fields: JSON.stringify(dataJson) }, function (res) {
        	var json=JSON.parse(res);
            $("#myOnlyModifyModalLabel").modal('hide');
            if (json.code == 1) {
                $(".btn-search").click();
                $("#smallModal").find(".tip-content").text("修改成功！");
                $("#smallModal").modal('show');
            } 
//            else if (json.code == 2) {
//                $(".tip-content").text("登录超时，请点击跳转登录页面！");
//                $("#smallModalClick").modal('show');
//                $(".btn-queding").click(function () { location.href = "/Home/Login"; });
//            } 
            else {
                $("#smallModal").find(".tip-content").text("错误！");
                $("#smallModal").modal('show');
            }
        })
    });

    //导出
    $(".btn-excel").on("click", function () {
        var cloudid = $("#select-project option:selected").val();
        var tableid = $("#select-table option:selected").val();
        var fieldObj = "", dataArr = [], keyArr = [];
        $(".select-fields").each(function (i, e) {
            keyArr.push($("option:selected", e).text());
        });
        $(".fieldval").each(function (i, e) {
            fieldObj = '{ "key": "' + keyArr[i] + '", "val": "' + $(e).val().trim() + '" }';
            dataArr.push(fieldObj);
        });
        $.post("/api/console/ExportTableData", { token: token, tableid: tableid, search: JSON.stringify(dataArr) }, function (json) {
            if (json.code == 1) {
                downloadFile(json.data);
            } else if (json.code == 0) {
                $(".thead-box").html("");
                $(".content-box").html("");
                var _headHtml = "", _nhtml = "", tlength = json.head.length;
                $(json.head).each(function (i, e) {
                    _nhtml += "<th>" + e.COLUMN_NAME + "</th>";
                });
                $(".thead-box").html("<tr>" + _nhtml + "</tr>");
                $(".content-box").append("<tr><td colspan='" + tlength + "' class='text-center'>暂无数据</td></tr>");
                $(".page-box").hide();
                //$(".btn-loadmore").attr("disabled", "disabled");
            } else if (json.code == 2) {
                $(".tip-content").text("登录超时，请点击跳转登录页面！");
                $("#smallModalClick").modal('show');
                $(".btn-queding").click(function () { location.href = "/Home/Login"; });
            } else {
                $("#smallModal").find(".tip-content").text(json.data);
                $("#smallModal").modal('show');
            }
        })
    });

    //清除已删除的表
    $(".btn-exclude").on('click', function () {
        $(".tip-content").text("是否删除已删除的表？");
        $("#smallModalClick").modal('show');
        $(".btn-queding").click(function () {
            $("#smallModalClick").modal('hide');
            var tableid = $("#select-table option:selected").val();
            $.post("/api/console/DeleteTable", { token: token, tableid: tableid }, function (json) {
                if (json.code == 1) {
                    alert(json.data);
                    window.location.reload();
                } else if (json.code == 2) {
                    $(".tip-content").text("登录超时，请点击跳转登录页面！");
                    $("#smallModalClick").modal('show');
                    $(".btn-queding").click(function () { location.href = "/Home/Login"; });
                } else {
                    $("#smallModal").find(".tip-content").text(json.data);
                    $("#smallModal").modal('show');
                }
            });
        })
    });

})


//获取初始第一个项目的选择表
function getFirstTable(cloudid) {
    $.post(el+"/getUserTable.action", {  cloudId: cloudid }, function (res) {
    	var json=JSON.parse(res);
        if (json.data) {
            var tableData = json.data, _html = "", firstTableId = json.data[0].tableid;
            getFirstTableFields(firstTableId);
            for (var i = 0; i < tableData.length; i++) {
                if (tableData[i].deleted == 1) {
                    var txt = "(已删除)";
                    var time = "["+tableData[i].deletetime+"]";
                } else {
                    var txt = "";
                    var time = "";
                }
                _html += "<option value=" + tableData[i].tableid + " data-delete='" + tableData[i].deleted + "'>" + tableData[i].tablename + txt + time + "</option>";
            }
            if (tableData[0].deleted == 1) {
                $(".btn-exclude").show();
            }
            $("#select-table").html(_html);
        } else if (json.code == 2) {
            $(".tip-content").text("错误！");
            $("#smallModalClick").modal('show');
//            $(".btn-queding").click(function () { location.href = "/Home/Login"; });
        } 
//        else {
//            $("#smallModal").find(".tip-content").text(json.data);
//            $("#smallModal").modal('show');
//        }
    })
}
//初始第一个表的字段
function getFirstTableFields(tableid) {
    $.post(el+"/getTableFiled.action", { tableId: tableid}, function (res) {
    	var json=JSON.parse(res);
        if (json.data) {
            var _html = "";
            $(json.data).each(function (i, e) {
                if(e.columnName == "id"){
                    
                }else{
                	_html += '<option value="' + e.dataType + '">' + e.columnName + '</option>';
                }
            });
            $(".select-fields").html(_html);     // ........................................
        }
        else if (json.code == 2) {
            $(".tip-content").text("错误！");
            $("#smallModalClick").modal('show');
            $(".btn-queding").click(function () { location.href = "/Home/Login"; });
        } 
//        else {
//            $("#smallModal").find(".tip-content").text(json.data);
//            $("#smallModal").modal('show');
//        }
    })
    
}

//渲染列表数据
function randerList(datalist) {
    var thhtml = "", tbhtml = "", nthhtml = "", ntbhtml = "", obj = {};
    if (datalist) {
        $(datalist).each(function (i, elem) {
            ntbhtml = "";
            for (var key in elem) {
                if(key != "UserID"){
                    ntbhtml += '<td>' + elem[key] + '</td>';
                }
            }
            tbhtml += '<tr><td><input type="checkbox" name="checkbox" class="checkboxOnly" value=""></td>' + ntbhtml + '<td><button type="button" class="btn btn-primary btn-minier btn-modify">修改</button></td></tr>';
        });
        for (var k in datalist[0]) {
        	console.log(k)
            if(k != "UserID"){
                nthhtml += '<th>' + k + '</th>';
            }
        }
        thhtml += '<tr><th><input type="checkbox" name="checkboxAll" id="checkboxAll" value=""></th>' + nthhtml + '<th>操作</th></tr>';

        obj.thead = thhtml;
        obj.tbody = tbhtml;
        return obj;
    }
}


function deleteDatas() {
    //批量单个删除设备
    var delDeviceArr = [];
    var delDevice = "";
    var index;
    $(".thead-box").children().find("th").each(function(i,e){
    	if($(e).text() == "id"){
    		index=i;
    	}
    })
    $(".checkboxOnly").each(function (i, e) {
        if ($(e).prop("checked")) {
            //console.log($(this).parent().next().text());
            delDeviceArr.push($(this).parent().next().text());
            delDevice += $(this).parent().parent().find("td").eq(index).text() + ",";
        }
    });
    var s=delDevice.substring(0,delDevice.length-1);
    if (delDeviceArr.length) {
        $(".tip-content").text("是否删除所选数据？");
        $("#smallModalClick").modal('show');
        $(".btn-queding").click(function () {
            $(".btn-queding").attr('disabled','');
            $.post(el+"/delData.action", { tableid: $("#select-table option:selected").val(), id: s }, function (res) {
            	var json=JSON.parse(res);
                $("#smallModalClick").modal('hide');
                if (json.code == 1) {
                    $("#smallModal").find(".tip-content").text("成功！");
                    $("#smallModal").modal('show');
                    //reloadData();  //重载数据
                    window.location.reload();
                } 
//                else if (json.code == 2) {
//                    $(".tip-content").text("登录超时，请点击跳转登录页面！");
//                    $("#smallModalClick").modal('show');
//                    $(".btn-queding").click(function () { location.href = "/Home/Login"; });
//                } 
                else {
                    $("#smallModal").find(".tip-content").text("失败！");
                    $("#smallModal").modal('show');
                }
                $(".btn-queding").removeAttr('disabled');
            });
        });
    } else {
        $("#smallModal").find(".tip-content").text("请先选择数据！");
        $("#smallModal").modal('show');
    }
}

function addDatas(data) {
    $("#myAddModalLabel").modal('show');
    var _html = '';
    $(data).each(function (i, elem) {
        if (i > 0) {
            if (elem['DATA_TYPE'] == 'int') {
                _html += '<div class="form-group">' +
                    '<label for="recipient-name" class="control-label col-xs-2">' + elem['columnName'] + '</label>' +
                    '<div class="col-xs-10">' +
                        '<input type="number" step="1" class="form-control filed-value" data-type="' + elem['dataType'] + '" id="" value="" placeholder="">' +
                    '</div></div>';
            } else if (elem['DATA_TYPE'] == 'datetime') {
                _html += '<div class="form-group">' +
                    '<label for="recipient-name" class="control-label col-xs-2">' + elem['columnName'] + '</label>' +
                    '<div class="col-xs-10">' +
                        '<input type="date" class="form-control filed-value" data-type="' + elem['dataType'] + '" id="" value="" placeholder="">' +
                    '</div></div>';
            } else if (elem['DATA_TYPE'] == 'decimal') {
                _html += '<div class="form-group">' +
                    '<label for="recipient-name" class="control-label col-xs-2">' + elem['columnName'] + '</label>' +
                    '<div class="col-xs-10">' +
                        '<input type="number" class="form-control filed-value" data-type="' + elem['dataType'] + '" id="" value="" placeholder="">' +
                    '</div></div>';
            } else {
                _html += '<div class="form-group">' +
                    '<label for="recipient-name" class="control-label col-xs-2">' + elem['columnName'] + '</label>' +
                    '<div class="col-xs-10">' +
                        '<input type="text" class="form-control filed-value" data-type="' + elem['dataType'] + '" id="" value="" placeholder="">' +
                    '</div></div>';
            }
        }
    });
    $('.field-form').html(_html);
}


//操作后重载数据
//function reloadData() {
//    $(".content-box").html("");
//    randerPage();
//}


//导出数据
function downloadFile(url) {
    try {
        var elemIF = document.createElement("iframe");
        elemIF.src = url;
        elemIF.style.display = "none";
        document.body.appendChild(elemIF);
    } catch (e) {
        alert("下载异常！");
    }
}
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