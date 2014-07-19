package com.rosten.app.bookKeeping

import grails.converters.JSON
import com.rosten.app.system.User

class CarActionController {

	def imgPath ="images/rosten/actionbar/"
	
  	def carRegisterView ={
		def actionList =[]
		def strname = "carRegister"
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
}
