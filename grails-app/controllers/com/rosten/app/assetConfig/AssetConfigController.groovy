package com.rosten.app.assetConfig

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.Util
import com.rosten.app.assetConfig.AssetCategory;
import com.rosten.app.system.Company
import com.rosten.app.system.User

class AssetConfigController {
	def springSecurityService
	def assetConfigService
	
	def imgPath ="images/rosten/actionbar/"
	
	def assetCategoryForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "assetCategory"
		def actionList = []
		
//		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def assetCategoryView ={
		def actionList =[]
		def strname = "assetCategory"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新增大类",imgPath + "add.png",strname + "_add")
		actionList << createAction("删除大类",imgPath + "delete.png",strname + "_delete")
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
	
	/*
    def assetCategoryDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def assetCategory = AssetCategory.get(it)
				if(assetCategory){
					assetCategory.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	def assetCategorySave ={
		def json=[:]
		def assetCategory = new AssetCategory()
		if(params.id && !"".equals(params.id)){
			assetCategory = AssetCategory.get(params.id)
		}else{
			if(params.companyId){
				assetCategory.company = Company.get(params.companyId)
			}
		}
		assetCategory.properties = params
		assetCategory.clearErrors()
		
		if(assetCategory.save(flush:true)){
			json["result"] = "true"
		}else{
			assetCategory.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	def assetCategoryAdd ={
		redirect(action:"assetCategoryShow",params:params)
	}
	def assetCategoryShow ={
		def model =[:]
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		def assetCategory = new AssetCategory()
		if(params.id){
			assetCategory = AssetCategory.get(params.id)
		}
		model["user"]=user
		model["company"] = company
		model["assetCategory"] = assetCategory
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
			//普通用户
//			fa.readOnly += ["description"]
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetConfig/assetCategory',model:model)
	}
	def assetCategoryGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetConfigService.getAssetCategoryListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetConfigService.getAssetCategoryListDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetConfigService.getAssetCategoryCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	*/
	def assetCategory ={
		def model =[:]
		model["company"] = Company.get(params.companyId)
		render(view:'/assetConfig/assetCategory',model:model)
	}
	
	def assetCategoryCreate ={
		def model =[:]
		model["parentId"] = params.parentId
		def parentAsset = AssetCategory.get(params.parentId)
		def parentCategoryCode = parentAsset?.categoryCode
		model["parentCategoryCode"] = parentCategoryCode
		model["companyId"] = params.companyId
		model["isRead"] = "no"
		model["assetCategory"] = new AssetCategory()
		render(view:'/assetConfig/assetCategoryEdit',model:model)
	}
	
	def assetCategoryShow ={
		def model =[:]
		def currentUser = (User) springSecurityService.getCurrentUser()
		def company = currentUser.company
		
		def categoryName
		if(params.categoryName && params.categoryName!=""){
			categoryName = params.categoryName
		}else{
			assetCategory = AssetCategory.get(params.id)
			categoryName = assetCategory.categoryName
		}
		def assetList = ['房屋及建筑物','电子设备','运输工具','办公家具']
		def isRead = "no"
		if(categoryName in assetList){
			isRead = "yes"
		}
//		println "isRead=="+isRead
		model["isRead"] = isRead
		model["assetList"] = assetList
		model["assetCategory"] = AssetCategory.get(params.id)
		model["companyId"] = company.id
		render(view:'/assetConfig/assetCategoryEdit',model:model)
	}
	
