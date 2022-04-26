var tenechart_base = function(args) {
	var self = this;

	let container = args.container;
	//let labels = args.labels;
	let canvas = $("<canvas width='100%' height='60'>");
	args.container.append(canvas);

	this.setLegend = function(tf) {
		self.chart.options.legend.display = tf;
		self.chart.update();
	}

	this.update = function(labels) {
		self.chart.update();
	};


	this.setLabels = function(labels) {
		//console.log(labels);
		self.chart.data.labels = labels;
	};

/*
	this.shiftLabel = function(label) {
		self.chart.data.labels.shift();
		let updateLables = self.chart.data.labels;
		updateLables.push(label);
		self.chart.data.labels = updateLables;	
		self.chart.update();	
	};
	*/

	this.addDataset = function(data) {
		self.chart.data.datasets.push(data);
	};

	this.updateData = function(index, hm,sval) {
		let h60 = (hm / 60);
      	let hh = Math.floor(h60);
      	let mm = (h60 - hh) * 60;
		let data = self.chart.data.datasets[0].data[index];
		data.x = pad(hh,2) + ":" + pad(mm,2);
		data.y = Number(sval);
		self.chart.update();


	};

	this.shiftData = function(key, value, curTime) {
		//console.log(key, value);
		//console.log(self.chart.data.datasets);
		let data;
		if (key < 5) {
			data = self.chart.data.datasets[key-1].data;
		} else {
			//data = self.chart.data.datasets[key-2].data;	
			data = self.chart.data.datasets[key-5].data;	
		}

		data.shift();
		let updateData = data;
		updateData.push(value);
		data = updateData;

		

		self.chart.data.labels.shift();
		let updateDataLabel = self.chart.data.labels;
		updateDataLabel.push(curTime);
		self.chart.data.labels = updateDataLabel;	

		self.chart.update();

	};

	this.removeData = function(index) {
		let data = self.chart.data.datasets[0].data;
		let labels = self.chart.data.labels;
		data.splice(index, 1);
		labels.splice(index, 1);
		//console.log(data, labels);
	};
	


	this.addyAxis = function(sensor) {
		//console.log(sensor);
		let position = "right";
		let stepSize = 10;
		let display = false;

		if (sensor.sk == "03") {
			position = "left";
			display = true;
		} else if (sensor.sk == "04") {
			stepSize = 20;
			display = true;
		} else if (sensor.sk == "05") {
			stepSize = 400;
		} else if (sensor.sk == "06") {
			stepSize = 400;
		} else if (sensor.sk == "14") {
			stepSize = 20;
		} else if (sensor.sk == "11") {
			stepSize = 20;
		} else if (sensor.sk == "12") {
			stepSize = 20;
		}

		let yAxis = {
			type: 'linear', 
			display: display,
			position: position,
			ticks: {
	            max: sensor.ch,
	        	min: sensor.cl,
	        	stepSize: stepSize
			},
			id: sensor.key,
		};
		self.chart.options.scales.yAxes.push(yAxis);
	};


	

	this.chart = new Chart(canvas[0].getContext('2d'), {
	    type: 'line',
	    data: {
	        labels: [],
	        datasets: []
	    },
	    options: {
			responsive: true,
			//hoverMode: 'index',
			stacked: false,
			title: {
				display: false,
				text: '1시간 평균'
			},
			legend: {
	            display: true,
	            position:"bottom",
	            labels: {
	                fontColor: '#000',
	                boxWidth:24
	            },

				onClick: function(event, legendItem) {
					var index = legendItem.datasetIndex;
				    var ci = this.chart;
				   // console.log(ci);
				    var meta = ci.getDatasetMeta(index);

				    // See controller.isDatasetVisible comment
				    meta.hidden = meta.hidden === null ? !ci.data.datasets[index].hidden : null;

				   // console.log(meta.hidden);

				    this.chart.options.scales.yAxes[index+1].display = !this.chart.options.scales.yAxes[index+1].display;

				    // We hid a dataset ... rerender the chart
				    ci.update();
				    /*
					console.log('onClick:' , legendItem);
					console.log(this.chart.data);

					//legendItem.hidden = true;
					this.chart.options.scales.yAxes[legendItem.datasetIndex+1].display = !this.chart.options.scales.yAxes[legendItem.datasetIndex+1].display;
					//console.log(this.chart.options.scales.yAxes);
					self.chart.update();
					*/
				}
	        },
			scales: {
				yAxes: [{
					type: 'linear', 
					display: false,
					position: "left",
					ticks: {
			            max: 1,
			        	min: 0,
			        	stepSize: 1
					},
					id: 9,
				}]
			}
		}
	});


	this.init = function(data,knd) {	
		//self.chart.data.datasets = [];	
		self.chart.data.labels = data.labels;	

		if (knd) {
			let sensor = {
				sk:"03",
				cl:0,
				ch:40,
				key:1
			};
			//console.log(sensor);
			self.addyAxis(sensor);
			let timeFormat = 'HH';
			self.chart.options.scales["xAxes"] = [{
				type: 'time',
				time: {
					parser: timeFormat,
					unit:'hour',
					minUnit:"hour",
					stepSize:2
				}
			}];

			self.setLegend(false);
			
		}
		
	};


};