package com.flashbird.http.framework

import com.flashbird.http.framework.authorization.Authorization
import com.flashbird.http.framework.request.{DefaultRequest, DynamicPathParamsRequest}
import cn.bird.http.util.PathPattern
import com.flashbird.http.util.FlashBirdConfig
import com.twitter.finagle.httpx.{Method, Request}

/**
 * Created by yangguo on 15/9/21.
 */
case class Router[IN:Manifest,OUT:Manifest](method:Method,path:String,callback:IN=>OUT,filter:ControllerFilter[Request,AnyRef],isAuth:Boolean) {
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
    var defaultAuthed=true
    if(isAuth && !authHandler(request)) defaultAuthed=false
    if(defaultAuthed){
      println(this.getClass.getName+",request->"+request.headerMap)
      service(request)
    }else{
      val permissionNeed=DefaultResponseCode.permission_need
      DefaultResponseContent(permissionNeed._1,permissionNeed._2)
    }
  }
  def authHandler(request: Request):Boolean={
    var result=false
    val authProvider:Authorization=FlashBirdConfig.getAuthProvider()
    val header_auth_key=FlashBirdConfig.getRequestHeaderAuthorizationKey()
    val header_base_user_info_key=FlashBirdConfig.key_access_token_info
    val accessToken=request.headerMap.get(header_auth_key)
    println(this.getClass.getName+","+header_auth_key+"->"+accessToken)
    accessToken match{
      case Some(token)=> {
        authProvider.validToken(token) match {
          case Some(info) => {
            request.headerMap.set(header_base_user_info_key, info)
            println(s"header info: $header_base_user_info_key:" + request.headerMap.get(header_base_user_info_key))
            result = true
          }
          case _ =>
        }
      }
      case None=>
    }
    result
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
