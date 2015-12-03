package com.flashbird.http.framework

import com.flashbird.http.util.JsonParser
import com.flashbird.http.util.exception.NotFindRouterException
import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Method, Response, Request}
import com.twitter.util.Future
import scala.collection.mutable.ArrayBuffer

/**
 * Created by yangguo on 15/9/21.
 */

case class MethodRouters(constanceRouters:Map[String,Router[_,_]],dynamicRouters:ArrayBuffer[Router[_,_]])
class HttpService(routers:ArrayBuffer[Router[_,_]]) extends Service[Request,Response]{
  import com.twitter.finagle.httpx.Method._
  val router=ArrayBuffer[Router[_,_]]()
  private val servers:Map[Method,MethodRouters]= {
    List(Get, Post, Put, Options, Head, Delete, Trace).map { method =>
      val methodR=routers.filter(_.method.equals( method))
      val constanceRouters={
        val _map=Map.newBuilder[String,Router[_,_]]
        methodR.filter(_.isConstantRoute).foreach{router=>
          _map.+=(router.path->router)
        }
        _map.result()
      }
      val dynamicRouters=methodR.filter(!_.isConstantRoute)
      (method -> MethodRouters(constanceRouters,dynamicRouters))
    }.toMap
  }
  override def apply(request: Request): Future[Response] = {
    Future.value {

        println(request.headerMap.mkString(","))
        val methodHandlers = servers.get(request.method)
        methodHandlers match {
          case Some(r) => {
            val incomingRequestRouter = getIncomingMessageRouter(request.path, r)
            if (incomingRequestRouter == null) throw new NotFindRouterException(s"No Find Router For ${request.path}")
            val responseContent = incomingRequestRouter(request)
            builderResponse(responseContent,incomingRequestRouter)
          }
          case None => throw new NotFindRouterException(s"NoFind Method [${request.method}]")
        }
    }
  }
  private def getIncomingMessageRouter(incomingPath:String,methodR:MethodRouters):Router[_,_]={
    val constanceR=methodR.constanceRouters.get(incomingPath)
    if(constanceR.isEmpty){
      val dynRs=methodR.dynamicRouters
      var router:Router[_,_]=null
      var isFind=false
      var idxDynamicRs=0
      while(!isFind&&idxDynamicRs<dynRs.size){
        val r=dynRs(idxDynamicRs)
        idxDynamicRs+=1
        if(r.pathPattern.regex.pattern.matcher(incomingPath).matches()){
          isFind=true
          router=r
        }
      }
      router
    }else constanceR.get
  }
  private def builderResponse(response:AnyRef,router:Router[_,_]):Response={
    if(response.isInstanceOf[Response]) response.asInstanceOf[Response]
    else {
      val content={
        if(response.isInstanceOf[ResponseContent]) response
        else DefaultResponseContent(0,response)
      }
      val res=Response()
      res.setContentString(JsonParser.objectToJsonStringParser(content))
//      res.setContent(JsonParser.objectToChannelBufferParser(content))
      res
    }
  }
}
