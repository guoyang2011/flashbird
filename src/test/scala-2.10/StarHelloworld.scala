import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import com.flashbird.http.framework.{RoutersPool, HttpService}
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.httpx.{Http}
import com.twitter.util.Duration

/**
 * Created by yangguo on 15/10/10.
 */
object StarHelloworld {
  def main(args: Array[String]) {
    val helloController=new HelloWorldController
    val httpService=new HttpService(RoutersPool.routers)
    ServerBuilder()
      .name("helloword")
      .codec(Http())
      .readTimeout(Duration(5,TimeUnit.SECONDS))
      .bindTo(new InetSocketAddress(10001))
      .build(httpService)
  }
}

