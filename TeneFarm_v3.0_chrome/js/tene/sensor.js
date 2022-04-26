var Dsensor_base = function(args, historydata) {
//	console.log(args);
	this.knd = args.sk;
	//let socketId= TENEFARM.user.Dfarms[args.fc].getSocketId(args.con_cde);
	let socketId = 0;
	let fixNum = 0;

	let yAxis;
	
	if ( (args.sk == "03") || (args.sk == "04") || (args.sk == "11") || (args.sk == "12") ) {
		fixNum = 1;
	} else if ((args.sk == "54") || (args.sk == "55") ) {
		fixNum = 2;
	} else if ((args.sk == "14")) {
		fixNum = 2;
	}

	
	this.setSocketID = function(socketID) {
		socketId = socketID;
	};

	//console.log(item);

	let preValue = 0;
	//this.value;

	let valueTag = $("<div class='value'>");
	valueTag.html("--");
	let img_updown = $("<img>");
	img_updown.attr("src","./css/iot/images/normal.png");

	let img_alert = $("<img>");
	img_alert.attr("src",(args.ia == "Y" ? "./css/iot/images/notice.png" : "./css/iot/images/unnotice.png" ));

	let alertOut = $("<div class='alertOut'>");
	let alertLow = $("<div class='alertValue'>");
	let alertdash = $("<div class='alertValue'>");
	alertdash.html("~");
	let alertHigh = $("<div class='alertValue'>");

	alertOut.append(alertLow);
	alertOut.append(alertdash);
	alertOut.append(alertHigh);


	alertLow.html(args.al);
	alertHigh.html(args.ah);


	let sumValue = 0; //10 minute sum
	let avgTerm = 10; //minute
	let incAvg = 0;

	let updated = false;

	//let actgrpCurSensorValue = 


	var self = this;

/*
	this.setActGrps = function(Dactgrps) {
		$.each(Dactgrps, function( index, actgrp ) {
			console.log(actgrp);
			if (actgrp.sk == self.knd) {

			}
		});

	};
	*/

	this.getKind = function() {
		return self.knd;
	};

	this.updateAlertInfo = function(param) {
		args.ia = (param.isuse == 1 ? "Y" : "N" );
		args.al = param.al;
		args.ah = param.ah;

		img_alert.attr("src",(param.isuse == 1 ? "./css/iot/images/notice.png" : "./css/iot/images/unnotice.png" ));
		alertLow.html(param.al);
		alertHigh.html(param.ah);

		param.socketId = socketId;
		param.hc = args.house_cde;
		param.nid = args.nid;
		
		TENEFARM.func_updateAlertInfo(param);
	};

	

	this.updateChartView = function(param) {
		//console.log(param);

		args.ic = (param.isuse == 1 ? "Y" : "N" );

		if (args.ic == "Y") {

		} else {

		}
		//dSensors[param.sid].updateChartView(param);
	};

	this.changeColor = function() {
		
	};


	this.updateSensor = function() {
		self.changeColor();
		
	};

	this.notReceive = function() {	
		valueTag.html("--");
	};

	this.refreshData = function(value, tenechart, curTime) {
		let txt = value.toFixed(fixNum);

		if (value == 6454.6) {
			txt = "--";

		} else {

			if (args.sk == "07") {
				var wdv = Number(value - 0).toFixed(0);
			  	wdv = wdv / 45;
			  	wdv = wdv.toFixed(0);
			  	txt = (winddirection[wdv]); 
				 
			} else if (args.sk == "14") {
				value = value / 10;
				txt = value.toFixed(0);				 
			} else if (args.sk == "01") {
				let step = 0;
				txt = rainStep[step];
			}
		}
		if (value == -99)
			txt = "--";

		if (args.sk == "03") { // temp
			args.briefingData.temp = txt;		
			//TENEFARM.br_temp = txt;		 
		} else if (args.sk == "04") { // hum
			args.briefingData.hum = txt;		
			//TENEFARM.br_hum = txt;		 
		} 
		
		valueTag.html(txt);
		if (tenechart) {
			tenechart.shiftData(args.key, value, curTime);
		}

			
		if (value>preValue) {
			img_updown.attr("src","./css/iot/images/sensorup.png");
			valueTag.effect("slide", {direction :"down"}, 500);
		} else if (value<preValue) {
			img_updown.attr("src","./css/iot/images/sensordown.png");
			valueTag.effect("slide", {direction :"up"}, 500);
		} else {
			img_updown.attr("src","./css/iot/images/normal.png");
		}


		//$(".curSensorValue_" + args.house_cde + args.sk).html(txt);
		
		preValue = value;
	};

	let makeTag = function(container,soilIdx) {
				

		let div = $("<div class='commonsensor'>");
		container.append(div);


		let div_header = $("<div class='sensor_header'>");
		let div_title = $("<div class='title'>");
		div_title.css("color","#"+args.dc);
		if ( (args.sk == "11") || (args.sk == "12") ) {
			//div_title.html(args.sm + " " + soilIdx +  (args.un != "" ? ("("+args.un+")") : ""));
			div_title.html(args.sm + " " +  (args.un != null ? ("("+args.un+")") : ""));
		} else {
			div_title.html(args.sm + (args.un != null ? ("("+args.un+")") : ""));
		
		}

		
		let div_container = $("<div class='sensoritem'>");
		let div_status = $("<div class='status'>");
		let div_updown = $("<div class='updown'>");


		
		let div_alert = $("<div class='alert'>");
		div_alert.append(img_alert);
		
		
		div_header.append(div_title);
		div.append(div_header);
		div.append(div_container);
		div_status.append(div_updown);
		div_container.append(div_status);



		div_container.append(valueTag);
		
		div_updown.append(img_updown);
		
		let div_footer = $("<div class='sensor_footer'>");		
		
		if (args.sk.match("03|04|05|11|12")) {
			div.append(div_footer);
			div_footer.append(div_alert);
			div_footer.append(alertOut);
		}
	};


	this.initTrandData = function(initChartData,tenechart) {
		//console.log("initChartData");
		if (tenechart) {
			let hidden = true;
			if (args.sk.match("03|04||11|12")) {
			//if ( (args.sk == "03") || (args.sk == "04") ) {
				hidden = false;
			}
			tenechart.addyAxis(args);
			let item_avg = {
				//type: 'line',
				//label: args.sm +"(평균)",
				label: args.sm,
				pointRadius:0,
				backgroundColor: "#"+args.dc,
				borderColor: "#"+args.dc,
				borderWidth:3,
				data: initChartData.avg,
				fill: false,
				hidden:hidden,
				yAxisID: args.key
			};
			//console.log(item_avg);
			tenechart.addDataset(item_avg);
		}
		
	};

	this.addField = function() {
		historydata.addField(args);

		
	}
	
	this.init = function(sensorCnt,soilIdx) {
		let container_large = args.container_large;
		let container_small = args.container_small;
		makeTag(container_small,soilIdx);
		self.addField();
		
	};

};