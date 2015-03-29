package com.rosten.app.assetChange

import grails.converters.JSON

import com.rosten.app.assetCards.CarCards
import com.rosten.app.assetCards.LandCards
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.assetCards.BookCards
import com.rosten.app.assetCards.FurnitureCards

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.GridUtil
import com.rosten.app.util.Util

import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Depart
import com.rosten.app.system.UserGroup

import org.activiti.engine.runtime.ProcessInstance
import com.rosten.app.system.Model
import com.rosten.app.system.SystemService
import com.rosten.app.workflow.WorkFlowService
import com.rosten.app.workflow.FlowBusiness

import com.rosten.app.share.ShareService
import com.rosten.app.share.FlowLog
import com.rosten.app.start.StartService
import com.rosten.app.gtask.Gtask

import com.rosten.app.export.ExcelExport
import com.rosten.app.export.WordExport

class AssetRepairController {

    def assetCardsService
    def assetChangeService
	def springSecurityService
	def workFlowService
	def taskService
	def systemService
	def shareService
	def startService

	def imgPath ="images/rosten/actionbar/"
	
	def assetRepairForm = {
		def webPath = request.getContextPath() + "/"
		def strname = "assetRepair"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		if(params.id){
			def entity = AssetRepair.get(params.id)
			def user = User.get(params.userid)
			if(user.equals(entity.currentUser)){
				//当前处理人
				switch (true){
					case entity.status.contains("新建"):
						actionList << createAction("保存",webPath +imgPath + "Save.gif",strname + "_save")
						actionList << createAction("填写意见",webPath +imgPath + "sign.png",strname + "_addComment")
						actionList << createAction("提交",webPath +imgPath + "submit.png",strname + "_submit")
						break;
					case entity.status.contains("审核") || entity.status.contains("审批"):
						actionList << createAction("填写意见",webPath +imgPath + "sign.png",strname + "_addComment")
						actionList << createAction("同意",webPath +imgPath + "ok.png",strname + "_submit")
						actionList << createAction("回退",webPath +imgPath + "back.png",strname + "_back")
						break;
					default :
						actionList << createAction("填写意见",webPath +imgPath + "sign.png",strname + "_addComment")
						actionList << createAction("提交",webPath +imgPath + "submit.png",strname + "_submit")
						break;
				}
			}
		}else{
			actionList << createAction("保存",webPath +imgPath + "Save.gif",strname + "_save")
		}
		render actionList as JSON
	}
	
	  def assetRepairView = {
		def actionList =[]
		def strname = "assetRepair"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		//增加资产管理员群组的控制权限
		def currentUser = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(currentUser).collect { elem ->
		  elem.group.groupName
		}
		if("zcgly" in userGroups || "xhzcgly" in userGroups || "资产管理员" in userGroups || "协会资产管理员" in userGroups || "admin".equals(currentUser.getUserType())){
			actionList << createAction("资产报修",imgPath + "add.png",strname + "_add")
			actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
//			actionList << createAction("打印",imgPath + "word_print.png",strname + "_print")
		}
		actionList << createAction("导出",imgPath + "export.png",strname + "_export")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}

