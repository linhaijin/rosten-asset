package com.rosten.app.assetCards

import com.rosten.app.util.GridUtil

class AssetCardsService {

    //车辆资产
	def getCarCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new CarCards())
	}
	
	def getCarCardsDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllCarCards(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllCarCards ={offset,max,company->
		def c = CarCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getCarCardsCount ={company->
		def c = CarCards.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
	//土地资产
	def getLandCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new LandCards())
	}
	
	def getLandCardsDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllLandCards(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllLandCards ={offset,max,company->
		def c = LandCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getLandCardsCount ={company->
		def c = LandCards.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
	
	//设备资产
	def getDeviceCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new DeviceCards())
	}
	
	def getDeviceCardsDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllDeviceCards(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllDeviceCards ={offset,max,company->
		def c = DeviceCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getDeviceCardsCount ={company->
		def c = DeviceCards.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
	//房屋资产
	def getHouseCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new HouseCards())
	}
	
	def getHouseCardsDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllHouseCards(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllHouseCards ={offset,max,company->
		def c = HouseCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getHouseCardsCount ={company->
		def c = HouseCards.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
	//图书资产
	def getBookCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new BookCards())
	}
	
	def getBookCardsDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllBookCards(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllBookCards ={offset,max,company->
		def c = BookCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getBookCardsCount ={company->
		def c = BookCards.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
	
	//家具资产
	def getFurnitureCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new FurnitureCards())
	}
	
	def getFurnitureCardsDataStore ={params->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllFurnitureCards(offset,max,params.company)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllFurnitureCards ={offset,max,company->
		def c = FurnitureCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
		}
		return c.list(pa,query)
	}
	
	def getFurnitureCardsCount ={company->
		def c = FurnitureCards.createCriteria()
		def query = { eq("company",company) }
		return c.count(query)
	}
}
