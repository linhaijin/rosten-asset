package com.rosten.app.assetChange

import grails.converters.JSON

import com.rosten.app.assetCards.CarCards
import com.rosten.app.assetCards.LandCards
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.assetCards.BookCards
import com.rosten.app.assetCards.FurnitureCards

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart

class AssetAllocateController {
	def assetCardsService
    def assetChangeService
	def springSecurityService

	def imgPath ="images/rosten/actionbar/"
	
	def assetAllocateForm = {
		def webPath = request.getContextPath() + "/"
		def strname = "assetAllocate"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	  def assetAllocateView = {
		def actionList =[]
		def strname = "assetAllocate"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除",imgPath + "read.gif",strname + "_delete")
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
	
	def assetAllocateAdd = {
		redirect(action:"assetAllocateShow",params:params)
	}
	
	def assetAllocateShow = {
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def assetAllocate = new AssetAllocate()
		if(params.id){
			assetAllocate = AssetAllocate.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["assetAllocate"] = assetAllocate
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetChange/assetAllocate',model:model)
	}
	
	def assetAllocateSave = {
		def json=[:]
		def company = Company.get(params.companyId)
		
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
		
		if(assetAllocate.save(flush:true)){
			json["result"] = "true"
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
						carCards.assetStatus = "调拨待审批"
						carCards.seriesNo = seriesNo
						totalPrice = carCards.onePrice
					}
				}else if(assetType.equals("land")){
					landCards LandCards.get(it)
					if(landCards){
						landCards.assetStatus = "调拨待审批"
						landCards.seriesNo = seriesNo
						totalPrice = landCards.onePrice
					}
				}else if(assetType.equals("house")){
					houseCards = HouseCards.get(it)
					if(houseCards){
						houseCards.assetStatus = "调拨待审批"
						houseCards.seriesNo = seriesNo
						totalPrice = houseCards.onePrice
					}
				}else if(assetType.equals("device")){
					deviceCards = DeviceCards.get(it)
					if(deviceCards){
						deviceCards.assetStatus = "调拨待审批"
						deviceCards.seriesNo = seriesNo
						totalPrice = deviceCards.onePrice
					}
				}else if(assetType.equals("book")){
					bookCards = BookCards.get(it)
					if(bookCards){
						bookCards.assetStatus = "调拨待审批"
						bookCards.seriesNo = seriesNo
						totalPrice = bookCards.onePrice
					}
				}else if(assetType.equals("furniture")){
					furnitureCards = FurnitureCards.get(it)
					if(furnitureCards){
						furnitureCards.assetStatus = "调拨待审批"
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
}
