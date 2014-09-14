/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	//资产报修
	assetRepair_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetRepair", rosten.webPath + "/assetRepair/assetRepairAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	assetRepair_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/assetRepair/assetRepairDelete", content,rosten.deleteCallback);
		};
	};
	
	assetRepair_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:assetRepair_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	assetRepair_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetRepair", rosten.webPath + "/assetRepair/assetRepairShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	/*
	 * 此功能默认必须存在
	 */
	show_assetMaintainNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		switch (oString) {
			case "assetRepair":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/assetRepair/assetRepairView?userId=" + userid,
	                gridSrc : rosten.webPath + "/assetRepair/assetRepairGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			
			
		}
		
	}
	connect.connect("show_naviEntity", show_assetMaintainNaviEntity);
});