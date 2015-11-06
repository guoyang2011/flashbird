package com.flashbird.http.util

/**
 * Created by yangguo on 15/9/23.
 */
package exception {

case class JsonEncoderException(msg: String) extends Exception(msg)

case class JsonDecoderException(msg: String) extends Exception(msg)

case class NotFindRouterException(msg: String) extends Exception(msg)
}
