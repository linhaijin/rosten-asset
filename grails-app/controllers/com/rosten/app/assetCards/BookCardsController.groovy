package com.rosten.app.assetCards

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart
import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.assetApply.ApplyNotes

class BookCardsController {

    def assetCardsService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	def bookCardsForm ={
		def currentUser = springSecurityService.getCurrentUser()
		def webPath = request.getContextPath() + "/"
		def strname = "bookCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		if(currentUser.getAllRolesValue().contains("系统管理员") || currentUser.getAllRolesValue().contains("资产管理员")){
			actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		}
		
		render actionList as JSON
	}
	
	def bookCardsView ={
		def actionList =[]
		def strname = "bookCards"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
//		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
//		actionList << createAction("提交",imgPath + "submit.png",strname + "_submit")
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
	
	def bookCardsSearchView ={
		def model =[:]
		
		def company = Company.get(params.companyId)
		model["DepartList"] = Depart.findAllByCompany(company)
		
		render(view:'/assetCards/bookCardsSearch',model:model)
	}
	
	def bookCardsAdd ={
		redirect(action:"bookCardsShow",params:params)
	}
	
	def bookCardsShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def bookCards = new BookCards()
		if(params.id){
			bookCards = BookCards.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["bookCards"] = bookCards
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetCards/bookCards',model:model)
	}
	
	def bookCardsSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//图书资产信息保存-------------------------------
		def bookCards = new BookCards()
		if(params.id && !"".equals(params.id)){
			bookCards = BookCards.get(params.id)
//			//处理申请单金额
//			def applyNotesId = bookCards.applyNotes.id
//			def applyNotes = ApplyNotes.get(applyNotesId)
//			applyNotes.totalPrice += params.onePrice.toDouble()
		}else{
			bookCards.company = company
			//新资产卡片编号
			if(!params.registerNum_form.equals("")){
				bookCards.registerNum = params.registerNum_form
			}
		}
		bookCards.properties = params
		bookCards.clearErrors()
		
		//特殊字段信息处理
		bookCards.buyDate = Util.convertToTimestamp(params.buyDate)
		bookCards.userDepart = Depart.get(params.allowdepartsId)
		
		if(bookCards.save(flush:true)){
			json["result"] = "true"
		}else{
			bookCards.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def bookCardsDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def bookCards = BookCards.get(it)
				if(bookCards){
					bookCards.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def bookCardsSubmit ={
		def ids = params.id.split(",")
		def json
		def assetStatus
		def assetCount
		def company = Company.get(params.companyId)
		try{
			ids.each{
				def bookCards = BookCards.get(it)
				if(bookCards){
					assetStatus = bookCards.assetStatus
					if(assetStatus=="新建"){
						bookCards.assetStatus = "已入库"
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
	
	def bookCardsGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCardsService.getBookCardsListLayout()
		}
		
		//增加查询条件
		def searchArgs =[:]
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		if(params.assetStatus && !"".equals(params.assetStatus)) searchArgs["assetStatus"] = params.assetStatus
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCardsService.getBookCardsDataStore(args,searchArgs)
			
		}
		if(params.refreshPageControl){
			def total = assetCardsService.getBookCardsCount(company,searchArgs)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
