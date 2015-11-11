package com.flashbird.http.util

import org.apache.commons.dbcp.BasicDataSource

import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.jdbc.{GetResult, StaticQuery => Q}

/**
 * Created by yangguo on 15/9/25.
 */
object SqlProvider {
  import scala.slick.driver.MySQLDriver.simple._
  private lazy val dbPool={
    val hostAndPort=FlashBirdConfig.getMysqlServerHosts()
    val databaseName=FlashBirdConfig.getMysqlServerDatabaseName()
    val username=FlashBirdConfig.getMysqlServerUserName()
    val password=FlashBirdConfig.getMysqlServerUserPwd()

    val url=s"jdbc:mysql://$hostAndPort/$databaseName?characterEncoding=UTF-8&autoReconnect=true"

    val ds=new BasicDataSource
    ds.setDriverClassName("com.mysql.jdbc.Driver")
    ds.setUsername(username)
    ds.setPassword(password)
    ds.setMaxActive(20)
    ds.setMaxIdle(10)
    ds.setInitialSize(5)
    ds.setTestOnBorrow(true)
    ds.setUrl(url)
    ds.setMaxWait(1)
    Database.forDataSource(ds)
  }
  def noTransactionExec[ResultType](sql:String)(implicit result:GetResult[ResultType])={
    dbPool.withSession{implicit session=>
      println(sql)
      sql"#$sql".as(result).list
    }
  }
  def transactionExec[ResultType](sqls:List[(Int,String)])(implicit result:GetResult[ResultType])={
    dbPool.withTransaction{implicit session=>
      sqls.sortBy(_._1).map{kv=>
        val sql=kv._2
        println(sql)
        sql"#$sql".as(result).list
      }
    }
  }

}
