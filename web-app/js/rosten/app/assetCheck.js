/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
		
	/*
	 * 此功能默认必须存在
	 */
	show_assetCheckNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		switch (oString) {
			case "assetInventory":
	            
	            break;
			
			
		}
		
	}
	connect.connect("show_naviEntity", show_assetMaintainNaviEntity);
});