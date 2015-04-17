package com.rosten.app.stat

import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.util.Util
import grails.converters.JSON

import com.rosten.app.assetCards.CarCards
import com.rosten.app.assetCards.LandCards
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.assetCards.BookCards
import com.rosten.app.assetCards.FurnitureCards
import com.rosten.app.assetConfig.AssetCategory

import com.rosten.app.share.ShareService
import com.rosten.app.assetChange.*

class StatisticsController {
	
	def shareService
	
	def imgPath ="images/rosten/actionbar/"
	
	def staticExport ={
		params._file = "staticChgLog"
		params.title = "资产变化统计清单"
		
		def company = Company.get(params.companyId)
		
		def reportDetails = []
		
		//获取所有相关数据
		def c = ChangeLog.createCriteria()
		
		def allList = c.list{
			eq("company",company)
			
			if(params.s_status && !"".equals(params.s_status)){
				like("changeType","%" + params.s_status + "%")
			}
			
			if(params.s_startDate && !"".equals(params.s_startDate)){
				if(params.s_endDate && !"".equals(params.s_endDate)){
					between("createDate", Util.convertToTimestamp(params.s_startDate), Util.convertToTimestamp(params.s_endDate))
				}else{
					between("createDate", Util.convertToTimestamp(params.s_startDate), new Date())
				}
			}else{
				if(params.s_endDate && !"".equals(params.s_endDate)){
					lt("createDate", Util.convertToTimestamp(params.s_endDate))
				}
			}
			
			order("changeId", "desc")
		}
		
		allList.each{
			def applayEntity,cardEntity
			switch (it.changeType){
				case "报废":
					applayEntity = AssetScrap.get(it.changeId)
					break
				case "报失":
					applayEntity = AssetLose.get(it.changeId)
					break
				case "调拨":
					applayEntity = AssetAllocate.get(it.changeId)
					break
				case "报修":
					applayEntity = AssetRepair.get(it.changeId)
					break
				
			}
			
			switch (it.cardType){
				case "车辆":
					cardEntity = CarCards.get(it.cardId)
					break
				case "电子设备":
					cardEntity = DeviceCards.get(it.cardId)
					break
				case "办公家具":
					cardEntity = FurnitureCards.get(it.cardId)
					break
				case "房屋及建筑物":
					cardEntity = HouseCards.get(it.cardId)
					break
				
			}
			
			def sMap =[:]
			sMap["sqbh"] = applayEntity.seriesNo
			sMap["lx"] = it.changeType
			sMap["rq"] = it.getFormattedCreatedDate()
			sMap["zcbh"] = cardEntity.registerNum
			sMap["zcmc"] = cardEntity.assetName
			sMap["syr"] = cardEntity.purchaser
			sMap["syzk"] = cardEntity.userStatus
			
			reportDetails << sMap
			
		}
		
		if(reportDetails.size()==0){
			def sMap =[:]
			sMap["sqbh"] = "无"
			sMap["lx"] = "无"
			sMap["rq"] = "无"
			sMap["zcbh"] = "无"
			sMap["zcmc"] = "无"
			sMap["syr"] = "无"
			sMap["syzk"] = "无"
			
			reportDetails << sMap
		}
		chain(controller: 'jasper', action: 'index', model: [data: reportDetails], params: params)
	}
	def staticSearchAction={
		def actionList =[]
		def strname = "static"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("打印",imgPath + "word_print.png",strname + "_export")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def staticSearchView ={
		def model =[:]
		def company = Company.get(params.companyId)
		model["company"] = company
		render(view:'/statistics/staticSearch',model:model)
	}
	def staticSearchGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		def _gridHeader =[]
		_gridHeader << ["name":"序号","width":"30px","colIdx":0,"field":"rowIndex"]
		_gridHeader << ["name":"申请单号","width":"120px","colIdx":1,"field":"sqbh"]
		_gridHeader << ["name":"类型","width":"80px","colIdx":2,"field":"lx"]
		_gridHeader << ["name":"资产编号","width":"auto","colIdx":3,"field":"zcbh"]
		_gridHeader << ["name":"资产名称","width":"auto","colIdx":4,"field":"zcmc"]
		_gridHeader << ["name":"使用人","width":"auto","colIdx":5,"field":"syr"]
		_gridHeader << ["name":"使用状况","width":"60px","colIdx":6,"field":"syzk"]
		_gridHeader << ["name":"日期","width":"auto","colIdx":7,"field":"rq"]
		json["gridHeader"] = _gridHeader
		
		//增加查询条件
		def searchArgs =[:]
		if(params.s_status && !"".equals(params.s_status)) searchArgs["changeType"] = params.s_status
		if(params.s_startDate && !"".equals(params.s_startDate)) searchArgs["startDate"] = Util.convertToTimestamp(params.s_startDate)
		if(params.s_endDate && !"".equals(params.s_endDate)) searchArgs["endDate"] = Util.convertToTimestamp(params.s_endDate)
		
		def totalNum = 0	//总条目数
		
		if(params.refreshData){
			def _json = [identifier:'id',label:'name',items:[]]
			
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			def offset = (nowPage-1) * perPageNum
			def max = perPageNum
			
			searchArgs["offset"] = offset?offset:0
			searchArgs["max"] = max?max:15
			searchArgs["company"] = company
			
			def allList = this.getSearchData(searchArgs)
			
			def idx = offset
			allList.each{
				def applayEntity,cardEntity
				switch (it.changeType){
					case "报废":
						applayEntity = AssetScrap.get(it.changeId)
						break
					case "报失":
						applayEntity = AssetLose.get(it.changeId)
						break
					case "调拨":
						applayEntity = AssetAllocate.get(it.changeId)
						break
					case "报修":
						applayEntity = AssetRepair.get(it.changeId)
						break
					
				}
				
				switch (it.cardType){
					case "车辆":
						cardEntity = CarCards.get(it.cardId)
						break
					case "电子设备":
						cardEntity = DeviceCards.get(it.cardId)
						break
					case "办公家具":
						cardEntity = FurnitureCards.get(it.cardId)
						break
					case "房屋及建筑物":
						cardEntity = HouseCards.get(it.cardId)
						break
					
				}
				
				def sMap =[:]
				sMap["rowIndex"] = idx+1
				sMap["id"] = it.id
				sMap["sqbh"] = applayEntity.seriesNo
				sMap["lx"] = it.changeType
				sMap["rq"] = it.getFormattedCreatedDate()
				sMap["zcbh"] = cardEntity.registerNum
				sMap["zcmc"] = cardEntity.assetName
				sMap["syr"] = cardEntity.purchaser
				sMap["syzk"] = cardEntity.userStatus
				
				_json.items+=sMap
				
				idx ++
			}
			json["gridData"] = _json
		}
		if(params.refreshPageControl){
			json["pageControl"] = ["total":this.getSearchDataCount(searchArgs).toString()]
		}
		render json as JSON
		
	}
	private def getSearchData ={searchArgs->
		def c = ChangeLog.createCriteria()
		def pa=[max:searchArgs.max.toInteger(),offset:searchArgs.offset.toInteger()]
		def query = {
			eq("company",searchArgs.company)
			
			if(searchArgs.changeType && !"".equals(searchArgs.changeType)){
				like("changeType","%" + searchArgs.changeType + "%")
			}
			if(searchArgs.startDate && !"".equals(searchArgs.startDate)){
				if(searchArgs.endDate && !"".equals(searchArgs.endDate)){
					between("createDate", searchArgs.startDate, searchArgs.endDate)
				}else{
					between("createDate", searchArgs.startDate, new Date())
				}
			}else{
				if(searchArgs.endDate && !"".equals(searchArgs.endDate)){
					lt("createDate", searchArgs.endDate)
				}
			}
			
			order("changeId", "desc")
		}
		return c.list(pa,query)
	}
	
