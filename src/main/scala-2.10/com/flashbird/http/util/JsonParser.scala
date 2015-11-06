package com.flashbird.http.util

import java.nio.charset.Charset

import com.flashbird.http.util.exception.{JsonEncoderException, JsonDecoderException}
import net.liftweb.json.Extraction._
import net.liftweb.json._
import net.liftweb.json.DefaultFormats
import org.jboss.netty.buffer.{ChannelBuffer, ChannelBuffers}

/**
 * Created by yangguo on 15/9/21.
 */

object JsonParser {
  implicit val jsonFormats=DefaultFormats
  def jsonStringToObjectParser[T:Manifest](jsonString:String):T={

    try {
        if (util.runtimeClassEq[T, String]) jsonString.asInstanceOf[T]
        else parse(jsonString).extract[T]
    }catch {
      case ex:Throwable=>throw new JsonDecoderException(ex.getMessage)
    }
  }
  def objectToJsonStringParser[T:Manifest](obj:T):String={
    try {
      if(util.runtimeClassEq[T,String]) obj.asInstanceOf[String]
      else compact(render(decompose(obj)))
    }catch{
      case ex:Throwable=>throw new JsonEncoderException(ex.getMessage)
    }
  }
  def objectToChannelBufferParser[T:Manifest](obj:T,charset:Charset=Charset.forName("UTF-8")):ChannelBuffer={
    val jsonStr=objectToJsonStringParser[T](obj)
    ChannelBuffers.wrappedBuffer(jsonStr.getBytes(charset))
  }
  def channelBufferToObjectParser[T:Manifest](cb:ChannelBuffer,charset:Charset=Charset.forName("UTF-8")):T={
    val bytes=if(cb.isDirect){
      val _bytes=new Array[Byte](cb.readableBytes())
      cb.readBytes(_bytes)
      _bytes
    }else cb.array()
    val jsonStr=new String(bytes,charset)
    jsonStringToObjectParser[T](jsonStr)
  }
}
