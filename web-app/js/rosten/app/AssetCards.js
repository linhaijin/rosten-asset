/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	
	//2015-1-15-------------增加修改卡片状态功能
	asset_changeStatus = function(){
		var unids = rosten.getGridUnid("multi");
        if (unids == "")
            return;
        
        var id = "sys_statusSelectDialog";
        var initValue = [];
        
		rosten.selectDialog("状态选择", id, rosten.webPath + "/cardsShare/statusSelect", false, initValue);
        rosten[id].callback = function(data) {
        	if(data.length>0){
        		var content = {status:data[0].name};
                rosten.readNoTime(rosten.webPath + "/cardsShare/changeCardsStatus/" + unids, content, rosten.submitCallback);
        	}else{
        		rosten.alert("请正确选择状态");
        	}
       	 	
        };
	}
	
	//2014-12-08 增加条形码打印功能--------------------------------------------------------------
	
	//------------------------------------------------------------------------------------
	
	//运输工具
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
	
	carCards_search = function(){
		var content = {};
		
		var registerNum = registry.byId("car_registerNum");
		if(registerNum.get("value")!=""){
			content.registerNum = registerNum.get("value");
		}
		
		var category = registry.byId("car_category");
		if(category.get("value")!=""){
			content.category = category.get("value");
		}
		
		var assetName = registry.byId("car_assetName");
		if(assetName.get("value")!=""){
			content.assetName = assetName.get("value");
		}
		
		var userDepart = registry.byId("car_userDepartIds");
		if(userDepart.get("value")!=""){
			content.userDepart = userDepart.get("value");
		}

		var assetStatus = registry.byId("car_assetStatus");
		if(assetStatus.get("value")!=""){
			content.assetStatus = assetStatus.get("value");
		}
		
		switch(rosten.kernel.navigationEntity) {
		default:
			rosten.kernel.refreshGrid(rosten.kernel.getGrid().defaultUrl, content);
			break;
		}
	};
	
	carCards_resetSearch = function(){
		switch(rosten.kernel.navigationEntity) {
		default:
			registry.byId("car_registerNum").set("value","");
			registry.byId("car_category").set("value","");
			registry.byId("car_assetName").set("value","");
			registry.byId("car_userDepartIds").set("value","");
			registry.byId("car_userDepart").set("value","");
			registry.byId("car_assetStatus").set("value","");
			
			rosten.kernel.refreshGrid();
			break;
		}	
	};
	
	carCards_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var qregisterNum = "";
		var qcategory = "";
		var qassetName = "";
		var quserDepart = "";
		var qassetStatus = "";
		
		var registerNum = registry.byId("car_registerNum");
		if(registerNum.get("value")!=""){
			registerNum = registerNum.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category = registry.byId("car_category");
		if(category.get("value")!=""){
			category = category.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName = registry.byId("car_assetName");
		if(assetName.get("value")!=""){
			assetName = assetName.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart = registry.byId("car_userDepartIds");
		if(userDepart.get("value")!=""){
			userDepart = userDepart.get("value");
			quserDepart = "&userDepart="+userDepart;
		}
		
		var assetStatus = registry.byId("car_assetStatus");
		if(assetStatus.get("value")!=""){
			assetStatus = assetStatus.get("value");
			qassetStatus = "&assetStatus="+assetStatus;
		}
		
		rosten.openNewWindow("carCards", rosten.webPath + "/carCards/carCardsExport?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart+qassetStatus);
	};
	
	carCards_printTxm = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var qregisterNum = "";
		var qcategory = "";
		var qassetName = "";
		var quserDepart = "";
		var qassetStatus = "";
		
		var registerNum = registry.byId("car_registerNum");
		if(registerNum.get("value")!=""){
			registerNum = registerNum.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category = registry.byId("car_category");
		if(category.get("value")!=""){
			category = category.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName = registry.byId("car_assetName");
		if(assetName.get("value")!=""){
			assetName = assetName.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart = registry.byId("car_userDepartIds");
		if(userDepart.get("value")!=""){
			userDepart = userDepart.get("value");
			quserDepart = "&userDepart="+userDepart;
		}
		
		var assetStatus = registry.byId("car_assetStatus");
		if(assetStatus.get("value")!=""){
			assetStatus = assetStatus.get("value");
			qassetStatus = "&assetStatus="+assetStatus;
		}
		
		rosten.openNewWindow("carCardsPrintTxm", rosten.webPath + "/carCards/carCardsPrintTxm?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart+qassetStatus);
	}
	
	//土地
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
	
	landCards_search = function(){
		var content = {};
		
		var registerNum = registry.byId("land_registerNum");
		if(registerNum.get("value")!=""){
			content.registerNum = registerNum.get("value");
		}
		
		var category = registry.byId("land_category");
		if(category.get("value")!=""){
			content.category = category.get("value");
		}
		
		var assetName = registry.byId("land_assetName");
		if(assetName.get("value")!=""){
			content.assetName = assetName.get("value");
		}
		
		var userDepart = registry.byId("land_userDepartIds");
		if(userDepart.get("value")!=""){
			content.userDepart = userDepart.get("value");
		}
		
		switch(rosten.kernel.navigationEntity) {
		default:
			rosten.kernel.refreshGrid(rosten.kernel.getGrid().defaultUrl, content);
			break;
		}
	};
	
	landCards_resetSearch = function(){
		switch(rosten.kernel.navigationEntity) {
		default:
			registry.byId("land_registerNum").set("value","");
			registry.byId("land_category").set("value","");
			registry.byId("land_assetName").set("value","");
			registry.byId("land_userDepartIds").set("value","");
			registry.byId("land_userDepart").set("value","");
			rosten.kernel.refreshGrid();
			break;
		}	
	};
	
	landCards_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var qregisterNum = "";
		var qcategory = "";
		var qassetName = "";
		var quserDepart = "";
		
		var registerNum = registry.byId("land_registerNum");
		if(registerNum.get("value")!=""){
			registerNum = registerNum.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category = registry.byId("land_category");
		if(category.get("value")!=""){
			category = category.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName = registry.byId("land_assetName");
		if(assetName.get("value")!=""){
			assetName = assetName.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart = registry.byId("land_userDepartIds");
		if(userDepart.get("value")!=""){
			userDepart = userDepart.get("value");
			quserDepart = "&userDepart="+userDepart;
		}
		
		rosten.openNewWindow("landCards", rosten.webPath + "/landCards/landCardsExport?companyId="+companyId+qregisterNum+qregisterNum+qcategory+qassetName+quserDepart);
	};

	//房屋及建筑物
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
	
	houseCards_search = function(){
		var content = {};
		
		var registerNum = registry.byId("house_registerNum");
		if(registerNum.get("value")!=""){
			content.registerNum = registerNum.get("value");
		}
		
		var category = registry.byId("house_category");
		if(category.get("value")!=""){
			content.category = category.get("value");
		}
		
		var assetName = registry.byId("house_assetName");
		if(assetName.get("value")!=""){
			content.assetName = assetName.get("value");
		}
		
		var userDepart = registry.byId("house_userDepartIds");
		if(userDepart.get("value")!=""){
			content.userDepart = userDepart.get("value");
		}
		
		var assetStatus = registry.byId("house_assetStatus");
		if(assetStatus.get("value")!=""){
			content.assetStatus = assetStatus.get("value");
		}
		
		switch(rosten.kernel.navigationEntity) {
		default:
			rosten.kernel.refreshGrid(rosten.kernel.getGrid().defaultUrl, content);
			break;
		}
	};
	
	houseCards_resetSearch = function(){
		switch(rosten.kernel.navigationEntity) {
		default:
			registry.byId("house_registerNum").set("value","");
			registry.byId("house_category").set("value","");
			registry.byId("house_assetName").set("value","");
			registry.byId("house_userDepartIds").set("value","");
			registry.byId("house_userDepart").set("value","");
			registry.byId("house_assetStatus").set("value","");
			rosten.kernel.refreshGrid();
			break;
		}	
	};
	
	houseCards_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var qregisterNum = "";
		var qcategory = "";
		var qassetName = "";
		var quserDepart = "";
		var qassetStatus = "";
		
		var registerNum = registry.byId("house_registerNum");
		if(registerNum.get("value")!=""){
			registerNum = registerNum.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category = registry.byId("house_category");
		if(category.get("value")!=""){
			category = category.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName = registry.byId("house_assetName");
		if(assetName.get("value")!=""){
			assetName = assetName.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart = registry.byId("house_userDepartIds");
		if(userDepart.get("value")!=""){
			userDepart = userDepart.get("value");
			quserDepart = "&userDepart="+userDepart;
		}

		var assetStatus = registry.byId("house_assetStatus");
		if(assetStatus.get("value")!=""){
			assetStatus = assetStatus.get("value");
			qassetStatus = "&assetStatus="+assetStatus;
		}
		
		rosten.openNewWindow("houseCards", rosten.webPath + "/houseCards/houseCardsExport?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart+qassetStatus);
	};
	
	houseCards_printTxm = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var qregisterNum = "";
		var qcategory = "";
		var qassetName = "";
		var quserDepart = "";
		var qassetStatus = "";
		
		var registerNum = registry.byId("house_registerNum");
		if(registerNum.get("value")!=""){
			registerNum = registerNum.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category = registry.byId("house_category");
		if(category.get("value")!=""){
			category = category.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName = registry.byId("house_assetName");
		if(assetName.get("value")!=""){
			assetName = assetName.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart = registry.byId("house_userDepartIds");
		if(userDepart.get("value")!=""){
			userDepart = userDepart.get("value");
			quserDepart = "&userDepart="+userDepart;
		}
		
		var assetStatus = registry.byId("house_assetStatus");
		if(assetStatus.get("value")!=""){
			assetStatus = assetStatus.get("value");
			qassetStatus = "&assetStatus="+assetStatus;
		}
		
		rosten.openNewWindow("houseCardsPrintTxm", rosten.webPath + "/houseCards/houseCardsPrintTxm?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart+qassetStatus);
	}
	
	//电子设备
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
	
	deviceCards_search = function(){
		var content = {};
		
		var registerNum = registry.byId("device_registerNum");
		if(registerNum.get("value")!=""){
			content.registerNum = registerNum.get("value");
		}
		
		var category = registry.byId("device_category");
		if(category.get("value")!=""){
			content.category = category.get("value");
		}
		
		var assetName = registry.byId("device_assetName");
		if(assetName.get("value")!=""){
			content.assetName = assetName.get("value");
		}
		
		var userDepart = registry.byId("device_userDepartIds");
		if(userDepart.get("value")!=""){
			content.userDepart = userDepart.get("value");
		}
		
		var assetStatus = registry.byId("device_assetStatus");
		if(assetStatus.get("value")!=""){
			content.assetStatus = assetStatus.get("value");
		}
		
		switch(rosten.kernel.navigationEntity) {
		default:
			rosten.kernel.refreshGrid(rosten.kernel.getGrid().defaultUrl, content);
			break;
		}
	};
	
	deviceCards_resetSearch = function(){
		switch(rosten.kernel.navigationEntity) {
		default:
			registry.byId("device_registerNum").set("value","");
			registry.byId("device_category").set("value","");
			registry.byId("device_assetName").set("value","");
			registry.byId("device_userDepartIds").set("value","");
			registry.byId("device_userDepart").set("value","");
			registry.byId("device_assetStatus").set("value","");
			rosten.kernel.refreshGrid();
			break;
		}	
	};
	
	deviceCards_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var qregisterNum = "";
		var qcategory = "";
		var qassetName = "";
		var quserDepart = "";
		var qassetStatus = "";
		
		var registerNum = registry.byId("device_registerNum");
		if(registerNum.get("value")!=""){
			registerNum = registerNum.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category = registry.byId("device_category");
		if(category.get("value")!=""){
			category = category.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName = registry.byId("device_assetName");
		if(assetName.get("value")!=""){
			assetName = assetName.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart = registry.byId("device_userDepartIds");
		if(userDepart.get("value")!=""){
			userDepart = userDepart.get("value");
			quserDepart = "&userDepart="+userDepart;
		}
		
		var assetStatus = registry.byId("device_assetStatus");
		if(assetStatus.get("value")!=""){
			assetStatus = assetStatus.get("value");
			qassetStatus = "&assetStatus="+assetStatus;
		}
		
		rosten.openNewWindow("deviceCards", rosten.webPath + "/deviceCards/deviceCardsExport?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart+qassetStatus);
	};
	
	deviceCards_printTxm = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var qregisterNum = "";
		var qcategory = "";
		var qassetName = "";
		var quserDepart = "";
		var qassetStatus = "";
		
		var registerNum = registry.byId("device_registerNum");
		if(registerNum.get("value")!=""){
			registerNum = registerNum.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category = registry.byId("device_category");
		if(category.get("value")!=""){
			category = category.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName = registry.byId("device_assetName");
		if(assetName.get("value")!=""){
			assetName = assetName.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart = registry.byId("device_userDepartIds");
		if(userDepart.get("value")!=""){
			userDepart = userDepart.get("value");
			quserDepart = "&userDepart="+userDepart;
		}
		
		var assetStatus = registry.byId("device_assetStatus");
		if(assetStatus.get("value")!=""){
			assetStatus = assetStatus.get("value");
			qassetStatus = "&assetStatus="+assetStatus;
		}
		
		rosten.openNewWindow("deviceCardsPrintTxm", rosten.webPath + "/deviceCards/deviceCardsPrintTxm?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart+qassetStatus);
	}
	
	//图书
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
	
	bookCards_search = function(){
		var content = {};
		
		var registerNum = registry.byId("book_registerNum");
		if(registerNum.get("value")!=""){
			content.registerNum = registerNum.get("value");
		}
		
		var category = registry.byId("book_category");
		if(category.get("value")!=""){
			content.category = category.get("value");
		}
		
		var assetName = registry.byId("book_assetName");
		if(assetName.get("value")!=""){
			content.assetName = assetName.get("value");
		}
		
		var userDepart = registry.byId("book_userDepart");
		if(userDepart.get("value")!=""){
			content.userDepart = userDepart.get("value");
		}
		
		switch(rosten.kernel.navigationEntity) {
		default:
			rosten.kernel.refreshGrid(rosten.kernel.getGrid().defaultUrl, content);
			break;
		}
	};
	
	bookCards_resetSearch = function(){
		switch(rosten.kernel.navigationEntity) {
		default:
			registry.byId("book_registerNum").set("value","");
			registry.byId("book_category").set("value","");
			registry.byId("book_assetName").set("value","");
			registry.byId("book_userDepart").set("value","");
			rosten.kernel.refreshGrid();
			break;
		}	
	};
	
	bookCards_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var qregisterNum = "";
		var qcategory = "";
		var qassetName = "";
		var quserDepart = "";
		
		var registerNum = registry.byId("book_registerNum");
		if(registerNum.get("value")!=""){
			registerNum = registerNum.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category = registry.byId("book_category");
		if(category.get("value")!=""){
			category = category.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName = registry.byId("book_assetName");
		if(assetName.get("value")!=""){
			assetName = assetName.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart = registry.byId("book_userDepart");
		if(userDepart.get("value")!=""){
			userDepart = userDepart.get("value");
			quserDepart = "&userDepart="+userDepart;
		}
		
		rosten.openNewWindow("bookCards", rosten.webPath + "/bookCards/bookCardsExport?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart);
	};
	
	//办公家具
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
	
	furnitureCards_search = function(){
		var content = {};
		
		var registerNum = registry.byId("furniture_registerNum");
		if(registerNum.get("value")!=""){
			content.registerNum = registerNum.get("value");
		}
		
		var category = registry.byId("furniture_category");
		if(category.get("value")!=""){
			content.category = category.get("value");
		}
		
		var assetName = registry.byId("furniture_assetName");
		if(assetName.get("value")!=""){
			content.assetName = assetName.get("value");
		}
		
		var userDepart = registry.byId("furniture_userDepartIds");
		if(userDepart.get("value")!=""){
			content.userDepart = userDepart.get("value");
		}
		
		var assetStatus = registry.byId("furniture_assetStatus");
		if(assetStatus.get("value")!=""){
			content.assetStatus = assetStatus.get("value");
		}
		
		switch(rosten.kernel.navigationEntity) {
		default:
			rosten.kernel.refreshGrid(rosten.kernel.getGrid().defaultUrl, content);
			break;
		}
	};
	
	furnitureCards_resetSearch = function(){
		switch(rosten.kernel.navigationEntity) {
		default:
			registry.byId("furniture_registerNum").set("value","");
			registry.byId("furniture_category").set("value","");
			registry.byId("furniture_assetName").set("value","");
			registry.byId("furniture_userDepartIds").set("value","");
			registry.byId("furniture_userDepart").set("value","");
			registry.byId("furniture_assetStatus").set("value","");
			rosten.kernel.refreshGrid();
			break;
		}	
	};
	
	furnitureCards_export = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var qregisterNum = "";
		var qcategory = "";
		var qassetName = "";
		var quserDepart = "";
		var qassetStatus = "";
		
		var registerNum = registry.byId("furniture_registerNum");
		if(registerNum.get("value")!=""){
			registerNum = registerNum.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category = registry.byId("furniture_category");
		if(category.get("value")!=""){
			category = category.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName = registry.byId("furniture_assetName");
		if(assetName.get("value")!=""){
			assetName = assetName.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart = registry.byId("furniture_userDepartIds");
		if(userDepart.get("value")!=""){
			userDepart = userDepart.get("value");
			quserDepart = "&userDepart="+userDepart;
		}
		
		var assetStatus = registry.byId("furniture_assetStatus");
		if(assetStatus.get("value")!=""){
			assetStatus = assetStatus.get("value");
			qassetStatus = "&assetStatus="+assetStatus;
		}
		
		rosten.openNewWindow("furnitureCards", rosten.webPath + "/furnitureCards/furnitureCardsExport?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart+qassetStatus);
	};
	
	furnitureCards_printTxm = function(){
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var qregisterNum = "";
		var qcategory = "";
		var qassetName = "";
		var quserDepart = "";
		var qassetStatus = "";
		
		var registerNum = registry.byId("furniture_registerNum");
		if(registerNum.get("value")!=""){
			registerNum = registerNum.get("value");
			qregisterNum = "&registerNum="+registerNum;
		}
		
		var category = registry.byId("furniture_category");
		if(category.get("value")!=""){
			category = category.get("value");
			qcategory = "&category="+category;
		}
		
		var assetName = registry.byId("furniture_assetName");
		if(assetName.get("value")!=""){
			assetName = assetName.get("value");
			qassetName = "&assetName="+assetName;
		}
		
		var userDepart = registry.byId("furniture_userDepartIds");
		if(userDepart.get("value")!=""){
			userDepart = userDepart.get("value");
			quserDepart = "&userDepart="+userDepart;
		}
		
		var assetStatus = registry.byId("furniture_assetStatus");
		if(assetStatus.get("value")!=""){
			assetStatus = assetStatus.get("value");
			qassetStatus = "&assetStatus="+assetStatus;
		}
		
		rosten.openNewWindow("furnitureCardsPrintTxm", rosten.webPath + "/furnitureCards/furnitureCardsPrintTxm?companyId="+companyId+qregisterNum+qcategory+qassetName+quserDepart+qassetStatus);
	}
	
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
	                searchSrc:rosten.webPath + "/carCards/carCardsSearchView?companyId=" + companyId,
	                gridSrc : rosten.webPath + "/carCards/carCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "landCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/landCards/landCardsView?userId=" + userid,
	                searchSrc:rosten.webPath + "/landCards/landCardsSearchView?companyId=" + companyId,
	                gridSrc : rosten.webPath + "/landCards/landCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "houseCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/houseCards/houseCardsView?userId=" + userid,
	                searchSrc:rosten.webPath + "/houseCards/houseCardsSearchView?companyId=" + companyId,
	                gridSrc : rosten.webPath + "/houseCards/houseCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "deviceCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/deviceCards/deviceCardsView?userId=" + userid,
	                searchSrc:rosten.webPath + "/deviceCards/deviceCardsSearchView?companyId=" + companyId,
	                gridSrc : rosten.webPath + "/deviceCards/deviceCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "bookCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/bookCards/bookCardsView?userId=" + userid,
	                searchSrc:rosten.webPath + "/bookCards/bookCardsSearchView?companyId=" + companyId,
	                gridSrc : rosten.webPath + "/bookCards/bookCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
			case "furnitureCards":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/furnitureCards/furnitureCardsView?userId=" + userid,
	                searchSrc:rosten.webPath + "/furnitureCards/furnitureCardsSearchView?companyId=" + companyId,
	                gridSrc : rosten.webPath + "/furnitureCards/furnitureCardsGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	
	            var rostenGrid = rosten.kernel.getGrid();
	            break;
		}
		
	}
	connect.connect("show_naviEntity", show_assetCardsNaviEntity);
});
