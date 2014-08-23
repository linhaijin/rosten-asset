/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	//机动车登记
	carRegister_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("carRegister", rosten.webPath + "/car/carRegisterAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	
	carRegister_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/car/carRegisterDelete", content,rosten.deleteCallback);
		};
	};
	
	carRegister_submit = function(){
		var _1 = rosten.confirm("确认提交入库，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/car/carRegisterSubmit", content,rosten.submitCallback);
		};
	};
	
	carRegister_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:carRegister_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	carRegister_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("carRegister", rosten.webPath + "/car/carRegisterShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//土地登记
	landRegister_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("landRegister", rosten.webPath + "/land/landRegisterAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	
	landRegister_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/land/landRegisterDelete", content,rosten.deleteCallback);
		};
	};
	
	landRegister_submit = function(){
		var _1 = rosten.confirm("确认提交入库，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/land/landRegisterSubmit", content,rosten.submitCallback);
		};
	};
	
	landRegister_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:landRegister_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	landRegister_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("landRegister", rosten.webPath + "/land/landRegisterShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};

	//房屋登记
	houseRegister_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("houseRegister", rosten.webPath + "/house/houseRegisterAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	
	houseRegister_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/house/houseRegisterDelete", content,rosten.deleteCallback);
		};
	};
	
	houseRegister_submit = function(){
		var _1 = rosten.confirm("确认提交入库，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/house/houseRegisterSubmit", content,rosten.submitCallback);
		};
	};
	
	houseRegister_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:houseRegister_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	houseRegister_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("houseRegister", rosten.webPath + "/house/houseRegisterShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//设备登记
	deviceRegister_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("deviceRegister", rosten.webPath + "/device/deviceRegisterAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	deviceRegister_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/device/deviceRegisterDelete", content,rosten.deleteCallback);
		};
	};
	deviceRegister_kp = function(){
		rosten.kernel.createRostenShowDialog(rosten.webPath + "/demo/kpshow", {
            onLoadFunction : function() {

            }
        });
	};
	deviceRegister_print = function(){
		
	};
	
	deviceRegister_import = function(){
		rosten.kernel.createRostenShowDialog(rosten.webPath + "/demo/importDe", {
            onLoadFunction : function() {

            }
        });
	};
	
	deviceRegister_export = function(){
		
	};
	
	deviceRegister_submit = function(){
		var _1 = rosten.confirm("确认提交审核，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/device/deviceRegisterSubmit", content,rosten.submitCallback);
		};
	};
	deviceRegister_agree = function(){
		var unids = rosten.getGridUnid("multi");
		if (unids == "")
			return;
		var content = {};
		content.id = unids;
		rosten.read(rosten.webPath + "/device/deviceRegisterAgree", content,rosten.submitCallback);
	};
	
	deviceRegister_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:deviceRegister_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	deviceRegister_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("deviceRegister", rosten.webPath + "/device/deviceRegisterShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//图书登记
	bookRegister_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("bookRegister", rosten.webPath + "/book/bookRegisterAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	bookRegister_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/book/bookRegisterDelete", content,rosten.deleteCallback);
		};
	};
	
	bookRegister_submit = function(){
		var _1 = rosten.confirm("确认提交入库，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/book/bookRegisterSubmit", content,rosten.submitCallback);
		};
	};
	
	bookRegister_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:bookRegister_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	bookRegister_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("bookRegister", rosten.webPath + "/book/bookRegisterShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	//家具登记
	furnitureRegister_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("furnitureRegister", rosten.webPath + "/furniture/furnitureRegisterAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	furnitureRegister_delete = function(){
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/furniture/furnitureRegisterDelete", content,rosten.deleteCallback);
		};
	};
	
	furnitureRegister_submit = function(){
		var _1 = rosten.confirm("确认提交入库，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {};
			content.id = unids;
			rosten.read(rosten.webPath + "/furniture/furnitureRegisterSubmit", content,rosten.submitCallback);
		};
	};
	
	furnitureRegister_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:furnitureRegister_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	furnitureRegister_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("furnitureRegister", rosten.webPath + "/furniture/furnitureRegisterShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	/*
	 * 此功能默认必须存在
	 */
	show_bookKeepingNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		switch (oString) {
			case "carRegister":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/car/carRegisterView?userId=" + userid,
	                gridSrc : rosten.webPath + "/car/carRegisterGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "landRegister":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/land/landRegisterView?userId=" + userid,
	                gridSrc : rosten.webPath + "/land/landRegisterGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "houseRegister":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/house/houseRegisterView?userId=" + userid,
	                gridSrc : rosten.webPath + "/house/houseRegisterGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "deviceRegister":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/device/deviceRegisterView?userId=" + userid,
	                gridSrc : rosten.webPath + "/device/deviceRegisterGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "bookRegister":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/book/bookRegisterView?userId=" + userid,
	                gridSrc : rosten.webPath + "/book/bookRegisterGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "furnitureRegister":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/furniture/furnitureRegisterView?userId=" + userid,
	                gridSrc : rosten.webPath + "/furniture/furnitureRegisterGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
		}
		
	}
	connect.connect("show_naviEntity", show_bookKeepingNaviEntity);
});
