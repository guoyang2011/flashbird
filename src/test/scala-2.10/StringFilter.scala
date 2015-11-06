import com.twitter.finagle
import com.twitter.finagle.Filter
import com.twitter.finagle.httpx.Request
import com.twitter.util.Future

/**
 * Created by yangguo on 15/9/23.
 */
/**
 * Filter Chain 作用按照定义的Chain链对输入数据进行过滤或预处理,对于Filter过滤条件的进行放行
 *
 */

object StringFilter extends App{
  type Service[ReqIn,RepOut]= (ReqIn)=>RepOut
  type FilterHandler[ReqIn,RepOut]=(ReqIn,Service[ReqIn,RepOut])=>RepOut
  trait HttpService[ReqIn,RepOut] extends Service[ReqIn,RepOut]
  trait SimpleFilter[ReqIn,RepOut] extends FilterHandler[ReqIn,RepOut]{
    def addThen(filter:SimpleFilter[ReqIn,RepOut]):SimpleFilter[ReqIn,RepOut]={
      new SimpleFilter[ReqIn,RepOut] {
        def apply(request: ReqIn, service: Service[ReqIn, RepOut]): RepOut = {
          SimpleFilter.this(
            request,
            new HttpService[ReqIn,RepOut] {
              override def apply(v1: ReqIn): RepOut = filter(v1,service)
            })
        }
      }
    }
    def andThen(service: Service[ReqIn,RepOut])={
      new Service[ReqIn,RepOut]{
        override def apply(v1: ReqIn): RepOut = SimpleFilter.this(v1,service)
      }
    }
  }

  val filter1=new SimpleFilter[String,String] {
    override def apply(v1: String, v2: Service[String, String]): String = {
      println("filter1")
      v2(v1+"filter1")
    }
  }
  val filter2=new SimpleFilter[String,String] {
    override def apply(v1: String, v2: Service[String, String]): String = {
      println("filter3")
      v2(v1+"filter3")
    }
  }
  val filter=filter1 addThen filter2 andThen new Service[String,String]{
    override def apply(v1: String): String ={
      println("filter4")
      v1
    }
  }
  println(filter("hello"))

  val val1=new Filter[String,Int,Object,Int]{
    override def apply(request: String, service: finagle.Service[Object, Int]): Future[Int] = {
      println("1")
      service(request)
    }
  }

  appp("")
  case class Bean(st:String)
  appp(Bean(""))
  def appp(obj:AnyRef)=println(obj.toString+","+obj.getClass.toString)
  //Filter[Request,Service[Request,Response]]:Response

}

