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
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.barcode.Barcode

import com.rosten.app.export.ExcelExport
import com.rosten.app.share.ShareService

class HouseCardsController {
	def shareService
    def assetCardsService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	def houseCardsForm ={
		//增加资产管理员群组的控制权限
		def currentUser = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(currentUser).collect { elem ->
		  elem.group.groupName
		}
		def webPath = request.getContextPath() + "/"
		def strname = "houseCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		if("zcgly" in userGroups || "xhzcgly" in userGroups || "资产管理员" in userGroups || "协会资产管理员" in userGroups){
			actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		}
		
		render actionList as JSON
	}
	
	def houseCardsPrintForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "houseCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("打印",webPath + imgPath + "word_print.png",strname + "_print")
		
		render actionList as JSON
	}
	
	def houseCardsView ={
		def actionList =[]
		def strname = "houseCards"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
//		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
//		actionList << createAction("入库",imgPath + "submit.png",strname + "_submit")
		actionList << createAction("导出",imgPath + "export.png",strname + "_export")
		actionList << createAction("打印条形码",imgPath + "word_print.png",strname + "_printTxm")
		actionList << createAction("修改卡片状态",imgPath + "init.gif","asset_changeStatus")
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
	
	def houseCardsSearchView ={
		def model =[:]
		
		def company = Company.get(params.companyId)
		model["company"] = company
//		model["DepartList"] = Depart.findAllByCompany(company)
		
		def parentCategory = AssetCategory.findByCategoryCode("house")
		model["categoryList"] = AssetCategory.findAllByCompanyAndParent(company,parentCategory)
		
		render(view:'/assetCards/houseCardsSearch',model:model)
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
//			//处理申请单金额
//			def applyNotesId =houseCards.applyNotes.id
//			def applyNotes = ApplyNotes.get(applyNotesId)
//			applyNotes.totalPrice += params.onePrice.toDouble()
		}else{
			houseCards.company = company
			//新资产卡片编号
			if(!params.registerNum_form.equals("")){
				houseCards.registerNum = params.registerNum_form
			}
		}
		houseCards.properties = params
		houseCards.clearErrors()
		
		//特殊字段信息处理
		houseCards.buyDate = Util.convertToTimestamp(params.buyDate)
		houseCards.userDepart = Depart.get(params.allowdepartsId)
		houseCards.userCategory = AssetCategory.get(params.allowCategoryId)

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
		
		//增加查询条件
		def searchArgs =[:]
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		def parentCategory = AssetCategory.findByCategoryCode("house")
		if(params.category && !"".equals(params.category)) searchArgs["userCategory"] = AssetCategory.findByCompanyAndCategoryNameAndParent(company,params.category,parentCategory)
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		def userDepartList = []
		if(params.userDepart && !"".equals(params.userDepart)){
			params.userDepart.split(",").each{
				def _list = []
				userDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
			}
			searchArgs["userDepart"] = userDepartList.unique()
		}
		if(params.assetStatus && !"".equals(params.assetStatus)) searchArgs["assetStatus"] = params.assetStatus
//		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCardsService.getHouseCardsDataStore(args,searchArgs)
			
		}
		if(params.refreshPageControl){
			def total = assetCardsService.getHouseCardsCount(company,searchArgs)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def houseCardsExport = {
		OutputStream os = response.outputStream
		def company = Company.get(params.companyId)
		response.setContentType('application/vnd.ms-excel')
		response.setHeader("Content-disposition", "attachment; filename=" + new String("房屋及建筑物资产卡片信息.xls".getBytes("GB2312"), "ISO_8859_1"))
		
		//增加查询条件
		def searchArgs =[:]
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		if(params.category && !"".equals(params.category)) searchArgs["category"] = AssetCategory.findByCompanyAndCategoryName(company,params.category)
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		def userDepartList = []
		if(params.userDepart && !"".equals(params.userDepart)){
			params.userDepart.split(",").each{
				def _list = []
				userDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
			}
			searchArgs["userDepart"] = userDepartList.unique()
		}
		if(params.assetStatus && !"".equals(params.assetStatus)) searchArgs["assetStatus"] = params.assetStatus
//		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		
		def c = HouseCards.createCriteria()

		def houseCardsList = c.list{
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("category") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		def excel = new ExcelExport()
		excel.houseCardsDc(os,houseCardsList)
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
	def houseCardsPrintTxm ={
		def model =[:]
		
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		//增加查询条件
		def searchArgs =[:]
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		if(params.category && !"".equals(params.category)) searchArgs["category"] = AssetCategory.findByCompanyAndCategoryName(company,params.category)
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		def userDepartList = []
		if(params.userDepart && !"".equals(params.userDepart)){
			params.userDepart.split(",").each{
				def _list = []
				userDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
			}
			searchArgs["userDepart"] = userDepartList.unique()
		}
		if(params.assetStatus && !"".equals(params.assetStatus)) searchArgs["assetStatus"] = params.assetStatus
//		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		
		def c = HouseCards.createCriteria()

		def houseCardsList = c.list{
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("category") || k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
		}
		
		model["user"] = user
		model["company"] = company
		model["houseCardsList"] = houseCardsList
		model["countSize"] = houseCardsList.size()
		
		render(view:'/assetCards/houseCardsPrintTxm',model:model)
	}
}
