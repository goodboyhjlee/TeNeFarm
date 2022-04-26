
window.chartColors = {
	red: 'rgb(255, 0, 0)',
	orange: 'rgb(255, 255, 255)',
	yellow: 'rgb(255, 205, 86)',
	green: 'rgb(75, 192, 192)',
	blue: 'rgb(54, 162, 235)',
	purple: 'rgb(153, 102, 255)',
	grey: 'rgb(201, 203, 207)'
};

var statusdata_base = function(args) {
	let container = $("#chart_container");

	let div_chart = $("<div class='chart'>");
	//div_chart.css("height","400px");
	div_chart.css("padding-top", "10px");
	//let canvas = $("<canvas width='100%' height='60'>");
	let canvas = $("<canvas width='100%' >");
	div_chart.append(canvas);

	container.append(div_chart);

	var randomScalingFactor = function() {
			return Math.round(Math.random() * 100);
		};

	

	let chart;
	let fields_data = {
		step: {
			nam:"기구분",
			color: "#123",
			ymin:0,
			ymax:10,
			datasets:{
				nth01:{data:[],color:"red"},
				nth02:{data:[],color:"blue"},
				nth03:{data:[],color:"green"},
				nth04:{data:[],color:"yellow"},
			}
		},
		mea_avg: {
			nam:"온도(측정)",
			color: "#123",
			ymin:0,
			ymax:10,
			data:[]
		},
		mea_min: {
			nam:"온도(최소)",
			color: "#123",
			ymin:0,
			ymax:10,
			data:[]
		},
		mea_max: {
			nam:"온도(최대)",
			color: "#123",
			ymin:0,
			ymax:10,
			data:[]
		},		
		mea_stack: {
			nam:"적산온도(측정)",
			color: "#123",
			ymin:0,
			ymax:10,
			data:[]
		},		
		std_stack: {
			nam:"적산온도(목표)",
			color: "#123",
			ymin:0,
			ymax:10,
			data:[]
		}
	};

	//console.log(fields_data);
	/*
	fields_data["f_"+info.sk] = {
		info : {
			nam:info.sm,
			color: "#"+info.dc,
			ymin:info.cl,
			ymax:info.ch,
			view:info.sk == "03" ? true : false,
			view_min:false,
			view_max:false
		},
		avg : [],
		min : [],
		max : []
	};
	*/


	let colorize = function(opaque, ctx) {
	//	let colorize = function(ctx) {
		/*
		console.log(ctx);
		var v = ctx.dataset.data[ctx.dataIndex];
		var c = v < -50 ? '#D60000'
			: v < 0 ? '#F46300'
			: v < 50 ? '#0358B6'
			: '#44DE28';
			*/
			console.log("dddd");
		//return opaque ? c : utils.transparentize(c, 1 - Math.abs(v / 150));
		return "#111";
	};
	



	let timeFormat = 'YY-MM-DD';
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
			/*
			elements: {
				line: {
					fill: false,
					backgroundColor: colorize.bind(null, false),
					borderColor: colorize.bind(null, true),
					//backgroundColor: colorize,
					//borderColor: colorize,
					borderWidth: 10
				}
			},
			*/
			tooltips: {
					mode: 'index',
					intersect: false,
					callbacks: {
		                label: function(tooltipItem, data) {
		                    //var label = data.datasets[tooltipItem.datasetIndex].label || '';
		                    let orgNam = data.datasets[tooltipItem.datasetIndex].label;
		                    let nam = orgNam.substring(0,4);
		                    
		                  //  console.log(orgNam, nam);
		                    //var label = fields_data[nam].info.nam || '';
		                    var label = orgNam; //"일반" || '';

		                   // if (orgNam == "avg")
		                   // 	label += "평균"
		                  //	else if (orgNam == "stack")
		                   // 	label += "누적"

		                   // console.log(label);

		                    if (label) {
		                        label += ' : ';
		                    }

		                    //console.log(tooltipItem.yLabel.toLocaleString());

		                    //label += Math.round(tooltipItem.yLabel * 100) / 100;
		                    label += tooltipItem.yLabel.toLocaleString();

		                    label += " °C";
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
			            display: true,
			            position:"bottom",
			            labels: {
			                fontColor: '#000',
			                boxWidth:24
			            }
			        },
			       
			scales: {
				
				xAxes: [
				{
					gridLines: {
						z:99999,
						lineWidth:1,
						//display: false ,
						drawOnChartArea:true,
						drawBorder: true,
						autoSkip:false,
						//stepSize: 1,
						
						//maxTicksLimit: 80,
						color: ['pink', 'red', 'orange', 'yellow', 'green', 'blue', 'indigo', 'purple',
						'pink', 'red', 'orange', 'yellow', 'green', 'blue', 'indigo', 'purple']
					},
					//display:true,
					
					//maxRotation:0,
					//drawBorder : false,
					//type: 'time',
					time: {
	                    unit: 'day',
	                    displayFormats: {
	                        day: timeFormat,
	                    }
	                },
					/*day: {
						displayFormats: {
	                        day: timeFormat,
	                    },
					},
					*/
					ticks: {
						// display: true,
						autoSkip:false,
						//autoSkipPadding: 100,
								maxRotation: 20,
						// stepSize: 1,
						maxTicksLimit: 80,
						
							callback: function(dataLabel, index) {
								return ((dataLabel.length == 2) ? dataLabel : "");
							}
							
						},
		            id: "x1"
		            
		        },		        
		        ],				

				yAxes: [
					{
						display:true,
						position: "right",
						ticks: {
				            max: 50,
				        	min: 0,
				        	//stepSize: 5
						},id: 1,
						scaleLabel: {
										display: true,
										labelString: '평균(°C)'
									}
					},
					{
						display:true,
						position: "left",
						ticks: {
				            max: 2000,
				        	min: 0,
				        	//stepSize: 5
						},id: 2,
						scaleLabel: {
										display: true,
										labelString: '누적(°C)'
									}
					}

				]
			}
			
		}
	}; // config


	
	chart = new Chart(canvas[0].getContext('2d'),config);

	this.viewMinMax = function(knd, isView) {
		//console.log(knd, isView);
		$.each(fields_data,function(i,field) {
			field.info["view_"+knd] = isView;
		});


	//	fields_data["f_"+knd].info.view = isView;
		$.each(chart.data.datasets, function( index, dataset ) {	

			if (dataset.label.indexOf(knd) > 0) {
				let lineLabel = dataset.label.substring(0,4);
				//console.log(lineLabel);
				if (fields_data[lineLabel].info.view) {
					dataset.hidden = !isView;	
				}
				
			}
		});
		chart.update();	
	};


	this.changeFieldView = function(knd, isView) {
		//console.log(knd, isView);
		fields_data["f_"+knd].info.view = isView;
		//fields_data["f_"+knd].info.view_min = isView;
		//fields_data["f_"+knd].info.view_max = isView;
		
		$.each(chart.data.datasets,function(index,dataset) {
			if (dataset.label.indexOf(knd) > 0) {
				if (isView) {
					if (dataset.label.indexOf("min")>0) {
	    				dataset.hidden = !fields_data["f_"+knd].info.view_min;
	    			} else if (dataset.label.indexOf("max")>0) {
	    				dataset.hidden = !fields_data["f_"+knd].info.view_max;
	    			} else {
	    				dataset.hidden = !fields_data["f_"+knd].info.view;
	    			}	
	    			//
				} else {
					dataset.hidden = !fields_data["f_"+knd].info.view;

				}
			}
		});

		$.each(chart.options.scales.yAxes,function(index,axis) {
			if (axis.id == "f_"+knd) {
				axis.display = isView;
			}
		});
		
		chart.update();	
	};

	this.getData = function(usr_id, farm_cde) {


		//alert("sss");
		//console.log(usr_id, farm_cde);

		let cultiPeriod = 90;

/*
		let mapGrowthStage = [
			{nam:"활착",       days:10,min:20,max:25,color:"red"},
			{nam:"초기영양생장",days:10,min:22,max:27,color:"blue"},
			{nam:"수정",       days:5,min:25,max:30,color:"green"},
			{nam:"비대",       days:20,min:26,max:30,color:"yellow"},
			{nam:"성숙",       days:15,min:20,max:25,color:"cyan"},
			{nam:"수확",       days:10,min:15,max:20,color:"#111"}
		];
		*/

		let mapGrowthStage = {
			1:{nam:"활착",color:"red"},
			11:{nam:"초기영양생장",color:"blue"},
			16:{nam:"수정",color:"green"},
			20:{nam:"비대",color:"yellow"},
			41:{nam:"성숙",color:"cyan"},
			74:{nam:"수확",color:"#111"}
		};

		chart.data.datasets = [];
	    //chart.options.scales.yAxes = [];

		var house_cde = $("#houselist_chart").val();
  		var startdat =  $("#startdat_chart").val();     
  	    

  	    let data_st = new Date(startdat);

  	    var year = data_st.getFullYear();
		var month = pad(data_st.getMonth()+1,2)
		var day = pad(data_st.getDate(),2);
		var incData = year+"-"+month+"-"+day;


		var enddat =  $("#enddat_chart").val();   
  	    //let now = new Date();
  	    let now = new Date(enddat);

  	    var today_year = now.getFullYear();
		var today_month = pad(now.getMonth()+1,2)
		var today_day = pad(now.getDate(),2);
		let today = today_year+"-"+today_month+"-"+today_day;

  	    var betweenDay = Math.round((now.getTime() - data_st.getTime())/1000/60/60/24);  

  	   	let labels = [];
  	   	
  	   
  	   	data_st = new Date(startdat);

  	   	let gridColors = [];

  	   	
  	    for (let i=0; i<cultiPeriod; i++) {
  	    	
    		var year = data_st.getFullYear();
    		var month = pad(data_st.getMonth()+1,2)
    		var day = pad(data_st.getDate(),2);
    		let incData = year+"-"+month+"-"+day;

    		//var day = moment(incData);
    		//labels.push(day);
    		if (mapGrowthStage[i]) {
    			gridColors.push("#424242");
    			labels.push([incData,mapGrowthStage[i].nam]);
    		//	labels.push([day,mapGrowthStage[i].nam]);
    		} else {
    			gridColors.push("");
    			labels.push(incData);
    			//labels.push(incData);
    			
    		}
    		
    		data_st.setDate(data_st.getDate() + 1);
  	    }
  	    
  	  
  	    chart.options.scales.xAxes[0].gridLines.color = gridColors;

		chart.data.labels = labels;

		//console.log(labels);
	
  	    sql_usr_id = "measuredata_stack_temp";
  	       
  	    var params = {
  	  		id:sql_usr_id,
  	  		farm_cde:farm_cde,
  	  		house_cde:house_cde,
  	  		usr_id:usr_id,
  	  		from_date:startdat,
  	  		to_date:today,
  	  		formURL:"datalist"
  	    };

  	    //console.log(params);

  	  
	    blight.read_get(params, function(result) {
	    	//console.log(result);
	    	//chart.data.datasets = [];
	    	//chart.options.scales.yAxes = [];
	    	fields_data.mea_avg.data = [];
	    	fields_data.mea_min.data = [];
	    	fields_data.mea_max.data = [];
	    	fields_data.mea_stack.data = [];



	    	if (result.status == "true") {
	    		
	    		let sumData = 0;
	    		$.each(result.data,function(i,data) {
	    			fields_data.mea_avg.data.push({x:data.mea_dat,y:data.f_03_avg});
	    			fields_data.mea_min.data.push({x:data.mea_dat,y:data.f_03_min});
	    			fields_data.mea_max.data.push({x:data.mea_dat,y:data.f_03_max});
	    			sumData = sumData + Number(data.f_03_avg);
	    			fields_data.mea_stack.data.push({x:data.mea_dat,y:sumData})
					
	    		});

	    		//console.log(fields_data.mea_stack.data);
	    		//console.log(fields_data.mea_stack.data.length);
	    		//console.log(fields_data.mea_stack.data[fields_data.mea_stack.data.length-1]);

	    		let lastDate = fields_data.mea_stack.data[fields_data.mea_stack.data.length-1].x;
	    		let lastValue = fields_data.mea_stack.data[fields_data.mea_stack.data.length-1].y;

	    		//console.log(lastDate);
	    		//console.log(lastValue);

	    		function numberWithCommas(x) {
				    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
				}




	    		

	    		let config_wm = {};
	    		
	    		if (lastValue > 1100) {
	    			$("#txt_stack" ).html(numberWithCommas(lastValue - 1100) + " °C");
	    			$("#txt_stack_cur" ).html("현재 적산온도 " + numberWithCommas(lastValue) + "°C 로");
	    			$("#txt_stack_info" ).html(" 초과되었습니다.");
	    			$("#canvas-holder").css("background-image", "url('/images/wmsick2.jpg'");
	    			console.log(lastValue);	
	    			config_wm = {
						type: 'pie',
						data: {
							datasets: [{
								data: [
									lastValue - 1100,
									1000 - (lastValue - 1100)
								],
								backgroundColor: [
									window.chartColors.red,
									window.chartColors.orange
								],
								label: 'Dataset 1'
							}],
							labels: [
								'Red',
								'Orange'
							]
						},
						options: {
							responsive: true,
							legend: {
									position: "none"
							},
						}
					};
	    		} else {
	    			$("#canvas-holder").css("background-image", "url('/images/wmgood.jpg'");
	    			$("#txt_stack_cur" ).html("현재 적산온도 ");
	    			$("#txt_stack" ).html(numberWithCommas(lastValue) + "°C");
	    			$("#txt_stack_info" ).html(" ");
	    			config_wm = {
						type: 'pie',
						data: {
							datasets: [{
								data: [
									lastValue,
									1000 - lastValue
								],
								backgroundColor: [
									window.chartColors.red,
									window.chartColors.orange
								],
								label: 'Dataset 1'
							}],
							labels: [
								'Red',
								'Orange'
							]
						},
						options: {
							responsive: true,
							legend: {
									position: "none"
							},
						}
					};
	    		}

	    		

				//console.log(config_wm);

				var ctx = document.getElementById('chart-area').getContext('2d');
			    let wmPie = new Chart(ctx, config_wm);
			    wmPie.update();


	    		let item_mea_avg = {
					type: "line",
					label: "평균",
					backgroundColor: "red",
					borderColor: "red",
					borderWidth:3,
					data: fields_data.mea_avg.data,
					fill: false,
					pointRadius: 0,
					lineTension :0,
					//steppedLine:true,
					//hidden:!fData.info.view,
					yAxisID: 1,
					//xAxisID: "x1"
				};
				chart.data.datasets.push(item_mea_avg);


				let item_mea_min = {
					type: "scatter",
					label: "최소",
					showLine:false,
					pointRadius: 5,
					data: fields_data.mea_min.data,
					pointStyle:"triangle",
					rotation:180,
					pointBorderColor:"blue",
					pointBackgroundColor:"blue",
					hidden:true,
					yAxisID: 1
				};
				chart.data.datasets.push(item_mea_min);

				let item_mea_max = {
					type: "scatter",
					label: "최대",
					showLine:false,
					pointRadius: 5,
					data: fields_data.mea_max.data,
					pointStyle:"triangle",
					rotation:0,
					pointBorderColor:"#FE9A2E",
					pointBackgroundColor:"#FE9A2E",
					hidden:true,
					//steppedLine:true,
					//hidden:!fData.info.view,
					yAxisID: 1
				};
				chart.data.datasets.push(item_mea_max);

				

				let item_std_stack = {
					type: "line",
					label: "누적",
					backgroundColor: "#FFBF00",
					borderColor: "green",
					borderWidth:0,
					//showLine:false,
					data: fields_data.mea_stack.data,
					fill: true,
					pointRadius: 0,
					//lineTension :0,
					//steppedLine:true,
					//hidden:!fData.info.view,
					yAxisID: 2
				};
				chart.data.datasets.push(item_std_stack);

					//console.log(chart.data);
	    		chart.update();	
	    		return;


		    	
	    	} else {
	    		jAlert("측정정보가 없습니다.", "측정정보 안내");
	    	}
	    });
	     
	};
};
