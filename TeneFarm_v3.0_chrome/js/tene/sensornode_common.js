var Dsensornode_common_base = function(args) {
	
	let tagID = "sn"+args.con_cde+args.nid;
	let dSensors = {};
	
	var self = this;

	this.init = function() {
		let container = args.container;
		
		$.each(args.sensor, function( index, info ) {
			info.fc = args.fc;
			//info.con_cde = args.con_cde;
			//info.house_cde = args.house_cde;
			//info.agid = args.agid;
			info.knd = args.knd;
			info.container = container;
			dSensors[info.key] = new Dsensor_common_base(info);
			dSensors[info.key].init();
		});
		
	};

	this.updateAlertInfo = function(param) {
		//console.log(param);
		dSensors[param.sid].updateAlertInfo(param);
	};

	

	this.updateSensor = function() {
		$.each(dSensors, function( index, sensor ) {
			sensor.updateSensor();
		});
	};

	this.removeData = function() {
		/*
		if (sensornode)
		$.each(sensornode.children, function( index, child ) {
			//console.log(child);
			if (child)
			if (child.name != "title")
				sensornode.remove(child);

		});
		*/
	};

	this.refreshData = function(sid,value) {
		//console.log(sid,value);
		if (dSensors[sid])
			dSensors[sid].refreshData(value);
	};

	this.updownNormal = function() {
		$.each(dSensors,function(index,sensor) {
			sensor.updownNormal();
		});
	};


	
	
	
};