package com.rosten.app.bookKeeping

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart

class HouseController {

    def bookKeepingService
	def springSecurityService

	def imgPath ="images/rosten/actionbar/"
	
	def houseRegisterForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "houseRegister"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	  def houseRegisterView ={
		def actionList =[]
		def strname = "houseRegister"
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
	
	def houseRegisterAdd ={
		redirect(action:"houseRegisterShow",params:params)
	}
	
	def houseRegisterShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def houseRegister = new HouseRegister()
		if(params.id){
			houseRegister = HouseRegister.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["houseRegister"] = houseRegister
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/bookKeeping/houseRegister',model:model)
	}
	
	def houseRegisterSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//房屋登记信息保存-------------------------------
		def houseRegister = new HouseRegister()
		if(params.id && !"".equals(params.id)){
			houseRegister = HouseRegister.get(params.id)
		}else{
			houseRegister.company = company
		}
		houseRegister.properties = params
		houseRegister.clearErrors()
		
		//特殊字段信息处理
		houseRegister.buyDate = Util.convertToTimestamp(params.buyDate)
		houseRegister.userDepart = Depart.get(params.allowdepartsId)
		if(!params.registerNum_form.equals("")){
			houseRegister.registerNum = params.registerNum_form
		}
		
		if(houseRegister.save(flush:true)){
			json["result"] = "true"
		}else{
			houseRegister.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def houseRegisterDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def houseRegister = HouseRegister.get(it)
				if(houseRegister){
					houseRegister.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def houseRegisterSubmit ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def houseRegister = HouseRegister.get(it)
				if(houseRegister){
					houseRegister.assetStatus = "已入库"
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def houseRegisterGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = bookKeepingService.getHouseRegisterListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = bookKeepingService.getHouseRegisterDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = bookKeepingService.getHouseRegisterCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
