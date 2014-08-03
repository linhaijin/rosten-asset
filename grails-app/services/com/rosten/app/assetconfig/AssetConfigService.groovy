package com.rosten.app.assetconfig

import com.rosten.app.util.GridUtil

class AssetConfigService {
	
	def getAssetCategoryListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new AssetCategory())
	}
	def getAssetCategoryListDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetCategory(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	def getAllAssetCategory ={offset,max,company->
		def c = AssetCategory.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			order("createdTime", "asc")
		}
		return c.list(pa,query)
	}
	def getAssetCategoryCount ={company->
		def c = AssetCategory.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
}
