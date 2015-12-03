package com.flashbird.http.framework

import com.flashbird.http.framework.authorization.Authorization
import com.flashbird.http.util.FlashBirdConfig
import com.twitter.finagle.httpx.{Response, Request}
import com.twitter.finagle.httpx.Method._

/**
 * Created by yangguo on 15/9/21.
 */
abstract class Controller(pre:Option[String]=None){
  import RoutersPool._

  val prefix=pre match {
    case Some(s) if s.trim.length>0=>s.trim
    case _=>""
  }

  def defaultIsNeedAuth=FlashBirdConfig.getRouterIsAuth()
  private[this] def builderFilter(isAuth:Boolean,defaultFilter:HttpControllerFilter):ControllerFilter[Request,AnyRef]={
    if(isAuth){
      createAuthFilter().addThen(defaultFilter)//.asInstanceOf[HttpControllerFilter]
    }else defaultFilter
  }
  def createAuthFilter():HttpControllerFilter=Controller.authFilter
  def get[Req:Manifest,Rep:Manifest](path:String,isAuth:Boolean=defaultIsNeedAuth,filter:HttpControllerFilter=Controller.defaultEmptyFilter)(callback:Req=>Rep)= routers += Router(Get,prefix+path,callback,builderFilter(false,filter),isAuth)
  def post[Req:Manifest,Rep:Manifest](path:String,isAuth:Boolean=defaultIsNeedAuth,filter:HttpControllerFilter=Controller.defaultEmptyFilter)(callback:Req=>Rep)= routers += Router(Post,prefix+path,callback,builderFilter(false,filter),isAuth)
  def put[Req:Manifest,Rep:Manifest](path:String,isAuth:Boolean=defaultIsNeedAuth,filter:HttpControllerFilter=Controller.defaultEmptyFilter)(callback:Req=>Rep)= routers += Router(Put,prefix+path,callback,builderFilter(false,filter),isAuth)
  def delete[Req:Manifest,Rep:Manifest](path:String,isAuth:Boolean=defaultIsNeedAuth,filter:HttpControllerFilter=Controller.defaultEmptyFilter)(callback:Req=>Rep)= routers += Router(Delete,prefix+path,callback,builderFilter(false,filter),isAuth)
  def options[Req:Manifest,Rep:Manifest](path:String,isAuth:Boolean=defaultIsNeedAuth,filter:HttpControllerFilter=Controller.defaultEmptyFilter)(callback:Req=>Rep)= routers += Router(Options,prefix+path,callback,builderFilter(false,filter),isAuth)
  def patch[Req:Manifest,Rep:Manifest](path:String,isAuth:Boolean=defaultIsNeedAuth,filter:HttpControllerFilter=Controller.defaultEmptyFilter)(callback:Req=>Rep)= routers += Router(Patch,prefix+path,callback,builderFilter(false,filter),isAuth)
  def head[Req:Manifest,Rep:Manifest](path:String,isAuth:Boolean=defaultIsNeedAuth,filter:HttpControllerFilter=Controller.defaultEmptyFilter)(callback:Req=>Rep)= routers += Router(Head,prefix+path,callback,builderFilter(false,filter),isAuth)
  def trace[Req:Manifest,Rep:Manifest](path:String,isAuth:Boolean=defaultIsNeedAuth,filter:HttpControllerFilter=Controller.defaultEmptyFilter)(callback:Req=>Rep)= routers += Router(Trace,prefix+path,callback,builderFilter(false,filter),isAuth)
}
object Controller{
  val defaultEmptyFilter:HttpControllerFilter=EmptyHttpControllerFilter
  val authFilter:HttpControllerFilter=new HttpControllerFilter {

    override def apply(request: Request, service: RouterService[Request, AnyRef]): AnyRef = {
      val authProvider:Authorization=FlashBirdConfig.getAuthProvider()
      val header_auth_key=FlashBirdConfig.getRequestHeaderAuthorizationKey()
      val header_base_user_info_key=FlashBirdConfig.key_access_token_info
      val permissionNeed=DefaultResponseCode.permission_need
      val response:AnyRef= DefaultResponseContent(permissionNeed._1,permissionNeed._2)
      val accessToken=request.getParam(header_auth_key)
      println(header_auth_key+"->"+accessToken)
      if(accessToken!=null){
        authProvider.validToken(accessToken) match{
          case Some(info)=>{
            request.headerMap.set(header_base_user_info_key,info)
            println(s"header info: $header_base_user_info_key:"+request.headerMap.get(header_base_user_info_key))
            service(request)
          }
          case None=>response
        }
      }else response
    }
  }
}
