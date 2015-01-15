package com.rosten.app.assetCards

import grails.converters.JSON

class CardsShareController {
	
	private def getEntity ={entityId->
		def entity = CarCards.get(entityId)
		if(!entity){
			entity = HouseCards.get(entityId)
			if(!entity){
				entity = DeviceCards.get(entityId)
				if(!entity){
					entity = FurnitureCards.get(entityId)
				}
			}
		}
		return entity
	}
	
	def statusSelect ={
		def _list =[]
		
		_list << [id:"001","name":"已入库"]
		_list << [id:"002","name":"已调拨"]
		_list << [id:"003","name":"已报修"]
		_list << [id:"004","name":"已报废"]
		_list << [id:"005","name":"已报失"]
		
		render _list as JSON
	}
	def changeCardsStatus ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def cards = this.getEntity(it)
				if(cards){
					cards.assetStatus=params.status
					cards.save(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
    
}
