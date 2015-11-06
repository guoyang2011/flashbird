import java.util.Collections
import java.util.concurrent._

/**
 * Created by yangguo on 15/9/28.
 */
class Task(val id:Int) extends Runnable{
  override def equals(obj: scala.Any): Boolean = {
    var res=false
    if(obj!=null&&obj.isInstanceOf[Task]) {
      val _tmp=obj.asInstanceOf[Task]
      if(_tmp.id==this.id) res=true
    }
    res
  }

  override def run(): Unit = println(id)
}
class KeyTask(val id:String){
  override def hashCode(): Int = id.hashCode

  override def equals(obj: scala.Any): Boolean = {
    var res=false
    if(obj!=null&&obj.isInstanceOf[KeyTask]) if(obj.asInstanceOf[KeyTask].id.equals(this.id)) res=true
    res
  }
}




object TestExcutores {
  def main(args: Array[String]) {
//    val queue=

    val pool = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable]())

    val run4=new Task(4)
    val run3=new Task(3)
    val run2=new Task(2) {
      override def run(): Unit = {
        Thread.sleep(3000)
        println("2"+","+pool.getQueue.size())
//        pool.remove(run3)
//        pool.remove(run4)
      }
    }
    val run1=new Task(1) {
      override def run(): Unit = {
        Thread.sleep(1000)
        println(1)
      }
    }
//    pool.execute(run1)
    pool.execute(run2)
    Thread.sleep(1000)
    pool.remove(run2)

//    pool.execute(run3)
//    pool.execute(run4)
    val map=new ConcurrentHashMap[KeyTask,AnyRef]()//.get()
    map.put(new KeyTask("1"),"2")
    map.put(new KeyTask("2"),"3")
    println(map.get(new KeyTask("2")))

  }
}
abstract class QueueLink[T] extends LinkedBlockingQueue[T]{
  override def remove(o: scala.Any): Boolean = super.remove(o)
}
