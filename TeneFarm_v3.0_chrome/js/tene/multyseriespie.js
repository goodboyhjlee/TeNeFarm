const MONTHS = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December'
];

var Utils_base = function() {
    this.numbers = function(config) {
      var cfg = config || {};
      var min = valueOrDefault(cfg.min, 0);
      var max = valueOrDefault(cfg.max, 100);
      var from = valueOrDefault(cfg.from, []);
      var count = valueOrDefault(cfg.count, 8);
      var decimals = valueOrDefault(cfg.decimals, 8);
      var continuity = valueOrDefault(cfg.continuity, 1);
      var dfactor = Math.pow(10, decimals) || 0;
      var data = [];
      var i, value;

      for (i = 0; i < count; ++i) {
        value = (from[i] || 0) + this.rand(min, max);
        if (this.rand() <= continuity) {
          data.push(Math.round(dfactor * value) / dfactor);
        } else {
          data.push(null);
        }
      }

      return data;
    };

    this.months = function (config) {
      var cfg = config || {};
      var count = cfg.count || 12;
      var section = cfg.section;
      var values = [];
      var i, value;

      for (i = 0; i < count; ++i) {
        value = MONTHS[Math.ceil(i) % 12];
        values.push(value.substring(0, section));
      }

      return values;
    };
    
};


var msp_base_b = function(args) {

  let chart;
  
  const actions = [
  {
    name: 'Randomize',
    handler(chart) {
      chart.data.datasets.forEach(dataset => {
        dataset.data = Utils.numbers({count: chart.data.labels.length, min: 0, max: 100});
      });
      chart.update();
    }
  },
  {
    name: 'Add Dataset',
    handler(chart) {
      const data = chart.data;
      const newDataset = {
        label: 'Dataset ' + (data.datasets.length + 1),
        backgroundColor: [],
        data: [],
      };

      for (let i = 0; i < data.labels.length; i++) {
        newDataset.data.push(Utils.numbers({count: 1, min: 0, max: 100}));

        const colorIndex = i % Object.keys(Utils.CHART_COLORS).length;
        newDataset.backgroundColor.push(Object.values(Utils.CHART_COLORS)[colorIndex]);
      }

      chart.data.datasets.push(newDataset);
      chart.update();
    }
  },
  {
    name: 'Add Data',
    handler(chart) {
      const data = chart.data;
      if (data.datasets.length > 0) {
        data.labels.push('data #' + (data.labels.length + 1));

        for (let index = 0; index < data.datasets.length; ++index) {
          data.datasets[index].data.push(Utils.rand(0, 100));
        }

        chart.update();
      }
    }
  },
  {
    name: 'Hide(0)',
    handler(chart) {
      chart.hide(0);
    }
  },
  {
    name: 'Show(0)',
    handler(chart) {
      chart.show(0);
    }
  },
  {
    name: 'Hide (0, 1)',
    handler(chart) {
      chart.hide(0, 1);
    }
  },
  {
    name: 'Show (0, 1)',
    handler(chart) {
      chart.show(0, 1);
    }
  },
  {
    name: 'Remove Dataset',
    handler(chart) {
      chart.data.datasets.pop();
      chart.update();
    }
  },
  {
    name: 'Remove Data',
    handler(chart) {
      chart.data.labels.splice(-1, 1); // remove the label first

      chart.data.datasets.forEach(dataset => {
        dataset.data.pop();
      });

      chart.update();
    }
  }
];


  const DATA_COUNT = 4;
  const NUMBER_CFG = {count: DATA_COUNT, min: 0, max: 100};

  const data = {
    labels: ['1', '2', '3', '4'],
    mydata: ['a', 'b', 'c', 'd'],
    radius: "50%",
    datasets: [
      {
        label: 'Dataset 1',
        data: [6,6,6,6],
        backgroundColor: ["#FF0000","#990066","#660099","#0000FF"]
      
      }
    ]
  };

  console.log(data);

  const config = {
    type: 'doughnut',
    //radius: "50%",
    data: data,
    //width:200,
    //width:"auto",
    //height:200,
    options: {
      title: {
                        display: false,
                        text: 'General Info'
                    },
      cutoutPercentage: 70,
      responsive: true,
      legend: {
          display: false,
          position: 'top',
        },
      
      onClick: function(point, event) {
          if(event.length <= 0) return;

          let index = event[0]['_index'];

          //console.log(event[0]._chart.config.data.mydata[index]);

          //console.log(event[0]['_index'])
      },
      
    }
  };
  

  



  var self = this;

  function degreesToRadians(degrees) {
    const pi = Math.PI;
    return degrees * (pi / 180);
  }



  this.draw = function() {
    let canvas = $("#chartmsp");
    Chart.plugins.register({beforeDraw : function(chart) {
      //if (chart.config.options.elements.center) {
        const context = chart.chart.ctx;
        context.fillStyle = "#ff0000";
        context.textAlign = 'center';
         //var centerConfig = chart.config.options.elements.center;
         //console.log(chart.config.options.elements);

         var centerX = ((chart.chartArea.left + chart.chartArea.right) / 2);
         var centerY = ((chart.chartArea.top + chart.chartArea.bottom) / 2);
          //context.font = "20px serif";
          //context.fillText('TeNeFarm', centerX,centerY);

        let radius = 50;
        context.font = "10px serif";
        for (var t=0; t<24; t++) {
          //if ((t % 45) == 0) {
            let x,y;
            //24:360 = t:x;
            let deg = ((360*t)/24)-90;
            x = (Math.cos(degreesToRadians(deg)) * radius) + centerX;
            y = (Math.sin(degreesToRadians(deg)) * radius) + centerY;
            context.fillText(t, x,y);
         // }
          


        }
     // }
        
        
      }});
    chart = new Chart(canvas[0].getContext('2d'),config);
    
    chart.update();

    
  }
};

