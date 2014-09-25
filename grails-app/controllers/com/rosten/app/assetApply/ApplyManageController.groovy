package com.rosten.app.assetApply

import grails.converters.JSON
import com.rosten.app.util.Util
import com.rosten.app.assetApply.ApplyNotes
import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.system.Company
import com.rosten.app.util.FieldAcl

import com.rosten.app.assetCards.CarCards
import com.rosten.app.assetCards.LandCards
import com.rosten.app.assetCards.HouseCards
import com.rosten.app.assetCards.DeviceCards
import com.rosten.app.assetCards.BookCards
import com.rosten.app.assetCards.FurnitureCards

class ApplyManageController {

	def getFormattedSeriesDate(){
		def nowDate= new Date()
		def SeriesDate= "20"+nowDate.time
		return SeriesDate
	}
	
    def imgPath ="images/rosten/actionbar/"
	def assetApplyService
	def springSecurityService
	
	def assetApplyForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "assetApply"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def mineApplyView ={
		def actionList =[]
		def strname = "assetApply"
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("提交",imgPath + "add.png",strname + "_submit")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def applyApprovalView ={
		def actionList =[]
		def strname = "assetApply"
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("同意",imgPath + "add.png",strname + "_agree")
		actionList << createAction("生成资产卡片",imgPath + "add.png","assetCards_create")
		actionList << createAction("财务对接",imgPath + "add.png",strname + "_delete")
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
	
	def assetApplyAdd ={
		redirect(action:"assetApplyShow",params:params)
	}
	
	def assetApplyShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def company = Company.get(params.companyId)
		
		def applyNotes = new ApplyNotes()
		if(params.id){
			applyNotes = ApplyNotes.get(params.id)
		}else{
			applyNotes.applyUser = currentUser
		}
		
		model["user"] = currentUser
		model["company"] = company
		model["applyNotes"] = applyNotes
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		render(view:'/assetApply/applyShow',model:model)
	}
	
	def assetApplySave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//资产申请信息保存-------------------------------
		def applyNotes = new ApplyNotes()
		if(params.id && !"".equals(params.id)){
			applyNotes = ApplyNotes.get(params.id)
		}else{
			applyNotes.company = company
			applyNotes.applyUser = springSecurityService.getCurrentUser()
		}
		applyNotes.properties = params
		applyNotes.clearErrors()
		
		//特殊字段信息处理
		applyNotes.userCategory = AssetCategory.get(params.allowCategoryId)
		if(AssetCategory.parentName != null){
			applyNotes.rootAssetCategory = AssetCategory.parentName
		}else{
			applyNotes.rootAssetCategory = AssetCategory.categoryName
		}
		
		if(!params.registerNum_form.equals("")){
			applyNotes.registerNum = params.registerNum_form
		}
		
		if(applyNotes.save(flush:true)){
			json["result"] = "true"
		}else{
			applyNotes.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def assetApplySubmit ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def applyNotes = ApplyNotes.get(it)
				if(applyNotes){
					applyNotes.applyStatus = params.applyStatus
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetApplyAgree ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def applyNotes = ApplyNotes.get(it)
				if(applyNotes){
					applyNotes.applyStatus = params.applyStatus
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetCardsCreate ={//创建资产卡片
		def model=[:]
		def company = Company.get(params.companyId)
		def createCards = "false"
		def ids = params.applyIds.split(",")
		/**
		 * 创建资产卡片
		 */
		if(ids.size()>0){
			createCards = "true"
			ids.each {  
				def applyNotes = ApplyNotes.get(it)
				def assetType = applyNotes.rootAssetCategory
				if(assetType == "车辆"){
					/*
					 * 获取建账信息中的数量，创建相同数量的资产卡片
					 */
					def assetCount = applyNotes.amount
					for(int i=0;i<assetCount;i++){
						def carCard = new CarCards()
						carCard.company = company
						carCard.applyNotes = applyNotes
						carCard.registerNum = getFormattedSeriesDate()
						carCard.userCategory = applyNotes.userCategory
						carCard.assetName = applyNotes.assetName
						carCard.userDepart = applyNotes.userDepart
//						carCard.userStatus = applyNotes.userStatus
//						carCard.assetSource = applyNotes.assetSource
//						carCard.costCategory = applyNotes.costCategory
//						carCard.buyDate = applyNotes.buyDate
//						carCard.organizationalType = applyNotes.organizationalType
						carCard.onePrice = applyNotes.totalPrice/assetCount
//						if(applyNotes.undertakingRevenue == 0){
//							carCard.undertakingRevenue = 0
//						}else{
//							carCard.undertakingRevenue = applyNotes.undertakingRevenue/assetCount
//						}
//						if(applyNotes.fiscalAppropriation == 0){
//							carCard.fiscalAppropriation = 0
//						}else{
//							carCard.fiscalAppropriation = applyNotes.fiscalAppropriation/assetCount
//						}
//						if(applyNotes.otherFund == 0){
//							carCard.otherFund = 0
//						}else{
//							carCard.otherFund = applyNotes.otherFund/assetCount
//						}
//						carCard.storagePosition = applyNotes.storagePosition
						carCard.country = applyNotes.country
						carCard.assetStatus = "新建"
						carCard.save(flush: true)
					}
					
				}else if(assetType == "房屋"){
					
				}else if(assetType == "土地"){
					
				}else if(assetType == "设备"){
					
				}else if(assetType == "图书"){
					
				}else if(assetType == "家具"){
					
				}
				applyNotes.isCreatedCards = "1"
			}
		}
		model["createCards"] = createCards
		def applyNotes = new ApplyNotes()
		
		if(createCards == "true"){
			render(view:"/assetApply/createCards",model:model)
		}else{
			render(view:'/demo/applyShow',model:model)
		}
		//undo
	}
	
	def mineApplyGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			def _gridHeader =[]
			_gridHeader << ["name":"序号","width":"40px","colIdx":0,"field":"rowIndex"]
			_gridHeader << ["name":"申请编号","width":"120px","colIdx":1,"field":"registerNum"]
			_gridHeader << ["name":"申请人","width":"100px","colIdx":2,"field":"getFormattedUser"]
			_gridHeader << ["name":"申请部门","width":"100px","colIdx":3,"field":"getDepartName"]
			_gridHeader << ["name":"资产分类","width":"100px","colIdx":4,"field":"getCategoryName"]
			_gridHeader << ["name":"资产名称","width":"auto","colIdx":5,"field":"assetName"]
			_gridHeader << ["name":"数量","width":"80px","colIdx":6,"field":"amount"]
			_gridHeader << ["name":"金额","width":"80px","colIdx":7,"field":"totalPrice"]
			_gridHeader << ["name":"用途","width":"100px","colIdx":8,"field":"usedBy"]
			_gridHeader << ["name":"状态","width":"80px","colIdx":9,"field":"applyStatus"]
			json["gridHeader"] = _gridHeader
//			json["gridHeader"] = assetApplyService.getAssetApplyListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetApplyService.getAssetApplyDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetApplyService.getAssetApplyCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def assetApplyGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetApplyService.getAssetApplyListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetApplyService.getAssetApplyDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetApplyService.getAssetApplyCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def flowShowActionhello ={
		def actionList =[]
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("保存",imgPath + "add.png","demo")
		
		render actionList as JSON
	}
	def showFlowhello ={
		def model=[:]
		render(view:'/demo/showFlow',model:model)
	}
	def importDe ={
		def model=[:]
		render(view:'/demo/importDe',model:model)
	}
	def kpshow ={
		def model=[:]
		render(view:'/demo/kpshow',model:model)
	}
	def staticView ={
		def model=[:]
		render(view:'/demo/designMore',model:model)
	}
	def staticShow ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		render actionList as JSON
	}
	def addTitle ={
		def model=[:]
		render(view:'/demo/addTitle',model:model)
	}
	
	def endPd ={
		def model=[:]
		render(view:'/demo/endPd',model:model)
	}
	def zdpd ={
		def model=[:]
		render(view:'/demo/zdpd',model:model)
	}
	def importPd ={
		def model=[:]
		render(view:'/demo/importPd',model:model)
	}
	def startPdAction ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("盘点数据导入",webPath + imgPath + "add.png","pddr")
		actionList << createAction("扫描枪盘点",webPath + imgPath + "add.png","zdpd")
		actionList << createAction("盘点结束",webPath + imgPath + "add.png","zdpd_ok")
		render actionList as JSON
	}
	def startPd ={
		def model=[:]
		render(view:'/demo/startPd',model:model)
	}
	def myPdrwAction ={
		def actionList =[]
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("开始盘点",imgPath + "add.png","startPd")
		actionList << createAction("刷新",imgPath + "fresh.gif","demo")
		
		render actionList as JSON
	}
	def myPdrw ={
		def model=[:]
		render(view:'/demo/myPdrw',model:model)
	}
	def addRw ={
		def model=[:]
		render(view:'/demo/addRw',model:model)
	}
	def rwfbAction ={
		def actionList =[]
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新建任务",imgPath + "add.png","addRw")
		actionList << createAction("启动任务",imgPath + "add.png","demo")
		actionList << createAction("刷新",imgPath + "fresh.gif","demo")
		
		render actionList as JSON
	}
	def fbrw ={
		def model=[:]
		render(view:'/demo/rwfb',model:model)
	}
	
	def desgine ={
		def actionList =[]
		
		actionList << createAction("返回",imgPath + "quit_1.gif","demoReturn")
		actionList << createAction("新增表头字段",imgPath + "add.png","addDemoTitle")
		actionList << createAction("删除表头字段",imgPath + "add.png","deleteDemoTitle")
		actionList << createAction("预览",imgPath + "add.png","demoView")
		actionList << createAction("保存",imgPath + "add.png", "demoReturn")
		
		render actionList as JSON
	}
	def staticDesign ={
		def actionList =[]
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("报表设计",imgPath + "add.png", "demo_staticDesign")
		actionList << createAction("删除",imgPath + "delete.png","demo")
		actionList << createAction("刷新",imgPath + "fresh.gif","demo")
		
		render actionList as JSON
	}
	def demo ={
		def model=[:]
		model.type = params.type
		render(view:'/demo/' + params.type,model:model)
	}
}
