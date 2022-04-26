
      
$( document ).bind( "mobileinit", function() {
  $.mobile.hashListeningEnabled = false;
  $.mobile.pushStateEnabled = false;
  $.mobile.changePage.defaults.changeHash = false;
});


var isAutoLogined = true;

var proj = new proj_base();



function fullscreen(elem) {
  if (elem.requestFullscreen) {
  elem.requestFullscreen();
  } else if (elem.mozRequestFullScreen) { /* Firefox */
  elem.mozRequestFullScreen();
  } else if (elem.webkitRequestFullscreen) { /* Chrome, Safari & Opera */
  elem.webkitRequestFullscreen();
  } else if (elem.msRequestFullscreen) { /* IE/Edge */
  elem.msRequestFullscreen();
  }
}

function closeFullscreen() {
  if (document.exitFullscreen) {
    document.exitFullscreen();
  } else if (document.mozCancelFullScreen) {
    document.mozCancelFullScreen();
  } else if (document.webkitExitFullscreen) {
    document.webkitExitFullscreen();
  } else if (document.msExitFullscreen) {
    document.msExitFullscreen();
  }
}

window.onload = function() {

  window.addEventListener('message', function(event) {
    let cam = event.data;
    //console.log(cam);
    let cmd = cam.cmd;
    if (cmd==1) {
      let camUrl = cam.url;
      let senddata = {
        url : camUrl
      };
      $("#webviewfull").attr("src",camUrl);
      $("#divfull").css("display","block");


      $("img[name='camperation']").attr("data-ip",cam.cam_ip);
      $("img[name='camperation']").attr("data-port",cam.http_port);
      $("img[name='camperation']").attr("data-chanel",cam.chanel);
      $("img[name='camperation']").attr("data-user",cam.user);
      $("img[name='camperation']").attr("data-pw",cam.pw);
      $("#preset").attr("data-ip",cam.cam_ip);
      $("#preset").attr("data-port",cam.http_port);
      $("#preset").attr("data-chanel",cam.chanel);
      $("#preset").attr("data-user",cam.user);
      $("#preset").attr("data-pw",cam.pw);

      //console.log(cam.knd);

      if( cam.knd == 0) {
        $(".isptz").css("display","none");
      } else {
        $(".isptz").css("display","inline-block");
      }


     // fullscreen($("#divfull")[0]);


    } else if (cmd==0) {
      $("#cctvfull").removeClass("cctvfull_on");
      $("#cctvfull").addClass("cctvfull_off");
    }
    }, false
  );

  $("#cctvfullexit").click(function(){
    $("#divfull").css("display","none");
    $("#webviewfull").attr("src",null);
    //closeFullscreen() ;
  });
  
  $(".dynamin_container").css("max-height", ($( window ).height() - 100) + "px");  
  $( window ).resize(function() {    
    $(".dynamin_container").css("max-height", ($( window ).height() - 100) + "px"); 
    //console.log(TENEFARM.historydata.refreshCanvas());
    TENEFARM.historydata.refreshCanvas();
  });
 



  $("#farm3dcontainer").css("height", ($( window ).height() - 80) + "px");  
  
  
  $("#control_container").scroll(function(){    
    var aa = document.elementFromPoint(1,120);
    var nam = $(aa).attr("hc");
    $(".float_layer").html(nam);
    if (nam == undefined)
      $(".float_layer").css("display","none");
    else
      $(".float_layer").css("display","block");  
  });
  
 
  

  

  

  /*
  
  $("#close_cctv").click(function(){
    $("#rtsp_scene").unbind("load");
    //console.log("colose cctv");
   	   $.mobile.back();
	});
  */
  
  $("#intro").click(function(){
   // let serverIP = $(this).attr("data-serverip");
    //console.log(serverInfos);
   	   jConfirm("종료하시곘습니까?","종료",function(e) {
   	     if (e) {
   	       chrome.sockets.udp.getSockets(function(socketInfos) {
              $.each(socketInfos,function(index,socketInfo) {
               // console.log(socketInfo);
               let socketId = socketInfo.socketId;
               
                var arrayBuffer = new ArrayBuffer(1);
      	        var buffer = new Uint8Array(arrayBuffer);
      	        buffer[0] = HeaderDefine_COMMON.C_WEB_CLIENT_CLOSE;
                console.log(serverInfos);
                if (serverInfos[socketId]) {
                  chrome.sockets.udp.send(socketInfo.socketId, arrayBuffer, serverInfos[socketId].serverIP, serverInfos[socketId].serverPort, function() {});    
                  setTimeout(function(){
                    chrome.sockets.udp.close(socketInfo.socketId, function(e) {
                      //console.log(e);
                    });
                  }, 200);
                }
                
              });

              setTimeout(function(){
                window.close();
              }, (200*socketInfos.length)+100);
            });
   	     }
   	   });
		});
   
  
  $("#logout").click(function(){
			jQuery.mobile.changePage( "#page_login",{transition: "flip"}  );
	});
  
  $("#login_submit").click(function(event){
    event.preventDefault();
    
    blight.login("frm_login",function(result) {
      //console.log(result);
      if (result.status == "true") {
        
        let usr_id = result.usr_id;
        let usr_pw = result.usr_pw;
        let usr_ni = result.usr_ni;
        chrome.storage.local.set({"tenefarm_usr_id":usr_id});
  	    chrome.storage.local.set({"tenefarm_usr_pw":usr_pw});
        chrome.storage.local.set({"tenefarm_usr_ni":usr_ni});
        

        isAutoLogined = false;
        proj.init({usr_id:usr_id,usr_ni:usr_ni});
        $.mobile.back();
  		  return false;
      } else {
        jAlert(names.login_fail, names.login_fail_title);
        return false;
      }
    });
	});
    
  
  /*
chrome.storage.local.set({"tenefarm_usr_id":"jjuniv"});
        chrome.storage.local.set({"tenefarm_usr_pw":"5131220b"});
        chrome.storage.local.set({"tenefarm_usr_ni":"제주대학교"});
 */
      

      chrome.storage.local.get('tenefarm_usr_id', function(local_usr_id){
      var usr_id = local_usr_id.tenefarm_usr_id;      
        chrome.storage.local.get('tenefarm_usr_pw', function(local_usr_pw){
        var usr_pw = local_usr_pw.tenefarm_usr_pw;
        if ( (usr_id !== undefined) && (usr_pw !== undefined) ) {
          $("#usr_id").val(usr_id);
          $("#usr_pw").val(usr_pw);
          
          blight.login("frm_login",function(result) {
            if (gutil.boolean(result.status)) {
                  chrome.storage.local.get('tenefarm_usr_pw', function(local_usr_ni){
                    var usr_ni = local_usr_ni.tenefarm_usr_ni;
              
                   proj.init({usr_id:usr_id,usr_ni:usr_ni});
              });
              return;
            }
          });
        } else {
          if (!isAutoLogined)
            jAlert(names.login_fail, names.login_fail_title);
          jQuery.mobile.changePage( "#page_login",{transition: "flip"}  );
          return false;
        }
      }); //iot_gthings_usr_pw
    }); // iot_gthings_usr_id
};

//webView.getSettings().setDisplayZoomControls(false);

/*
chrome.app.window.onClosed.addListener(function() {
  // Do some simple clean-up tasks.
  $.ajax({url:"http://tezaur-local/auth/logout", async:false});
  //var expires = new Date();
//  expires.setTime(expires.getTime() + (1 * 24 * 60 * 60 * 1000));
    //document.cookie = 'laravel_session=;path=/;expires=' + expires.toUTCString();
});
*/



