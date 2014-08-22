package com.rosten.app.assetMaintain

import com.rosten.app.util.GridUtil

class AssetMaintainService {

    //资产报修
    def getAssetRepairListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new AssetRepair())
	}
	
	def getAssetRepairDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetRepair(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetRepair ={offset,max,company->
		def c = AssetRepair.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getAssetRepairCount ={company->
		def c = AssetRepair.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
}
