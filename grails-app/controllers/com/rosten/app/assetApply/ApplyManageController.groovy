package com.rosten.app.assetApply

import grails.converters.JSON
import java.text.SimpleDateFormat
import org.activiti.engine.runtime.ProcessInstance

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.Util

import com.rosten.app.assetApply.ApplyNotes
import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.assetConfig.AssetCode

import com.rosten.app.assetCards.CarCards
import com.rosten.app.assetCards.LandCards
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.assetCards.BookCards
import com.rosten.app.assetCards.FurnitureCards

import com.rosten.app.workflow.WorkFlowService
import com.rosten.app.workflow.FlowBusiness

import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.system.Model
import com.rosten.app.system.User
import com.rosten.app.system.SystemService
import com.rosten.app.system.UserGroup

import com.rosten.app.share.ShareService
import com.rosten.app.share.FlowLog
import com.rosten.app.start.StartService
import com.rosten.app.gtask.Gtask

import com.rosten.app.export.ExcelExport
import com.rosten.app.export.WordExport

class ApplyManageController {
	def mineApplySearchView ={
		def model =[:]
		
		def company = Company.get(params.companyId)
		model["categoryList"] = AssetCategory.findAllByCompany(company)
		
		render(view:'/assetApply/mineApplySearch',model:model)
	}
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	
    def imgPath ="images/rosten/actionbar/"
	def assetApplyService
	def springSecurityService
	def workFlowService
	def taskService
	def systemService
	def shareService
	def startService
	
//	/*机构代码转化列表*/
//	def getRegions = {
//		def regions = []
//		regions<<[name:'协会本部',orgno:'01']
//		regions<<[name:'舟山办事处',orgno:'02']
//		regions<<[name:'普陀办事处',orgno:'03']
//		regions<<[name:'岱山办事处',orgno:'04']
//		regions<<[name:'嵊泗办事处',orgno:'05']
//		regions<<[name:'定海办事处',orgno:'06']
//		regions<<[name:'台州办事处',orgno:'07']
//		regions<<[name:'温岭办事处',orgno:'08']
//		regions<<[name:'玉环办事处',orgno:'09']
//		regions<<[name:'路桥办事处',orgno:'10']
//		regions<<[name:'椒江办事处',orgno:'11']
//		regions<<[name:'临海办事处',orgno:'12']
//		regions<<[name:'三门办事处',orgno:'13']
//		regions<<[name:'温州办事处',orgno:'14']
//		regions<<[name:'瑞安办事处',orgno:'15']
//		regions<<[name:'苍南办事处',orgno:'16']
//		regions<<[name:'平阳办事处',orgno:'17']
//		regions<<[name:'乐清办事处',orgno:'18']
//		regions<<[name:'洞头办事处',orgno:'19']
//		regions<<[name:'嘉善办事处',orgno:'20']
//		regions<<[name:'平湖办事处',orgno:'21']
//		regions<<[name:'淳安办事处',orgno:'22']
//		regions<<[name:'上虞办事处',orgno:'23']
//		regions<<[name:'海盐办事处',orgno:'24']
//		regions<<[name:'台州服务中心本级',orgno:'61']
//		regions<<[name:'台州服务中心松门服务部',orgno:'62']
//		regions<<[name:'舟山服务中心',orgno:'63']
//		regions<<[name:'温州服务中心',orgno:'64']
//		
//		return regions
//	}
	
