package com.rosten.app.assetChange

import grails.converters.JSON

import com.rosten.app.assetCards.CarCards
import com.rosten.app.assetCards.LandCards
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.assetCards.BookCards
import com.rosten.app.assetCards.FurnitureCards
import com.rosten.app.assetChange.AssetAllocate

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

class AssetAllocateController {
	def assetCardsService
    def assetChangeService
	def springSecurityService
	def workFlowService
	def taskService
	def systemService
	def shareService
	def startService

	def imgPath ="images/rosten/actionbar/"
	
	def assetAllocateForm = {
		def webPath = request.getContextPath() + "/"
		def strname = "assetAllocate"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
		if(params.id){
			def entity = AssetAllocate.get(params.id)
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
	
	def assetAllocateView = {
		def actionList =[]
		def strname = "assetAllocate"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		//增加资产管理员群组的控制权限
		def currentUser = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(currentUser).collect { elem ->
		  elem.group.groupName
		}
		if("zcgly" in userGroups || "xhzcgly" in userGroups || "admin".equals(currentUser.getUserType())){
			actionList << createAction("资产调拨",imgPath + "add.png",strname + "_add")
			actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
			actionList << createAction("打印",imgPath + "word_print.png",strname + "_print")
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
	
	def assetAllocateSearchView ={
		def model =[:]
		
		def company = Company.get(params.companyId)
		model["company"] = company
//		model["DepartList"] = Depart.findAllByCompany(company)
		
		render(view:'/assetChange/assetAllocateSearch',model:model)
	}
	
	def assetAllocateAdd = {
		if(params.flowCode){
			//需要走流程
			def company = Company.get(params.companyId)
			def flowBusiness = FlowBusiness.findByFlowCodeAndCompany(params.flowCode,company)
			if(flowBusiness && !"".equals(flowBusiness.relationFlow)){
				params.relationFlow = flowBusiness.relationFlow
				redirect(action:"assetAllocateShow",params:params)
			}else{
				//不存在流程引擎关联数据
				render '<h2 style="color:red;width:660px;margin:0 auto;margin-top:60px">当前业务不存在流程设置，无法创建，请联系管理员！</h2>'
			}
		}else{
			redirect(action:"assetAllocateShow",params:params)
		}
	}
	
	def assetAllocateShow = {
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
//		def currentDepart = Depart.findByDepartName(currentUser.getDepartName())
//		def isSubDepart = currentDepart.isSubDepart
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def currentDepart
		def isSubDepart
		def assetAllocate = new AssetAllocate()
		def drafter
		def isAllowedEdit
		if(params.id){
			assetAllocate = AssetAllocate.get(params.id)
			currentDepart = currentUser.getDepartEntity()
			isSubDepart = currentDepart?.isSubDepart
			//判断用户是否一致，若一致且未提交状态，则可编辑 
			drafter = assetAllocate.drafter
			if(user.equals(drafter) && assetAllocate.dataStatus.equals("新建")){
				isAllowedEdit = "yes"
			}else{
				isAllowedEdit = "no"
			}
		}else{
			isAllowedEdit = "new"
		}
		model["user"] = currentUser
		model["company"] = company
		model["currentDepart"] = currentDepart
		model["isSubDepart"] = isSubDepart
		model["assetAllocate"] = assetAllocate
		model["isAllowedEdit"] = isAllowedEdit
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		//流程相关信息----------------------------------------------
		model["relationFlow"] = params.relationFlow
		model["flowCode"] = params.flowCode
		//------------------------------------------------------
		
		render(view:'/assetChange/assetAllocate',model:model)
	}
	
	def assetAllocateSave = {
		def json=[:]
		def company = Company.get(params.companyId)
		def currentUser = springSecurityService.getCurrentUser()
		
		//资产调拨申请信息保存-------------------------------
		def assetAllocate = new AssetAllocate()
		if(params.id && !"".equals(params.id)){
			assetAllocate = AssetAllocate.get(params.id)
		}else{
			assetAllocate.company = company
		}
		assetAllocate.properties = params
		assetAllocate.clearErrors()
		
		//特殊字段信息处理
		assetAllocate.applyDate = Util.convertToTimestamp(params.applyDate)
		if(params.originalDepartId && !params.originalDepartId.equals("")){
			assetAllocate.originalDepart = Depart.get(params.originalDepartId)
		}else{
			assetAllocate.originalDepart = params.originalDepartName
		}
		if(params.newDepartId && !params.newDepartId.equals("")){
			assetAllocate.newDepart = Depart.get(params.newDepartId)
		}else{
			assetAllocate.newDepart = params.newDepartName
		}
		if(!params.seriesNo_form.equals("")){
			assetAllocate.seriesNo = params.seriesNo_form
		}
		assetAllocate.dataStatus = assetAllocate.status
		
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
					entity.purchaser = params.newUser
					entity.assetStatus = "已调拨"
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
				assetAllocate.currentUser = currentUser
				assetAllocate.currentDepart = currentUser.getDepartName()
				assetAllocate.currentDealDate = new Date()
				
				assetAllocate.drafter = currentUser
				assetAllocate.drafterDepart = currentUser.getDepartName()
			}
			
			//增加读者域
			if(!assetAllocate.readers.find{ it.id.equals(currentUser.id) }){
				assetAllocate.addToReaders(currentUser)
			}
			
			//流程引擎相关信息处理-------------------------------------------------------------------------------------
			if(!assetAllocate.processInstanceId){
				//启动流程实例
				def _processInstance = workFlowService.getProcessDefinition(params.relationFlow)
				Map<String, Object> variables = new HashMap<String, Object>();
				ProcessInstance processInstance = workFlowService.addFlowInstance(_processInstance.key, currentUser.username,assetAllocate.id, variables);
				assetAllocate.processInstanceId = processInstance.getProcessInstanceId()
				assetAllocate.processDefinitionId = processInstance.getProcessDefinitionId()
				
				//获取下一节点任务
				def task = workFlowService.getTasksByFlow(processInstance.getProcessInstanceId())[0]
				assetAllocate.taskId = task.getId()
			}
			//-------------------------------------------------------------------------------------------------
		}
		
		
		if(assetAllocate.save(flush:true)){
			json["result"] = "true"
			json["id"] = assetAllocate.id
		}else{
			assetAllocate.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def assetAllocateDelete = {
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def assetAllocate = AssetAllocate.get(it)
				if(assetAllocate){
					assetAllocate.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetAllocateGrid = {
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetChangeService.getAssetAllocateListLayout()
		}
		
		//增加查询条件
		def searchArgs =[:]
		
		if(params.seriesNo && !"".equals(params.seriesNo)) searchArgs["seriesNo"] = params.seriesNo
		
		def originalDepartList = []
		if(params.originalDepart && !"".equals(params.originalDepart)){
			params.originalDepart.split(",").each{
				def _list = []
				originalDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
			}
			searchArgs["originalDepart"] = originalDepartList.unique()
		}
//		if(params.originalDepart && !"".equals(params.originalDepart)) searchArgs["originalDepart"] = Depart.findByCompanyAndDepartName(company,params.originalDepart)
		def newDepartList = []
		if(params.newDepart && !"".equals(params.newDepart)){
			params.newDepart.split(",").each{
				def _list = []
				newDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
			}
			searchArgs["newDepart"] = newDepartList.unique()
		}
//		if(params.newDepart && !"".equals(params.newDepart)) searchArgs["newDepart"] = Depart.findByCompanyAndDepartName(company,params.newDepart)
		if(params.newUser && !"".equals(params.newUser)) searchArgs["newUser"] = params.newUser
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetChangeService.getAssetAllocateDataStore(args,searchArgs)
		}
		if(params.refreshPageControl){
			def total = assetChangeService.getAssetAllocateCount(company,searchArgs)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def assetAllocateListDataStore = {
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
			if(offset!=null){
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
		def assetAllocate = new AssetAllocate()
		
		def carCards
		def houseCards
		def deviceCards
		def furnitureCards
		
		double assetTotal = 0
		double cardsPrice = 0
		def originalUser
		def seriesNo
		
		if(params.allocateId && !"".equals(params.allocateId)){
			assetAllocate = AssetAllocate.get(params.allocateId)
		}
		
		if(params.assetTotal && params.assetTotal !=0){
			assetTotal = params.assetTotal.toDouble()
		}
		
		if(params.originalUser && params.originalUser != "" && params.originalUser != null){
			originalUser = params.originalUser
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
				//变更资产建账信息中的申请单号和资产变更类型，同时计算总金额
				
				def entity = this.getEntity(it)
				if(entity){
					entity.assetStatus = "已入库"
					entity.purchaser = originalUser
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
	
	def assetAllocateSaveCheck = {
		def json,message
//		def categoryId
//		def categoryIds = []
//		if(params.categoryId && params.categoryId !=null){
//			categoryId = params.categoryId
//			categoryIds = categoryId.split(",")
//		}
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
	
	def assetAllocateFlowDeal ={
		def json=[:]
		
		def assetAllocate = AssetAllocate.get(params.id)
		//处理当前人的待办事项
		def currentUser = springSecurityService.getCurrentUser()
		def frontStatus = assetAllocate.status
		def nextStatus,nextDepart,nextLogContent
		def nextUsers=[]
		
		//流程引擎相关信息处理-------------------------------------------------------------------------------------
		
		//结束当前任务，并开启下一节点任务
		def map =[:]
		if(params.conditionName){
			map[params.conditionName] = params.conditionValue
		}
		taskService.complete(assetAllocate.taskId,map)	//结束当前任务
		
		ProcessInstance processInstance = workFlowService.getProcessIntance(assetAllocate.processInstanceId)
		if(!processInstance || processInstance.isEnded()){
			//流程已结束
			nextStatus = "已结束"
			assetAllocate.dataStatus = nextStatus
			assetAllocate.currentUser = null
			assetAllocate.currentDepart = null
			assetAllocate.taskId = null
		}else{
			//获取下一节点任务，目前处理串行情况
			def tasks = workFlowService.getTasksByFlow(assetAllocate.processInstanceId)
			def task = tasks[0]
			if(task.getDescription() && !"".equals(task.getDescription())){
				nextStatus = task.getDescription()
			}else{
				nextStatus = task.getName()
			}
			assetAllocate.taskId = task.getId()
		
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
					def _model = Model.findByModelCodeAndCompany("assetAllocate",currentUser.company)
					def authorize = systemService.checkIsAuthorizer(nextUser,_model,new Date())
					if(authorize){
						shareService.addFlowLog(assetAllocate.id,"assetAllocate",nextUser,"委托授权给【" + authorize.beAuthorizerDepart + ":" + authorize.getFormattedAuthorizer() + "】")
						nextUser = authorize.beAuthorizer
						nextDepart = authorize.beAuthorizerDepart
					}
					//-------------------------------------------------------------------------
					
					//任务指派给当前拟稿人
					taskService.claim(assetAllocate.taskId, nextUser.username)
					
					def args = [:]
					args["type"] = "【资产调拨】"
					args["content"] = "请您审核编号为  【" + assetAllocate.seriesNo +  "】 的资产调拨申请信息"
					args["contentStatus"] = nextStatus
					args["contentId"] = assetAllocate.id
					args["user"] = nextUser
					args["company"] = nextUser.company
					
					startService.addGtask(args)
					
					assetAllocate.currentUser = nextUser
					assetAllocate.currentDepart = nextDepart
					
					if(!assetAllocate.readers.find{ item->
						item.id.equals(nextUser.id)
					}){
						assetAllocate.addToReaders(nextUser)
					}
					nextUsers << nextUser.getFormattedName()
				}
			}
		}
		assetAllocate.status = nextStatus
		assetAllocate.currentDealDate = new Date()
		//处理资产调拨状态
		assetAllocate.dataStatus = nextStatus
		
		//判断下一处理人是否与当前处理人员为同一人
		if(currentUser.equals(assetAllocate.currentUser)){
			json["refresh"] = true
		}
		
		//----------------------------------------------------------------------------------------------------
		
		//修改代办事项状态
		def gtask = Gtask.findWhere(
			user:currentUser,
			company:currentUser.company,
			contentId:assetAllocate.id,
			contentStatus:frontStatus,
			status:"0"
		)
		if(gtask!=null){
			gtask.dealDate = new Date()
			gtask.status = "1"
			gtask.save(flush:true)
		}
		
		if(assetAllocate.save(flush:true)){
			//添加日志
			def logContent
			switch (true){
				case assetAllocate.status.contains("已结束"):
					logContent = "结束流程"
					break
				case assetAllocate.status.contains("归档"):
					logContent = "归档"
					break
				case assetAllocate.status.contains("不同意"):
					logContent = "不同意！"
					break
				default:
					logContent = "提交" + assetAllocate.status + "【" + nextUsers.join("、") + "】"
					break
			}
			shareService.addFlowLog(assetAllocate.id,"assetAllocate",currentUser,logContent)
			json["nextUserName"] = nextUsers.join("、")
			json["result"] = true
		}else{
			assetAllocate.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	
	def assetAllocateFlowBack ={
		def json=[:]
		def assetAllocate = AssetAllocate.get(params.id)
		
		def currentUser = springSecurityService.getCurrentUser()
		def frontStatus = assetAllocate.status
		
		try{
			//获取上一处理任务
			def frontTaskList = workFlowService.findBackAvtivity(assetAllocate.taskId)
			if(frontTaskList && frontTaskList.size()>0){
				//简单的取最近的一个节点
				def activityEntity = frontTaskList[frontTaskList.size()-1]
				def activityId = activityEntity.getId();
				
				//流程跳转
				workFlowService.backProcess(assetAllocate.taskId, activityId, null)
				
				//获取下一节点任务，目前处理串行情况
				def nextStatus
				def tasks = workFlowService.getTasksByFlow(assetAllocate.processInstanceId)
				def task = tasks[0]
				if(task.getDescription() && !"".equals(task.getDescription())){
					nextStatus = task.getDescription()
				}else{
					nextStatus = task.getName()
				}
				assetAllocate.taskId = task.getId()
				
				//获取对应节点的处理人员以及相关状态
				def historyActivity = workFlowService.getHistrotyActivityByActivity(assetAllocate.taskId,activityId)
				def user = User.findByUsername(historyActivity.getAssignee())
				
				//任务指派给当前拟稿人
				taskService.claim(assetAllocate.taskId, user.username)
				
				//增加待办事项
				def args = [:]
				args["type"] = "【资产调拨】"
				args["content"] = "编号为  【" + assetAllocate.seriesNo +  "】 的资产调拨申请信息被退回，请查看！"
				args["contentStatus"] = nextStatus
				args["contentId"] = assetAllocate.id
				args["user"] = user
				args["company"] = user.company
				
				startService.addGtask(args)
					
				//修改相关信息
				assetAllocate.currentUser = user
				assetAllocate.currentDepart = user.getDepartName()
				assetAllocate.currentDealDate = new Date()
				assetAllocate.status = nextStatus
				//处理资产调拨状态
				assetAllocate.dataStatus = nextStatus
				
				//判断下一处理人是否与当前处理人员为同一人
				if(currentUser.equals(assetAllocate.currentUser)){
					json["refresh"] = true
				}
				
				//----------------------------------------------------------------------------------------------------
				
				//修改代办事项状态
				def gtask = Gtask.findWhere(
					user:currentUser,
					company:currentUser.company,
					contentId:assetAllocate.id,
					contentStatus:frontStatus,
					status:"0"
				)
				if(gtask!=null){
					gtask.dealDate = new Date()
					gtask.status = "1"
					gtask.save(flush:true)
				}
				
				assetAllocate.save(flush:true)
				
				//添加日志
				def logContent = "退回【" + user.getFormattedName() + "】"
				
				shareService.addFlowLog(assetAllocate.id,"assetAllocate",currentUser,logContent)
				json["nextUserName"] = user?.getFormattedName()
			}
				
			json["result"] = true
		}catch(Exception e){
			json["result"] = false
		}
		render json as JSON
	}
	
	def assetAllocateExport = {
		OutputStream os = response.outputStream
		def company = Company.get(params.companyId)
		response.setContentType('application/vnd.ms-excel')
		response.setHeader("Content-disposition", "attachment; filename=" + new String("资产调拨申请信息.xls".getBytes("GB2312"), "ISO_8859_1"))
		
		//增加查询条件
		def searchArgs =[:]
		
		if(params.seriesNo && !"".equals(params.seriesNo)) searchArgs["seriesNo"] = params.seriesNo
		
		def originalDepartList = []
		if(params.originalDepart && !"".equals(params.originalDepart)){
			params.originalDepart.split(",").each{
				def _list = []
				originalDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
			}
			searchArgs["originalDepart"] = originalDepartList.unique()
		}
		
		def newDepartList = []
		if(params.newDepart && !"".equals(params.newDepart)){
			params.newDepart.split(",").each{
				def _list = []
				newDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
			}
			searchArgs["newDepart"] = newDepartList.unique()
		}
//		if(params.originalDepart && !"".equals(params.originalDepart)) searchArgs["originalDepart"] = Depart.findByCompanyAndDepartName(company,params.originalDepart)
//		if(params.newDepart && !"".equals(params.newDepart)) searchArgs["newDepart"] = Depart.findByCompanyAndDepartName(company,params.newDepart)
		if(params.newUser && !"".equals(params.newUser)) searchArgs["newUser"] = params.newUser
		
		def c = AssetAllocate.createCriteria()

		def assetAllocateList = c.list{
			
			eq("company",company)
//			eq("status","已结束")
			searchArgs.each{k,v->
				if(k.equals("originalDepart") || k.equals("newDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		def excel = new ExcelExport()
		excel.assetAllocateDc(os,assetAllocateList)
	}
	
	def assetAllocatePrint = {
		def word = new WordExport()
		def ids = params.id.split(",")
		if(null != ids && ids.length > 0){
			if(ids.length == 1){
				def assetAllocate = AssetAllocate.get(params.id)
				def map =[:]
				map["assetAllocate"] = assetAllocate
				def seriesNo = assetAllocate.seriesNo
				
				def acCount = 0
				def acRow = []
				def acList
				def assetName = ""
				def registerNum = ""
				def specifications = ""
				def acCarList = CarCards.findAllBySeriesNoLike("%"+seriesNo+"%")
				def acHouseList = HouseCards.findAllBySeriesNoLike("%"+seriesNo+"%")
				def acDeviceList = DeviceCards.findAllBySeriesNoLike("%"+seriesNo+"%")
				def acFurnitureList = FurnitureCards.findAllBySeriesNoLike("%"+seriesNo+"%")
				if(acCarList.size() != 0){
					acCount = acCarList.size()
					if(acCount == 1){
						acRow = acCarList[0]
					}
				}else if(acHouseList.size() != 0){
					acCount = acHouseList.size()
					if(acCount == 1){
						acRow = acCarList[0]
					}
				}else if(acDeviceList.size() != 0){
					acCount = acDeviceList.size()
					if(acCount == 1){
						acRow = acCarList[0]
					}
				}else if(acFurnitureList.size() != 0){
					acCount = acFurnitureList.size()
					if(acCount == 1){
						acRow = acCarList[0]
					}
				}
				map["acCount"] = acCount
				acRow.each{
					assetName =  it.assetName
					registerNum = it.registerNum
					specifications = it.specifications
				}
				map["acRow"] = [assetName:assetName,registerNum:registerNum,specifications:specifications]
				
				//所有意见默认取最后一次意见
				//获取后勤意见
				def hqyj = shareService.getCommentByStatus(assetAllocate.id,"后勤部领导审核")
				if(hqyj && hqyj.size()>0){
					map["hqComment"]= hqyj[0]
				}else{
					map["hqComment"] = [content:"",date:"",name:""]
				}
				
				//获取财务意见
				def cwyj = shareService.getCommentByStatus(assetAllocate.id,"财务部审核")
				if(cwyj && cwyj.size()>0){
					map["cwComment"]= cwyj[0]
				}else{
					map["cwComment"] = [content:"",date:"",name:""]
				}
				
				//获取秘书长意见
				def mszyj = shareService.getCommentByStatus(assetAllocate.id,"秘书长审批")
				if(mszyj && mszyj.size()>0){
					map["mszComment"]= mszyj[0]
				}else{
					map["mszComment"] = [content:"",date:"",name:""]
				}
				
				word.singlePrint(response,"zcdbd.xml",assetAllocate.seriesNo,map)
			}else{
//				word.downloadZzkhbZip(response,params.id)
			}
		}
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
