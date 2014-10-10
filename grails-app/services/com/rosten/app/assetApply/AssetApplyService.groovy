package com.rosten.app.assetApply

import com.rosten.app.util.GridUtil

class AssetApplyService {

	def springSecurityService
	
	def getMineApplyDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllMineApply(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllMineApply ={offset,max,company->
		def user = springSecurityService.getCurrentUser()
		def c = ApplyNotes.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			eq("applyUser",user)
		}
		return c.list(pa,query)
	}
	
	def getMineApplyCount ={company->
		def user = springSecurityService.getCurrentUser()
		def c = ApplyNotes.createCriteria()
		def query = {
			eq("company",company)
			eq("applyUser",user)
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
			eq("applyUser",user)
			ne("applyStatus","新建")
		}
		return c.list(pa,query)
	}
	
	def getAssetApplyCount ={company->
		def user = springSecurityService.getCurrentUser()
		def c = ApplyNotes.createCriteria()
		def query = { 
			eq("company",company) 
			eq("applyUser",user)
		}
		return c.count(query)
	}
	
    def serviceMethod() {

    }
}
