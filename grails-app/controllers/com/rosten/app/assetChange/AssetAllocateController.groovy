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
import com.rosten.app.util.Util
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Depart

import com.rosten.app.util.GridUtil
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
					case entity.status.contains("审核") || entity.status.contains("审批"):
						actionList << createAction("填写意见",webPath +imgPath + "sign.png",strname + "_addComment")
						actionList << createAction("同意",webPath +imgPath + "ok.png",strname + "_submit")
						actionList << createAction("退回",webPath +imgPath + "back.png",strname + "_back")
						break;
					default :
						actionList << createAction("保存",webPath +imgPath + "Save.gif",strname + "_save")
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
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除",imgPath + "read.gif",strname + "_delete")
		actionList << createAction("导出",imgPath + "read.gif",strname + "_export")
		
		render actionList as JSON
	}

	private def createAction = {name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
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
		def currentDepart = currentUser.getDepartName()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def assetAllocate = new AssetAllocate()
		if(params.id){
			assetAllocate = AssetAllocate.get(params.id)
		}
		
		model["user"] = currentUser
		model["company"] = company
		model["currentDepart"] = currentDepart
		model["assetAllocate"] = assetAllocate
		
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
		if(params.callOutDeptId && !params.callOutDeptId.equals("")){
			assetAllocate.callOutDept = Depart.get(params.callOutDeptId)
		}else{
			assetAllocate.callOutDept = params.callOutDeptName
		}
		if(params.callInDeptId && !params.callInDeptId.equals("")){
			assetAllocate.callInDept = Depart.get(params.callInDeptId)
		}else{
			assetAllocate.callInDept = params.callInDeptName
		}
		if(!params.seriesNo_form.equals("")){
			assetAllocate.seriesNo = params.seriesNo_form
		}
		
		/**
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
		*/
		
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
		/*
		def seriesNo
		
		def carCards
		def landCards
		def houseCards
		def deviceCards
		def bookCards
		def furnitureCards
		
		def assetList_car
		def assetList_land
		def assetList_house
		def assetList_device
		def assetList_book
		def assetList_furniture
		*/
		
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def assetAllocate = AssetAllocate.get(it)
				if(assetAllocate){
					assetAllocate.delete(flush: true)
					/*
					//获取当前资产变更申请单号，用以重置资产卡片中的值
					seriesNo = assetAllocate.seriesNo
					//重置车辆资产卡片的资产变更申请单号
					assetList_car = CarCards.findAllBySeriesNo(seriesNo)
					if(assetList_car.size()>0){
						assetList_car.each{
							def assetId = it.id
							carCards = CarCards.get(assetId)
							if(carCards){
								carCards.seriesNo = null
							}
						}
					}
					//重置土地资产卡片的资产变更申请单号
					assetList_land = LandCards.findAllBySeriesNo(seriesNo)
					if(assetList_land.size()>0){
						assetList_land.each{
							def assetId = it.id
							landCards = LandCards.get(assetId)
							if(landCards){
								landCards.seriesNo = null
							}
						}
					}
					//重置房屋资产卡片的资产变更申请单号
					assetList_house = HouseCards.findAllBySeriesNo(seriesNo)
					if(assetList_house.size()>0){
						assetList_house.each{
							def assetId = it.id
							houseCards = HouseCards.get(assetId)
							if(houseCards){
								houseCards.seriesNo = null
							}
						}
					}
					//重置设备资产卡片的资产变更申请单号
					assetList_device = DeviceCards.findAllBySeriesNo(seriesNo)
					if(assetList_device.size()>0){
						assetList_device.each{
							def assetId = it.id
							deviceCards = DeviceCards.get(assetId)
							if(deviceCards){
								deviceCards.seriesNo = null
							}
						}
					}
					//重置图书资产卡片的资产变更申请单号
					assetList_book = BookCards.findAllBySeriesNo(seriesNo)
					if(assetList_book.size()>0){
						assetList_book.each{
							def assetId = it.id
							bookCards = BookCards.get(assetId)
							if(bookCards){
								bookCards.seriesNo = null
							}
						}
					}
					//重置家具资产卡片的资产变更申请单号
					assetList_furniture = FurnitureCards.findAllBySeriesNo(seriesNo)
					if(assetList_furniture.size()>0){
						assetList_furniture.each{
							def assetId = it.id
							furnitureCards = FurnitureCards.get(assetId)
							if(furnitureCards){
								furnitureCards.seriesNo = null
							}
						}
					}
					*/
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
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetChangeService.getAssetAllocateDataStore(args)
		}
		if(params.refreshPageControl){
			def total = assetChangeService.getAssetAllocateCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def assetAllocateListDataStore = {
		def json=[:]
		
		def car = CarCards.createCriteria()
		def land = LandCards.createCriteria()
		def house = HouseCards.createCriteria()
		def device = DeviceCards.createCriteria()
		def book = BookCards.createCriteria()
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
		_gridHeader << ["name":"序号","width":"40px","colIdx":0,"field":"rowIndex"]
		_gridHeader << ["name":"资产编号","width":"120px","colIdx":1,"field":"registerNum"]
		_gridHeader << ["name":"资产分类","width":"100px","colIdx":2,"field":"userCategory"]
		_gridHeader << ["name":"资产名称","width":"auto","colIdx":3,"field":"assetName"]
		_gridHeader << ["name":"使用状况","width":"80px","colIdx":4,"field":"userStatus"]
		_gridHeader << ["name":"金额","width":"80px","colIdx":5,"field":"onePrice"]
		_gridHeader << ["name":"使用部门","width":"100px","colIdx":6,"field":"userDepart"]
		_gridHeader << ["name":"购买日期","width":"80px","colIdx":7,"field":"buyDate"]
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
					eq("seriesNo",seriesNo)
				}
			}
		}
		
		def assetList
		if(params.refreshData){
			if(!(assetType.equals("") || assetType==null)){
				if(assetType.equals("car")){
					assetList = car.list(pa,query)
					totalNum = assetList.size()
				}else if(assetType.equals("land")){
					assetList = land.list(pa,query)
					totalNum = assetList.size()
				}else if(assetType.equals("house")){
					assetList = house.list(pa,query)
					totalNum = assetList.size()
				}else if(assetType.equals("device")){
					assetList = device.list(pa,query)
					totalNum = assetList.size()
				}else if(assetType.equals("book")){
					assetList = book.list(pa,query)
					totalNum = assetList.size()
				}else if(assetType.equals("furniture")){
					assetList = furniture.list(pa,query)
					totalNum = assetList.size()
				}
			}else{
				assetList = car.list(pa,query)
				assetList += land.list(pa,query)
				assetList += house.list(pa,query)
				assetList += device.list(pa,query)
				assetList += book.list(pa,query)
				assetList += furniture.list(pa,query)
				totalNum = assetList.size()
			}
			
			def idx = 0
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
	
	def assetChooseListDataStore = {
		def car = CarCards.createCriteria()
		def land = LandCards.createCriteria()
		def house = HouseCards.createCriteria()
		def device = DeviceCards.createCriteria()
		def book = BookCards.createCriteria()
		def furniture = FurnitureCards.createCriteria()
		
		def json=[:]
		
		def seriesNo
		if(params.seriesNo && params.seriesNo!="" && params.seriesNo!=null){
			seriesNo = params.seriesNo
		}
		
		def freshType
		if(params.freshType && params.freshType!="" && params.freshType!=null){
			freshType = params.freshType
		}
		
		def assetType = "car"
		if(params.assetType && params.assetType!="" && params.assetType!=null){
			assetType = params.assetType
		}
		
		def companyId
		if(params.companyId && params.companyId!="" && params.companyId!=null){
			companyId = params.companyId
		}
		def companyEntity = Company.get(companyId)

		def _gridHeader =[]
		_gridHeader << ["name":"序号","width":"40px","colIdx":0,"field":"rowIndex"]
		_gridHeader << ["name":"资产编号","width":"120px","colIdx":1,"field":"registerNum"]
		_gridHeader << ["name":"资产分类","width":"100px","colIdx":2,"field":"userCategory"]
		_gridHeader << ["name":"资产名称","width":"auto","colIdx":3,"field":"assetName"]
		_gridHeader << ["name":"使用状况","width":"80px","colIdx":4,"field":"userStatus"]
		_gridHeader << ["name":"金额","width":"80px","colIdx":5,"field":"onePrice"]
		_gridHeader << ["name":"使用部门","width":"100px","colIdx":6,"field":"userDepart"]
		_gridHeader << ["name":"购买日期","width":"80px","colIdx":7,"field":"buyDate"]
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
					eq("assetStatus","已入库")
				}
			}
		}
		
		def assetList
		if(params.refreshData){
			if(!(assetType.equals("") || assetType==null)){
				if(assetType.equals("car")){
					assetList = car.list(pa,query)
					totalNum = assetList.size()
				}else if(assetType.equals("land")){
					assetList = land.list(pa,query)
					totalNum = assetList.size()
				}else if(assetType.equals("house")){
					assetList = house.list(pa,query)
					totalNum = assetList.size()
				}else if(assetType.equals("device")){
					assetList = device.list(pa,query)
					totalNum = assetList.size()
				}else if(assetType.equals("book")){
					assetList = book.list(pa,query)
					totalNum = assetList.size()
				}else if(assetType.equals("furniture")){
					assetList = furniture.list(pa,query)
					totalNum = assetList.size()
				}
			}
			
			def idx = 0
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
	
	def assetChooseOperate = {
		def json,message
		
		def seriesNo
		if(params.seriesNo && params.seriesNo!=""){
			seriesNo = params.seriesNo
		}
		
		def assetType
		if(params.assetType && params.assetType!=""){
			assetType = params.assetType
		}
		
		def assetId
		def assetIds
		if(params.assetId && params.assetId!="" && params.assetId!=null){
			assetId = params.assetId
			assetIds = assetId.split(",")
		}
		
		def nowTotalPrice
		double assetTotal = 0
		if(params.assetTotal && params.assetTotal!=""){
			nowTotalPrice = params.assetTotal.replace("-",".").toDouble()
			assetTotal = nowTotalPrice
			
		}
		double totalPrice = 0
		
		def carCards
		def landCards
		def houseCards
		def deviceCards
		def bookCards
		def furnitureCards
		
		if(assetIds.size()>0){
			assetIds.each{
				//将申请单号和资产变更类型写入资产建账信息中，同时计算总金额
				if(assetType.equals("car")){
					carCards = CarCards.get(it)
					if(carCards){
						carCards.assetStatus = "资产已调拨"
						carCards.seriesNo = seriesNo
						totalPrice = carCards.onePrice
					}
				}else if(assetType.equals("land")){
					landCards LandCards.get(it)
					if(landCards){
						landCards.assetStatus = "资产已调拨"
						landCards.seriesNo = seriesNo
						totalPrice = landCards.onePrice
					}
				}else if(assetType.equals("house")){
					houseCards = HouseCards.get(it)
					if(houseCards){
						houseCards.assetStatus = "资产已调拨"
						houseCards.seriesNo = seriesNo
						totalPrice = houseCards.onePrice
					}
				}else if(assetType.equals("device")){
					deviceCards = DeviceCards.get(it)
					if(deviceCards){
						deviceCards.assetStatus = "资产已调拨"
						deviceCards.seriesNo = seriesNo
						totalPrice = deviceCards.onePrice
					}
				}else if(assetType.equals("book")){
					bookCards = BookCards.get(it)
					if(bookCards){
						bookCards.assetStatus = "资产已调拨"
						bookCards.seriesNo = seriesNo
						totalPrice = bookCards.onePrice
					}
				}else if(assetType.equals("furniture")){
					furnitureCards = FurnitureCards.get(it)
					if(furnitureCards){
						furnitureCards.assetStatus = "资产已调拨"
						furnitureCards.seriesNo = seriesNo
						totalPrice = furnitureCards.onePrice
					}
				}
				assetTotal += totalPrice
			}
			message = "操作成功！"
			json = [result:'true',assetTotal:assetTotal,message:message]
		}else{
			message = "操作失败！"
			json = [result:'error',assetTotal:assetTotal,message:message]
		}
		render json as JSON
	}
	
	def assetChooseDelete = {
		def json,message
		def assetAllocate = new AssetAllocate()
		
		def carCards
		def landCards
		def houseCards
		def deviceCards
		def bookCards
		def furnitureCards
		
		double totalPrice = 0
		double assetTotal = 0
		
		if(params.allocateId && !"".equals(params.allocateId)){
			assetAllocate = AssetAllocate.get(params.allocateId)
			assetTotal = assetAllocate.assetTotal
		}
		
		def assetId
		def assetIds
		if(params.assetId && params.assetId!="" && params.assetId!=null){
			assetId = params.assetId
			assetIds = assetId.split(",")
		}
		if(assetIds.size()>0){
			assetIds.each{
				//变更资产建账信息中的申请单号和资产变更类型，同时计算总金额
				carCards = CarCards.get(it)
				if(carCards){
					carCards.assetStatus = "已入库"
					carCards.seriesNo = null
					totalPrice = carCards.onePrice
				}
				landCards = LandCards.get(it)
				if(landCards){
					landCards.assetStatus = "已入库"
					landCards.seriesNo = null
					totalPrice = landCards.onePrice
				}
				houseCards = HouseCards.get(it)
				if(houseCards){
					houseCards.assetStatus = "已入库"
					houseCards.seriesNo = null
					totalPrice = houseCards.onePrice
				}
				deviceCards = DeviceCards.get(it)
				if(deviceCards){
					deviceCards.assetStatus = "已入库"
					deviceCards.seriesNo = null
					totalPrice = deviceCards.onePrice
				}
				bookCards = BookCards.get(it)
				if(bookCards){
					bookCards.assetStatus = "已入库"
					bookCards.seriesNo = null
					totalPrice = bookCards.onePrice
				}
				furnitureCards = FurnitureCards.get(it)
				if(furnitureCards){
					furnitureCards.assetStatus = "已入库"
					furnitureCards.seriesNo = null
					totalPrice = furnitureCards.onePrice
				}
				assetTotal -= totalPrice
			}
			message = "操作成功！"
			json = [result:'true',assetTotal:assetTotal,message:message]
		}else{
			message = "操作失败！"
			json = [result:'error',assetTotal:assetTotal,message:message]
		}
		render json as JSON
	}
	
	def assetAllocateFlowDeal ={
		def json=[:]
		
		def assetAllocate = AssetAllocate.get(params.id)
		//处理资产调拨状态
		assetAllocate.dataStatus = params.status
//		if(params.status.equals("已归档")){
//			assetAllocate.dataStatus = params.status
//		}
		
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
					args["content"] = "请您审核编号为  【" + assetAllocate.seriesNo +  "】 的资产调拨信息"
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
				args["content"] = "编号为  【" + assetAllocate.seriesNo +  "】 的资产调拨信息被退回，请查看！"
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
		response.setHeader("Content-disposition", "attachment; filename=" + new String("资产调拨信息.xls".getBytes("GB2312"), "ISO_8859_1"))
		
		//查询条件
//		def searchArgs =[:]
//		if(params.username && !"".equals(params.username)) searchArgs["username"] = params.username
//		if(params.chinaName && !"".equals(params.chinaName)) searchArgs["chinaName"] = params.chinaName
//		if(params.departName && !"".equals(params.departName)) searchArgs["departName"] = params.departName
		
		def c = AssetAllocate.createCriteria()

		def allocateList = c.list{
			
			eq("company",company)
			eq("status","已结束")
			
//			searchArgs.each{k,v->
//				if(k.equals("departName")){
//					departs{
//						like(k,"%" + v + "%")
//					}
//				}else if(k.equals("username")){
//					createAlias('user', 'a')
//					like("a.username","%" + v + "%")
//
//				}else{
//					like(k,"%" + v + "%")
//				}
//			}
			
//			if("staffAdd".equals(params.type)){
//				not {'in'("status",["在职","退休","离职"])}
//				order("createDate", "desc")
//			}else{
//				'in'("status",["在职","退休","离职"])
//				order("createDate", "desc")
//			}
			
		}
		def excel = new ExcelExport()
		excel.allocateDc(os,allocateList)
	}
}
