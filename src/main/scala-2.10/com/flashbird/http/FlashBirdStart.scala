package com.flashbird.http

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import com.flashbird.http.framework.{DefaultResponseContent, RoutersPool, HttpService}
import com.flashbird.http.util.{JsonParser, FlashBirdConfig}
import com.flashbird.http.util.exception.{NotFindRouterException, JsonEncoderException, JsonDecoderException}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.httpx.{Response, Request, Http}
import com.twitter.util.{JavaTimer, Future, Duration}

/**
 * Created by yangguo on 15/11/2.
 */
trait BuilderFlashBirdServer {
  type HttpSimpleFilter=SimpleFilter[Request,Response]
  def accessLogFilter=new HttpSimpleFilter {
    override def apply(request: Request, service: Service[Request, Response]): Future[Response] = service(request)
  }
  def exceptionFilter=new HttpSimpleFilter {
    import com.flashbird.http.framework.DefaultResponseCode._
    override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
      service(request) handle {
        case ex: JsonDecoderException => {
          val content=DefaultResponseContent(Invalid_authorization_parameters._1,Invalid_authorization_parameters._2+s"[${ex.getMessage}]")
          val response=Response()
          response.setContentString(JsonParser.objectToJsonStringParser(content))
          response
        }
        case ex:JsonEncoderException=> {
          val content = DefaultResponseContent(Invalid_authorization_parameters._1, Invalid_authorization_parameters._2 + s"[${ex.getMessage}]")
          val response = Response()
          response.setContentString(JsonParser.objectToJsonStringParser(content))
          response
        }
        case ex:NotFindRouterException=>{
          val content = DefaultResponseContent(no_such_method._1, no_such_method._2 + s"[${ex.getMessage}]")
          val response = Response()
          response.setContentString(JsonParser.objectToJsonStringParser(content))
          response
        }
        case ex:Exception=>{
          val content = DefaultResponseContent(service_inline_cause._1, service_inline_cause._2 + s"[${ex.getMessage}]")
          val response = Response()
          response.setContentString(JsonParser.objectToJsonStringParser(content))
          response
        }

      }
    }
  }

  def timeoutFilter=new HttpSimpleFilter{
    implicit val javaTime=new JavaTimer()
    override def apply(request: Request, service: Service[Request, Response]): Future[Response] = service(request).within(Duration(FlashBirdConfig.getServerRequestTimeOut(),TimeUnit.SECONDS))
  }
  def spiderActionFilter=new HttpSimpleFilter{
    override def apply(request: Request, service: Service[Request, Response]): Future[Response] = service(request)
  }

  def builderFilter=this.accessLogFilter andThen this.exceptionFilter andThen this.timeoutFilter
  def builderControllers():Unit
  def start(configPath:String):Unit={
    loadConfig(configPath)
    builderControllers()
    val httpService=builderFilter andThen new HttpService(RoutersPool.routers)
    ServerBuilder()
      .name(FlashBirdConfig.getServerName())
      .codec(Http())
      .requestTimeout(Duration(FlashBirdConfig.getServerRequestTimeOut(),TimeUnit.SECONDS))
      .readTimeout(Duration(FlashBirdConfig.getServerRequestReadTimeOut(),TimeUnit.SECONDS))
      .hostConnectionMaxIdleTime(Duration(FlashBirdConfig.getServerConnectMaxIdleTime(),TimeUnit.SECONDS))
      .hostConnectionMaxLifeTime(Duration(FlashBirdConfig.getServerConnectMaxLifeCycleTime(),TimeUnit.SECONDS))
      .keepAlive(FlashBirdConfig.getServerConnectIsKeepAlived())
      .recvBufferSize(FlashBirdConfig.getServerRequestMaxRecvBufferSize())
      .sendBufferSize(FlashBirdConfig.getServerResponseMaxBufferSize())
      .maxConcurrentRequests(FlashBirdConfig.getServerMaxConnectedClientNumber())
      .bindTo(new InetSocketAddress(FlashBirdConfig.getServerHost(),FlashBirdConfig.getServerPort()))
      .build(httpService)
  }
  def loadConfig(path:String):Unit
}
