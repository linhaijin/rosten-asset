package com.rosten.app.assetChange

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.GridUtil
import com.rosten.app.util.Util

import com.rosten.app.assetCards.CarCards
import com.rosten.app.assetCards.LandCards
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.assetCards.BookCards
import com.rosten.app.assetCards.FurnitureCards

import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Depart

class AssetCategoryChooseController {
	def imgPath ="images/rosten/actionbar/"
	
	def assetCategoryChooseForm = {
		def webPath = request.getContextPath() + "/"
		def strname = "assetCategoryChoose"
		def actionList = []
		
//		actionList << createAction("返回",webPath + imgPath + "quit_1.gif",strname + "_close")
		actionList << createAction("添加",webPath +imgPath + "add.png",strname + "_add")
		render actionList as JSON
	}
	
	private def createAction = {name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
	
    def assetChoose = {
		def model = [:]
		def assetCardsType
		def assetDepart
		def assetUser
		def controlName
		def seriesNo
		
		def company = Company.get(params.companyId)
		model["company"] = company
		model["DepartList"] = Depart.findAllByCompany(company)
		
		if(params.assetCardsType && !params.assetCardsType.equals("")){
			assetCardsType = params.assetCardsType
			model["assetCardsType"] = assetCardsType
		}
		if(params.assetDepartId && !params.assetDepartId.equals("")){
			assetDepart = Depart.get(params.assetDepartId)
			model["assetDepart"] = assetDepart
		}
		if(params.assetUser && !params.assetUser.equals("")){
			assetUser = params.assetUser
			model["assetUser"] = assetUser
		}
		if(params.controlName && !params.controlName.equals("")){
			controlName = params.controlName
			model["controlName"] = controlName
		}
		if(params.seriesNo && !params.seriesNo.equals("")){
			seriesNo = params.seriesNo
			model["seriesNo"] = seriesNo
		}
		render(view:'/assetChange/assetCategoryChoose',model:model)
	}
	
	def assetCategoryChooseListDataStore = {
		def json=[:]
		
		def companyId
		if(params.companyId && params.companyId != "" && params.companyId != null){
			companyId = params.companyId
		}
		def company = Company.get(companyId)
		
		def controlName
		if(params.controlName && params.controlName!="" && params.controlName!=null){
			controlName = params.controlName
		}
		def car = CarCards.createCriteria()
		def house = HouseCards.createCriteria()
		def device = DeviceCards.createCriteria()
		def furniture = FurnitureCards.createCriteria()
		
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
		
		//获取搜索条件
		def hasArgs = false
		
		def assetList
		def assetCardsType = params.assetCardsType
			
		def assetDepart
		def depart
		if(params.assetDepart && params.assetDepart != "" && params.assetDepart != null){
			assetDepart = params.assetDepart
			depart = Depart.findByCompanyAndDepartName(company,assetDepart)
			
			if(!hasArgs) hasArgs= true
		}
		
		def assetUser
		if(params.assetUser && params.assetUser != "" && params.assetUser != null){
			assetUser = params.assetUser
			
			if(!hasArgs) hasArgs= true
		}
		
		//2015-1-10--增加查询条件---------------------------------------------------------------
		def registerNum
		if(params.assetRegisterNum && params.assetRegisterNum != "" && params.assetRegisterNum != null){
			registerNum = params.assetRegisterNum
			
			if(!hasArgs) hasArgs= true
		}
		
		def assetName
		if(params.assetName && params.assetName != "" && params.assetName != null){
			assetName = params.assetName
			
			if(!hasArgs) hasArgs= true
		}
		
		def buyDate
		if(params.buyDate && params.buyDate != "" && params.buyDate != null){
			buyDate = Util.convertToTimestamp(params.buyDate)
			
			if(!hasArgs) hasArgs= true
		}
		
		//----------------------------------------------------------------------------------
		
		def query = {
			eq("company",company)
			if(controlName == "assetLose"){
				or{
					eq("assetStatus","已入库")
					like("assetStatus","%已调拨%")
					like("assetStatus","%已报废%")
					like("assetStatus","%已报修%")
				}
				not{
					like("assetStatus","%已报失%")
				}
			}else{
				eq("assetStatus","已入库")
			}
			
			if(assetDepart != null && assetDepart != ""){
				eq("userDepart",depart)
			}
			if(assetUser != null && assetUser != ""){
				eq("purchaser",assetUser)
			}
			//2015-1-10-----增加查询条件------------------------------------
			if(registerNum != null && registerNum != ""){
				like("registerNum","%" + registerNum + "%")
			}
			if(assetName != null && assetName != ""){
				like("assetName","%" + assetName + "%")
			}
			if(buyDate != null && buyDate != ""){
				eq("buyDate",buyDate)
			}
			order("createDate","desc")
			//--------------------------------------------------------
			
		}	
	
		if(params.refreshData){
			if(assetCardsType.equals("car")){
				assetList = car.list(pa,query)
				totalNum = CarCards.createCriteria().count(query)
			}else if(assetCardsType.equals("house")){
				assetList = house.list(pa,query)
				totalNum = HouseCards.createCriteria().count(query)
			}else if(assetCardsType.equals("device")){
				assetList = device.list(pa,query)
				totalNum = DeviceCards.createCriteria().count(query)
			}else if(assetCardsType.equals("furniture")){
				assetList = furniture.list(pa,query)
				totalNum = FurnitureCards.createCriteria().count(query)
			}else{
			
				if(hasArgs){
					//默认取所有类型的信息--------2015-1-14---------------
					//--------此处，可能条目数方面有bug，待修复
					def _list =[],_totalNum =0
					_list += car.list(pa,query)
					_totalNum += CarCards.createCriteria().count(query)
					
					_list += house.list(pa,query)
					_totalNum += HouseCards.createCriteria().count(query)
					
					_list += device.list(pa,query)
					_totalNum += DeviceCards.createCriteria().count(query)
					
					_list += furniture.list(pa,query)
					_totalNum += FurnitureCards.createCriteria().count(query)
					
					assetList = _list.unique()
					totalNum = _totalNum
					
					//----------------------------------------------
				}
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
				sMap["userDepartId"] = it.userDepart?.id
				
				_json.items += sMap
				idx += 1
				
			}
				
		}
		json["gridData"] = _json
		if(params.refreshPageControl){
			json["pageControl"] = ["total":totalNum.toString()]
		}
		render json as JSON
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
	def assetCategoryChooseOperate = {
		def json,message
		
//		def controlName
//		def cardsStatus
//		if(params.controlName && params.controlName!="" && params.controlName!=null){
//			controlName = params.controlName
//			cardsStatus = ["assetAllocate":"已调拨","assetScrap":"已报废","assetLose":"已报失","assetRepair":"已报修"][controlName]
//		}
		
		def seriesNo
		if(params.seriesNo && params.seriesNo!="" && params.seriesNo!=null){
			seriesNo = params.seriesNo
		}
		
		def assetCardsType
		if(params.assetCardsType && params.assetCardsType!="" && params.assetCardsType!=null){
			assetCardsType = params.assetCardsType
		}
		
		def assetId
		def assetIds
		if(params.assetId && params.assetId!="" && params.assetId!=null){
			assetId = params.assetId
			assetIds = assetId.split(",")
		}
		
		double assetTotal = 0
		def nowTotalPrice
		if(params.assetTotal && params.assetTotal!=""){
			nowTotalPrice = params.assetTotal.replace("-",".").toDouble()
			assetTotal = nowTotalPrice
		}
		double totalPrice = 0
		
		def seriesNo_exist
		def seriesNo_exists
		if(assetIds.size()>0){
			assetIds.each{
				
				//205-1-14------默认采用统一处理方法，前提各相关domain字段需字段一致，相同-------
				
				def entity = this.getEntity(it)
				if(entity){
					seriesNo_exist = entity.seriesNo
					if(seriesNo_exist != null && seriesNo_exist !=""){//操作号已存在
						seriesNo_exists = seriesNo_exist.split(",")
						if(seriesNo in seriesNo_exists){
							//undo
						}else{
//							entity.assetStatus = cardsStatus
							entity.seriesNo += ","+seriesNo
							totalPrice = entity.onePrice
						}
					}else{
//						entity.assetStatus = cardsStatus
						entity.seriesNo = seriesNo
						totalPrice = entity.onePrice
					}
				}
				//--------------------------------------------------------------------
				
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
	
}
