var weatherstaion_base = function(args, parent) {
	//console.log(args);
	var self = this;
	let dSensors = {};
	this.init = function() {
		//console.log(args.sensornode);
		if (args.sensornode) {
			let containerSensorNode = makeTag();
			$.each(args.sensornode, function( index, sensornode ) {
				$.each(sensornode.sensor, function( index, info ) {
					info.fc = args.fc;
					info.container = containerSensorNode;
					dSensors[info.key] = new Dsensor_common_base(info);
					dSensors[info.key].init();
					

				});
			});
		}
		
		
	};

	this.updateSensorCommon = function(nid) {
		//console.log(nid);
		if (DsensornodesCommon[nid])
			DsensornodesCommon[nid].updateSensor();
	};

	this.refreshData = function(sid,value) {
		if (dSensors[sid])
			dSensors[sid].refreshData(value);
	};

	let rRdataCommon = function(data) {
		//console.log(data);
		$.each(DsensornodesCommon,function(index,node) {
			node.updownNormal();
		});
		let rev_data = new Uint8Array(data);
		let pos = 2;
		let sngCnt = rev_data[pos++];
		
		for (let sngidx=0; sngidx<sngCnt; sngidx++) {
			let snCnt = rev_data[pos++];
			for (let snidx=0; snidx<snCnt; snidx++) {
				var base2 = pad(rev_data[pos++].toString(2),8);
				let substr = base2.substring(0,4);
				var nid =  parseInt(substr, 2);
				substr = base2.substring(4,8);
				var sCnt =  parseInt(substr, 2);
			    for (let sidx=0; sidx<sCnt; sidx++) {
			    	let sid = rev_data[pos++]; 

			    	let buf_value = [rev_data[pos++], rev_data[pos++]];
  			    	let value = (gutil.byte2Int(buf_value)) / 10;

  			    	//console.log(nid);

  			    	DsensornodesCommon[nid].refreshData(sid,value);
			    }
			}
		}
		
		
	};

	let makeTag = function() {
		let container = $("#weather_station");
		let div = $("<div class='commonsensornode_ws'>");

		let div_header = $("<div class='header'>");

		let div_title = $("<div class='title'>");
		div_title.html("기상");

		let div_bts = $("<div class='buttons'>");
		let div_bt_alert = $("<div class='button alert'>");
		div_bts.append(div_bt_alert);
		div_header.append(div_bts);

		let suninfo = $('<div class="suninfo">');
		let sunrise = $('<div class="suntitle sunrise"></div><div class="sunvalue" id="sunrise_time"></div>');
		let moonrise = $('<div class="suntitle moonrise"></div><div class="sunvalue" id="sunset_time"></div>');
		suninfo.append(sunrise);
		suninfo.append(moonrise);
		let div_container = $("<div class='container'>");
		div_header.append(div_title);
		//div_header.append(div_bts);
		div_header.append(suninfo);


		
		
		div.append(div_header);

		div.append(div_container);
		container.append(div);



		div.trigger("create");
		container.append(div);

		div_bt_alert.click(function(){
				$("#pop_sensor_title").html("알림설정" + " (" + "기상" + ")" );
				let sensornodes = args.sensornode;

				let div_container = $("#pop_sensor_container");
				div_container.empty();
				//$.each(sensornodes,function(index,sensornode) {
					//if (sensornode.knd != "99")
					$.each(args.sensornode[0].sensor,function(index,item) {
						//console.log(item);
						//if (item.sk.match("08|09|01|02")) {
						if (item.sk.match("08|01|02")) {
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

							let range_start = $('<input   type="number" />');
							let range_end = $('<input  type="number" />');
							let tag_cvalue;

							if (item.sk == "02") {
								let div_itembox_range = $('<div class="range">');		
								div_values.append(div_itembox_range);
								let div_value_range = $('<div class="value">');
								
							    tag_cvalue = $('<input type="text" data-role="spinbox" data-mini="true"  />');   
							    tag_cvalue.attr("min",item.cl);
							    tag_cvalue.attr("max",item.ch);
							    tag_cvalue.val(item.ah);
							    tag_cvalue.css("font-family","digitalFont");
							    tag_cvalue.css("width","20px");  
							    tag_cvalue.css("font-size","16px");							    
								

							   	div_value_range.append(tag_cvalue);
								div_itembox_range.append(div_value_range);

								tag_cvalue.spinbox({type:"horizontal"});
							} else if (item.sk == "01") {
								let div_itembox_range = $('<div class="range">');		
								div_values.append(div_itembox_range);
								let div_value_range = $('<div class="value">');

								tag_cvalue = $('<select data-mini="true"  />');   
								$.each(rainStep, function( val, txt ) {
									//console.log(val,txt);
									if (val>0) {
										let opt = $('<option value="' + val + '">' + txt + '</option>');
										tag_cvalue.append(opt);
									}
								});
							    tag_cvalue.val(1);

							   	div_value_range.append(tag_cvalue);
								div_itembox_range.append(div_value_range);

								tag_cvalue.selectmenu();
							} else {
								//범위
								let div_itembox_range = $('<div class="range">');		
								div_values.append(div_itembox_range);
									
								//value
								let div_value_range = $('<div class="value">');
								let tag_range = $('<div data-role="rangeslider">');
								
								range_start.attr("min",item.cl);
								range_start.attr("max",item.ch);
								range_start.val(item.al);

								
								range_end.attr("min",item.cl);
								range_end.attr("max",item.ch);
								range_end.val(item.ah);
								
								tag_range.append(range_start);
								tag_range.append(range_end);
							    div_value_range.append(tag_range);
								div_itembox_range.append(div_value_range);
								tag_range.rangeslider(); 
							}
							

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
								    	let farm_cde = args.key;
								    	let nid = item.nid;
								    	let sid = item.key;

								    	let param = {};
								    	if (item.sk == "02") {
								    		param = {
									    		//farm_cde:farm_cde,
									    		nid:nid,
									    		sid:sid,
									    		al:0,
									    		ah:tag_cvalue.val(),
									    		isuse: (tag_isuse.is(':checked') ? 1 : 0)
									    	};
								    	} else if (item.sk == "01") {
								    		param = {
									    		//farm_cde:farm_cde,
									    		nid:nid,
									    		sid:sid,
									    		al:0,
									    		ah:0,
									    		isuse: (tag_isuse.is(':checked') ? 1 : 0)
									    	};

								    	} else {
											param = {
									    		//farm_cde:farm_cde,
									    		nid:nid,
									    		sid:sid,
									    		al:range_start.val(),
									    		ah:range_end.val(),
									    		isuse: (tag_isuse.is(':checked') ? 1 : 0)
									    	};
								    	}

								    	 
								    	//console.log(param)
								    	self.updateAlertInfo(param);
					    			}
				    			});
							});
						}
						

					});
				//});
				div_container.trigger("create");
				$( "#senser_alert" ).popup("open");
			});

		return div_container;
	};


	this.updateAlertInfo = function(param) {
		//console.log(param);
		dSensors[param.sid].updateAlertInfo(param);
	};
	
}