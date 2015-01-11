package com.rosten.app.assetCheck

import grails.converters.JSON
import java.text.SimpleDateFormat
import jxl.Sheet;
import jxl.Workbook;

import com.rosten.app.util.FieldAcl
import com.rosten.app.util.GridUtil
import com.rosten.app.util.SystemUtil
import com.rosten.app.util.Util
import com.rosten.app.system.Company
import com.rosten.app.system.User
import com.rosten.app.system.Depart
import com.rosten.app.system.UserGroup
import com.rosten.app.system.Group1
import com.rosten.app.assetConfig.AssetCategory
import com.rosten.app.assetCards.*
import com.rosten.app.export.ExcelExport
import com.rosten.app.share.ShareService



class InventoryTaskController {
	def assetCheckService
	def springSecurityService
	def shareService
	
	def imgPath ="images/rosten/actionbar/"
	
	private def createAction={name,img,action->
		def model =[:]
		model["name"] = name
		model["img"] = img
		model["action"] = action
		return model
	}
	
	//2014-12-10----增加myTask页面
	def myTaskShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def company = Company.get(params.companyId)
		
		def myTask = new MyTask()
		if(params.id){
			myTask = MyTask.get(params.id)
		}
		
		model["user"] = currentUser
		model["company"] = company
		model["myTask"] = myTask
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		render(view:'/assetCheck/myTaskShow',model:model)
	}
	def myTaskForm ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		
		def myTask = MyTask.get(params.id)
		if("未完成".equals(myTask.status)){
			actionList << createAction("数据导入盘点",webPath + imgPath + "word_open.png","pddr")
			actionList << createAction("扫描枪盘点",webPath + imgPath + "changeStatus.gif","zdpd")
			actionList << createAction("结束盘点",webPath + imgPath + "qx.png","zdpd_ok")
		}
		
		render actionList as JSON
	}
	def taskItemGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		def myTask = MyTask.get(params.id)
		
		if(params.refreshHeader){
			json["gridHeader"] = assetCheckService.getTaskCardsListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			args["myTask"] = myTask
			json["gridData"] = assetCheckService.getTaskCardsDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetCheckService.getTaskCardsCount(company,myTask)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	def zdpd ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		render(view:'/assetCheck/zdpd',model:model)
	}
	
	def zdpd_search ={
		//资产查询
		def json =[:]
		
		def entity = TaskCards.findByCardsRegisterNum(params.id)
		
		if(entity){
			//通过种类获取对应的卡片详细信息
			
			def _entity
			switch (entity.cardCategory){
				case "car":
					_entity = CarCards.findByRegisterNum(params.id)
					break
				case "furniture":
					_entity = FurnitureCards.findByRegisterNum(params.id)
					break
				case "device":
					_entity = DeviceCards.findByRegisterNum(params.id)
					break
				case "house":
					_entity = HouseCards.findByRegisterNum(params.id)
					break
			}
			
			json["registerNum"] = _entity?.registerNum
			json["assetName"] = _entity?.assetName
			json["specifications"] = _entity?.specifications
			json["buyDate"] = _entity?.getFormattedShowBuyDate()
			json["onePrice"] = _entity?.onePrice
			json["storagePosition"] = _entity?.storagePosition
			json["purchaser"] = _entity?.purchaser
			json["depart"] = _entity?.getDepartName()
			
			json["result"] = "true"
		}else{
			json["result"] = "error"
		}
		render json as JSON
	}
	def zdpd_ok ={
		//结束盘点
		def json =[:]
		
		def myTask = MyTask.get(params.id)
		if(myTask){
			myTask.status = "已完成"
			if(myTask.save()){
				json["result"] = "true"
			}else{
				json["result"] = "error"
			}
			
		}else{
			json["result"] = "error"
		}
		render json as JSON
		
	}
	def zdpd_pdrk ={
		//盘点入库
		def json =[:]
		
		def _entity = TaskCards.findByCardsRegisterNum(params.id)
		
		if(_entity){
			_entity.nowNumber = 1
			_entity.result = "在盘"
			
			if(_entity.save()){
				json["result"] = "true"
			}else{
				json["result"] = "error"
			}
		}else{
			json["result"] = "error"
		}
		render json as JSON
	}
	def zddr ={
		def model =[:]
		render(view:'/assetCheck/zddr',model:model)
	}
	def zddrSubmit ={
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
				
				for(int i=1;i<sourceRowCount;i++){
					String zcbh =sourceSheet.getCell(1, i).getContents();	//资产编号
					def _entity = TaskCards.findByCardsRegisterNum(zcbh)
					
					_entity.nowNumber = 1
					_entity.result = "在盘"
					
					_entity.save()
				}
				
				ostr ="<script>var _parent = window.parent;_parent.rosten.alert('导入成功').queryDlgClose=function(){_parent.rosten.variable.dialog.hide();_parent.rosten.variable.dialog.destroy();_parent.taskItemGrid.refresh();}</script>"
			}
		}catch(Exception e){
			println e
			ostr = "<script>window.parent.rosten.alert('导入失败');</script>"
		}
		render ostr
	}
	//----------------------------------------------------
	
	//2014-12-08-增加资产核查菜单---------------------------------------
	def assetHcView ={
		def actionList =[]
		def strname = "assetCheck"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("导出",imgPath + "export.png",strname + "_export")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	def assetHcSearchView ={
		def model =[:]
		
		def company = Company.get(params.companyId)
//		model["DepartList"] = Depart.findAllByCompany(company)
		
		def _list =[]
		
		AssetCategory.findAllByCompany(company).each{
			if(it.parent){
				_list << it.categoryName
			}
		}
		model["company"] = company
		model["categoryList"] = _list.unique()
		
		render(view:'/assetCheck/assetCheckSearch',model:model)
	}
	def assetHcGrid ={
		def company = Company.get(params.companyId)
		def json=[:]
		if(params.refreshHeader){
			def _gridHeader =[]

			_gridHeader << ["name":"序号","width":"30px","colIdx":0,"field":"rowIndex"]
			_gridHeader << ["name":"资产编号","width":"auto","colIdx":1,"field":"registerNum","formatter":"assetCard_formatTopic"]
			_gridHeader << ["name":"资产分类","width":"auto","colIdx":2,"field":"userCategory"]
			_gridHeader << ["name":"资产名称","width":"auto","colIdx":3,"field":"assetName"]
			_gridHeader << ["name":"归属部门","width":"auto","colIdx":4,"field":"userDepart"]
			_gridHeader << ["name":"使用人","width":"auto","colIdx":5,"field":"purchaser"]
			_gridHeader << ["name":"使用状况","width":"auto","colIdx":6,"field":"userStatus"]
			_gridHeader << ["name":"购置日期","width":"130px","colIdx":7,"field":"buyDate"]
			_gridHeader << ["name":"价格","width":"auto","colIdx":8,"field":"onePrice"]
			_gridHeader << ["name":"资产状态","width":"auto","colIdx":9,"field":"assetStatus"]

			json["gridHeader"] = _gridHeader
		}
		
		def searchArgs =[:]
		
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		if(params.category && !"".equals(params.category)) searchArgs["userCategory"] = params.category
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		
		def userDepartList = []
		if(params.userDepart && !"".equals(params.userDepart)){
			params.userDepart.split(",").each{
				def _list = []
				userDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
//				userDepartList << Depart.get(it)
			}
			searchArgs["userDepart"] = userDepartList.unique()
		}
		
		def totalNum = 0	//总条目数
		def totalMoney = 0.00	//总金额
		if(params.refreshData){
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)

			def offset = (nowPage-1) * perPageNum
			def max  = perPageNum

			def _json = [identifier:'id',label:'name',items:[]]
			
			def allList =[]
			
			allList += HouseCards.createCriteria().list{
				eq("company",company)
					searchArgs.each{k,v->
						if(k.equals("userCategory")){
							createAlias('userCategory', 'a')
							like("a.categoryName","%" + v + "%")
						}else if(k.equals("userDepart")){
							'in'(k,v)
						}else{
							like(k,"%" + v + "%")
						}
				}
				order("registerNum", "desc")
			}
			allList += CarCards.createCriteria().list{
				eq("company",company)
					searchArgs.each{k,v->
						if(k.equals("userCategory")){
							createAlias('userCategory', 'a')
							like("a.categoryName","%" + v + "%")
						}else if(k.equals("userDepart")){
							'in'(k,v)
						}else{
							like(k,"%" + v + "%")
						}
				}
				order("registerNum", "desc")
			}
			allList += DeviceCards.createCriteria().list{
				eq("company",company)
					searchArgs.each{k,v->
						if(k.equals("userCategory")){
							createAlias('userCategory', 'a')
							like("a.categoryName","%" + v + "%")
						}else if(k.equals("userDepart")){
							'in'(k,v)
						}else{
							like(k,"%" + v + "%")
						}
				}
				order("registerNum", "desc")
			}
			allList += FurnitureCards.createCriteria().list{
				eq("company",company)
					searchArgs.each{k,v->
						if(k.equals("userCategory")){
							createAlias('userCategory', 'a')
							like("a.categoryName","%" + v + "%")
						}else if(k.equals("userDepart")){
							'in'(k,v)
						}else{
							like(k,"%" + v + "%")
						}
				}
				order("registerNum", "desc")
			}
			
			totalNum = allList.size()
			totalMoney = allList.collect { _item ->
				_item.onePrice
			}.sum()
			
			if(totalNum>0){
				def idx = 0
				if(offset!=null) idx=offset
				
				def lastIndex = offset+max -1
				if(lastIndex >totalNum-1){
					lastIndex = totalNum -1
				}
				
				
				allList[offset..lastIndex].each{
					
					def sMap =[:]
					sMap["rowIndex"] = idx+1
					sMap["id"] = it.id
					sMap["registerNum"] = it.registerNum
					sMap["userCategory"] = it.getCategoryName()
					sMap["assetName"] = it.assetName
					sMap["userDepart"] = it.getDepartName()
					sMap["purchaser"] = it.purchaser
					sMap["userStatus"] = it.userStatus
					sMap["buyDate"] = it.getFormattedBuyDate()
					sMap["onePrice"] = it.onePrice
					sMap["assetStatus"] = it.assetStatus
					sMap["userCategoryId"] = it.userCategory?.id
					
					_json.items+=sMap
					idx += 1
				}
			}

			json["gridData"] = _json
		}
		
		if(params.refreshPageControl){
			json["pageControl"] = ["total":totalNum.toString(),"endHtml":"总金额共<span style=\"color:red;margin-left:2px;margin-right:2px\">" + Util.DoubleToFormat(totalMoney?totalMoney/10000:0.00,2) + "</span>万元"]
		}
		render json as JSON
	}
	
	def assetCardShow ={
		def category = AssetCategory.get(params.categoryId)
		def rootCategory = category.getRootCategory(category)
		switch(rootCategory.categoryCode){
			case "house":	//房屋及建筑物
				redirect(controller: "houseCards",action:"houseCardsShow",params:params)
				break
			case "car":	//运输工具
				redirect(controller: "carCards",action:"carCardsShow",params:params)
				break
			case "furniture":	//办公家具
				redirect(controller: "furnitureCards",action:"furnitureCardsShow",params:params)
				break
			case "device":	//电子设备
				redirect(controller: "deviceCards",action:"deviceCardsShow",params:params)
				break
		}
	}
	
	//------------------------------------------------------------
	
	def assetCheckForm = {
		def webPath = request.getContextPath() + "/"
		def strname = "assetCheck"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("保存",webPath + imgPath + "Save.gif",strname + "_save")
		
		render actionList as JSON
	}
	
	def inventoryTaskView ={
		def actionList =[]
		def strname = "assetCheck"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("新建",imgPath + "add.png",strname + "_add")
		actionList << createAction("启动",imgPath + "config.png",strname + "_run")
		actionList << createAction("删除",imgPath + "delete.png",strname + "_delete")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		
		render actionList as JSON
	}
	
	def myTaskView ={
		def actionList =[]
		def strname = "assetCheck"
		actionList << createAction("退出",imgPath + "quit_1.gif","returnToMain")
		actionList << createAction("开始盘点",imgPath + "flow.png",strname + "_start")
		actionList << createAction("刷新",imgPath + "fresh.gif","freshGrid")
		render actionList as JSON
	}
	
	def startPdAction ={
		def webPath = request.getContextPath() + "/"
		def actionList = []
		
		actionList << createAction("返回",webPath + imgPath + "quit_1.gif","page_quit")
		actionList << createAction("数据导入盘点",webPath + imgPath + "word_open.png","pddr")
		actionList << createAction("扫描枪盘点",webPath + imgPath + "changeStatus.gif","zdpd")
		actionList << createAction("结束盘点",webPath + imgPath + "qx.png","zdpd_ok")
		render actionList as JSON
	}
	
	def assetCheckAdd ={
		redirect(action:"assetCheckShow",params:params)
	}
	
	def assetCheckShow ={
		def model =[:]
		def currentUser = springSecurityService.getCurrentUser()
		
		def company = Company.get(params.companyId)
		
		def inventoryTask = new InventoryTask()
		if(params.id){
			inventoryTask = InventoryTask.get(params.id)
		}
		
		model["user"] = currentUser
		model["company"] = company
		model["inventoryTask"] = inventoryTask
		
		FieldAcl fa = new FieldAcl()
		model["fieldAcl"] = fa
		
		render(view:'/assetCheck/taskShow',model:model)
	}
	
	def assetCheckSave ={
		def json=[:]
		def company = Company.get(params.companyId)
		
		//盘点任务信息保存-------------------------------
		def inventoryTask = new InventoryTask()
		if(params.id && !"".equals(params.id)){
			inventoryTask = InventoryTask.get(params.id)
		}else{
			inventoryTask.company = company
			inventoryTask.sendMan = springSecurityService.getCurrentUser()
		}
		inventoryTask.properties = params
		inventoryTask.clearErrors()
		
		//特殊字段信息处理
		if(params.isAllDepart.equals("true")){
			inventoryTask.isAllDepart = true
		}else{
			inventoryTask.isAllDepart = false
			
			params.allowdepartsId.split(",").each{
				def depart = Depart.get(it)
				inventoryTask.addToInventoryDeparts(depart)
			}
			
			params.allowusersId.split(",").each{
				def user = User.get(it)
				inventoryTask.addToReceiveUsers(user)
			}
		}
		
		if(params.isAllCategory.equals("true")){
			inventoryTask.isAllCategory = true
			
		}else{
			inventoryTask.isAllCategory = false
			params.allowCategoryId.split(",").each{
				def category = AssetCategory.get(it)
				inventoryTask.addToInventoryCategorys(category)
			}
		}
		
		inventoryTask.makeDate = Util.convertToTimestamp(params.makeDate)
		inventoryTask.startDate = Util.convertToTimestamp(params.startDate)
		inventoryTask.endDate = Util.convertToTimestamp(params.endDate)
		
		if(inventoryTask.save(flush:true)){
			json["result"] = "true"
		}else{
			inventoryTask.errors.each{
				println it
			}
			json["result"] = "false"
		}
		render json as JSON
	}
	
	def assetCheckRun ={
		def ids = params.id.split(",")
		def json
		def currentUser = springSecurityService.getCurrentUser()
		def company = currentUser.company
		try{
			ids.each{_id ->
				def inventoryTask = InventoryTask.get(_id)
				if(inventoryTask){
					//创建相关的myTask任务--------------------------------------------
					def dealUsers =[]
					def zcglyUsers = UserGroup.findAllByGroup(Group1.findByGroupName("zcgly")).collect { elem ->
						elem.user
					 }
					dealUsers += zcglyUsers
					
					//协会本部资产管理员----------------------------------
					def xhzcglyUser =  UserGroup.findAllByGroup(Group1.findByGroupName("xhzcgly")).collect { elem ->
						elem.user
					 }
					
					def _myTask = new MyTask()
					_myTask.user = xhzcglyUser[0]
					_myTask.company = company
					_myTask.inventoryTask = inventoryTask
					_myTask.save()
					inventoryTask.addToMyTasks(_myTask)
					//-----------------------------------------------
					
					def mytaskList = []
					dealUsers.unique().each{
						if(!it.equals(xhzcglyUser[0])){
							def myTask = new MyTask()
							myTask.user = it
							myTask.company = company
							myTask.inventoryTask = inventoryTask
							myTask.save()
							inventoryTask.addToMyTasks(myTask)
							mytaskList << myTask
						}
					}
					
					//获取所有的资产信息-----------------------------------------
					def dealAssetCards =[]
					if(inventoryTask.isAllDepart){
						//所有部门的资产均需要处理
						if(inventoryTask.isAllCategory){
							dealAssetCards +=  CarCards.list()
							dealAssetCards +=  DeviceCards.list()
							dealAssetCards +=  HouseCards.list()
							dealAssetCards +=  FurnitureCards.list()
						}else{
							inventoryTask.invCates.each{category->
								dealAssetCards +=  CarCards.findAllByUserCategory(category)
								dealAssetCards +=  DeviceCards.findAllByUserCategory(category)
								dealAssetCards +=  HouseCards.findAllByUserCategory(category)
								dealAssetCards +=  FurnitureCards.findAllByUserCategory(category)
							}
						}
					}else{
						inventoryTask.invDepts.each{
							if(inventoryTask.isAllCategory){
								dealAssetCards += CarCards.findAllByUserDepart(it)
								dealAssetCards += DeviceCards.findAllByUserDepart(it)
								dealAssetCards += HouseCards.findAllByUserDepart(it)
								dealAssetCards += FurnitureCards.findAllByUserDepart(it)
							}else{
								inventoryTask.invCates.each{category->
									dealAssetCards += CarCards.findAllByUserDepartAndUserCategory(it,category)
									dealAssetCards += DeviceCards.findAllByUserDepartAndUserCategory(it,category)
									dealAssetCards += HouseCards.findAllByUserDepartAndUserCategory(it,category)
									dealAssetCards += FurnitureCards.findAllByUserDepartAndUserCategory(it,category)
								}
							}
						}
					}
					
					//创建对应的TaskCards
					dealAssetCards.each{
						def taskCards = new TaskCards()
						taskCards.myTask = this.getMyTaskByCardsDepart(mytaskList,_myTask,it.userDepart)
						taskCards.cardsRegisterNum = it.registerNum
						taskCards.cardsName = it.assetName
						taskCards.company = company
						
						def rootCategory = taskCards.userCategory.getRootCategory(taskCards.userCategory)
						taskCards.cardCategory = rootCategory.categoryCode
						
						taskCards.save()
						
					}
					
					inventoryTask.runStatus = params.runStatus
					inventoryTask.taskStatus = params.taskStatus
					
					inventoryTask.save()
				}
			}
			json = [result:'true']
		}catch(Exception e){
			println e
			json = [result:'error']
		}
		render json as JSON
	}
	private def getMyTaskByCardsDepart={mytaskList,defaultTask,departEntity ->
		def _task = defaultTask
		mytaskList.each{
			if(departEntity?.isSubDepart){
				if(departEntity in it.user.getAllDepartEntity()){
					_task = it
				}
			}
		}
		return _task
	}
	def assetCheckDelete = {
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def inventoryTask = InventoryTask.get(it)
				if(inventoryTask){
					inventoryTask.delete(flush: true)
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def assetCheckStart = {
		def model=[:]
		render(view:'/demo/startPd',model:model)
	}
	
	def assetCheckComplete ={
		def ids = params.id.split(",")
		def json
		try{
			ids.each{
				def inventoryTask = InventoryTask.get(it)
				if(inventoryTask){
					inventoryTask.completeStatus = params.completeStatus
					inventoryTask.taskStatus = params.taskStatus
				}
			}
			json = [result:'true']
		}catch(Exception e){
			json = [result:'error']
		}
		render json as JSON
	}
	
	def inventoryTaskGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCheckService.getInventoryTaskListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCheckService.getInventoryTaskDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetCheckService.getInventoryTaskCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def myTaskGrid ={
		def json=[:]
		def company = Company.get(params.companyId)
		if(params.refreshHeader){
			json["gridHeader"] = assetCheckService.getMyTaskListLayout()
		}
		if(params.refreshData){
			def args =[:]
			int perPageNum = Util.str2int(params.perPageNum)
			int nowPage =  Util.str2int(params.showPageNum)
			
			args["offset"] = (nowPage-1) * perPageNum
			args["max"] = perPageNum
			args["company"] = company
			json["gridData"] = assetCheckService.getMyTaskDataStore(args)
			
		}
		if(params.refreshPageControl){
			def total = assetCheckService.getMyTaskCount(company)
			json["pageControl"] = ["total":total.toString()]
		}
		render json as JSON
	}
	
	def assetCheckExport = {
		OutputStream os = response.outputStream
		response.setContentType('application/vnd.ms-excel')
		response.setHeader("Content-disposition", "attachment; filename=" + new String("资产卡片清单.xls".getBytes("GB2312"), "ISO_8859_1"))
		def company = Company.get(params.companyId)
		
		//增加查询条件
		def searchArgs =[:]
		if(params.registerNum && !"".equals(params.registerNum)) searchArgs["registerNum"] = params.registerNum
		if(params.category && !"".equals(params.category)) searchArgs["userCategory"] = params.category
		if(params.assetName && !"".equals(params.assetName)) searchArgs["assetName"] = params.assetName
		def userDepartList = []
		if(params.userDepart && !"".equals(params.userDepart)){
			params.userDepart.split(",").each{
				def _depart = Depart.get(it)
				
				if(_depart){
					def _list = []
					userDepartList += shareService.getAllDepartByChild(_list,Depart.get(it))
				}
				
			}
			searchArgs["userDepart"] = userDepartList.unique()
		}
		
		def allList =[]
		
		allList += HouseCards.createCriteria().list{
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory")){
					createAlias('userCategory', 'a')
					like("a.categoryName","%" + v + "%")
				}else if(k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("registerNum", "desc")
		}
		allList += CarCards.createCriteria().list{
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory")){
					createAlias('userCategory', 'a')
					like("a.categoryName","%" + v + "%")
				}else if(k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("registerNum", "desc")
		}
		allList += DeviceCards.createCriteria().list{
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory")){
					createAlias('userCategory', 'a')
					like("a.categoryName","%" + v + "%")
				}else if(k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("registerNum", "desc")
		}
		allList += FurnitureCards.createCriteria().list{
			eq("company",company)
			searchArgs.each{k,v->
				if(k.equals("userCategory")){
					createAlias('userCategory', 'a')
					like("a.categoryName","%" + v + "%")
				}else if(k.equals("userDepart")){
					'in'(k,v)
				}else{
					like(k,"%" + v + "%")
				}
			}
			order("registerNum", "desc")
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
		def assetCardsList = []
		allList.each {
			List sMap =[]
			sMap << it.registerNum
			sMap << it.userCategory.categoryName
			sMap << it.assetName
			sMap << it.userDepart?.departName
			sMap << String.format("%.2f", it.onePrice)
			sMap << sdf.format(it.buyDate)
			def storagePosition
			if(it.storagePosition != "" && it.storagePosition != null){
				storagePosition = it.storagePosition
			}else{
				storagePosition = ""
			}
			sMap << storagePosition
			def specifications
			if(it.specifications != "" && it.specifications != null){
				specifications = it.specifications
			}else{
				specifications = ""
			}
			sMap << specifications
			def purchaser
			if(it.purchaser != "" && it.purchaser != null){
				purchaser = it.purchaser
			}else{
				purchaser = ""
			}
			sMap << specifications
			assetCardsList << sMap
		}
		
		def excel = new ExcelExport()
		excel.assetCardsDc(os,assetCardsList)
	}
}
