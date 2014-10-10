package com.rosten.app.assetApply

import grails.converters.JSON
import org.activiti.engine.runtime.ProcessInstance

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.Util

import com.rosten.app.assetApply.ApplyNotes
import com.rosten.app.assetConfig.AssetCategory

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

import com.rosten.app.share.ShareService
import com.rosten.app.share.FlowLog
import com.rosten.app.start.StartService
import com.rosten.app.gtask.Gtask

class ApplyManageController {

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
	
	def assetApplyForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "assetApply"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		actionList << createAction("填写意见",webPath + imgPath + "submit.png",strname + "_addComment")
		actionList << createAction("提交",webPath + imgPath + "submit.png",strname + "_submit")
		actionList << createAction("退回",webPath + imgPath + "submit.png",strname + "_back")
		render actionList as JSON
	}
	
	def mineApplyView ={
		def actionList =[]
		def strname = "assetApply"
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("提交",imgPath + "add.png",strname + "_submit")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def applyApprovalView ={
		def actionList =[]
		def strname = "assetApply"
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("同意",imgPath + "add.png",strname + "_agree")
		actionList << createAction("生成资产卡片",imgPath + "add.png","assetCards_create")
		actionList << createAction("财务对接",imgPath + "add.png",strname + "_delete")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
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
		
		def applyNotes = new ApplyNotes()
		if(params.id){
			applyNotes = ApplyNotes.get(params.id)
		}else{
			applyNotes.applyUser = currentUser
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
		}else{
			applyNotes.errors.each{
				println it
			}
			json["result"] = "false"
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
	
	def assetCardsCreate ={//创建资产卡片
		def model=[:]
		def company = Company.get(params.companyId)
		def createCards = "false"
		def ids = params.applyIds.split(",")
		def json
		/**
		 * 创建资产卡片
		 */
		try{
			if(ids.size()>0){
				createCards = "true"
				ids.each {  
					def applyNotes = ApplyNotes.get(it)
					def assetType = applyNotes.rootAssetCategory
					if(assetType.equals("设备")){
						/*
						 * 获取申请信息中的数量，创建相同数量的资产卡片
						 */
						def assetCount = applyNotes.amount
						for(int i=0;i<assetCount;i++){
							def deviceCard = new DeviceCards()
							deviceCard.company = company
							deviceCard.applyNotes = applyNotes
							deviceCard.registerNum = getFormattedSeriesDate()
							deviceCard.userCategory = applyNotes.userCategory
							deviceCard.assetName = applyNotes.assetName
							deviceCard.userDepart = applyNotes.userDepart
							deviceCard.onePrice = applyNotes.totalPrice/assetCount
							deviceCard.country = applyNotes.country
							deviceCard.assetStatus = "新建"
							deviceCard.save(flush: true)
						}
					}else if(assetType == "车辆"){
						/*
						 * 获取申请信息中的数量，创建相同数量的资产卡片
						 */
						def assetCount = applyNotes.amount
						for(int i=0;i<assetCount;i++){
							def carCard = new CarCards()
							carCard.company = company
							carCard.applyNotes = applyNotes
							carCard.registerNum = getFormattedSeriesDate()
							carCard.userCategory = applyNotes.userCategory
							carCard.assetName = applyNotes.assetName
							carCard.userDepart = applyNotes.userDepart
	//						carCard.userStatus = applyNotes.userStatus
	//						carCard.assetSource = applyNotes.assetSource
	//						carCard.costCategory = applyNotes.costCategory
	//						carCard.buyDate = applyNotes.buyDate
	//						carCard.organizationalType = applyNotes.organizationalType
							carCard.onePrice = applyNotes.totalPrice/assetCount
	//						if(applyNotes.undertakingRevenue == 0){
	//							carCard.undertakingRevenue = 0
	//						}else{
	//							carCard.undertakingRevenue = applyNotes.undertakingRevenue/assetCount
	//						}
	//						if(applyNotes.fiscalAppropriation == 0){
	//							carCard.fiscalAppropriation = 0
	//						}else{
	//							carCard.fiscalAppropriation = applyNotes.fiscalAppropriation/assetCount
	//						}
	//						if(applyNotes.otherFund == 0){
	//							carCard.otherFund = 0
	//						}else{
	//							carCard.otherFund = applyNotes.otherFund/assetCount
	//						}
	//						carCard.storagePosition = applyNotes.storagePosition
							carCard.country = applyNotes.country
							carCard.assetStatus = "新建"
							carCard.save(flush: true)
						}
						
					}else if(assetType == "房屋"){
						/*
						 * 获取申请信息中的数量，创建相同数量的资产卡片
						 */
						def assetCount = applyNotes.amount
						for(int i=0;i<assetCount;i++){
							def houseCard = new HouseCards()
							houseCard.company = company
							houseCard.applyNotes = applyNotes
							houseCard.registerNum = getFormattedSeriesDate()
							houseCard.userCategory = applyNotes.userCategory
							houseCard.assetName = applyNotes.assetName
							houseCard.userDepart = applyNotes.userDepart
							houseCard.onePrice = applyNotes.totalPrice/assetCount
							houseCard.country = applyNotes.country
							houseCard.assetStatus = "新建"
							houseCard.save(flush: true)
						}
					}else if(assetType == "土地"){
						/*
						 * 获取申请信息中的数量，创建相同数量的资产卡片
						 */
						def assetCount = applyNotes.amount
						for(int i=0;i<assetCount;i++){
							def landCard = new LandCards()
							landCard.company = company
							landCard.applyNotes = applyNotes
							landCard.registerNum = getFormattedSeriesDate()
							landCard.userCategory = applyNotes.userCategory
							landCard.assetName = applyNotes.assetName
							landCard.userDepart = applyNotes.userDepart
							landCard.onePrice = applyNotes.totalPrice/assetCount
							landCard.country = applyNotes.country
							landCard.assetStatus = "新建"
							landCard.save(flush: true)
						}
					}else if(assetType == "图书"){
						/*
						 * 获取申请信息中的数量，创建相同数量的资产卡片
						 */
						def assetCount = applyNotes.amount
						for(int i=0;i<assetCount;i++){
							def bookCard = new BookCards()
							bookCard.company = company
							bookCard.applyNotes = applyNotes
							bookCard.registerNum = getFormattedSeriesDate()
							bookCard.userCategory = applyNotes.userCategory
							bookCard.assetName = applyNotes.assetName
							bookCard.userDepart = applyNotes.userDepart
							bookCard.onePrice = applyNotes.totalPrice/assetCount
							bookCard.country = applyNotes.country
							bookCard.assetStatus = "新建"
							bookCard.save(flush: true)
						}
					}else if(assetType == "家具"){
						/*
						 * 获取申请信息中的数量，创建相同数量的资产卡片
						 */
						def assetCount = applyNotes.amount
						for(int i=0;i<assetCount;i++){
							def furnitureCard = new FurnitureCards()
							furnitureCard.company = company
							furnitureCard.applyNotes = applyNotes
							furnitureCard.registerNum = getFormattedSeriesDate()
							furnitureCard.userCategory = applyNotes.userCategory
							furnitureCard.assetName = applyNotes.assetName
							furnitureCard.userDepart = applyNotes.userDepart
							furnitureCard.onePrice = applyNotes.totalPrice/assetCount
							furnitureCard.country = applyNotes.country
							furnitureCard.assetStatus = "新建"
							furnitureCard.save(flush: true)
						}
					}
					applyNotes.isCreatedCards = "1"
				}
				json = [result:'true']
			}else{
				json = [result:'error']
			}
		}catch(Exception e){
			json = [result:'error']
		}
		model["createCards"] = createCards
		def applyNotes = new ApplyNotes()
		
		render json as JSON
//		if(createCards == "true"){
//			render(view:"/assetApply/createCards",model:model)
//		}else{
//			render(view:'/demo/applyShow',model:model)
//		}
		//undo
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
			_gridHeader << ["name":"数量","width":"80px","colIdx":6,"field":"amount"]
			_gridHeader << ["name":"金额（元）","width":"80px","colIdx":7,"field":"totalPrice"]
			_gridHeader << ["name":"用途","width":"100px","colIdx":8,"field":"usedBy"]
			_gridHeader << ["name":"状态","width":"80px","colIdx":9,"field":"applyStatus"]
			json["gridHeader"] = _gridHeader
//			json["gridHeader"] = assetApplyService.getAssetApplyListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetApplyService.getMineApplyDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetApplyService.getMineApplyCount(company)
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
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetApplyService.getAssetApplyDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetApplyService.getAssetApplyCount(company)
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
					def _model = Model.findByModelCodeAndCompany("zcsq",currentUser.company)
					def authorize = systemService.checkIsAuthorizer(nextUser,_model,new Date())
					if(authorize){
						shareService.addFlowLog(applyNotes.id,"zcsq",nextUser,"委托授权给【" + authorize.beAuthorizerDepart + ":" + authorize.getFormattedAuthorizer() + "】")
						nextUser = authorize.beAuthorizer
						nextDepart = authorize.beAuthorizerDepart
					}
					//-------------------------------------------------------------------------
					
					//任务指派给当前拟稿人
					taskService.claim(applyNotes.taskId, nextUser.username)
					
					def args = [:]
					args["type"] = "【资产申请】"
					args["content"] = "请您审核编号为  【" + applyNotes.registerNum +  "】 的申请信息"
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
			shareService.addFlowLog(applyNotes.id,"zcsq",currentUser,logContent)
						
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
				args["content"] = "编号为  【" + applyNotes.registerNum +  "】 的申请信息被退回，请查看！"
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
				
				shareService.addFlowLog(applyNotes.id,"zcsq",currentUser,logContent)
			}
				
			json["result"] = true
		}catch(Exception e){
			json["result"] = false
		}
		render json as JSON
	}
}
