package com.flashbird.http.framework.request

import com.flashbird.http.util.JsonParser
import com.twitter.finagle.httpx.Request

/**
 * Created by yangguo on 15/9/21.
 */
object DefaultRequest {
  def getCookie(request:Request):Map[String,String]=Map()
  def createContentFromBody[Body:Manifest](request: Request):Option[Body]={
    val jsonString=request.getContentString()
    if(jsonString==null||jsonString.trim().length==0) None
    else Some(JsonParser.jsonStringToObjectParser[Body](request.getContentString()))
  }
}
