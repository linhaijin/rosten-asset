package com.rosten.app.bookKeeping

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart

class DeviceController {

    def bookKeepingService
	def springSecurityService

	def imgPath ="images/rosten/actionbar/"
	
	def deviceRegisterForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "deviceRegister"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	  def deviceRegisterView ={
		def actionList =[]
		def strname = "deviceRegister"
		
		def user = springSecurityService.getCurrentUser()
		
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		
		if("资产管理员".equals(user.getUserTypeName())){
			actionList << createAction("通过",imgPath + "hf.gif",strname + "_agree")
			actionList << createAction("生成条形码",imgPath + "hf.gif",strname + "_kp")
			actionList << createAction("批量打印条形码",imgPath + "hf.gif",strname + "_print")
		}else{
			actionList << createAction("提交",imgPath + "hf.gif",strname + "_submit")
		}
		
		actionList << createAction("批量导入",imgPath + "add.png",strname + "_import")
		actionList << createAction("导出",imgPath + "add.png",strname + "_export")
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
	
	def deviceRegisterAdd ={
		redirect(action:"deviceRegisterShow",params:params)
	}
	
	def deviceRegisterShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def deviceRegister = new DeviceRegister()
		if(params.id){
			deviceRegister = DeviceRegister.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["deviceRegister"] = deviceRegister
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/bookKeeping/deviceRegister',model:model)
	}
	
	def deviceRegisterSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//设备登记信息保存-------------------------------
		def deviceRegister = new DeviceRegister()
		if(params.id && !"".equals(params.id)){
			deviceRegister = DeviceRegister.get(params.id)
		}else{
			deviceRegister.company = company
		}
		deviceRegister.properties = params
		deviceRegister.clearErrors()
		
		//特殊字段信息处理
		deviceRegister.buyDate = Util.convertToTimestamp(params.buyDate)
		deviceRegister.userDepart = Depart.get(params.allowdepartsId)
		if(!params.registerNum_form.equals("")){
			deviceRegister.registerNum = params.registerNum_form
		}
		
		if(deviceRegister.save(flush:true)){
			json["result"] = "true"
		}else{
			deviceRegister.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def deviceRegisterDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def deviceRegister = DeviceRegister.get(it)
				if(deviceRegister){
					deviceRegister.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def deviceRegisterSubmit ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def deviceRegister = DeviceRegister.get(it)
				if(deviceRegister){
					deviceRegister.assetStatus = "审核"
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def deviceRegisterAgree ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def deviceRegister = DeviceRegister.get(it)
				if(deviceRegister){
					deviceRegister.assetStatus = "已入库"
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def deviceRegisterGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = bookKeepingService.getDeviceRegisterListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = bookKeepingService.getDeviceRegisterDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = bookKeepingService.getDeviceRegisterCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
