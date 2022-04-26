var priority_chart_base = function(args) {
	//console.log(args);
	let container;

	let div_chart = $("<div class='chart'>");
	div_chart.css("padding-top", "10px");
	let canvas = $("<canvas width='100%' height='60'>");
	div_chart.append(canvas);
	let chart;

	let timeFormat = 'hh:mm';
	let config =  {
		type: 'line',
		data: {
	    	labels: [],
	    	datasets: [	        
	            {
					backgroundColor: "#000",
					borderColor: "#000",
					fill: false,
					//data: cdata ,
					lineTension: 0,
					//pointRadius: pointRadius,
					//pointBorderColor:"#DF013A",
					//pointBackgroundColor:"#DF013A"
				},
				{
					type: 'scatter',
					//backgroundColor: "#000",
					//borderColor: "#000",
					//fill: false,
					//data: cdata ,
					//lineTension: 0,
					//pointRadius: pointRadius,
					pointRadius:8,
					pointBorderColor:"#DF013A",
					pointBackgroundColor:"#DF013A"
				}
			
	    	]
		},
		options: {
			title: {
				display: false,
				text: '1시간 평균'
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
				xAxes: [{
					type: 'time',
					time: {
						parser: timeFormat,
						unit:'hour',
						minUnit:"hour",
						stepSize:2
					}
				}],

				yAxes: [yAxis = {
					//type: 'linear', 
					display: true,
					position: 'left',
					ticks: {
			            max: 40,
			        	min: 5,
			        	stepSize: 5
					},
					id: 1,
				}],
			}
		}
	}; // config


	

	this.setContainer = function(incontainer, initData) {
		container = incontainer;
		container.append(div_chart);
		chart = new Chart(canvas[0].getContext('2d'),config);
		self.setData(initData);
		//init();
	}

	var self = this;

	let init = function() {
		//container.append(div_chart);
	};


	this.setCurSensorData = function(curTime, sensorValue) {
		//console.log(curTime,sensorValue);
		//console.log(chart);
		if (chart) {

			let csensorData = [];
			csensorData.push({x:curTime,y:sensorValue});
			chart.data.datasets[1].data = csensorData;	
			chart.update();		
		}
		
	};
	
  	this.setDataa = function(data) {
  		let firstitem = data[0];
  		let lastitem = data[data.length-1];
  		let labels = [];
  		let cdata = [];
  		//let csensorData = [];
  		let pointRadius = [];
  		for (let i=firstitem.rt_h;i<=lastitem.rt_h;i++) {
  			let label = new Date();
  			label.setHours(i); 
  			label.setMinutes(0);
  			label.setSeconds(0);
  			labels.push(label);
  		}
  		$.each(data, function( index, item ) {	
  			let label = new Date();
  			label.setHours(item.rt_h); 
  			label.setMinutes(item.rt_m);
  			cdata.push(
  				{
  					x:pad(label.getHours(),2)+":"+pad(label.getMinutes(),2),
  					y:item.val_max
  				
  				}
  			);
  			pointRadius.push(5);
  		});

		pointRadius.push(0);

		chart.data.labels = labels;
		chart.data.datasets[0].data = cdata;		

		//csensorData.push({x:"12:00",y:10});
		//chart.data.datasets[1].data = csensorData;	
		
	//console.log(config);
	
	
		chart.update();	
	
  		
  	};

  	


};