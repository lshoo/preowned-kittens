/*
import play.api.mvc._
import play.api.routing._
import play.api.routing.sird._
import play.core.server.NettyServer

object StartServer {

  def main(args: Array[String]) {
    val server = NettyServer.fromRouter() {
      case GET(p"/posts") => Action {
        Results.Ok("All posts")
      }
      case GET(p"/post/$id") => Action {
        Results.Ok("Post " + id)
      }
    }

  }
}
*/
