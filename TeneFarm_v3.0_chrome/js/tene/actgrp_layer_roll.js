var actgrp_layerroll_base = function(args) {
	//console.log(args);

	let socketId = 0;
	var self = this;

	this.setSocketID = function(socketID) {
		socketId = socketID;
	};

	this.init = function(container, dactknd, unit) {
		//makeTags(container);
		//console.log(container, dactknd);

		let div_desc = $('<div class="roll_desc">');

		div_desc.html("내부온도가 " + "<span style='color:red;'>높을 때</span>"  + " 창을 " + "<span style='color:red;'>차광</span>" +  " 용도로 사용합니다.");
		container.append(div_desc);

		let ret = {

		};
		$.each(dactknd, function( index, actknd ) {
			//console.log(actknd);
			if (actknd.getIsLayer()) {
				//title
				

				let layerlist = actknd.getLayers();
				//console.log(layerlist);	
				let div_out = $('<div class="roll">');
				container.append(div_out);
				let div_header = $('<div class="knd_title">');
				div_header.html(layerlist.nam);
				div_out.append(div_header);

				let div_item_header = $('<div class="header">');

				let div_header_layernam = $('<div class="sub layer_nam">');
				div_header_layernam.html("구분");
				div_item_header.append(div_header_layernam);

				let div_header_high = $('<div class="sub layer_high">');
				div_header_high.html("온도("+ unit + ")");
				div_item_header.append(div_header_high);

				
				let div_header_crate = $('<div class="sub layer_crate">');
				div_header_crate.html("닫힘(%)");
				div_item_header.append(div_header_crate);

				let div_header_isuse = $('<div class="sub  layer_isuse">');
				div_header_isuse.html("사용");
				div_item_header.append(div_header_isuse);

				div_out.append(div_item_header);

				let div_item_contatiner = $('<div class="layers">');				
				div_out.append(div_item_contatiner);

				makeTags(div_item_contatiner, layerlist.layerlist, unit, actknd);
			}
			
		});
	};

	let makeTags = function(container, layerlist, unit, actknd) {
		$.each(layerlist,function(index,item) {
			//console.log(item);	
			let div_layer = $('<div class="layer">');
			container.append(div_layer);
			let div_title = $('<div class="layer_title">');
			div_title.html(item.nam);
			div_layer.append(div_title);
			
//====item
			let div_itembox_high = $('<div class="high">');		
			div_layer.append(div_itembox_high);
				
				//value
			let div_value_high = $('<div class="value">');
		    let tag_high = $('<input type="text" data-role="spinbox" data-mini="true"  />');   
		    //tag_high.attr("min",30);
		    tag_high.attr("min",30);
		    tag_high.attr("max",50);
		    tag_high.val(item.highsval);
		    tag_high.css("font-family","digitalFont");
		    tag_high.css("width","20px");  
		    tag_high.css("font-size","16px");

		    let div_temp_for_tag_high = $("<div>");
		    div_value_high.append(div_temp_for_tag_high);
		    div_temp_for_tag_high.append(tag_high);
			div_itembox_high.append(div_temp_for_tag_high);
			tag_high.spinbox({type:"horizontal"});

//====item
			let div_itembox_crate = $('<div class="crate">');		
			div_layer.append(div_itembox_crate);
				
				//value
			let div_value_crate = $('<div class="value">');
		    let tag_crate = $('<input type="text" data-role="spinbox" data-mini="true"  />');   
		    tag_crate.attr("min",30);
		    tag_crate.attr("max",70);
		    tag_crate.val(item.crate);
		    tag_crate.css("font-family","digitalFont");
		    tag_crate.css("width","20px");  
		    tag_crate.css("font-size","16px");

		    let div_temp_for_tag_crate = $("<div>");
		    div_value_crate.append(div_temp_for_tag_crate);
		    div_temp_for_tag_crate.append(tag_crate);
			div_itembox_crate.append(div_temp_for_tag_crate);
			tag_crate.spinbox({type:"horizontal"});

			//isuse
			let div_itembox_isuse = $('<div class="isuse">');		
			div_layer.append(div_itembox_isuse);

			
	        var lable_isuse = $('<label>사용</lable>');
	        let tag_isuse = $('<input type="checkbox" data-mini="true" >');
	       // tag_isuse.attr("corid",item.mkey);
	        lable_isuse.append(tag_isuse);
	        div_itembox_isuse.append(lable_isuse);
	        tag_isuse.prop('checked', item.risuse == "Y" ? true : false).checkboxradio();


			//저장
			let div_save = $('<div class="btns">');
			var button_save = $('<button class="ui-btn ui-icon-check ui-btn-icon-notext ui-mini" data-role="button" inline="true" >&nbsp; 저장</button>');

			div_save.append(button_save);
			div_layer.append(div_save);

			button_save.click(function() {
			    	jConfirm("저장 하시겠습니까?","확인", function(e) {
			    		if (e) {
			    			
			    			let key = item.key;
			    			let highsval = Number(tag_high == undefined ? 0 : tag_high.val());
			    			item.highsval = highsval;
			    			let crate = Number(tag_crate == undefined ? 0 : tag_crate.val());
			    			item.crate = crate;
			    			let isuse =  (tag_isuse.is(':checked') ? 1 : 0);
			    			item.risuse = (tag_isuse.is(':checked') ? "Y" : "N");

			    			//console.log(actknd);

			    			let updateArgs = {
					      		key:key,					      		
					      		highsval:highsval,
					      		crate:crate,
					      		risuse:item.risuse
					      	};

					      	

			    			actknd.updateLayerRoll(updateArgs);
			    			

					      	let sendArgs = {
					      		socketId:socketId,
					      		house_cde:args.house_cde,
					      		agid:args.agid,
					      		akid:actknd.getKey(),
					      		key:key,					      		
					      		highsval:highsval,
					      		crate:crate,
					      		risuse:isuse
					      	};

					      	TENEFARM.func_updateRoll(sendArgs);
			          	}
			      	});
			    });


			
		});
			
		
			

	};
};