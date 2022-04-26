var Dfarm_base = function(args, parent, historydata) {
	//console.log(historydata);
	var self = this;

	let sunRigeHour = 6;
	let sunRigeMin = 20;
	let sunSetHour = 19;
	let sunSetMin = 10;

	let camangdir = 0;
	let caminc = 0.002;
	let ang = 0;
	let camlen = 50;
	let mistInc = true;
	let runInc = 0;

	let ddns = args.ddns;
	let frm_cde = args.key;
	let frm_nam = args.nam;

	//let Dweatherstation = {};
	this.weatherstation;
	let Dcontrols = {};
	let Dcams = {};
	

	this.parent = parent;

	this.getHouseInfo = function() {
		let ret = {};
		$.each(args.control, function( index, info ) {	
			let controlInfo = Dcontrols[info.key].getHouseInfo();
			ret[info.key] = controlInfo;
		});
		return ret;
	};

	this.init = function(myIP, onReceiveMap) {
		self.addCCTV(args.camera);
		if (args.ws) {
			let info = args.ws[0];
			self.weatherstation = new weatherstaion_base(info,self);
			self.weatherstation.init();
		}

   		$.each(args.control, function( index, info ) {	
			info.fc = frm_cde;
			
			//gutil.sleep(1);
			//if (info.localip == myIP) {
				Dcontrols[info.key] = new Dcontrol_base(info,self, historydata);
				Dcontrols[info.key].init(myIP,onReceiveMap);	
			//}
		  	

		});


    	
	};

	this.addCCTV = function(cams) {
		//console.log(cams);
		if (cams)
		$.each(cams, function( index, cam ) {
			cam.container = $("#cams_container");

			let cambase = new cctv_base(cam, self.scene);
			cambase.init();
		});
	};


	this.onReceive = function(info) {
		//console.log(info);
		onReceiveMap[info.socketId](info);
	};


	this.refreshDataCommon = function(sk,txt) {
		$.each(Dcontrols, function( index, control ) {
			control.refreshDataCommon(sk,txt);
		});
	};

	
	

	this.getID = function() {
		return frm_cde;
	};

	this.getName = function() {
		return frm_nam;
	};

	this.getDDNS = function() {
		return ddns;
	};


	this.getControlName = function(con_cde) {
		return Dcontrols[con_cde].getName();
	};

	this.getSocketId = function(con_cde) {
		return Dcontrols[con_cde].getSocketId();
	};

	this.getSocketId_ws = function(fc) {
		//console.log(fc);
		//return Dweatherstation[fc].getSocketId();
		return self.weatherstation.getSocketId();
	};

	

	this.getHouseName = function(con_cde, house_cde) {
		return Dcontrols[con_cde].getName(house_cde);
	};

	this.getActGrpName = function(device) {		
		return Dcontrols[device.con_cde].getActGrpName(device.house_cde,device.agid);
	};

	this.getActName = function(device) {		
		return Dcontrols[device.con_cde].getActName(device.house_cde,device.agid,device.aid);
	};

	this.getActSubName = function(device) {		
		return Dcontrols[device.con_cde].getActSubName(device.house_cde,device.agid,device.aid, device.subid);
	};

	this.getOppState = function(device) {		
		return Dcontrols[device.con_cde].getOppState(device.house_cde,device.agid,device.aid, device.subid);
	};

	this.getOpenRate = function(device) {		
		return Dcontrols[device.con_cde].getOpenRate(device.house_cde,device.agid,device.aid, device.subid);
	};

	this.getOppOrder = function(device) {		
		return Dcontrols[device.con_cde].getOppOrder(device.house_cde,device.agid,device.aid, device.subid);
	};

	this.setOppOrder = function(device, order) {		
		Dcontrols[device.con_cde].setOppOrder(device.house_cde,device.agid,device.aid, device.subid, order);
	};

	this.changeColor = function(device) {		
		if (device.knd == "sncommon") 
			Dcontrols[device.con_cde].updateSensorCommon(device.nid);
		else if (device.knd == "cam") 
			Dcams[device.cam_cde].changeColor();
		else if (device.knd == "device") 
			Dcontrols[device.con_cde].changeColor(device.house_cde, device.agid, device.aid, device.subid);
	};

	this.changeStatus = function(device, state) {
		Dcontrols[device.con_cde].changeStatus(device.house_cde, device.agid, device.aid, device.subid, state);
	};

	
	
	
	

	let fillContentActGrp = function(popname) {
		$( "#"+popname ).popup( "open" ); 
		//console.log(args);
		let contentDeviceLayer = $("#contentDeviceLayer");
		contentDeviceLayer.empty();
		let controls = args.control;
		$.each(controls, function( index, control ) {

			if (control.iu == "Y") {
				let houses = control.house;
				$.each(houses, function( index, house ) {
					if (house.iu == "Y") {
						let divHouse = $("<div class='house'>");
						let divHouseTitle = $("<div class='title'>");
						let divHouseValue = $("<div class='value'>");
						divHouseTitle.html(house.hm);

						divHouse.append(divHouseTitle);
						divHouse.append(divHouseValue);
						contentDeviceLayer.append(divHouse);
						let actgroups = house.actgroup;
						if (actgroups)
						$.each(actgroups, function( index, actgroup ) {
							//console.log(actgroup);
							let divGrp = $("<div class='actgrp'>");
							let divGrpTitle = $("<div class='title'>");
							let divGrpValue = $("<div class='value'>");

							let select = $('<select class="layeronoff" data-role="slider" data-mini="true">');
							let optionOff = $('<option value="off">Off</option>');
							let optionOn  = $('<option value="on">On</option>');

							divGrpTitle.html(actgroup.nam);
							select.attr("tene-actgrp-layer",actgroup.layer)

							divGrp.append(divGrpTitle);
							divGrp.append(divGrpValue);

							divGrpValue.append(select);
							select.append(optionOff);
							select.append(optionOn);

							divHouseValue.append(divGrp);
						});
					}
				});
			}
			
		});
		$('.layeronoff').slider();	


		$( ".layeronoff" ).bind( "change", function(event, ui) {
		 	//console.log($(this).attr("tene-actgrp-layer"));
		 	let layer = Number($(this).attr("tene-actgrp-layer"));
		 	self.camera.layers.toggle( layer );
		});
		//contentDeviceLayer.trigger("create");
	};

	
	
	
};