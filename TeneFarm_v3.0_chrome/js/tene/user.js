var user_base = function(args, parent) {

	//console.log(args);
	
	this.usr_id = args.usr_id;
	this.usr_ni = args.usr_ni;
	this.Dfarms = {};

	this.onReceive = function(info) {
		$.each(self.Dfarms, function( index, farm ) {
			farm.onReceive(info);
		});
	};

	this.getHouseInfo = function() {
		let ret = {};
		$.each(self.Dfarms, function( index, farm ) {
			let farmInfo = farm.getHouseInfo();
			//ret[farm.key] = 
		}); 
		return self.Dfarms;
	};
	
	var self = this;
	
	this.init = function(myIP, data, historydata, onReceiveMap) {
		//console.log(data);
		//console.log(historydata);
		$.each(data, function( index, info ) {
			//console.log(info);
			self.Dfarms[info.key] = new Dfarm_base(info, self, historydata);
			self.Dfarms[info.key].init(myIP, onReceiveMap);
		});
	};
};