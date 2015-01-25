package com.rosten.app.assetCards

import com.rosten.app.util.GridUtil
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.assetCards.CarCards
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.assetCards.FurnitureCards

class AssetCardsService {

    //运输工具
	def getCarCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new CarCards())
	}
	
	def getCarCardsDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllCarCards(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllCarCards ={offset,max,company,searchArgs->
		def c = CarCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	
	def getCarCardsCount ={company,searchArgs->
		def c = CarCards.createCriteria()
		def query = { 
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		return c.count(query)
	}
	
	//土地
	def getLandCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new LandCards())
	}
	
	def getLandCardsDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllLandCards(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllLandCards ={offset,max,company,searchArgs->
		def c = LandCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("category") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	
	def getLandCardsCount ={company,searchArgs->
		def c = LandCards.createCriteria()
		def query = { 
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("category") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		return c.count(query)
	}
	
	//电子设备
	def getDeviceCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new DeviceCards())
	}
	
	def getDeviceCardsDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllDeviceCards(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllDeviceCards ={offset,max,company,searchArgs->
		def c = DeviceCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	
	def getDeviceCardsCount ={company,searchArgs->
		def c = DeviceCards.createCriteria()
		def query = { 
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		return c.count(query)
	}
	
	//房屋及建筑物
	def getHouseCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new HouseCards())
	}
	
	def getHouseCardsDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllHouseCards(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllHouseCards ={offset,max,company,searchArgs->
		def c = HouseCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	
	def getHouseCardsCount ={company,searchArgs->
		def c = HouseCards.createCriteria()
		def query = { 
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		return c.count(query)
	}
	
	//图书
	def getBookCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new BookCards())
	}
	
	def getBookCardsDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllBookCards(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllBookCards ={offset,max,company,searchArgs->
		def c = BookCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("category") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	
	def getBookCardsCount ={company,searchArgs->
		def c = BookCards.createCriteria()
		def query = { 
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("category") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		return c.count(query)
	}
	
	//办公家具
	def getFurnitureCardsListLayout ={
		def gridUtil = new GridUtil()
		return gridUtil.buildLayoutJSON(new FurnitureCards())
	}
	
	def getFurnitureCardsDataStore ={params,searchArgs->
		Integer offset = (params.offset)?params.offset.toInteger():0
		Integer max = (params.max)?params.max.toInteger():15
		def propertyList = getAllFurnitureCards(offset,max,params.company,searchArgs)

		def gridUtil = new GridUtil()
		return gridUtil.buildDataList("id","title",propertyList,offset)
	}
	
	def getAllFurnitureCards ={offset,max,company,searchArgs->
		def c = FurnitureCards.createCriteria()
		def pa=[max:max,offset:offset]
		def query = {
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("createDate", "desc")
		}
		return c.list(pa,query)
	}
	
	def getFurnitureCardsCount ={company,searchArgs->
		def c = FurnitureCards.createCriteria()
		def query = { 
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		return c.count(query)
	}
}
