package com.flashbird.http.framework

import com.flashbird.http.framework.request.{DefaultRequest, DynamicPathParamsRequest}
import cn.bird.http.util.PathPattern
import com.twitter.finagle.httpx.{Method, Request}

/**
 * Created by yangguo on 15/9/21.
 */
case class Router[IN:Manifest,OUT:Manifest](method:Method,path:String,callback:IN=>OUT,filter:HttpControllerFilter) {
  val pathPattern=PathPattern(path)
  val captureNames=pathPattern.captureNames
  val isConstantRoute=captureNames.isEmpty

  private val service=filter andThen  new RouterService[Request,AnyRef]{
    override def apply(request: Request): AnyRef = {
      val callbackRequest = {
        if (isEqualRequestRuntimeType[Request]) {
          request
        } else if (isEqualRequestRuntimeType[DynamicPathParamsRequest[_]]) {
          val bodyParamType = requestRuntimeClass.typeArguments.head
          DynamicPathParamsRequest(request,pathPattern)(bodyParamType)
        } else {
          DefaultRequest.createContentFromBody(request)(requestRuntimeClass) match{
            case Some(content)=> content
            case None=> throw new Exception(DefaultResponseCode.invalid_request_parameters._2)
          }
        }
      }
      callback(createRequest(callbackRequest)).asInstanceOf[AnyRef]
    }
  }
  def apply(request: Request):AnyRef={
    service(request)
  }
  def getPathParams(incomingPath:String):Option[Map[String,String]]={
    if(!isConstantRoute) pathPattern.extract(incomingPath)
    else None
  }
  val requestRuntimeClass=manifest[IN]
  val responseRuntimeClass=manifest[OUT]

  def isEqualRequestRuntimeType[Req:Manifest]=manifest[Req].runtimeClass==requestRuntimeClass.runtimeClass

  def createRequest[T:Manifest](t:T)=t.asInstanceOf[IN]
}
