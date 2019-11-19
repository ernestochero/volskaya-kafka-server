import modules.ConfigurationModule
import zio._
import zio.console.{ Console, putStrLn }
import zio.kafka.client.serde._
import zio.kafka.client.{ Consumer, Subscription }
import Config._
import commons.ZIOHelpers._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.internal.PlatformLive
object Server extends App {
  val appRuntime = Runtime(liveEnvironments, PlatformLive.Default)
  def services: ZIO[Clock with Blocking with Console with ConfigurationModule, Throwable, Unit] =
    for {
      configuration <- ConfigurationModule.factory.configuration
      _             <- putStrLn(s"Application ${configuration.appName}")
      subscription = Subscription.topics("test")
      consumer     = Consumer.make(consumerSettings)
      fib <- consumer.use { c =>
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
    } yield fib
  def run(args: List[String]): ZIO[Any, Nothing, Int] =
    services.provide(liveEnvironments).fold(_ => 1, _ => 0)
}
