var chart_step_base = function() {
	let chart;
	let fields_data = {};
	let container = $("#stepcontainer");
	let div_chart = $("<div class='chart'>");
	let canvas = $("<canvas>");
	canvas.css("width","96%" );
	div_chart.append(canvas);
	container.append(div_chart);
	let config;
	let range = 70;
	let ccnt = range/5;	

	let steps = ["생육촉진기","출뢰기","개화기","비대기","수확기"];
	let steps_map = new Map();

	//let steps = {0:"생육촉진기",1:"출뢰기",2:"개화기",3:"과실비대기",4:"수확기"};

	this.init = function() {
		let timeFormat = 'yyyy-mm-dd hh';
		config =  {
			type: 'line',
			lineAtIndex: [0,14,28,42,56],
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
			                    //console.log(orgNam);
			                   // console.log(nam);
			                   // var label = fields_data[orgNam].info.nam || '';
			                    var label = orgNam;

			                    //if (orgNam.indexOf("min")>0)
			                    //	label += "(최소)"
			                  	//else if (orgNam.indexOf("max")>0)
			                    //	label += "(최대)"

			                    if (label) {
			                        label += ': ';
			                    }
			                    label += Math.round(tooltipItem.yLabel * 100) / 100;
			                    console.log(label);
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

		

  		const verticalLinePlugin = {
		  getLinePosition: function (chart, pointIndex) {
		  	//console.log(chart.chart.data);
		      const meta = chart.getDatasetMeta(2); // first dataset is used to discover X coordinate of a point
		      //const meta = chart.chart.data.datasets[0];
		      const data = meta.data;
		      return data[pointIndex]._model.x;
		  },
		  renderVerticalLine: function (chartInstance, pointIndex) {
		      const lineLeftOffset = this.getLinePosition(chartInstance, pointIndex);
		      //const scale = chartInstance.scales['y-axis-0'];
		      const scale = chartInstance.scales['guid_am'];
		      const context = chartInstance.chart.ctx;

		      // render vertical line
		      context.beginPath();
		      context.strokeStyle = '#4B2203';
		      context.moveTo(lineLeftOffset, scale.top+30);
		      context.lineTo(lineLeftOffset, scale.bottom);
		      context.stroke();

		      // write label
		      context.fillStyle = "#1338ED";
		      context.textAlign = 'center';
		      let txt = steps_map.get(pointIndex);
		      //context.fillText('MY TEXT', lineLeftOffset, (scale.bottom - scale.top) / 2 + scale.top);
		      context.fillText(txt, lineLeftOffset, scale.top+20);
		  },

		  afterDatasetsDraw: function (chart, easing) {
		      if (chart.config.lineAtIndex) {
		          chart.config.lineAtIndex.forEach(pointIndex => this.renderVerticalLine(chart, pointIndex));
		      }
		  }
		  };

		  Chart.plugins.register(verticalLinePlugin);
		  

		
	};

	this.draw = function() {
		
		//labels.push(data.mea_dat);
		//config.data = 
		var startdat =  $("#startdat_step").val();   
		var house_cde = $("#houselist_step").val(); 
  	       
  	    var params = {
  	  		id:"measuredata_v2_d",
  	  		farm_cde:"KRJJ000001",
  	  		house_cde:house_cde,  	  		
  	  		from_date:startdat,  	  		
  	  		formURL:"datalist"
  	    };

		blight.read_get(params, function(result) {
	    	if (result.status == "true") {
	    		self.addData(result.data, "낮온도","#020822", "temp_day");

	    		params.id = "measuredata_v2_n";
	    		blight.read_get(params, function(result) {
		    		if (result.status == "true") {
		    			self.addData(result.data, "밤온도","#66ED13", "temp_night");
		    			self.addbasedata();
		    			chart = new Chart(canvas[0].getContext('2d'),config);
		    			chart.update();	
		    		}
	    		});

	    		
	    	}
	    });
	};

	this.addbasedata = function() {

		

  	    //console.log(params);
  	  
	    
		fields_data["guid_am"] = {
			info : {
				nam: "낮",
				color_axis: "#02021F",
				color_day: "#F0440B",
				color_night: "#02021F",
				ymin:0,
				ymax:40,
				view:true,
			},
			data_day_min : [],
			data_day_max : [],
			data_night_min : [],
			data_night_max : []
		};	

		let org_data_day_min = [28,25,23,20,20];
		let org_data_day_max = [30,26,25,23,23];
		let org_data_night_min = [10,8,5,5,5];
		let org_data_night_max = [13,10,8,7,6];

		

		let data_day_min = [];
		let data_day_max = [];
		let data_night_min = [];
		let data_night_max = [];

		//console.log(ccnt);
		let labels = [];
		var idx = 0;
		for (var k=0; k<range; k++) {				
			labels.push(k+1);
			if ((k % ccnt) == 0) {
				steps_map.set(k,steps[idx++]);
			}
		}

		//console.log(steps_map);
		//console.log(steps_map.get(0));
		
		for (var k=0; k<5; k++) {	
			for (var kk=0; kk<ccnt; kk++) {			
				fields_data["guid_am"].data_day_min.push(org_data_day_min[k]);				
				fields_data["guid_am"].data_day_max.push(org_data_day_max[k]);				
				fields_data["guid_am"].data_night_min.push(org_data_night_min[k]);				
				fields_data["guid_am"].data_night_max.push(org_data_night_max[k]);
			}
		}

		//console.log(fields_data);

		$.each(fields_data,function(i,fData) {
			let position = "left";

			let yAxis = {
				//display: field.info.view,
				display:true,
				position: position,
				color:fData.info.color,
				//backdropColor:field.info.color,
				//textStrokeColor:field.info.color,


				scaleLabel: {
					display: false,
					//padding:-16,
					padding:-30,
					lineHeight:100,
					fontColor:fData.info.color_axis,
					labelString: fData.info.nam
				},
				ticks: {
		            max: fData.info.ymax,
		        	min: fData.info.ymin,
		        	fontColor:fData.info.color_axis
		        	//stepSize: 5
				},id: i
			}
			//chart.options.scales.yAxes.push(yAxis);
			config.options.scales.yAxes.push(yAxis);
			let item_day_min = {
				type: "line",
				label: "낮최소",
				backgroundColor: fData.info.color_day,
				borderColor: fData.info.color_day,
				borderWidth:2,
				data: fData.data_day_min,
				fill: false,
				pointRadius: 0,
				hidden:false,
				yAxisID: i
			};

			let item_day_max = {
				type: "line",
				label: "낮최대",
				backgroundColor: fData.info.color_day,
				borderColor: fData.info.color_day,
				borderWidth:2,
				data: fData.data_day_max,
				fill: "-1",
				pointRadius: 0,
				hidden:false,
				yAxisID: i
			};

			let item_night_min = {
				type: "line",
				label: "밤최소",
				backgroundColor: fData.info.color_night,
				borderColor: fData.info.color_night,
				borderWidth:2,
				data: fData.data_night_min,
				fill: false,
				pointRadius: 0,
				hidden:false,
				yAxisID: i
			};

			let item_night_max = {
				type: "line",
				label: "밤최대",
				backgroundColor: fData.info.color_night,
				borderColor: fData.info.color_night,
				borderWidth:2,
				data: fData.data_night_max,
				fill: "-1",
				pointRadius: 0,
				hidden:false,
				yAxisID: i
			};

			config.data.datasets.push(item_day_min);
			config.data.datasets.push(item_day_max);
			config.data.datasets.push(item_night_min);
			config.data.datasets.push(item_night_max);
		});

		//let labels = ["생육촉진기","출뢰기","개화기","과실비대기","수확기"];
		
		config.data.labels = labels;
	};

	this.addData = function(rdata,label, color, yname) {
		
	    		let item_day_temp = {
					type: "line",
					label: label,
					backgroundColor: color,
					borderColor: color,
					borderWidth:2,
					data: [],
					fill: false,
					pointRadius: 0,
					hidden:false,
					yAxisID: yname
				};
				//let data = result.data;
				$.each(rdata,function(index,data) {
					$.each(data,function(i,value) {
	    				if (value != null) {
		    				if ( (i != "house_cde") && (i != "mea_dat") ) {
			    				let fieldName = i.substring(0,6);
		    					if (i.indexOf("min")>0) {
			    					//fields_data[fieldName].min.push(Number(data[i]));
				    			} else if (i.indexOf("max")>0) {
				    				//fields_data[fieldName].max.push(Number(data[i]));
				    			} else {
				    				item_day_temp.data.push(Number(data[i]));
				    			}			    				
				    		}
				    	}
	    			});
	    		});

	    		let yAxis = {
					display:false,
					position: "left",
					color:color,
					scaleLabel: {
						display: false,
						padding:-30,
						lineHeight:100,
						fontColor:color,
						labelString: label
					},
					ticks: {
			            max: 40,
			        	min: 0,
			        	fontColor:color
			        	//stepSize: 5
					},id: yname
				}				
				config.options.scales.yAxes.push(yAxis);
		    	config.data.datasets.push(item_day_temp);



		    	
	    	
	};

	var self = this;
	self.init();

}