package com.rosten.app.bookKeeping

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart
import com.rosten.app.assetConfig.AssetCategory

class BookController {

    def bookKeepingService
	def springSecurityService

	def imgPath ="images/rosten/actionbar/"
	
	def bookRegisterForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "bookRegister"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	  def bookRegisterView ={
		def actionList =[]
		def strname = "bookRegister"
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
	
	def bookRegisterAdd ={
		redirect(action:"bookRegisterShow",params:params)
	}
	
	def bookRegisterShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def bookRegister = new BookRegister()
		if(params.id){
			bookRegister = BookRegister.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["bookRegister"] = bookRegister
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/bookKeeping/bookRegister',model:model)
	}
	
	def bookRegisterSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//图书登记信息保存-------------------------------
		def bookRegister = new BookRegister()
		if(params.id && !"".equals(params.id)){
			bookRegister = BookRegister.get(params.id)
		}else{
			bookRegister.company = company
		}
		bookRegister.properties = params
		bookRegister.clearErrors()
		
		//特殊字段信息处理
		bookRegister.userCategory = AssetCategory.get(params.allowCategoryId)
		bookRegister.buyDate = Util.convertToTimestamp(params.buyDate)
		bookRegister.userDepart = Depart.get(params.allowdepartsId)
		if(!params.registerNum_form.equals("")){
			bookRegister.registerNum = params.registerNum_form
		}
		
		if(bookRegister.save(flush:true)){
			json["result"] = "true"
		}else{
			bookRegister.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def bookRegisterDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def bookRegister = BookRegister.get(it)
				if(bookRegister){
					bookRegister.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def bookRegisterSubmit ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def bookRegister = BookRegister.get(it)
				if(bookRegister){
					bookRegister.assetStatus = "已入库"
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def bookRegisterGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = bookKeepingService.getBookRegisterListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = bookKeepingService.getBookRegisterDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = bookKeepingService.getBookRegisterCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