	def assetCategorySave ={
		def assetCategory
		
		def assetList = ['房屋及建筑物','电子设备','运输工具','办公家具']
		def isRead = "no"
		if(params.categoryName in assetList){
			isRead = "yes"
		}
		
		if(params.id){
			assetCategory = AssetCategory.get(params.id)
			assetCategory.properties = params
			assetCategory.clearErrors()
			def company = Company.get(params.companyId)
			
			//判断部门名称是否已经存在
			def _assetCategory = AssetCategory.findByCompanyAndCategoryName(company,params.categoryName)
			if(_assetCategory && !params.id.equals(_assetCategory.id)){
				flash.message = "<"+params.categoryName+">已经存在，请重新输入！"
				render(view:'/assetConfig/assetCategoryEdit',model:[assetCategory:assetCategory,parentId:params.parentId,companyId:params.companyId,"isRead":isRead])
				return
			}
			
			if(assetCategory.save(flush:true)){
				flash.refreshTree = true;
				flash.message = "'"+assetCategory.categoryName+"' 已成功保存！"
				render(view:'/assetConfig/assetCategoryEdit',model:[assetCategory:assetCategory,parentId:params.parentId,companyId:params.companyId,"isRead":isRead])
			}else{
				render(view:'/assetConfig/assetCategoryEdit',model:[assetCategory:assetCategory,parentId:params.parentId,companyId:params.companyId,"isRead":isRead])
			}
		}else{
			assetCategory = new AssetCategory()
			assetCategory.properties = params
			assetCategory.clearErrors()
			
			def company = Company.get(params.companyId)
			assetCategory.company = company
			
			//判断是否已经存在
			def _assetCategory = AssetCategory.findByCompanyAndCategoryName(company,params.categoryName)
			if(_assetCategory){
				//已经存在
				flash.message = "<"+params.categoryName+">已经存在，请重新输入！"
				render(view:'/assetConfig/assetCategoryEdit',model:[assetCategory:assetCategory,parentId:params.parentId,companyId:company.id,"isRead":isRead])
				return
			}
			
			if(params.parentId){
				def parent = AssetCategory.get(params.parentId)
				assetCategory.parentName = parent.categoryName
				assetCategory.allCode = parent.categoryCode+"_"+params.categoryCode
				parent.addToChildren(assetCategory)
				parent.save(flush:true)
			}else{
				assetCategory.save(flush:true)
			}
			flash.refreshTree = true;
			flash.message = "'"+assetCategory.categoryName+"' 已成功保存！"
			render(view:'/assetConfig/assetCategoryEdit',model:[assetCategory:assetCategory,parentId:params.parentId,companyId:company.id,"isRead":isRead])
		}
	}
	
	def assetCategoryDelete ={
		def ids = params.id.split(",")
		def currentUser = springSecurityService.getCurrentUser()
		def name,message
		try{
			ids.each{
				def assetCategory = AssetCategory.get(it)
				if(assetCategory){
					name = assetCategory.categoryName
					if(currentUser.getAllRolesValue().contains("系统管理员") || currentUser.getAllRolesValue().contains("资产管理员") || "admin".equals(currentUser.getUserType())){
						message = "资产分类<"+name+">及其下层分类已删除！"
//						assetCategory.delete(flush: true)
						assetConfigService.deleteAssetCategory(assetCategory)
					}else{
						message = "<span style=\"color:red\">注意：您没有权限进行操作，请联系管理员！</span>"
					}
				}
			}
		}catch(Exception e){
			message = "<span style=\"color:red\">注意：当前此分类已产生相关数据，不允许删除！</span>"
//			println e
		}
		render "<script type='text/javascript'>refreshAssetCategoryTree()</script><h3>&nbsp;&nbsp;"+message+"</h3>"
	}
	
	def assetCategoryTreeDataStore ={
		def company = Company.get(params.companyId)
		def dataList = AssetCategory.findAllByCompany(company,[sort: "serialNo", order: "asc"])
		def json = [identifier:'id',label:'name',items:[]]
		dataList.each{
			def sMap = ["id":it.id,"name":it.categoryName,"parentId":it.parent?.id,"children":[]]
			def childMap
			it.children.each{item->
				childMap = ["_reference":item.id]
				sMap.children += childMap
			}
			json.items+=sMap
		}
		render json as JSON
	}
	
	private def getFirstAssetCategory ={assetCategory->
		if(assetCategory.parent){
			return getFirstAssetCategory(assetCategory.parent)
		}else{
			return assetCategory
		}
	}
	
	private def getAllAssetCategory ={assetCategoryList,assetCategory->
		assetCategoryList << assetCategory
		if(assetCategory.parent){
			return getAllAssetCategory(assetCategoryList,assetCategory.parent)
		}else{
			return assetCategoryList
		}
	}
	
	private def getAllAssetCategoryByChild ={assetCategoryList,assetCategory->
		assetCategoryList << assetCategory
		if(assetCategory.children){
			assetCategory.children.each{
				return getAllAssetCategoryByChild(assetCategoryList,it)
			}
		}else{
			return assetCategoryList
		}
	}
}
