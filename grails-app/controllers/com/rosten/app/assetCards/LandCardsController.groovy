package com.rosten.app.assetCards

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart
import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.assetApply.ApplyNotes

class LandCardsController {

	def assetCardsService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	def landCardsForm ={
		def currentUser = springSecurityService.getCurrentUser()
		def webPath = request.getContextPath() + "/"
		def strname = "landCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		if(currentUser.getAllRolesValue().contains("系统管理员") || currentUser.getAllRolesValue().contains("资产管理员")){
			actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		}
		
		render actionList as JSON
	}
	
	def landCardsView ={
		def actionList =[]
		def strname = "landCards"
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
	
	def landCardsAdd ={
		redirect(action:"landCardsShow",params:params)
	}
	
	def landCardsShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def landCards = new LandCards()
		if(params.id){
			landCards = LandCards.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["landCards"] = landCards
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetCards/landCards',model:model)
	}
	
	def landCardsSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//土地资产信息保存-------------------------------
		def landCards = new LandCards()
		if(params.id && !"".equals(params.id)){
			landCards = LandCards.get(params.id)
			//处理申请单金额
			def applyNotesId =landCards.applyNotes.id
			def applyNotes = ApplyNotes.get(applyNotesId)
			applyNotes.totalPrice += params.onePrice.toDouble()
		}else{
			landCards.company = company
			//新资产卡片编号
			if(!params.registerNum_form.equals("")){
				landCards.registerNum = params.registerNum_form
			}
		}
		
		landCards.properties = params
		landCards.clearErrors()
		
		//特殊字段信息处理
		landCards.buyDate = Util.convertToTimestamp(params.buyDate)
		landCards.userDepart = Depart.get(params.allowdepartsId)
		
		if(landCards.save(flush:true)){
			json["result"] = "true"
		}else{
			landCards.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def landCardsDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def landCards = LandCards.get(it)
				if(landCards){
					landCards.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def landCardsSubmit ={
		def ids = params.id.split(",")
		def json
		def assetStatus
		def assetCount
		def company = Company.get(params.companyId)
		try{
			ids.each{
				def landCards = LandCards.get(it)
				if(landCards){
					assetStatus = landCards.assetStatus
					if(assetStatus=="新建"){
						landCards.assetStatus = "已入库"
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
	
	def landCardsGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCardsService.getLandCardsListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCardsService.getLandCardsDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetCardsService.getLandCardsCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
