package com.flashbird.http.util

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

/**
 * Created by yangguo on 15/10/10.
 */
object RedisProvider {
  private lazy val redisPool = {
    val config = new JedisPoolConfig
    config.setMaxTotal(500)
    config.setMaxIdle(5)
    config.setMaxWaitMillis(1000 * 10)
    config.setTestOnBorrow(true)
    val (host,port)=createRedisServer(FlashBirdConfig.getRedisCacheServers()(0))
    new JedisPool(config, host, port)
  }
  private def createRedisServer(server:String):(String,Int)={
    println(server)
    val _s=server.split(":")
    if(_s.size>2) {
      val port=try{
        _s(1).toInt
      }catch{
        case ex:Throwable=> 6379
      }
      (_s(0),port)
    }else{
      ("localhost",6379)
    }
  }

  def redisCommand[T](f: Jedis => T): T = {
    val client = redisPool.getResource
    try {
      f(client)
    } finally {
      if(client!=null&&client.isConnected) redisPool.returnResource(client)
      else try{
        redisPool.returnResource(client)
      }catch{
        case ex:Throwable=>{
          ex.printStackTrace()
        }
      }
    }
  }

}
