package com.rosten.app.assetMaintain

import com.rosten.app.util.GridUtil

class AssetMaintainService {

    //资产报修
    def getAssetRepair_zuofeiListLayout ={
		def gridUtil = new GridUtil()
		//return gridUtil.buildLayoutJSON(new AssetRepair_zuofei())
	}
	
	def getAssetRepair_zuofeiDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetRepair_zuofei(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetRepair_zuofei ={offset,max,company->
		def c = AssetRepair_zuofei.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getAssetRepair_zuofeiCount ={company->
		def c = AssetRepair_zuofei.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
}
