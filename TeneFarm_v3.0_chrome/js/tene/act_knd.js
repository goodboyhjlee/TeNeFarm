var Dact_knd_base = function(args) {
	let Dact_layer = {};
	let Dact_onoff = {};

	let socketId = 0;

	this.setSocketID = function(socketID) {
		socketId = socketID;
		
		$.each(Dact_layer, function( index, act_layer ) {
			act_layer.setSocketID(socketID);
		});
		$.each(Dact_onoff, function( index, act_onoff ) {
			act_onoff.setSocketID(socketID);
		});
	};

	this.getKey = function() {
		return args.key;
	};

	this.updateLayerRoll = function(sendArgs) {
		let actLayer = Dact_layer[sendArgs.key];
		actLayer.updateLayerRoll(sendArgs);
	};

	this.getIsLayer = function() {
		return (args.knd == "01") || (args.knd == "06") || (args.knd == "07") || (args.knd == "08") ;
	};

	this.getLayers = function() {
		let ret = {
			key:args.key,
			nam:args.nam,
			layerlist:[]
		};
		
		$.each(Dact_layer, function( index, act_layer ) {
			let layerinfo = act_layer.getInfo();
			ret.layerlist.push(layerinfo);
		});

		return ret;
	};

	this.changeOpenrate = function( data) {
		
		var akid_alCnt = pad(data[0].toString(2),8);
		var alCnt =  parseInt(akid_alCnt.substring(4,8), 2);
		let pos = 1;
		for (let alidx=0; alidx<alCnt; alidx++ ) {
			let alDataSize =  data[pos++];
			let alData = data.slice(pos, pos+alDataSize);
			var alid_lsCnt = pad(alData[0].toString(2),8);
  			var alid =  parseInt(alid_lsCnt.substring(0,4), 2);
			pos = pos+alDataSize;
			Dact_layer[alid].changeOpenrate( alData);
		}
	};

	this.changeStatus = function( layerID,layerSubID, oppstate,openRate) {
		Dact_layer[layerID].changeStatus(layerSubID, oppstate,openRate);
	};

	this.changeOnOffStatus = function( onoffID,oppstate) {
		Dact_onoff[onoffID].changeOnOffStatus(oppstate);
	};

	

	let makeOnOffContent = function(container, item) {	
		//console.log(item)	;
		let div_subname = $("<div class='subname'>");
		div_subname.html(item.nam);
		container.append(div_subname);

		let div_open = $("<div class='open'>");
        var value_open = $('<div>');
        div_open.append(value_open);
        container.append(div_open);
       	
        var input_fulltime_open =  $('<input type="text" data-role="spinbox" data-mini="true"  class="limitspinbox"  min="1" max="60" />');   
  		input_fulltime_open.css("font-family","digitalFont");
	    input_fulltime_open.css("width","20px");  
	    input_fulltime_open.css("font-size","16px");
  		value_open.append(input_fulltime_open);

       
       	let div_close = $("<div class='close'>");
        var value_close = $('<div>');
        div_close.append(value_close);
        container.append(div_close);

        var input_fulltime_close =  $('<input type="text" data-role="spinbox" data-mini="true" class="limitspinbox"  min="1" max="60" />');   
  		input_fulltime_close.css("font-family","digitalFont");
	    input_fulltime_close.css("width","20px");  
	    input_fulltime_close.css("font-size","16px");
  		value_close.append(input_fulltime_close);

  		//isuse
		let div_isuse = $('<div class="isuse">');	
		var value_isuse = $('<div>');
        div_isuse.append(value_isuse);
        container.append(div_isuse);
		
        var lable_isuse = $('<label>사용</lable>');
        let tag_isuse = $('<input type="checkbox" data-mini="true" >');
        lable_isuse.append(tag_isuse);
        value_isuse.append(lable_isuse);
        tag_isuse.prop('checked', item.it_use == "Y" ? true : false).checkboxradio();


  		let div_btns = $("<div class='btns'>");
  		let div_btn = $("<div class='btn'>");

        var button_save = $('<button class="ui-btn ui-icon-check ui-btn-icon-notext ui-mini" data-role="button" inline="true" >저장</button>');
        div_btns.append(div_btn);
        div_btn.append(button_save);
        container.append(div_btns);

        if (item.ir === undefined)
  		    input_fulltime_open.val(10);
  		else
  		    input_fulltime_open.val(Number(item.ir).toFixed(0));
        
        if (item.is === undefined)
  		    input_fulltime_close.val(10);
  		else
  		    input_fulltime_close.val(Number(item.is).toFixed(0));
  		  
  		  button_save.click(function() {
  		  	jConfirm("저장 하시겠습니까?","확인", function(e) {
			    if (e) {
			    	var fulltime_open = Number(input_fulltime_open.val());
	    			var fulltime_close = Number(input_fulltime_close.val());
	    			let it_use =  (tag_isuse.is(':checked') ? "Y" : "N");
	    			Dact_onoff[item.key].setRunStd(fulltime_open,fulltime_close, it_use );

	    			item.ir = fulltime_open;
	    			item.is = fulltime_close;
	    			item.it_use = it_use;

	    			let fargs = {
		            	socketId:socketId,
		            	house_cde:item.house_cde,
				      	agid:item.agid,
		              	akid:item.akid,
		              	onoffid:item.key,
		              	run:fulltime_open,
		              	std:fulltime_close,
		              	it_use:(tag_isuse.is(':checked') ? 1 : 0)
		            };
	    			TENEFARM.func_sendRunStd(fargs);
    			}
    			});
    		});
	};

	let waveToRGB = function(w) {	
		//let w = $(this).val();

		  	let a = 0;
		  	let gamma = 0.8;
			let IntensityMax = 255.0;
			let factor = 0;

		  	let red = 0;
			let blue = 0;
			let green = 0;

			  if (w >= 380 && w <= 440) {
			    //Serial.println(w);
			    
			    red = (-1 * (w - 440.0)) / (440.0 - 380.0);
			    green = 0.0;
			    blue = 1.0;
			    //
			  }
			  else if (w > 440 && w <= 490) {
			    red = 0.0;
			    green = (w - 440.0) / (490.0 - 440.0);
			    blue = 1.0;
			  }
			  else if (w > 490 && w <= 510) {
			    red = 0.0;
			    green = 1.0;
			    blue = -(w - 510.0) / (510.0 - 490.0);
			  }
			  else if (w > 510 && w <= 580) {
			    red = (w - 510.0) / (580.0 - 510.0);
			    green = 1.0;
			    blue = 0.0;
			  }
			  else if (w > 580 && w <= 645) {
			    red = 1.0;
			    green = -(w - 645.0) / (645.0 - 580.0);
			    blue = 0.0;
			    //Serial.println(w);
			    //Serial.println(green);
			  }
			  else if (w > 645 && w <= 780) {
			    red = 1.0;
			    green = 0.0;
			    blue = 0.0;
			  }
			  else {
			    red = 0.0;
			    green = 0.0;
			    blue = 0.0;
			  }



			  if((w >= 380) && (w<420)){
			    factor = 0.3 + 0.7*(w - 380.0) / (420.0 - 380.0);
			  }else if((w >= 420) && (w<701)){
			    factor = 1.0;
			  }else if((w >= 701) && (w<781)){
			    factor = 0.3 + 0.7*(780.0 - w) / (780.0 - 700.0);
			  }else{
			    factor = 0.0;
			  };
			  
			  if (red != 0)
			  	red = Math.round(IntensityMax * Math.pow(red * factor, gamma));
			  if (green != 0)
			  	green = Math.round(IntensityMax * Math.pow(green * factor, gamma));
			  if (blue != 0)
			  	blue = Math.round(IntensityMax * Math.pow(blue * factor, gamma));

			  return {"R":red,"G":green,"B":blue};
	};

	let makeLEDContent = function(container, item) {	
		//console.log(item)	;
		let div_subname = $("<div class='subname'>");
		div_subname.html(item.nam);
		container.append(div_subname);

		let div_open = $("<div class='open'>");
		div_open.css("margin-right","20px"); 
        var value_open = $('<div>');
        value_open.css("width","120px"); 
        value_open.css("display","inline-block"); 
        div_open.append(value_open);
        container.append(div_open);
       	
        var input_fulltime_open =  $('<input type="text"  data-role="spinbox" data-mini="false"  class=""  min="380" max="780" />');   
  		input_fulltime_open.css("font-family","digitalFont");
	    input_fulltime_open.css("width","100px");  
	    input_fulltime_open.css("font-size","16px");
  		value_open.append(input_fulltime_open);


  		let div_color_out = $("<div class=''>");
  		div_color_out.css("display","inline-block"); 
  		div_color_out.css("vertical-align","middle"); 
       	let div_color = $("<div class=''>");
       	div_color.css("width","20px"); 
       	div_color.css("height","20px"); 
       	div_color.css("border","1px solid rgb(51, 51, 51)"); 
       	div_color.css("border-radius","10x"); 
       	div_color_out.append(div_color);
       	div_open.append(div_color_out);


  		
       	


  		let div_btns = $("<div class='btns'>");
  		let div_btn = $("<div class='btn'>");

        var button_save = $('<button class="ui-btn ui-icon-check ui-btn-icon-notext ui-mini" data-role="button" inline="true" >저장</button>');
        div_btns.append(div_btn);
        div_btn.append(button_save);
        container.append(div_btns);


        if (item.ir === undefined)
  		    input_fulltime_open.val(10);
  		else
  		    input_fulltime_open.val(Number(item.ir).toFixed(0));

        let rgb = waveToRGB(item.ir);

		div_color.css("background-color","rgb("+rgb.R+","+rgb.G+","+rgb.B+")");

        

  		input_fulltime_open.change(function(e){
  			//console.log($(this).val());
  			let rgb = waveToRGB($(this).val());

			div_color.css("background-color","rgb("+rgb.R+","+rgb.G+","+rgb.B+")");
			  
  		});
        
        
  		  button_save.click(function() {
  		  	jConfirm("저장 하시겠습니까?","확인", function(e) {
			    if (e) {
			    	var fulltime_open = Number(input_fulltime_open.val());

	    			
	    			//let it_use =  (tag_isuse.is(':checked') ? "Y" : "N");
	    			Dact_onoff[item.key].setRunStd(fulltime_open,1, "N" );

	    			item.ir = fulltime_open;
	    			item.is = 1;
	    			item.it_use = "N";

	    			let fargs = {
		            	socketId:socketId,
		            	house_cde:item.house_cde,
				      	agid:item.agid,
		              	akid:item.akid,
		              	onoffid:item.key,
		              	run:fulltime_open,
		              	std:1,
		              	it_use:0
		            };
	    			TENEFARM.func_sendLED(fargs);
    			}
    			});
    		});
	};

	this.init = function() {
		let manual_container = $('<div class="act_knd">');
		args.manual_container.append(manual_container);

		if (args.act_layer)
		$.each(args.act_layer, function( index, info ) {
			info.manual_container = manual_container;
			info.fc = args.fc;
			info.con_cde = args.con_cde;
			info.house_cde = args.house_cde;
			info.agid = args.agid;
			info.akid = args.key;
			info.kndnam = args.nam;
			info.kndtype = args.knd;

			Dact_layer[info.key] = new Dact_layer_base(info);
			Dact_layer[info.key].init();
		});

		if (args.act_onoff) {
			let header = $("<div class='header'>");
			manual_container.append(header);
			let content = $("<div class='content'>");
			manual_container.append(content);
			let div_layer_nam = $("<div class='layer_name'>");
			div_layer_nam.addClass("ly"+args.knd);
			div_layer_nam.html( args.nam);

			let icon = $("<div class='icon window'>");

			header.append(div_layer_nam);
			header.append(icon);
			$.each(args.act_onoff, function( index, info ) {
				info.manual_container = content;
				info.fc = args.fc;
				info.con_cde = args.con_cde;
				info.house_cde = args.house_cde;
				info.agid = args.agid;
				info.akid = args.key;
				info.kndnam = args.nam;
				info.kndtype = args.knd;

				Dact_onoff[info.key] = new Dact_onoff_base(info);
				Dact_onoff[info.key].init();
			});

			icon.click(function(){
				$("#pop_sensor_title").html("운전조건" + " (" + args.nam + ")" );
				let out_container = $("#pop_sensor_container");
				out_container.empty();
				let div_container = $('<div class="limit">');
				out_container.append(div_container);

				let div_item_header = $('<div class="header">');
				div_container.append(div_item_header);

				let div_item_contatiner = $('<div class="layers">');				
				div_container.append(div_item_contatiner);

				//console.log(args.knd);
				let txtUnit = "분";
				if (args.knd == "04") {
					txtUnit = "초";
				} else if (args.knd == "13") {
					txtUnit = "nm";
				}

				if (args.knd == "13") {
					let div_header_layernam = $('<div class="sub layersub_nam">');
					div_header_layernam.html("구분");
					div_item_header.append(div_header_layernam);

					let div_header_high = $('<div class="sub layersub_open">');
					div_header_high.html("파장(" + txtUnit + ")");
					div_item_header.append(div_header_high);
					
					

					$.each(args.act_onoff, function( index, info ) {
						//console.log(info);
						let limit_sublayer_container = $("<div class='limit_sub'>");
						div_item_contatiner.append(limit_sublayer_container);
						makeLEDContent(limit_sublayer_container,info);
					});
				} else {
					let div_header_layernam = $('<div class="sub layersub_nam">');
					div_header_layernam.html("구분");
					div_item_header.append(div_header_layernam);

					let div_header_high = $('<div class="sub layersub_open">');
					div_header_high.html("동작(" + txtUnit + ")");
					div_item_header.append(div_header_high);
					
					let div_header_crate = $('<div class="sub layersub_close">');
					div_header_crate.html("대기(" + txtUnit + ")");
					div_item_header.append(div_header_crate);

					let div_header_ituse = $('<div class="sub layersub_ituse">');
					div_header_ituse.html("동작대기");
					div_item_header.append(div_header_ituse);

					$.each(args.act_onoff, function( index, info ) {
						//console.log(info);
						let limit_sublayer_container = $("<div class='limit_sub'>");
						div_item_contatiner.append(limit_sublayer_container);
						makeOnOffContent(limit_sublayer_container,info);
					});
				}

				

				//let limit_container = $("#pop_sensor_container");
				out_container.trigger("create");

				

				$( "#senser_alert" ).popup("open");
			});
		}
		

	};
};