	private def createAction = {name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
	
	def assetRepairSearchView ={
		def model =[:]
		
		def company = Company.get(params.companyId)
		model["company"] = company
//		model["DepartList"] = Depart.findAllByCompany(company)
		
		render(view:'/assetChange/assetRepairSearch',model:model)
	}
	
	def assetRepairAdd = {
		if(params.flowCode){
			//需要走流程
			def company = Company.get(params.companyId)
			def flowBusiness = FlowBusiness.findByFlowCodeAndCompany(params.flowCode,company)
			if(flowBusiness && !"".equals(flowBusiness.relationFlow)){
				params.relationFlow = flowBusiness.relationFlow
				redirect(action:"assetRepairShow",params:params)
			}else{
				//不存在流程引擎关联数据
				render '<h2 style="color:red;width:660px;margin:0 auto;margin-top:60px">当前业务不存在流程设置，无法创建，请联系管理员！</h2>'
			}
		}else{
			redirect(action:"assetRepairShow",params:params)
		}
	}
	
	def assetRepairShow = {
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def assetRepair = new AssetRepair()
		def drafter
		def isAllowedEdit
		if(params.id){
			assetRepair = AssetRepair.get(params.id)
			//判断用户是否一致，若一致且未提交状态，则可编辑 
			drafter = assetRepair.drafter
			if(user.equals(drafter) && assetRepair.dataStatus.equals("新建")){
				isAllowedEdit = "yes"
			}else{
				isAllowedEdit = "no"
			}
		}else{
			isAllowedEdit = "new"
		}
		
		model["user"] = user
		model["company"] = company
		model["assetRepair"] = assetRepair
		model["isAllowedEdit"] = isAllowedEdit
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		//流程相关信息----------------------------------------------
		model["relationFlow"] = params.relationFlow
		model["flowCode"] = params.flowCode
		//------------------------------------------------------
		
		render(view:'/assetChange/assetRepair',model:model)
	}
	
	def assetRepairSave = {
		def json=[:]
		def company = Company.get(params.companyId)
		def currentUser = springSecurityService.getCurrentUser()
		
		//资产报修申请信息保存-------------------------------
		def assetRepair = new AssetRepair()
		if(params.id && !"".equals(params.id)){
			assetRepair = AssetRepair.get(params.id)
		}else{
			assetRepair.company = company
		}
		assetRepair.properties = params
		assetRepair.clearErrors()
		
		//特殊字段信息处理
		assetRepair.applyDate = Util.convertToTimestamp(params.applyDate)
		if(params.usedDepartId.equals("")){
			assetRepair.usedDepart = params.usedDepartName
		}else{
			assetRepair.usedDepart = Depart.get(params.usedDepartId)
		}
		if(!params.seriesNo_form.equals("")){
			assetRepair.seriesNo = params.seriesNo_form
		}
		assetRepair.dataStatus = assetRepair.status
		
		//处理筛选的资产卡片资源
		def categoryId
		def categoryIds = []
		if(params.categoryId && params.categoryId !=null){
			categoryId = params.categoryId
			categoryIds = categoryId.split(",")
		}
		if(categoryIds.size()>0){
			categoryIds.each {
				def entity = this.getEntity(it)
				if(entity){
					entity.purchaser = params.usedMan
					entity.assetStatus = "已报修"
					def seriesNo_exist = entity.seriesNo
					if(seriesNo_exist != null && seriesNo_exist !=""){//操作号已存在
						def seriesNo_exists = seriesNo_exist.split(",")
						if(params.seriesNo_form in seriesNo_exists){
							//undo
						}else{
							entity.seriesNo += ","+params.seriesNo_form
						}
					}else{
						entity.seriesNo = params.seriesNo_form
					}
				}
			}
		}
		
		//判断是否需要走流程
		def _status
		if(params.relationFlow){
			//需要走流程
			if(params.id){
				_status = "old"
			}else{
				_status = "new"
				assetRepair.currentUser = currentUser
				assetRepair.currentDepart = currentUser.getDepartName()
				assetRepair.currentDealDate = new Date()
				
				assetRepair.drafter = currentUser
				assetRepair.drafterDepart = currentUser.getDepartName()
			}
			
			//增加读者域
			if(!assetRepair.readers.find{ it.id.equals(currentUser.id) }){
				assetRepair.addToReaders(currentUser)
			}
			
			//流程引擎相关信息处理-------------------------------------------------------------------------------------
			if(!assetRepair.processInstanceId){
				//启动流程实例
				def _processInstance = workFlowService.getProcessDefinition(params.relationFlow)
				Map<String, Object> variables = new HashMap<String, Object>();
				ProcessInstance processInstance = workFlowService.addFlowInstance(_processInstance.key, currentUser.username,assetRepair.id, variables);
				assetRepair.processInstanceId = processInstance.getProcessInstanceId()
				assetRepair.processDefinitionId = processInstance.getProcessDefinitionId()
				
				//获取下一节点任务
				def task = workFlowService.getTasksByFlow(processInstance.getProcessInstanceId())[0]
				assetRepair.taskId = task.getId()
			}
			//-------------------------------------------------------------------------------------------------
		}
		
		if(assetRepair.save(flush:true)){
			json["result"] = "true"
			json["id"] = assetRepair.id
		}else{
			assetRepair.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def assetRepairDelete = {
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def assetRepair = AssetRepair.get(it)
				if(assetRepair){
					assetRepair.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetRepairGrid = {
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetChangeService.getAssetRepairListLayout()
		}
		
		//增加查询条件
		def searchArgs =[:]
		
		if(params.seriesNo && !"".equals(params.seriesNo)) searchArgs["seriesNo"] = params.seriesNo
		def usedDepartList = []
		if(params.usedDepart && !"".equals(params.usedDepart)){
			params.usedDepart.split(",").each{
				def _list = []
				usedDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
			}
			searchArgs["usedDepart"] = usedDepartList.unique()
		}
//		if(params.usedDepart && !"".equals(params.usedDepart)) searchArgs["usedDepart"] = Depart.findByCompanyAndDepartName(company,params.usedDepart)
		if(params.usedMan && !"".equals(params.usedMan)) searchArgs["usedMan"] = params.usedMan
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetChangeService.getAssetRepairDataStore(args,searchArgs)
		}
		if(params.refreshPageControl){
			def total = assetChangeService.getAssetRepairCount(company,searchArgs)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def assetRepairListDataStore = {
		def json=[:]
		
		def car = CarCards.createCriteria()
		def house = HouseCards.createCriteria()
		def device = DeviceCards.createCriteria()
		def furniture = FurnitureCards.createCriteria()
		
		def seriesNo
		if(params.seriesNo && params.seriesNo!="" && params.seriesNo!=null){
			seriesNo = params.seriesNo
		}
		
		def freshType
		if(params.freshType && params.freshType!="" && params.freshType!=null){
			freshType = params.freshType
		}
		
		def assetType
		if(params.assetType && params.assetType!="" && params.assetType!=null){
			assetType = params.assetType
		}
		
		def companyId
		if(params.companyId && params.companyId!="" && params.companyId!=null){
			companyId = params.companyId
		}
		def companyEntity = Company.get(companyId)

		def _gridHeader =[]
		_gridHeader << ["name":"序号","width":"30px","colIdx":0,"field":"rowIndex"]
		_gridHeader << ["name":"资产编号","width":"100px","colIdx":1,"field":"registerNum"]
		_gridHeader << ["name":"资产分类","width":"100px","colIdx":2,"field":"userCategory"]
		_gridHeader << ["name":"资产名称","width":"auto","colIdx":3,"field":"assetName"]
		_gridHeader << ["name":"金额（元）","width":"60px","colIdx":4,"field":"onePrice"]
		_gridHeader << ["name":"负责人","width":"60px","colIdx":5,"field":"assetUser"]
		_gridHeader << ["name":"归属部门","width":"100px","colIdx":6,"field":"userDepart"]
		_gridHeader << ["name":"购买日期","width":"80px","colIdx":7,"field":"buyDate"]
		_gridHeader << ["name":"使用状况","width":"60px","colIdx":8,"field":"userStatus"]
		json["gridHeader"] = _gridHeader
		
		int totalNum = 0
		
		def _json = [identifier:'id',label:'name',items:[]]
		int perPageNum = Util.str2int(params.perPageNum)
		int nowPage =  Util.str2int(params.showPageNum)

		def offset = (nowPage-1) * perPageNum
		def max  = perPageNum
		
		def pa=[max:max,offset:offset]
		
		def query = {
			and{
				if(companyId!=null && companyId!=""){
					eq("company",companyEntity)
					like("seriesNo","%"+seriesNo+"%")
				}
			}
		}
		
		def assetList
		if(params.refreshData){
			if(!(assetType.equals("") || assetType==null)){
				if(assetType.equals("car")){//运输工具
					assetList = car.list(pa,query)
					totalNum = CarCards.createCriteria().count(query)
				}else if(assetType.equals("house")){//房屋及建筑物
					assetList = house.list(pa,query)
					totalNum = HouseCards.createCriteria().count(query)
				}else if(assetType.equals("device")){//电子设备
					assetList = device.list(pa,query)
					totalNum = DeviceCards.createCriteria().count(query)
				}else if(assetType.equals("furniture")){//办公家具
					assetList = furniture.list(pa,query)
					totalNum = FurnitureCards.createCriteria().count(query)
				}
			}else{
				assetList = car.list(pa,query)
				totalNum += CarCards.createCriteria().count(query)
				assetList += house.list(pa,query)
				totalNum += HouseCards.createCriteria().count(query)
				assetList += device.list(pa,query)
				totalNum += DeviceCards.createCriteria().count(query)
				assetList += furniture.list(pa,query)
				totalNum += FurnitureCards.createCriteria().count(query)
			}
			
			def idx = 0
			if(offset != null){
				idx = offset
			}
			assetList.each{
				def sMap =[:]
				sMap["rowIndex"] = idx+1
				sMap["id"] = it.id
				sMap["registerNum"] = it.registerNum
				sMap["userCategory"] = it.getCategoryName()
				sMap["assetName"] = it.assetName
				sMap["userStatus"] = it.userStatus
				sMap["onePrice"] = it.onePrice
				sMap["userDepart"] = it.getDepartName()
				sMap["buyDate"] = it.getFormattedShowBuyDate()
				sMap["assetUser"] = it.purchaser
				
				_json.items+=sMap
				idx += 1
			}
			json["gridData"] = _json
		}
			
		if(params.refreshPageControl){
			json["pageControl"] = ["total":totalNum.toString()]
		}
		render json as JSON
	}
	
	def assetChooseDelete = {
		def json,message
		def assetRepair = new AssetRepair()
		
		def carCards
		def houseCards
		def deviceCards
		def furnitureCards
		
		double assetTotal = 0
		double cardsPrice = 0
		def seriesNo
		
		if(params.repairId && !"".equals(params.repairId)){
			assetRepair = AssetRepair.get(params.repairId)
		}
		
		if(params.assetTotal && params.assetTotal !=0){
			assetTotal = params.assetTotal.toDouble()
		}
		
		if(params.seriesNo && params.seriesNo != "" && params.seriesNo != null){
			seriesNo = params.seriesNo
		}
		
		def assetId
		def assetIds
		if(params.assetId && params.assetId!="" && params.assetId!=null){
			assetId = params.assetId
			assetIds = assetId.split(",")
		}
		
		def seriesNo_exist
		def seriesNo_exists = []
		if(assetIds.size()>0){
			assetIds.each{
				//变更资产卡片信息中的申请单号和资产变更类型，同时计算总金额
				
				def entity = this.getEntity(it)
				if(entity){
					entity.assetStatus = "已入库"
					seriesNo_exist = entity.seriesNo
					if(seriesNo_exist != null && seriesNo_exist != ""){//操作号已存在
						seriesNo_exists = seriesNo_exist.split(",").toList()
						if(seriesNo in seriesNo_exists){
							cardsPrice = entity.onePrice
							if(seriesNo_exists.size() == 1){
								entity.seriesNo = null
							}else{
								for(int i=0;i<seriesNo_exists.size();i++){
									if(seriesNo_exists.get(i) == seriesNo){
										seriesNo_exists.remove(i)
										--i
									}
								}
								def seriesNo_new = seriesNo_exists.join(",")
								entity.seriesNo = seriesNo_new
							}
						}else{
							//undo
						}
					}
				}
				
				
				
				
//				carCards = CarCards.get(it)
//				if(carCards){
//					carCards.assetStatus = "已入库"
//					carCards.seriesNo = null
//					cardsPrice = carCards.onePrice
//				}
//				houseCards = HouseCards.get(it)
//				if(houseCards){
//					houseCards.assetStatus = "已入库"
//					houseCards.seriesNo = null
//					cardsPrice = houseCards.onePrice
//				}
//				deviceCards = DeviceCards.get(it)
//				if(deviceCards){
//					deviceCards.assetStatus = "已入库"
//					deviceCards.seriesNo = null
//					cardsPrice = deviceCards.onePrice
//				}
//				furnitureCards = FurnitureCards.get(it)
//				if(furnitureCards){
//					furnitureCards.assetStatus = "已入库"
//					furnitureCards.seriesNo = null
//					cardsPrice = furnitureCards.onePrice
//				}
				assetTotal -= cardsPrice
			}
			message = "操作成功！"
			json = [result:'true',assetTotal:assetTotal,message:message]
		}else{
			message = "操作失败！"
			json = [result:'error',assetTotal:assetTotal,message:message]
		}
		render json as JSON
	}
	
	def assetRepairSaveCheck = {
		def json,message
		def categoryId
		def categoryIds = []
		if(params.categoryId && params.categoryId !=null){
			categoryId = params.categoryId
			categoryIds = categoryId.split(",")
		}
		def seriesNo
		if(params.seriesNo && params.seriesNo != null && params.seriesNo != ""){
			seriesNo = params.seriesNo
		}
		
		def carCards
		def houseCards
		def deviceCards
		def furnitureCards

		def typeArr = []
		carCards = CarCards.findAllBySeriesNoLike("%"+seriesNo+"%")
		if(carCards.size()>0){
			typeArr<<"car"
		}
		houseCards = HouseCards.findAllBySeriesNoLike("%"+seriesNo+"%")
		if(houseCards.size()>0){
			typeArr<<"house"
		}
		deviceCards = DeviceCards.findAllBySeriesNoLike("%"+seriesNo+"%")
		if(deviceCards.size()>0){
			typeArr<<"device"
		}
		furnitureCards = FurnitureCards.findAllBySeriesNoLike("%"+seriesNo+"%")
		if(furnitureCards.size()>0){
			typeArr<<"furniture"
		}
		typeArr = typeArr.unique()
		if(typeArr.size()==1){
			message = "操作成功！"
			json = [result:'true',message:message]
		}else if(typeArr.size()>1){
			message = "注意：资产列表只能为同类型资产！！"
			json = [result:'false',message:message]
		}else{
			message = "操作失败，请联系管理员！"
			json = [result:'error',message:message]
		}
		
		render json as JSON
	}
	
	def assetRepairFlowDeal ={
		def json=[:]
		
		def assetRepair = AssetRepair.get(params.id)
		
		//处理当前人的待办事项
		def currentUser = springSecurityService.getCurrentUser()
		def frontStatus = assetRepair.status
		def nextStatus,nextDepart,nextLogContent
		def nextUsers=[]
		
		//流程引擎相关信息处理-------------------------------------------------------------------------------------
		
		//结束当前任务，并开启下一节点任务
		def map =[:]
		if(params.conditionName){
			map[params.conditionName] = params.conditionValue
		}
		taskService.complete(assetRepair.taskId,map)	//结束当前任务
		
		ProcessInstance processInstance = workFlowService.getProcessIntance(assetRepair.processInstanceId)
		if(!processInstance || processInstance.isEnded()){
			//流程已结束
			nextStatus = "已结束"
			assetRepair.dataStatus = nextStatus
			assetRepair.currentUser = null
			assetRepair.currentDepart = null
			assetRepair.taskId = null
		}else{
			//获取下一节点任务，目前处理串行情况
			def tasks = workFlowService.getTasksByFlow(assetRepair.processInstanceId)
			def task = tasks[0]
			if(task.getDescription() && !"".equals(task.getDescription())){
				nextStatus = task.getDescription()
			}else{
				nextStatus = task.getName()
			}
			assetRepair.taskId = task.getId()
		
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
					def _model = Model.findByModelCodeAndCompany("assetRepair",currentUser.company)
					def authorize = systemService.checkIsAuthorizer(nextUser,_model,new Date())
					if(authorize){
						shareService.addFlowLog(assetRepair.id,"assetRepair",nextUser,"委托授权给【" + authorize.beAuthorizerDepart + ":" + authorize.getFormattedAuthorizer() + "】")
						nextUser = authorize.beAuthorizer
						nextDepart = authorize.beAuthorizerDepart
					}
					//-------------------------------------------------------------------------
					
					//任务指派给当前拟稿人
					taskService.claim(assetRepair.taskId, nextUser.username)
					
					def args = [:]
					args["type"] = "【资产报修】"
					args["content"] = "请您审核编号为  【" + assetRepair.seriesNo +  "】 的资产报修申请信息"
					args["contentStatus"] = nextStatus
					args["contentId"] = assetRepair.id
					args["user"] = nextUser
					args["company"] = nextUser.company
					
					startService.addGtask(args)
					
					assetRepair.currentUser = nextUser
					assetRepair.currentDepart = nextDepart
					
					if(!assetRepair.readers.find{ item->
						item.id.equals(nextUser.id)
					}){
						assetRepair.addToReaders(nextUser)
					}
					nextUsers << nextUser.getFormattedName()
				}
			}
		}
		assetRepair.status = nextStatus
		assetRepair.currentDealDate = new Date()
		//处理资产报修状态
		assetRepair.dataStatus = nextStatus
		
		//判断下一处理人是否与当前处理人员为同一人
		if(currentUser.equals(assetRepair.currentUser)){
			json["refresh"] = true
		}
		
		//----------------------------------------------------------------------------------------------------
		
		//修改代办事项状态
		def gtask = Gtask.findWhere(
			user:currentUser,
			company:currentUser.company,
			contentId:assetRepair.id,
			contentStatus:frontStatus,
			status:"0"
		)
		if(gtask!=null){
			gtask.dealDate = new Date()
			gtask.status = "1"
			gtask.save(flush:true)
		}
		
		if(assetRepair.save(flush:true)){
			//添加日志
			def logContent
			switch (true){
				case assetRepair.status.contains("已结束"):
					logContent = "结束流程"
					
					//2015-3-25-------修改相关卡片信息以及增加日志---------
					CarCards.createCriteria().list{
						eq("company",assetRepair.company)
						like("seriesNo","%"+assetRepair.seriesNo+"%")
					}.each{
					
						def chgLog = new ChangeLog()
						chgLog.changeId = assetRepair.id
						chgLog.cardId = it.id
						chgLog.changeType = "报修"
						chgLog.cardType = "车辆"
						chgLog.company = assetRepair.company
						chgLog.save()
						
					}
					DeviceCards.createCriteria().list{
						eq("company",assetRepair.company)
						like("seriesNo","%"+assetRepair.seriesNo+"%")
					}.each{
					
						def chgLog = new ChangeLog()
						chgLog.changeId = assetRepair.id
						chgLog.cardId = it.id
						chgLog.changeType = "报修"
						chgLog.cardType = "电子设备"
						chgLog.company = assetRepair.company
						chgLog.save()
					}
					FurnitureCards.createCriteria().list{
						eq("company",assetRepair.company)
						like("seriesNo","%"+assetRepair.seriesNo+"%")
					}.each{
						def chgLog = new ChangeLog()
						chgLog.changeId = assetRepair.id
						chgLog.cardId = it.id
						chgLog.changeType = "报修"
						chgLog.cardType = "办公家具"
						chgLog.company = assetRepair.company
						chgLog.save()
					}
					HouseCards.createCriteria().list{
						eq("company",assetRepair.company)
						like("seriesNo","%"+assetRepair.seriesNo+"%")
					}.each{
						
						def chgLog = new ChangeLog()
						chgLog.changeId = assetRepair.id
						chgLog.cardId = it.id
						chgLog.changeType = "报修"
						chgLog.cardType = "房屋及建筑物"
						chgLog.company = assetRepair.company
						chgLog.save()
					}
					
					break
				case assetRepair.status.contains("归档"):
					logContent = "归档"
					break
				case assetRepair.status.contains("不同意"):
					logContent = "不同意！"
					break
				default:
					logContent = "提交" + assetRepair.status + "【" + nextUsers.join("、") + "】"
					break
			}
			shareService.addFlowLog(assetRepair.id,"assetRepair",currentUser,logContent)
			json["nextUserName"] = nextUsers.join("、")
			json["result"] = true
		}else{
			assetRepair.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	
	def assetRepairFlowBack ={
		def json=[:]
		def assetRepair = AssetRepair.get(params.id)
		
		def currentUser = springSecurityService.getCurrentUser()
		def frontStatus = assetRepair.status
		
		try{
			//获取上一处理任务
			def frontTaskList = workFlowService.findBackAvtivity(assetRepair.taskId)
			if(frontTaskList && frontTaskList.size()>0){
				//简单的取最近的一个节点
				def activityEntity = frontTaskList[frontTaskList.size()-1]
				def activityId = activityEntity.getId();
				
				//流程跳转
				workFlowService.backProcess(assetRepair.taskId, activityId, null)
				
				//获取下一节点任务，目前处理串行情况
				def nextStatus
				def tasks = workFlowService.getTasksByFlow(assetRepair.processInstanceId)
				def task = tasks[0]
				if(task.getDescription() && !"".equals(task.getDescription())){
					nextStatus = task.getDescription()
				}else{
					nextStatus = task.getName()
				}
				assetRepair.taskId = task.getId()
				
				//获取对应节点的处理人员以及相关状态
				def historyActivity = workFlowService.getHistrotyActivityByActivity(assetRepair.taskId,activityId)
				def user = User.findByUsername(historyActivity.getAssignee())
				
				//任务指派给当前拟稿人
				taskService.claim(assetRepair.taskId, user.username)
				
				//增加待办事项
				def args = [:]
				args["type"] = "【资产报修】"
				args["content"] = "编号为  【" + assetRepair.seriesNo +  "】 的资产报修申请信息被退回，请查看！"
				args["contentStatus"] = nextStatus
				args["contentId"] = assetRepair.id
				args["user"] = user
				args["company"] = user.company
				
				startService.addGtask(args)
					
				//修改相关信息
				assetRepair.currentUser = user
				assetRepair.currentDepart = user.getDepartName()
				assetRepair.currentDealDate = new Date()
				assetRepair.status = nextStatus
				//处理资产报修状态
				assetRepair.dataStatus = nextStatus
				
				//判断下一处理人是否与当前处理人员为同一人
				if(currentUser.equals(assetRepair.currentUser)){
					json["refresh"] = true
				}
				
				//----------------------------------------------------------------------------------------------------
				
				//修改代办事项状态
				def gtask = Gtask.findWhere(
					user:currentUser,
					company:currentUser.company,
					contentId:assetRepair.id,
					contentStatus:frontStatus,
					status:"0"
				)
				if(gtask!=null){
					gtask.dealDate = new Date()
					gtask.status = "1"
					gtask.save(flush:true)
				}
				
				assetRepair.save(flush:true)
				
				//添加日志
				def logContent = "退回【" + user.getFormattedName() + "】"
				
				shareService.addFlowLog(assetRepair.id,"assetRepair",currentUser,logContent)
				json["nextUserName"] = user?.getFormattedName()
			}
				
			json["result"] = true
		}catch(Exception e){
			json["result"] = false
		}
		render json as JSON
	}
	
	def assetRepairExport = {
		OutputStream os = response.outputStream
		def company = Company.get(params.companyId)
		response.setContentType('application/vnd.ms-excel')
		response.setHeader("Content-disposition", "attachment; filename=" + new String("资产报修申请信息.xls".getBytes("GB2312"), "ISO_8859_1"))
		
		//增加查询条件
		def searchArgs =[:]
		
		if(params.seriesNo && !"".equals(params.seriesNo)) searchArgs["seriesNo"] = params.seriesNo
		def usedDepartList = []
		if(params.usedDepart && !"".equals(params.usedDepart)){
			params.usedDepart.split(",").each{
				def _list = []
				usedDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
			}
			searchArgs["usedDepart"] = usedDepartList.unique()
		}
//		if(params.usedDepart && !"".equals(params.usedDepart)) searchArgs["usedDepart"] = Depart.findByCompanyAndDepartName(company,params.usedDepart)
		if(params.usedMan && !"".equals(params.usedMan)) searchArgs["usedMan"] = params.usedMan
		
		def c = AssetRepair.createCriteria()

		def assetRepairList = c.list{
			eq("company",company)
//			eq("status","已结束")
			searchArgs.each{k,v->
				if(k.equals("usedDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		def excel = new ExcelExport()
		excel.assetRepairDc(os,assetRepairList)
	}
	
	private def getEntity ={entityId->
		def entity = CarCards.get(entityId)
		if(!entity){
			entity = HouseCards.get(entityId)
			if(!entity){
				entity = DeviceCards.get(entityId)
				if(!entity){
					entity = FurnitureCards.get(entityId)
				}
			}
		}
		return entity
	}
}
