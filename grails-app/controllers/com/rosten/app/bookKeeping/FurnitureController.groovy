package com.rosten.app.bookKeeping

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart

class FurnitureController {

    def bookKeepingService
	def springSecurityService

	def imgPath ="images/rosten/actionbar/"
	
	def furnitureRegisterForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "furnitureRegister"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	  def furnitureRegisterView ={
		def actionList =[]
		def strname = "furnitureRegister"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
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
	
	def furnitureRegisterAdd ={
		redirect(action:"furnitureRegisterShow",params:params)
	}
	
	def furnitureRegisterShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def furnitureRegister = new FurnitureRegister()
		if(params.id){
			furnitureRegister = FurnitureRegister.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["furnitureRegister"] = furnitureRegister
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/bookKeeping/furnitureRegister',model:model)
	}
	
	def furnitureRegisterSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//家具登记信息保存-------------------------------
		def furnitureRegister = new FurnitureRegister()
		if(params.id && !"".equals(params.id)){
			furnitureRegister = FurnitureRegister.get(params.id)
		}else{
			furnitureRegister.company = company
		}
		furnitureRegister.properties = params
		furnitureRegister.clearErrors()
		
		//特殊字段信息处理
		furnitureRegister.buyDate = Util.convertToTimestamp(params.buyDate)
		furnitureRegister.userDepart = Depart.get(params.allowdepartsId)
		if(!params.registerNum_form.equals("")){
			furnitureRegister.registerNum = params.registerNum_form
		}
		
		if(furnitureRegister.save(flush:true)){
			json["result"] = "true"
		}else{
			furnitureRegister.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def furnitureRegisterDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def furnitureRegister = FurnitureRegister.get(it)
				if(furnitureRegister){
					furnitureRegister.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def furnitureRegisterSubmit ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def furnitureRegister = FurnitureRegister.get(it)
				if(furnitureRegister){
					furnitureRegister.assetStatus = "已入库"
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def furnitureRegisterGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = bookKeepingService.getFurnitureRegisterListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = bookKeepingService.getFurnitureRegisterDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = bookKeepingService.getFurnitureRegisterCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
