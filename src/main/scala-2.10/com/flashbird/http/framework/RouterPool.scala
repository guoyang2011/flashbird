package com.flashbird.http.framework

import scala.collection.mutable.ArrayBuffer


/**
 * Created by yangguo on 15/9/21.
 */


object RoutersPool {
  val routers=ArrayBuffer[Router[_,_]]()
}
