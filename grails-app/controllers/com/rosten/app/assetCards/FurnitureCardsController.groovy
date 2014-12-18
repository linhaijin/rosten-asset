package com.rosten.app.assetCards

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.Util

import com.rosten.app.system.Company
import com.rosten.app.system.Depart
import com.rosten.app.system.User
import com.rosten.app.system.UserGroup

import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.assetApply.ApplyNotes
import com.rosten.app.assetCards.FurnitureCards
import com.rosten.app.barcode.Barcode

import com.rosten.app.export.ExcelExport

class FurnitureCardsController {

    def assetCardsService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	def furnitureCardsForm ={
		//增加资产管理员群组的控制权限
		def currentUser = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(currentUser).collect { elem ->
		  elem.group.groupName
		}
		def webPath = request.getContextPath() + "/"
		def strname = "furnitureCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		if("zcgly" in userGroups || "xhzcgly" in userGroups){
			actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		}
		
		render actionList as JSON
	}
	
	def furnitureCardsPrintForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "furnitureCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("打印",webPath + imgPath + "word_print.png",strname + "_print")
		
		render actionList as JSON
	}
	
	def furnitureCardsView ={
		def actionList =[]
		def strname = "furnitureCards"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
//		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
//		actionList << createAction("入库",imgPath + "submit.png",strname + "_submit")
		actionList << createAction("导出",imgPath + "export.png",strname + "_export")
		actionList << createAction("打印条形码",imgPath + "word_print.png",strname + "_printTxm")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
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
	
	def furnitureCardsSearchView ={
		def model =[:]
		
		def company = Company.get(params.companyId)
		model["DepartList"] = Depart.findAllByCompany(company)
		
		def parentCategory = AssetCategory.findByCategoryCode("furniture")
		model["categoryList"] = AssetCategory.findAllByCompanyAndParent(company,parentCategory)
		
		render(view:'/assetCards/furnitureCardsSearch',model:model)
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
//			//处理申请单金额
//			def applyNotesId = furnitureCards.applyNotes.id
//			def applyNotes = ApplyNotes.get(applyNotesId)
//			applyNotes.totalPrice += params.onePrice.toDouble()
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
		furnitureCards.userCategory = AssetCategory.get(params.allowCategoryId)
		
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
		
		//增加查询条件
		def searchArgs =[:]
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		
		def parentCategory = AssetCategory.findByCategoryCode("furniture")
		if(params.category && !"".equals(params.category)) searchArgs["userCategory"] = AssetCategory.findByCompanyAndCategoryNameAndParent(company,params.category,parentCategory)
		
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCardsService.getFurnitureCardsDataStore(args,searchArgs)
			
		}
		if(params.refreshPageControl){
			def total = assetCardsService.getFurnitureCardsCount(company,searchArgs)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def furnitureCardsExport = {
		OutputStream os = response.outputStream
		def company = Company.get(params.companyId)
		response.setContentType('application/vnd.ms-excel')
		response.setHeader("Content-disposition", "attachment; filename=" + new String("办公家具资产卡片信息.xls".getBytes("GB2312"), "ISO_8859_1"))
		
		//增加查询条件
		def searchArgs =[:]
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		if(params.category && !"".equals(params.category)) searchArgs["category"] = AssetCategory.findByCompanyAndCategoryName(company,params.category)
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		
		def c = FurnitureCards.createCriteria()

		def furnitureCardsList = c.list{
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("category") || k.equals("userDepart")){
					eq(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		def excel = new ExcelExport()
		excel.furnitureCardsDc(os,furnitureCardsList)
	}
	
	def getBarcode = {
		def registerNum
		if(params.registerNum && !"".equals(params.registerNum)){
			registerNum = params.registerNum
		}
		def _util = new Barcode()
		byte[] b = new byte[1024];
		int len = -1;
		InputStream imageStream = new ByteArrayInputStream(_util.txmStr(registerNum));
		
		while ((len = imageStream.read(b, 0, 1024)) != -1) {
		  response.outputStream.write(b, 0, len);
		}
		response.outputStream.flush()
		response.outputStream.close()
	}
	
	//2014-12-08增加条形码打印功能
	def furnitureCardsPrintTxm ={
		def model =[:]
		
		def currentUser = springSecurityService.getCurrentUser()
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		//增加查询条件
		def searchArgs =[:]
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		if(params.category && !"".equals(params.category)) searchArgs["category"] = AssetCategory.findByCompanyAndCategoryName(company,params.category)
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		
		def c = FurnitureCards.createCriteria()

		def furnitureCardsList = c.list{
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("category") || k.equals("userDepart")){
					eq(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		
		model["user"] = user
		model["company"] = company
		model["furnitureCardsList"] = furnitureCardsList
		model["countSize"] = furnitureCardsList.size()
		
		render(view:'/assetCards/furnitureCardsPrintTxm',model:model)
	}
}
