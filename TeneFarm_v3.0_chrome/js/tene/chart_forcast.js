


var forcastchart_base_b = function(args) {

  let chart;
  
  


  

  
  



  var self = this;

  let townforecast = function( nowDate, nowTime) {
    //console.log(nowDate);
    //console.log(Number("1000".substring(0,2)));
    let apikey = "Fow%2B1%2BLwKWyZbn%2FEW4126%2FLzLBdS1Lei6FbVJBF8QhVa%2F8NjLFMriPZs9eGzpg20v1J%2Fve1dbCCfp9DAa013pQ%3D%3D";
    var xhr = new XMLHttpRequest();
    var url = 'http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst'; /*URL*/


    var queryParams = '?' + encodeURIComponent('serviceKey') + '='+ apikey; /*Service Key*/
    queryParams += '&' + encodeURIComponent('pageNo') + '=' + encodeURIComponent('3'); /**/
    queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('1200'); /**/
    //queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('300'); /**/
    queryParams += '&' + encodeURIComponent('dataType') + '=' + encodeURIComponent('JSON'); /**/
    queryParams += '&' + encodeURIComponent('base_date') + '=' + encodeURIComponent(nowDate); /**/
    queryParams += '&' + encodeURIComponent('base_time') + '=' + encodeURIComponent('0500'); /**/
    queryParams += '&' + encodeURIComponent('nx') + '=' + encodeURIComponent('53'); /**/
    queryParams += '&' + encodeURIComponent('ny') + '=' + encodeURIComponent('37'); /**/
    xhr.open('GET', url + queryParams);
    xhr.onreadystatechange = function () {
        if (this.readyState == 4) {
            //console.log(this.status+'nHeaders: '+JSON.stringify(this.getAllResponseHeaders())+'nBody: '+this.responseText);
            //console.log(this.responseText);
            const json = JSON.parse(this.responseText);
            //console.log(json);

            console.log(json.response.header.resultCode);
            let resultCode = json.response.header.resultCode;
            if (resultCode == "00") {
              $.each(json.response.body.items, function( index, item ) {  
                console.log(item);
                let catName = item.category;
                let fcstDate = item.fcstDate;
                let fcstTime = item.fcstTime;
                let fcstValue = item.fcstTime;
              });
              
            }


            return;


            var xmlDoc = this.responseXML;
            //console.log(xmlDoc);
            //let resultCode = xmlDoc.getElementsByTagName("header")[0].getElementsByTagName("resultCode")[0].textContent;
            console.log(resultCode);

            if (resultCode == "00") {
              
              let items = xmlDoc.getElementsByTagName("body")[0].getElementsByTagName("items")[0];
              let itemlist = items.childNodes;

             
              $.each(itemlist, function( index, item ) {  
                //console.log(item);
                
                let catName = item.getElementsByTagName("category")[0].textContent;
                let fcstDate = item.getElementsByTagName("fcstDate")[0].textContent;
                let fcstTime = item.getElementsByTagName("fcstTime")[0].textContent;
                let fcstValue = item.getElementsByTagName("fcstValue")[0].textContent;
                let obsTime = Number(fcstTime.substring(0,2));
                if ( (catName == "TMN") || (catName == "TMX") ) {
                  if (catName == "TMN")
                    tmn = fcstValue;
                  if (catName == "TMX")
                    tmx = fcstValue;
                } 

               // console.log(catName, fcstDate + " " + fcstTime + " : " + fcstValue);
                /*
                if ( (nowDate == fcstDate) && (nowTime < obsTime) ) {
                  if (catName == "POP") {
                    sumpop += Number(fcstValue);
                    cntpop++;
                  } else if (catName == "WSD") {
                    sumwsd += Number(fcstValue);
                    cntwsd++;
                  } else if (catName == "VEC") {
                    sumvec += Number(fcstValue);
                    cntvec++;
                  } else if (catName == "SKY") {
                    avgsky = Math.max(avgsky,fcstValue);
                  }
                }     
                */      
              });

          

              
            }
        }
    };

    xhr.send('');
  };

  this.getForcastData = function() {
    var d = new Date();
    townforecast(d.getFullYear() + "" + pad((d.getMonth()+1) ,2)+ "" + pad((d.getDate()) ,2), d.getHours());
   // console.log(data);
  };
  

  this.draw = function() {
    let canvas = $("#chartforcast");
    Chart.plugins.register({beforeDraw : function(chart) {
      //if (chart.config.options.elements.center) {
        const context = chart.chart.ctx;
        context.fillStyle = "#ff0000";
        context.textAlign = 'center';
         //var centerConfig = chart.config.options.elements.center;
         //console.log(chart.config.options.elements);

         var centerX = ((chart.chartArea.left + chart.chartArea.right) / 2);
         var centerY = ((chart.chartArea.top + chart.chartArea.bottom) / 2);
          context.font = "40px serif";
          context.fillText('TeNeFarm', centerX,centerY);

        let radius = 300;
        context.font = "20px serif";
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

