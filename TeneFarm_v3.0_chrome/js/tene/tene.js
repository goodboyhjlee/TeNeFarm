
let serverInfos = {};

class Queue {
  constructor() {
    this._arr = [];
  }
  enqueue(item) {
    this._arr.push(item);
  }
  dequeue() {
    return this._arr.shift();
  }
}

var ThreeDSmartFarm = function(info, tb) {
	//console.log(info);
	this.apikey = "361f24eeee31e8841d2a6847d16ea53a";
	//this.apikey = "6727750ec2331704a8f377e90bc712d6";
	Kakao.init(this.apikey);
	console.log(Kakao.isInitialized());
	//Kakao.isInitialized();

	//this.info = info;

	this.br_outtemp = 0;
	this.br_outhum = 0;
	this.br_outwindspeed = 0;
	this.br_outwinddir = 0;
	this.br_raindrop = "--";

//	this.br_temp = 0;
//	this.br_hum = 0;
	

  

/*
  this.recognize = function() {
  	let kakaourl = "https://kakaoi-newtone-openapi.kakao.com/v1/recognize";
		

		var xhr = new XMLHttpRequest();
		xhr.open('POST', kakaourl, true);
		xhr.setRequestHeader("Transfer-Encoding", "chunked");
		xhr.setRequestHeader("Content-type", "application/octet-stream");
		xhr.setRequestHeader("Authorization", "KakaoAK " + apikey);
		//xhr.responseType = 'blob';
		xhr.onreadystatechange = function() { 
	        if (xhr.status == 200 ) {
	        	console.log(xhr.responseText);
	        } 
	   };
	   xhr.send("--data-binary @heykakao.wav");
  };
*/
  

	
	//console.log(info);
	this.user = new user_base(info, tb);


 	
 	
 	let ptz = function(data, cmd) {
 		let ip = data.attr("data-ip");
 		let port = data.attr("data-port");
		let chanel = data.attr("data-chanel");
		let user = data.attr("data-user");
		let pw = data.attr("data-pw");

		$.ajax({
		    url : "http://" + ip + ":" + port +"/ptz?channel=" + chanel + "&cmd="+cmd,
		    type : "GET",
		    headers: {
			    "Authorization": "Basic " + btoa(user + ":" + pw)
			  },
		    success: function(result) {
		        //console.log( result );
		    }
		});
 	};

 	let preset = function(data) {
 		let ip = data.attr("data-ip");
 		let port = data.attr("data-port");
		let chanel = data.attr("data-chanel");
		let user = data.attr("data-user");
		let pw = data.attr("data-pw");
		let preset = data.val();

		$.ajax({
			url : "http://" + ip + ":" + port + "/ptz?channel=" + chanel + "&cmd=7&presetNo=" + preset,
		    type : "GET",
		    headers: {
			    "Authorization": "Basic " + btoa(user + ":" + pw)
			  },
		    success: function(result) {
		        //console.log( result );
		    }
		});
 	};

 	

 	


	$("#zoomin").on('mousedown', function(e) {  
		ptz($(this),1);
  	});

  	$("#zoomout").on('mousedown', function(e) {     
	  	ptz($(this),2);
  	});

  	$("#panleft").on('mousedown', function(e) {    
	  	ptz($(this),3);
  	});


  	$("#panright").on('mousedown', function(e) {    
	  	ptz($(this),4);
  	});


  	$("#panup").on('mousedown', function(e) {    
	  	ptz($(this),5);
  	});


  	$("#pandown").on('mousedown', function(e) {    
	  	ptz($(this),6);
  	});


  	$("#preset").on('change', function(e) {    
  		preset($(this));
	 });

  let onReceiveMap = {};

  	
	this.sunraiseH = 0;
	this.sunraiseM = 0;
	this.sunsetH = 0;
	this.sunsetM = 0;


	let onReceive = function(info) {	
		let rev_data = new Uint8Array(info.data);
		if (rev_data[0] == 11) {
			serverInfos[info.socketId] = {
				serverIP : info.remoteAddress,
				serverPort : info.remotePort
			};
		}

		//let funcs = {
		//		"onReceive" : self.onReceive,
		//		"checkConnection" : self.checkConnection
		//	};	

		onReceiveMap[info.socketId].onReceive(rev_data);



		//onReceiveMap[info.socketId](rev_data);
	};
	
	let onReceiveError = function(e){
		console.log(e);	
	};

	chrome.sockets.udp.onReceive.addListener(onReceive)
	chrome.sockets.udp.onReceiveError.addListener(onReceiveError);


	let conntdownList = {};

	this.updateCountdown = function(std,tag_id) {
		if (conntdownList[tag_id] == undefined) {
			conntdownList[tag_id] = {
				std:std*60,
				tag:$("#cd"+tag_id)
			};

			let btn = $("#btnbox"+tag_id);
			btn.addClass("ing");
		} else {
		}
	};

	this.updateCountdownRemove = function(tag_id) {
		$("#cd"+tag_id).html("");
		delete conntdownList[tag_id];
		let btn = $("#btnbox"+tag_id);
		btn.removeClass("ing");
	};

	this.historydata = new historydata_base();
	let statusdata = new statusdata_base();
	var self = this;
	
	this.init = function(data) {	
		window.RTCPeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;//compatibility for Firefox and chrome
		var pc = new RTCPeerConnection({iceServers:[]}), noop = function(){};      
		pc.createDataChannel('');//create a bogus data channel
		pc.createOffer(pc.setLocalDescription.bind(pc), noop);// create offer and set local description
		pc.onicecandidate = function(ice) {	
			if (ice && ice.candidate && ice.candidate.candidate) {
			  	let myIP = /([0-9]{1,3}(\.[0-9]{1,3}){3}|[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7})/.exec(ice.candidate.candidate)[1];
			  	//console.log(myIP);
			  	pc.onicecandidate = noop;
			  	let historyitemselecter = $("#historyitemselecter");
			  	historyitemselecter.empty();
		  		self.user.init(myIP,data, self.historydata, onReceiveMap);



		  		$("select[name='houselist']").selectmenu("refresh");
		  		$("select[name='houselist']").bind( "change", function(event, ui) {
						//console.log($(this).val());
						self.historydata.addField_loaded($(this).val());
						

						//let h_itemselecter = document.getElementById("con_historyitemselecter").clientHeight;
						//console.log(h_itemselecter );

						$($("input[name='check_chartknd']")[0]).prop('checked',true).checkboxradio('refresh');
				

						$("input[name='check_chartknd']").bind( "change", function(event, ui) {
							self.historydata.changeFieldView($(this).attr("data-nid"),$(this).attr("data-knd"), $(this).prop("checked"));
						});

						$("input[name='check_minmax']").bind( "change", function(event, ui) {
							self.historydata.viewMinMax($(this).attr("data-knd"), $(this).prop("checked"));
						});
					});

		  		let farm_cde = data[0].key;
		
					$("#historyitemselecter").trigger('create');
					$($("input[name='check_chartknd']")[0]).prop('checked',true).checkboxradio('refresh');
				

					$("input[name='check_chartknd']").bind( "change", function(event, ui) {
						self.historydata.changeFieldView($(this).attr("data-nid"),$(this).attr("data-knd"), $(this).prop("checked"));
					});

					$("input[name='check_minmax']").bind( "change", function(event, ui) {
						self.historydata.viewMinMax($(this).attr("data-knd"), $(this).prop("checked"));
					});

					var now = new Date();
					var breoreDay_num = now - (7 * 24 * 60 * 60 * 1000);
		  	  	var breoreDay = new Date(breoreDay_num);   
		      //var enddat = now.getFullYear() + "-" + (now.getMonth()+1) + "-" + (now.getDate());
		      	var enddat = now.getFullYear() + "-" + ( (now.getMonth()+1) < 10 ? ("0" + (now.getMonth()+1)) : (now.getMonth()+1) ) + "-" + ( (now.getDate() < 10 ? ("0" + now.getDate()) : now.getDate()  ));
		      	var startdat = breoreDay.getFullYear() + "-" + ( (breoreDay.getMonth()+1) < 10 ? ("0" + (breoreDay.getMonth()+1)) : (breoreDay.getMonth()+1) ) + "-" + ( (breoreDay.getDate() < 10 ? ("0" + breoreDay.getDate()) : breoreDay.getDate()  ));
		      
		      	$( "input[name*='enddat']" ).val(enddat );
		      	$( "input[name*='startdat']" ).val(startdat );

		      	//$( "#startdat_chart" ).val("2020-02-01" );

		      	$("#btn_getdata" ).click(function() {
		  			//historydata.getData(self.user.usr_id, "KRJN000011");
		  			//console.log(self.user.usr_id, "KRJN000001");
		  				self.historydata.getData(self.user.usr_id, farm_cde);
		  	  	});

		  	  	$("#btn_getdata_chart" ).click(function() {
		  				statusdata.getData(self.user.usr_id, farm_cde);
		  	  	});

/*
		  	  	let msp = new msp_base();

		  	  	$("#btn_msp" ).click(function() {
		  				msp.draw();
		  	  	});
		  	  	*/

/*
		  	  	let forcastchart = new forcastchart_base();

		  	  	$("#btn_forcast" ).click(function() {
		  				forcastchart.getForcastData();
		  	  	});
*/
		  	  	let step_chart = new chart_step_base();

		  	  	$("#btn_step" ).click(function() {
		  				step_chart.draw();
		  	  	});
		  	  	

		  	  	

		  	  	
		  	  	
			  	
			}
		}


		let checkInc = 0;
		setInterval(function() { 
			let now = new Date();
			let hh = now.getHours();
			let mm = now.getMinutes();
			let ss = now.getSeconds();
			//console.log(onReceiveMap);
			//console.log(hh + ":" + mm + ":" + ss);
			$("#clock").html(pad(hh,2) + ":" + pad(mm,2) + ":" + pad(ss,2));

			checkInc++;
			if (checkInc == 60) {
				$.each(serverInfos, function( index, item ) {	
	  			//console.log(index,item);
	  			onReceiveMap[index].checkConnection();
	  		});
	  		checkInc = 0;
			}

			


/*
			serverInfos[info.socketId] = {
				serverIP : info.remoteAddress,
				serverPort : info.remotePort
			};
			*/

			
/*
			$.each(conntdownList, function( index, item ) {
				item.std = item.std - 1;
				let mm = Math.floor(item.std / 60);
				let ss = item.std - (mm*60);
				item.tag.html(pad(mm,2)+":"+pad(ss,2));
			});
			*/

		}, 1000);

		//$('#preset').selectmenu("refresh");
		$('#preset').selectmenu();

		

	};

/*
	$("#briefing").on('mousedown', function(e) {  
		TENEFARM.briefing();
  });

  $("#recognize").on('mousedown', function(e) {  
		TENEFARM.recognize();
  });
  */

	this.func_layersub_stop = function(socketId,  house_cde, agid, akid, layer, key) {	
		/*
		let now = new Date();
		
		console.log("=================");
		console.log(now);
		console.log("func_layersub_stop" + " " + house_cde + " " + agid + " " + akid + " " + layer + " " + key);
		console.log("=====");
		*/

		var arrayBuffer = new ArrayBuffer(7);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_LAYER_SUB_STOP;
	    buffer[1] = 5;
	    buffer[2] = Number(house_cde);
	    buffer[3] = Number(agid);
	    buffer[4] = Number(akid);
	    buffer[5] = Number(layer);
	    buffer[6] = Number(key);
	    chrome.sockets.udp.send(socketId, arrayBuffer, serverInfos[socketId].serverIP, serverInfos[socketId].serverPort, function() {});	
	};

	this.func_oppfit = function(socketId,  house_cde, agid, akid, layer, key, orderrate) {	
		/*
		let now = new Date();
		console.log("=================");
		console.log(now);
		console.log("func_oppfit" + " " + house_cde + " " + agid + " " + akid + " " + layer + " " + key + " " + orderrate);
		console.log("=====");
		*/
		var arrayBuffer = new ArrayBuffer(8);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_OPPFIT;
	    buffer[1] = 6;
	    buffer[2] = Number(house_cde);
	    buffer[3] = Number(agid);
	    buffer[4] = Number(akid);
	    buffer[5] = Number(layer);
	    buffer[6] = Number(key);
	    buffer[7] = Number(orderrate);
		chrome.sockets.udp.send(socketId, arrayBuffer, serverInfos[socketId].serverIP, serverInfos[socketId].serverPort, function() {});	
	};

	this.func_onoff = function(socketId, house_cde, agid, akid, key, onoff) {	
		var arrayBuffer = new ArrayBuffer(7);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_ONOFF;
	    buffer[1] = 5;
	    buffer[2] = Number(house_cde);
	    buffer[3] = Number(agid);
	    buffer[4] = Number(akid);
	    buffer[5] = Number(key);
	    buffer[6] = Number(onoff);
		chrome.sockets.udp.send(socketId, arrayBuffer, serverInfos[socketId].serverIP, serverInfos[socketId].serverPort, function() {});	
	};


	this.func_am = function(fargs) {
		/*
		let now = new Date();
		
		console.log("=================");
		console.log("func_am");
		console.log(now);
		console.log(fargs);
		console.log("=====");
		*/
		var arrayBuffer = new ArrayBuffer(5);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_AUTOMANUAL;
	    buffer[1] = 3;
	    buffer[2] = Number(fargs.house_cde);
	    buffer[3] = Number(fargs.agid);
	    buffer[4] = Number(fargs.opp);	    
	   chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
	};

	this.func_islocal = function(fargs) {
		/*
		let now = new Date();
		console.log(now);
		console.log(fargs);
		console.log(serverInfos[fargs.socketId]);
		*/
		//console.log(fargs);
		var arrayBuffer = new ArrayBuffer(4);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.C_ISLOCAL;
	    buffer[1] = 2;
	    buffer[2] = Number(fargs.house_cde);
	    buffer[3] = Number(fargs.opp);
	   chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
	};


	this.func_insertPeriod = function(fargs) {
		var arrayBuffer = new ArrayBuffer(3);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.INS_PERIORD;
	    buffer[1] = fargs.house_cde;
	    buffer[2] = fargs.agid;
		chrome.sockets.udp.send(socketId, arrayBuffer, serverInfos[socketId].serverIP, serverInfos[socketId].serverPort, function() {});	
	};

	this.func_deletePeriod = function(fargs) {
		var arrayBuffer = new ArrayBuffer(4);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.DEL_PERIORD;
	    buffer[1] = fargs.house_cde;
	    buffer[2] = fargs.agid;
	    buffer[3] = fargs.key;
		chrome.sockets.udp.send(socketId, arrayBuffer, serverInfos[socketId].serverIP, serverInfos[socketId].serverPort, function() {});	
	};

	this.func_updatePeriod = function(fargs) {
		console.log(fargs);
	  let chCnt = fargs.data.length;
	  console.log(chCnt);

		var arrayBuffer = new ArrayBuffer((chCnt*14) + 4);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_PERIORD;
	    buffer[1] = (chCnt*14) + 2; // data soze
	    buffer[2] = fargs.house_cde;
	    buffer[3] = fargs.agid;
	    

	    let pos = 4
	    $.each(fargs.data,function(index,pp) {
				buffer[pos++] = pp.mkey;
				buffer[pos++] = pp.tknd;
				var tempbytes = gutil.floatToBytes(Number(pp.tinc));
		    buffer[pos++] = tempbytes[0];
		    buffer[pos++] = tempbytes[1];
		    buffer[pos++] = tempbytes[2];
		    buffer[pos++] = tempbytes[3];

		    tempbytes = gutil.floatToBytes(Number(pp.hm));
		    buffer[pos++] = tempbytes[0];
		    buffer[pos++] = tempbytes[1];
		    buffer[pos++] = tempbytes[2];
		    buffer[pos++] = tempbytes[3];

		    tempbytes = gutil.floatToBytes(Number(pp.sval));
		    buffer[pos++] = tempbytes[0];
		    buffer[pos++] = tempbytes[1];
		    buffer[pos++] = tempbytes[2];
		    buffer[pos++] = tempbytes[3];
	    });



	   // console.log(fargs);
	    //console.log(serverInfos);
	    //console.log(buffer);
	    chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
	};

	this.func_updateCor = function(fargs) {
		var arrayBuffer = new ArrayBuffer(14);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.UP_CORECTION;
	    buffer[1] = fargs.house_cde;
	    buffer[2] = fargs.agid;
	    buffer[3] = fargs.corid;
	    buffer[4] = fargs.cor_val;

	    var tempbytes = gutil.floatToBytes(fargs.cor_min);
	    buffer[5] = tempbytes[0];
	    buffer[6] = tempbytes[1];
	    buffer[7] = tempbytes[2];
	    buffer[8] = tempbytes[3];

	    tempbytes = gutil.floatToBytes(fargs.cor_max);
	    buffer[9] = tempbytes[0];
	    buffer[10] = tempbytes[1];
	    buffer[11] = tempbytes[2];
	    buffer[12] = tempbytes[3];

	    buffer[13] = fargs.isuse;

		
		chrome.sockets.udp.send(socketId, arrayBuffer, serverInfos[socketId].serverIP, serverInfos[socketId].serverPort, function() {});	
	};

	this.func_updateEtc = function(fargs) {
		var arrayBuffer = new ArrayBuffer(8);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.UP_ETC;
	    buffer[1] = fargs.house_cde;
	    buffer[2] = fargs.agid;
	    buffer[3] = fargs.etcid;
	    buffer[4] = fargs.etc_val;
	    buffer[5] = fargs.isuse;
	    let bufsk = gutil.stringToBytes(fargs.sk);
	    buffer[6] = bufsk[0];
	    buffer[7] = bufsk[1];

		chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
	};

	this.func_updateRoll = function(fargs) {
		var arrayBuffer = new ArrayBuffer(8);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.UP_ROLL;
	    buffer[1] = fargs.house_cde;
	    buffer[2] = fargs.agid;
	    buffer[3] = fargs.akid;
	    buffer[4] = fargs.key;
	    buffer[5] = fargs.risuse;
	    buffer[6] = fargs.highsval;
	    buffer[7] = fargs.crate;
		chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
	};

	this.func_sendConfigLimit = function(fargs) {
		var arrayBuffer = new ArrayBuffer(18);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_LIMIT_VALUE;
	    buffer[1] = fargs.house_cde;
	    buffer[2] = fargs.agid;
	    buffer[3] = fargs.akid;
	    buffer[4] = fargs.layerid;
	    buffer[5] = fargs.subid;

	    let tempbytes = gutil.floatToBytes(fargs.open);
	    buffer[6] = tempbytes[0];
	    buffer[7] = tempbytes[1];
	    buffer[8] = tempbytes[2];
	    buffer[9] = tempbytes[3];
	    
	    
	    tempbytes = gutil.floatToBytes(fargs.close);
	    buffer[10] = tempbytes[0];
	    buffer[11] = tempbytes[1];
	    buffer[12] = tempbytes[2];
	    buffer[13] = tempbytes[3];
	    
	    tempbytes = gutil.floatToBytes(fargs.max);
	    buffer[14] = tempbytes[0];
	    buffer[15] = tempbytes[1];
	    buffer[16] = tempbytes[2];
	    buffer[17] = tempbytes[3];
    
    
		chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
		
	};

	this.func_sendOpenRateReset = function(fargs) {
		var arrayBuffer = new ArrayBuffer(6);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_OPENRATE_RESET;
	    buffer[1] = fargs.house_cde;
	    buffer[2] = fargs.agid;
	    buffer[3] = fargs.akid;
	    buffer[4] = fargs.layerid;
	    buffer[5] = fargs.rate;
		chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
	};

	this.func_sendRunStd = function(fargs) {
		var arrayBuffer = new ArrayBuffer(8);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_RUNSTD;
	    buffer[1] = fargs.house_cde;
	    buffer[2] = fargs.agid;
	    buffer[3] = fargs.akid;
	    buffer[4] = fargs.onoffid;
	    buffer[5] = fargs.run;
	    buffer[6] = fargs.std;
	    buffer[7] = fargs.it_use;
		chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
	};

	this.func_sendLED = function(fargs) {
		var arrayBuffer = new ArrayBuffer(9);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_LED;
	    buffer[1] = fargs.house_cde;
	    buffer[2] = fargs.agid;
	    buffer[3] = fargs.akid;
	    buffer[4] = fargs.onoffid;

	    let tempbytes = gutil.floatToBytes(fargs.run);
	    buffer[5] = tempbytes[0];
	    buffer[6] = tempbytes[1];
	    buffer[7] = tempbytes[2];
	    buffer[8] = tempbytes[3];
		chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
	};

	this.func_updateAlertInfo = function(fargs) {
		//console.log(fargs);

		var arrayBuffer = new ArrayBuffer(13);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_SENSOR_ALERT;
	    buffer[1] = fargs.hc;
	    buffer[2] = fargs.nid;
	    buffer[3] = fargs.sid;
	    buffer[4] = fargs.isuse;

	    let tempbytes = gutil.floatToBytes(fargs.al);
	    buffer[5] = tempbytes[0];
	    buffer[6] = tempbytes[1];
	    buffer[7] = tempbytes[2];
	    buffer[8] = tempbytes[3];
	    
	    
	    tempbytes = gutil.floatToBytes(fargs.ah);
	    buffer[9] = tempbytes[0];
	    buffer[10] = tempbytes[1];
	    buffer[11] = tempbytes[2];
	    buffer[12] = tempbytes[3];
	    
		chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
		
	};

	this.func_updateAlertInfo_ws = function(fargs) {
		//console.log(fargs);

		var arrayBuffer = new ArrayBuffer(12);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_SENSOR_ALERT_WS;
	    buffer[1] = fargs.nid;
	    buffer[2] = fargs.sid;
	    buffer[3] = fargs.isuse;
	    

	    let tempbytes = gutil.floatToBytes(fargs.al);
	    buffer[4] = tempbytes[0];
	    buffer[5] = tempbytes[1];
	    buffer[6] = tempbytes[2];
	    buffer[7] = tempbytes[3];
	    
	    
	    tempbytes = gutil.floatToBytes(fargs.ah);
	    buffer[8] = tempbytes[0];
	    buffer[9] = tempbytes[1];
	    buffer[10] = tempbytes[2];
	    buffer[11] = tempbytes[3];
	    //console.log(buffer);
	    
		chrome.sockets.udp.send(fargs.socketId, arrayBuffer, serverInfos[fargs.socketId].serverIP, serverInfos[fargs.socketId].serverPort, function() {});	
		
	};

	this.func_sendConfigPriorityIsUse = function(socketId, house_cde, agid,prioID, isuse) {
		var arrayBuffer = new ArrayBuffer(6);
	    var buffer = new Uint8Array(arrayBuffer);
	    buffer[0] = HeaderDefine_COMMON.CH_CONDITION_PRIORITY;
	    buffer[1] = 4;
	    buffer[2] = Number(house_cde);
	    buffer[3] = Number(agid);
	    buffer[4] = Number(prioID);
	    var byte_isuse = (isuse) ? "1" : "0";
	    buffer[5] = Number(byte_isuse);
		chrome.sockets.udp.send(socketId, arrayBuffer, serverInfos[socketId].serverIP, serverInfos[socketId].serverPort, function() {});


		
	};

	this.func_startbriefing = function(socketId, house_cde) {
			var arrayBuffer = new ArrayBuffer(2);
			var buffer = new Uint8Array(arrayBuffer);
			buffer[0] = HeaderDefine_COMMON.RUN_BRIEFING;
			buffer[1] = house_cde;
			//console.log(socketId, buffer);
			chrome.sockets.udp.send(socketId, arrayBuffer, serverInfos[socketId].serverIP, serverInfos[socketId].serverPort, function() {});	
	};

	
};


	



function degtorad(deg) {
	return deg * (Math.PI/180); 
};

function radtodeg(rad) {
	return rad * (180/Math.PI); 
};