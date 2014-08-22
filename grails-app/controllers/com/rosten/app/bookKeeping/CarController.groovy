package com.rosten.app.bookKeeping

import grails.converters.JSON
import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart

class CarController {

	def bookKeepingService
	def springSecurityService
	
	def imgPath ="images/rosten/actionbar/"
	
	def carRegisterForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "carRegister"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def carRegisterView ={
		def actionList =[]
		def strname = "carRegister"
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
	
	def carRegisterAdd ={
		redirect(action:"carRegisterShow",params:params)
	}
	
	def carRegisterShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def carRegister = new CarRegister()
		if(params.id){
			carRegister = CarRegister.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["carRegister"] = carRegister
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/bookKeeping/carRegister',model:model)
	}
	
	def carRegisterSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//车辆登记信息保存-------------------------------
		def carRegister = new CarRegister()
		if(params.id && !"".equals(params.id)){
			carRegister = CarRegister.get(params.id)
		}else{
			carRegister.company = company
		}
		carRegister.properties = params
		carRegister.clearErrors()
		
		//特殊字段信息处理
		carRegister.buyDate = Util.convertToTimestamp(params.buyDate)
		carRegister.userDepart = Depart.get(params.allowdepartsId)
		if(!params.registerNum_form.equals("")){
			carRegister.registerNum = params.registerNum_form
		}
		
		if(carRegister.save(flush:true)){
			json["result"] = "true"
		}else{
			carRegister.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def carRegisterDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def carRegister = CarRegister.get(it)
				if(carRegister){
					carRegister.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def carRegisterSubmit ={
		def ids = params.id.split(",")
		def json
		def assetStatus
		try{
			ids.each{
				def carRegister = CarRegister.get(it)
				if(carRegister){
					assetStatus = carRegister.assetStatus
					if(assetStatus=="新建"){
						carRegister.assetStatus = "已入库"
					}
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def carRegisterGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = bookKeepingService.getCarRegisterListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = bookKeepingService.getCarRegisterDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = bookKeepingService.getCarRegisterCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
