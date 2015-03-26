package com.rosten.app.system

import grails.converters.JSON;
import com.rosten.app.workflow.FlowBusiness

class AssetController {
	def systemService
	
	
	def modelInit ={
		/*
		 * 初始化菜单功能序号统一从7开始
		 */
		
		def json,model,resource
		def company = Company.get(params.id)
		def path = request.contextPath
		
		try{
			//删除当前单位下面的所有模块信息（除系统管理等基础模块）
			def modelCodes = ["system","workflow","public","sms","question","personconfig"]
			Model.findAllByCompany(company).each{
				if(!modelCodes.contains(it.modelCode)){
					FlowBusiness.findAllByModel(it).each{item->
						item.model = null
						item.save()
					}
					it.delete()
				}
			}
			//增加资产系统特有的功能模块
			model = new Model(company:company)
			model.modelName = "资产配置"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "assetConfig"
			model.serialNo = 7
			
			resource = new Resource()
			resource.resourceName = "资产分类管理"
			resource.url = "assetCategory"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "资产编号配置"
			resource.url = "assetCodeConfig"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			model.save()
			
//			resource = new Resource()
//			resource.resourceName = "资产限额配置"
//			resource.url = "assetQuotaConfigure"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
//			model.save()
			
			model = new Model(company:company)
			model.modelName = "资产申请"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "assetApply"
			model.serialNo = 8
			
			resource = new Resource()
			resource.resourceName = "资产申请"
			resource.url = "mineApply"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "生成卡片"
			resource.url = "applyApproval"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			model.save()
			
//			model = new Model(company:company)
//			model.modelName = "资产建账"
//			model.modelUrl = path + "/system/navigation"
//			model.modelCode = "bookKeeping"
//			model.serialNo = 6
//			
//			resource = new Resource()
//			resource.resourceName = "车辆登记"
//			resource.url = "carRegister"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
//					
//			resource = new Resource()
//			resource.resourceName = "土地登记"
//			resource.url = "landRegister"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
//			
//			resource = new Resource()
//			resource.resourceName = "房屋登记"
//			resource.url = "houseRegister"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
//			
//			resource = new Resource()
//			resource.resourceName = "设备登记"
//			resource.url = "deviceRegister"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
//			
//			resource = new Resource()
//			resource.resourceName = "图书登记"
//			resource.url = "bookRegister"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
//			
//			resource = new Resource()
//			resource.resourceName = "家具登记"
//			resource.url = "furnitureRegister"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
//			model.save()
			
			model = new Model(company:company)
			model.modelName = "资产卡片"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "assetCards"
			model.serialNo = 9
			
			resource = new Resource()
			resource.resourceName = "房屋及建筑物资产"
			resource.url = "houseCards"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "运输工具资产"
			resource.url = "carCards"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "办公家具资产"
			resource.url = "furnitureCards"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "电子设备资产"
			resource.url = "deviceCards"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
					
//			resource = new Resource()
//			resource.resourceName = "土地资产"
//			resource.url = "landCards"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
//			
//			resource = new Resource()
//			resource.resourceName = "图书资产"
//			resource.url = "bookCards"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
			
			model.save()
			
			model = new Model(company:company)
			model.modelName = "资产变动"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "assetChange"
			model.serialNo = 10
			
			resource = new Resource()
			resource.resourceName = "资产调拨"
			resource.url = "assetAllocate"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
//			resource.resourceName = "报废报损"
			resource.resourceName = "资产报废"
			resource.url = "assetScrap"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "资产报失"
			resource.url = "assetLose"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "资产报修"
			resource.url = "assetRepair"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
//			resource = new Resource()
//			resource.resourceName = "增值减值"
//			resource.url = "assetAddDelete"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
			
//			resource = new Resource()
//			resource.resourceName = "资产调出"
//			resource.url = "assetExport"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
			
//			resource = new Resource()
//			resource.resourceName = "资产退库"
//			resource.url = "assetQuit"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
			
//			model = new Model(company:company)
//			model.modelName = "资产运维"
//			model.modelUrl = path + "/system/navigation"
//			model.modelCode = "assetMaintain"
//			model.serialNo = 9
//			
//			resource = new Resource()
//			resource.resourceName = "资产报修"
//			resource.url = "assetRepair"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
			model.save()
			
			model = new Model(company:company)
			model.modelName = "资产核查"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "assetCheck"
			model.serialNo = 11
			
			resource = new Resource()
			resource.resourceName = "资产卡片查询"
			resource.url = "assetHc"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "盘点任务发布"
			resource.url = "assetRwfb"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "我的盘点任务"
			resource.url = "myPdrw"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			model.save()
			
//			model = new Model(company:company)
//			model.modelName = "资产上报"
//			model.modelUrl = path + "/system/navigation"
//			model.modelCode = "assetReport"
//			model.serialNo = 12
//			
//			resource = new Resource()
//			resource.resourceName = "财政上报"
//			resource.url = "fiscalReport"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
//			model.save()
			
			model = new Model(company:company)
			model.modelName = "统计分析"
			model.modelUrl = path + "/system/navigation"
			model.modelCode = "assetStatist"
			model.description ="统计分析"
			model.serialNo = 13
			
//			resource = new Resource()
//			resource.resourceName = "档案报表"
//			resource.url = "staticDesign"
//			resource.imgUrl = "images/rosten/navigation/rosten.png"
//			model.addToResources(resource)
			
			resource = new Resource()
			resource.resourceName = "变动查询"
			resource.url = "staticSearch"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			model.save()
			
			resource = new Resource()
			resource.resourceName = "统计汇总"
			resource.url = "staticCollect"
			resource.imgUrl = "images/rosten/navigation/rosten.png"
			model.addToResources(resource)
			model.save()
			
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
			
			def _service = new NormalService()
			_service.serviceName = "协同办公系统"
			_service.company = company
			_service.functionUrl = "http://oa.html"
			_service.imgUrl = "images/rosten/service/oa.gif"
			_service.save(flush:true)
			
			_service = new NormalService()
			_service.serviceName = "人事管理系统"
			_service.company = company
			_service.functionUrl = "http://zcxt.html"
			_service.imgUrl = "images/rosten/service/rsxt.gif"
			_service.save(flush:true)
			
			json = [result:'true']
		}catch(Exception e){
			log.debug(e);
			json = [result:'error']
		}
		render json as JSON
	}
}
