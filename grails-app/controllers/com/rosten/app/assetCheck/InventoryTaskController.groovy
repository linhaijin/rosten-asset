package com.rosten.app.assetCheck

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.GridUtil
import com.rosten.app.util.Util
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Depart
import com.rosten.app.assetConfig.AssetCategory

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
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		actionList << createAction("新建",imgPath + "add.png",strname + "_add")
		actionList << createAction("启动",imgPath + "config.png",strname + "_run")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		
		render actionList as JSON
	}
	
	def myTaskView ={
		def actionList =[]
		def strname = "assetCheck"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		actionList << createAction("开始盘点",imgPath + "flow.png",strname + "_start")
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
