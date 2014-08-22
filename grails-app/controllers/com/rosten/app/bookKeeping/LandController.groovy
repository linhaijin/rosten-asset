package com.rosten.app.bookKeeping

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart

class LandController {

	def bookKeepingService
	def springSecurityService

	def imgPath ="images/rosten/actionbar/"
	
	def landRegisterForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "landRegister"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	  def landRegisterView ={
		def actionList =[]
		def strname = "landRegister"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除",imgPath + "read.gif",strname + "_delete")
		actionList << createAction("提交",imgPath + "hf.gif",strname + "_submit")
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
	
	def landRegisterAdd ={
		redirect(action:"landRegisterShow",params:params)
	}
	
	def landRegisterShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def landRegister = new LandRegister()
		if(params.id){
			landRegister = LandRegister.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["landRegister"] = landRegister
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/bookKeeping/landRegister',model:model)
	}
	
	def landRegisterSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//土地登记信息保存-------------------------------
		def landRegister = new LandRegister()
		if(params.id && !"".equals(params.id)){
			landRegister = LandRegister.get(params.id)
		}else{
			landRegister.company = company
		}
		landRegister.properties = params
		landRegister.clearErrors()
		
		//特殊字段信息处理
		landRegister.buyDate = Util.convertToTimestamp(params.buyDate)
		landRegister.userDepart = Depart.get(params.allowdepartsId)
		if(!params.registerNum_form.equals("")){
			landRegister.registerNum = params.registerNum_form
		}
		
		if(landRegister.save(flush:true)){
			json["result"] = "true"
		}else{
			landRegister.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def landRegisterDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def landRegister = LandRegister.get(it)
				if(landRegister){
					landRegister.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def landRegisterSubmit ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def landRegister = LandRegister.get(it)
				if(landRegister){
					landRegister.assetStatus = "已入库"
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def landRegisterGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = bookKeepingService.getLandRegisterListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = bookKeepingService.getLandRegisterDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = bookKeepingService.getLandRegisterCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
