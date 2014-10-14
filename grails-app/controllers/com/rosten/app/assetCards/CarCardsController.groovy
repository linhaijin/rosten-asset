package com.rosten.app.assetCards

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart
import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.assetApply.ApplyNotes

class CarCardsController {

    def assetCardsService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	def carCardsForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "carCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def carCardsView ={
		def actionList =[]
		def strname = "carCards"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
//		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("入库",imgPath + "submit.png",strname + "_submit")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		
		render actionList as JSON
	}
	
	private def createAction={name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
	
	def carCardsAdd ={
		redirect(action:"carCardsShow",params:params)
	}
	
	def carCardsShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def carCards = new CarCards()
		if(params.id){
			carCards = CarCards.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["carCards"] = carCards
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetCards/carCards',model:model)
	}
	
	def carCardsSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//车辆资产信息保存-------------------------------
		def carCards = new CarCards()
		if(params.id && !"".equals(params.id)){
			carCards = CarCards.get(params.id)
			//处理申请单金额
			def applyNotesId =carCards.applyNotes.id
			def applyNotes = ApplyNotes.get(applyNotesId)
			applyNotes.totalPrice += params.onePrice.toDouble()
		}else{
			carCards.company = company
			//新资产卡片编号
			if(!params.registerNum_form.equals("")){
				carCards.registerNum = params.registerNum_form
			}
		}
		carCards.properties = params
		carCards.clearErrors()
		
		//特殊字段信息处理
		carCards.buyDate = Util.convertToTimestamp(params.buyDate)
		carCards.userDepart = Depart.get(params.allowdepartsId)
		
		if(carCards.save(flush:true)){
			json["result"] = "true"
		}else{
			carCards.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def carCardsDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def carCards = CarCards.get(it)
				if(carCards){
					carCards.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def carCardsSubmit ={
		def ids = params.id.split(",")
		def json
		def assetStatus
		def assetCount
		def company = Company.get(params.companyId)
		try{
			ids.each{
				def carCards = CarCards.get(it)
				if(carCards){
					assetStatus = carCards.assetStatus
					if(assetStatus=="新建"){
						carCards.assetStatus = "已入库"
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
	
	def carCardsGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCardsService.getCarCardsListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCardsService.getCarCardsDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetCardsService.getCarCardsCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
