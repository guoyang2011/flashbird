package com.flashbird.http.util

import java.io.{File, FileInputStream}

import com.flashbird.http.framework.authorization.{DefaultRedisAuthorizationProvider, Authorization}
import org.yaml.snakeyaml.Yaml
import scala.collection.JavaConverters._


/**
 * Created by yangguo on 15/10/10.
 */

object FlashBirdConfig {
  val key_access_token_info:String="id"

  private var _config:java.util.Map[String,AnyRef]= _
  def apply(configPath:String):Unit={
    val configFile=new File(configPath)
    if(!configFile.exists() || !configFile.isFile){
      throw new IllegalArgumentException(s"Config File[$configPath] Is Not Exists")
    }
    val yaml=new Yaml()
    val config= yaml.load(new FileInputStream(new File(configPath))).asInstanceOf[java.util.Map[String,AnyRef]]
    apply(config)
  }
  def apply(config:java.util.Map[String,AnyRef]):Unit={
    _config=config
    println(JsonParser.objectToJsonStringParser(_config))
  }

  def getServerName(key:String="com.ancare.name",defaultValue:String="ancare-01"):String=toString(key,defaultValue)

  private def toString(key:String,defaultValue:String):String={
    val obj=_config.get(key)
    if(obj==null) defaultValue
    else obj.toString
  }
  def getServerHost(key:String="com.ancare.host",defaultValue:String="localhost"):String=toString(key,defaultValue)

  private def toInt(key:String,defaultValue:Int):Int={
    val obj=_config.get(key)
    if(obj==null) defaultValue
    else {
      if (obj.isInstanceOf[Int]) obj.asInstanceOf[Int]
      else
        try {
          obj.toString.toInt
        } catch {
          case ex: Throwable => defaultValue
        }
    }
  }
  def getServerPort(key:String="com.ancare.port",defaultValue:Int=10001):Int=toInt(key,defaultValue)

  def getServerRequestTimeOut(key:String="com.ancare.server.request.timeout",defaultValue:Int=45):Int=toInt(key,defaultValue)

  def getServerRequestReadTimeOut(key:String="com.ancare.server.request.read.timeout",defaultValue:Int=15):Int=toInt(key,defaultValue)

  def getServerRequestMaxRecvBufferSize(key:String="com.ancare.server.max.recv.buffer.size",defaultValue:Int=5242880):Int=toInt(key,defaultValue)

  def getServerResponseMaxBufferSize(key:String="com.ancare.server.max.response.buffer.size",defaultValue:Int=5242880):Int=toInt(key,defaultValue)

  def getServerConnectMaxIdleTime(key:String="com.ancare.server.connect.max.idle.time",defaultValue:Int=5):Int=toInt(key,defaultValue)

  def getServerConnectMaxLifeCycleTime(key:String="com.ancare.server.connect.max.lifecycle.time",defaultValue:Int=60):Int=toInt(key,defaultValue)

  def getServerMaxConnectedClientNumber(key:String="com.ancare.server.max.connected.client.number",defaultValue:Int=1024):Int=toInt(key,defaultValue)


  def getSMSServerURL(key:String="com.changhong.sms.server.url",defaultValue:String="")=toString(key,defaultValue)
  def getSMSServerApiKey(key:String="com.changhong.sms.server.appkey",defaultValue:String="")=toString(key,defaultValue)
  def getSMSServerSecretKey(key:String="com.changhong.sms.server.secretkey",defaultValue:String="")=toString(key,defaultValue)

  private def toBoolean(key:String,defaultValue:Boolean):Boolean={
    val obj=_config.get(key)
    if(obj==null) defaultValue
    else {
      if (obj.isInstanceOf[Boolean]) obj.asInstanceOf[Boolean]
      else
        try {
          obj.toString.toBoolean
        } catch {
          case ex: Throwable => defaultValue
        }
    }
  }
  def getServerConnectIsKeepAlived(key:String="com.ancare.server.connect.keepalive",defaultValue:Boolean=false):Boolean=toBoolean(key,defaultValue)

  def getServerStorageLocalDir(key:String="com.ancare.storage.local.dir",defaultValue:String="/usr/share/nginx/html/lazystore/img_store/ancare"):String=toString(key,defaultValue)
  def getNginxFileStorageProxyUrl(key:String="com.ancare.nginx.filestorage.proxy.url",defaultValue:String="http://chphone.cn:8085/ancare"):String=toString(key,defaultValue)

  def getValue(key:String,defaultValue:String):String=toString(key,defaultValue)
  def getValue(key:String,defaultValue:List[String]):List[String]=toList(key,defaultValue)
  def getValue(key:String,defaultValue:Int):Int=toInt(key,defaultValue)
  private def toList[T:Manifest](key:String,defaultValue:List[T]):List[T]= {
    val obj = _config.get(key)
    if (obj != null) {
      if (obj.isInstanceOf[java.util.List[T]]) {
        try {
          val _tmp=obj.asInstanceOf[java.util.List[T]].asScala.toList
          if(_tmp.size>0) _tmp
          else defaultValue
        } catch {
          case ex: Throwable =>{
            ex.printStackTrace()
            defaultValue
          }
        }
      } else defaultValue
    } else defaultValue
  }
  def getRedisCacheServers(key:String="com.ancare.redis.cache.servers",defaultValues:List[String]=List("localhost:6379")):List[String]=toList(key,defaultValues)

  def getMysqlServerHosts(key:String="com.ancare.mysql.servers.hosts",defaultValue:String=""):String=toString(key,defaultValue)

  def getMysqlServerDatabaseName(key:String="com.ancare.mysql.servers.database",defaultValue:String=""):String=toString(key,defaultValue)
  def getMysqlServerUserName(key:String="com.ancare.mysql.servers.username",defaultValue:String=""):String=toString(key,defaultValue)
  def getMysqlServerUserPwd(key:String="com.ancare.mysql.servers.password",defaultValue:String=""):String=toString(key,defaultValue)

  def getRequestHeaderAuthorizationKey(key:String="com.ancare.authorization.key",defaultValue:String="Authorization"):String=toString(key,defaultValue)

  def getSMSVerifyCodeExpiredTime(key:String="com.ancare.server.user.register.verifycode.expired",defaultValue:Int=300)=toInt(key,defaultValue)

  def getTokenExpiredTime(key:String="com.ancare.server.user.token.expired",defaultValue:Int=86400):Int=toInt(key,defaultValue)


  private[this] var routerIsAuth:Boolean=true

  def setRouterIsAuth(boolean: Boolean)=routerIsAuth=boolean
  def getRouterIsAuth()=routerIsAuth

  private var authProvider:Authorization=null
  def getAuthProvider(key:String="com.flashbird.http.framework.authorization",defaultValue:Authorization=new DefaultRedisAuthorizationProvider)={
    if(authProvider==null) authProvider=try {
      val clsName=getValue(key,"com.flashbird.http.framework.authorization.DefaultRedisAuthorizationProvider")
      val cls=Class.forName(clsName)
      cls.newInstance().asInstanceOf[Authorization]
    }catch{
      case ex:Throwable=> new DefaultRedisAuthorizationProvider
    }
    authProvider
  }
  def getMaxLoginUserCount(key:String="com.flashbird.framework.max.login.size",defaultValue:Int=1):Int={
    getValue(key,defaultValue)
  }




}
