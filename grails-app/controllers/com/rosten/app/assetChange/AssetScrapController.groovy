package com.rosten.app.assetChange

import grails.converters.JSON

import com.rosten.app.bookKeeping.CarRegister
import com.rosten.app.util.FieldAcl
import com.rosten.app.util.GridUtil
import com.rosten.app.util.Util
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Depart

class AssetScrapController {

	def bookKeepingService
    def assetChangeService
	def springSecurityService

	def imgPath ="images/rosten/actionbar/"
	
	def assetScrapForm ={
		def webPath = request.getContextPath() + "/"
		def strname = "assetScrap"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	  def assetScrapView ={
		def actionList =[]
		def strname = "assetScrap"
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
	
	def assetScrapAdd ={
		redirect(action:"assetScrapShow",params:params)
	}
	
	def assetScrapShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def user = User.get(params.userid)
		def company = Company.get(params.companyId)
		
		def assetScrap = new AssetScrap()
		if(params.id){
			assetScrap = AssetScrap.get(params.id)
		}
		
		model["user"] = user
		model["company"] = company
		model["assetScrap"] = assetScrap
		
		FieldAcl fa = new FieldAcl()
		if("normal".equals(user.getUserType())){
		}
		model["fieldAcl"] = fa
		
		render(view:'/assetChange/assetScrap',model:model)
//		render(view:'/assetChange/test',model:model)
	}
	
	def assetScrapSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//报废报损申请信息保存-------------------------------
		def assetScrap = new AssetScrap()
		if(params.id && !"".equals(params.id)){
			assetScrap = AssetScrap.get(params.id)
		}else{
			assetScrap.company = company
		}
		assetScrap.properties = params
		assetScrap.clearErrors()
		
		//特殊字段信息处理
		assetScrap.applyDate = Util.convertToTimestamp(params.applyDate)
		if(params.allowdepartsId.equals("")){
			assetScrap.applyDept = params.allowdepartsName
		}else{
			assetScrap.applyDept = Depart.get(params.allowdepartsId)
		}
		
		
		if(assetScrap.save(flush:true)){
			json["result"] = "true"
		}else{
			assetScrap.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def assetScrapDelete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def assetScrap = AssetScrap.get(it)
				if(assetScrap){
					assetScrap.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetScrapGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetChangeService.getAssetScrapListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetChangeService.getAssetScrapDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetChangeService.getAssetScrapCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def getAssetListLayout ={
		def gridUtil = new GridUtil()
		def _gridHeader =[]
		_gridHeader << ["name":"序号","width":"40px","colIdx":0,"field":"rowIndex"]
		_gridHeader << ["name":"资产编号","width":"100px","colIdx":1,"field":"registerNum","formatter":"carRegister_formatTopic"]
		_gridHeader << ["name":"资产分类","width":"100px","colIdx":2,"field":"assetCategory"]
		_gridHeader << ["name":"资产名称","width":"auto","colIdx":3,"field":"assetName"]
		_gridHeader << ["name":"使用状况","width":"80px","colIdx":4,"field":"userStatus"]
		_gridHeader << ["name":"金额","width":"80px","colIdx":5,"field":"totalPrice"]
		_gridHeader << ["name":"使用部门","width":"100px","colIdx":6,"field":"userDepart"]
		_gridHeader << ["name":"购买日期","width":"80px","colIdx":7,"field":"buyDate"]
		return gridUtil.buildLayoutJSON(_gridHeader)
	}
	
	def assetScrapListDataStore ={
		def json=[:]
		def companyEntity = Company.get(params.companyId)

		def _gridHeader =[]
		_gridHeader << ["name":"序号","width":"40px","colIdx":0,"field":"rowIndex"]
		_gridHeader << ["name":"资产编号","width":"100px","colIdx":1,"field":"registerNum","formatter":"carRegister_formatTopic"]
		_gridHeader << ["name":"资产分类","width":"100px","colIdx":2,"field":"assetCategory"]
		_gridHeader << ["name":"资产名称","width":"auto","colIdx":3,"field":"assetName"]
		_gridHeader << ["name":"使用状况","width":"80px","colIdx":4,"field":"userStatus"]
		_gridHeader << ["name":"金额","width":"80px","colIdx":5,"field":"totalPrice"]
		_gridHeader << ["name":"使用部门","width":"100px","colIdx":6,"field":"userDepart"]
		_gridHeader << ["name":"购买日期","width":"80px","colIdx":7,"field":"buyDate"]
		json["gridHeader"] = _gridHeader
		
		int totalNum = 0
		
		if(params.refreshData){
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)

			def offset = (nowPage-1) * perPageNum
			def max  = perPageNum

			def _json = [identifier:'id',label:'name',items:[]]
			
//			def c = CarRegister.createCriteria()
//			def pa=[max:max,offset:offset]
//			def query = {
//				eq("company",company)
//			}
//			def assetList = c.list(pa,query)
			
			def assetList = CarRegister.findAllByCompany(companyEntity,[max: max, sort: "createDate", order: "desc", offset: offset])
			totalNum = assetList.size()
			println totalNum
			
			def idx = 0
			assetList.each{
//				println it.id
//				println it.registerNum
//				println it.assetCategory
//				println it.assetName
//				println it.userStatus
//				println it.totalPrice
//				println it.getDepartName()
//				println it.getFormattedShowBuyDate()
				def sMap =[:]
				sMap["rowIndex"] = idx+1
				sMap["id"] = it.id
				sMap["registerNum"] = it.registerNum
				sMap["assetCategory"] = it.assetCategory
				sMap["assetName"] = it.assetName
				sMap["userStatus"] = it.userStatus
				sMap["totalPrice"] = it.totalPrice
				sMap["userDepart"] = it.getDepartName()
				sMap["buyDate"] = it.getFormattedShowBuyDate()
				
				_json.items+=sMap
				
				idx += 1
			}

			json["gridData"] = _json
		}
		
		
		if(params.refreshPageControl){
			json["pageControl"] = ["total":totalNum.toString()]
		}
		println json
		
		render json as JSON
	}
	
}
