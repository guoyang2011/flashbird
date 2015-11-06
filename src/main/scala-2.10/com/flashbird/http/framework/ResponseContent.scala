package com.flashbird.http.framework

/**
 * Created by yangguo on 15/9/24.
 */
trait ResponseContent

case class DefaultResponseContent(code:Int,jsonObj:AnyRef) extends ResponseContent

case class RangeResponseContent(code:Int,jsonObj:List[AnyRef],start:Long,end:Long) extends ResponseContent


