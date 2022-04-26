var Dcontrol_base = function(args, parent, historydata) {
	//console.log(args);
	var self = this;
	
	//const MY_SOCKET_SOCKETPORT = 9998;
	const SOCKET_URL = args.remoteip;
	//const SOCKET_URL = args.localip;
	const MY_SOCKET_SOCKETPORT = Number(args.sp);
	
	const SOCKET_SOCKETPORT = Number(args.sp);
	
	
 	//const SOCKET_WSPORT = Number(args.wp);
 	

 	const socketProperties = {
 		persistent:false,
 		
      //	name:"aaa",
      	//bufferSize:1024
      	bufferSize:128
      	//bufferSize:200000
  	};

  	$("#intro").attr("data-serverip",SOCKET_URL);
  	
  	
  	let connected = false;

  	let socketId = 0;
	let con_cde = args.key;	
	let name = args.nam;	
	let Dhouses = {};

	//let myIP;

	

	let connect = function(myIP, onReceiveMap){	
		//console.log(SOCKET_URL, SOCKET_SOCKETPORT)	;
		chrome.sockets.udp.create(socketProperties, function(createInfo) {
			socketId = createInfo.socketId;		
			let funcs = {
				"onReceive" : self.onReceive,
				"checkConnection" : self.checkConnection
			};	
			onReceiveMap[socketId] = funcs;
			//onReceiveMap[socketId] = self.onReceive;
			chrome.sockets.udp.bind(socketId, myIP, SOCKET_SOCKETPORT, function(result) {			
	  			if (result > -1) {	  				
	  				let arrayBuffer = new ArrayBuffer(1);
		            let buffer = new Uint8Array(arrayBuffer);
		            buffer[0] = HeaderDefine_COMMON.C_WEB_CLIENT_CONNECTED;
		            chrome.sockets.udp.send(socketId, arrayBuffer, SOCKET_URL, SOCKET_SOCKETPORT, function(rep) {
		            	if (rep.resultCode > -1) {
		            		connected = true;
				            $.each(Dhouses, function( index, house ) {
				            	house.setSocketID(socketId);
				            });
		            	}
		            });			            
	  			}
			});
		});
	};

	this.responseCK = 0;

	let checkResponse = function() {
		if (self.responseCK == 0) {
			//console.log("reconnect");
			let arrayBuffer = new ArrayBuffer(1);
            let buffer = new Uint8Array(arrayBuffer);
            buffer[0] = HeaderDefine_COMMON.C_WEB_CLIENT_CONNECTED;
            //console.log(socketId, SOCKET_URL);
            chrome.sockets.udp.send(socketId, arrayBuffer, SOCKET_URL, SOCKET_SOCKETPORT, function(rep) {
            	if (rep.resultCode > -1) {
            		connected = true;
		            
            	}
            });	
		} else {
			//console.log("ccc");
		}
	};

	this.checkConnection = function() {		
		let arrayBuffer = new ArrayBuffer(1);
        let buffer = new Uint8Array(arrayBuffer);
        buffer[0] = HeaderDefine_COMMON.C_WEB_CLIENT_CHECKCONNTION;     
		chrome.sockets.udp.send(socketId, arrayBuffer, SOCKET_URL, SOCKET_SOCKETPORT, function(rep) {
			//console.log(rep);
        	if (rep.resultCode > -1) {
        		self.responseCK = 0;        		
        		setTimeout(function() {
        			checkResponse();
				}, 2000);
        	}
        });	
	};

	this.getHouseInfo = function() {
		let ret = {};
		if (args.house) {
			$.each(args.house, function( index, info ) {
				let houseinfo = Dhouses[info.key].getHouseInfo();
				ret[info.key] = houseinfo;
			});
		}

		return ret;
	};
	
	this.init = function(myIP, onReceiveMap) {
		if (self.isuse) {
			
			if (args.house) {
				$.each(args.house, function( index, info ) {
					info.fc = args.fc;
					info.con_cde = con_cde;
					Dhouses[info.key] = new Dhouse_base(info,self, historydata);
					Dhouses[info.key].init();
				});
			}
			
			connect(myIP, onReceiveMap);
		}
	};

	this.isuse = (args.iu == "Y");

	
	this.getID= function() {
		return con_cde;
	};

	this.getName= function() {
		return name;
	};

	this.getSocketId = function() {
		return socketId;
	};


	this.getHouseName = function(house_cde) {
		return Dhouses[house_cde].getName();
	};

	this.getActGrpName = function(house_cde, agid) {
		return Dhouses[house_cde].getActGrpName(agid);
	};

	this.getActName = function(house_cde, agid, aid) {		
		return Dhouses[house_cde].getActName(agid,aid);
	};

	this.getActSubName = function(house_cde, agid, aid, subid) {		
		return Dhouses[house_cde].getActSubName(agid, aid, subid);
	};

	this.getOppState = function(house_cde, agid, aid, subid) {		
		return Dhouses[house_cde].getOppState(agid, aid, subid);
	};

	this.getOpenRate = function(house_cde, agid, aid, subid) {		
		return Dhouses[house_cde].getOpenRate(agid, aid, subid);
	};

	this.getOppOrder = function(house_cde, agid, aid, subid) {			
		return Dhouses[house_cde].getOppOrder(agid, aid, subid);
	};

	this.changeColor = function(house_cde, agid, aid, subid) {
		return Dhouses[house_cde].changeColor(agid, aid, subid);
	};

/*
	this.changeStatus = function(house_cde, agid, aid, subid, state, openrate) {
		Dhouses[house_cde].changeStatus(agid, aid, subid, state, openrate);
	};
	*/
	this.changeStatus = function(hc,agID,akID,layerID,layerSubID, oppstate,openRate) {
		Dhouses[hc].changeStatus(agID,akID,layerID,layerSubID, oppstate,openRate);

		
	};



	this.rChangeOnOff = function(hc,agID,akID,onoffID,oppstate) {
		Dhouses[hc].changeOnOffStatus(agID,akID,onoffID,oppstate);
	};

	

	this.changeAutoManual = function(house_cde, agid, isAuto) {
		Dhouses[house_cde].changeAutoManual(agid, isAuto);
	};

	this.changeIsLocal = function(house_cde, isLocal) {
		//console.log(house_cde, isLocal);
		Dhouses[house_cde].changeIsLocal(isLocal);
	};

	this.changeOpenrate = function(house_cde, agid, aid, subid, openrate) {
		//console.log(house_cde);
		//Dhouses[house_cde].changeOpenrate(agid, aid, subid, openrate);
	};

	this.setOppOrder = function(house_cde, agid, aid, subid, order) {	
		Dhouses[house_cde].setOppOrder(agid, aid, subid, order);
	};

	this.updateSensor = function(house_cde, nid) {
		if (Dhouses[house_cde])
			Dhouses[house_cde].updateSensor(nid);
	};

	let briefing = function(house_cde) {
		Dhouses[house_cde].briefing();
	}

	

	let rConnected = function(serverkey) {
		
		var arrayBuffer = new ArrayBuffer(3);
        var buffer = new Uint8Array(arrayBuffer);
        buffer[0] = HeaderDefine_COMMON.C_WEB_CLIENT_CONNECTED_COMPLETE;
        buffer[1] = serverkey+7;
        //buffer[1] = serverkey+8;
        buffer[2] = 0;
        //console.log(serverkey);
       	chrome.sockets.udp.send(socketId, arrayBuffer, SOCKET_URL, SOCKET_SOCKETPORT, function() {});
       
	};

	

	let rRdata = function(rev_data) {
		
		let hcnt = rev_data[1];
		let pos = 2;
		for (let hidx=0; hidx<hcnt; hidx++) {
			let hc = rev_data[pos++];
			let hDataSize = rev_data[pos++];
			if (hDataSize > 0) {
				let hData = rev_data.slice(pos,pos + hDataSize);
				Dhouses[hc].refreshData(hData);
				pos = pos+hDataSize;


/*
				$.each(Dhouses, function( index, house ) {
	            	house.refreshSharedData(hc, hData);
	            });
	            */
			} else {
				
			}
			
		}

		//console.log("pos",pos);

		let isws = rev_data[pos++];
		//console.log(isws);
		if (isws == 22) {
			//console.log("isws",isws);	
			let sCnt = rev_data[pos++];
			//console.log("sCnt",sCnt);
			for (let sidx=0; sidx<sCnt; sidx++) {
				let sid = rev_data[pos++];
				//console.log("sid",sid);
				let ff = rev_data[pos++];
				let ss = rev_data[pos++];
				//console.log("sid",sid);
				//console.log(ff,ss);
				//let buf_value = [rev_data[pos++], rev_data[pos++]];
				//let buf_value = [ff, ss];
				//if (ff > 127)
				//	ff = ff - 256;

				//if (ss > 127)
				//	ss = ss - 256;

				let buf_value = [ff, ss];

				//let buf_value = [-1, -76];
	    		let value = (gutil.byte2Int(buf_value)) / 10;
	    		//console.log("sid",sid);
	    		//console.log(sid,value);
	    		parent.weatherstation.refreshData(sid,value);
			}
		}

		let sun = rev_data[pos++];
		
		if (sun == 23) {
			TENEFARM.sunraiseH = rev_data[pos++];
			TENEFARM.sunraiseM = rev_data[pos++];
			TENEFARM.sunsetH = rev_data[pos++];
			TENEFARM.sunsetM = rev_data[pos++];

			$("#sunrise_time").html(pad(TENEFARM.sunraiseH,2)+":"+pad(TENEFARM.sunraiseM,2));
			$("#sunset_time").html(pad(TENEFARM.sunsetH,2)+":"+ pad(TENEFARM.sunsetM,2));

			//console.log(TENEFARM.sunraiseH,TENEFARM.sunraiseM);	
			//console.log(TENEFARM.sunsetH,TENEFARM.sunsetM);	

		}

	};

	

	

	let rOpenrate = function(rev_data) {
		//console.log(rev_data);
		//let rev_data = new Uint8Array(data);      		
  		let houseCnt = rev_data[1];
  		
  		let pos = 2;
  		for (let idx=0; idx<houseCnt; idx++ ) {
  			let houseDataSize = rev_data[pos++];	
  			
  			let houseData = rev_data.slice(pos, pos+houseDataSize);
  			var hc_agCnt = pad(houseData[0].toString(2),8);
  			var hc =  parseInt(hc_agCnt.substring(0,4), 2);
  			Dhouses[hc].changeOpenrate(houseData);
  			pos = pos+houseDataSize;
  		}

  		return;
	};

	

	let rChangeRun = function(rev_data) {
		//let rev_data = new Uint8Array(data);
		//console.log(rev_data);
		var hc_agid = pad(rev_data[1].toString(2),8);
		var hc =  parseInt(hc_agid.substring(0,4), 2);
		var agID =  parseInt(hc_agid.substring(4,8), 2);
		
		let akid_layerid = pad(rev_data[2].toString(2),8);
		var akID =  parseInt(akid_layerid.substring(0,4), 2);
		var layerID =  parseInt(akid_layerid.substring(4,8), 2);
		let layerSubID = rev_data[3];


		let status_openrate = pad(rev_data[4].toString(2),8);
		var oppstate =  parseInt(status_openrate.substring(0,1), 2);
		var openRate =  parseInt(status_openrate.substring(1,8), 2);

		self.changeStatus(hc,agID,akID,layerID,layerSubID, oppstate,openRate);
	};

	let rChangeOnOff = function(rev_data) {
		//let rev_data = new Uint8Array(data);
		//console.log(rev_data);

		var hc_agid = pad(rev_data[1].toString(2),8);
		var hc =  parseInt(hc_agid.substring(0,4), 2);
		var agID =  parseInt(hc_agid.substring(4,8), 2);

		let akid_onoffid = pad(rev_data[2].toString(2),8);
		var akID =  parseInt(akid_onoffid.substring(0,4), 2);
		var onoffID =  parseInt(akid_onoffid.substring(4,8), 2);
		let oppstate = rev_data[3];

		self.rChangeOnOff(hc,agID,akID,onoffID,oppstate);
	};

	let rAutoManual = function(rev_data) {
		//let rev_data = new Uint8Array(data);
		let hc = rev_data[1];
		let agID = rev_data[2];
		let isAuto = rev_data[3];
		self.changeAutoManual(hc,agID,isAuto);
	};

	let rIsLocal = function(rev_data) {
		//let rev_data = new Uint8Array(data);
		let hc = rev_data[1];
		let isLocal =  rev_data[2];
		//console.log(hc,isLocal);
		self.changeIsLocal(hc,isLocal);
	};

	

	this.onReceive = function(rev_data) {
		//console.log(SOCKET_SOCKETPORT);
		//console.log(info);

		//let rev_data = new Uint8Array(info.data);
		let cmd = rev_data[0];
		//console.log(cmd);

		switch (cmd) {
			case HeaderDefine_COMMON.C_WEB_CLIENT_CHECKCONNTION :
				self.responseCK = 1;
			break;
			case HeaderDefine_COMMON.RUN_BRIEFING :
				//console.log("RUN_BRIEFING");
				briefing(rev_data[1]);
			break;
			case 78 :
				console.log("not auth");
				console.log(rev_data);
			break;
			case HeaderDefine_COMMON.C_WEB_CLIENT_CONNECTED:	
				
		        rConnected(rev_data[1]);
	      	break;
	      	case HeaderDefine_COMMON.C_WEB_CLIENT_CONNECTED_COMPLETE:
	      	break;
	      	case HeaderDefine_COMMON.C_ISLOCAL:
	      		//console.log(rev_data);
	      		rIsLocal(rev_data);
	      	break;
	      
	      	case HeaderDefine_COMMON.R_DATA:
	      		//console.log(rev_data);
	      		rRdata(rev_data);
	      	break;
	      	case HeaderDefine_COMMON.R_DATA_COMMON:
	      		//console.log("cccc");
	      		rRdataCommon(rev_data);
	      	break;
	      	case HeaderDefine_COMMON.CH_OPENRATE:
	      		rOpenrate(rev_data);
	      	break;
	      	case HeaderDefine_COMMON.CH_LAYER_SUB_STOP:
	      		rChangeRun(rev_data);
	      	break;
	      	case HeaderDefine_COMMON.CH_ONOFF:
	      		rChangeOnOff(rev_data);
	      	break;
	      	case HeaderDefine_COMMON.CH_AUTOMANUAL:
	      		rAutoManual(rev_data);
	      	break;
	      	case HeaderDefine_COMMON.CH_OPPFIT:
	      	break;
	      	case HeaderDefine_COMMON.INS_PERIORD:
	      		Dhouses[rev_data[1]].insPeriod(rev_data[2],rev_data[3]);
	      	break;
	      	case HeaderDefine_COMMON.DEL_PERIORD:
	      		//console.log(rev_data);
	      		Dhouses[rev_data[1]].delPeriod(rev_data[2],rev_data[3]);
	      	break;

	      	
		}
		
		
		
	};
};
