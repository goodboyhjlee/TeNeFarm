var Dact_layer_base = function(args) {
	let Dact_layersub = {};

	let socketId = 0;

	this.setSocketID = function(socketID) {
		socketId = socketID;
		$.each(Dact_layersub, function( index, actsub_layer ) {
			actsub_layer.setSocketID(socketID);
		});
	};

	
	this.updateLayerRoll = function(sendArgs) {
		args.highsval = sendArgs.highsval;
		args.crate = sendArgs.crate;
		args.risuse = sendArgs.risuse;
	};

	this.getInfo = function() {
		//console.log(args);
		let ret = {
			key:args.key,
			nam:args.nam,
			layer:args.layer,
			highsval:args.highsval,
			risuse:args.risuse,
			crate:args.crate

		};
		return ret;

	};

	this.changeOpenrate = function( data) {
		var alid_lsCnt = pad(data[0].toString(2),8);
		var lsCnt =  parseInt(alid_lsCnt.substring(4,8), 2);
		let pos = 1;
		for (let lsidx=0; lsidx<lsCnt; lsidx++ ) {
			let id_status =  pad(data[pos++].toString(2),8);
  			var lsid =  parseInt(id_status.substring(0,4), 2);
  			var status =  parseInt(id_status.substring(4,8), 2);
  			let openrate = data[pos++];
			Dact_layersub[lsid].changeOpenrate( status, openrate);
		}
	};

	this.changeStatus = function( layerSubID, oppstate,openRate) {
		Dact_layersub[layerSubID].changeStatus( oppstate,openRate);
	};

	
	let makeLimitContent = function(container, item) {		
		let div_subname = $("<div class='subname'>");
		div_subname.html(item.nam);
		container.append(div_subname);

		let div_open = $("<div class='open'>");
        var value_open = $('<div>');
        div_open.append(value_open);
        container.append(div_open);
       	
        var input_fulltime_open =  $('<input type="text" data-role="spinbox" data-mini="true"  class="limitspinbox"  min="10" max="600" />');   
  		input_fulltime_open.css("font-family","digitalFont");
	    input_fulltime_open.css("width","20px");  
	    input_fulltime_open.css("font-size","16px");
  		value_open.append(input_fulltime_open);

       
       	let div_close = $("<div class='close'>");
        var value_close = $('<div>');
        div_close.append(value_close);
        container.append(div_close);

        var input_fulltime_close =  $('<input type="text" data-role="spinbox" data-mini="true" class="limitspinbox"  min="10" max="600" />');   
  		input_fulltime_close.css("font-family","digitalFont");
	    input_fulltime_close.css("width","20px");  
	    input_fulltime_close.css("font-size","16px");
  		value_close.append(input_fulltime_close);


  		let div_max = $("<div class='max'>");
        var value_max = $('<div>');
        div_max.append(value_max);
        container.append(div_max);
        
        var input_fulltime_max =  $('<input type="text" data-role="spinbox" data-mini="true"  class="limitspinbox"  min="10" max="100" />');   
  		input_fulltime_max.css("font-family","digitalFont");
	    input_fulltime_max.css("width","20px");  
	    //input_fulltime_max.css("font-size","16px");
  		value_max.append(input_fulltime_max);

  		let div_btns = $("<div class='btns'>");
  		let div_btn = $("<div class='btn'>");

  		//var button_add = $('<button class="ui-btn ui-icon-star ui-btn-icon-left ui-mini" data-role="button" inline="true" >추가</button>');

        var button_save = $('<button class="ui-btn ui-icon-check ui-btn-icon-notext ui-mini" data-role="button" inline="true" >저장</button>');
        div_btns.append(div_btn);
        div_btn.append(button_save);
        container.append(div_btns);

        if (item.lo === undefined)
  		    input_fulltime_open.val(10);
  		else
  		    input_fulltime_open.val(Number(item.lo).toFixed(0));
        
        if (item.lc === undefined)
  		    input_fulltime_close.val(10);
  		else
  		    input_fulltime_close.val(Number(item.lc).toFixed(0));
  		    
  		if (item.lm === undefined)
  		    input_fulltime_max.val(10);
  		else
  		    input_fulltime_max.val(Number(item.lm).toFixed(0));  
  		  
  		  button_save.click(function() {
  		  	jConfirm("저장 하시겠습니까?","확인", function(e) {
			    if (e) {
			    	var fulltime_open = Number(input_fulltime_open.val());
	    			var fulltime_close = Number(input_fulltime_close.val());	    			
	    			var fulltime_max = Number(input_fulltime_max.val());

	    			Dact_layersub[item.key].setMaxRate(fulltime_max);

	    			item.lo = fulltime_open;
	    			item.lc = fulltime_close;
	    			item.lm = fulltime_max;

	    			let fargs = {
		            	socketId:socketId,
		            	house_cde:item.house_cde,
				      	agid:item.agid,
		              	akid:item.akid,
		              	layerid:item.layer,
		              	subid:item.key,
		              	open:fulltime_open,
		              	close:fulltime_close,
		              	max:fulltime_max
		            };
	    			TENEFARM.func_sendConfigLimit(fargs);
    			}
    			});
    		});
	};

	this.init = function() {
		let div_actlayer = $("<div class='act_layer'>");
  		args.manual_container.append(div_actlayer);
		let div_layer_nam = $("<div class='layer_name'>");
		div_layer_nam.addClass("ly"+args.kndtype);
		div_layer_nam.html(args.kndnam + " #" + args.nam);
		div_actlayer.append(div_layer_nam);

		let icon = $("<div class='icon window'>");
		div_actlayer.append(icon);

		icon.click(function(){
			$("#pop_sensor_title").html("구동조건" + " (" + args.kndnam + " " + args.nam + ")" );
			let out_container = $("#pop_sensor_container");
			out_container.empty();

			let div_container = $('<div class="limit">');
			out_container.append(div_container);

			let div_item_header = $('<div class="header">');
			div_container.append(div_item_header);

			let div_item_contatiner = $('<div class="layers">');				
			div_container.append(div_item_contatiner);

			let div_reset_contatiner = $('<div class="reset">');				
			div_container.append(div_reset_contatiner);

			let div_reset_desc = $('<div class="desc">');
			//div_desc.html("내부온도가 " + "<span style='color:red;'>높을 때</span>"  + " 창을 " + "<span style='color:red;'>차광</span>" +  " 용도로 사용합니다.");
			div_reset_desc.html("주) 수동모드로 동작후 개방도가 불일치할 경우 <br> 창을 " + "<span style='color:red;'>완전히 닫거나 열고</span>" + " 아래의 버튼을 클릭해 주십시요.");				
			div_reset_contatiner.append(div_reset_desc);

			let div_reset_btns = $('<div class="btns">');
			div_reset_contatiner.append(div_reset_btns);

			let div_reset_btn_0 = $('<div class="btn">');
			div_reset_btns.append(div_reset_btn_0);
			var button_save_0 = $('<button class="ui-btn ui-icon-refresh ui-btn-icon-left ui-mini" data-role="button" inline="true" ><span style="color:#0b4073;font-size: 20px;font-family: digitalFont;">0%</span>로 초기화</button>');
	        div_reset_btn_0.append(button_save_0);

	        let div_reset_btn_100 = $('<div class="btn">');
			div_reset_btns.append(div_reset_btn_100);			
	        var button_save_100 = $('<button class="ui-btn ui-icon-refresh ui-btn-icon-left ui-mini" data-role="button" inline="true" ><span style="color:#0b4073;font-size: 20px;font-family: digitalFont;">100%</span>로 초기화</button>');
	        div_reset_btn_100.append(button_save_100);

	        button_save_0.click(function() {
  		  		jConfirm(args.kndnam + " " + args.nam + "을(를) 0%로 초기화 하시겠습니까?","확인", function(e) {
				    if (e) {
				    	$.each(Dact_layersub,function(index,lsub) {
				    		lsub.setOpenRateReset(0);
				    	});
				    	

				    	let fargs = {
			            	socketId:socketId,
			            	house_cde:args.house_cde,
					      	agid:args.agid,
			              	akid:args.akid,
			              	layerid:args.key,
			              	rate:0
			            };
		    			TENEFARM.func_sendOpenRateReset(fargs);
	    			}
    			});
    		});

    		button_save_100.click(function() {
  		  		jConfirm(args.kndnam + " " + args.nam + "을(를) 100%로 초기화 하시겠습니까?","확인", function(e) {
				    if (e) {
				    	$.each(Dact_layersub,function(index,lsub) {
				    		lsub.setOpenRateReset(100);
				    	});
				    	let fargs = {
			            	socketId:socketId,
			            	house_cde:args.house_cde,
					      	agid:args.agid,
			              	akid:args.akid,
			              	layerid:args.key,
			              	rate:100
			            };
		    			TENEFARM.func_sendOpenRateReset(fargs);
	    			}
    			});
    		});
	        

			let div_header_layernam = $('<div class="sub layersub_nam">');
			div_header_layernam.html("구분");
			div_item_header.append(div_header_layernam);

			let div_header_high = $('<div class="sub layersub_open">');
			div_header_high.html("열림(초)");
			div_item_header.append(div_header_high);
			
			let div_header_crate = $('<div class="sub layersub_close">');
			div_header_crate.html("닫힘(초)");
			div_item_header.append(div_header_crate);

			let div_header_isuse = $('<div class="sub  layersub_max">');
			div_header_isuse.html("최대열림(%)");
			div_item_header.append(div_header_isuse);

			//out_container.append(div_item_header);

			$.each(args.act_layer_sub, function( index, info ) {	
				//console.log(info);
				let limit_sublayer_container = $("<div class='limit_sub'>");
				div_item_contatiner.append(limit_sublayer_container);
				makeLimitContent(limit_sublayer_container,info);	
			});

			//let limit_container = $("#pop_sensor_container");
			out_container.trigger("create");
			$(".limitspinbox").spinbox({type:"horizontal"});
			$( "#senser_alert" ).popup("open");
		});

		let div_sublayer_container = $("<div>");
		div_actlayer.append(div_sublayer_container);
		

		if (args.act_layer_sub)
		$.each(args.act_layer_sub, function( index, info ) {			
			info.container = div_sublayer_container;
			info.fc = args.fc;
			info.con_cde = args.con_cde;
			info.house_cde = args.house_cde;
			info.agid = args.agid;
			info.akid = args.akid;
			info.layer = args.key;
			info.socketId = socketId;
			Dact_layersub[info.key] = new Dact_layersub_base(info);
			Dact_layersub[info.key].init();
		});
		
	};
};