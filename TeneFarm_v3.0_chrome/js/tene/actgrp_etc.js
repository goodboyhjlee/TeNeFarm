var actgrp_etc_base = function(args) {
	//console.log(args);

	let socketId = 0;
	var self = this;

	this.setSocketID = function(socketID) {
		socketId = socketID;
	};

	this.init = function(container) {
		makeTags(container);
	};

	let makeTags = function(container) {
		if (args.etc) {
			/*
			let items = [];
			$.each(args.etc, function( index, item ) {
				
				items.push({etcid:item.mkey,title:item.nam, unit:item.unit,sk:item.sk, val:item.val, 
					isuse:item.isuse,
					sk:item.sk});
			});
			*/

			$.each(args.etc,function(index,item) {
				let div_out = $('<div class="corection">');
				container.append(div_out);
				//main title
				let div_header = $('<div class="header">');
				div_header.html(item.nam)
				div_out.append(div_header);

				//저장
				let div_save = $('<div class="btn">');
				var button_save = $('<button class="ui-btn ui-icon-check ui-btn-icon-notext ui-mini" data-role="button" inline="true" >&nbsp; 저장</button>');

				//보정값
				let tag_cvalue;
				let range_start;
				let range_end;
				
				let div_itembox_cvalue = $('<div class="itembox">');		
				div_out.append(div_itembox_cvalue);

				if ( (item.sk == "02") || (item.sk == "06") || (item.sk == "14")) { // wind speed
					
						//title
					let div_title_cvalue = $('<div class="etc_title">');
					div_title_cvalue.html("최대값("+ item.unit + ")");
					div_itembox_cvalue.append(div_title_cvalue);
						//value
					let div_value_cvalue = $('<div class="value">');
				    tag_cvalue = $('<input type="text" data-role="spinbox" data-mini="true"  />');   
				    tag_cvalue.attr("min",item.min);
				    tag_cvalue.attr("max",item.max);
				    tag_cvalue.val(item.val);
				    tag_cvalue.css("font-family","digitalFont");
				    tag_cvalue.css("width","20px");  
				    tag_cvalue.css("font-size","16px");

				    let div_temp_for_tag_cvalue = $("<div>");
				    div_value_cvalue.append(div_temp_for_tag_cvalue);
				    div_temp_for_tag_cvalue.append(tag_cvalue);
					div_itembox_cvalue.append(div_value_cvalue);
					tag_cvalue.spinbox({type:"horizontal"});

					
				} else if (item.sk == "07") { // wind direction
					let div_title_cvalue = $('<div class="etc_title">');
					div_title_cvalue.html("외부온도(°C)");
					div_itembox_cvalue.append(div_title_cvalue);
						//value
					let div_value_cvalue = $('<div class="value">');
				    tag_cvalue = $('<input type="text" data-role="spinbox" data-mini="true"  />');   
				    tag_cvalue.attr("min",item.min);
				    tag_cvalue.attr("max",item.max);
				    tag_cvalue.val(item.val);
				    tag_cvalue.css("font-family","digitalFont");
				    tag_cvalue.css("width","20px");  
				    tag_cvalue.css("font-size","16px");

				    let div_temp_for_tag_cvalue = $("<div>");
				    div_value_cvalue.append(div_temp_for_tag_cvalue);
				    div_temp_for_tag_cvalue.append(tag_cvalue);
					div_itembox_cvalue.append(div_value_cvalue);
					tag_cvalue.spinbox({type:"horizontal"});
				} else if (item.sk == "01") { // rain drop
						//title
					let div_title_cvalue = $('<div class="etc_title">');
					div_title_cvalue.html("강우유형");
					//div_title_cvalue.css("width","160px");
					div_itembox_cvalue.append(div_title_cvalue);
						//value
					let div_value_cvalue = $('<div class="value">');
					tag_cvalue = $('<select data-mini="true"  />');   
					$.each(rainStep, function( val, txt ) {
						//console.log(val,txt);
						if (val>0) {
							let opt = $('<option value="' + val + '">' + txt + '</option>');
							tag_cvalue.append(opt);
						}
					});
				    tag_cvalue.val(item.val);
				   	div_value_cvalue.append(tag_cvalue);
					div_itembox_cvalue.append(div_value_cvalue);
					tag_cvalue.selectmenu();
				} else if (item.sk == "03") { // Temperature
						//title
					let div_title_cvalue = $('<div class="etc_title">');
					div_title_cvalue.html("최대값("+ item.unit + ")");
					div_itembox_cvalue.append(div_title_cvalue);
						//value
					let div_value_cvalue = $('<div class="value">');
				    tag_cvalue = $('<input type="text" data-role="spinbox" data-mini="true"  />');   
				    tag_cvalue.attr("min",item.min);
				    tag_cvalue.attr("max",item.max);
				    tag_cvalue.val(item.val);
				    tag_cvalue.css("font-family","digitalFont");
				    tag_cvalue.css("width","20px");  
				    tag_cvalue.css("font-size","16px");

				    let div_temp_for_tag_cvalue = $("<div>");
				    div_value_cvalue.append(div_temp_for_tag_cvalue);
				    div_temp_for_tag_cvalue.append(tag_cvalue);
					div_itembox_cvalue.append(div_value_cvalue);
					tag_cvalue.spinbox({type:"horizontal"});
				}


				div_save.append(button_save);
				div_itembox_cvalue.append(div_save);

				var input_div_isuse = $('<div>');
		    	input_div_isuse.addClass("isuse");
		        var lable_isuse = $('<label>사용</lable>');
		        let tag_isuse = $('<input type="checkbox" data-mini="true" >');
		        tag_isuse.attr("corid",item.mkey);
		        lable_isuse.append(tag_isuse);
		        input_div_isuse.append(lable_isuse);
		        div_itembox_cvalue.append(input_div_isuse);
		        tag_isuse.prop('checked', item.isuse == "Y" ? true : false).checkboxradio();

		        

				button_save.click(function() {
			    	jConfirm("저장 하시겠습니까?","확인", function(e) {
			    		if (e) {
			    			let etcid = item.mkey;
			    			let val = tag_cvalue == undefined ? 0 : tag_cvalue.val();
			    			let isuse =  (tag_isuse.is(':checked') ? 1 : 0);

			    			item.isuse = (tag_isuse.is(':checked') ? "Y" : "N");

			    			//console.log(item);
			    			

					      	let sendArgs = {
					      		socketId:socketId,
					      		house_cde:args.house_cde,
					      		agid:args.agid,
					      		etcid:etcid,					      		
					      		etc_val:val,
					      		isuse:isuse,
					      		sk:item.sk
					      		//nid:item.nid,
					      		//sid:item.sid
					      };

					    //  console.log(sendArgs);

					      TENEFARM.func_updateEtc(sendArgs);
			          	}
			      	});
			    });
			});
		} else {

		}

	};
};