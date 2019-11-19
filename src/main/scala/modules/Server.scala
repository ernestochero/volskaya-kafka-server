package modules
import zio.interop.catz._
import zio.ZIO
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.kafka.client.{Consumer, Subscription}
import zio.random.Random
import zio.system.System
import zio.kafka.client.serde._

class Server extends CatsApp {
  val logic: ZIO[Console with Blocking with Clock with ConfigurationModule,
                 Nothing,
                 Int] = (for {
    configuration <- ConfigurationModule.factory.configuration
    subscription = Subscription.topics("topic")
    consumer = Consumer.make(configuration.kafka.consumer)
    _ <- consumer.use { c =>
      c.subscribeAnd(subscription)
        .plainStream(Serde.string, Serde.string)
        .flattenChunks
        .tap(
          cr => putStrLn(s"key: ${cr.record.key}, value: ${cr.record.value}")
        )
        .map(_.offset)
        .aggregate(Consumer.offsetBatches)
        .mapM(_.commit)
        .runDrain
    }
    _ <- Consumer.consumeWith(
      configuration.kafka.consumer,
      subscription,
      Serde.string,
      Serde.string
    ) {
      case (key, value) =>
        putStrLn(s"Received message ${key}: ${value}")
      // Perform an effect with the received message
    }
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
