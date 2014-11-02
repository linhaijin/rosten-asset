package com.rosten.app.assetCards

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart
import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.assetApply.ApplyNotes

class FurnitureCardsController {

    def assetCardsService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	def furnitureCardsForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "furnitureCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
//		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def furnitureCardsView ={
		def actionList =[]
		def strname = "furnitureCards"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
//		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		actionList << createAction("提交",imgPath + "submit.png",strname + "_submit")
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
	
	def furnitureCardsAdd ={
		redirect(action:"furnitureCardsShow",params:params)
	}
	
	def furnitureCardsShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def furnitureCards = new FurnitureCards()
		if(params.id){
			furnitureCards = FurnitureCards.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["furnitureCards"] = furnitureCards
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetCards/furnitureCards',model:model)
	}
	
	def furnitureCardsSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//家具资产信息保存-------------------------------
		def furnitureCards = new FurnitureCards()
		if(params.id && !"".equals(params.id)){
			furnitureCards = FurnitureCards.get(params.id)
			//处理申请单金额
			def applyNotesId = furnitureCards.applyNotes.id
			def applyNotes = ApplyNotes.get(applyNotesId)
			applyNotes.totalPrice += params.onePrice.toDouble()
		}else{
			furnitureCards.company = company
			//新资产卡片编号
			if(!params.registerNum_form.equals("")){
				furnitureCards.registerNum = params.registerNum_form
			}
		}
		furnitureCards.properties = params
		furnitureCards.clearErrors()
		
		//特殊字段信息处理
		furnitureCards.buyDate = Util.convertToTimestamp(params.buyDate)
		furnitureCards.userDepart = Depart.get(params.allowdepartsId)
		
		if(furnitureCards.save(flush:true)){
			json["result"] = "true"
		}else{
			furnitureCards.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def furnitureCardsDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def furnitureCards = FurnitureCards.get(it)
				if(furnitureCards){
					furnitureCards.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def furnitureCardsSubmit ={
		def ids = params.id.split(",")
		def json
		def assetStatus
		def assetCount
		def company = Company.get(params.companyId)
		try{
			ids.each{
				def furnitureCards = FurnitureCards.get(it)
				if(furnitureCards){
					assetStatus = furnitureCards.assetStatus
					if(assetStatus=="新建"){
						furnitureCards.assetStatus = "已入库"
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
	
	def furnitureCardsGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCardsService.getFurnitureCardsListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCardsService.getFurnitureCardsDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetCardsService.getFurnitureCardsCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
