//self.font;
//self.matTxt = new THREE.MeshBasicMaterial( { color: 0xffffff } );

/*
self.onmessage = function( e ) {
    //console.log( 'Worker가 받은 메시지 ', e.data );
    console.log(e.data);

    if (e.data.cmd == 0) {
    	let loader = new THREE.FontLoader();
		loader.load(
			//'./fonts/DoHyeon_Regular.json',
			'./fonts/helvetiker_bold.typeface.json',

			// onLoad callback
			function ( infont ) {
				self.font = infont;
				
			}
		);
    	
    } else if (e.data.cmd == 1) {
    	//console.log(self.font);
    	self.matTxt.color = e.data.color;
    	//console.log(self.matTxt.color);

    	let textGeoFront = new THREE.TextGeometry( e.data.txt, {
			font: self.font ,
			size: e.data.size,
			height: e.data.height
		});
		
		textGeoFront = new THREE.BufferGeometry().fromGeometry( textGeoFront );
		
		let meshTxt = new THREE.Mesh( textGeoFront, self.matTxt );

		//meshTxt.position.x = posX; //-(txt.length/2)*size;
		//meshTxt.position.y = posY;
		meshTxt.name = "value";
		//e.data.pp.push( meshTxt );
    }
   // console.log(font);


   let newFont = new THREE.Font(e.data.font.data);
    console.log(newFont);

    let textGeoFront = new THREE.TextGeometry( e.data.txt, {
			font: newFont,
			size: e.data.size,
			height: e.data.height
		});
		
		textGeoFront = new THREE.BufferGeometry().fromGeometry( textGeoFront );
	
		//let meshTxt = new THREE.Mesh( textGeoFront, e.data.matTxt );

		//meshTxt.position.x = posX; //-(txt.length/2)*size;
		//meshTxt.position.y = posY;
		//meshTxt.name = "value";
		//e.data.pp.push( meshTxt );
		

    // 1초 후에 호출한 페이지에 데이터를 보낸다.
   // setTimeout( function() {
    //    postMessage( 'Worker Value' );
   // }, 1000 );

   //return e;
};
*/

//var CACHE_NAME = 'my-site-cache-v1';
//var urlsToCache = [
  //'/',
 // '/styles/main.css',
 // '/js/tene/sensor.js'
//];

/*
self.addEventListener('install', function(event) {
  // Perform install steps
  console.log('start');
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(function(cache) {
        console.log('Opened cache');
        return cache.addAll(urlsToCache);
      })
  );
});
*/


if ('serviceWorker' in navigator) {
  self.addEventListener('load', function() {
    navigator.serviceWorker.register('./js/tene/sensor_worker.js').then(function(registration) {
      // Registration was successful
     // console.log(registration);
     // console.log('ServiceWorker registration successful with scope: ', registration.scope);
    }, function(err) {
      // registration failed :(
      console.log('ServiceWorker registration failed: ', err);
    });
  });
  self.addEventListener('message', function(e) {
  	//console.log(e.data);
  	//console.log(sensornode);

  	if (e.data.cmd == 1) {
  		let textGeoFront = new THREE.TextGeometry( e.data.txt, {
			font: TENEFARM.engfont ,
			size: e.data.size,
			height: e.data.height
		});
		
		textGeoFront = new THREE.BufferGeometry().fromGeometry( textGeoFront );
		
		let meshTxt = new THREE.Mesh( textGeoFront, self.matTxt );

		//meshTxt.position.x = posX; //-(txt.length/2)*size;
		//meshTxt.position.y = posY;
		meshTxt.name = "value";
		sensornode.add( meshTxt );
	}
  	
  //	e.data.ret.add(100);
  	//return 100;
  });
}



//self.postMessage('sssss');