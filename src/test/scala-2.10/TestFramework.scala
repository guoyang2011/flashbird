import java.net.InetSocketAddress
import java.util
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import com.flashbird.http.framework.{RoutersPool, HttpService}
import com.twitter.finagle.{Filter, Service}
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.httpx.{Response, Http, Request}
import com.twitter.util.{Future, Duration}


/**
 * Created by yangguo on 15/9/23.
 */
object TestFramework {

  def main(args: Array[String]) {
    val helloController=new HelloWorldController
    val httpService=new HttpService(RoutersPool.routers)
    ServerBuilder()
      .name("helloword")
      .codec((Http()))
      .readTimeout(Duration(5,TimeUnit.SECONDS))
      .bindTo(new InetSocketAddress(10001))
      .build(httpService)
  }
}
