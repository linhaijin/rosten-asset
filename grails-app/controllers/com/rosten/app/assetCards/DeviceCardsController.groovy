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
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.barcode.Barcode

import com.rosten.app.export.ExcelExport
import com.rosten.app.share.ShareService

class DeviceCardsController {
	def shareService
    def assetCardsService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	def deviceCardsForm ={
		//增加资产管理员群组的控制权限
		def currentUser = springSecurityService.getCurrentUser()
		def userGroups = UserGroup.findAllByUser(currentUser).collect { elem ->
		  elem.group.groupName
		}
		def webPath = request.getContextPath() + "/"
		def strname = "deviceCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		if("zcgly" in userGroups || "xhzcgly" in userGroups){
			actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		}
		
		render actionList as JSON
	}
	
	def deviceCardsPrintForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "deviceCards"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("打印",webPath + imgPath + "word_print.png",strname + "_print")
		
		render actionList as JSON
	}
	
	def deviceCardsView ={
		def actionList =[]
		def strname = "deviceCards"
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
	
	def deviceCardsSearchView ={
		def model =[:]
		
		def company = Company.get(params.companyId)
		model["company"] = company
//		model["DepartList"] = Depart.findAllByCompany(company)
		
		def parentCategory = AssetCategory.findByCategoryCode("device")
		model["categoryList"] = AssetCategory.findAllByCompanyAndParent(company,parentCategory)
		
		render(view:'/assetCards/deviceCardsSearch',model:model)
	}
	
	def deviceCardsAdd ={
		redirect(action:"deviceCardsShow",params:params)
	}
	
	def deviceCardsShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def deviceCards = new DeviceCards()
		if(params.id){
			deviceCards = DeviceCards.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["deviceCards"] = deviceCards
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetCards/deviceCards',model:model)
	}
	
	def deviceCardsSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//设备资产信息保存-------------------------------
		def deviceCards = new DeviceCards()
		if(params.id && !"".equals(params.id)){
			deviceCards = DeviceCards.get(params.id)
//			//处理申请单金额
//			def applyNotesId =deviceCards.applyNotes.id
//			def applyNotes = ApplyNotes.get(applyNotesId)
//			applyNotes.totalPrice += params.onePrice.toDouble()
		}else{
			deviceCards.company = company
			//新资产卡片编号
			if(!params.registerNum_form.equals("")){
				deviceCards.registerNum = params.registerNum_form
			}
		}
		deviceCards.properties = params
		deviceCards.clearErrors()
		
		//特殊字段信息处理
		deviceCards.buyDate = Util.convertToTimestamp(params.buyDate)
		deviceCards.userDepart = Depart.get(params.allowdepartsId)
		deviceCards.userCategory = AssetCategory.get(params.allowCategoryId)
		
		if(deviceCards.save(flush:true)){
			json["result"] = "true"
		}else{
			deviceCards.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def deviceCardsDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def deviceCards = DeviceCards.get(it)
				if(deviceCards){
					deviceCards.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def deviceCardsSubmit ={
		def ids = params.id.split(",")
		def json
		def assetStatus
		def assetCount
		def company = Company.get(params.companyId)
		try{
			ids.each{
				def deviceCards = DeviceCards.get(it)
				if(deviceCards){
					assetStatus = deviceCards.assetStatus
					if(assetStatus=="新建"){
						deviceCards.assetStatus = "已入库"
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
	
	def deviceCardsGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCardsService.getDeviceCardsListLayout()
		}
		
		//增加查询条件
		def searchArgs =[:]
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		
		def parentCategory = AssetCategory.findByCategoryCode("device")
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
//		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCardsService.getDeviceCardsDataStore(args,searchArgs)
			
		}
		if(params.refreshPageControl){
			def total = assetCardsService.getDeviceCardsCount(company,searchArgs)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def deviceCardsExport = {
		OutputStream os = response.outputStream
		def company = Company.get(params.companyId)
		response.setContentType('application/vnd.ms-excel')
		response.setHeader("Content-disposition", "attachment; filename=" + new String("电子设备资产卡片信息.xls".getBytes("GB2312"), "ISO_8859_1"))
		
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
//		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		
		def c = DeviceCards.createCriteria()

		def deviceCardsList = c.list{
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
		excel.deviceCardsDc(os,deviceCardsList)
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
	def deviceCardsPrintTxm ={
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
//		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		
		def c = DeviceCards.createCriteria()

		def deviceCardsList = c.list{
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
		model["deviceCardsList"] = deviceCardsList
		model["countSize"] = deviceCardsList.size()
		
		render(view:'/assetCards/deviceCardsPrintTxm',model:model)
	}
}
