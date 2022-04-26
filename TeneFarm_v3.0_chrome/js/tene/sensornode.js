
//scene.add(sensornode);
var Dsensornode_base = function(args, historydata) {
	//console.log(args);
	let nid = args.key;
	let tagID = "sn"+args.con_cde+args.house_cde+args.key;
	//console.log(tagID);
	let dSensors = {};	
	let batguage ;
	let isview = false;

	let div_chart = $("<div class='chart'>");
	div_chart.css("display","none");
	let tenechart;

	let setInitDataForChart = function() {
		//console.log(sensornode);
		//if (args.iu == "Y") {
			let params = {
				id : "tranddata_v2",
				farm_cde:args.fc,
				house_cde:args.house_cde,
				formURL : "getdatalist",
		    };
	    	//console.log(params);
	    	blight.read_get(params,	function(result) {
	    		//console.log(result);
	    		let initTrandData = result.data_list;
	    		let labels= [];
	    		if (initTrandData) {
	    			$.each(initTrandData, function( index, item ) {	
		    			let mea_dat = item.mea_dat;
		    			mea_dat = mea_dat.split(" ")[1];
		    			let hour = mea_dat.split(":")[0];
		    			let min = mea_dat.split(":")[1];
		    			labels.push(hour+":"+min);
		    		});

		    		let param_charrt = {
		    			container:div_chart,
		    			
		    		};
		    		

		    		//console.log(initTrandData);
		    		if (initTrandData) {
		    			tenechart = new tenechart_base(param_charrt);
			    		tenechart.setLabels(labels);

			    		self.initTrandData(initTrandData, tenechart);
						tenechart.update();	
					}
	    		}
	      	});
		//}
	};

	var self = this;


	this.init = function(data) {
		//console.log(args);
		if (args.iu == "Y") {
			let container = args.container;
			let div_sensornode = $("<div class='sensornode'>");
			container.append(div_sensornode);

			//container.append(div_chart);


			setInitDataForChart();

			let div_sn = $("<div class='title'>");
			div_sn.html(args.snnam);

			let div_bat_out = $("<div class='bat_out'>");
			batguage = $("<div class='bat_rem'>");

			let div_bettary = $("<div class='battery_bg'>");
			
			div_bettary.append(batguage);
			div_bat_out.append(div_bettary);


			//=====================================

			let div_bts = $("<div class='buttons'>");
			
			let div_bt_alert = $("<div class='button alert'>");
			let div_bt_chart = $("<div class='button chart'>");
			

			div_bts.append(div_bt_chart);
			div_bts.append(div_bt_alert);
			div_bts.append(div_bat_out);
			

			//사용
			var input_div_isuse = $('<div>');
	    	input_div_isuse.addClass("isuse");
	        var lable_isuse = $('<label>실시간추이</lable>');
	        let tag_isuse = $('<input type="checkbox" data-mini="true" >');
	        lable_isuse.append(tag_isuse);
	        input_div_isuse.append(lable_isuse);

	        div_bt_chart.click(function(){
				isview = !isview;
				//console.log(isview);
				if (isview)
	        		div_chart.css("display","inline-block");
	        	else
	        		div_chart.css("display","none");
			});

			div_bt_alert.click(function(){
				$("#pop_sensor_title").html("알림설정" + " (" + name + ")" );
				let sensornodes = args.sensornode;

				let div_container = $("#pop_sensor_container");
				div_container.empty();
				//$.each(sensornodes,function(index,sensornode) {
					//if (sensornode.knd != "99")
					$.each(args.sensor,function(index,item) {
						//console.log(item);
						if (item.sk.match("03|04|05|11|12")) {
							let div_out = $('<div class="container">');
							div_container.append(div_out);

							let div_itembox_cvalue = $('<div class="header">');		
							div_out.append(div_itembox_cvalue);
							//title
							let div_title_cvalue = $('<div class="title">');
							div_title_cvalue.html(item.sm +"("+item.un+")");
							div_itembox_cvalue.append(div_title_cvalue);

							//values
							let div_values = $('<div class="values">');
							div_out.append(div_values);

							//범위
							let div_itembox_range = $('<div class="range">');		
							div_values.append(div_itembox_range);
								
							//value
							let div_value_range = $('<div class="value">');
							let tag_range = $('<div data-role="rangeslider">');
							let range_start = $('<input   type="number" />');
							range_start.attr("min",item.cl);
							range_start.attr("max",item.ch);
							range_start.val(item.al);

							let range_end = $('<input  type="number" />');
							range_end.attr("min",item.cl);
							range_end.attr("max",item.ch);
							range_end.val(item.ah);
							
							tag_range.append(range_start);
							tag_range.append(range_end);
						    div_value_range.append(tag_range);
							div_itembox_range.append(div_value_range);
							tag_range.rangeslider();  

							//사용
							var input_div_isuse = $('<div>');
					    	input_div_isuse.addClass("isuse");
					        var lable_isuse = $('<label>사용</lable>');
					        let tag_isuse = $('<input type="checkbox" data-mini="true" >');
					        lable_isuse.append(tag_isuse);
					        input_div_isuse.append(lable_isuse);
					        tag_isuse.prop('checked', item.ia == "Y" ? true : false).checkboxradio();
					        div_values.append(input_div_isuse);	

					        //저장
					        let div_save = $('<div class="save">');
							var button_save = $('<button class="ui-btn ui-icon-check ui-btn-icon-notext ui-mini" data-role="button" inline="true" >&nbsp; 저장</button>');
							div_save.append(button_save);
					        div_values.append(div_save);
							

							button_save.click(function(){
								jConfirm("저장 하시겠습니까?","확인", function(e) {
								    if (e) {
								    	let nid = args.key;
								    	let sid = item.key;

								    	let param = {
								    		sid:sid,
								    		al:range_start.val(),
								    		ah:range_end.val(),
								    		isuse: (tag_isuse.is(':checked') ? 1 : 0)
								    	};
								    	self.updateAlertInfo(param);
					    			}
				    			});
							});
						}
						

					});
				//});
				$( "#senser_alert" ).popup("open");
			});



			//======================================
			

			//let div_large = $("<div class='large'>");
			let div_header = $("<div class='header'>");
			let div_small = $("<div class='small'>");
			div_header.append(div_sn);
			div_header.append(div_bts);
			//div_header.append(div_bat_out);
			div_sensornode.append(div_header);
			div_sensornode.append(div_small);
			div_sensornode.append(div_chart);
			let sensorCnt = args.sensor.length;
			let soilIdx = 0;
			let soilcheck = false;
			$.each(args.sensor, function( index, info ) {
				if ( (info.sk == "11")  && !soilcheck) {
					soilIdx++;
					soilcheck = true;
				} 

				if (info.sk == "12") {
				//	soilIdx--;
					soilcheck = false;
				}

				//console.log(info);
				info.fc = args.fc;
				info.con_cde = args.con_cde;
				info.house_cde = args.house_cde;
				//info.facilityInfo = args.facilityInfo;
				info.agid = args.agid;
				info.knd = args.knd;
				info.briefingData = args.briefingData;

				//info.container_large = div_large;
				info.container_small = div_small;
				dSensors[info.key] = new Dsensor_base(info, historydata);
				dSensors[info.key].init(sensorCnt,soilIdx);
				
			});
		}
	};

	this.initTrandData = function(data,tenechart) {
		//console.log(data);
		//console.log("sensornode");
		$.each(dSensors, function( index, sensor ) {
			if (data) {
				let knd_avg = "f_" + nid + "_" + sensor.getKind();
				let eachDatas_avg = [];
				$.each(data, function( idx, item ) {
					eachDatas_avg.push(Number(item[knd_avg]));
				});

				let data_chart = {
					avg:eachDatas_avg
				};
				//console.log(data_chart);
				sensor.initTrandData(data_chart, tenechart);
			} else {
				
			}
		});
		
	};

/*
	this.setActGrps = function(Dactgrps) {
		$.each(dSensors, function( index, sensor ) {
			sensor.setActGrps(Dactgrps);
		});

	};
	*/

	
	this.updateSensor = function() {
		$.each(dSensors, function( index, sensor ) {
			sensor.updateSensor();
		});

		$.each(sensornode.children,function(i,child) {
		if (child.name == "common")	
			child.material.color.setHex( 0xFFFFFF );
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

	this.notReceive = function() {	
		$.each(dSensors, function( index, sensor ) {
			sensor.notReceive();
		});	
		batguage.css("height","0px");	
	};

	//this.refreshData = function(sid,value,Dactgrps) {
	this.refreshData = function(sCnt, rev_data, curTime, battery) {
		//console.log("SN:",args.key);
		//console.log(sCnt, battery);
		//containerBAT.html(battery);
		//battery:37 = x:100;
		let rate =  (battery * 1) / 37;
		//console.log(rate);
		//let rate = (battery - 32 ) / 5;
		if (rate > 1)
			rate = 1;
		let height = 24 * rate;
		//console.log(rate,height);
		batguage.css("height",height+"px");
		if (battery < 33) {
			batguage.css("height","2px");
			batguage.css("background-color","red");
		} else {
			batguage.css("background-color","#035f9a");
		}

		//if (args.key == 6)
		//	console.log("send", args.key);
		let pos = 0;
		for (let sidx=0; sidx<sCnt; sidx++) {
	    	let sid = rev_data[pos++]; 
	    	
	    	let ff = rev_data[pos++];
			let ss = rev_data[pos++];
			//console.log("sid",sid);
	    	//console.log(ff,ss);
	    	let buf_value = [ff, ss];
	    	let value = (gutil.byte2Int(buf_value)) / 10;
	    	

	    	//console.log(tenechart);
	    	//console.log(sid,value );
	    	if (dSensors[sid]) {
	    		dSensors[sid].refreshData(value, tenechart, curTime);	
	    	}
	    	
	    	
	    	//Dhouses[hc].refreshData(nid,sid,value);
	    }

		//if (dSensors[sid])
		//	dSensors[sid].refreshData(value,Dactgrps);
	};

	this.updateAlertInfo = function(param) {
		//console.log(param);
		dSensors[param.sid].updateAlertInfo(param);
	};

	this.updateChartView = function(param) {
		//console.log(param);
		dSensors[param.sid].updateChartView(param);
	};

	
	this.setSocketID = function(socketID) {
		socketId = socketID;
		$.each(dSensors, function( index, sensor ) {
			sensor.setSocketID(socketID);
		});
		
	};
	

	
	
	
};