package com.rosten.app.assetconfig

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.Util
import com.rosten.app.system.Company
import com.rosten.app.system.User

class AssetConfigController {
	
	def assetConfigService
	
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
}
