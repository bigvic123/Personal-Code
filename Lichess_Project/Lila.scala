package lila.app

import play.api.inject.DefaultApplicationLifecycle
import play.api.Configuration

// The program entry point.
// To run with bloop:
// /path/to/bloop run lila -m lila.app.Lila -c /path/to/lila/.bloop
object Lila:

  def main(args: Array[String]): Unit =
    lila.web.PlayServer.start(args): env =>
      LilaComponents(
        env,
        DefaultApplicationLifecycle(),
        Configuration.load(env)
      ).application
