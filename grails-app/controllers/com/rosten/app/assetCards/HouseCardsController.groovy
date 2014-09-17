package com.rosten.app.assetCards

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart
import com.rosten.app.assetConfig.AssetCategory

class HouseCardsController {

    def assetCardsService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	def houseCardsForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "houseCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def houseCardsView ={
		def actionList =[]
		def strname = "houseCards"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
//		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除",imgPath + "read.gif",strname + "_delete")
		actionList << createAction("提交",imgPath + "hf.gif",strname + "_submit")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	private def createAction={name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
	
	def houseCardsAdd ={
		redirect(action:"houseCardsShow",params:params)
	}
	
	def houseCardsShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def houseCards = new HouseCards()
		if(params.id){
			houseCards = HouseCards.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["houseCards"] = houseCards
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetCards/houseCards',model:model)
	}
	
	def houseCardsSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//房屋资产信息保存-------------------------------
		def houseCards = new HouseCards()
		if(params.id && !"".equals(params.id)){
			houseCards = HouseCards.get(params.id)
		}else{
			houseCards.company = company
		}
		houseCards.properties = params
		houseCards.clearErrors()
		
		//特殊字段信息处理
		houseCards.buyDate = Util.convertToTimestamp(params.buyDate)
		houseCards.userDepart = Depart.get(params.allowdepartsId)
		if(!params.registerNum_form.equals("")){
			houseCards.registerNum = params.registerNum_form
		}
		
		if(houseCards.save(flush:true)){
			json["result"] = "true"
		}else{
			houseCards.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def houseCardsDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def houseCards = HouseCards.get(it)
				if(houseCards){
					houseCards.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def houseCardsSubmit ={
		def ids = params.id.split(",")
		def json
		def assetStatus
		def assetCount
		def company = Company.get(params.companyId)
		try{
			ids.each{
				def houseCards = HouseCards.get(it)
				if(houseCards){
					assetStatus = houseCards.assetStatus
					if(assetStatus=="新建"){
						houseCards.assetStatus = "已入库"
					}
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	
	def houseCardsGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCardsService.getHouseCardsListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCardsService.getHouseCardsDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetCardsService.getHouseCardsCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
