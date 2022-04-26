/**
 * Listens for the app launching, then creates the window.
 *
 * @see http://developer.chrome.com/apps/app.runtime.html
 * @see http://developer.chrome.com/apps/app.window.html
 */

chrome.runtime.onSuspend.addListener(function() {
  chrome.sockets.tcp.getSockets(function(socketInfos) {
    let cnt = socketInfos.length;
//console.log("cnt",cnt);
    let arrayBuffer = new ArrayBuffer(1);
    let buffer = new Uint8Array(arrayBuffer);
    buffer[0] = 24; //HeaderDefine_COMMON.C_WEB_CLIENT_CLOSE;

    for (let index=0;index<cnt;index++) {
      chrome.sockets.tcp.send(socketInfos[index].socketId, arrayBuffer, function() {});
     // chrome.sockets.tcp.close(socketInfos[index].socketId, function() {});
    }

    for (let index=0;index<cnt;index++) {
      chrome.sockets.tcp.close(socketInfos[index].socketId, function() {});
    }
    /*
    $.each(socketInfos,function(index,socketInfo) {
       chrome.sockets.tcp.close(socketInfo.socketId, function() {});
    });
    */
  });
});

chrome.runtime.onSuspendCanceled.addListener(function() {
  chrome.sockets.tcp.getSockets(function(socketInfos) {
    let cnt = socketInfos.length;

   // console.log("cnt",cnt);

    let arrayBuffer = new ArrayBuffer(1);
    let buffer = new Uint8Array(arrayBuffer);
    buffer[0] = HeaderDefine_COMMON.C_WEB_CLIENT_CLOSE;

    for (let index=0;index<cnt;index++) {
      chrome.sockets.tcp.send(socketInfos[index].socketId, arrayBuffer, function() {});
     // chrome.sockets.tcp.close(socketInfos[index].socketId, function() {});
    }

    for (let index=0;index<cnt;index++) {
      chrome.sockets.tcp.close(socketInfos[index].socketId, function() {});
    }
    /*
    $.each(socketInfos,function(index,socketInfo) {
       chrome.sockets.tcp.close(socketInfo.socketId, function() {});
    });
    */
  });
});



chrome.app.runtime.onLaunched.addListener(function(launchData) {
  //console.log(launchData);
  chrome.app.window.create(
    'index.html',
    {
      id: 'mainWindow',
      //id: 'main',
      bounds: {width: 600, height: 1024},
      resizable:true,
      //resizable:false,
      //frame:'none',
      //alwaysOnTop:true,
      //state:'fullscreen'
    },
    function(win) {
     console.log(win);
     
    }
  );
  
    
});
