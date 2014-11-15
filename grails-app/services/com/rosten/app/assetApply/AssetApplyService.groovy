package com.rosten.app.assetApply

import com.rosten.app.util.GridUtil

class AssetApplyService {

	def springSecurityService
	
	def getMineApplyDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllMineApply(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllMineApply ={offset,max,company,searchArgs->
		def user = springSecurityService.getCurrentUser()
		def c = ApplyNotes.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			if(user.getAllRolesValue().contains("系统管理员") || user.getAllRolesValue().contains("资产管理员")){
				eq("company",company)
			}else{
				eq("company",company)
				or{
					eq("currentUser",user)
					eq("applyUser",user)
				}
			}
			searchArgs.each{k,v->
				if(k.equals("userCategory")){
					eq(k,v)
				}else{
					like(k,"%" + v + "%")
				}
				
			}
		}
		return c.list(pa,query)
	}
	
	def getMineApplyCount ={company,searchArgs->
		def user = springSecurityService.getCurrentUser()
		def c = ApplyNotes.createCriteria()
		def query = {
			if(user.getAllRolesValue().contains("系统管理员") || user.getAllRolesValue().contains("资产管理员")){
				eq("company",company)
			}else{
				eq("company",company)
				or{
					eq("currentUser",user)
					eq("applyUser",user)
				}
			}
			searchArgs.each{k,v->
				if(k.equals("userCategory")){
					eq(k,v)
				}else{
					like(k,"%" + v + "%")
				}
				
			}
		}
		return c.count(query)
	}
	
    def getAssetApplyListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new ApplyNotes())
	}
	
	def getAssetApplyDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetApply(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetApply ={offset,max,company->
		def user = springSecurityService.getCurrentUser()
		def c = ApplyNotes.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			eq("applyStatus","已结束")
		}
		return c.list(pa,query)
	}
	
	def getAssetApplyCount ={company->
		def user = springSecurityService.getCurrentUser()
		def c = ApplyNotes.createCriteria()
		def query = { 
			eq("company",company) 
			eq("applyStatus","已结束")
		}
		return c.count(query)
	}
	
    def serviceMethod() {

    }
}
