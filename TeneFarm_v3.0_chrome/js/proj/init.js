var TENEFARM;

var proj_base = function() {
	var self = this;

	this.init = function(info) {

		setswipe();
		

	    let params = {
			usr_id : info.usr_id,
			formURL : "getfarminfo"
	    };
    	
    	//console.log(params);
    	
    	blight.read_get(params,	function(result) {
    		//console.log(result.data[0]);
			let usr_info = {
					usr_id:info.usr_id,
					usr_ni:result.data[0].nam
			};
			//console.log(result.data);
			TENEFARM = new ThreeDSmartFarm(usr_info);
			//console.log(result.data);
			TENEFARM.init(result.data);
      	});
    	return;
	};
	
	let setswipe = function() {
		var swiper = new Swiper('.swiper-container', {
	        pagination: '.swiper-pagination',
	        paginationClickable: true,
	        nextButton: '.swiper-button-next',
	        prevButton: '.swiper-button-prev',
	        //noSwiping:true,
	        
	        onSlideChangeStart : function (swiper) {
	        	var index = swiper.activeIndex;
	        	console.log(index);
	        	switch (index) { 
	  	    		case 0:
	  	            break;
	  	    		case 1:
	  	            break;
	  	    		case 2:
	  	  	            break;
	  	    		case 3:
	  	  	            break;
	  	  	        case 4:
	  	  	            break;
	  	  	        case 5:
	  	  	            break;
	  	    		
  	        	}
  	      	},
  	      paginationBulletRender: function (index, className) {
  	      	var title = "Device";
	        switch (index) {
    		    case 0:
    			    title = "온실환경";
    			    break;
    		    case 1:
    			    title = "제어";
    			    break;
    		    case 2:
    			    title = "카메라";
    			    break;
    		    case 3:
    			    title = "누적자료";
    			    break;
    			case 4:
    			    title = "생육단계";
    			    break;
    			case 5:
    			    title = "MSP";
    			    break;
    		    
        	}
	        	
        	var temptag = $('<div>');
        	var outtag = $('<div>');
        	outtag.css("width", "14%");
        	
        	outtag.addClass(className);
        	
        	var txt = $('<span>');
        	txt.css("font-size","11px");
        	txt.html(title);
        	//txt.addClass(className);
        	
        	outtag.append(txt);
        	temptag.append(outtag);
        	
        	return temptag.html();
	        }
	 	});
	 	
	 	
	};		
	
	
	
};