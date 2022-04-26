//var OpenApiHostUrl = "http://www.ngis.co.kr/ict";
//var OpenApiHostUrl = "http://meisgis.ngis.co.kr/ict";
//var OpenApiHostUrl = "http://116.124.251.51:81/ict";
//var OpenApiHostUrl = "http://smartfarm.gthings.co.kr/ict";
//var OpenApiHostUrl_raw = "http://www.ngis.co.kr";
//var OpenApiHostUrl = "http://192.168.0.101/ict";
//var OpenApiHostUrl = "http://116.124.251.51:82/ict";

//var OpenApiHostUrl = "http://rin-main.iptime.org:8080/ict";
//var OpenApiHostUrl = "http://rin-main.iptime.org:8081/ict";
//var OpenApiHostUrl = "http://rin-main.iptime.org:8082/ict";
//var OpenApiHostUrl = "http://192.168.0.101/ict";

//var OpenApiHostUrl = "http://rin-main.iptime.org:8083/ict";
//var OpenApiHostUrl = "http://192.168.0.16/ict";
//var OpenApiHostUrl = "http://localhost/ict";
var OpenApiHostUrl = "http://tns.tenefarm.com/ict";
//var OpenApiHostUrl = "http://192.168.0.201/ict";
//var OpenApiHostUrl = "http://tene-sub.iptime.org/ict";

