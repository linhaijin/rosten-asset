/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	//资产报损
	assetScrap_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetScrap", rosten.webPath + "/assetScrap/assetScrapAdd?companyId=" + companyId + "&userid=" + userid + "&flowCode=assetScrap");
//      rosten.kernel.setHref(rosten.webPath + "/assetScrap/assetScrapAdd?companyId=" + companyId+ "&userid=" + userid, "test");
	};

	assetScrap_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		 /*
		 var content = {};
			
			var username = registry.byId("s_username");
			if(username.get("value")!=""){
				content.username = username.get("value");
			}
			
			var chinaName = registry.byId("s_chinaName");
			if(chinaName.get("value")!=""){
				content.chinaName = chinaName.get("value");
			}
			
			var departName = registry.byId("s_departName");
			if(departName.get("value")!=""){
				content.departName = departName.get("value");
			}
		 */
		rosten.openNewWindow("assetScrap", rosten.webPath + "/assetScrap/assetScrapExport?companyId="+companyId);
	};
	
	assetScrap_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/assetScrap/assetScrapDelete", content,rosten.deleteCallback);
		};
	};
	
	assetScrap_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:assetScrap_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	assetScrap_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetScrap", rosten.webPath + "/assetScrap/assetScrapShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
//	资产调拨
	assetAllocate_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetAllocate", rosten.webPath + "/assetAllocate/assetAllocateAdd?companyId=" + companyId + "&userid=" + userid + "&flowCode=assetAllocate");
	};
	
	assetAllocate_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetAllocate", rosten.webPath + "/assetAllocate/assetAllocateExport?companyId="+companyId);
	};
	
	assetAllocate_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/assetAllocate/assetAllocateDelete", content,rosten.deleteCallback);
		};
	};
	
	assetAllocate_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:assetAllocate_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	assetAllocate_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetAllocate", rosten.webPath + "/assetAllocate/assetAllocateShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//资产报失
	assetLose_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetLose", rosten.webPath + "/assetLose/assetLoseAdd?companyId=" + companyId + "&userid=" + userid + "&flowCode=assetLose");
	};
	
	assetLose_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetLose", rosten.webPath + "/assetLose/assetLoseExport?companyId="+companyId);
	};
	
	assetLose_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/assetLose/assetLoseDelete", content,rosten.deleteCallback);
		};
	};
	
	assetLose_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:assetLose_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	assetLose_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetLose", rosten.webPath + "/assetLose/assetLoseShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//资产报修
	assetRepair_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetRepair", rosten.webPath + "/assetRepair/assetRepairAdd?companyId=" + companyId + "&userid=" + userid + "&flowCode=assetRepair");
	};
	
	assetRepair_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetRepair", rosten.webPath + "/assetRepair/assetRepairExport?companyId="+companyId);
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
	
	//增值减值
	/**暂不作要求
	assetAddDelete_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetAddDelete", rosten.webPath + "/assetAddDelete/assetAddDeleteAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	assetAddDelete_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/assetAddDelete/assetAddDeleteDelete", content,rosten.deleteCallback);
		};
	};
	
	assetAddDelete_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:assetAddDelete_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	assetAddDelete_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetAddDelete", rosten.webPath + "/assetAddDelete/assetAddDeleteShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	**/
	/*
	 * 此功能默认必须存在
	 */
	show_assetChangeNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		switch (oString) {
			case "assetScrap":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/assetScrap/assetScrapView?userId=" + userid,
	                gridSrc : rosten.webPath + "/assetScrap/assetScrapGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "assetAllocate":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/assetAllocate/assetAllocateView?userId=" + userid,
	                gridSrc : rosten.webPath + "/assetAllocate/assetAllocateGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "assetLose":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/assetLose/assetLoseView?userId=" + userid,
	                gridSrc : rosten.webPath + "/assetLose/assetLoseGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "assetRepair":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/assetRepair/assetRepairView?userId=" + userid,
	                gridSrc : rosten.webPath + "/assetRepair/assetRepairGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
//			case "assetAddDelete":
//	            var naviJson = {
//	                identifier : oString,
//	                actionBarSrc : rosten.webPath + "/assetAddDelete/assetAddDeleteView?userId=" + userid,
//	                gridSrc : rosten.webPath + "/assetAddDelete/assetAddDeleteGrid?companyId=" + companyId
//	            };
//	            rosten.kernel.addRightContent(naviJson);
//	
//	            var rostenGrid = rosten.kernel.getGrid();
//	            break;
			
		}
	}
	connect.connect("show_naviEntity", show_assetChangeNaviEntity);
});
