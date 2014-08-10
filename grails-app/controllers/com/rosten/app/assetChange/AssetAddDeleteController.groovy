package com.rosten.app.assetChange

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.util.Util
import com.rosten.app.system.Depart

class AssetAddDeleteController {

    def assetChangeService
	def springSecurityService

	def imgPath ="images/rosten/actionbar/"
	
	def assetAddDeleteForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "assetAddDelete"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	  def assetAddDeleteView ={
		def actionList =[]
		def strname = "assetAddDelete"
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
	
	def assetAddDeleteAdd ={
		redirect(action:"assetAddDeleteShow",params:params)
	}
	
	def assetAddDeleteShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def assetAddDelete = new AssetAddDelete()
		if(params.id){
			assetAddDelete = AssetAddDelete.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["assetAddDelete"] = assetAddDelete
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetChange/assetAddDelete',model:model)
	}
	
	def assetAddDeleteSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//增值减值申请信息保存-------------------------------
		def assetAddDelete = new AssetAddDelete()
		if(params.id && !"".equals(params.id)){
			assetAddDelete = AssetAllocate.get(params.id)
		}else{
			assetAddDelete.company = company
		}
		assetAddDelete.properties = params
		assetAddDelete.clearErrors()
		
		//特殊字段信息处理
		assetAddDelete.applyDate = Util.convertToTimestamp(params.applyDate)
		if(params.callOutDeptId.equals("")){
			assetAddDelete.callOutDept = params.callOutDeptName
		}else{
			assetAddDelete.callOutDept = Depart.get(params.callOutDeptId)
		}
		if(params.callInDeptId.equals("")){
			assetAddDelete.callInDept = params.callInDeptName
		}else{
			assetAddDelete.callInDept = Depart.get(params.callInDeptId)
		}
		
		
		if(assetAddDelete.save(flush:true)){
			json["result"] = "true"
		}else{
			assetAddDelete.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def assetAddDeleteDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def assetAddDelete = AssetAddDelete.get(it)
				if(assetAddDelete){
					assetAddDelete.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetAddDeleteGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetChangeService.getAssetAddDeleteListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetChangeService.getAssetAddDeleteDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetChangeService.getAssetAddDeleteCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
}
