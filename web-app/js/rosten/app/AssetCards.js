/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	//车辆资产
	carCards_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("carCards", rosten.webPath + "/carCards/carCardsAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	carCards_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/carCards/carCardsDelete", content,rosten.deleteCallback);
		};
	};
	
	carCards_submit = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var _1 = rosten.confirm("确认提交入库，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			content.companyId = companyId;
			rosten.read(rosten.webPath + "/carCards/carCardsSubmit", content,rosten.submitCallback);
		};
	};
	
	carCards_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:carCards_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	carCards_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("carCards", rosten.webPath + "/carCards/carCardsShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//土地资产
	landCards_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("landCards", rosten.webPath + "/landCards/landCardsAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	
	landCards_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/landCards/landCardsDelete", content,rosten.deleteCallback);
		};
	};
	
	landCards_submit = function(){
		var _1 = rosten.confirm("确认提交入库，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/landCards/landCardsSubmit", content,rosten.submitCallback);
		};
	};
	
	landCards_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:landCards_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	landCards_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("landCards", rosten.webPath + "/landCards/landCardsShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};

	//房屋资产
	houseCards_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("houseCards", rosten.webPath + "/houseCards/houseCardsAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	
	houseCards_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/houseCards/houseCardsDelete", content,rosten.deleteCallback);
		};
	};
	
	houseCards_submit = function(){
		var _1 = rosten.confirm("确认提交入库，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/houseCards/houseCardsSubmit", content,rosten.submitCallback);
		};
	};
	
	houseCards_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:houseCards_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	houseCards_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("houseCards", rosten.webPath + "/houseCards/houseCardsShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//设备资产
	deviceCards_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("deviceCards", rosten.webPath + "/deviceCards/deviceCardsAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	deviceCards_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/deviceCards/deviceCardsDelete", content,rosten.deleteCallback);
		};
	};
	deviceCards_kp = function(){
		rosten.kernel.createRostenShowDialog(rosten.webPath + "/demo/kpshow", {
            onLoadFunction : function() {

            }
        });
	};
	deviceCards_print = function(){
		
	};
	
	deviceCards_import = function(){
		rosten.kernel.createRostenShowDialog(rosten.webPath + "/demo/importDe", {
            onLoadFunction : function() {

            }
        });
	};
	
	deviceCards_export = function(){
		
	};
	
	deviceCards_submit = function(){
		var _1 = rosten.confirm("确认提交审核，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/deviceCards/deviceCardsSubmit", content,rosten.submitCallback);
		};
	};
	deviceCards_agree = function(){
		var unids = rosten.getGridUnid("multi");
		if (unids == "")
			return;
		var content = {};
		content.id = unids;
		rosten.read(rosten.webPath + "/deviceCards/deviceCardsAgree", content,rosten.submitCallback);
	};
	
	deviceCards_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:deviceCards_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	deviceCards_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("deviceCards", rosten.webPath + "/deviceCards/deviceCardsShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//图书资产
	bookCards_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("bookCards", rosten.webPath + "/bookCards/bookCardsAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	bookCards_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/bookCards/bookCardsDelete", content,rosten.deleteCallback);
		};
	};
	
	bookCards_submit = function(){
		var _1 = rosten.confirm("确认提交入库，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/bookCards/bookCardsSubmit", content,rosten.submitCallback);
		};
	};
	
	bookCards_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:bookCards_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	bookCards_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("bookCards", rosten.webPath + "/bookCards/bookCardsShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//家具资产
	furnitureCards_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("furnitureCards", rosten.webPath + "/furnitureCards/furnitureCardsAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	furnitureCards_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/furnitureCards/furnitureCardsDelete", content,rosten.deleteCallback);
		};
	};
	
	furnitureCards_submit = function(){
		var _1 = rosten.confirm("确认提交入库，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/furnitureCards/furnitureCardsSubmit", content,rosten.submitCallback);
		};
	};
	
	furnitureCards_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:furnitureCards_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	furnitureCards_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("furnitureCards", rosten.webPath + "/furnitureCards/furnitureCardsShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	/*
	 * 此功能默认必须存在
	 */
	show_assetCardsNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		switch (oString) {
			case "carCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/carCards/carCardsView?userId=" + userid,
	                gridSrc : rosten.webPath + "/carCards/carCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "landCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/landCards/landCardsView?userId=" + userid,
	                gridSrc : rosten.webPath + "/landCards/landCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "houseCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/houseCards/houseCardsView?userId=" + userid,
	                gridSrc : rosten.webPath + "/houseCards/houseCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "deviceCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/deviceCards/deviceCardsView?userId=" + userid,
	                gridSrc : rosten.webPath + "/deviceCards/deviceCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "bookCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/bookCards/bookCardsView?userId=" + userid,
	                gridSrc : rosten.webPath + "/bookCards/bookCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "furnitureCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/furnitureCards/furnitureCardsView?userId=" + userid,
	                gridSrc : rosten.webPath + "/furnitureCards/furnitureCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
		}
		
	}
	connect.connect("show_naviEntity", show_assetCardsNaviEntity);
});
