/**
 * @author ercjlo
 */
define([ "dojo/_base/connect", "dojo/_base/lang","dijit/registry", "dojo/_base/kernel","rosten/kernel/behavior" ], function(
		connect, lang,registry,kernel) {
	//资产申请
	assetApply_add = function(){//新增资产申请
		var userid = rosten.kernel.getUserInforByKey("idnumber");
        var companyId = rosten.kernel.getUserInforByKey("companyid");
        rosten.openNewWindow("assetApply", rosten.webPath + "/applyManage/assetApplyAdd?companyId=" + companyId + "&userid=" + userid + "&flowCode=assetApply");
	};
	
	assetApply_delete = function(){//删除资产申请
		var _1 = rosten.confirm("删除后将无法恢复，是否继续?");
		_1.callback = function() {
			var unids = rosten.getGridUnid("multi");
			var content = {};
			if (unids == ""){
				rosten.alert("注意：请在列表中选择数据！");
				return;
			}else{
				content.id = unids;
			}
			rosten.read(rosten.webPath + "/applyManage/assetApplyDelete", content,function(data){
				if(data.result == true || data.result == "true"){
					rosten.alert("成功：资产申请单已删除！");
					rosten.kernel.refreshGrid();
				}else{
					rosten.alert(data.result);
				}
			});
		};
	};
	
	//提交功能由列表页面改为视图提交，注销代码
//	assetApply_submit = function(){//提交资产申请
//		var _1 = rosten.confirm("确认提交申请，是否继续?");
//		_1.callback = function() {
//			var unids = rosten.getGridUnid("multi");
//			if (unids == "")
//				return;
//			var content = {applyStatus:"审批中"};
//			content.id = unids;
//			rosten.read(rosten.webPath + "/applyManage/assetApplySubmit", content,rosten.submitCallback);
//		};
//	};
	
	//前台页面取消按钮，注销代码
//	assetApply_agree = function(){
//		var unids = rosten.getGridUnid("multi");
//		if (unids == "")
//			return;
//		var content = {applyStatus:"已审批"};
//		content.id = unids;
//		rosten.read(rosten.webPath + "/applyManage/assetApplyAgree", content,rosten.submitCallback);
//	};
	
	assetCards_create = function(){//生产资产卡片
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		var unids = rosten.getGridUnid("multi");
		if (unids == ""){
			alert("注意：请在列表中选择数据！")
			return;
		}
		var isCreatedCards = rosten.getGridSelectedValue("getCardsCreatedLabel");
		if(isCreatedCards == "已生成"){
			alert("注意：该申请单已生成资产卡片，请勿重复生成！");
			return;
		}
		var content ={};
		content.companyId = companyId;
		content.applyIds = unids;
		rosten.read(rosten.webPath + "/applyManage/assetCardsCreate",content,rosten.assetApplyCallback);
//		rosten.kernel.createRostenShowDialog(rosten.webPath + "/applyManage/assetCardsCreate?companyId=" + companyId + "&applyIds=" + unids, {
//            onLoadFunction : function() {
//            	
//            }
//        });
	};
	
	assetApply_formatTopic = function(value,rowIndex){
		return "<a href=\"javascript:assetApply_onMessageOpen(" + rowIndex + ");\">" + value + "</a>";
	};
	
	assetApply_onMessageOpen = function(rowIndex){
        var unid = rosten.kernel.getGridItemValue(rowIndex,"id");
        var userid = rosten.kernel.getUserInforByKey("idnumber");
		var companyId = rosten.kernel.getUserInforByKey("companyid");
		rosten.openNewWindow("assetApply", rosten.webPath + "/applyManage/assetApplyShow/" + unid + "?userid=" + userid + "&companyId=" + companyId + "&flowCode=assetApply");
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
