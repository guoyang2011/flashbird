package com.flashbird.http.util

import scala.slick.jdbc.{GetResult, PositionedResult}

/**
 * Created by yangguo on 15/9/25.
 */
object UserDefinedGetResult {
  implicit object SlickResultMap extends GetResult[Map[String,AnyRef]]{
    override def apply(pr: PositionedResult): Map[String, AnyRef] = {
      val cvalues=pr.rs
      val cnames=cvalues.getMetaData
      val res = (1 to pr.numColumns).map{ i=>
        val obj=cvalues.getObject(i)
        val alias=cnames.getColumnName(i)
        (alias->obj)
      }
      res.toMap
    }
  }
}
