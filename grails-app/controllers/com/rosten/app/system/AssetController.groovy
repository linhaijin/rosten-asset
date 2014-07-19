package com.rosten.app.system

import grails.converters.JSON

class AssetController {
	def systemService
	
	def modelInit ={
		def json,model,resource
		def company = Company.get(params.id)
		def path = request.contextPath
		
		try{
			//删除当前单位下面的所有模块信息（除系统管理等基础模块）
			def modelCodes = ["system","workflow","public","sms","question","personconfig"]
			Model.findAllByCompany(company).each{
				if(!modelCodes.contains(it.modelCode)){
					it.delete()
				}
			}
			//增加资产系统特有的功能模块
			model = new Model(company:company)
			model.modelName = "资产配置"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "assetConfig"
			model.serialNo = 4
			
			resource = new Resource()
			resource.resourceName = "资产大类"
			resource.url = "assetCategory"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			model.save()
			
			model = new Model(company:company)
			model.modelName = "资产建账"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "bookKeeping"
			model.serialNo = 5
			
			resource = new Resource()
			resource.resourceName = "建账登记"
			resource.url = "register"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "审批入库"
			resource.url = "approveStorage"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			model.save()
			
			model = new Model(company:company)
			model.modelName = "资产变动"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "assetChange"
			model.serialNo = 6
			
			resource = new Resource()
			resource.resourceName = "报废报损"
			resource.url = "scrap"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "调拨"
			resource.url = "allocate"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "增值减值"
			resource.url = "addOrDelete"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "资产调出"
			resource.url = "assetExport"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "资产报失"
			resource.url = "assetLose"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "资产退库"
			resource.url = "assetQuit"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			model.save()
			
			model = new Model(company:company)
			model.modelName = "资产运维"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "maintain"
			model.serialNo = 7
			
			resource = new Resource()
			resource.resourceName = "资产报修"
			resource.url = "assetMaintain"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			model.save()
			
			model = new Model(company:company)
			model.modelName = "资产核查"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "check"
			model.serialNo = 8
			
			resource = new Resource()
			resource.resourceName = "资产盘点"
			resource.url = "assetCheck"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			model.save()
			
			model = new Model(company:company)
			model.modelName = "资产上报"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "report"
			model.serialNo = 9
			
			resource = new Resource()
			resource.resourceName = "财政上报"
			resource.url = "fiscalReport"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			model.save()
			
			model = new Model(company:company)
			model.modelName = "统计分析"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "static"
			model.description ="统计分析"
			model.serialNo = 10
			
			resource = new Resource()
			resource.resourceName = "统计分析"
			resource.url = "static"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			model.save()
			
			//增加人事系统特有的服务列表信息
			NormalService.findAllByCompany(company).each{
				it.delete()
			}
			
			systemService.initData_service(path,company)
			
			json = [result:'true']
		}catch(Exception e){
			log.debug(e);
			json = [result:'error']
		}
		render json as JSON
	}
}
