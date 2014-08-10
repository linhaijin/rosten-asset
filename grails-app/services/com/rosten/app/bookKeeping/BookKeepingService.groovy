package com.rosten.app.bookKeeping

import com.rosten.app.util.GridUtil

class BookKeepingService {
	//车辆登记
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
	//土地登记
	def getLandRegisterListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new LandRegister())
	}
	
	def getLandRegisterDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllLandRegister(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllLandRegister ={offset,max,company->
		def c = LandRegister.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getLandRegisterCount ={company->
		def c = LandRegister.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	//房屋登记
	def getHouseRegisterListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new HouseRegister())
	}
	
	def getHouseRegisterDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllHouseRegister(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllHouseRegister ={offset,max,company->
		def c = HouseRegister.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getHouseRegisterCount ={company->
		def c = HouseRegister.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	//设备登记
	def getDeviceRegisterListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new DeviceRegister())
	}
	
	def getDeviceRegisterDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllDeviceRegister(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllDeviceRegister ={offset,max,company->
		def c = DeviceRegister.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getDeviceRegisterCount ={company->
		def c = DeviceRegister.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	//图书登记
	def getBookRegisterListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new BookRegister())
	}
	
	def getBookRegisterDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllBookRegister(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllBookRegister ={offset,max,company->
		def c = BookRegister.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getBookRegisterCount ={company->
		def c = BookRegister.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	//家具登记
	def getFurnitureRegisterListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new FurnitureRegister())
	}
	
	def getFurnitureRegisterDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllFurnitureRegister(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllFurnitureRegister ={offset,max,company->
		def c = FurnitureRegister.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getFurnitureRegisterCount ={company->
		def c = FurnitureRegister.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
}