	private def getSearchDataCount ={searchArgs->
		def c = ChangeLog.createCriteria()
		def query = {
			eq("company",searchArgs.company)
			if(searchArgs.changeType && !"".equals(searchArgs.changeType)){
				like("changeType","%" + searchArgs.changeType + "%")
			}
			if(searchArgs.startDate && !"".equals(searchArgs.startDate)){
				if(searchArgs.endDate && !"".equals(searchArgs.endDate)){
					between("createDate", searchArgs.startDate, searchArgs.endDate)
				}else{
					between("createDate", searchArgs.startDate, new Date())
				}
			}else{
				if(searchArgs.endDate && !"".equals(searchArgs.endDate)){
					lt("createDate", searchArgs.endDate)
				}
			}
		}
		return c.count(query)
	}
	
	//2015-4-11-----------新增-------------------------
	def staticSearchTool ={
		def model =[:]
		def company = Company.get(params.companyId)
		model["company"] = company
		render(view:'/statistics/staticSearchView',model:model)
	}
	
	//2015-3-25----------新增-------------------------
	def staticSearch={
		params._file = "staticSearch"
//		params._format = "HTML"
		params.title = "办事部门固定资产总值"
		
		def reportDetails = []
		
		//所有地区
		def dqList = [[name:"舟山地区",code:"zsdq"],[name:"台州地区",code:"tzdq"],[name:"温州地区",code:"wzdq"],[name:"淡水地区地区",code:"dsdq"]]
		dqList.each{dq->
			def zsDepart = Depart.findByDepartCode(dq.code)
			if(zsDepart){
				zsDepart.children.each{dept ->
					//办事处
					def smap =[dqID:dq.name,bsc:dept.departName,clsl:0,clyz:0,gdzcsl:0,gdzcyz:0,bgsbsl:0,bgsbyz:0,bfzcsl:0,bfzcyz:0]
					
					//车辆
					CarCards.findAllByUserDepart(dept).each{card ->
						if(!"报废".equals(card.userStatus)){
							smap["clsl"] += 1
							smap["clyz"] += card.onePrice
						}else{
							//报废
							smap["bfzcsl"] += 1
							smap["bfzcyz"] += card.onePrice
						}
					}
					//电子设备
					DeviceCards.findAllByUserDepart(dept).each{card ->
						if(!"报废".equals(card.userStatus)){
							if("办公设备".equals(card.userCategory.categoryName)){
								smap["bgsbsl"] += 1
								smap["bgsbyz"] += card.onePrice
							}else if("固定资产".equals(card.userCategory.categoryName)){
								smap["gdzcsl"] += 1
								smap["gdzcyz"] += card.onePrice
							}
						}else{
							//报废---------------------
							smap["bfzcsl"] += 1
							smap["bfzcyz"] += card.onePrice
						}
					
					}
					
					//办公家具
					FurnitureCards.findAllByUserDepart(dept).each{card ->
						if(!"报废".equals(card.userStatus)){
							
							//判断是否为固定资产或者办公设备
							if("办公设备".equals(card.userCategory.categoryName)){
								smap["bgsbsl"] += 1
								smap["bgsbyz"] += card.onePrice
							}else if("固定资产".equals(card.userCategory.categoryName)){
								smap["gdzcsl"] += 1
								smap["gdzcyz"] += card.onePrice
							}
						}else{
							smap["bfzcsl"] += 1
							smap["bfzcyz"] += card.onePrice
						}
					
					}
					
					//房屋及建筑物
					HouseCards.findAllByUserDepart(dept).each{card ->
						if(!"报废".equals(card.userStatus)){
							
							//判断是否为固定资产或者办公设备
							if("办公设备".equals(card.userCategory.categoryName)){
								smap["bgsbsl"] += 1
								smap["bgsbyz"] += card.onePrice
							}else if("固定资产".equals(card.userCategory.categoryName)){
								smap["gdzcsl"] += 1
								smap["gdzcyz"] += card.onePrice
							}
						}else{
							smap["bfzcsl"] += 1
							smap["bfzcyz"] += card.onePrice
						}
					
					}
					
					reportDetails << smap
				}
			}
		}
		
		//协会本部-------------------------------------------------------------------------------------------------------
		def xhDepart = Depart.findByDepartCode("xhbb")
		def smap =[dqID:"协会",bsc:xhDepart.departName,clsl:0,clyz:0,gdzcsl:0,gdzcyz:0,bgsbsl:0,bgsbyz:0,bfzcsl:0,bfzcyz:0]
		
		//获取协会下面所有的部门id
		def userDepartList = []
		if(xhDepart){
			userDepartList << xhDepart
			xhDepart.children.each{
				def _list = []
				userDepartList += shareService.getAllDepartByChild(_list,it)
			}
			userDepartList.unique()
		}
		
		//通过所有部门找出所有的资产数据
		//车辆
		CarCards.createCriteria().list{
			'in'("userDepart",userDepartList)
		}.each{card->
			if(!"报废".equals(card.userStatus)){
				smap["clsl"] += 1
				smap["clyz"] += card.onePrice
			}else{
				smap["bfzcsl"] += 1
				smap["bfzcyz"] += card.onePrice
			}
		}
		
		//其他所有资产数据
		def allList =[]
		allList += HouseCards.createCriteria().list{
				'in'("userDepart",userDepartList)
		}
		allList += DeviceCards.createCriteria().list{
			'in'("userDepart",userDepartList)
		}
		allList += FurnitureCards.createCriteria().list{
			'in'("userDepart",userDepartList)
		}
		allList.unique().each{card->
			if(!"报废".equals(card.userStatus)){
				//判断是否为固定资产或者办公设备
				if("办公设备".equals(card.userCategory.categoryName)){
					smap["bgsbsl"] += 1
					smap["bgsbyz"] += card.onePrice
				}else if("固定资产".equals(card.userCategory.categoryName)){
					smap["gdzcsl"] += 1
					smap["gdzcyz"] += card.onePrice
				}
			}else{
				smap["bfzcsl"] += 1
				smap["bfzcyz"] += card.onePrice
			}
		}
		reportDetails << smap
		
		//----------------------------------------------------------------------------------------------
		if(reportDetails.size()==0){
			
		}
		
		chain(controller: 'jasper', action: 'index', model: [data: reportDetails], params: params)
	}
	
