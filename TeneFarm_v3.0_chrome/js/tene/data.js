var historydata_base = function(args) {

	this.usr_id_re;
	this.farm_cde_re;

	let container = $("#historycontainer");
	let div_chart = $("<div class='chart'>");
	let canvas = $("<canvas>");
	canvas.css("width","96%" );
	div_chart.append(canvas);
	container.append(div_chart);

	let chart;
	let org_fields_data = {};
	let fields_data = {};

	let timeFormat = 'yyyy-mm-dd hh';
	let config =  {
		type: 'line',
		data: {
	    	labels: [],
	    	datasets: []	        
	            
		},
		options: {
			title: {
				display: false,
				text: '1시간 평균'
			},
			tooltips: {
					mode: 'index',
					intersect: false,
					callbacks: {
		                label: function(tooltipItem, data) {
		                    //var label = data.datasets[tooltipItem.datasetIndex].label || '';
		                    let orgNam = data.datasets[tooltipItem.datasetIndex].label;
		                    let nam = orgNam.substring(0,6);
		                    
		                   // console.log(nam);
		                    var label = fields_data[nam].info.nam || '';

		                    if (orgNam.indexOf("min")>0)
		                    	label += "(최소)"
		                  	else if (orgNam.indexOf("max")>0)
		                    	label += "(최대)"

		                    if (label) {
		                        label += ': ';
		                    }
		                    label += Math.round(tooltipItem.yLabel * 100) / 100;
		                   // console.log(label);
		                    return label;
		                }
		            }
			},
			hover: {
				mode: 'nearest',
				intersect: true
			},
			legend: {
			            display: false,
			            position:"bottom",
			            labels: {
			                fontColor: '#000',
			                boxWidth:24
			            }
			        },
			       
			scales: {
				 /*
				xAxes: [{
					type: 'time',
					time: {
						parser: timeFormat,
						unit:'hour',
						minUnit:"hour",
						stepSize:2
					}
				}],
*/
				yAxes: [
				

				]
			}
			
		}
	}; // config



	chart = new Chart(canvas[0].getContext('2d'),config);
	

	//this.setLabels = function(labels) {
		//console.log(labels);
	//	self.chart.data.labels = labels;
	//};

	this.viewMinMax = function(knd, isView) {
		//console.log(knd, isView);
		$.each(fields_data,function(i,field) {
			field.info["view_"+knd] = isView;
		});


	//	fields_data["f_"+knd].info.view = isView;
		$.each(chart.data.datasets, function( index, dataset ) {	
			//console.log(dataset.label);
			if (dataset.label.indexOf(knd) > 0) {
				//let lineLabel = dataset.label.substring(0,4);
				let lineLabel = dataset.label.substring(0,6);
				//console.log(lineLabel);
				if (fields_data[lineLabel].info.view) {
					dataset.hidden = !isView;	
				}
				
			}
		});
		chart.update();	
	};

	this.refreshCanvas = function() {
		let w_w = $( window ).width(); //.replace(/[^0-9]/g, ""); 
		let h_w = $( window ).height(); //.replace(/[^0-9]/g, ""); 

		let h_header = $("#header").outerHeight();
		let h_where = $("#where").outerHeight();
		let h_itemselecter = $("#con_historyitemselecter").outerHeight();
		let h_itemminmax = $("#con_historyitemminmax").outerHeight();
		let sumHeight = h_w - h_header - h_where - h_itemselecter - h_itemminmax;

		//console.log(w_w,h_w );

		//container.css("width",w_w - 5 );
		//container.css("height",sumHeight );

		container.empty();

		if (sumHeight < 300) {
			sumHeight = 300;
		}

		let div_chart = $("<div class='chart'>");
		//div_chart.css("padding-top", "10px");
		div_chart.css("width",w_w - 5 );
		div_chart.css("height",sumHeight );

		
		//let w_canvas = div_chart.css("width").replace(/[^0-9]/g, ""); 
		//let h_canvas = div_chart.css("height").replace(/[^0-9]/g, ""); 
		

		let canvas = $("<canvas>");
		canvas.css("width","96%" );
		canvas.css("height",sumHeight + "px");
		div_chart.append(canvas);

		container.append(div_chart);
		

		if (self.usr_id_re) {
			//console.log(self.usr_id_re);
			chart = new Chart(canvas[0].getContext('2d'),config);
			chart.update();	
			//self.getData(self.usr_id_re, self.farm_cde_re);
		}

		
	};

	this.resizeContainer = function() {
		$("#historyitemselecter").trigger('create');
		//let container = $("#historycontainer");

		let w_w = $( window ).width(); //.replace(/[^0-9]/g, ""); 
		let h_w = $( window ).height(); //.replace(/[^0-9]/g, ""); 

		//console.log(w_w,h_w );
		
		let h_header = $("#header").outerHeight();
		let h_where = $("#where").outerHeight();
		let h_itemselecter = $("#con_historyitemselecter").outerHeight();
		//console.log(h_itemselecter );

		//let h_itemselecter = document.getElementById("con_historyitemselecter").clientHeight;

		//console.log(document.getElementById("historyitemselecter"));
		let h_itemminmax = $("#con_historyitemminmax").outerHeight();
		//console.log(h_w );
		//console.log(h_header,h_where );
		//console.log(h_itemselecter,h_itemminmax );
		
		let sumHeight = h_w - h_header - h_where - h_itemselecter - h_itemminmax;
		//console.log(sumHeight );
		
		if (sumHeight < 300) {
			sumHeight = 300;
		}

		//container.css("width",w_w - 5 );
		//container.css("height",sumHeight );

		div_chart.css("width",w_w - 5 );
		div_chart.css("height",sumHeight );

		//let canvas = $("<canvas width='100%' height='60'>");
		let w_canvas = div_chart.css("width").replace(/[^0-9]/g, ""); 
		let h_canvas = div_chart.css("height").replace(/[^0-9]/g, ""); 
		

		//let canvas = $("<canvas>");
		canvas.css("width","96%" );
		canvas.css("height",sumHeight + "px");
	};

	this.addField_loaded = function(house_cde) {
		let historyitemselecter = $("#historyitemselecter");
	  	historyitemselecter.empty();	
	  	//console.log(org_fields_data);

		$.each(org_fields_data, function( index, data ) {
        	//console.log(index, data);
        	if ($("#check"+(data.nid-1)+data.sk).length == 0) {
				
				if (house_cde == data.house_cde) {
					var check_div = $('<div class="ui-controlgroup-controls" style="width:50px; margin-right:2px;">');
					let check = $('<input type="checkbox" class="custom" />');

					check.attr("data-knd",data.sk);
					check.attr("data-nid",data.nid);
					let label;
					check.attr("id","check"+(data.nid-1)+data.sk);
					check.attr("name","check_chartknd");
					if (data.sk.match("03|04|05|06|54|55|06")) {
						label = $('<label for="check' +(data.nid-1)+data.sk + '">' + data.sm + '</label>');				
								
					} else {
						label = $('<label for="check' +(data.nid-1)+data.sk + '">' + data.sm+(data.nid-1)+"열" + '</label>');				
					}

					label.css("color","#" + data.dc);
					check_div.append(check);
					check_div.append(label);
					historyitemselecter.append(check_div);	
				}
				
			}
        });

        

        chart.data.datasets = [];
    	chart.options.scales.yAxes = [];
    	$.each(fields_data,function(i,field) {
			field.avg = [];
			field.min = [];
			field.max = [];
		});

		$.each(fields_data, function( index, data ) {
        	(data.sk == "03") ? (data.info.view = true) : (data.info.view = false);        		
        });

        self.resizeContainer();

       chart.update();	
		
	};

	this.addField = function(info) {	
		let historyitemselecter = $("#historyitemselecter");	


		if ($("#check"+(info.nid-1)+info.sk).length == 0) {
			//historydata.addField(info);
			var house_cde = $("#houselist_table").val();
			//console.log(house_cde);
			if (house_cde == info.house_cde) {
				//console.log(info);
				var check_div = $('<div class="ui-controlgroup-controls" style="width:50px; margin-right:2px;">');
				//check_div.css("border","1px solid #" + info.dc);
				let check = $('<input type="checkbox" class="custom" />');

				check.attr("data-knd",info.sk);
				check.attr("data-nid",info.nid);
				let label;
				check.attr("id","check"+(info.nid-1)+info.sk);
				check.attr("name","check_chartknd");
				if (info.sk.match("03|04|05|06|54|55|06")) {
					label = $('<label for="check' +(info.nid-1)+info.sk + '">' + info.sm + '</label>');				
								
				} else {
					label = $('<label for="check' +(info.nid-1)+info.sk + '">' + info.sm+(info.nid-1)+"열" + '</label>');				
				}

				label.css("color","#" + info.dc);
				check_div.append(check);
				check_div.append(label);
				historyitemselecter.append(check_div);	
			}
			
		}	

		org_fields_data[info.house_cde+""+info.nid+""+info.sk] = info;
		fields_data["f_"+(info.nid)+"_"+info.sk] = {
			info : {
				nam: info.sk.match("11|12") ? info.sm+(info.nid-1)+"열" :  info.sm,
				color: "#"+info.dc,
				ymin:info.cl,
				ymax:info.ch,
				view:info.sk == "03" ? true : false,
				view_min:false,
				view_max:false
			},
			avg : [],
			min : [],
			max : [],
			house_cde : info.house_cde,
			nid : info.nid,
			sk : info.sk,
			dc : info.dc
		};		

		self.resizeContainer();
	};

	this.changeFieldView = function(nid, knd, isView) {
		fields_data["f_"+(nid)+"_"+knd].info.view = isView;		
		
		$.each(chart.data.datasets,function(index,dataset) {
			//if (dataset.label.indexOf(knd) > 0) {
			if (dataset.label.indexOf(nid+"_"+knd) > 0) {
				if (isView) {
					if (dataset.label.indexOf("min")>0) {
	    				dataset.hidden = !fields_data["f_"+(nid)+"_"+knd].info.view_min;
	    			} else if (dataset.label.indexOf("max")>0) {
	    				dataset.hidden = !fields_data["f_"+(nid)+"_"+knd].info.view_max;
	    			} else {
	    				dataset.hidden = !fields_data["f_"+(nid)+"_"+knd].info.view;
	    			}	
	    			//
				} else {
					dataset.hidden = !fields_data["f_"+(nid)+"_"+knd].info.view;

				}
			}
		});

		//chart.options.scales.yAxes["f_"+nid+"_"+knd].display = isView;
		

		if (knd.match("11|12")) {
			isOtherOn(nid,knd,isView)
			//if (isView) {
				//if (!(isOtherOn(nid,knd))) {
				//	axis.display = true;
				//}
			//} 
			
		} else {
			$.each(chart.options.scales.yAxes,function(index,axis) {
				if (axis.id == "f_"+nid+"_"+knd) {				
					axis.display = isView;
				}
			});
		}
		
		
		
		chart.update();	
	};

	let isOtherOn = function(nid,knd, isView) {
		var other = 0;

		$.each(fields_data,function(i,field) {
			if (i.indexOf(knd)>0) {
				other |= field.info.view;
			}
		});
		
		$.each(chart.options.scales.yAxes,function(index,axis) {
			if (axis.id.indexOf(knd)>0) {
				if (axis.id == "f_"+nid+"_"+knd) {	
					
					axis.display = other;
				} else {					
					axis.display = false;	
				}	
			}	
		});
	};



	this.getData = function(usr_id, farm_cde) {
		self.usr_id_re = usr_id;
		self.farm_cde_re = farm_cde;
		//alert("sss");
		//console.log(usr_id, farm_cde);
		var knd = "table";
  	    //var farm_cde = $("#farmlist_" + knd).val();
  		var house_cde = $("#houselist_" + knd).val();
  		var startdat =  $("#startdat_" + knd).val();     
  	    var enddat =  $("#enddat_" + knd).val();

  	    //let hidden_avg = !$("#historyavg").prop("checked");
  	    //let hidden_min = !$("#historymin").prop("checked");
  	    //let hidden_max = !$("#historymax").prop("checked");

  	    sql_usr_id = "measuredata_v2_" + $("#mea_term_" + knd).val();
  	       
  	    var params = {
  	  		id:sql_usr_id,
  	  		farm_cde:farm_cde,
  	  		house_cde:house_cde,
  	  		usr_id:usr_id,
  	  		from_date:startdat,
  	  		to_date:enddat,
  	  		formURL:"datalist"
  	    };

  	    //console.log(params);
  	  
	    blight.read_get(params, function(result) {
	    	//console.log(result);
	    	chart.data.datasets = [];
	    	chart.options.scales.yAxes = [];
	    	$.each(fields_data,function(i,field) {
    			field.avg = [];
    			field.min = [];
    			field.max = [];
    		});

    		//console.log(fields_data);
	    	
	    	if (result.status == "true") {
	    		let labels = [];
	    		$.each(result.data,function(index,data) {
	    			labels.push(data.mea_dat);
	    			$.each(data,function(i,value) {
	    				if (value != null) {
		    				if ( (i != "house_cde") && (i != "mea_dat") ) {
			    				let fieldName = i.substring(0,6);
			    				//console.log(i,value);
			    				//console.log(fieldName);
			    				if (fields_data[fieldName]) {
			    					if (i.indexOf("min")>0) {
				    					fields_data[fieldName].min.push(Number(data[i]));
					    			} else if (i.indexOf("max")>0) {
					    				fields_data[fieldName].max.push(Number(data[i]));
					    			} else {
					    				fields_data[fieldName].avg.push(Number(data[i]));
					    			}
			    				}
			    					
					    		
			    				
				    		}
				    	}
	    			});
	    		});

	    		
	    		$.each(fields_data,function(i,field) {
	    			//let position = "right";
	    			//if (i.indexOf("03")>0) 
	    			//if (i == "f_03")
	    				position = "left";

	    			let yAxis = {
						//display: field.info.view,
						display:false,
						position: position,
						color:field.info.color,
						//backdropColor:field.info.color,
						//textStrokeColor:field.info.color,


						scaleLabel: {
							display: false,
							//padding:-16,
							padding:-30,
							lineHeight:100,
							fontColor:field.info.color,
							labelString: field.info.nam
						},
						ticks: {
				            max: field.info.ymax,
				        	min: field.info.ymin,
				        	fontColor:field.info.color
				        	//stepSize: 5
						},id: i
					}
					chart.options.scales.yAxes.push(yAxis);
	    		});
	    		chart.data.labels = labels;
	    		
	    		//console.log(fields_data);

	    		$.each(fields_data,function(i,fData) {
	    			let item_avg = {
						type: "line",
						label: i+"avg",
						backgroundColor: fData.info.color,
						borderColor: fData.info.color,
						borderWidth:2,
						data: fData.avg,
						fill: fData.info.nam == "누적일사" ? true : false,
						pointRadius: 0,
						hidden:!fData.info.view,
						yAxisID: i
					};
					let item_min = {
						type: "scatter",
						label: i+"min",
						data: fData.min,
						fill: false,
						showLine:false,
						pointRadius: 5,
						pointStyle:"triangle",
						rotation:180,
						pointBorderColor:"#888844",
						pointBackgroundColor:"#888844",
						hidden:!fData.info.view_min,
						yAxisID: i
					};
					let item_max = {
						type: "scatter",
						label: i+"max",
						data: fData.max,
						fill: false,
						showLine:false,
						pointRadius: 5,
						pointStyle:"triangle",
						rotation:0,
						pointBorderColor:"#DF013A",
						pointBackgroundColor:"#DF013A",
						hidden:!fData.info.view_max,
						yAxisID: i
					};
					chart.data.datasets.push(item_avg);
					chart.data.datasets.push(item_min);
					chart.data.datasets.push(item_max);

					$.each(chart.options.scales.yAxes,function(index,axis) {
						if (axis.id == "f_"+(fData.nid)+"_"+ fData.sk) {
							//if (fData.info.view)
							axis.display = fData.info.view;
							//console.log(axis);
						}
					});
					
	    		});


	    		chart.update();	
	    		//return;

	    		

		    	
	    	} else {
	    		jAlert("측정정보가 없습니다.", "측정정보 안내");
	    	}
	    });
	     
	};

	let self = this;
};