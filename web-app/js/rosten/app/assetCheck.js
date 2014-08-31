/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	
	startPd = function(){
		rosten.openNewWindow("startPd", rosten.webPath + "/demo/startPd");	
	};
	endPd = function(){
		rosten.kernel.setHref(rosten.webPath + "/demo/endPd", "endPd");
	};
	addRw = function(){
		rosten.kernel.createRostenShowDialog(rosten.webPath + "/demo/addRw", {
            onLoadFunction : function() {

            }
        });
	};
	
	
		
	/*
	 * 此功能默认必须存在
	 */
	show_assetCheckNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		switch (oString) {
			case "assetRwfb":
				rosten.kernel.setHref(rosten.webPath + "/demo/fbrw", oString);
	            break;
			case "myPdrw":
				rosten.kernel.setHref(rosten.webPath + "/demo/myPdrw", oString);
	            break;
			
		}
		
	}
	connect.connect("show_naviEntity", show_assetCheckNaviEntity);
});