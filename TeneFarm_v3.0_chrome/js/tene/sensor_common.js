var Dsensor_common_base = function(args) {
	//console.log(args);
	this.knd = args.sk;
	//console.log(args.fc);
	//let socketId= TENEFARM.user.Dfarms[args.fc].getSocketId_ws(args.fc);
	

	let fixNum = 1;

	if (args.sk == "03") {
		fixNum = 1;
	} else if (args.sk == "04") {
		fixNum = 1;
	} else if (args.sk == "06") {
		fixNum = 0;
	}

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

	//alertOut.append(alertLow);
	//alertOut.append(alertdash);
	//alertOut.append(alertHigh);


	alertLow.html(args.al);
	alertHigh.html(args.ah);
	
	var self = this;


	this.changeColor = function() {
		
	};

	this.updateAlertInfo = function(param) {
		args.ia = (param.isuse == 1 ? "Y" : "N" );
		args.al = param.al;
		args.ah = param.ah;

		img_alert.attr("src",(param.isuse == 1 ? "./css/iot/images/notice.png" : "./css/iot/images/unnotice.png" ));
		alertLow.html(param.al);
		alertHigh.html(param.ah);

		param.socketId = socketId;
		//param.hc = args.house_cde;
		param.nid = args.nid;

		//console.log(param);
		
		TENEFARM.func_updateAlertInfo_ws(param);
	};

	this.updateSensor = function() {
		self.changeColor();
		if (  (args.sk == "02") ) {
			if (self.value) {
				
			}
		}
	};

	this.refreshData = function(value) {
		let txt = value.toFixed(fixNum);
		if (args.sk == "07") {	//wind dir		
			var wdv = Number(value - 0).toFixed(0);
		  	wdv = wdv / 45;
		  	wdv = wdv.toFixed(0);
		  	txt = (winddirection[wdv]); 
		  	TENEFARM.br_outwinddir = txt;
			 
		} else if (args.sk == "02") { // wind speed			
			TENEFARM.br_outwindspeed = txt;		 
		} else if (args.sk == "08") { // out temp		
			TENEFARM.br_outtemp = txt;		 
		} else if (args.sk == "09") { // out hum		
			TENEFARM.br_outhum = txt;		 
		} else if (args.sk == "14") {			
			txt = value.toFixed(0);			 
		} else if (args.sk == "01") {			
			if (value > 0 ) {
				txt = "비";
			} else {
				txt = "--";
			}

			TENEFARM.br_raindrop = txt;	
			
		}

		
		valueTag.html(txt);

		if ( (self.knd == "08") || (self.knd == "09") || (self.knd == "06") || (self.knd == "02") ) {
			if (value>preValue) {
				img_updown.attr("src","./css/iot/images/sensorup.png");
				valueTag.effect("slide", {direction :"down"}, 500);
			} else if (value<preValue) {
				img_updown.attr("src","./css/iot/images/sensordown.png");
				//valueTag.effect("slide", 500);
				valueTag.effect("slide", {direction :"up"}, 500);
			} else {
				img_updown.attr("src","./css/iot/images/normal.png");
			}
		}
		
		
		preValue = value;
		//self.divValue.html(txt);

		//$(".realcurvalue"+args.sk).html(txt);

		
	};

	this.updownNormal = function() {
		img_updown.attr("src","./css/iot/images/normal.png");
	};

	let makeTag = function(container) {
		let div = $("<div class='commonsensor'>");
		let div_header = $("<div class='sensor_header'>");
		let div_title = $("<div class='title'>");
		div_title.css("color","#"+args.dc);
		div_title.html(args.sm + (args.un != null ? ("("+args.un+")") : ""));
		//div_title.html(args.sm + (args.un != "" ? ("("+args.un+")") : ""));
		let div_container = $("<div class='sensoritem'>");
		
		
		
		//let img_alert = $("<img>");
		img_alert.attr("src",(args.ia == "Y" ? "./css/iot/images/notice.png" : "./css/iot/images/unnotice.png" ));


		//let alertOut = $("<div class='alertOut'>");
		//let alertLow = $("<div class='alertValue'>");
		//let alertdash = $("<div class='alertValue'>");
		//alertdash.html("~");
		//let alertHigh = $("<div class='alertValue'>");

		if (self.knd.match("08|02")) {
			if (self.knd == "08") {
				alertOut.append(alertLow);
				alertOut.append(alertdash);
				alertOut.append(alertHigh);
				alertLow.html(args.al);
				alertHigh.html(args.ah);
			} else {

				alertOut.append(alertHigh);
				
				alertHigh.html(args.ah);
			}
			
		}
		

		div_header.append(div_title);
		div.append(div_header);
		div.append(div_container);
		
		

		if ( (self.knd == "08") || (self.knd == "09") || (self.knd == "06") || (self.knd == "02") ) {
			let div_status = $("<div class='status'>");
			let div_updown = $("<div class='updown'>");
			div_updown.append(img_updown);
			div_status.append(div_updown);
			div_container.append(div_status);
			valueTag.css("width","90px");
			valueTag.css("text-align","center");
		} else {
			valueTag.css("text-align","center");
			valueTag.css("width","100%");
		}

		let div_alert = $("<div class='alert'>");
		div_alert.append(img_alert);

		div_container.append(valueTag);
		container.append(div);

		let div_footer = $("<div class='sensor_footer'>");

		
		
		if (self.knd.match("08|01|02")) {
			div.append(div_footer);
			div_footer.append(div_alert);
			div_footer.append(alertOut);
		}

		

		if ( (self.knd == "01") || (self.knd == "07") ) {
			valueTag.css("font-size","30px");
		}

		
	};
	
	this.init = function() {
		let container = args.container;
		makeTag(container);
		
	
	};

};