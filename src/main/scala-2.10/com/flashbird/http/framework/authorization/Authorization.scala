package com.flashbird.http.framework.authorization

import java.util.UUID

import com.flashbird.http.util.{FlashBirdConfig, RedisProvider}
import sun.misc.BASE64Encoder

/**
 * Created by yangguo on 15/10/10.
 */
trait Authorization{
  def validToken(tokenString: String):Option[String]

  def creatorToken(info:String,expired:Int=FlashBirdConfig.getTokenExpiredTime()):String
}
class DefaultRedisAuthorizationProvider extends Authorization{
  private def generatorTokenStr={
    val uuid=UUID.randomUUID().toString
    new BASE64Encoder().encode(uuid.getBytes())
  }
  override def validToken(tokenString: String): Option[String] = {
    RedisProvider.redisCommand{implicit client=>
     val info= client.get(tokenString)
      if(info==null) None
      else Some(info)
    }
  }
  override def creatorToken(info: String,expired:Int=FlashBirdConfig.getTokenExpiredTime()): String = {
    val token=generatorTokenStr
    RedisProvider.redisCommand{implicit client=>
      val status=client.set(token,info)
      if(status.equals("OK")){
        client.expire(token,expired)
      }
    }
    token
  }
}
