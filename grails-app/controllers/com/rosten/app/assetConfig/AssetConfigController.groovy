package com.rosten.app.assetConfig

import grails.converters.JSON

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.Util
import com.rosten.app.util.SystemUtil
import com.rosten.app.assetConfig.AssetCategory;
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Depart
import com.rosten.app.assetCards.*

import jxl.Sheet;
import jxl.Workbook;

class AssetConfigController {
	def springSecurityService
	def assetConfigService
	
	def imgPath ="images/rosten/actionbar/"
	
	//2014-12-06 修复资产 卡片信息-----仅供管理员使用
	private def getRostenCategoryByName={rootCategory,categoryName->
		def _category
		rootCategory.children.each{
			if(it.categoryName.equals(categoryName)){
				_category =  it
			}
		}
		return _category
	}
	def assetCategoryRepair ={
		def json
		try{
			def currentUser = (User) springSecurityService.getCurrentUser()
			def company = currentUser.company
			
			//办公家具
			def bgjj = AssetCategory.findByCategoryCode("furniture")
			def jj_bgsb = this.getRostenCategoryByName(bgjj,"办公设备")
			def jj_gdzc = this.getRostenCategoryByName(bgjj,"固定资产")
			
			FurnitureCards.findAllByCompany(company).each{
				def _name = it.getCategoryName()
				if(!_name.equals("办公设备") && !_name.equals("固定资产")){
					//通过资产编号判断是否为办公设备或者固定资产
					def _n = it.registerNum.substring(0, 1)
					if(_n.equals("1")){
						//固定资产
						if(jj_gdzc){
							it.userCategory = jj_gdzc
							it.save()
						}
						
					}else if(_n.equals("2")){
						//办公设备
						if(jj_bgsb){
							it.userCategory = jj_bgsb
							it.save()
						}
					}
					
				}
			}
			
			//电子设备
			def dzsb = AssetCategory.findByCategoryCode("device")
			def dzsb_bgsb = this.getRostenCategoryByName(dzsb,"办公设备")
			def dzsb_gdzc = this.getRostenCategoryByName(dzsb,"固定资产")
			
			DeviceCards.findAllByCompany(company).each{
				def _name = it.getCategoryName()
				if(!_name.equals("办公设备") && !_name.equals("固定资产")){
					//通过资产编号判断是否为办公设备或者固定资产
					def _n = it.registerNum.substring(0, 1)
					if(_n.equals("1")){
						//固定资产
						if(dzsb_gdzc){
							it.userCategory = dzsb_gdzc
							it.save()
						}
						
					}else if(_n.equals("2")){
						//办公设备
						if(dzsb_bgsb){
							it.userCategory = dzsb_bgsb
							it.save()
						}
					}
					
				}
			}
			
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	//---------------------------------------------------------
	
	//2014-11-30批量导入固定资产
	def importAsset ={
		def model =[:]
		model["company"] = Company.get(params.id)
		render(view:'/assetConfig/importAsset',model:model)
	}
	def importAssetSubmit ={
		def ostr
		try{
			SystemUtil sysUtil = new SystemUtil()
			def currentUser = (User) springSecurityService.getCurrentUser()
			def company = currentUser.company
			
			def f = request.getFile("uploadedfile")
			if (!f.empty) {
				
				def uploadPath
				def companyPath = company?.shortName
				if(companyPath == null){
					uploadPath = sysUtil.getUploadPath("template")+"/"
				}else{
					uploadPath = sysUtil.getUploadPath(company?.shortName + "/template") + "/"
				}
				
				//添加附件信息
				String name = f.getOriginalFilename()//获得文件原始的名称
				def realName = sysUtil.getRandName(name)
				def filePath = new File(uploadPath,realName)
				f.transferTo(filePath)
				
				//解析上传的excel文件
				Sheet sourceSheet = Workbook.getWorkbook(filePath).getSheet(0);
				int sourceRowCount = sourceSheet.getRows();//获得源excel的行数
				
				//从第三行开始计算
				for(int i=2;i<sourceRowCount;i++){
					String bm =sourceSheet.getCell(0, i).getContents();	//所属部门
					String zcdl =sourceSheet.getCell(1, i).getContents();	//资产大类名称
					String lb =sourceSheet.getCell(2, i).getContents();	//资产类别
					String dm =sourceSheet.getCell(4, i).getContents();	//资产代码
					String mc =sourceSheet.getCell(5, i).getContents();	//资产名称
					String jz =sourceSheet.getCell(6, i).getContents();	//资产价值
					String sj =sourceSheet.getCell(7, i).getContents();	//开始使用时间
					String dd =sourceSheet.getCell(8, i).getContents();	//存放地点
					String xh =sourceSheet.getCell(9, i).getContents();	//规格型号
					String zrr =sourceSheet.getCell(10, i).getContents();	//责任人
					String bz =sourceSheet.getCell(11, i).getContents();	//备注
					
					//通过类别获取大类信息，如未找到，则默认为其他类别
					def rootCategory = AssetCategory.findByCategoryName(zcdl)
					def _category = AssetCategory.findByCategoryNameAndParent(lb,rootCategory)

					if(rootCategory && _category){
						//创建资产卡片信息
						
						def cardEntity
						switch (rootCategory.categoryCode){
							case "house":	//房屋及建筑物
								cardEntity = new HouseCards()
								cardEntity.company = company
								cardEntity.registerNum = dm
								cardEntity.userDepart = Depart.findByDepartName(bm)
								cardEntity.userCategory = _category
								cardEntity.assetName = mc
								cardEntity.onePrice = Util.obj2Double(jz)
								cardEntity.storagePosition = dd
								cardEntity.specifications = xh
								cardEntity.purchaser = zrr
								cardEntity.createDate = Util.convertToTimestamp(sj)
								cardEntity.remark = bz
								
								cardEntity.save()
								break
							case "car":		//运输工具
								cardEntity = new CarCards()
								cardEntity.company = company
								cardEntity.registerNum = dm
								cardEntity.userDepart = Depart.findByDepartName(bm)
								cardEntity.userCategory = _category
								cardEntity.assetName = mc
								cardEntity.onePrice = Util.obj2Double(jz)
								cardEntity.storagePosition = dd
								cardEntity.specifications = xh
								cardEntity.purchaser = zrr
								cardEntity.buyDate = Util.convertToTimestamp(sj)
								cardEntity.remark = bz
								
								cardEntity.save()
								break
							case "furniture":	//办公家具
								cardEntity = new FurnitureCards()
								cardEntity.company = company
								cardEntity.registerNum = dm
								cardEntity.userDepart = Depart.findByDepartName(bm)
								cardEntity.userCategory = _category
								cardEntity.assetName = mc
								cardEntity.onePrice = Util.obj2Double(jz)
								cardEntity.storagePosition = dd
								cardEntity.specifications = xh
								cardEntity.purchaser = zrr
								cardEntity.buyDate = Util.convertToTimestamp(sj)
								cardEntity.remark = bz
								
								cardEntity.save()
								break
							case "device":		//电子设备
								cardEntity = new DeviceCards()
								cardEntity.company = company
								cardEntity.registerNum = dm
								cardEntity.userDepart = Depart.findByDepartName(bm)
								cardEntity.userCategory = _category
								cardEntity.assetName = mc
								cardEntity.onePrice = Util.obj2Double(jz)
								cardEntity.storagePosition = dd
								cardEntity.specifications = xh
								cardEntity.purchaser = zrr
								cardEntity.buyDate = Util.convertToTimestamp(sj)
								cardEntity.remark = bz
								
								cardEntity.save()
								break
								
							case "other":		//其他
							
								break
						}
					}					
				}
				
				ostr ="<script>var _parent = window.parent;_parent.rosten.alert('导入成功').queryDlgClose=function(){_parent.rosten.kernel.hideRostenShowDialog();}</script>"
			}
		}catch(Exception e){
			println e
			ostr = "<script>window.parent.rosten.alert('导入失败');</script>"
		}
		render ostr
	}
	
	//204-11-23------资产编号配置-----------------------------------------------------
	def assetCodeConfig ={
		def model = [:]
		def user = springSecurityService.getCurrentUser()
		
		def entity = AssetCode.findWhere(company:user.company)
		if(entity==null) {
			entity = new AssetCode()
			
			Calendar cal = Calendar.getInstance();
			entity.nowYear = cal.get(Calendar.YEAR)
			entity.frontYear = entity.nowYear -1
			
			model.companyId = user.company.id
		}else{
			model.companyId = entity.company.id
		}
		model.config = entity
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		render(view:'/assetConfig/assetCodeConfig',model:model)
	}
	def assetCodeConfigView ={
		def actionList =[]
		
		def strname = "assetCodeConfig"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("保存",imgPath + "Save.gif",strname + "_save")
		actionList << createAction("批量导入资产",imgPath + "back.png","asset_import")
		
		//2014-12-06 修复资产看片分类信息
		def currentUser = springSecurityService.getCurrentUser()
		if("admin".equals(currentUser.getUserType())){
			actionList << createAction("修复资产卡片分类",imgPath + "init.gif","asset_repair")
		
		}
		render actionList as JSON
	}
	def assetCodeConfigSave ={
		def json=[:]
		def entity = new AssetCode()
		if(params.id && !"".equals(params.id)){
			entity = AssetCode.get(params.id)
		}
		entity.properties = params
		entity.clearErrors()
		entity.company = Company.get(params.companyId)
		
		entity.nowCancel = params.config_nowCancel
		entity.frontCancel = params.config_frontCancel
		
		if(entity.save(flush:true)){
			json["result"] = true
			json["configId"] = entity.id
			json["companyId"] = entity.company.id
		}else{
			entity.errors.each{
				println it
			}
			json["result"] = false
		}
		render json as JSON
	}
	//---------------------------------------------------------------------------
	
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
		
		//获取跟节点资产分类
		
		
		if(params.id){
			assetCategory = AssetCategory.get(params.id)
			assetCategory.properties = params
			assetCategory.clearErrors()
			def company = Company.get(params.companyId)
			
			//2014-12-05去除重名判断功能
			//判断部门名称是否已经存在
//			def _assetCategory = AssetCategory.findByCompanyAndCategoryName(company,params.categoryName)
//			if(_assetCategory && !params.id.equals(_assetCategory.id)){
//				flash.message = "<"+params.categoryName+">已经存在，请重新输入！"
//				render(view:'/assetConfig/assetCategoryEdit',model:[assetCategory:assetCategory,parentId:params.parentId,companyId:params.companyId,"isRead":isRead])
//				return
//			}
			
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
			
			//2014-12-05去除重名判断功能
			//判断是否已经存在
			def _assetCategory = AssetCategory.findByCompanyAndCategoryName(company,params.categoryName)
//			if(_assetCategory){
//				//已经存在
//				flash.message = "<"+params.categoryName+">已经存在，请重新输入！"
//				render(view:'/assetConfig/assetCategoryEdit',model:[assetCategory:assetCategory,parentId:params.parentId,companyId:company.id,"isRead":isRead])
//				return
//			}
			
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
			it.getSortChildren().each{item->
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