	def getAssetStatic ={
		def company = Company.get(params.id)
		def json = [identifier:'id',label:'name',items:[]]
		
		def index = 1
		
		params.departIds.split(",").each{
			def depart = Depart.get(it)
			def lastIndex,sMap
			
			//房屋及建筑物
			lastIndex = Util.obj2str(index).padLeft(3,"0")
			
			def houseList = HouseCards.findAllByUserDepart(depart)
			def houseTotal = houseList.collect { _item ->
				_item.onePrice
			}.sum()
			
			sMap = ["id":lastIndex,"name":depart.departName,"type":"fw","money":houseTotal?houseTotal/10000:0]
			index += 1
			json.items+=sMap
			
			//办公家具
			def furList = FurnitureCards.findAllByUserDepart(depart)
			def furTotal = furList.collect { item ->
				item.onePrice
			}.sum()
			
			lastIndex = Util.obj2str(index).padLeft(3,"0")
			sMap = ["id":lastIndex,"name":depart.departName,"type":"bgjj","money":furTotal?furTotal/10000:0]
			index += 1
			json.items+=sMap
			
			//运输工具
			def carList = CarCards.findAllByUserDepart(depart)
			def carTotal=carList.collect { _item ->
				_item.onePrice
			}.sum()
			
			lastIndex = Util.obj2str(index).padLeft(3,"0")
			sMap = ["id":lastIndex,"name":depart.departName,"type":"ysgj","money":carTotal?carTotal/10000:0]
			index += 1
			json.items+=sMap
			
			//电子设备
			def deviceList = DeviceCards.findAllByUserDepart(depart)
			def deviceTotal = deviceList.collect { _item ->
				_item.onePrice
			}.sum()
			
			lastIndex = Util.obj2str(index).padLeft(3,"0")
			sMap = ["id":lastIndex,"name":depart.departName,"type":"dzsb","number":30,"money":deviceTotal?deviceTotal/10000:0]
			index += 1
			json.items+=sMap
			
			
		}
		render json as JSON
	}
	def getAssetByDepart ={
		def company = Company.get(params.id)
		def json = [identifier:'id',label:'name',items:[]]
		
		def index = 1
		def groupNames = params.groupNames.split(",")
		
		params.departIds.split(",").each{
			def depart = Depart.get(it)
			
			groupNames.each{item ->
				def lastIndex = Util.obj2str(index).padLeft(3,"0")
				
				def sMap
				def _number = 0
				
				switch (item){
					case "房屋及建筑物":
						def houseList = HouseCards.findAllByUserDepart(depart)
						def houseTotal = houseList.collect { _item ->
							_item.onePrice
						}.sum()
						_number = houseTotal?houseTotal/10000:0
						sMap = ["id":lastIndex,"name":depart.departName,"group":item,"number":_number]
						break
					case "运输工具":
						def carList = CarCards.findAllByUserDepart(depart)
						def carTotal=carList.collect { _item ->
							_item.onePrice
						}.sum()
						_number = carTotal?carTotal/10000:0
						sMap = ["id":lastIndex,"name":depart.departName,"group":item,"number":_number]
						break
					case "电子设备":
						def deviceList = DeviceCards.findAllByUserDepart(depart)
						def deviceTotal = deviceList.collect { _item ->
							_item.onePrice
						}.sum()
						_number = deviceTotal?deviceTotal/10000:0
						sMap = ["id":lastIndex,"name":depart.departName,"group":item,"number":_number]
						break
					case "办公家具":
						def furList = FurnitureCards.findAllByUserDepart(depart)
						def furTotal = furList.collect { _item ->
							_item.onePrice
						}.sum()
						_number = furTotal?furTotal/10000:0
						sMap = ["id":lastIndex,"name":depart.departName,"group":item,"number":_number]
						break
				}
				
				index += 1
				
				json.items+=sMap
			}
		}
		json.items.unique().removeAll([null])
		render json as JSON
	}
	def getAssetByType ={
		def company = Company.get(params.id)
		def json = [identifier:'id',label:'name',items:[]]
		def sMap
		
//		def landList = LandCards.list()
//		def landTotal = landList.collect { item ->
//			item.onePrice
//		}.sum()
//		def sMap = ["id":001,"name":"土地","number":landTotal?landTotal/10000:0]
//		json.items+=sMap
		
		def houseList = HouseCards.list()
		def houseTotal = houseList.collect { item ->
			item.onePrice
		}.sum()
		sMap = ["id":002,"name":"房屋及建筑物","number":houseTotal?houseTotal/10000:0]
		json.items+=sMap
		
		def carList = CarCards.list()
		def carTotal=carList.collect { item ->
			item.onePrice
		}.sum()
		sMap = ["id":003,"name":"运输工具","number":carTotal?carTotal/10000:0]
		json.items+=sMap
		
		def furList = FurnitureCards.list()
		def furTotal = furList.collect { item ->
			item.onePrice
		}.sum()
		sMap = ["id":004,"name":"办公家具","number":furTotal?furTotal/10000:0]
		json.items+=sMap
		
		def deviceList = DeviceCards.list()
		def deviceTotal = deviceList.collect { item ->
			item.onePrice
		}.sum()
		
		sMap = ["id":005,"name":"电子设备","number":deviceTotal?deviceTotal/10000:0]
		json.items+=sMap
		
		render json as JSON
	}
	
    def chart ={
		def model =[:]
		def company = Company.get(params.companyId)
		model["company"] = company
		
		//获取资产大类
		def categoryList = AssetCategory.findAllByCompanyAndParent(company,null,[sort: "serialNo", order: "asc"])
		def groupList = categoryList.collect{
			it.categoryName	
		}
		
		model["groupList"] = groupList as JSON
		model["groupNames"] = groupList.join(",")
		
		def departList =[]
		def departIds =[]
		
		Depart.findAllByCompany(company).each{
			departList << it.departName
			departIds << it.id
		}
		model["departList"] = departList as JSON
		model["departIds"] = departIds.join(",")
		
		render(view:'/statistics/chart',model:model)
	}
	private def createAction = {name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
}
