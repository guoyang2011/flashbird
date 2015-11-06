package com.flashbird.http.framework.request

import cn.bird.http.util.PathPattern
import com.twitter.finagle.httpx.Request
import org.jboss.netty.handler.codec.http.QueryStringDecoder
import scala.collection.JavaConverters._

/**
 * Created by yangguo on 15/9/21.
 */
case class DynamicPathParamsRequest[Body:Manifest](underlying:Request,pathPattern:PathPattern){
  var dynamicPathParams:Option[Map[String,String]]=None
  val content:Option[Body]=DefaultRequest.createContentFromBody[Body](underlying)
  val cookie:Map[String,String]=DefaultRequest.getCookie(underlying)
  val decoder=new QueryStringDecoder(underlying.uri)
  def getDynamicPathParam(key:String,defaultValue:String="default"):String={
    if(dynamicPathParams.isEmpty) {
      val pathParams=pathPattern.extract(decoder.getPath)
      dynamicPathParams={
        if(pathParams.isDefined) pathParams
        else Some(Map())
      }
    }
    dynamicPathParams.get.getOrElse(key,defaultValue)
  }
  def getUrlParam(key:String,defaultValue:String="default"):List[String]={
    val javaValues=decoder.getParameters.get(key)
    if(javaValues == null) List(defaultValue)
    else javaValues.asScala.toList
  }
}