	def assetApplyForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "assetApply"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		if(params.id){
			def entity = ApplyNotes.get(params.id)
			def user = User.get(params.userid)
			if(user.equals(entity.currentUser)){
				//当前处理人
				switch (true){
					case entity.status.contains("审核") || entity.status.contains("审批"):
						actionList << createAction("填写意见",webPath +imgPath + "sign.png",strname + "_addComment")
						actionList << createAction("同意",webPath +imgPath + "ok.png",strname + "_submit")
						actionList << createAction("回退",webPath +imgPath + "back.png",strname + "_back")
						break;
					case entity.status.contains("打印"):
						actionList << createAction("填写意见",webPath +imgPath + "sign.png",strname + "_addComment")
						actionList << createAction("完成",webPath +imgPath + "submit.png",strname + "_submit")
						actionList << createAction("回退",webPath +imgPath + "back.png",strname + "_back")
						break;
					default :
						actionList << createAction("填写意见",webPath +imgPath + "sign.png",strname + "_addComment")
						actionList << createAction("提交",webPath +imgPath + "submit.png",strname + "_submit")
						actionList << createAction("回退",webPath +imgPath + "back.png",strname + "_back")
						break;
				}
			}
		}else{
			actionList << createAction("保存",webPath +imgPath + "Save.gif",strname + "_save")
		}
		render actionList as JSON
	}
	
	def mineApplyView ={
		def actionList =[]
		def strname = "assetApply"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		//增加资产管理员群组的控制权限
		def currentUser = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(currentUser).collect { elem ->
		  elem.group.groupName
		}
		if("zcgly" in userGroups || "xhzcgly" in userGroups){
			actionList << createAction("新增",imgPath + "add.png",strname + "_add")
			actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		}
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def applyApprovalView ={
		def actionList =[]
		def strname = "assetApply"
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("生成资产卡片",imgPath + "init.gif","assetCards_create")
		actionList << createAction("财务对接",imgPath + "s_open.png",strname + "_docking")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		actionList << createAction("导出",imgPath + "export.png",strname + "_export")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	private def createAction={name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
	
	def assetApplyAdd ={
		if(params.flowCode){
			//需要走流程
			def company = Company.get(params.companyId)
			def flowBusiness = FlowBusiness.findByFlowCodeAndCompany(params.flowCode,company)
			if(flowBusiness && !"".equals(flowBusiness.relationFlow)){
				params.relationFlow = flowBusiness.relationFlow
				redirect(action:"assetApplyShow",params:params)
			}else{
				//不存在流程引擎关联数据
				render '<h2 style="color:red;width:660px;margin:0 auto;margin-top:60px">当前业务不存在流程设置，无法创建，请联系管理员！</h2>'
			}
		}else{
			redirect(action:"assetApplyShow",params:params)
		}
	}
	
	def assetApplyShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def company = Company.get(params.companyId)
//		def onePrice
		def applyNotes = new ApplyNotes()
		if(params.id){
			applyNotes = ApplyNotes.get(params.id)
//			onePrice = String.format("%.2f", applyNotes.onePrice)//取消科学技术法展示
		}else{
			applyNotes.applyUser = currentUser
			applyNotes.userDepart = currentUser.getDepartEntity()
		}
		
		model["user"] = currentUser
		model["company"] = company
		model["applyNotes"] = applyNotes
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		//流程相关信息----------------------------------------------
		model["relationFlow"] = params.relationFlow
		model["flowCode"] = params.flowCode
		//------------------------------------------------------
		
		render(view:'/assetApply/applyShow',model:model)
	}
	
	def assetApplySave ={
		def json=[:]
		def company = Company.get(params.companyId)
		def currentUser = springSecurityService.getCurrentUser()
		
		//资产申请信息保存-------------------------------
		def applyNotes = new ApplyNotes()
		if(params.id && !"".equals(params.id)){
			applyNotes = ApplyNotes.get(params.id)
		}else{
			applyNotes.company = company
			applyNotes.applyUser = currentUser
		}
		applyNotes.properties = params
		applyNotes.clearErrors()
		
		//特殊字段信息处理
		def assetCategory = AssetCategory.get(params.allowCategoryId)
		applyNotes.userCategory = assetCategory
		if(assetCategory.parentName != null){
			applyNotes.rootAssetCategory = assetCategory.parentName
		}else{
			applyNotes.rootAssetCategory = assetCategory.categoryName
		}
//		applyNotes.userCategory = AssetCategory.get(params.allowCategoryId)
//		if(AssetCategory.parentName != null){
//			applyNotes.rootAssetCategory = AssetCategory.parentName
//		}else{
//			applyNotes.rootAssetCategory = AssetCategory.categoryName
//		}
		if(params.allowdepartsId.equals("")){
			applyNotes.userDepart = params.allowdepartsName
		}else{
			applyNotes.userDepart = Depart.get(params.allowdepartsId)
		}
		
		if(!params.registerNum_form.equals("")){
			applyNotes.registerNum = params.registerNum_form
		}
		applyNotes.applyStatus = applyNotes.status
		
		//判断是否需要走流程
		def _status
		if(params.relationFlow){
			//需要走流程
			if(params.id){
				_status = "old"
			}else{
				_status = "new"
				applyNotes.currentUser = currentUser
				applyNotes.currentDepart = currentUser.getDepartName()
				applyNotes.currentDealDate = new Date()
				
				applyNotes.drafter = currentUser
				applyNotes.drafterDepart = currentUser.getDepartName()
			}
			
			//增加读者域
			if(!applyNotes.readers.find{ it.id.equals(currentUser.id) }){
				applyNotes.addToReaders(currentUser)
			}
			
			//流程引擎相关信息处理-------------------------------------------------------------------------------------
			if(!applyNotes.processInstanceId){
				//启动流程实例
				def _processInstance = workFlowService.getProcessDefinition(params.relationFlow)
				Map<String, Object> variables = new HashMap<String, Object>();
				ProcessInstance processInstance = workFlowService.addFlowInstance(_processInstance.key, currentUser.username,applyNotes.id, variables);
				applyNotes.processInstanceId = processInstance.getProcessInstanceId()
				applyNotes.processDefinitionId = processInstance.getProcessDefinitionId()
				
				//获取下一节点任务
				def task = workFlowService.getTasksByFlow(processInstance.getProcessInstanceId())[0]
				applyNotes.taskId = task.getId()
			}
			//-------------------------------------------------------------------------------------------------
		}
		
		if(applyNotes.save(flush:true)){
			json["result"] = "true"
			json["id"] = applyNotes.id
		}else{
			applyNotes.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def assetApplyDelete = {
		def ids = params.id.split(",")
		def currentUser = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(currentUser).collect { elem ->
			elem.group.groupName
		}
		def json
		try{
			ids.each{
				def applyNotes = ApplyNotes.get(it)
				if(applyNotes){
					if("xhzcgly" in userGroups || currentUser.getAllRolesValue().contains("资产管理员")){
						applyNotes.delete(flush: true)
						json = [result:'true']
					}else{
						if(applyNotes.isCreatedCards=="0"){
							applyNotes.delete(flush: true)
							json = [result:'true']
						}else{
							json = [result:'已生产资产卡片的申请单无法删除，请联系管理员！']
						}
					}
					
				}
			}
//			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetApplySubmit ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def applyNotes = ApplyNotes.get(it)
				if(applyNotes){
					applyNotes.applyStatus = params.applyStatus
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetApplyAgree ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def applyNotes = ApplyNotes.get(it)
				if(applyNotes){
					applyNotes.applyStatus = params.applyStatus
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	private def getAssetCardCode ={config,applyNotes ->
		def a,b,c,d
		
		//资产类别代码
		if(applyNotes.onePrice>2000){
			a = "1"
		}else{
			a = "2"
		}
		
		//机构代码
		b = applyNotes.userDepart.departCode?applyNotes.userDepart.departCode:"01"
		
		//购置年月
		Calendar cal = Calendar.getInstance();
		c = cal.get(Calendar.YEAR).toString().substring(2) + (cal.get(Calendar.MONTH)+1).toString()
		
		//资产序号
		d = config.nowSN.toString().padLeft(3,"0")
		config.nowSN += 1
		
		return a + b + c + d
	}
	def assetCardsCreate ={//创建资产卡片
		def model=[:]
		def company = Company.get(params.companyId)
		def createCards = "false"
		def ids = params.applyIds.split(",")
		def json,message
		/**
		 * 创建资产卡片
		 */
		try{
			int createCardsCount = 0
			def config = AssetCode.first()
			
			ids.each{  
				def applyNotes = ApplyNotes.get(it)
				def assetType = applyNotes.userCategory.getRootCategory().categoryName
				
				//获取资产编号
				def assetCount = applyNotes.amount
				def onePrice = applyNotes.onePrice

				if(applyNotes.isCreatedCards == "0"){
					if(assetType.equals("电子设备")){
						/*获取申请信息中的数量，创建相同数量的资产卡片*/
						for(int i=0;i<assetCount;i++){
							def deviceCard = new DeviceCards()
							deviceCard.company = company
							deviceCard.applyNotes = applyNotes
							deviceCard.registerNum = this.getAssetCardCode(config,applyNotes)
							deviceCard.userCategory = applyNotes.userCategory
							deviceCard.assetName = applyNotes.assetName
							deviceCard.userDepart = applyNotes.userDepart
							deviceCard.onePrice = onePrice
							deviceCard.country = applyNotes.country
							deviceCard.assetStatus = "已入库"
							deviceCard.save()
						}
					}else if(assetType == "运输工具"){
						/*获取申请信息中的数量，创建相同数量的资产卡片*/
						for(int i=0;i<assetCount;i++){
							def carCard = new CarCards()
							carCard.company = company
							carCard.applyNotes = applyNotes
							carCard.registerNum = this.getAssetCardCode(config,applyNotes)
							carCard.userCategory = applyNotes.userCategory
							carCard.assetName = applyNotes.assetName
							carCard.userDepart = applyNotes.userDepart
							carCard.onePrice = onePrice
							carCard.country = applyNotes.country
							carCard.assetStatus = "已入库"
							carCard.save()
						}
						
					}else if(assetType == "房屋及建筑物"){
						/*获取申请信息中的数量，创建相同数量的资产卡片*/
						for(int i=0;i<assetCount;i++){
							def houseCard = new HouseCards()
							houseCard.company = company
							houseCard.applyNotes = applyNotes
							houseCard.registerNum = this.getAssetCardCode(config,applyNotes)
							houseCard.userCategory = applyNotes.userCategory
							houseCard.assetName = applyNotes.assetName
							houseCard.userDepart = applyNotes.userDepart
							houseCard.onePrice = onePrice
							houseCard.country = applyNotes.country
							houseCard.assetStatus = "已入库"
							houseCard.save()
						}
					}else if(assetType == "办公家具"){
						/*获取申请信息中的数量，创建相同数量的资产卡片*/
						for(int i=0;i<assetCount;i++){
							def furnitureCard = new FurnitureCards()
							furnitureCard.company = company
							furnitureCard.applyNotes = applyNotes
							furnitureCard.registerNum = this.getAssetCardCode(config,applyNotes)
							furnitureCard.userCategory = applyNotes.userCategory
							furnitureCard.assetName = applyNotes.assetName
							furnitureCard.userDepart = applyNotes.userDepart
							furnitureCard.onePrice = onePrice
							furnitureCard.country = applyNotes.country
							furnitureCard.assetStatus = "已入库"
							furnitureCard.save()
						}
					}
//					else if(assetType == "土地"){
//						/*获取申请信息中的数量，创建相同数量的资产卡片*/
//						for(int i=0;i<assetCount;i++){
//							def landCard = new LandCards()
//							landCard.company = company
//							landCard.applyNotes = applyNotes
//							landCard.registerNum = this.getAssetCardCode(config,applyNotes)
//							landCard.userCategory = applyNotes.userCategory
//							landCard.assetName = applyNotes.assetName
//							landCard.userDepart = applyNotes.userDepart
//							landCard.onePrice = onePrice
//							landCard.country = applyNotes.country
//							landCard.assetStatus = "已入库"
//							landCard.save()
//						}
//					}else if(assetType == "图书"){
//						/*获取申请信息中的数量，创建相同数量的资产卡片*/
//						for(int i=0;i<assetCount;i++){
//							def bookCard = new BookCards()
//							bookCard.company = company
//							bookCard.applyNotes = applyNotes
//							bookCard.registerNum = this.getAssetCardCode(config,applyNotes)
//							bookCard.userCategory = applyNotes.userCategory
//							bookCard.assetName = applyNotes.assetName
//							bookCard.userDepart = applyNotes.userDepart
//							bookCard.onePrice = onePrice
//							bookCard.country = applyNotes.country
//							bookCard.assetStatus = "已入库"
//							bookCard.save()
//						}
//					}
					applyNotes.isCreatedCards = "1"
					createCardsCount += 1
					createCards = "true"
				}
				//修改编号配置文档信息，并flush
				config.save(flush:true)
				
			}
			message = "共有"+createCardsCount+"个资产申请单生成资产卡片！"
			json = [result:'true',message:message]
		}catch(Exception e){
			message = "错误，请联系管理员！"
			json = [result:'error',message:message]
		}
		model["createCards"] = createCards
		
		render json as JSON
	}
	
	def mineApplyGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			def _gridHeader =[]
			_gridHeader << ["name":"序号","width":"40px","colIdx":0,"field":"rowIndex"]
			_gridHeader << ["name":"申请编号","width":"120px","colIdx":1,"field":"registerNum","formatter":"assetApply_formatTopic"]
			_gridHeader << ["name":"申请人","width":"100px","colIdx":2,"field":"getFormattedUser"]
			_gridHeader << ["name":"申请部门","width":"100px","colIdx":3,"field":"getDepartName"]
			_gridHeader << ["name":"资产分类","width":"100px","colIdx":4,"field":"getCategoryName"]
			_gridHeader << ["name":"资产名称","width":"auto","colIdx":5,"field":"assetName"]
			_gridHeader << ["name":"使用人","width":"100px","colIdx":6,"field":"userName"]
			_gridHeader << ["name":"数量","width":"80px","colIdx":7,"field":"amount"]
			_gridHeader << ["name":"单价（元）","width":"80px","colIdx":8,"field":"onePrice"]
			_gridHeader << ["name":"当前处理人","width":"100px","colIdx":9,"field":"getCurrentUserName"]
			_gridHeader << ["name":"流程状态","width":"80px","colIdx":10,"field":"status"]
			json["gridHeader"] = _gridHeader
//			json["gridHeader"] = assetApplyService.getAssetApplyListLayout()
		}
		
		//增加查询条件
		def searchArgs =[:]
		
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		if(params.category && !"".equals(params.category)) searchArgs["userCategory"] = AssetCategory.findByCompanyAndCategoryName(company,params.category)
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetApplyService.getMineApplyDataStore(args,searchArgs)
			
		}
		if(params.refreshPageControl){
			def total = assetApplyService.getMineApplyCount(company,searchArgs)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def assetApplyGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetApplyService.getAssetApplyListLayout()
		}
		
		//增加查询条件
		def searchArgs =[:]
		
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		if(params.category && !"".equals(params.category)) searchArgs["userCategory"] = AssetCategory.findByCompanyAndCategoryName(company,params.category)
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetApplyService.getAssetApplyDataStore(args,searchArgs)
			
		}
		if(params.refreshPageControl){
			def total = assetApplyService.getAssetApplyCount(company,searchArgs)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def assetApplyFlowDeal ={
		def json=[:]
		
		def applyNotes = ApplyNotes.get(params.id)
		
		//处理当前人的待办事项
		def currentUser = springSecurityService.getCurrentUser()
		def frontStatus = applyNotes.status
		def nextStatus,nextDepart,nextLogContent
		def nextUsers=[]
		
		//流程引擎相关信息处理-------------------------------------------------------------------------------------
		
		//结束当前任务，并开启下一节点任务
		def map =[:]
		if(params.conditionName){
			map[params.conditionName] = params.conditionValue
		}
		taskService.complete(applyNotes.taskId,map)	//结束当前任务
		
		ProcessInstance processInstance = workFlowService.getProcessIntance(applyNotes.processInstanceId)
		if(!processInstance || processInstance.isEnded()){
			//流程已结束
			nextStatus = "已结束"
			applyNotes.currentUser = null
			applyNotes.currentDepart = null
			applyNotes.taskId = null
		}else{
			//获取下一节点任务，目前处理串行情况
			def tasks = workFlowService.getTasksByFlow(applyNotes.processInstanceId)
			def task = tasks[0]
			if(task.getDescription() && !"".equals(task.getDescription())){
				nextStatus = task.getDescription()
			}else{
				nextStatus = task.getName()
			}
			applyNotes.taskId = task.getId()
		
			if(params.dealUser){
				//下一步相关信息处理
				def dealUsers = params.dealUser.split(",")
				if(dealUsers.size() >1){
					//并发
				}else{
					//串行
					def nextUser = User.get(Util.strLeft(params.dealUser,":"))
					nextDepart = Util.strRight(params.dealUser, ":")
					
					//判断是否有公务授权------------------------------------------------------------
					def _model = Model.findByModelCodeAndCompany("assetApply",currentUser.company)
					def authorize = systemService.checkIsAuthorizer(nextUser,_model,new Date())
					if(authorize){
						shareService.addFlowLog(applyNotes.id,"assetApply",nextUser,"委托授权给【" + authorize.beAuthorizerDepart + ":" + authorize.getFormattedAuthorizer() + "】")
						nextUser = authorize.beAuthorizer
						nextDepart = authorize.beAuthorizerDepart
					}
					//-------------------------------------------------------------------------
					
					//任务指派给当前拟稿人
					taskService.claim(applyNotes.taskId, nextUser.username)
					
					def args = [:]
					args["type"] = "【资产申请】"
					args["content"] = "请您审核编号为  【" + applyNotes.registerNum +  "】 的资产申请信息！"
					args["contentStatus"] = nextStatus
					args["contentId"] = applyNotes.id
					args["user"] = nextUser
					args["company"] = nextUser.company
					
					startService.addGtask(args)
					
					applyNotes.currentUser = nextUser
					applyNotes.currentDepart = nextDepart
					
					if(!applyNotes.readers.find{ item->
						item.id.equals(nextUser.id)
					}){
						applyNotes.addToReaders(nextUser)
					}
					nextUsers << nextUser.getFormattedName()
				}
			}
		}
		applyNotes.status = nextStatus
		applyNotes.currentDealDate = new Date()
		//处理资产审核状态
		applyNotes.applyStatus = nextStatus
//		if(params.status.equals("资产报备") || params.status.equals("打印盖章")){
//			applyNotes.applyStatus = "已结束"
//		}else{
//			applyNotes.applyStatus = nextStatus
//		}
		
		//判断下一处理人是否与当前处理人员为同一人
		if(currentUser.equals(applyNotes.currentUser)){
			json["refresh"] = true
		}
		
		//----------------------------------------------------------------------------------------------------
		
		//修改代办事项状态
		def gtask = Gtask.findWhere(
			user:currentUser,
			company:currentUser.company,
			contentId:applyNotes.id,
			contentStatus:frontStatus,
			status:"0"
		)
		if(gtask!=null){
			gtask.dealDate = new Date()
			gtask.status = "1"
			gtask.save(flush:true)
		}
		
		if(applyNotes.save(flush:true)){
			//添加日志
			def logContent
			switch (true){
				case applyNotes.status.contains("已结束"):
					logContent = "结束流程"
					break
				case applyNotes.status.contains("归档"):
					logContent = "归档"
					break
				case applyNotes.status.contains("不同意"):
					logContent = "不同意！"
					break
				default:
					logContent = "提交" + applyNotes.status + "【" + nextUsers.join("、") + "】"
					break
			}
			shareService.addFlowLog(applyNotes.id,"assetApply",currentUser,logContent)
			json["nextUserName"] = nextUsers.join("、")
			json["result"] = true
		}else{
			applyNotes.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	
	def assetApplyFlowBack ={
		def json=[:]
		def applyNotes = ApplyNotes.get(params.id)
		
		def currentUser = springSecurityService.getCurrentUser()
		def frontStatus = applyNotes.status
		
		try{
			//获取上一处理任务
			def frontTaskList = workFlowService.findBackAvtivity(applyNotes.taskId)
			if(frontTaskList && frontTaskList.size()>0){
				//简单的取最近的一个节点
				def activityEntity = frontTaskList[frontTaskList.size()-1]
				def activityId = activityEntity.getId();
				
				//流程跳转
				workFlowService.backProcess(applyNotes.taskId, activityId, null)
				
				//获取下一节点任务，目前处理串行情况
				def nextStatus
				def tasks = workFlowService.getTasksByFlow(applyNotes.processInstanceId)
				def task = tasks[0]
				if(task.getDescription() && !"".equals(task.getDescription())){
					nextStatus = task.getDescription()
				}else{
					nextStatus = task.getName()
				}
				applyNotes.taskId = task.getId()
				
				//获取对应节点的处理人员以及相关状态
				def historyActivity = workFlowService.getHistrotyActivityByActivity(applyNotes.taskId,activityId)
				def user = User.findByUsername(historyActivity.getAssignee())
				
				//任务指派给当前拟稿人
				taskService.claim(applyNotes.taskId, user.username)
				
				//增加待办事项
				def args = [:]
				args["type"] = "【资产申请】"
				args["content"] = "编号为  【" + applyNotes.registerNum +  "】 的资产申请信息被退回，请查看！"
				args["contentStatus"] = nextStatus
				args["contentId"] = applyNotes.id
				args["user"] = user
				args["company"] = user.company
				
				startService.addGtask(args)
					
				//修改相关信息
				applyNotes.currentUser = user
				applyNotes.currentDepart = user.getDepartName()
				applyNotes.currentDealDate = new Date()
				applyNotes.status = nextStatus
				//处理资产审核状态
				applyNotes.applyStatus = nextStatus
				
				//判断下一处理人是否与当前处理人员为同一人
				if(currentUser.equals(applyNotes.currentUser)){
					json["refresh"] = true
				}
				
				//----------------------------------------------------------------------------------------------------
				
				//修改代办事项状态
				def gtask = Gtask.findWhere(
					user:currentUser,
					company:currentUser.company,
					contentId:applyNotes.id,
					contentStatus:frontStatus,
					status:"0"
				)
				if(gtask!=null){
					gtask.dealDate = new Date()
					gtask.status = "1"
					gtask.save(flush:true)
				}
				
				applyNotes.save(flush:true)
				
				//添加日志
				def logContent = "退回【" + user.getFormattedName() + "】"
				
				shareService.addFlowLog(applyNotes.id,"assetApply",currentUser,logContent)
				json["nextUserName"] = user?.getFormattedName()
			}
			
			json["result"] = true
		}catch(Exception e){
			json["result"] = false
		}
		render json as JSON
	}
	
	def assetApplyExport = {
		OutputStream os = response.outputStream
		def company = Company.get(params.companyId)
		response.setContentType('application/vnd.ms-excel')
		response.setHeader("Content-disposition", "attachment; filename=" + new String("资产申请信息.xls".getBytes("GB2312"), "ISO_8859_1"))
		
		//查询条件
//		def searchArgs =[:]
//		if(params.username && !"".equals(params.username)) searchArgs["username"] = params.username
//		if(params.chinaName && !"".equals(params.chinaName)) searchArgs["chinaName"] = params.chinaName
//		if(params.departName && !"".equals(params.departName)) searchArgs["departName"] = params.departName
		
		def c = ApplyNotes.createCriteria()

		def applyNotesList = c.list{
			
			eq("company",company)
			eq("status","已结束")
		}
		def excel = new ExcelExport()
		excel.assetApplyDc(os,applyNotesList)
	}
}
