var tenefarm;
function init() {
	
	let container = document.getElementById( 'container' );
	
	let usr_info = {
			id:"tene",
			nam:"테네"
	};
	
	tenefarm = new tenefarm_base(container,usr_info);
	tenefarm.init();

	
	
}