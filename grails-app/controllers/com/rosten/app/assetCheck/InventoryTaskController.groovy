package com.rosten.app.assetCheck

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.GridUtil
import com.rosten.app.util.Util
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Depart
import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.assetCards.*

class InventoryTaskController {
	def assetCheckService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	private def createAction={name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
	
	//2014-12-08-增加资产核查菜单---------------------------------------
	def assetHcView ={
		def actionList =[]
		def strname = "assetCheck"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	def assetHcSearchView ={
		def model =[:]
		
		def company = Company.get(params.companyId)
		model["DepartList"] = Depart.findAllByCompany(company)
		
		def _list =[]
		
		AssetCategory.findAllByCompany(company).each{
			if(it.parent){
				_list << it.categoryName
			}
		}
		
		model["categoryList"] = _list.unique()
		
		render(view:'/assetCheck/assetCheckSearch',model:model)
	}
	def assetHcGrid ={
		def company = Company.get(params.companyId)
		def json=[:]
		if(params.refreshHeader){
			def _gridHeader =[]

			_gridHeader << ["name":"序号","width":"30px","colIdx":0,"field":"rowIndex"]
			_gridHeader << ["name":"资产编号","width":"auto","colIdx":1,"field":"registerNum","formatter":"assetCard_formatTopic"]
			_gridHeader << ["name":"资产分类","width":"auto","colIdx":2,"field":"userCategory"]
			_gridHeader << ["name":"资产名称","width":"auto","colIdx":3,"field":"assetName"]
			_gridHeader << ["name":"归属部门","width":"auto","colIdx":4,"field":"userDepart"]
			_gridHeader << ["name":"使用人","width":"auto","colIdx":5,"field":"purchaser"]
			_gridHeader << ["name":"使用状况","width":"auto","colIdx":6,"field":"userStatus"]
			_gridHeader << ["name":"购置日期","width":"130px","colIdx":7,"field":"buyDate"]
			_gridHeader << ["name":"价格","width":"auto","colIdx":8,"field":"onePrice"]
			_gridHeader << ["name":"资产状态","width":"auto","colIdx":9,"field":"assetStatus"]

			json["gridHeader"] = _gridHeader
		}
		
		def searchArgs =[:]
		
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		if(params.category && !"".equals(params.category)) searchArgs["userCategory"] = params.category
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		if(params.userDepart && !"".equals(params.userDepart)) searchArgs["userDepart"] = Depart.findByCompanyAndDepartName(company,params.userDepart)
		
		def totalNum = 0
		if(params.refreshData){
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)

			def offset = (nowPage-1) * perPageNum
			def max  = perPageNum

			def _json = [identifier:'id',label:'name',items:[]]
			
			def allList =[]
			
			allList += HouseCards.createCriteria().list{
				eq("company",company)
					searchArgs.each{k,v->
						if(k.equals("userCategory")){
							createAlias('userCategory', 'a')
							like("a.categoryName","%" + v + "%")
						}else if(k.equals("userDepart")){
							eq(k,v)
						}else{
							like(k,"%" + v + "%")
						}
				}
				order("registerNum", "desc")
			}
			allList += CarCards.createCriteria().list{
				eq("company",company)
					searchArgs.each{k,v->
						if(k.equals("userCategory")){
							createAlias('userCategory', 'a')
							like("a.categoryName","%" + v + "%")
						}else if(k.equals("userDepart")){
							eq(k,v)
						}else{
							like(k,"%" + v + "%")
						}
				}
				order("registerNum", "desc")
			}
			allList += DeviceCards.createCriteria().list{
				eq("company",company)
					searchArgs.each{k,v->
						if(k.equals("userCategory")){
							createAlias('userCategory', 'a')
							like("a.categoryName","%" + v + "%")
						}else if(k.equals("userDepart")){
							eq(k,v)
						}else{
							like(k,"%" + v + "%")
						}
				}
				order("registerNum", "desc")
			}
			allList += FurnitureCards.createCriteria().list{
				eq("company",company)
					searchArgs.each{k,v->
						if(k.equals("userCategory")){
							createAlias('userCategory', 'a')
							like("a.categoryName","%" + v + "%")
						}else if(k.equals("userDepart")){
							eq(k,v)
						}else{
							like(k,"%" + v + "%")
						}
				}
				order("registerNum", "desc")
			}
			
			totalNum = allList.size()
			
			if(totalNum>0){
				def idx = 0
				if(offset!=null) idx=offset
				
				def lastIndex = offset+max -1
				if(lastIndex >totalNum-1){
					lastIndex = totalNum -1
				}
				
				
				allList[offset..lastIndex].each{
					
					def sMap =[:]
					sMap["rowIndex"] = idx+1
					sMap["id"] = it.id
					sMap["registerNum"] = it.registerNum
					sMap["userCategory"] = it.getCategoryName()
					sMap["assetName"] = it.assetName
					sMap["userDepart"] = it.getDepartName()
					sMap["purchaser"] = it.purchaser
					sMap["userStatus"] = it.userStatus
					sMap["buyDate"] = it.getFormattedBuyDate()
					sMap["onePrice"] = it.onePrice
					sMap["assetStatus"] = it.assetStatus
					sMap["userCategoryId"] = it.userCategory?.id
					
					_json.items+=sMap
					idx += 1
				}
			}			

			json["gridData"] = _json
		}
		
		if(params.refreshPageControl){
			json["pageControl"] = ["total":totalNum.toString()]
		}
		render json as JSON
	}
	
	def assetCardShow ={
		def category = AssetCategory.get(params.categoryId)
		def rootCategory = category.getRootCategory(category)
		switch(rootCategory.categoryCode){
			case "house":	//房屋及建筑物
				redirect(controller: "houseCards",action:"houseCardsShow",params:params)
				break
			case "car":	//运输工具
				redirect(controller: "carCards",action:"carCardsShow",params:params)
				break
			case "furniture":	//办公家具
				redirect(controller: "furnitureCards",action:"furnitureCardsShow",params:params)
				break
			case "device":	//电子设备
				redirect(controller: "deviceCards",action:"deviceCardsShow",params:params)
				break
		}
	}
	
	//------------------------------------------------------------
	
	def assetCheckForm = {
		def webPath = request.getContextPath() + "/"
		def strname = "assetCheck"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def inventoryTaskView ={
		def actionList =[]
		def strname = "assetCheck"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新建",imgPath + "add.png",strname + "_add")
		actionList << createAction("启动",imgPath + "config.png",strname + "_run")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def myTaskView ={
		def actionList =[]
		def strname = "assetCheck"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("开始盘点",imgPath + "flow.png",strname + "_start")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		render actionList as JSON
	}
	
	def startPdAction ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("数据导入盘点",webPath + imgPath + "word_open.png","pddr")
		actionList << createAction("扫描枪盘点",webPath + imgPath + "changeStatus.gif","zdpd")
		actionList << createAction("结束盘点",webPath + imgPath + "qx.png","zdpd_ok")
		render actionList as JSON
	}
	
	def assetCheckAdd ={
		redirect(action:"assetCheckShow",params:params)
	}
	
	def assetCheckShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def company = Company.get(params.companyId)
		
		def inventoryTask = new InventoryTask()
		if(params.id){
			inventoryTask = InventoryTask.get(params.id)
		}else{
			inventoryTask.sendMan = currentUser
		}
		
		model["user"] = currentUser
		model["company"] = company
		model["inventoryTask"] = inventoryTask
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		render(view:'/assetCheck/taskShow',model:model)
	}
	
	def assetCheckSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//盘点任务信息保存-------------------------------
		def inventoryTask = new InventoryTask()
		if(params.id && !"".equals(params.id)){
			inventoryTask = InventoryTask.get(params.id)
		}else{
			inventoryTask.company = company
			inventoryTask.sendMan = springSecurityService.getCurrentUser()
		}
		inventoryTask.properties = params
		inventoryTask.clearErrors()
		
		//特殊字段信息处理
		if(params.allowdepartsId.equals("")){
			inventoryTask.inventoryDepart = params.allowdepartsName
		}else{
			inventoryTask.inventoryDepart = Depart.get(params.allowdepartsId)
		}
		
		if(params.allowCategoryId.equals("")){
			inventoryTask.inventoryCategory = params.allowCategoryName
		}else{
			inventoryTask.inventoryCategory = AssetCategory.get(params.allowCategoryId)
		}
		
		if(!params.taskNum_form.equals("")){
			inventoryTask.taskNum = params.taskNum_form
		}
		
		if(inventoryTask.save(flush:true)){
			json["result"] = "true"
		}else{
			inventoryTask.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def assetCheckRun ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def inventoryTask = InventoryTask.get(it)
				if(inventoryTask){
					inventoryTask.runStatus = params.runStatus
					inventoryTask.taskStatus = params.taskStatus
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetCheckDelete = {
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def inventoryTask = InventoryTask.get(it)
				if(inventoryTask){
					inventoryTask.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetCheckStart = {
		def model=[:]
		render(view:'/demo/startPd',model:model)
	}
	
	def assetCheckComplete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def inventoryTask = InventoryTask.get(it)
				if(inventoryTask){
					inventoryTask.completeStatus = params.completeStatus
					inventoryTask.taskStatus = params.taskStatus
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def inventoryTaskGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCheckService.getInventoryTaskListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCheckService.getInventoryTaskDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetCheckService.getInventoryTaskCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def myTaskGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCheckService.getMyTaskListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCheckService.getMyTaskDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetCheckService.getMyTaskCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
}
