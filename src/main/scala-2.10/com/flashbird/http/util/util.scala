package com.flashbird.http.util

import java.util.concurrent.Executors

import com.twitter.util.FuturePool
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}
import sun.misc.BASE64Encoder

/**
 * Created by yangguo on 15/9/21.
 */
object util {
  def runtimeClassEq[type1: Manifest, type2: Manifest]: Boolean = manifest[type1].runtimeClass == manifest[type2].runtimeClass
  lazy val coreNumber = Runtime.getRuntime.availableProcessors()
  lazy val futurePool = FuturePool(Executors.newFixedThreadPool((coreNumber * 1.2).toInt))

}

