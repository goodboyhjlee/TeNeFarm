var Dact_onoff_base = function(args) {
	let tag_id = args.fc+args.con_cde + args.house_cde + args.agid + args.akid + args.key;
	let oppbutton = $('<select  data-role="slider" data-mini="true">');
	oppbutton.attr("id","opp"+tag_id);
	let tagIDActGrp = ""+args.con_cde+args.house_cde+args.agid;
	let divoppbox_openrate = $("<div class='openrate'>");
	divoppbox_openrate.attr("id","cd"+tag_id);

	let socketId = 0;

	let envetChange = function(event, ui) {
		let thisstatus = $(this).val();
		//console.log(thisstatus);
		TENEFARM.func_onoff(socketId, args.house_cde,args.agid,args.akid,args.key,thisstatus);
	};

	this.setSocketID = function(socketID) {
		socketId = socketID;
		oppbutton.bind( "change", envetChange);  

	};

	this.changeOnOffStatus = function( oppstate) {
		//console.log(oppstate);
		if (oppstate >= 2) {
			if (oppstate == 2) 
				TENEFARM.updateCountdown(args.is,tag_id);
			else
				TENEFARM.updateCountdownRemove(tag_id);
		} else {
			oppbutton.unbind("change");
			oppbutton.val(oppstate).slider('refresh');
			oppbutton.bind( "change", envetChange); 
			if (oppstate == 1)	
				TENEFARM.updateCountdownRemove(tag_id);
		}
		
	};

	this.setRunStd = function( oppstate) {
		//console.log(oppstate);
		//$("#opp"+tag_id).val(oppstate).slider('refresh');
	};


	

	let makeManualContent = function(container) {
		//console.log(args);
		let divbox = $("<div>");
		divbox.addClass("itemgrp");
		container.append(divbox);
		let title = $("<div class='title'>");
		title.html(args.nam);

		let divoppbox_btnam = $("<div class='name'>");
		let divoppbox_btnopp = $("<div class='bottons'>");
		divoppbox_btnopp.attr("id","btnbox"+tag_id);
		
		//let divoppbox_slide = $("<div class='slide'>");
		//divoppbox_slide.attr("id","slider"+tag_id);

		divoppbox_btnam.append(title);
		

		divbox.append(divoppbox_btnam);
		divbox.append(divoppbox_openrate);
		divbox.append(divoppbox_btnopp);

		
		oppbutton.addClass("manualbtn");
		oppbutton.attr("name",tagIDActGrp);
		let optRun;
		let optStop;
		
		if (args.kndtype == 13) {
			optRun = $('<option value="1">Off</option>');
			optStop = $('<option value="0">On</option>');
		} else {
			optRun = $('<option value="1">정지</option>');
			optStop = $('<option value="0">가동</option>');
		}
		
		
		oppbutton.append(optRun);
		oppbutton.append(optStop);
		divoppbox_btnopp.append(oppbutton);

		oppbutton.val(Number(args.oppstate));
		
		//oppbutton.bind( "change", envetChange);  

		divbox.trigger("create");
	};

	this.init = function() {

		makeManualContent(args.manual_container);

	
	return;

		
	};
};