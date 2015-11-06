import java.io.FileOutputStream
import java.net.URLDecoder
import java.util.concurrent.{ThreadPoolExecutor, Executors}

import com.flashbird.http.framework._
import com.flashbird.http.framework.request.DynamicPathParamsRequest
import com.twitter.finagle.httpx.{Response, Request}
import com.twitter.finatra.http.request.RequestUtils
import org.jboss.netty.buffer.ChannelBuffers
import java.io.File


/**
 * Created by yangguo on 15/9/23.
 */
case class User(username:String,password:String)
object HelloWorldController{
  implicit  val filter:HttpControllerFilter=new HttpControllerFilter {
    override def apply(request: Request, service: RouterService[Request, AnyRef]): AnyRef = {
      service(request)
    }
  }
}
class HelloWorldController extends Controller(Some("/test")){

  import HelloWorldController._
  get("/hello"){request:Request=>
    "hello word"
  }//(implicit val filter=Controller.filter)
  get("/hello1"){str:String=>
    "hello world"
  }
  get("/user"){request:Request=>
    val response=Response()
    response.setContentString(("yangguo"))//,"password")
    response
  }
  get("/t/:id/:name"){request:DynamicPathParamsRequest[Map[String,String]]=>
    val id=request.getDynamicPathParam("id")
    val name=request.getDynamicPathParam("name")
    val condition=request.getUrlParam("s")(0)
    println(request.content.getOrElse("username","default"))
    RangeResponseContent(0,List(User(name,id)),0,1)
  }
  post("/form"){request:Request=>

    RequestUtils.multiParams(request).foreach{kv=>
      val multriPart=kv._2
      if(!multriPart.isFormField){
        val outstream=new FileOutputStream(new File(URLDecoder.decode(multriPart.filename.getOrElse("default"))))
        outstream.write(multriPart.data)
        outstream.flush();
        outstream.close()

      }
      println(URLDecoder.decode(kv._1)+","+kv._2.isFormField+","+kv._2.filename)
    }
  }
}
