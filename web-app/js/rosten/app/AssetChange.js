/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
//	报废报损
	assetScrap_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetScrap", rosten.webPath + "/assetScrap/assetScrapAdd?companyId=" + companyId + "&userid=" + userid);
        
//        rosten.kernel.setHref(rosten.webPath + "/assetScrap/assetScrapAdd?companyId=" + companyId+ "&userid=" + userid, "test");
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
        rosten.openNewWindow("assetAllocate", rosten.webPath + "/assetAllocate/assetAllocateAdd?companyId=" + companyId + "&userid=" + userid);
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
	
//	增值减值
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
			case "assetAddDelete":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/assetAddDelete/assetAddDeleteView?userId=" + userid,
	                gridSrc : rosten.webPath + "/assetAddDelete/assetAddDeleteGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			
			
		}
		
	}
	connect.connect("show_naviEntity", show_assetChangeNaviEntity);
});
