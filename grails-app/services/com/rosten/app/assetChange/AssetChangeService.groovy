package com.rosten.app.assetChange

import com.rosten.app.util.GridUtil
import com.rosten.app.system.UserGroup

class AssetChangeService {
	def springSecurityService
	//资产报废
    def getAssetScrapListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new AssetScrap())
	}
	
	def getAssetScrapDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetScrap(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetScrap ={offset,max,company,searchArgs->
		def user = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(user).collect { elem ->
			elem.group.groupName
		}
		def c = AssetScrap.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			if("xhzcgly" in userGroups || "zcgly" in userGroups || "协会资产管理员" in userGroups || "资产管理员" in userGroups || user.getAllRolesValue().contains("资产管理员")){
				eq("company",company)
			}else{
				eq("company",company)
				or{
					eq("drafter",user)
					eq("currentUser",user)
					eq("status","已结束")
				}
			}
			searchArgs.each{k,v->
				if(k.equals("usedDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	
	def getAssetScrapCount ={company,searchArgs->
		def user = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(user).collect { elem ->
			elem.group.groupName
		}
		def c = AssetScrap.createCriteria()
		def query = { 
			if("xhzcgly" in userGroups || "zcgly" in userGroups || "协会资产管理员" in userGroups || "资产管理员" in userGroups || user.getAllRolesValue().contains("资产管理员")){
				eq("company",company)
			}else{
				eq("company",company)
				or{
					eq("drafter",user)
					eq("currentUser",user)
					eq("status","已结束")
				}
			}
			searchArgs.each{k,v->
				if(k.equals("usedDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		return c.count(query)
	}
	
	//资产调拨
	def getAssetAllocateListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new AssetAllocate())
	}
	
	def getAssetAllocateDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetAllocate(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetAllocate ={offset,max,company,searchArgs->
		def user = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(user).collect { elem ->
			elem.group.groupName
		}
		def c = AssetAllocate.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			if("xhzcgly" in userGroups || "zcgly" in userGroups || "协会资产管理员" in userGroups || "资产管理员" in userGroups || user.getAllRolesValue().contains("资产管理员")){
				eq("company",company)
			}else{
				eq("company",company)
				or{
					eq("drafter",user)
					eq("currentUser",user)
					eq("status","已结束")
				}
			}
			searchArgs.each{k,v->
				if(k.equals("originalDepart") || k.equals("newDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	
	def getAssetAllocateCount ={company,searchArgs->
		def user = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(user).collect { elem ->
			elem.group.groupName
		}
		def c = AssetAllocate.createCriteria()
		def query = { 
			if("xhzcgly" in userGroups || "zcgly" in userGroups || "协会资产管理员" in userGroups || "资产管理员" in userGroups || user.getAllRolesValue().contains("资产管理员")){
				eq("company",company)
			}else{
				eq("company",company)
				or{
					eq("drafter",user)
					eq("currentUser",user)
					eq("status","已结束")
				}
			}
			searchArgs.each{k,v->
				if(k.equals("originalDepart") || k.equals("newDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		return c.count(query)
	}
	
	//资产报失
	def getAssetLoseListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new AssetLose())
	}
	
	def getAssetLoseDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetLose(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetLose ={offset,max,company,searchArgs->
		def user = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(user).collect { elem ->
			elem.group.groupName
		}
		def c = AssetLose.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			if("xhzcgly" in userGroups || "zcgly" in userGroups || "协会资产管理员" in userGroups || "资产管理员" in userGroups || user.getAllRolesValue().contains("资产管理员")){
				eq("company",company)
			}else{
				eq("company",company)
				or{
					eq("drafter",user)
					eq("currentUser",user)
					eq("status","已结束")
				}
			}
			searchArgs.each{k,v->
				if(k.equals("usedDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	
	def getAssetLoseCount ={company,searchArgs->
		def user = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(user).collect { elem ->
			elem.group.groupName
		}
		def c = AssetLose.createCriteria()
		def query = { 
			if("xhzcgly" in userGroups || "zcgly" in userGroups || "协会资产管理员" in userGroups || "资产管理员" in userGroups || user.getAllRolesValue().contains("资产管理员")){
				eq("company",company)
			}else{
				eq("company",company)
				or{
					eq("drafter",user)
					eq("currentUser",user)
					eq("status","已结束")
				}
			}
			searchArgs.each{k,v->
				if(k.equals("usedDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		return c.count(query)
	}
	
	//资产报修
	def getAssetRepairListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new AssetRepair())
	}
	
	def getAssetRepairDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetRepair(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetRepair ={offset,max,company,searchArgs->
		def user = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(user).collect { elem ->
			elem.group.groupName
		}
		def c = AssetRepair.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			if("xhzcgly" in userGroups || "zcgly" in userGroups || "协会资产管理员" in userGroups || "资产管理员" in userGroups || user.getAllRolesValue().contains("资产管理员")){
				eq("company",company)
			}else{
				eq("company",company)
				or{
					eq("drafter",user)
					eq("currentUser",user)
					eq("status","已结束")
				}
			}
			searchArgs.each{k,v->
				if(k.equals("usedDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	
	def getAssetRepairCount ={company,searchArgs->
		def user = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(user).collect { elem ->
			elem.group.groupName
		}
		def c = AssetRepair.createCriteria()
		def query = { 
			if("xhzcgly" in userGroups || "zcgly" in userGroups || "协会资产管理员" in userGroups || "资产管理员" in userGroups || user.getAllRolesValue().contains("资产管理员")){
				eq("company",company)
			}else{
				eq("company",company)
				or{
					eq("drafter",user)
					eq("currentUser",user)
					eq("status","已结束")
				}
			}
			searchArgs.each{k,v->
				if(k.equals("usedDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		return c.count(query)
	}
	
	//增值减值
	/**暂不需要
	def getAssetAddDeleteListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new AssetAddDelete())
	}
	
	def getAssetAddDeleteDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetAddDelete(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetAddDelete ={offset,max,company->
		def c = AssetAddDelete.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getAssetAddDeleteCount ={company->
		def c = AssetAddDelete.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	**/
}
