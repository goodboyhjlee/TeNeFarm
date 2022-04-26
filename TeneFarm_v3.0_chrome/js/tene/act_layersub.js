var Dact_layersub_base = function(args) {
	//console.log(args);
	let con_cde = args.con_cde;
	let house_cde = args.house_cde;
	let agid = args.agid;
	let akid = args.akid;
	let layerid = args.layer;
	let id = args.key;


	let tagIDActGrp = ""+args.con_cde+args.house_cde+args.agid;

	let oppbutton = $('<select  data-role="slider" data-mini="true">');
	let current_rate = $("<div class='current_rate'>");
	let inputrate = $('<input  data-highlight="true" min="0" max="' + args.lm + '"  class="selectedoppslider">');

	let socketId = 0;

	this.setSocketID = function(socketID) {
		socketId = socketID;
	};


	this.changeOpenrate = function( status, openrate) {
		
		oppbutton.val(0).slider('refresh');
		current_rate.html(openrate);		
	};

	this.changeStatus = function(  oppstate,openrate) {
		oppbutton.val(oppstate).slider('refresh');
		current_rate.html(openrate);
		
	};

	this.setMaxRate = function(maxRate) {
		inputrate.attr("max",maxRate);
		inputrate.slider("refresh");
	};

	this.setOpenRateReset = function(rate) {
		console.log(rate);
		current_rate.html(rate);
	};
	

	let makeManualContent = function(container) {
		let divbox = $("<div>");
		divbox.addClass("itemgrp");
		container.append(divbox);
		let div_top = $("<div class='top'>");
		let div_bottom = $("<div class='bottom'>");
		div_bottom.attr("name",tagIDActGrp);
		//layer sub name
		let title = $("<div class='title'>");
		title.html(args.nam);
		let divoppbox_btnam = $("<div class='name'>");
		let divoppbox_btnopp = $("<div class='bottons'>");
		let divoppbox_openrate = $("<div class='openrate'>");
		let divoppbox_slide = $("<div class='slide'>");
		divoppbox_btnam.append(title);
		div_top.append(divoppbox_btnam);
		div_top.append(divoppbox_openrate);
		div_top.append(divoppbox_btnopp);
		div_bottom.append(divoppbox_slide);
		divbox.append(div_top);
		divbox.append(div_bottom);
		//operation button
		oppbutton.addClass("manualbtn");
		oppbutton.attr("name",tagIDActGrp);
		let optRun = $('<option value="1">정지</option>');
		let optStop = $('<option value="0">가동</option>');
		
		oppbutton.append(optRun);
		oppbutton.append(optStop);
		
		divoppbox_btnopp.append(oppbutton);
		oppbutton.val(Number(1));

		
		//order fit
		let sliderBox = $("<div class='sliderBox'>");
		let divoppbox_input = $("<div class='oppbox_input' data-role='fieldcontain'>");
		inputrate.attr("name",tagIDActGrp);
		inputrate.attr("dir","0");
		inputrate.val(Number(args.of));
		inputrate.css("font-size","24px");
		divoppbox_input.append(inputrate);
		sliderBox.append(divoppbox_input);
		divoppbox_slide.append(sliderBox);
		$( ".selectedoppslider" ).slider();

		//current rate
		current_rate.html(Number(args.or));
		let current_rate_unit = $("<div class='current_rate_unit'>%</div>");
		divoppbox_openrate.append(current_rate);
		divoppbox_openrate.append(current_rate_unit);

		oppbutton.bind( "change", function(event, ui) {
			//console.log(Number(current_rate.html()));
			let cRate = Number(current_rate.html());
			let iRate = inputrate.val();

			if (cRate == iRate) {
				oppbutton.val(1);
				jAlert("현재 " + "<span style='color:red;font-size:20px;font-family:digitalFont;'>" + cRate + "%</span>"  + 
					" 로 설정되어 있습니다.\n슬라이드를 조정하여 개방율을 변경하십시요.","설정알림");
			} else {
				let thisstatus = $(this).val();
				if (thisstatus == 0)
					TENEFARM.func_oppfit   (socketId, args.house_cde,args.agid,args.akid,args.layer,args.key, iRate);
				else if (thisstatus == 1) 
					TENEFARM.func_layersub_stop(socketId, args.house_cde,args.agid,args.akid,args.layer,args.key);
			}
		});  

		divbox.trigger("create");
	};

	this.init = function() {
		makeManualContent(args.container);
	};
};