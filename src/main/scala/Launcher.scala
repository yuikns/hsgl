import org.slf4j.LoggerFactory
import tutorial.JavaCLTutorial1

case class ArgsOpt(
  dir: String = "",
  delta: Int = 3,
  step: Int = 20) {

  override def toString =
    s"dir : $dir\ngraph : $graph\n" +
      s"logs : $logs\ndelta : $delta\nstep : $step"

  def logs = s"$dir/logs.txt"

  def graph = s"$dir/graph.txt"

  def nodeDict = s"$dir/node_dict.txt"

  def init() = this.suit(suitCands("small"))

  def suit(s: String) = this.copy(dir = s"data/dataset/$s")

  def suitCands = Map[String, String](
    "small" -> "ER_small",
    "middle" -> "ER_middle",
    "large" -> "ER_large"
  )

  def withArgs(args: Array[String], offset: Int): ArgsOpt = {
    //lazy val logger = LoggerFactory.getLogger(this.getClass)
    def noEnoughParameterErrorLog(s: String): Unit = {
      println(s"no enough parameter for $s")
    }
    def unExpectParameterErrorLog(s: String, e: String): Unit = {
      println(s"unexpect parameter after: $s $e")
    }
    if (args.length > offset) {
      args(offset) match {
        case "-d" => // delta
          if (args.length > offset) {
            val nxArgs: (ArgsOpt, Int) = try {
              (this.copy(delta = args(offset + 1).toInt), offset + 2)
            } catch {
              case t: Throwable =>
                unExpectParameterErrorLog(args(offset), t.getLocalizedMessage)
                (this, offset + 1)
            }
            nxArgs._1.withArgs(args, nxArgs._2)
          } else {
            noEnoughParameterErrorLog(args(offset))
            this
          }
        case "-l" => // log step
          if (args.length > offset) {
            val nxArgs: (ArgsOpt, Int) = try {
              (this.copy(step = args(offset + 1).toInt), offset + 2)
            } catch {
              case t: Throwable =>
                unExpectParameterErrorLog(args(offset), t.getLocalizedMessage)
                (this, offset + 1)
            }
            nxArgs._1.withArgs(args, nxArgs._2)
          } else {
            noEnoughParameterErrorLog(args(offset))
            this
          }
        case "-s" =>
          if (args.length > offset) {
            val nxArgs: (ArgsOpt, Int) = try {
              (this.copy(dir = args(offset + 1)), offset + 2)
            } catch {
              case t: Throwable =>
                unExpectParameterErrorLog(args(offset), t.getLocalizedMessage)
                (this, offset + 1)
            }
            nxArgs._1.withArgs(args, nxArgs._2)
          } else {
            noEnoughParameterErrorLog(args(offset))
            this
          }
        case "-v" =>
          this.copy(step = 1).withArgs(args, offset + 1)
        case "-h" =>
          println(s"pattern-counter [-s dir] [-l log step] [-d delta] [-v] [suit/dir]")
          this
        case s: String =>
          if (suitCands.contains(s)) {
            this.suit(suitCands(s)).withArgs(args, offset + 1)
          } else {
            this.suit(s).withArgs(args, offset + 1)
          }
      }
    } else {
      this
    }
  }
}

/**
 * @author yu
 */
object Launcher extends App {
  lazy val logger = LoggerFactory.getLogger(Launcher.getClass)
  println(s"args: $args")
  val opts = ArgsOpt().init().withArgs(args, 0)
  println(s"Options:\n$opts")
  logger.info("all starting ...")
  val timeStart = System.currentTimeMillis()

  JavaCLTutorial1.test(args)

  logger.info(s"all done , all time cost: ${System.currentTimeMillis() - timeStart} ms")
}
