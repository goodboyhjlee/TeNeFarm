
var cctv_base = function(cam, scene) {
	//console.log(cam);
	//var cctv_shanshot = $('<img>');	
	var cctv_shanshot = $('<webview>');	


	let name = cam.cm;
	//let ip = cam.lip;
	//let port = cam.rtsp_port;
	let ip = cam.cam_ip;	
	let port = cam.http_port;

	let user = cam.cam_user;
	let pwd = cam.cam_pwd;
	//let hport = cam.http_port;
	//let rport = cam.rtsp_port;

	let cmd_s = cam.cmd_s;
	let cmd_v = cam.cmd_v;

	let cammap = {
		"s01":"402",
		"s02":"502",
		"s03":"602",
		"s04":"102",
		"s05":"202",
		"s06":"302",
		"s07":"702",
		"s08":"802",
	};

	//http://223.171.134.58:5004/ISAPI/Streaming/channels/102/picture	
	//let url_snap  = "http://cctv:5131220b@" + ip +  ":"+ port + "/view/" + cmd_s;	;	
	//let url_snap  = "http://admin:5131220b@" + "30.1.6.202" +  ":"+ port + "/ISAPI/Streaming/channels/" + cammap[cmd_s] + "/picture";
	let url_snap  = "http://admin:5131220b@" + ip +  ":"+ "5004" + "/ISAPI/Streaming/channels/" + cammap[cmd_s] + "/picture";
	//let url_snap  = "http://admin:5131220b@" + "30.1.6.202" +  ":"+ "80" + "/ISAPI/Streaming/channels/" + cammap[cmd_s] + "/picture";
	let url_mjpeg = "http://cctv:5131220b@" + ip +  ":"+ port + "/view/" + cmd_v;		

	//console.log(url_snap);


/*
	var xhr = new XMLHttpRequest();
	//xhr.open('GET', "http://" + ip + ":" + rport + "/" + cam.cmd_s, true, "cctv", "5131220b");
	//xhr.open('GET', "http://" + ip + ":" + hport + "/view/" + cam.cmd_s, true, "cctv", "5131220b");
	xhr.open('GET', "http://" +user+":"+ pwd +"@" + ip + ":" + port + "/view/" + cmd_s, true);
	xhr.setRequestHeader("Authorization", "Basic " + btoa(user + ":" + pwd));
	xhr.responseType = 'blob';
	xhr.onreadystatechange = function() { // 요청에 대한 콜백
		//console.log(111);
      //if (xhr.readyState == xhr.DONE) { // 요청이 완료되면
        //if (xhr.status == 200 || xhr.status == 201) {
        	//console.log(xhr.status);
        if (xhr.status == 200 ) {
          //console.log("OK");

          xhr.abort()
          onetime = 1;
        } 
     // }
    };
    
        xhr.send();
        */
     

	

	var self = this;
	
	this.init = function() {
		self.makecamera();
	};


	this.makecamera = function() {
	  	if (cam.isuse == "Y") {

		  	let cam_width =  180 ;
		  	let cam_height = (cam_width*3) / 4;

			var cam_out = $("<div>");
			cam_out.addClass("cam_out");
			cam_out.css("width",cam_width + "px");
			cam_out.css("height",cam_height + "px");
			cam.container.append(cam_out);
			
			  
			var cam_out_body = $("<div>");
			cam_out_body.addClass("cam_out_body");
			cam_out_body.css("width",cam_width + "px");
			cam_out_body.css("height",cam_height + "px");
			cam_out.append(cam_out_body);
			
			var cam_out_header = $("<div>");
			cam_out_header.css("display","none");
			cam_out_header.addClass("cam_out_header");

			cam_out_body.append(cam_out_header);		  
			cam_out_header.html(cam.cm);  
			
		
			
			var cam_play = $("<div>");
			cam_play.addClass("cam_play");
			cam_play.css("display","none");
			cam_out_body.append(cam_play);	

			//var div_play = $("<div>");
			//div_play.addClass("div_play");

			var div_full = $("<div>");
			div_full.addClass("div_full");

			//cam_play.append(div_play);
			cam_play.append(div_full);

/*
			var cam_play_img = $("<img >");
			cam_play_img.attr("src","./css/iot/images/play.png");
			cam_play_img.attr("play",false);
			div_play.append(cam_play_img);	
			*/

			var cam_play_fullscreen = $("<img >");
			cam_play_fullscreen.attr("src","./css/iot/images/fullscreen.png");
			cam_play_fullscreen.attr("full",false);
			div_full.append(cam_play_fullscreen);	


			cam_out_body.hover(
				
			  function() {
			   cam_play.css("display","block");
			    cam_out_header.css("display","block");
			  }, function() {
			    cam_play.css("display","none");
			    cam_out_header.css("display","none");
			  }			  
			);


			cctv_shanshot.attr("id","shapshot"+cam.cc);
			cctv_shanshot.css("width",cam_width + "px");
			cctv_shanshot.css("height",cam_height + "px");

			cam_out_body.append(cctv_shanshot);

			//console.log(url_snap);

			//cctv_shanshot.attr("src",url_snap);

			/*
			cctv_shanshot.on('click', function(e) {
				console.log('sss');
			});
			*/

			cctv_shanshot.css("display","none");

			cam_out_body.on('click', function(e) {
				//console.log(url_snap);
				cctv_shanshot.css("display","block");
				cctv_shanshot.attr("src",url_snap);
			});
			


/*
		  	cam_play_img.on('click', function(e) {

		  		if (cam_play_img.attr("play") == "true") {
		  			//console.log(url_snap);
			  		cam_play_img.attr("play",false);
			  		cam_play_img.attr("src","./css/iot/images/play.png");
			  		cctv_shanshot.attr("src",null);
			  		//cctv_shanshot.attr("src","./css/iot/images/camera-update.png");
			  		cctv_shanshot.attr("src",url_snap);
			  	} else {
			  		cam_play_img.attr("play",true);
			  		cam_play_img.attr("src","./css/iot/images/stop.png");
			  		cctv_shanshot.attr("src",null);
			  		//cctv_shanshot.attr("src","./css/iot/images/camera-update.png");
			  		cctv_shanshot.attr("src",url_mjpeg);
			  	}
		  	});
		  	*/

		  	cam_play_fullscreen.on('click', function(e) {
		  		if (cam_play_fullscreen.attr("full") == "true") {
			  		cam_play_fullscreen.attr("full",false);
			  		cam_play_fullscreen.attr("src","./css/iot/images/fullscreen.png");
			  	} else {
			  		//console.log(cam);
			  		//window.parent.postMessage( {cmd:1,url:cctv_shanshot.attr("src"),
			  		window.parent.postMessage( {cmd:1,url:url_mjpeg,
			  			//cam_ip:cam.cam_ip,
			  			//http_port:cam.http_port,
			  			cam_ip:ip,
			  			http_port:port,

			  			chanel:cam.chanel,
			  			user:cam.cam_user,
			  			pw:cam.cam_pwd,
			  			knd:cam.knd}, 
			  			'*' );
			  	}
		  	});
	  }
	  
	};

};