package com.rosten.app.assetChange

import grails.converters.JSON

import com.rosten.app.assetCards.CarCards
import com.rosten.app.assetCards.LandCards
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.assetCards.BookCards
import com.rosten.app.assetCards.FurnitureCards
import com.rosten.app.assetChange.AssetScrap

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.GridUtil
import com.rosten.app.util.Util

import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Depart

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

class AssetScrapController {
	def assetCardsService
    def assetChangeService
	def springSecurityService
	def workFlowService
	def taskService
	def systemService
	def shareService
	def startService

	def imgPath ="images/rosten/actionbar/"
	
	def assetScrapForm = {
		def webPath = request.getContextPath() + "/"
		def strname = "assetScrap"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		if(params.id){
			def entity = AssetScrap.get(params.id)
			def user = User.get(params.userid)
			if(user.equals(entity.currentUser)){
				//当前处理人
				switch (true){
					case entity.status.contains("审核") || entity.status.contains("审批"):
						actionList << createAction("填写意见",webPath +imgPath + "sign.png",strname + "_addComment")
						actionList << createAction("同意",webPath +imgPath + "ok.png",strname + "_submit")
						actionList << createAction("回退",webPath +imgPath + "back.png",strname + "_back")
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
	
	def assetScrapView = {
		def actionList =[]
		def strname = "assetScrap"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
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
	
	def assetScrapAdd = {
		if(params.flowCode){
			//需要走流程
			def company = Company.get(params.companyId)
			def flowBusiness = FlowBusiness.findByFlowCodeAndCompany(params.flowCode,company)
			if(flowBusiness && !"".equals(flowBusiness.relationFlow)){
				params.relationFlow = flowBusiness.relationFlow
				redirect(action:"assetScrapShow",params:params)
			}else{
				//不存在流程引擎关联数据
				render '<h2 style="color:red;width:660px;margin:0 auto;margin-top:60px">当前业务不存在流程设置，无法创建，请联系管理员！</h2>'
			}
		}else{
			redirect(action:"assetScrapShow",params:params)
		}
	}
	
	def assetScrapShow = {
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def money
		def assetScrap = new AssetScrap()
		def assetType
		if(params.id){
			assetScrap = AssetScrap.get(params.id)
			money = assetScrap.assetTotal
			def seriesNo = assetScrap.seriesNo
			def carCards = CarCards.findAllBySeriesNo(seriesNo)
			if(carCards != null){assetType = "car"}
			def landCards = LandCards.findAllBySeriesNo(seriesNo)
			if(landCards != null){assetType = "land"}
			def houseCards = HouseCards.findAllBySeriesNo(seriesNo)
			if(houseCards != null){assetType = "house"}
			def deviceCards = DeviceCards.findAllBySeriesNo(seriesNo)
			if(deviceCards != null){assetType = "device"}
			def bookCards = BookCards.findAllBySeriesNo(seriesNo)
			if(bookCards != null){assetType = "book"}
			def furnitureCards = FurnitureCards.findAllBySeriesNo(seriesNo)
			if(furnitureCards != null){assetType = "furniture"}
		}
		
		model["assetType"] = assetType
		model["user"] = currentUser
		model["company"] = company
		model["money"] = money
		model["assetScrap"] = assetScrap
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		//流程相关信息----------------------------------------------
		model["relationFlow"] = params.relationFlow
		model["flowCode"] = params.flowCode
		//------------------------------------------------------
		
		render(view:'/assetChange/assetScrap',model:model)
	}
	
	def assetScrapSave = {
		def json=[:]
		def company = Company.get(params.companyId)
		def currentUser = springSecurityService.getCurrentUser()
		
		//资产报损申请信息保存-------------------------------
		def assetScrap = new AssetScrap()
		if(params.id && !"".equals(params.id)){
			assetScrap = AssetScrap.get(params.id)
		}else{
			assetScrap.company = company
		}
		assetScrap.properties = params
		assetScrap.clearErrors()
		
		//特殊字段信息处理
		assetScrap.applyDate = Util.convertToTimestamp(params.applyDate)
		
		if(params.allowdepartsId.equals("")){
			assetScrap.applyDept = params.allowdepartsName
		}else{
			assetScrap.applyDept = Depart.get(params.allowdepartsId)
		}
		
		if(!params.seriesNo_form.equals("")){
			assetScrap.seriesNo = params.seriesNo_form
		}
		assetScrap.dataStatus = assetScrap.status
		
		//判断是否需要走流程
		def _status
		if(params.relationFlow){
			//需要走流程
			if(params.id){
				_status = "old"
			}else{
				_status = "new"
				assetScrap.currentUser = currentUser
				assetScrap.currentDepart = currentUser.getDepartName()
				assetScrap.currentDealDate = new Date()
				
				assetScrap.drafter = currentUser
				assetScrap.drafterDepart = currentUser.getDepartName()
			}
			
			//增加读者域
			if(!assetScrap.readers.find{ it.id.equals(currentUser.id) }){
				assetScrap.addToReaders(currentUser)
			}
			
			//流程引擎相关信息处理-------------------------------------------------------------------------------------
			if(!assetScrap.processInstanceId){
				//启动流程实例
				def _processInstance = workFlowService.getProcessDefinition(params.relationFlow)
				Map<String, Object> variables = new HashMap<String, Object>();
				ProcessInstance processInstance = workFlowService.addFlowInstance(_processInstance.key, currentUser.username,assetScrap.id, variables);
				assetScrap.processInstanceId = processInstance.getProcessInstanceId()
				assetScrap.processDefinitionId = processInstance.getProcessDefinitionId()
				
				//获取下一节点任务
				def task = workFlowService.getTasksByFlow(processInstance.getProcessInstanceId())[0]
				assetScrap.taskId = task.getId()
			}
			//-------------------------------------------------------------------------------------------------
		}
		
		if(assetScrap.save(flush:true)){
			json["result"] = "true"
			json["id"] = assetScrap.id
		}else{
			assetScrap.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def assetScrapDelete = {
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
				def assetScrap = AssetScrap.get(it)
				if(assetScrap){
					assetScrap.delete(flush: true)
					/*
					//获取当前资产变更申请单号，用以重置资产卡片中的值
					seriesNo = assetScrap.seriesNo
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
					//重置土地资产建账的资产变更申请单号
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
					//重置房屋资产建账的资产变更申请单号
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
					//重置设备资产建账的资产变更申请单号
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
					//重置图书资产建账的资产变更申请单号
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
					//重置家具资产建账的资产变更申请单号
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
	
	def assetScrapGrid = {
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetChangeService.getAssetScrapListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetChangeService.getAssetScrapDataStore(args)
		}
		if(params.refreshPageControl){
			def total = assetChangeService.getAssetScrapCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def assetScrapListDataStore = {
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
//					if(assetType=="" || assetType==null){
//						eq("assetStatus","报废待审批")
//					}
//					if(freshType=="twice"){
//						eq("assetStatus","报废待审批")
//					}
				}
			}
		}
		
		def assetList
		if(params.refreshData){
			if(!(assetType.equals("") || assetType==null)){
				if(assetType.equals("car")){
					assetList = car.list(pa,query)
//					assetList = CarCards.findAllByCompany(companyEntity,[max: max, sort: "createDate", order: "desc", offset: offset])
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
//					if(freshType=="twice"){
//						eq("assetStatus","报废待审批")
//						eq("seriesNo",seriesNo)
//					}else{
						eq("assetStatus","已入库")
//					}
				}
			}
		}
		
		def assetList
		if(params.refreshData){
			if(!(assetType.equals("") || assetType==null)){
				if(assetType.equals("car")){
					assetList = car.list(pa,query)
//					assetList = CarRegister.findAllByCompany(companyEntity,[max: max, sort: "createDate", order: "desc", offset: offset])
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
				//将申请单号和资产变更类型写入资产卡片信息中，同时计算总金额
				if(assetType.equals("car")){
					carCards = CarCards.get(it)
					if(carCards){
						carCards.assetStatus = "资产已报损"
						carCards.seriesNo = seriesNo
						totalPrice = carCards.onePrice
					}
				}else if(assetType.equals("land")){
					landCards = LandCards.get(it)
					if(landCards){
						landCards.assetStatus = "资产已报损"
						landCards.seriesNo = seriesNo
						totalPrice = landCards.onePrice
					}
				}else if(assetType.equals("house")){
					houseCards = HouseCards.get(it)
					if(houseCards){
						houseCards.assetStatus = "资产已报损"
						houseCards.seriesNo = seriesNo
						totalPrice = houseCards.onePrice
					}
				}else if(assetType.equals("device")){
					deviceCards = DeviceCards.get(it)
					if(deviceCards){
						deviceCards.assetStatus = "资产已报损"
						deviceCards.seriesNo = seriesNo
						totalPrice = deviceCards.onePrice
					}
				}else if(assetType.equals("book")){
					bookCards = BookCards.get(it)
					if(bookCards){
						bookCards.assetStatus = "资产已报损"
						bookCards.seriesNo = seriesNo
						totalPrice = bookCards.onePrice
					}
				}else if(assetType.equals("furniture")){
					furnitureCards = FurnitureCards.get(it)
					if(furnitureCards){
						furnitureCards.assetStatus = "资产已报损"
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
		def assetScrap = new AssetScrap()
		
		def carCards
		def landCards
		def houseCards
		def deviceCards
		def bookCards
		def furnitureCards
		
		double assetTotal = 0
		double cardsPrice = 0
		
		if(params.scrapId && !"".equals(params.scrapId)){
			assetScrap = AssetScrap.get(params.scrapId)
//			assetTotal = assetScrap.assetTotal
		}
		
		if(params.assetTotal && params.assetTotal !=0){
			assetTotal = params.assetTotal.toDouble()
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
					cardsPrice = carCards.onePrice
				}
				landCards = LandCards.get(it)
				if(landCards){
					landCards.assetStatus = "已入库"
					landCards.seriesNo = null
					cardsPrice = landCards.onePrice
				}
				houseCards = HouseCards.get(it)
				if(houseCards){
					houseCards.assetStatus = "已入库"
					houseCards.seriesNo = null
					cardsPrice = houseCards.onePrice
				}
				deviceCards = DeviceCards.get(it)
				if(deviceCards){
					deviceCards.assetStatus = "已入库"
					deviceCards.seriesNo = null
					cardsPrice = deviceCards.onePrice
				}
				bookCards = BookCards.get(it)
				if(bookCards){
					bookCards.assetStatus = "已入库"
					bookCards.seriesNo = null
					cardsPrice = bookCards.onePrice
				}
				furnitureCards = FurnitureCards.get(it)
				if(furnitureCards){
					furnitureCards.assetStatus = "已入库"
					furnitureCards.seriesNo = null
					cardsPrice = furnitureCards.onePrice
				}
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
	
	def assetScrapSaveCheck = {
		def json,message
		def categoryId
		def categoryIds = []
		if(params.categoryId && params.categoryId !=null){
			categoryId = params.categoryId
			categoryIds = categoryId.split(",")
		}
		def carCards
		def landCards
		def houseCards
		def deviceCards
		def bookCards
		def furnitureCards

		def typeArr = []
		if(categoryIds.size()>0){
			categoryIds.each{
				//通过资产Id获取资产类型
				carCards = CarCards.get(it)
				if(carCards){
					typeArr<<"car"
				}
				landCards = LandCards.get(it)
				if(landCards){
					typeArr<<"land"
				}
				houseCards = HouseCards.get(it)
				if(houseCards){
					typeArr<<"house"
				}
				deviceCards = DeviceCards.get(it)
				if(deviceCards){
					typeArr<<"device"
				}
				bookCards = BookCards.get(it)
				if(bookCards){
					typeArr<<"book"
				}
				furnitureCards = FurnitureCards.get(it)
				if(furnitureCards){
					typeArr<<"furniture"
				}
			}
			typeArr = typeArr.unique()
			if(typeArr.size()==1){
				message = "操作成功！"
				json = [result:'true',message:message]
			}else{
				message = "注意：资产列表只能为同类型资产！！"
				json = [result:'false',message:message]
			}
		}else{
			message = "操作失败，请联系管理员！"
			json = [result:'error',message:message]
		}
		render json as JSON
	}
	
	def assetScrapFlowDeal ={
		def json=[:]
		
		def assetScrap = AssetScrap.get(params.id)
		//处理资产报损状态
		assetScrap.dataStatus = params.status
//		if(params.status.equals("已归档")){
//			assetScrap.dataStatus = params.status
//		}
		
		//处理当前人的待办事项
		def currentUser = springSecurityService.getCurrentUser()
		def frontStatus = assetScrap.status
		def nextStatus,nextDepart,nextLogContent
		def nextUsers=[]
		
		//流程引擎相关信息处理-------------------------------------------------------------------------------------
		
		//结束当前任务，并开启下一节点任务
		def map =[:]
		if(params.conditionName){
			map[params.conditionName] = params.conditionValue
		}
		taskService.complete(assetScrap.taskId,map)	//结束当前任务
		
		ProcessInstance processInstance = workFlowService.getProcessIntance(assetScrap.processInstanceId)
		if(!processInstance || processInstance.isEnded()){
			//流程已结束
			nextStatus = "已结束"
			assetScrap.dataStatus = nextStatus
			assetScrap.currentUser = null
			assetScrap.currentDepart = null
			assetScrap.taskId = null
		}else{
			//获取下一节点任务，目前处理串行情况
			def tasks = workFlowService.getTasksByFlow(assetScrap.processInstanceId)
			def task = tasks[0]
			if(task.getDescription() && !"".equals(task.getDescription())){
				nextStatus = task.getDescription()
			}else{
				nextStatus = task.getName()
			}
			assetScrap.taskId = task.getId()
		
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
					def _model = Model.findByModelCodeAndCompany("assetScrap",currentUser.company)
					def authorize = systemService.checkIsAuthorizer(nextUser,_model,new Date())
					if(authorize){
						shareService.addFlowLog(assetScrap.id,"assetScrap",nextUser,"委托授权给【" + authorize.beAuthorizerDepart + ":" + authorize.getFormattedAuthorizer() + "】")
						nextUser = authorize.beAuthorizer
						nextDepart = authorize.beAuthorizerDepart
					}
					//-------------------------------------------------------------------------
					
					//任务指派给当前拟稿人
					taskService.claim(assetScrap.taskId, nextUser.username)
					
					def args = [:]
					args["type"] = "【资产报损】"
					args["content"] = "请您审核编号为  【" + assetScrap.seriesNo +  "】 的资产报损信息"
					args["contentStatus"] = nextStatus
					args["contentId"] = assetScrap.id
					args["user"] = nextUser
					args["company"] = nextUser.company
					
					startService.addGtask(args)
					
					assetScrap.currentUser = nextUser
					assetScrap.currentDepart = nextDepart
					
					if(!assetScrap.readers.find{ item->
						item.id.equals(nextUser.id)
					}){
						assetScrap.addToReaders(nextUser)
					}
					nextUsers << nextUser.getFormattedName()
				}
			}
		}
		assetScrap.status = nextStatus
		assetScrap.currentDealDate = new Date()
		
		//判断下一处理人是否与当前处理人员为同一人
		if(currentUser.equals(assetScrap.currentUser)){
			json["refresh"] = true
		}
		
		//----------------------------------------------------------------------------------------------------
		
		//修改代办事项状态
		def gtask = Gtask.findWhere(
			user:currentUser,
			company:currentUser.company,
			contentId:assetScrap.id,
			contentStatus:frontStatus,
			status:"0"
		)
		if(gtask!=null){
			gtask.dealDate = new Date()
			gtask.status = "1"
			gtask.save(flush:true)
		}
		
		if(assetScrap.save(flush:true)){
			//添加日志
			def logContent
			switch (true){
				case assetScrap.status.contains("已结束"):
					logContent = "结束流程"
					break
				case assetScrap.status.contains("归档"):
					logContent = "归档"
					break
				case assetScrap.status.contains("不同意"):
					logContent = "不同意！"
					break
				default:
					logContent = "提交" + assetScrap.status + "【" + nextUsers.join("、") + "】"
					break
			}
			shareService.addFlowLog(assetScrap.id,"assetScrap",currentUser,logContent)
						
			json["result"] = true
		}else{
			assetScrap.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	
	def assetScrapFlowBack ={
		def json=[:]
		def assetScrap = AssetScrap.get(params.id)
		
		def currentUser = springSecurityService.getCurrentUser()
		def frontStatus = assetScrap.status
		
		try{
			//获取上一处理任务
			def frontTaskList = workFlowService.findBackAvtivity(assetScrap.taskId)
			if(frontTaskList && frontTaskList.size()>0){
				//简单的取最近的一个节点
				def activityEntity = frontTaskList[frontTaskList.size()-1]
				def activityId = activityEntity.getId();
				
				//流程跳转
				workFlowService.backProcess(assetScrap.taskId, activityId, null)
				
				//获取下一节点任务，目前处理串行情况
				def nextStatus
				def tasks = workFlowService.getTasksByFlow(assetScrap.processInstanceId)
				def task = tasks[0]
				if(task.getDescription() && !"".equals(task.getDescription())){
					nextStatus = task.getDescription()
				}else{
					nextStatus = task.getName()
				}
				assetScrap.taskId = task.getId()
				
				//获取对应节点的处理人员以及相关状态
				def historyActivity = workFlowService.getHistrotyActivityByActivity(assetScrap.taskId,activityId)
				def user = User.findByUsername(historyActivity.getAssignee())
				
				//任务指派给当前拟稿人
				taskService.claim(assetScrap.taskId, user.username)
				
				//增加待办事项
				def args = [:]
				args["type"] = "【资产报损】"
				args["content"] = "编号为  【" + assetScrap.seriesNo +  "】 的资产报损信息被退回，请查看！"
				args["contentStatus"] = nextStatus
				args["contentId"] = assetScrap.id
				args["user"] = user
				args["company"] = user.company
				
				startService.addGtask(args)
					
				//修改相关信息
				assetScrap.currentUser = user
				assetScrap.currentDepart = user.getDepartName()
				assetScrap.currentDealDate = new Date()
				assetScrap.status = nextStatus
				
				//判断下一处理人是否与当前处理人员为同一人
				if(currentUser.equals(assetScrap.currentUser)){
					json["refresh"] = true
				}
				
				//----------------------------------------------------------------------------------------------------
				
				//修改代办事项状态
				def gtask = Gtask.findWhere(
					user:currentUser,
					company:currentUser.company,
					contentId:assetScrap.id,
					contentStatus:frontStatus,
					status:"0"
				)
				if(gtask!=null){
					gtask.dealDate = new Date()
					gtask.status = "1"
					gtask.save(flush:true)
				}
				
				assetScrap.save(flush:true)
				
				//添加日志
				def logContent = "退回【" + user.getFormattedName() + "】"
				
				shareService.addFlowLog(assetScrap.id,"assetScrap",currentUser,logContent)
			}
				
			json["result"] = true
		}catch(Exception e){
			json["result"] = false
		}
		render json as JSON
	}
	
//	def exportAssetScrap ={
//		OutputStream os = response.outputStream
//		def company = Company.get(params.companyId)
//		response.setContentType('application/vnd.ms-excel')
//		response.setHeader("Content-disposition", "attachment; filename=" + new String("员工信息.xls".getBytes("GB2312"), "ISO_8859_1"))
//		
//		def excel = new ExcelExport()
//		excel.mbxz(os)
//	} 
	
	def assetScrapExport = {
		OutputStream os = response.outputStream
		def company = Company.get(params.companyId)
		response.setContentType('application/vnd.ms-excel')
		response.setHeader("Content-disposition", "attachment; filename=" + new String("资产报损信息.xls".getBytes("GB2312"), "ISO_8859_1"))
		
		//查询条件
//		def searchArgs =[:]
//		if(params.username && !"".equals(params.username)) searchArgs["username"] = params.username
//		if(params.chinaName && !"".equals(params.chinaName)) searchArgs["chinaName"] = params.chinaName
//		if(params.departName && !"".equals(params.departName)) searchArgs["departName"] = params.departName
		
		def c = AssetScrap.createCriteria()

		def assetScrapList = c.list{
			
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
		excel.assetScrapDc(os,assetScrapList)
	}
}
