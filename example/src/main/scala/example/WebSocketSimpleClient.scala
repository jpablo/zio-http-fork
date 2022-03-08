package example

import zhttp.service.transport.Transport.Auto
import zhttp.socket.{Socket, WebSocketFrame}
import zio._
import zio.stream.ZStream

object WebSocketSimpleClient extends zio.App {

  // Setup client envs
  val env = Auto.live

  val url = "ws://localhost:8090/subscriptions"

  val app = Socket
    .collect[WebSocketFrame] {
      case WebSocketFrame.Text("BAZ") => ZStream.succeed(WebSocketFrame.close(1000))
      case frame                      => ZStream.succeed(frame)
    }
    .toSocketApp
    .connect(url)

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    app.exitCode.provideCustomLayer(env)
  }
}
