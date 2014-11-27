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

class StatisticsController {
	
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
