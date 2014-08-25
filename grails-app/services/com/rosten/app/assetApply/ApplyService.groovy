package com.rosten.app.assetApply
import com.rosten.app.util.GridUtil

class ApplyService {
	def getListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new Apply())
	}
	
	def getDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAll(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAll ={offset,max,company->
		def c = Apply.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getCount ={company->
		def c = Apply.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
    def serviceMethod() {

    }
}
