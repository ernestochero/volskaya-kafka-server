package commons

import modules.ConfigurationModule
import zio.clock.Clock
import zio.random.Random
import zio.system.System
import zio.console.Console
import zio.blocking.Blocking
import scala.concurrent.Future
import zio.{ Task, ZIO }
object ZIOHelpers {
  type AppEnvironment = zio.ZEnv with ConfigurationModule
  val liveEnvironments: AppEnvironment =
    new Clock.Live with Console.Live with System.Live with Random.Live with Blocking.Live
    with ConfigurationModule.Live

  def fromFuture[A](f: Future[A]): Task[A] =
    ZIO.fromFuture(implicit ec => f.map(a => a))

  def eradicateNull[E, A](possiblyNullValue: A, errOnNull: E): ZIO[Any, E, A] =
    Option(possiblyNullValue).map(ZIO.succeed).getOrElse(ZIO.fail(errOnNull))

}
