package com.rosten.app.assetChange

import com.rosten.app.util.GridUtil

class AssetChangeService {
	//报废报损
    def getAssetScrapListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new AssetScrap())
	}
	
	def getAssetScrapDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetScrap(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetScrap ={offset,max,company->
		def c = AssetScrap.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getAssetScrapCount ={company->
		def c = AssetScrap.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
	//资产调拨
	def getAssetAllocateListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new AssetAllocate())
	}
	
	def getAssetAllocateDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetAllocate(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetAllocate ={offset,max,company->
		def c = AssetAllocate.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getAssetAllocateCount ={company->
		def c = AssetAllocate.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
	//资产报失
	def getAssetLoseListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new AssetLose())
	}
	
	def getAssetLoseDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllAssetLose(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllAssetLose ={offset,max,company->
		def c = AssetLose.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getAssetLoseCount ={company->
		def c = AssetLose.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
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
