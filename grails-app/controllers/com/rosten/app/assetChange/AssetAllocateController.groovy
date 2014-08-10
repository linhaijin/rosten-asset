package com.rosten.app.assetChange

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart

class AssetAllocateController {

    def assetChangeService
	def springSecurityService

	def imgPath ="images/rosten/actionbar/"
	
	def assetAllocateForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "assetAllocate"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	  def assetAllocateView ={
		def actionList =[]
		def strname = "assetAllocate"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除",imgPath + "read.gif",strname + "_delete")
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
	
	def assetAllocateAdd ={
		redirect(action:"assetAllocateShow",params:params)
	}
	
	def assetAllocateShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def assetAllocate = new AssetAllocate()
		if(params.id){
			assetAllocate = AssetAllocate.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["assetAllocate"] = assetAllocate
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetChange/assetAllocate',model:model)
	}
	
	def assetAllocateSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//报废报损申请信息保存-------------------------------
		def assetAllocate = new AssetAllocate()
		if(params.id && !"".equals(params.id)){
			assetAllocate = AssetAllocate.get(params.id)
		}else{
			assetAllocate.company = company
		}
		assetAllocate.properties = params
		assetAllocate.clearErrors()
		
		//特殊字段信息处理
		assetAllocate.applyDate = Util.convertToTimestamp(params.applyDate)
		if(params.callOutDeptId && !params.callOutDeptId.equals("")){
			assetAllocate.callOutDept = Depart.get(params.callOutDeptId)
		}else{
			assetAllocate.callOutDept = params.callOutDeptName
		}
		if(params.callInDeptId && !params.callInDeptId.equals("")){
			assetAllocate.callInDept = Depart.get(params.callInDeptId)
		}else{
			assetAllocate.callInDept = params.callInDeptName
		}
		
		
		if(assetAllocate.save(flush:true)){
			json["result"] = "true"
		}else{
			assetAllocate.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def assetAllocateDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def assetAllocate = AssetAllocate.get(it)
				if(assetAllocate){
					assetAllocate.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetAllocateGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetChangeService.getAssetAllocateListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetChangeService.getAssetAllocateDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetChangeService.getAssetAllocateCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
