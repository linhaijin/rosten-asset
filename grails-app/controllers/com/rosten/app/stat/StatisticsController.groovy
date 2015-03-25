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

class StatisticsController {
	
	def shareService
	
	//2015-3-25----------新增-------------------------
	def staticSearch={
		params._file = "staticSearch"
		params._format = "HTML"
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
						if("报废".equals(card.userStatus)){
							smap["clsl"] += 1
							smap["clyz"] += card.onePrice
						}else{
							smap["bfzcsl"] += 1
							smap["bfzcyz"] += card.onePrice
						}
					}
					//电子设备
					DeviceCards.findAllByUserDepart(dept).each{card ->
						if("报废".equals(card.userStatus)){
							smap["clsl"] += 1
							smap["clyz"] += card.onePrice
						}else{
							//判断是否为固定资产或者办公设备
							if("办公设备".equals(card.userCategory.categoryName)){
								smap["bgsbsl"] += 1
								smap["bgsbyz"] += card.onePrice
							}else{
								smap["gdzcsl"] += 1
								smap["gdzcyz"] += card.onePrice
							}
						}
					
					}
					
					//办公家具
					FurnitureCards.findAllByUserDepart(dept).each{card ->
						if("报废".equals(card.userStatus)){
							smap["clsl"] += 1
							smap["clyz"] += card.onePrice
						}else{
							//判断是否为固定资产或者办公设备
							if("办公设备".equals(card.userCategory.categoryName)){
								smap["bgsbsl"] += 1
								smap["bgsbyz"] += card.onePrice
							}else{
								smap["gdzcsl"] += 1
								smap["gdzcyz"] += card.onePrice
							}
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
			if("报废".equals(card.userStatus)){
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
		allList.each{card->
			if("报废".equals(card.userStatus)){
				smap["clsl"] += 1
				smap["clyz"] += card.onePrice
			}else{
				//判断是否为固定资产或者办公设备
				if("办公设备".equals(card.userCategory.categoryName)){
					smap["bgsbsl"] += 1
					smap["bgsbyz"] += card.onePrice
				}else{
					smap["gdzcsl"] += 1
					smap["gdzcyz"] += card.onePrice
				}
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
}
