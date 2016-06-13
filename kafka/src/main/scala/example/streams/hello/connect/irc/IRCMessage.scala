package example.streams.hello.connect.irc

import org.apache.kafka.connect.data.{Schema, Struct}

/**
  * Please doc ...
  */
/*case class IRCMessage(channel: String, user: IRCUser, message: String, override val schema: Schema)
  extends Struct(schema) {

  put("timestamp", System.currentTimeMillis())
  put("channel", channel)
  put("message", message)
  put("user", new Struct(schema.field("user").schema())
    .put("nick", user.getNick())
      .put("username", user.getUsername())
      .put("host", user.getHost())
  )

  override def toString() =
    s"""IRCMessage ${get("timestamp")} \t ${get("channel")} \t ${get("message")}"""
}*/
