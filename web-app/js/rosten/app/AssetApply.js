/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	//资产申请
	assetApply_add = function(){//新增资产申请
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetApply", rosten.webPath + "/applyManage/assetApplyAdd?companyId=" + companyId + "&userid=" + userid);
	};
	
	assetApply_delete = function(){//删除资产申请
		
	};
	
	assetApply_submit = function(){//提交资产申请
		var _1 = rosten.confirm("确认提交申请，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			if (unids == "")
				return;
			var content = {applyStatus:"审批中"};
			content.id = unids;
			rosten.read(rosten.webPath + "/applyManage/assetApplySubmit", content,rosten.submitCallback);
		};
	};
	
	assetApply_agree = function(){
		var unids = rosten.getGridUnid("multi");
		if (unids == "")
			return;
		var content = {applyStatus:"已审批"};
		content.id = unids;
		rosten.read(rosten.webPath + "/applyManage/assetApplyAgree", content,rosten.submitCallback);
	};
	
	assetCards_create = function(){//生产资产卡片
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var unids = rosten.getGridUnid("multi");
		rosten.kernel.createRostenShowDialog(rosten.webPath + "/applyManage/assetCardsCreate?companyId=" + companyId + "&applyIds=" + unids, {
            onLoadFunction : function() {
            	
            }
        });
	};
	
	assetApply_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:assetApply_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	assetApply_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetApply", rosten.webPath + "/applyManage/assetApplyShow/" + unid + "?userid=" + userid + "&companyId=" + companyId);
		rosten.kernel.getGrid().clearSelected();
	};
	
	/*
	 * 此功能默认必须存在
	 */
	show_assetApplyNaviEntity = function(oString) {
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var userid = rosten.kernel.getUserInforByKey("idnumber");
		switch (oString) {
			case "mineApply":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/applyManage/mineApplyView?userId=" + userid,
	                gridSrc : rosten.webPath + "/applyManage/mineApplyGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	            break;
			case "applyApproval":
	            var naviJson = {
	                identifier : oString,
	                actionBarSrc : rosten.webPath + "/applyManage/applyApprovalView?userId=" + userid,
	                gridSrc : rosten.webPath + "/applyManage/assetApplyGrid?companyId=" + companyId
	            };
	            rosten.kernel.addRightContent(naviJson);
	            break;
		}
		
	}
	connect.connect("show_naviEntity", show_assetApplyNaviEntity);
});
