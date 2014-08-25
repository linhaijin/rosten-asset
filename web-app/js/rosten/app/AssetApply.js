/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	//机动车登记
	apply_add = function(){
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("apply", rosten.webPath + "/demo/applyAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	apply_delete = function(){
		
	};
	apply_create = function(){
		rosten.kernel.createRostenShowDialog(rosten.webPath + "/demo/createApply", {
            onLoadFunction : function() {

            }
        });
	};
	apply_agrain = function(){
		var unids = rosten.getGridUnid("multi");
		if (unids == "")
			return;
		var content = {status:"已审批"};
		content.id = unids;
		rosten.read(rosten.webPath + "/demo/applySubmit", content,rosten.submitCallback);
	};
	
	apply_submit = function(){
		var _1 = rosten.confirm("确认提交申请，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {status:"审批中"};
			content.id = unids;
			rosten.read(rosten.webPath + "/demo/applySubmit", content,rosten.submitCallback);
		};
	};
	
	apply_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:apply_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	apply_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("apply", rosten.webPath + "/demo/applyShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	
	/*
	 * 此功能默认必须存在
	 */
	show_applyNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		switch (oString) {
			case "zcsq":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/demo/applyView?userId=" + userid,
	                gridSrc : rosten.webPath + "/demo/applyGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	            break;
			case "sqsh":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/demo/applyView1?userId=" + userid,
	                gridSrc : rosten.webPath + "/demo/applyGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	            break;
		}
		
	}
	connect.connect("show_naviEntity", show_applyNaviEntity);
});
