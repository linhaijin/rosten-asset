package com.rosten.app.bookKeeping
import com.rosten.app.util.GridUtil
class CarService {

def getCarRegisterListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new CarRegister())
	}
	
	def getCarRegisterDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllCarRegister(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllCarRegister ={offset,max,company->
		def c = CarRegister.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getCarRegisterCount ={company->
		def c = CarRegister.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
}