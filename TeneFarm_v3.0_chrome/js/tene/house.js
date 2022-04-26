var Dhouse_base = function(args,parent, historydata) {
	//console.log(args);
	this.socketId = 0;
	this.id = args.key;
	var self = this;
	let Dactgrps = {};
	let Dsensornodes = {};
	let frame_cnt = args.frame_cnt;
	
	let name = args.nam;
	
	let tenechart;
	let incAvg = 0;
	let updated = false;
	//let isview = false;

	let islocalButton;



	this.setSocketID = function(socketID) {

		self.socketId = socketID;
		//console.log("house", self.socketId);

		$.each(Dactgrps, function( index, actgrp ) {
			actgrp.setSocketID(socketID);
		});

		$.each(Dsensornodes, function( index, sensornode ) {
			sensornode.setSocketID(socketID);
		});
	};

	this.getHouseInfo = function() {
		let houseinfo = {};
		//let sensorNodeInfo = [];

		let ret = {
			houseinfo:houseinfo,
			sensorNodeInfo:args.sensornode
		};
		return ret;
	};

	this.briefingData = {"temp":0,"hum":0};

	this.init = function() {
		let op = $('<option value="' + args.key + '">' + args.nam + '</option>');
		$("select[name='houselist']").append(op);
		if (args.iu == "Y") {

			makeHouse();
			
			if (args.sensornode) {
				let container = makeTag(args.sensornode[0].nam);
				let containerSensorNode = container.main;
				//let containerBettary = container.bettary;
				$.each(args.sensornode, function( index, info ) {
					//if (info.knd != "99") {
						info.fc = args.fc;
						info.con_cde = args.con_cde;
						info.house_cde = args.key;
						info.container = containerSensorNode;
						info.briefingData = self.briefingData;
						//info.containerBAT = containerBettary;
						let sensornode = new Dsensornode_base(info, historydata);
						sensornode.init();
						Dsensornodes[info.key] = sensornode;
						//setInitDataForChart(sensornode);
					//}
				});

				//self.changeIsLocal((args.islocal == "Y" ? 1 : 0));

				
			}

			if (args.actgroup) {
				//self.changeIsLocal((args.islocal == "Y" ? 1 : 0));
				$.each(args.actgroup, function( index, info ) {
					info.fc = args.fc;
					info.con_cde = args.con_cde;
					info.house_cde = args.key;
					info.container = orDivActGrp;
					let actgrp = new Dactgrp_base(info, self);				
					Dactgrps[info.key] = actgrp;
					actgrp.init();
				});
			}
			


			
	     
		}
		
	};

	this.getID= function() {
		return self.id;
	};

	this.getName= function() {
		return name;
	};

	this.getActGrpName = function(agid) {
		return Dactgrps[agid].getName();
	};

	this.getActName = function( agid, aid) {		
		return Dactgrps[agid].getActName(aid);
	};

	this.getActSubName = function( agid, aid, subid) {		
		return Dactgrps[agid].getActSubName(aid, subid);
	};

	this.getOppState = function( agid, aid, subid) {		
		return Dactgrps[agid].getOppState(aid, subid);
	};

	this.getOpenRate = function( agid, aid, subid) {		
		return Dactgrps[agid].getOpenRate( aid, subid);
	};

	this.getOppOrder = function( agid, aid, subid) {			
		return Dactgrps[agid].getOppOrder( aid, subid);
	};

	this.changeColor = function(agid, aid, subid) {
		return Dactgrps[agid].changeColor(aid, subid);
	};

/*
	this.changeStatus = function( agid, aid, subid, state, openrate) {
		Dactgrps[agid].changeStatus(aid, subid, state, openrate);
	};
	*/

	this.changeStatus = function( agID,akID,layerID,layerSubID, oppstate,openRate) {
		//console.log(oppstate,openRate);
		Dactgrps[agID].changeStatus(akID,layerID,layerSubID, oppstate,openRate);
	};

	this.changeOnOffStatus = function( agID,akID,onoffID,oppstate) {
		Dactgrps[agID].changeOnOffStatus(akID,onoffID,oppstate);
	};

	this.changeAutoManual = function(agid, isAuto) {
		Dactgrps[agid].changeAutoManual(isAuto);
	};

/*
	this.changeIsLocal = function(isLocal) {
		islocalButton.prop('checked', (isLocal == 1 ? false : true)).checkboxradio('refresh');;
		$.each(Dactgrps, function( index, actgrp ) {
			actgrp.changeIsLocal(isLocal);
		});
	};
	*/

/*
	this.getIsLocal = function() {
		return (islocalButton.prop("checked")) ? 0 : 1;
	}
	*/

	this.changeOpenrate = function( data) {

		var hc_agCnt = pad(data[0].toString(2),8);
		var agCnt =  parseInt(hc_agCnt.substring(4,8), 2);
		let pos = 1;
		//console.log(self.id, agCnt);
		for (let agidx=0; agidx<agCnt; agidx++ ) {
			let agDataSize =  data[pos++];
			let agData = data.slice(pos, pos+agDataSize);
			var agid_akCnt = pad(agData[0].toString(2),8);
  			var agid =  parseInt(agid_akCnt.substring(0,4), 2);
			pos = pos+agDataSize;
			Dactgrps[agid].changeOpenrate( agData);
		}
	};

	this.setOppOrder = function( agid, aid, subid, order) {	
		Dactgrps[agid].setOppOrder( aid, subid, order);
	};

	this.updateSensor = function( nid) {
		Dsensornodes[nid].updateSensor();
	};

	this.insPeriod = function( agid, key) {
		Dactgrps[agid].insPeriod(key);
	};

	this.delPeriod = function( agid, key) {
		Dactgrps[agid].delPeriod(key);
	};


	let orDivActGrp = $('<div class="logger_out" >');

	

	let makeHouse = function() {
		if (args.actgroup) {
			let control_container = $("#control_container");
			var house = $('<div data-role="collapsible" data-inset="true" data-collapsed="false" class="house_out">');
	  		var house_header = $('<h3></h3>');
	  		let title = $("<div>");
	  		title.addClass("house_nam");
	  		title.html(args.nam);

	  		
	  		

	  		house_header.append(title);
	  		//house_header.append(briefing);
	  		//house_header.append(div_islocal);
	  		house_header.addClass("house_out_header");
	  		house.append(house_header);
	  		house.append(orDivActGrp);
	  		control_container.append(house);
	  		house.collapsible();

	  		
  		}
	};


	let briefStep = 0;

	let townforecast = function(div_briefing, nowDate, nowTime) {
	//let townforecast = function( nowDate, nowTime) {
		//console.log(nowDate);
		//console.log(Number("1000".substring(0,2)));
		let apikey = "Fow%2B1%2BLwKWyZbn%2FEW4126%2FLzLBdS1Lei6FbVJBF8QhVa%2F8NjLFMriPZs9eGzpg20v1J%2Fve1dbCCfp9DAa013pQ%3D%3D";
		var xhr = new XMLHttpRequest();
		var url = 'http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst'; /*URL*/


		var queryParams = '?' + encodeURIComponent('serviceKey') + '='+ apikey; /*Service Key*/
		queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('1'); /**/
		queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('400'); /**/
		//queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('300'); /**/
		queryParams += '&' + encodeURIComponent('dataType') + '=' + encodeURIComponent('XML'); /**/
		queryParams += '&' + encodeURIComponent('base_date') + '=' + encodeURIComponent(nowDate); /**/
		queryParams += '&' + encodeURIComponent('base_time') + '=' + encodeURIComponent('0500'); /**/
		queryParams += '&' + encodeURIComponent('nx') + '=' + encodeURIComponent('53'); /**/
		queryParams += '&' + encodeURIComponent('ny') + '=' + encodeURIComponent('37'); /**/

		//console.log(queryParams);

		xhr.open('GET', url + queryParams);
		xhr.onreadystatechange = function () {
		    if (this.readyState == 4) {
		        //console.log(this.status+'nHeaders: '+JSON.stringify(this.getAllResponseHeaders())+'nBody: '+this.responseText);
		        var xmlDoc = this.responseXML;
		        console.log(xmlDoc);
		        let resultCode = xmlDoc.getElementsByTagName("header")[0].getElementsByTagName("resultCode")[0].textContent;
		        console.log(resultCode);

		        if (resultCode == "00") {
		        	div_briefing.addClass("briefing_begin");
		        	let items = xmlDoc.getElementsByTagName("body")[0].getElementsByTagName("items")[0];
			        let itemlist = items.childNodes;

			        let tmn = 0;
			        let tmx = 0;

			        let avgpop = 0;
			        let sumpop = 0;
			        let cntpop = 0;

			        let avgwsd = 0;
			        let sumwsd = 0;
			        let cntwsd = 0;

			        let avgvec = 0;
			        let sumvec = 0;
			        let cntvec = 0;

			        let avgsky = 0;

			        $.each(itemlist, function( index, item ) {	
						//console.log(item);
						let catName = item.getElementsByTagName("category")[0].textContent;
						let fcstDate = item.getElementsByTagName("fcstDate")[0].textContent;
						let fcstTime = item.getElementsByTagName("fcstTime")[0].textContent;
						let fcstValue = item.getElementsByTagName("fcstValue")[0].textContent;
						let obsTime = Number(fcstTime.substring(0,2));
						if ( (catName == "TMN") || (catName == "TMX") ) {
							if (catName == "TMN")
								tmn = fcstValue;
							if (catName == "TMX")
								tmx = fcstValue;
						} 

						if ( (nowDate == fcstDate) && (nowTime < obsTime) ) {
							if (catName == "POP") {
								sumpop += Number(fcstValue);
								cntpop++;
							} else if (catName == "WSD") {
								sumwsd += Number(fcstValue);
								cntwsd++;
							} else if (catName == "VEC") {
								sumvec += Number(fcstValue);
								cntvec++;
							} else if (catName == "SKY") {
								avgsky = Math.max(avgsky,fcstValue);
							}
						}						
					});

					avgpop = Math.round(sumpop/cntpop);
					avgwsd = Math.round(sumwsd/cntwsd);
					avgvec = Math.round(sumvec/cntvec);

				  	avgvec = avgvec / 45;
				  	avgvec = avgvec.toFixed(0);
				  	avgvec = (winddirection[avgvec]); 
				  	avgsky = skyStep[avgsky];

				  	//briefStep = 1;

				  	console.log(avgsky);

			        kakao(div_briefing, tmn,tmx, avgpop, avgvec,avgwsd, avgsky );
		        }
		    }
		};

		xhr.send('');
	};

	let kakao = function(div_briefing, tmn,tmx, avgpop, avgvec,avgwsd, avgsky) {
		var d = new Date();
		let kakaourl = "https://kakaoi-newtone-openapi.kakao.com/v1/synthesize";
		
		let speak =  "<speak>";
		speak += "안녕하십니까? " + TENEFARM.user.usr_ni + "님";
		//speak += "안녕하우꽈~ <break time='100ms'/>" + self.user.usr_ni + "님";
		speak += "<break time='150ms'/> ";
		speak += "테네팜 입니다. ";
		speak += "<break time='150ms'/> ";
		speak += "현재시간 " + (d.getMonth()+1) + " 월 " + d.getDate() + " 일 ";
		speak += (d.getHours()) + " 시 " + d.getMinutes() + " 분 ";
		
		speak += "<break time='150ms'/> ";
		speak += "브리핑을 시작하겠습니다.";
		speak += "<break time='150ms'/> ";
		speak += "현재 외부 온도는 " + TENEFARM.br_outtemp + "도, 습도는 " + TENEFARM.br_outhum + "퍼센트 이며";
		speak += "<break time='150ms'/> ";
		
		if (TENEFARM.br_raindrop == "비") {
			speak += "바람은 " + TENEFARM.br_outwinddir + "쪽에서 초속" + TENEFARM.br_outwindspeed + "미터로 불고 있고";
			speak += "<break time='150ms'/> ";
			speak += "비가 내리고 있습니다.";	
		} else {
			speak += "바람은 " + TENEFARM.br_outwinddir + "쪽에서 초속" + TENEFARM.br_outwindspeed + "미터로 불고 있습니다.";
		}

		speak += "<break time='150ms'/> ";
		speak += name+"온실의 온도는 " + self.briefingData.temp + "도, 습도는 " + self.briefingData.hum + " 퍼센트 입니다.";
		speak += "<break time='150ms'/> ";
		speak += "오늘 기상청예보에 따르면 ";
		speak += "<break time='150ms'/> ";
		speak += avgsky + " 으로 예상되며 ";
		if (avgpop > 0) {
			speak += "최저기온은 " + tmn + "도, 최고기온은 " + tmx + "도 이며 ";
			speak += "평균 강수 확률 은 " + avgpop + "퍼센트 입니다. ";
		} else {
			speak += "최저기온은 " + tmn + "도, 최고기온은 " + tmx + "도 입니다.";
		}

		speak += "바람은 " + avgvec + "쪽에서 ";
		speak += "초속 " + avgwsd + "미터로 불 것으로 예보되고 있습니다. ";


		
		
		speak += "<break time='150ms'/> ";
		//speak += "고맙수다 예~에";
		speak += "감사합니다.";
		speak += "</speak>";

		console.log(speak);

		var xhr = new XMLHttpRequest();
		xhr.open('POST', kakaourl, true);
		xhr.setRequestHeader("Content-type", "application/xml");
		xhr.setRequestHeader("Authorization", "KakaoAK " + TENEFARM.apikey);
		xhr.responseType = 'blob';
		xhr.onreadystatechange = function() { 
			//console.log(xhr);
	        if ((xhr.status == 200) && (xhr.readyState == 4) ) {
	        	//console.log(xhr);
	        	var audio =  new Audio();
	        	var blob = new Blob([xhr.response], {type: 'audio/mpeg'});
	        	var objectUrl = URL.createObjectURL(blob);
	        	$(audio).on("loadedmetadata", function(){
		            //console.log(audio.duration);
		            setInterval(function() { 
						div_briefing.removeClass("briefing_begin");

					}, audio.duration*1000);
		        });

	        	audio.src = objectUrl;
	        	/*
	        	audio.loadedmetadata = function(evt) {
	        		console.log(evt);
	        		console.log(audio.duration);
			        //URL.revokeObjectURL(objectUrl);

		      	};
		      	*/
		      	audio.play();



		      	

		      	//gutil.sleep(5);

		      	
	        } 
	   };
	   xhr.send(speak);
	};

	
	let div_briefing = $("<div>");
  	this.briefing = function() {
		var d = new Date();
		townforecast(div_briefing, d.getFullYear() + "" + pad((d.getMonth()+1) ,2)+ "" + pad((d.getDate()) ,2), d.getHours());

		//return;
  		
  	};

	let makeTag = function(title) {
		let container = $("#rtime_container");
		let div = $("<div class='commonsensornode'>");

		let div_header = $("<div class='header'>");
		let div_title = $("<div class='title'>");
		div_title.html(args.nam);

		
  		div_briefing.addClass("briefing");
  		//briefing.html(args.nam);

		let div_container = $("<div class='container'>");

		div_header.append(div_title);
		div_header.append(div_briefing);
		
		div.append(div_header);
		div.append(div_container);
		container.append(div);

		div_briefing.click(function(){
			//console.log(TENEFARM);
			TENEFARM.func_startbriefing(self.socketId, self.id);
			//briefing(div_briefing);
		});

		return {main:div_container} ;


	};

	


	
	this.removeData = function() {
		$.each(Dsensornodes,function(index,node) {
			node.removeData();
		});
	};

	this.notReceive = function() {	
		$.each(Dsensornodes,function(index,node) {
			node.notReceive();
		});
	};

	this.refreshData = function(rev_data) {
		//console.log("HC:",id);

		var currentDate = new Date();
    	let hour = currentDate.getHours();
    	let min = currentDate.getMinutes();

    	let curH = pad(hour,2);
		let curM = pad(min,2);		
		

		//Current Period Corection Value
		$.each(Dactgrps, function( index, grp ) {
			grp.setCurCorValue((hour*60)+min);
		});
		
		let snDataSize = 0;
		let pos = 0;
		if (self.id == 1) {
			//console.log("HC",self.id);	
			//console.log(nid,battery);
		}
		let snCnt = rev_data[pos++];
		for (let snidx=0; snidx<snCnt; snidx++) {
			var base2 = pad(rev_data[pos++].toString(2),8);
			let substr = base2.substring(0,4);
			var nid =  parseInt(substr, 2);
			substr = base2.substring(4,8);
			var sCnt =  parseInt(substr, 2);
			snDataSize += (sCnt * 3);
			let battery = rev_data[pos++];
			let snData = rev_data.slice(pos,pos + snDataSize);	
			//if (self.id == 1) {
				//console.log("HC",self.id);	
				//console.log(nid,battery);
			//}
			
				
			if (Number(battery) == 0) {
				battery = 37;
			}	
//
			if (Dsensornodes[nid]) {
				Dsensornodes[nid].refreshData(sCnt,snData, curH+":"+curM, battery);

				
				$.each(Dactgrps, function( index, grp ) {
					if (grp.nid == nid)
						grp.refreshData(snData);
				});	

			} 
			pos += (sCnt*3);		
		}		
	};

	this.refreshSharedData = function(hc, rev_data) {
		//console.log(self.id, hc);
		if (self.id != hc) {

			var currentDate = new Date();
	    	let hour = currentDate.getHours();
	    	let min = currentDate.getMinutes();

	    	let curH = pad(hour,2);
			let curM = pad(min,2);	
			//Current Period Corection Value
			$.each(Dactgrps, function( index, grp ) {
				grp.setCurCorValue((hour*60)+min);
			});

			let snDataSize = 0;
			let pos = 0;
			let snCnt = rev_data[pos++];
			for (let snidx=0; snidx<snCnt; snidx++) {
				var base2 = pad(rev_data[pos++].toString(2),8);
				let substr = base2.substring(0,4);
				var nid =  parseInt(substr, 2);
				substr = base2.substring(4,8);
				var sCnt =  parseInt(substr, 2);
				snDataSize += (sCnt * 3);
				let battery = rev_data[pos++];
				let snData = rev_data.slice(pos,pos + snDataSize);			
					
				$.each(Dactgrps, function( index, grp ) {
					grp.refreshSharedData(hc, nid, snData);
				});	

				
				pos += (sCnt*3);		
			}
				
		} 
		
		
	};

	this.refreshDataGrp = function(agid,valueCor) {
		Dactgrps[agid].refreshDataGrp(valueCor);
	};

	this.refreshDataCommon = function(sk,txt) {
		$.each(Dactgrps, function( index, actgrp ) {
			actgrp.refreshData(sk,txt);
		});
	};
	
};