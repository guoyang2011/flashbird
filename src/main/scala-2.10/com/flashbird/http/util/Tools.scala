package com.flashbird.http.util

import java.io.{Closeable, FileOutputStream, File}
import java.security.MessageDigest
import java.util.UUID

import sun.misc.BASE64Encoder

/**
 * Created by yangguo on 15/10/12.
 */
object Tools {
  def encryptString(str:String): String ={
    new BASE64Encoder().encode(str.getBytes())
  }

  def md5(str:String):String={
    val md5=MessageDigest.getInstance("MD5")
    md5.update(str.getBytes)
    val bytes=md5.digest()
    val res=bytes.map{byte=>
      var tbyte:Int=0
      if(byte<0) tbyte = 256 +byte
      else tbyte=byte
      val prefix=if(tbyte<16) "0"
      else ""
      prefix+Integer.toHexString(tbyte)
    }.reduce(_+_)
    res
  }
  def shortFileStorage(bytes:Array[Byte],filename:Option[String]):Option[String]={
    val storageFileName=UUID.randomUUID().toString.replaceAll("-","")
    val path=FlashBirdConfig.getServerStorageLocalDir()+storageFileName
    val outStream=new FileOutputStream(new File(path))
    try{
      outStream.write(bytes)
      val proxyPath=FlashBirdConfig.getNginxFileStorageProxyUrl()+storageFileName
      Some(proxyPath)
    }catch{
      case ex:Throwable=>None
    }finally {
      unsafeCloseStream(outStream)
    }
  }
  def unsafeCloseStream(stream:Closeable): Unit ={
    try{
      if(stream!=null) stream.close()
    }catch{
      case ex:Throwable=> ex.printStackTrace()
    }
  }
}

