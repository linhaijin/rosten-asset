package com.rosten.app.share

import com.rosten.app.system.SystemCode

class ShareService {
	
	/*
	 * 2015-3-31：增加意见记录
	 * user:人员,status:状态，content:默认为“同意。”
	 */
	def addCommentAuto ={user,status,belongToId,belongToObject->
		def comment = FlowComment.findWhere(user:user,status:status,belongToId:belongToId)
		if(!comment) this.addComment(user,status,"同意。",belongToId,belongToObject)
	}
	/*
	 * 2015-3-31：增加意见记录
	 * user:人员,status:状态，content:内容,
	 */
	def addComment ={user,status,content,belongToId,belongToObject->
		def comment = new FlowComment()
		comment.user = user
		comment.status = status
		comment.content = content
		comment.belongToId = belongToId
		comment.belongToObject = belongToObject
		comment.company = user.company
		comment.save(flush:true)
	}
	
	def getCommentByStatus ={dealId,status->
		def _list =[]
		def commentList = FlowComment.findAllByBelongToIdAndStatus(dealId,status,[sort: "createDate", order: "desc"])
		if(commentList && commentList.size()>0){
			commentList.each{
				def map =[:]
				map["name"] = it.user.getChinaName()
				map["content"]= it.content
				map["date"]= it.getFormattedCreatedDate()
				_list << map
			}
			
		}
		return _list
	}
	def addFlowLog ={ belongToId,flowCode,currentUser,content ->
		//添加日志
		def _log = new FlowLog()
		_log.user = currentUser
		_log.belongToId = belongToId
		_log.belongToObject = flowCode
		_log.content = content
		_log.company = currentUser.company
		_log.save(flush:true)
	}
	def getSystemCodeItems={company,code ->
		def systemCode = SystemCode.findByCompanyAndCode(company,code)
		if(systemCode){
			return systemCode.items
		}else{
			return []
		}
	}
	
  def getAllDepartByChild ={departList,depart->
		departList << depart
		if(depart.children){
			depart.children.each{
				return getAllDepartByChild(departList,it)
			}
		}else{
			return departList
		}
	}
  
  
}
