package com.flashbird.http.framework.authorization

import java.util.UUID

import com.flashbird.http.util.{FlashBirdConfig, RedisProvider}
import sun.misc.BASE64Encoder
import scala.collection.JavaConverters._
/**
 * Created by yangguo on 15/10/10.
 */
trait Authorization{
  def validToken(tokenString: String):Option[String]

  def creatorToken(info:String,expired:Int=FlashBirdConfig.getTokenExpiredTime()):String
}
class DefaultRedisAuthorizationProvider extends Authorization{
  val maxLoginUserSize=FlashBirdConfig.getMaxLoginUserCount()
  private def generatorTokenStr={
    val uuid=UUID.randomUUID().toString
    new BASE64Encoder().encode(uuid.getBytes())
  }
  override def validToken(tokenString: String): Option[String] = {
    RedisProvider.redisCommand{implicit client=>
     val info= client.get(tokenString)
      println(tokenString+"->"+info)
      if(info==null || info.equalsIgnoreCase("null")) None
      else Some(info)
    }
  }
  override def creatorToken(info: String,expired:Int=FlashBirdConfig.getTokenExpiredTime()): String = {
    val uSessionKeyPrefix="user_session_"
    val uSessionKey=uSessionKeyPrefix+info

    val token=generatorTokenStr
    RedisProvider.redisCommand{implicit client=>
      val status=client.set(token,info)
      if(status.equals("OK")){
        client.expire(token,expired)
      }
      client.lpush(uSessionKey,token)
      val currentUSessionSize=client.llen(uSessionKey)
      if(currentUSessionSize>maxLoginUserSize) {
        val tokens = client.lrange(uSessionKey, maxLoginUserSize, -1)
        client.del(tokens.asScala:_*)
        client.ltrim(uSessionKey, 0, maxLoginUserSize - 1)
      }
    }
    token
  }
}
