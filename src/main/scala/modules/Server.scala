package modules
import zio.interop.catz._
import org.apache.kafka.common.serialization.Serdes
import zio.ZIO
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.kafka.client.{Consumer, Subscription}
import zio.random.Random
import zio.system.System
import zio.duration._

class Server extends CatsApp {
  implicit val serdeString = Serdes.String()

  val logic: ZIO[Console with ConfigurationModule, Nothing, Int] = (for {
    configuration <- ConfigurationModule.factory.configuration
    fib <- Consumer
      .consumeWith[Console, String, String](
        Subscription.Topics(Set("test")),
        configuration.kafka.consumer
      ) { (key, value) =>
        putStrLn(s"Received message ${key}: ${value}")
      // Perform an effect with the received message
      }
      .fork
    _ <- ZIO.sleep(20.seconds)
    _ <- fib.interrupt
    _ <- fib.join.ignore
  } yield 0).catchAll(err => putStrLn(err.toString).as(1))

  val program = logic.provideSome[zio.ZEnv] { env =>
    new System with Clock with Console with Blocking with Random
    with ConfigurationModule.Live {
      override val system: System.Service[Any] = env.system
      override val clock: Clock.Service[Any] = env.clock
      override val console: Console.Service[Any] = env.console
      override val blocking: Blocking.Service[Any] = env.blocking
      override val random: Random.Service[Any] = env.random
    }
  }
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = program
}
