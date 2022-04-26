var actgrp_correction_base = function(args) {
	var self = this;

	this.init = function(container) {
		corectionOnExpand(container);
	};

	let corection = function(container) {
		let collapsible_corection = $('<div data-role="collapsible" data-collapsed-icon="carat-d" data-expanded-icon="carat-u" data-inset="true"></div>');
    	let header = $('<h4></h4>');

    	let title = $("<div>");
  		title.addClass("house_nam");
  		title.html("세부설정");
  		header.append(title);
    	collapsible_corection.append(header);
		container.append(collapsible_corection);

		let div_coection_body = $('<div>');
		collapsible_corection.append(div_coection_body);
    	
    	collapsible_corection.collapsible();
    	collapsible_corection.collapsible({
    		expand: function( event, ui ) {  
    						
    			if (!self.autofilledCorr) {
    		    	corectionOnExpand(div_coection_body);  
    		      	self.autofilledCorr = true;
    		    }
    		 }
    	});
	};

	

	let corectionOnExpand = function(container) {
		//console.log(args.cor);
		if (args.cor) {
			let items = [];
			$.each(args.cor, function( index, item ) {
				let min = 0;
				let max = 0;
				if (item.sk == "06") {
					min=0;
					max=1500;
				} else if (item.sk == "14") {
					min=0;
					max=30;
				} else if (item.sk == "04") {
					min=30;
					max=90;
				} else if (item.sk == "02") {
					min=2;
					max=6;
				} else if (item.sk == "07") {
					min=2;
					max=6;
				} else if (item.sk == "01") {
					min=1;
					max=10;
				} else if (item.sk == "08") {
					min=1;
					max=30;
				}
				items.push({corid:item.corid,title:item.nam, unit:item.unit,sk:item.sk, cor_val:item.cor_val, 
					min:min,max:max, 
					cor_min:item.cor_min, cor_max:item.cor_max,
					isuse:item.isuse});
			});


			$.each(items,function(index,item) {
				let div_out = $('<div class="corection">');
				container.append(div_out);
				//main title
				let div_header = $('<div class="header">');
				div_header.html(item.title)
				div_out.append(div_header);


				

				//저장
				let div_save = $('<div class="btn">');
				var button_save = $('<button class="ui-btn ui-icon-check ui-btn-icon-notext ui-mini" data-role="button" inline="true" >&nbsp; 저장</button>');
				
				

				//보정값
				let tag_cvalue;
				let range_start;
				let range_end;
				if ( (item.sk == "04") || (item.sk == "06") || (item.sk == "14") || (item.sk == "08") ) {
					let div_itembox_cvalue = $('<div class="itembox">');		
					div_out.append(div_itembox_cvalue);
						//title
					let div_title_cvalue = $('<div class="title">');
					div_title_cvalue.html("보정값(°C)");
					div_itembox_cvalue.append(div_title_cvalue);
						//value
					let div_value_cvalue = $('<div class="value">');
				    tag_cvalue = $('<input type="text" data-role="spinbox" data-mini="true"   min="0" max="3" />');   
				   // tag_cvalue.attr("corid",item.corid);
				    //tag_cvalue.attr("id","scv"+item.clsnam+tagIDActGrp);
				    tag_cvalue.val(item.cor_val);
				    //corValues[item.sk] = Number(item.cor_val);
				   // humcv = item.cvalue;
				    tag_cvalue.css("font-family","digitalFont");
				    tag_cvalue.css("width","20px");  
				    tag_cvalue.css("font-size","16px");

				    let div_temp_for_tag_cvalue = $("<div>");
				    div_value_cvalue.append(div_temp_for_tag_cvalue);
				    div_temp_for_tag_cvalue.append(tag_cvalue);
					div_itembox_cvalue.append(div_value_cvalue);


					//범위
					let div_itembox_range = $('<div class="itembox">');		
					div_out.append(div_itembox_range);
						//title
					let div_title_range = $('<div class="title">');
					div_title_range.html("범위("+item.unit+")");
					div_itembox_range.append(div_title_range);
						//value
					let div_value_range = $('<div class="value">');
					div_value_range.css("width","250px")

					
					let tag_range = $('<div data-role="rangeslider">');
					range_start = $('<input   type="number" />');
					//range_start.attr("id","smin"+item.clsnam+tagIDActGrp);
					range_start.attr("min",item.min);
					range_start.attr("max",item.max);
					range_start.val(item.cor_min);
					//corMinValues[item.sk] = Number(item.cor_min);
					//hummin = item.min_value;
					range_start.css("font-family","digitalFont");
					range_end = $('<input  type="number" />');
					//range_start.attr("id","smax"+item.clsnam+tagIDActGrp);
					range_end.attr("min",item.min);
					range_end.attr("max",item.max);
					range_end.val(item.cor_max);
					//corMaxValues[item.sk] = Number(item.cor_max);
					//hummax = item.max_value;
					range_end.css("font-family","digitalFont");
					tag_range.append(range_start);
					tag_range.append(range_end);

				    div_value_range.append(tag_range);
					div_itembox_range.append(div_value_range);

					tag_cvalue.spinbox({type:"horizontal"});
					tag_range.rangeslider();  

					div_save.append(button_save);
					div_out.append(div_save);
				} 

				let div_itembox_cvalue = $('<div class="itembox">');		
				div_out.append(div_itembox_cvalue);

				var input_div_isuse = $('<div>');
		    	input_div_isuse.addClass("isuse");
		        var lable_isuse = $('<label>사용</lable>');
		        let tag_isuse = $('<input type="checkbox" data-mini="true" >');
		       // tag_isuse.attr("id","isuse"+item.clsnam+tagIDActGrp);
		        tag_isuse.attr("corid",item.corid);
		        lable_isuse.append(tag_isuse);
		        input_div_isuse.append(lable_isuse);
		        
		        div_itembox_cvalue.append(input_div_isuse);

		        tag_isuse.prop('checked', item.isuse == "Y" ? true : false).checkboxradio();
		        //input_isuse.prop('checked', true).checkboxradio();


				button_save.click(function() {
			    	jConfirm("보정계수를 저장 하시겠습니까?","확인", function(e) {
			    		if (e) {
			    			let corid = item.corid;
			    			let cor_val = tag_cvalue == undefined ? 0 : tag_cvalue.val();
			    			let cor_min = range_start == undefined ? 0 : range_start.val();
			    			let cor_max = range_end == undefined ? 0 : range_end.val();
			    			let isuse =  (tag_isuse.is(':checked') ? 1 : 0);
					      	let sendArgs = {
					      		socketId:args.socketId,
					      		house_cde:args.house_cde,
					      		agid:args.agid,
					      		corid:corid,					      		
					      		cor_val:cor_val,
					      		cor_min:cor_min,
					      		cor_max:cor_max,
					      		isuse:isuse
					      };
					      TENEFARM.func_updateCor(sendArgs);
			          	}
			      	});
			    });
			});
		} else {
			
		}

		return;
		

	};




};