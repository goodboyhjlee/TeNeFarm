var actgrp_period_base = function(args) {
	//console.log(args);

	let tenechart;
	let chartData = []; // call by ref
	let labels= [];
	let table_body = $("<tbody>");

	let socketId = 0;
	let con_cde = args.con_cde;
	let house_cde = args.house_cde;
	let agid = args.agid;
	let ag_knd = args.ag_knd;
	let periods = args.periods;
	let stepCnt = periods.length;
	let chart;

	this.curSensorValue;
	//if (periods)
	//	chart = new priority_chart_base(periods[0]);

	//console.log(stepCnt);
	

	var self = this;

	this.setSocketID = function(socketID) {
		socketId = socketID;
	};

	let getBeginPeriod = function(endkey) {
		let beginkey = 0;
		if (endkey == 0) {
			beginkey = stepCnt-1;
		} else {
			beginkey = endkey-1;
		}	
		return periods[beginkey];
		
		//return mapActGroupPeriod.get(beginkey);
	};

	this.getCurPeriod = function(curTime) {
		//console.log(curTime, periods);
		
		let beginPeriod;
		let endPeriod;
		let curCorValue = 0;
		let step = 0;
		for (let i=0; i<stepCnt; i++) {
			period = periods[i];
			//console.log(curTime,period );
			if (curTime <= period.hm) {
				endPeriod = period;
				step = i;
				beginPeriod = getBeginPeriod(step);
				break;
			}
		}

		
		//console.log(beginPeriod);
		//console.log(endPeriod);

		if (typeof endPeriod == "undefined") {
			step = 0;
			endPeriod = periods[step];
			beginPeriod = getBeginPeriod(step);
		}

		let begingab = curTime - beginPeriod.hm;
		let timegab = endPeriod.hm - beginPeriod.hm;
		let rate = (begingab*1.0) / (timegab*1.0);
		//System.out.println("rate : " + rate + "  " + begingab + "  " + timegab);
		//timegab : 100 = begingab : x		
		

		let beginSval = Number(beginPeriod.sval);
		let endSval = Number(endPeriod.sval);

		//console.log(beginSval, endSval);

		//console.log(rate);
		
		
		if (beginSval > endSval) {
			curCorValue = beginSval - ((beginSval - endSval) * rate);
		} else if (beginSval < endSval) {
			curCorValue = beginSval + ((endSval - beginSval) * rate);
		} else {
			curCorValue = endSval;
		}
			
			
		//console.log("sss", curCorValue);
		$(".curCorValue_" + house_cde + ""+ agid).html(Number(curCorValue).toFixed(1));

		if (typeof self.curSensorValue != "undefined") {
			let curssvalue = Number(self.curSensorValue.html());
			if (Math.abs(curssvalue-curCorValue)>=0.5) {
				if (curssvalue > curCorValue) {
					self.curSensorValue.css("color","red");
				} else if (curssvalue < curCorValue) {
					self.curSensorValue.css("color","blue");
				}

			} else {
				self.curSensorValue.css("color","#000404");
				
			}
			

			//console.log(self.curSensorValue.html());
		}

		//curCorValue.addClass("curCorValue_" + args.house_cde + ""+ args.key );
		
		
	};

/*
	this.getCurPeriod = function(curTime) {
		//console.log(curTime, periods);
		let len = periods.length;
		let beginPeriod = "";
		let endPeriod = "";
		let curCorValue = 0;
		for (let i=0; i<len; i++) {
			beginPeriod = periods[i];
			
			if (i < (len-1))
				endPeriod = periods[i+1];
			else
				endPeriod = periods[0];

			let beginVal = Number(beginPeriod.sval);
			let endVal = Number(endPeriod.sval);
			let beginHm = Number(beginPeriod.hm);
			let endHm = Number(endPeriod.hm);	

			//console.log(beginPeriod, endPeriod);
							

			if ( (endHm - beginHm) > 0 ) {
				if ( (curTime >= beginHm) && (curTime < endHm) ) {
					let overRate = (curTime - beginHm) / (endHm - beginHm);
					curCorValue = beginVal + ((endVal - beginVal) * overRate);
					//console.log(beginPeriod, endPeriod);
					//console.log(overRate, curCorValue);
					break;
				}

			} else {	
				let overRate = 0;			
				if ((curTime - endHm) > 0 ) { //befor 24
					overRate = (curTime - beginHm) / ( ((24*60) - beginHm) + endHm);
					//console.log("sss", overRate);
				} else { // over 24
					overRate = ( ((24*60) - beginHm) + curTime) / ( ((24*60) - beginHm) + endHm);
				}
				curCorValue = beginVal + ((endVal - beginVal) * overRate);
				//console.log("sss", curCorValue);
			}

			
		}
		//console.log("sss", curCorValue);
		$(".curCorValue_" + house_cde + ""+ agid).html(Number(curCorValue).toFixed(0));

		//curCorValue.addClass("curCorValue_" + args.house_cde + ""+ args.key );
		
		
	};
	*/

	this.setCurSensorData = function(curTime, sensorValue) {
		chart.setCurSensorData(curTime, sensorValue);
	};

	

	let fill_range = function(periods,table_body, pKind, nam, knd) {		
		table_body.empty();
		chartData = [];
		//console.log(periods);
		$.each(periods, function( index, period ) {
			addPeriod(period,table_body,pKind, nam, knd);
		});
	};

	let init_tkind = function(tag, tkind, period) {	
		let def = tag.attr("def");
		if (tkind == 0) {
			var option_s = $("<option></option>");
	        option_s.attr("value",0);
	        option_s.text(pad(TENEFARM.sunraiseH,2)+":"+pad(TENEFARM.sunraiseM,2));
	        option_s.attr("selected","true");	
	        tag.append(option_s);
		} else if (tkind == 3) {
			var option_s = $("<option></option>");
	        option_s.attr("value",0);
	        option_s.text(pad(TENEFARM.sunsetH,2)+":"+pad(TENEFARM.sunsetM,2));
	        option_s.attr("selected","true");	
	        tag.append(option_s);
		} else if ( (tkind == 1) || (tkind == 2) ) {
			let kind_min = [{"val":30, "txt":"30분"}, {"val":60, "txt":"1시간"}];
		    $.each(kind_min, function( index, info ) {
		        var option_s = $("<option></option>");
		        option_s.attr("value",info.val);
		        option_s.text(info.txt);
		        //console.log();
		        if (def == "hs") {
		        	if (period.sval == info.val) {
			            option_s.attr("selected","true");
			        }
		        } else if (def == "he") {
		        	if (period.hm == info.val) {
			            option_s.attr("selected","true");
			        }
		        }
		        
		        tag.append(option_s);					        
		    });
		} else if ((tkind == 4) || (tkind == 5) ) {
			let kind_min = [{"val":30, "txt":"30분"}, {"val":60, "txt":"1시간"}];
		    $.each(kind_min, function( index, info ) {
		        var option_s = $("<option></option>");
		        option_s.attr("value",info.val);
		        option_s.text(info.txt);
		        if (def == "hs") {
		        	if (period.sval == info.val) {
			            option_s.attr("selected","true");
			        }
		        } else if (def == "he") {
		        	if (period.hm == info.val) {
			            option_s.attr("selected","true");
			        }
		        }
		        tag.append(option_s);					        
		    });
		} else {
			for (var i=0; i<24; i++) {
		        var each_time_s = (i<10 ? ("0" + i) : i);
		        var option_s = $("<option></option>");
		        option_s.attr("value",each_time_s);
		        option_s.text(each_time_s + " 시" );	
		        if (def == "hs") {
			        if (period.sval == i) {
			            option_s.attr("selected","true");
			        }	
			    } else if (def == "he") {
			        if (period.hm == i) {
			            option_s.attr("selected","true");
			        }	
			    }	        		         
		        tag.append(option_s);
		    }
		}


	};

	let init_tkind_common = function(tag, tkind, period) {	
		let def = tag.attr("def");
		if (tkind == 0) {
			var option_s = $("<option></option>");
	        option_s.attr("value",0);
	        option_s.text(pad(TENEFARM.sunraiseH,2)+":"+pad(TENEFARM.sunraiseM,2));
	        option_s.attr("selected","true");	
	        tag.append(option_s);
		} else if (tkind == 3) {
			var option_s = $("<option></option>");
	        option_s.attr("value",0);
	        option_s.text(pad(TENEFARM.sunsetH,2)+":"+pad(TENEFARM.sunsetM,2));
	        option_s.attr("selected","true");	
	        tag.append(option_s);
		} else if ( (tkind == 1) || (tkind == 2) ) {
			let kind_min = [{"val":30, "txt":"30분"}, {"val":60, "txt":"1시간"}];
		    $.each(kind_min, function( index, info ) {
		        var option_s = $("<option></option>");
		        option_s.attr("value",info.val);
		        option_s.text(info.txt);
		        //console.log();
		        if (def == "hs") {
		        	if (period.tinc == info.val) {
			            option_s.attr("selected","true");
			        }
		        } 
		        
		        tag.append(option_s);					        
		    });
		} else if ((tkind == 4) || (tkind == 5) ) {
			let kind_min = [{"val":30, "txt":"30분"}, {"val":60, "txt":"1시간"}];
		    $.each(kind_min, function( index, info ) {
		        var option_s = $("<option></option>");
		        option_s.attr("value",info.val);
		        option_s.text(info.txt);
		        if (def == "hs") {
		        	if (period.tinc == info.val) {
			            option_s.attr("selected","true");
			        }
		        } 
		        tag.append(option_s);					        
		    });
		} else {
			for (var i=0; i<24; i++) {
		        var each_time_s = (i<10 ? ("0" + i) : i);
		        var option_s = $("<option></option>");
		        option_s.attr("value",each_time_s);
		        option_s.text(each_time_s + " 시" );	
		        if (def == "hs") {
			        if (period.tinc == i) {
			            option_s.attr("selected","true");
			        }	
			    } 	        		         
		        tag.append(option_s);
		    }
		}


	};

	let change_tkind = function(tag,tag_id,period, tr ) {	
		//console.log(tag);
    	let selected_val = $(tag).find(':selected').val();
    	let selected_def = $(tag).attr("def");
    	//console.log(selected_val);
    	//console.log(selected_def);



    	let def_tag = $("#"+selected_def+"define_"+tag_id);
    	def_tag.find('option').remove().end();
    	if ( (selected_val == 0) || (selected_val == 3) ) {
    		def_tag.attr("disabled",true);
    		if (selected_val == 0) {
    			var option_s = $("<option></option>");
		        option_s.attr("value",0);
		        option_s.text(pad(TENEFARM.sunraiseH,2)+":"+pad(TENEFARM.sunraiseM,2));
		        option_s.attr("selected","true");	
		        def_tag.append(option_s);
    		} else if (selected_val == 3) {
    			var option_s = $("<option></option>");
		        option_s.attr("value",0);
		        option_s.text(pad(TENEFARM.sunsetH,2)+":"+pad(TENEFARM.sunsetM,2));
		        option_s.attr("selected","true");	
		        def_tag.append(option_s);
    		}
    	} else {
    		//def_tag.find('option').remove().end();
    		def_tag.attr("disabled",false);
    		if ( (selected_val == 1) || (selected_val == 2) || (selected_val == 4) || (selected_val == 5) ) {
    			
    			let kind_min = [{"val":30, "txt":"30분"}, {"val":60, "txt":"1시간"}];
			    $.each(kind_min, function( index, info ) {
			        var option_s = $("<option></option>");
			        option_s.attr("value",info.val);
			        option_s.text(info.txt);
			        def_tag.append(option_s);					        
			    });
    		} else {
    			for (var i=0; i<24; i++) {
			        var each_time_s = (i<10 ? ("0" + i) : i);
			        var option_s = $("<option></option>");
			        option_s.attr("value",each_time_s);
			        option_s.text(each_time_s + " 시" );			         
			        def_tag.append(option_s);
			    }
    		}
    	}
    	def_tag.selectmenu('refresh', true);

    	if (selected_def == "hs") {
    		if (period.tknd != selected_val) {
	  			tr.attr("changed","true");
	  			tr.attr("tknd",selected_val);
	  			if ( (selected_val == 0) || (selected_val == 3) ) {
	  				tr.attr("sval",0);
	  			} else if ( (selected_val == 1) || (selected_val == 2) || (selected_val == 4) || (selected_val == 5) ) {
	  				tr.attr("sval",30);
	  			} else {
	  				tr.attr("sval",0);
	  			}
	  		}
    	} else if (selected_def == "he") {
    		//console.log("HE");
    		if (period.tinc != selected_val) {
	  			tr.attr("changed","true");
	  			tr.attr("tinc",selected_val);
	  			if ( (selected_val == 0) || (selected_val == 3) ) {
	  				tr.attr("hm",0);
	  			} else if ( (selected_val == 1) || (selected_val == 2) || (selected_val == 4) || (selected_val == 5) ) {
	  				tr.attr("hm",30);
	  			} else {
	  				tr.attr("hm",0);
	  			}
	  		}
    	}
   
	};

	let change_tkind_common = function(tag,tag_id,period, tr ) {	
		//console.log(tag);
    	let selected_val = $(tag).find(':selected').val();
    	let selected_def = $(tag).attr("def");
    	//console.log(selected_val);
    	//console.log(selected_def);



    	let def_tag = $("#"+selected_def+"define_"+tag_id);
    	def_tag.find('option').remove().end();
    	if ( (selected_val == 0) || (selected_val == 3) ) {
    		def_tag.attr("disabled",true);
    		if (selected_val == 0) {
    			var option_s = $("<option></option>");
		        option_s.attr("value",0);
		        option_s.text(pad(TENEFARM.sunraiseH,2)+":"+pad(TENEFARM.sunraiseM,2));
		        option_s.attr("selected","true");	
		        def_tag.append(option_s);
    		} else if (selected_val == 3) {
    			var option_s = $("<option></option>");
		        option_s.attr("value",0);
		        option_s.text(pad(TENEFARM.sunsetH,2)+":"+pad(TENEFARM.sunsetM,2));
		        option_s.attr("selected","true");	
		        def_tag.append(option_s);
    		}
    	} else {
    		//def_tag.find('option').remove().end();
    		def_tag.attr("disabled",false);
    		if ( (selected_val == 1) || (selected_val == 2) || (selected_val == 4) || (selected_val == 5) ) {
    			
    			let kind_min = [{"val":30, "txt":"30분"}, {"val":60, "txt":"1시간"}];
			    $.each(kind_min, function( index, info ) {
			        var option_s = $("<option></option>");
			        option_s.attr("value",info.val);
			        option_s.text(info.txt);
			        def_tag.append(option_s);					        
			    });
    		} else {
    			for (var i=0; i<24; i++) {
			        var each_time_s = (i<10 ? ("0" + i) : i);
			        var option_s = $("<option></option>");
			        option_s.attr("value",each_time_s);
			        option_s.text(each_time_s + " 시" );			         
			        def_tag.append(option_s);
			    }
    		}
    	}
    	//tag.selectmenu('refresh', true);
    	def_tag.selectmenu('refresh', true);

    	//console.log(selected_val);

    	period.tknd = selected_val;
		//if (period.tknd != selected_val) {
  			tr.attr("changed","true");
  			tr.attr("tknd",selected_val);
  			if ( (selected_val == 0) || (selected_val == 3) ) {
  				if (selected_val == 0) {
  					tr.attr("hm",(TENEFARM.sunraiseH*60) + TENEFARM.sunraiseM);  					
  				} else if (selected_val == 3) {
  					tr.attr("hm",(TENEFARM.sunsetH*60) + TENEFARM.sunsetM);  		
  				}
  			} else if ( (selected_val == 1) || (selected_val == 2) || (selected_val == 4) || (selected_val == 5) ) {
  				if  (selected_val == 1 ) {
  					tr.attr("hm",(TENEFARM.sunraiseH*60) + TENEFARM.sunraiseH - 30);  
	  			}  else if (selected_val == 2) {
	  				tr.attr("hm",(TENEFARM.sunraiseH*60) + TENEFARM.sunraiseH + 30);  
	  			} else if  (selected_val == 4 ) {
  					tr.attr("hm",(TENEFARM.sunsetH*60) + TENEFARM.sunsetM - 30);  
	  			}  else if (selected_val == 5) {
	  				tr.attr("hm",(TENEFARM.sunsetH*60) + TENEFARM.sunsetM + 30);  
	  			}					
  			} else {
  				tr.attr("hm",0);
  			}
  		//}
    	
   
	};

	let change_svalue = function(tag,tag_id,period, tr ) {	
		//console.log(tag);
    	let selected_val = $(tag).find(':selected').val();
    	let selected_def = $(tag).attr("def");
    	//console.log(selected_val);
    	//console.log(selected_def);
    	//console.log(period);


    	if (selected_def == "hs") {
    		if (period.sval != selected_val) {
	  			tr.attr("changed","true");
	  			tr.attr("sval",selected_val);
	  		}
    	} else if (selected_def == "he") {
    		if (period.hm != selected_val) {
	  			tr.attr("changed","true");
	  			tr.attr("hm",selected_val);
	  		}
    	}
   
	};

	let change_tinc = function(tag,period,tr,selected_val_tkind ) {	
		//console.log(selected_val_tkind);
    	let selected_val = $(tag).find(':selected').val();
    	//let selected_def = $(tag).attr("def");
    	//console.log(selected_val);
    	//console.log(selected_def);
    	//console.log(period);

    	let myhm = Number(selected_val);
    	

    	if ( (selected_val_tkind == 1) || (selected_val_tkind == 2) || 
					(selected_val_tkind == 4) || (selected_val_tkind == 5) ) {
			if  (selected_val_tkind == 1 ) {
				tr.attr("hm",(TENEFARM.sunraiseH*60) + TENEFARM.sunraiseH - myhm);  
			}  else if (selected_val_tkind == 2) {
				tr.attr("hm",(TENEFARM.sunraiseH*60) + TENEFARM.sunraiseH + myhm);  
			} else if  (selected_val_tkind == 4 ) {
				tr.attr("hm",(TENEFARM.sunsetH*60) + TENEFARM.sunsetM - myhm);  
			}  else if (selected_val_tkind == 5) {
				tr.attr("hm",(TENEFARM.sunsetH*60) + TENEFARM.sunsetM + myhm);  
			}					
		} else if (selected_val_tkind == 6) {
			tr.attr("hm",myhm*60);
		}
		tr.attr("changed","true");
		tr.attr("tinc",selected_val);


    	
		//if (period.sval != selected_val) {
  		//	tr.attr("changed","true");
  		//	tr.attr("tinc",selected_val);
  		//}
    	
   
	};

	let addPeriod = function( period, table_body,pKind, nam,knd) {	
		//console.log(knd);
		//console.log(period);

		if (knd == "08") {
			let tr = $('<tr>');
		    tr.addClass("line autoperiod");
		    tr.attr("mkey",period.mkey);
		    tr.attr("changed",false);
		    tr.attr("tknd",period.tknd);
		    tr.attr("tinc",period.tinc);
		    tr.attr("hm",period.hm);
	        tr.attr("sval",period.sval);
	    	

		    table_body.append(tr);

			
			let mkey = period.mkey;
		    var tag_id = ""+con_cde+house_cde+agid+mkey;
		      
		//sunset sunraise
		    var td_sunset = $('<td>');
		    td_sunset.addClass("sunknd");

		    
		    var sunknd;
		    
	    	sunknd = $("<span>");
		    sunknd.html(mkey + "단계");
		    //sunknd.html(nam);

		    var td_hs = $('<td>');
		    td_hs.addClass("time");
		    var tag_hs =$('<select  data-native-menu="false" data-mini="true" class="control_select" data-iconpos="none">');
		    tag_hs.attr("id","hs_"+tag_id);
		    tag_hs.attr("def","hs");
		    tag_hs.addClass("tkind");
		    td_hs.append(tag_hs);
		    tr.append(td_hs);

		    var td_hs_define = $('<td>');
		    td_hs_define.addClass("time svalue" );
		    var tag_hs_define =$('<select  data-native-menu="false" data-mini="true" class="control_select" data-iconpos="none">');
		    tag_hs_define.attr("id","hsdefine_"+tag_id);
		    tag_hs_define.attr("def","hs");
		    tag_hs_define.addClass("svalue");
		    td_hs_define.append(tag_hs_define);
		    tr.append(td_hs_define);

		    var td_he = $('<td>');
		    td_he.addClass("time");
		    var tag_he =$('<select  data-native-menu="false" data-mini="true" class="control_select" data-iconpos="none">');
		    tag_he.attr("id","he_"+tag_id);
		    tag_he.attr("def","he");
		    tag_he.addClass("tkind");
		    td_he.append(tag_he);
		    tr.append(td_he);

		    var td_he_define = $('<td>');
		    td_he_define.addClass("time");
		    var tag_he_define =$('<select  data-native-menu="false" data-mini="true" class="control_select" data-iconpos="none">');
		    tag_he_define.attr("id","hedefine_"+tag_id);
		    tag_he_define.attr("def","he");
		    tag_he_define.addClass("svalue");
		    td_he_define.append(tag_he_define);
		    tr.append(td_he_define);

		    let tkinds = ["일출","일출전","일출후","일물","일몰전","일몰후","정의"];
		    $.each(tkinds, function( index, info ) {
		    	//console.log(index);
		    	//var each_time_s = (i<10 ? ("0" + i) : i);
		        var option_s = $("<option></option>");
		        option_s.attr("value",index);
		        option_s.text(info);
		        if (period.tknd == index) {
		            option_s.attr("selected","true");
		        }
		        tag_hs.append(option_s);

		        var option_e = $("<option></option>");
		        option_e.attr("value",index );
		        option_e.text(info);
		        if (period.tinc == index) {
		            option_e.attr("selected","true");
		        }
		        tag_he.append(option_e);
		    });

		    //change_tkind(tag_hs,tag_id);
		    //change_tkind(tag_he,tag_id);

		    init_tkind(tag_hs_define,period.tknd,period);
		    init_tkind(tag_he_define,period.tinc,period);

		    $(".tkind").bind("change",function(e) {
		    	change_tkind(this,tag_id,period,tr);		    	
		    });

		    $(".svalue").bind("change",function(e) {
		    	change_svalue(this,tag_id,period,tr);		    	
		    });




		} else {
			let tr = $('<tr>');
		    tr.addClass("line autoperiod");
		    tr.attr("mkey",period.mkey);
		    tr.attr("changed",false);
		    tr.attr("tknd",period.tknd);
		    tr.attr("tinc",period.tinc);
		    tr.attr("hm",period.hm);
	        tr.attr("sval",period.sval);
	    	

		    table_body.append(tr);

			
			let mkey = period.mkey;
		    var tag_id = ""+con_cde+house_cde+agid+mkey;
		      
		

		    //시간
		    var td_hs = $('<td>');
		    td_hs.addClass("time");
		    var tag_hs =$('<select  data-native-menu="false" data-mini="true" class="control_select" data-iconpos="none">');
		    tag_hs.attr("id","hs_"+tag_id);
		    tag_hs.attr("def","hs");
		    tag_hs.addClass("tkind");
		    td_hs.append(tag_hs);
		    tr.append(td_hs);

		    var td_hs_define = $('<td>');
		    //td_hs_define.addClass("time svalue" );
		    var tag_hs_define =$('<select  data-native-menu="false" data-mini="true" class="control_select" data-iconpos="none">');
		    tag_hs_define.attr("id","hsdefine_"+tag_id);
		    tag_hs_define.attr("def","hs");
		   // tag_hs_define.addClass("svalue");
		    td_hs_define.append(tag_hs_define);
		    tr.append(td_hs_define);



		    let tkinds = ["일출","일출전","일출후","일물","일몰전","일몰후","정의"];
		    $.each(tkinds, function( index, info ) {
		    	//console.log(index);
		    	//var each_time_s = (i<10 ? ("0" + i) : i);
		        var option_s = $("<option></option>");
		        option_s.attr("value",index);
		        option_s.text(info);
		        if (period.tknd == index) {
		            option_s.attr("selected","true");
		        }
		        tag_hs.append(option_s);
		    });

		    
		    

		    init_tkind_common(tag_hs_define,period.tknd,period);

		    tag_hs.bind("change",function(e) {
		    	change_tkind_common(this,tag_id,period,tr);		    	
		    });

		    tag_hs_define.bind("change",function(e) {
		    	let selected_val_tkind = tag_hs.find(':selected').val();
		    	change_tinc(this,period,tr,selected_val_tkind);		    	
		    });

			//sensor value
		    var td_sensor = $('<td>');
		    td_sensor.addClass("sensor");
		    var tag_sensor =$('<input type="text" data-role="spinbox" data-mini="true"  min="0" max="100" />');
		    tag_sensor.val(period.sval);   

		    tag_sensor.css("font-family","digitalFont");
		    tag_sensor.css("font-size","16px");
		    tag_sensor.css("width","50px");
		    tag_sensor.attr("id","sensor"+tag_id);

		    let div_temp_sensor = $("<div>");    
		    td_sensor.append(div_temp_sensor);
		    div_temp_sensor.append(tag_sensor);
		    tr.append(td_sensor);   

		    
		     

	      	tag_sensor.spinbox({type:"horizontal"});
	      	
			

		   	tag_sensor.bind("change",function(e) {
		    	//console.log(e);
		    	let val = $(this).val();
	      		if (period.sval != val) {
	      			tr.attr("changed","true");
	      			tr.attr("sval",val);
	      		} else {
	      			tr.attr("changed","false");
	      		}
		    });
		}

		

	    return period;
	};

	this.dispalyCurSensorValue = function(periods) {
		let txt_begin;
		let txt_end;

		//console.log(periods);

		$.each(periods, function( index, period ) {
			let tkind = period.tknd;
			let tinc = period.tinc;
			let sval = period.sval;
			let hm = period.hm;

			

			if (tkind == 0) {
				txt_begin = "일출("+pad(TENEFARM.sunraiseH,2)+":"+pad(TENEFARM.sunraiseM,2)+")";
			} else if (tkind == 3) {
				txt_begin = "일몰("+pad(TENEFARM.sunsetH,2)+":"+pad(TENEFARM.sunsetM,2)+")";;
			} else if (tkind == 1) {
				txt_begin = "일출전 " + (sval==30 ? (sval + "분") : "1시간");				
			} else if (tkind == 2) {
				txt_begin = "일출후 " + (sval==30 ? (sval + "분") : "1시간");						
			} else if (tkind == 4) {
				txt_begin = "일몰전 " + (sval==30 ? (sval + "분") : "1시간");					
			} else if (tkind == 5) {
				txt_begin = "일몰후 " + (sval==30 ? (sval + "분") : "1시간");					
			} else if (tkind == 6) {
				txt_begin = (sval<10 ? ("0" + sval) : sval);
			}

			if (tinc == 0) {
				txt_end = "일출("+pad(TENEFARM.sunraiseH,2)+":"+pad(TENEFARM.sunraiseM,2)+")";
			} else if (tinc == 3) {
				txt_end = "일몰("+pad(TENEFARM.sunsetH,2)+":"+pad(TENEFARM.sunsetM,2)+")";;
			} else if (tinc == 1) {
				txt_end = "일출전 " + (hm==30 ? (hm + "분") : "1시간");				
			} else if (tinc == 2) {
				txt_end = "일출후 " + (hm==30 ? (hm + "분") : "1시간");						
			} else if (tinc == 4) {
				txt_end = "일몰전 " + (hm==30 ? (hm + "분") : "1시간");					
			} else if (tinc == 5) {
				txt_end = "일몰후 " + (hm==30 ? (hm + "분") : "1시간");					
			} else if (tinc == 6) {
				txt_end = (hm<10 ? ("0" + hm) : hm);
			}
		});

		return txt_begin+"~"+txt_end;
	};

	this.setSensorValueTag = function(curSensorValue) {
		self.curSensorValue = curSensorValue;
	};

	this.init = function(priority_container, nam, knd,curSensorValue) {

		self.curSensorValue = curSensorValue;
		//console.log(self.curSensorValue.html());
		//===============================================================
		let pKind = 1;
		//==============================================================

	  	let listview_auto = $('<div class="priority">');
		priority_container.append(listview_auto);

      	let table = $('<table cellpadding="0" cellspacing="0" border="0">');
      	table.addClass("table");
	    let table_header = $("<thead>");
	    table_header.addClass("header");	  		    

	    let table_footer = $("<tfoot>");
	    table_footer.addClass("footer");	
	    let table_footer_tr = $("<tr>");
	    


	    table_header_tr = $("<tr>");
	    table_header_tr.addClass("header-title");

	  
    	let table_header_tr_th_sun = $("<th>");
	    table_header_tr_th_sun.addClass("tknd");
	    //table_header_tr_th_sun.html("기준");
	    table_header_tr_th_sun.html("시간범위");
	    let table_header_tr_th_sensor = $("<th>");
	    table_header_tr_th_sensor.addClass("sval");
	    if (knd=="08") {
	    	table_header_tr_th_sun.html("점등시간");
	    	table_header_tr_th_sun.attr("colSpan",2);
	    	table_header_tr_th_sensor.html("소등시간");
	    	table_header_tr_th_sensor.attr("colSpan",2);
	    	//table_header_tr_th_sensor.html("운전"+args.sm );		
	    } else {
	    	table_header_tr_th_sun.attr("colSpan",2);
	    	table_header_tr_th_sensor.html("목표"+args.sm + "("+ args.unit + ")");	
	    }
	    	    
	    table_header_tr.append(table_header_tr_th_sun); 
	    table_header_tr.append(table_header_tr_th_sensor);	

	    

	    

	    let table_footer_td = $("<td colspan='5'>");
	    table_footer_tr.append(table_footer_td);
	    table_footer.append(table_footer_tr);

	    var button_save = $('<button class="ui-btn ui-icon-check ui-btn-icon-left ui-mini" data-role="button" inline="true">저장</button>');

	    let div_btns = $("<div class='div_btns'>");
	    let div_btn_save = $("<div class='div_btn'>");

	    div_btns.append(div_btn_save);
	    div_btn_save.append(button_save);
	    table_footer_td.append(div_btns);

	    button_save.click(function() { 
	      jConfirm("저장 하시겠습니까?","확인", function(e) {
	        if (e) {
	        	let changedPeriods = [];   
	        	
	        		 
			    	let trList = $(".autoperiod");
			    	$.each(trList, function( index, tr ) {
			    		let changed = $(tr).attr("changed");
			    		//console.log(changed);
			    		if (changed == "true") {
			    			let mkey = $(tr).attr("mkey");
			    			let tknd = $(tr).attr("tknd");
			    			let tinc = $(tr).attr("tinc");
			    			let hm = $(tr).attr("hm");
			    			let sval = $(tr).attr("sval");
			    			$.each(periods, function( index, ccperiod ) {
			    				if (ccperiod.mkey == mkey) {
			    					//console.log("ccperiod", ccperiod);
			    					ccperiod.tknd = tknd;
			    					ccperiod.tinc = tinc;
			    					ccperiod.hm = hm;
			    					ccperiod.sval = sval;
			    					changedPeriods.push(ccperiod);

			    					//tenechart.updateData(index, hm,sval);
			    				}
			    			});
			    			$(tr).attr("changed", false);
			    		}
			    	});

			    	
	        	//}

	        	//console.log(changedPeriods);

	        	if (changedPeriods.length > 0) {
		    		let fargs = {
			          	socketId:socketId,
		            	house_cde:house_cde,
				      	agid:agid,
				      	data:changedPeriods
			        };
			        //console.log(fargs);
			        TENEFARM.func_updatePeriod(fargs);

			        if (ag_knd == "08")
			        	self.curSensorValue.html(self.dispalyCurSensorValue(periods));
		    	}
	          	
		    	return;   

	        }
	      });
	    });


	    
	    table.append(table_header);
	    table_header.append(table_header_tr);	    

	    table.append(table_footer);	
	    
	    table_body.addClass("content");
      	table.append(table_body);
      	listview_auto.append(table);


      	let div_coection = $('<div>');
	  	div_coection.addClass("chartbody");

	  	let canvas = '<canvas id="chartmsp" >';
	  	div_coection.append(canvas);

	  	listview_auto.append(div_coection);
	  	

      	fill_range(periods,table_body, pKind, nam, knd);

      	//let msp = new msp_base();
      	//msp.draw();
	};

};