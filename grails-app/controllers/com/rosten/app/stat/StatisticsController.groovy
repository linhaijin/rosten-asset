package com.rosten.app.stat

import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.util.Util
import grails.converters.JSON

class StatisticsController {
	
	def getAssetStatic ={
		def company = Company.get(params.id)
		def json = [identifier:'id',label:'name',items:[]]
		
		def index = 1
		
		params.departIds.split(",").each{
			def depart = Depart.get(it)
			def lastIndex,sMap
			
			//土地
			lastIndex = Util.obj2str(index).padLeft(3,"0")
			sMap = ["id":lastIndex,"name":depart.departName,"type":"td","money":4000]
			index += 1
			json.items+=sMap
			
			//房屋
			lastIndex = Util.obj2str(index).padLeft(3,"0")
			sMap = ["id":lastIndex,"name":depart.departName,"type":"fw","money":2000]
			index += 1
			json.items+=sMap
			
			//车辆
			lastIndex = Util.obj2str(index).padLeft(3,"0")
			sMap = ["id":lastIndex,"name":depart.departName,"type":"cl","number":30,"money":1000]
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
					case "土地":
						_number = 1000
						sMap = ["id":lastIndex,"name":depart.departName,"group":item,"number":_number]
						break
					case "房屋":
						_number = 3000
						sMap = ["id":lastIndex,"name":depart.departName,"group":item,"number":_number]
						break
					case "设备":
						_number = 200
						sMap = ["id":lastIndex,"name":depart.departName,"group":item,"number":_number]
						break
					case "图书":
						_number = 500
						sMap = ["id":lastIndex,"name":depart.departName,"group":item,"number":_number]
						break
					case "车辆":
						_number = 1500
						sMap = ["id":lastIndex,"name":depart.departName,"group":item,"number":_number]
						break
				}
				
				index += 1
				
				json.items+=sMap
			}
		}
		render json as JSON
	}
	def getAssetByType ={
		def company = Company.get(params.id)
		def json = [identifier:'id',label:'name',items:[]]
		
		def sMap = ["id":001,"name":"土地","number":3000]
		json.items+=sMap
		
		sMap = ["id":002,"name":"房屋","number":1000]
		json.items+=sMap
		
		sMap = ["id":003,"name":"设备","number":1000]
		json.items+=sMap
		
		sMap = ["id":004,"name":"图书","number":200]
		json.items+=sMap
		
		sMap = ["id":005,"name":"车辆","number":2000]
		json.items+=sMap
		
		render json as JSON
	}
	
    def chart ={
		def model =[:]
		def company = Company.get(params.companyId)
		model["company"] = company
		
		//获取资产大类
		def groupList =["土地","房屋","设备","图书","车辆"]
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
