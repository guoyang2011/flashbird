package com.flashbird.http.framework

import com.twitter.finagle.httpx.Request

/**
 * Created by yangguo on 15/9/24.
 */
trait RouterService[ReqIn,RepOut] extends (ReqIn=>RepOut){
  def apply(request:ReqIn):RepOut
}

trait FilterHandler[ReqIn,RepOut] extends ((ReqIn,RouterService[ReqIn,RepOut])=>RepOut){
  def apply(request: ReqIn,service:RouterService[ReqIn,RepOut]):RepOut
}

trait ControllerFilter[ReqIn,RepOut] extends FilterHandler[ReqIn,RepOut]{
  def addThen(filter:ControllerFilter[ReqIn,RepOut]):ControllerFilter[ReqIn,RepOut]={
    new ControllerFilter[ReqIn,RepOut] {
      def apply(request: ReqIn, service: RouterService[ReqIn, RepOut]): RepOut = {
        ControllerFilter.this(
          request,
          new RouterService[ReqIn,RepOut] {
            override def apply(v1: ReqIn): RepOut = filter(v1,service)
          })
      }
    }
  }
  def andThen(service: RouterService[ReqIn,RepOut])={
    new RouterService[ReqIn,RepOut]{
      override def apply(v1: ReqIn): RepOut = ControllerFilter.this(v1,service)
    }
  }
}
trait HttpControllerFilter extends ControllerFilter[Request,AnyRef]
object EmptyHttpControllerFilter extends HttpControllerFilter {
  override def apply(v1: Request, v2: RouterService[Request, AnyRef]): AnyRef = v2(v1)
}

