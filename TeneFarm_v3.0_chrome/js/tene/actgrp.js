var Dactgrp_base = function(args,parent) {
	//console.log(args);
	let Dact_knd = {};	
	let socketId = 0;

	this.autofilled = false;
	this.autofilledCorr = false;
	this.sk = args.as_knd;
	this.sn_house = args.sn_house;
	this.nid = args.nid;
	this.sid = args.sid;

	this.isAuto;
	let sm =  args.sm;
	let unit =  args.unit;

	let infoPeriod = {
		socketId:socketId,
		con_cde:args.con_cde,
		house_cde:args.house_cde,
		agid:args.key,
		periods:args.actgroup_perioad,
		sm:sm,
		unit:unit,
		ag_knd:args.knd
	};

	this.period = new actgrp_period_base(infoPeriod);

	let infoEtc = {
		socketId:socketId,
		con_cde:args.con_cde,
		house_cde:args.house_cde,
		agid:args.key,
		etc:args.etc
	};
	this.etc = new actgrp_etc_base(infoEtc);

	let infoRoll = {
		socketId:socketId,
		con_cde:args.con_cde,
		house_cde:args.house_cde,
		agid:args.key,
		roll:this.Dact_knd
	};

	this.layerroll = new actgrp_layerroll_base(infoRoll);



	let nam = args.nam;
	let id = args.agid;
	let tagIDActGrp = ""+args.con_cde+args.house_cde+args.key;
	
	
	this.setSocketID = function(socketID) {
		socketId = socketID;
		//console.log("grp", socketId);
		$.each(Dact_knd, function( index, act_knd ) {
			act_knd.setSocketID(socketID);
		});

		self.period.setSocketID(socketID);
		self.etc.setSocketID(socketID);
		self.layerroll.setSocketID(socketID);
	};
	


	let autoManualButton;
	let curSensorValue;


	var self = this;

	this.init = function() {
		let actgrp = $('<div class="actuator_group_out">');
		args.container.append(actgrp);
		let header = $('<div class="topheader">');
		let name = $('<div class="actuator_group_nam">');
		name.addClass("ag"+args.knd);
		name.html(nam);
		header.append(name);	
		//let curSensorValue = $('<div class="actgrp_cur">');
		curSensorValue = $('<div class="actgrp_cur">');
		curSensorValue.addClass("curSensorValue_" + args.house_cde + args.as_knd );
		header.append(curSensorValue);	
		self.period.setSensorValueTag(curSensorValue);
		if (self.sid == 99) {
			curSensorValue.css("font-size","16px");
			//let disptxt = self.period.dispalyCurSensorValue(args.actgroup_perioad);
			//curSensorValue.html(disptxt);
			//if (args.actgroup_perioad.tknd
			//console.log(args);
		} else {
			let curSensorunit = $('<div class="actgrp_unit">');
			curSensorunit.html("("+unit+")/");
			header.append(curSensorunit);	
			let curCorValue = $('<div class="actgrp_cur">');
			curCorValue.addClass("curCorValue_" + args.house_cde + ""+ args.key );
			header.append(curCorValue);	
			let curCorUnit = $('<div class="actgrp_unit">');
			curCorUnit.html("("+unit+")");
			header.append(curCorUnit);	
		}
		
		actgrp.append(header);
		makeAutoManual(header);
		makeManualButtons(actgrp);		
		actgrp.trigger("create");	
		setTimeout(function() {
			setControlEnDis(args.ia == "Y" ? true : false);	
		}, 500);
		
	};

	this.refreshData = function(snData) {
		//console.log(snData);
		//console.log(snData.length);
		//if (self.sid == 99) {
		if (args.knd == "08") {
			let disptxt = self.period.dispalyCurSensorValue(args.actgroup_perioad);
			curSensorValue.html(disptxt);
		} else {
			let lcnt = snData.length / 3;
			//console.log(lcnt);
			for (let idx=0; idx<lcnt; idx++) {
				let sid = snData[3*idx];
				if (sid == self.sid) {
					let high = snData[(3*idx)+1];
					let low = snData[(3*idx)+2];

					let buf_value = [high, low];
		    		let value = (gutil.byte2Int(buf_value)) / 10;
		    		curSensorValue.html(value);
		    		break;
				}
			}
		}
		
		//
	};

	this.refreshSharedData = function(hc, nid, snData) {
		if ((hc = self.sn_house) && (nid == self.nid)) {
			let lcnt = snData.length / 3;
			for (let idx=0; idx<lcnt; idx++) {
				let sid = snData[3*idx];
				if (sid == self.sid) {
					let high = snData[(3*idx)+1];
					let low = snData[(3*idx)+2];

					let buf_value = [high, low];
		    		let value = (gutil.byte2Int(buf_value)) / 10;
		    		curSensorValue.html(value);
				}
			}
		}
			
	};

	this.setCurCorValue = function(curTime) {
		//console.log(curTime);
		//console.log(self.period);
		self.period.getCurPeriod(curTime);
	};
	

	this.func_sendConfigPriorityIsUse = function(house_cde, agid,prioID, isuse) {	
		TENEFARM.func_oppfit   (socketId, house_cde,agid,prioID,isuse);  
		self.isChangeing = true;
	};

	this.getID= function() {
		return id;
	};

	this.getName= function() {
		return nam;
	};

	this.getActName = function(aid) {
		return Dacts[aid].getName();
	};

	this.getActSubName = function(  aid, subid) {		
		return Dacts[aid].getActSubName(subid);
	};

	this.getOppState = function(  aid, subid) {		
		return Dacts[aid].getOppState(subid);
	};

	this.getOpenRate = function( aid, subid) {		
		return Dacts[aid].getOpenRate( subid);
	};

	this.getOppOrder = function( aid, subid) {			
		return Dacts[aid].getOppOrder( subid);
	};

/*
	this.changeStatus = function(  aid, subid, state, openrate) {
		Dact_knd[aid].changeStatus( subid, state, openrate);
	};
	*/

	this.changeStatus = function( akID,layerID,layerSubID, oppstate,openRate) {
		
		Dact_knd[akID].changeStatus(layerID,layerSubID, oppstate,openRate);
	};

	this.changeOnOffStatus = function( akID,onoffID,oppstate) {
		Dact_knd[akID].changeOnOffStatus(onoffID,oppstate);
	};

	this.changeAutoManual = function(isAuto) {
		self.isAuto = isAuto==1
		setControlEnDis(self.isAuto);
		
	};

	this.changeIsLocal = function(isLocal) {
		//self.isAuto
		
		if ((isLocal==1)) {
			autoManualButton.prop('checked', false).checkboxradio('refresh');;
			autoManualButton.checkboxradio('disable');
			$(".manualbtn[name='" + tagIDActGrp + "']").slider('disable');
			$(".selectedoppslider[name='" + tagIDActGrp + "']" ).slider('disable');	
			$(".bottom[name='" + tagIDActGrp + "']" ).addClass("notview");
		} else {
			autoManualButton.checkboxradio('enable');
			if (self.isAuto) {
				$(".manualbtn[name='" + tagIDActGrp + "']").slider('disable');
				$(".selectedoppslider[name='" + tagIDActGrp + "']" ).slider('disable');	
				$(".bottom[name='" + tagIDActGrp + "']" ).addClass("notview");
			} else {
				$(".manualbtn[name='" + tagIDActGrp + "']").slider('enable');
				$(".selectedoppslider[name='" + tagIDActGrp + "']" ).slider('enable');
				$(".bottom[name='" + tagIDActGrp + "']" ).removeClass("notview");
			}
			
		}
	};

	let setControlEnDis = function(isAuto) {
		//console.log(parent.getIsLocal());
		
		autoManualButton.prop('checked', isAuto).checkboxradio('refresh');
		/*
		if (parent.getIsLocal() == 1) {
			$(".manualbtn[name='" + tagIDActGrp + "']").slider('disable');
			$(".selectedoppslider[name='" + tagIDActGrp + "']" ).slider('disable');	
			$(".bottom[name='" + tagIDActGrp + "']" ).addClass("notview");
		} else {
			*/
			if (isAuto) {
				$(".manualbtn[name='" + tagIDActGrp + "']").slider('disable');
				$(".selectedoppslider[name='" + tagIDActGrp + "']" ).slider('disable');	
				$(".bottom[name='" + tagIDActGrp + "']" ).addClass("notview");
			} else {
				$(".manualbtn[name='" + tagIDActGrp + "']").slider('enable');
				$(".selectedoppslider[name='" + tagIDActGrp + "']" ).slider('enable');
				$(".bottom[name='" + tagIDActGrp + "']" ).removeClass("notview");
			}
		//}
		
	};

	this.insPeriod = function( key) {
		self.period.insPeriod(key);
	};

	this.delPeriod = function( key) {
		self.period.delPeriod(key);
	};

	this.changeOpenrate = function( data) {
		var agid_akCnt = pad(data[0].toString(2),8);
		var akCnt =  parseInt(agid_akCnt.substring(4,8), 2);
		let pos = 1;
		for (let akidx=0; akidx<akCnt; akidx++ ) {
			let akDataSize =  data[pos++];
			let akData = data.slice(pos, pos+akDataSize);
			var akid_alCnt = pad(akData[0].toString(2),8);
  			var akid =  parseInt(akid_alCnt.substring(0,4), 2);
			pos = pos+akDataSize;
			Dact_knd[akid].changeOpenrate( akData);		
		}
	};

	this.setOppOrder = function(  aid, subid, order) {	
		Dacts[aid].setOppOrder(  subid, order);
	};
	
	this.changeColor = function( aid, subid) {
		return Dacts[aid].changeColor(subid);
	};

	this.refreshDataGrp = function(valueCor) {
		let curvv = Number($("#realcurvalue"+tagIDActGrp).html());
		corValue.html(valueCor);
		if (curvv<valueCor)
			curValue.css("color","blue");
		else
			curValue.css("color","red");
	};

	

	let makeRealCorValues = function(actgrp) {
		let box = $('<div class="realcorbox">');
		actgrp.append(box);		
		box.append(corTitle);
		box.append(curValueUnit);
		box.append(curValue);		
		box.append(conValueUnit);
		box.append(conValue);		
		if (args.cor) {
			box.append(corValueUnit);
			box.append(corValue);	
		}		
	};

	


	let makeAutoConditionTab = function(container) {
		//console.log(args);
		let tabs = $('<div data-role="tabs">');
		tabs.attr("id","tabs_condition"+tagIDActGrp);

		let navbar = $('<div data-role="navbar">');
		navbar.attr("id","navbar_condition"+tagIDActGrp);
		let ul = $('<ul>');
		ul.attr("id","ul_corection"+tagIDActGrp);
		tabs.append(navbar);
		navbar.append(ul);

		//주기
		let period_container;
		if (args.cor || args.etc) {
			let li_period = $('<li></li>');
			li_period.attr("id","li_period"+tagIDActGrp);
			let a_period = $('<a  class="ui-btn-active" >주기</a>');
			a_period.attr("href","#period_contatiner"+tagIDActGrp);
			li_period.append(a_period);
			ul.append(li_period);
			period_container = $('<div class="">');
			period_container.attr("id","period_contatiner"+tagIDActGrp);
			tabs.append(period_container);
		} else {
			//if (args.knd == "08") {

			//} else {
				period_container = $('<div class="">');
				period_container.attr("id","period_contatiner"+tagIDActGrp);
				container.append(period_container);
			//}
			
		}

		

		//옵션
		let etc_container;
		if (args.etc) {
			let li_etc = $('<li></li>');
			li_etc.attr("id","li_etc"+tagIDActGrp);
			let a_etc = $('<a>옵션</a>');
			a_etc.attr("href","#correction_etc"+tagIDActGrp);
			li_etc.append(a_etc);
			ul.append(li_etc);
			etc_container = $('<div class="limit">');
			etc_container.attr("id","correction_etc"+tagIDActGrp);
			tabs.append(etc_container);	
		}

		//보정계수
		let corection_container;
		if (args.cor) {
			let li_corection = $('<li></li>');
			li_corection.attr("id","li_corection"+tagIDActGrp);
			let a_corection = $('<a>보정계수</a>');
			a_corection.attr("href","#corection_contatiner"+tagIDActGrp);
			li_corection.append(a_corection);
			ul.append(li_corection);
			corection_container = $('<div class="">');
			corection_container.attr("id","corection_contatiner"+tagIDActGrp);
			tabs.append(corection_container);
		}

		let roll_container = $('<div class="">');
		if (args.knd.match("01|03")) {
		//if (args.knd == "01") {			
			let li_roll = $('<li></li>');
			li_roll.attr("id","li_roll"+tagIDActGrp);
			let a_roll = $('<a>역활설정</a>');
			a_roll.attr("href","#roll_contatiner"+tagIDActGrp);
			li_roll.append(a_roll);
			ul.append(li_roll);
			//roll_container = $('<div class="">');
			roll_container.attr("id","roll_contatiner"+tagIDActGrp);
			tabs.append(roll_container);
		}	

		if (corection_container || etc_container) {
			container.append(tabs);
		}

		let ret = {};
		if (args.knd.match("01|03")) {
		//if (args.knd == "01") {
			ret = {
			per:((period_container != undefined) ? period_container : null),
			cor:((corection_container != undefined) ? 
				corection_container : null),
			etc:((etc_container != undefined) ? etc_container : null),
			roll:((roll_container != undefined) ? roll_container : null),
			knd:args.knd
			};
		} else {
			ret = {
			per:((period_container != undefined) ? period_container : null),
			cor:((corection_container != undefined) ? 
				corection_container : null),
			etc:((etc_container != undefined) ? etc_container : null),
			roll:null,
			knd:args.knd
			};
		}

		return ret;
	};

	let makeAutoManual = function(actgrp) {
		let div_btn = $("<div class='button'>");
		div_btn.html("");
		let icon = $("<div class='icon autocon'>");
		actgrp.append(div_btn);
		div_btn.append(icon);

		let value = $('<div class="auto ">');
		actgrp.append(value);
		self.isAuto = (args.ia == "Y" ? true : false);
		autoManualButton = $('<input type="checkbox" >');
		autoManualButton.prop('checked', self.isAuto);

		let label = $('<label >자동</label>');
		value.append(label);
		label.append(autoManualButton);

		autoManualButton.bind( "change", function(event, ui) {
			var opp = ($(this).prop("checked")) ? 1 : 0;
			let fags = {
				socketId:socketId,
				house_cde:args.house_cde,
				agid:args.key,
				opp:opp
			}
			TENEFARM.func_am(fags);
		});

		div_btn.click(function(){
			$("#pop_sensor_title").html("자동운전조건" + " (" + nam + ")" );
			let priority_container = $("#pop_sensor_container");
			priority_container.empty();
			let coretc_contatiner = makeAutoConditionTab(priority_container);

			self.period.init(coretc_contatiner.per, nam, coretc_contatiner.knd,curSensorValue); 
	    	self.etc.init(coretc_contatiner.etc);
	    	//console.log(args);
	    	if (args.knd.match("01|03")) {
	    	//if (args.knd == "01") {
	    		self.layerroll.init(coretc_contatiner.roll, Dact_knd, unit);
	    	}
			priority_container.trigger("create");
			$( "#senser_alert" ).popup("open");
		});
	};

	let makeManualButtons = function(actgrp) {
		//수동운전
		let manual_container = $('<div class="manual">');
		manual_container.attr("id","manual"+tagIDActGrp);
		actgrp.append(manual_container);
		if (args.act_knd)
		$.each(args.act_knd, function( index, info ) {
			info.fc = args.fc;
			info.con_cde = args.con_cde;
			info.house_cde = args.house_cde;
			info.agid = args.key;
			info.manual_container = manual_container;
			info.socketId = socketId;
			Dact_knd[info.key] = new Dact_knd_base(info);
			Dact_knd[info.key].init();
		});
	};

	
	

	
	
};