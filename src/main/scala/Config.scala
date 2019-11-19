import zio.kafka.client.{ ConsumerSettings, ProducerSettings }
import zio.duration._
object Config {
  val consumerSettings: ConsumerSettings = ConsumerSettings(
    bootstrapServers = List("127.0.0.1:9092"),
    groupId = "group",
    clientId = "client",
    closeTimeout = 30.seconds,
    extraDriverSettings = Map.empty,
    pollInterval = 250.milliseconds,
    pollTimeout = 50.milliseconds,
    perPartitionChunkPrefetch = 2
  )
  val producerSettings: ProducerSettings = ProducerSettings(
    bootstrapServers = List("127.0.0.1:9092"),
    closeTimeout = 30.seconds,
    extraDriverSettings = Map.empty
  )
}