var blight = new function() {
	/*
	 * Post 방식으로 데이터베이스에 Insert
	 * 인자
	 * params : json
	 * callbackfunc : function(result) {.......} 데이터베이스에 Insert 후 호출한 페이지로 결과 전달
	 */
	this.create = function(params, callbackfunc) {
		//console.log(params);
		//console.log($( "#" + params.formName ).serializeArray());
		
		//$.post( OpenApiHostUrl + "/" + params.formURL, $( "#" + params.formName ).serializeArray(), function(result) {
		$.post( params.formURL, $( "#" + params.formName ).serializeArray(), function(result) {
			
			if (callbackfunc != undefined)
				callbackfunc(result);
			else {
				if (result.status == "success")
					alert("정상적으로 입력되었입니다.");
				else {
					alert(result.status + "\n" + result.message);
				}
			}
		},"json");
	};
	
	
	/*
	 * Post 방식으로 데이터베이스에 update
	 * 인자
	 * params : json
	 * callbackfunc : function(result) {.......} 데이터베이스에 update 후 호출한 페이지로 결과 전달
	 */
	this.update = function(params, callbackfunc) {
		$.post( params.formURL, $( "#" + params.formName ).serializeArray(), function(result) {
		//$.post( OpenApiHostUrl + "/" + params.formURL, $( "#" + params.formName ).serializeArray(), function(result) {
			
			if (callbackfunc != undefined)
				callbackfunc(result);
			else {
				if (result.status == "success")
					alert("정상적으로 입력되었입니다.");
				else {
					alert(result.status + "\n" + result.message);
				}
			}
		},"json");
	};
	
	this.update_get = function(params, callbackfunc, remainprocess) {
		//console.log(params);
		//$.get( OpenApiHostUrl + "/" + params.formURL, params, function(result) {
		$.get( OpenApiHostUrl + "/" + params.formURL, params, function(result) {
			if (callbackfunc != undefined)
				callbackfunc(result, params, remainprocess);
			else {
				if (result.status == "success")
					alert("정상적으로 선택되었입니다.");
				else {
					alert(result.status + "\n" + result.message);
				}
			}
		},"json");
		
		
	};
	
	
	/*
	 * Post 방식으로 데이터베이스에 delete
	 * 인자
	 * params : json
	 * callbackfunc : function(result) {.......} 데이터베이스에 delete 후 호출한 페이지로 결과 전달
	 */
	this.del = function(params, callbackfunc) {
		
		$.post( params.formURL, $( "#" + params.formName ).serializeArray(), function(result) {
			
			if (callbackfunc != undefined)
				callbackfunc(result);
			else {
				if (result.status == "success")
					alert("정상적으로 삭제되었입니다.");
				else {
					alert(result.status + "\n" + result.message);
				}
			}
		},"json");
	};
	
	
	
	/*
	 * Post 방식으로 데이터베이스에 Select
	 * 인자
	 * params : json
	 * callbackfunc : function(result) {.......} 데이터베이스에 Select 후 호출한 페이지로 결과 전달
	 */
	this.read_post = function(params, callbackfunc) {
		//console.log($( "#" + params.formName ).serializeArray());
		$.post( params.formURL, $( "#" + params.formName ).serializeArray(), function(result) {
			
			if (callbackfunc != undefined)
				callbackfunc(result);
			else {
				if (result.status == "success")
					alert("정상적으로 선택되었입니다.");
				else {
					alert(result.status + "\n" + result.message);
				}
			}
		},"json");
	};
	
	
	/*
	 * Get 방식으로 데이터베이스에 Select
	 * 인자
	 * params : json
	 * callbackfunc : function(result) {.......} 데이터베이스에 Select 후 호출한 페이지로 결과 전달
	 */
	this.read_get = function(params, callbackfunc, remainprocess) {
		//console.log(params);
		//$.get( OpenApiHostUrl + "/" + params.formURL, params, function(result) {
		$.get( OpenApiHostUrl + "/" + params.formURL, params, function(result) {
			if (callbackfunc != undefined)
				callbackfunc(result, params, remainprocess);
			else {
				if (result.status == "success")
					alert("정상적으로 선택되었입니다.");
				else {
					alert(result.status + "\n" + result.message);
				}
			}
		},"json");
		
		
	};
	
	this.getTableData = function(formName, callbackfunc) {
		
		var params = {
				formName: formName,
				formURL: OpenApiHostUrl + "/datatable"
		};
		
		
		this.read_post(params, callbackfunc);
		
		return;
	};
	
	this.dupcheck = function(formName, callbackfunc) {
		
		$.post( OpenApiHostUrl + "/process/dupcheck", $( "#" + formName ).serializeArray(), function(result) {
			
			if (callbackfunc != undefined)
				callbackfunc(result);
			else {
				if (result.status == "success")
					alert("정상적으로 입력되었입니다.");
				else {
					alert(result.status + "\n" + result.message);
				}
			}
		},"json");
		
		return;
	};
	
	this.login = function(formName, callbackfunc) {
		
		//console.log($( "#" + formName ).serializeArray());
		
		//console.log(OpenApiHostUrl + "/process/login");
		
		//return;

		
		$.post( OpenApiHostUrl + "/process/login", $( "#" + formName ).serializeArray(), function(result) {
			//console.log(result);
			
			if (callbackfunc != undefined)
				callbackfunc(result);
			else {
				if (result.status == "success")
					alert("정상적으로 입력되었입니다.");
				else {
					alert(result.status + "\n" + result.message);
				}
			}
		},"json");
		
		return;
	};
	
	this.logout = function(formName, callbackfunc) {
		$.post( OpenApiHostUrl + "/process/logout", $( "#" + formName ).serializeArray(), function(result) {
			
			if (callbackfunc != undefined)
				callbackfunc(result);
			else {
				if (result.status == "success")
					alert("정상적으로 입력되었입니다.");
				else {
					alert(result.status + "\n" + result.message);
				}
			}
		},"json");
		
		return;
	};
	
	this.leave = function() {
		
		$.post( OpenApiHostUrl + "/process/leave","", function(result) {
			
			
		},"json");
		
		return;
	};
	
	
	this.search = function(params, callbackfunc) {
		
		this.getTableData(params.formName,function(result) {
			
			//console.log(result);
			
			if (eval(result.status)) {
				if ($("#" + params.formName + "notfind") != undefined)
					$("#" + params.formName + "notfind").remove();
				
				params.pagenum = result.pagenum;
				params.numrow = result.numrow;
				params.totalcnt = result.totalcnt;
				
				//console.log(params);
				
				window[callbackfunc](result.data, params);	

				
			} else {
				if ($("#" + params.formName + "notfind") != undefined)
					$("#" + params.formName + "notfind").remove();
				
				$("#" + params.paging).empty();
				$("#" + params.tbody_id).empty();
	    		
	    		
	    		$("#" + params.totalcnttag).text("0 건");
	    		$("#" + params.curpage).text("0");
	    		
	    		
	    		
	    		var tbody =  $("#" + params.tbody_id);
	    		
	    		
	    		var tr = $("<tr>").addClass("center").appendTo(tbody);
	    		var td_notfind = $("<td>").addClass("center").appendTo(tr);
	    		td_notfind.attr("colspan",params.colinfo.length);
	    		
	    		
	    		
	    		var label = $("<label>").text(result.message).attr("id",params.formName + "notfind").appendTo(td_notfind);
	    		
	    		
			}
		});
		
		return;
	};
	
	
	this.check_required = function(formName) {
		
		var frm = $('form[name="' + formName + '"]');
		var required_list = $(frm).find(".required");
		
		var ret = true;
		
		$.each(required_list, function( index, data ) {
			if ($(data).val() == "") {
				alert($(data).attr("placeholder") + "을(를) 입력하십시요.");
				$(data).focus();
				ret = false;
				return false;
				
			}
		});
		
		return ret;
	};
}