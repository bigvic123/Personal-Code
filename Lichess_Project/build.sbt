import com.typesafe.sbt.packager.Keys.{ bashScriptExtraDefines, scriptClasspath }
import play.sbt.PlayCommands
import play.sbt.PlayInternalKeys.playDependencyClasspath
import play.sbt.routes.RoutesKeys

import BuildSettings._
import Dependencies._

lazy val root = Project("lila", file("."))
  .enablePlugins(JavaServerAppPackaging, RoutesCompiler)
  .dependsOn(api)
  .aggregate(api)
  .settings(buildSettings)
  .settings(scalacOptions ++= Seq("-unchecked", "-deprecation"))

organization         := "org.lichess"
Compile / run / fork := true
javaOptions in ThisBuild ++= Seq("-Xms2G", "-Xmx4G", "-XX:+UseG1GC")
scriptClasspath             := Seq("*")
Compile / resourceDirectory := baseDirectory.value / "conf"
shellPrompt := PlayCommands.playPrompt
playDependencyClasspath := (Runtime / externalDependencyClasspath).value
ivyLoggingLevel     := UpdateLogging.DownloadOnly
Compile / mainClass := Some("lila.app.Lila")
bashScriptExtraDefines += "addJava \"-Duser.dir=$(realpath \"$(cd \"${app_home}/..\"; pwd -P)\"  $(is_cygwin && echo \"fix\"))\"\n"
Compile / RoutesKeys.generateReverseRouter := false
Compile / RoutesKeys.routes / sources ++= {
  val dirs = (Compile / unmanagedResourceDirectories).value
  (dirs * "routes").get ++ (dirs * "*.routes").get
}
Compile / RoutesKeys.generateReverseRouter := false
Compile / RoutesKeys.generateForwardRouter := true
target := baseDirectory.value / "target"
Compile / sourceDirectory := baseDirectory.value / "app"
Compile / scalaSource := baseDirectory.value / "app"
Universal / sourceDirectory := baseDirectory.value / "dist"
ThisBuild / libraryDependencySchemes ++= Seq(
  "org.typelevel" %% "cats-parse" % VersionScheme.Always
)
javacOptions ++= Seq("-source", "11", "-target", "11")
libraryDependencies ++= akka.bundle ++ playWs.bundle ++ macwire.bundle ++ scalalib.bundle ++ chess.bundle ++ Seq(
  play.json, play.logback, compression, hasher,
  reactivemongo.driver, maxmind, scalatags,
  kamon.core, kamon.influxdb, kamon.metrics, kamon.prometheus,
  scaffeine, caffeine, lettuce, uaparser, nettyTransport, reactivemongo.shaded
) ++ tests.bundle
lazy val modules = Seq(
  core, coreI18n, ui, common, tree, db, room, search,
  memo, rating, game, gathering, study, user, puzzle, analyse,
  report, pref, chat, playban, lobby, mailer, oauth,
  insight, evaluation, storm, security, tournament, relay, plan, round,
  swiss, insight, fishnet, tutor, mod, challenge, web, team, forum,
  streamer, simul, activity, msg, ublog, notifyModule, clas, perfStat,
  opening, timeline, setup, video, fide, push, pool, lobby, relation, tv,
  coordinate, feed, history, shutup, appeal, irc, explorer, learn, event,
  coach, practice, evalCache, irwin, bot, racer, cms, i18n, socket,
  bookmark, studySearch, gameSearch, forumSearch, teamSearch
)
lazy val moduleRefs = modules map projectToRef
lazy val moduleCPDeps = moduleRefs map { sbt.ClasspathDependency(_, None) }
lazy val core = module("core", Seq(), Seq(scalatags, galimatias) ++ scalalib.bundle ++ reactivemongo.bundle ++ tests.bundle)
lazy val coreI18n = module("coreI18n", Seq(), Seq(scalatags) ++ scalalib.bundle)
lazy val common = module("common", Seq(core), Seq(kamon.core, scaffeine, apacheText, chess.playJson) ++ tests.bundle ++ flexmark.bundle)
lazy val db = module("db", Seq(common), Seq(hasher) ++ macwire.bundle)
lazy val memo = module("memo", Seq(db), Seq(scaffeine) ++ playWs.bundle)
lazy val i18n = module("i18n", Seq(common, coreI18n), tests.bundle).settings(
  Compile / resourceGenerators += Def.task {
    val outputFile = (Compile / resourceManaged).value / "I18n.ser"
    I18n.serialize(
      sourceDir = new File("translation/source"),
      destDir = new File("translation/dest"),
      dbs = "site arena emails learn activity coordinates study class contact appeal patron coach broadcast streamer tfa settings preferences team perfStat search tourname faq lag swiss puzzle puzzleTheme challenge storm ublog insight keyboardMove timeago oauthScope dgt voiceCommands onboarding".split(' ').toList,
      outputFile
    )
  }.taskValue
)
lazy val rating = module("rating", Seq(db, ui), tests.bundle ++ Seq(apacheMath)).dependsOn(common % "test->test")
lazy val cms = module("cms", Seq(memo, coreI18n, ui), Seq())
lazy val puzzle = module("puzzle", Seq(coreI18n, tree, memo, rating, ui), tests.bundle)
lazy val storm = module("storm", Seq(puzzle), Seq())
lazy val racer = module("racer", Seq(storm, room), Seq())
lazy val video = module("video", Seq(memo, ui), macwire.bundle)
lazy val coach = module("coach", Seq(memo, rating, ui), Seq())
lazy val streamer = module("streamer", Seq(ui, memo), Seq())
lazy val coordinate = module("coordinate", Seq(db, ui), macwire.bundle)
lazy val feed = module("feed", Seq(memo, ui), Seq())
lazy val ublog = module("ublog", Seq(coreI18n, memo, ui), Seq(bloomFilter))
lazy val evaluation = module("evaluation", Seq(analyse, game), tests.bundle)
lazy val perfStat = module("perfStat", Seq(ui, memo, rating), Seq())
lazy val history = module("history", Seq(rating, memo), Seq())
lazy val search = module("search", Seq(common), playWs.bundle)
lazy val chat = module("chat", Seq(memo, ui), Seq())
lazy val room = module("room", Seq(common), Seq(lettuce))
lazy val timeline = module("timeline", Seq(memo, ui), Seq())
lazy val event = module("event", Seq(memo, coreI18n, ui), Seq())
lazy val mod = module("mod", Seq(evaluation, report, chat, user), Seq())
lazy val user = module("user", Seq(rating, memo), Seq(hasher) ++ tests.bundle ++ playWs.bundle)
lazy val game = module("game", Seq(tree, rating, memo), Seq(compression) ++ tests.bundle)
lazy val gameSearch = module("gameSearch", Seq(coreI18n, search, ui), Seq())
lazy val tv = module("tv", Seq(game), Seq(hasher))
lazy val bot = module("bot", Seq(chat, game), Seq())
lazy val analyse = module("analyse", Seq(coreI18n, tree, memo, ui), tests.bundle)
lazy val round = module("round", Seq(room, game, user, playban, pref, chat), Seq(hasher, kamon.core, lettuce) ++ tests.bundle)
lazy val pool = module("pool", Seq(coreI18n, db, rating), Seq())
lazy val activity = module("activity", Seq(puzzle), Seq())
lazy val lobby = module("lobby", Seq(memo, rating), Seq(lettuce) ++ tests.bundle)
lazy val setup = module("setup", Seq(lobby, ui), Seq())
lazy val insight = module("insight", Seq(analyse, game), Seq())
lazy val tutor = module("tutor", Seq(insight, ui), tests.bundle)
lazy val opening = module("opening", Seq(coreI18n, memo, ui), tests.bundle)
lazy val gathering = module("gathering", Seq(rating), tests.bundle)
lazy val tournament = module("tournament", Seq(gathering, room, memo, ui), Seq(lettuce) ++ tests.bundle)
     lazy val swiss = module("swiss", Seq(gathering, room, memo), Seq(lettuce) ++ tests.bundle)
     lazy val simul = module("simul", Seq(gathering, room, memo), Seq(lettuce))
     lazy val fishnet = module("fishnet", Seq(analyse), Seq(lettuce) ++ tests.bundle)
     lazy val irwin = module("irwin", Seq(analyse, game, report), Seq())
     lazy val oauth = module("oauth", Seq(memo, coreI18n, ui), Seq())
     lazy val security = module("security", Seq(oauth, user, mailer), Seq(maxmind, hasher, uaparser) ++ tests.bundle)
     lazy val shutup = module("shutup", Seq(db), tests.bundle)
     lazy val challenge = module("challenge", Seq(game, room, oauth), Seq(lettuce) ++ tests.bundle)
     lazy val fide = module("fide", Seq(memo, ui), Seq())
     lazy val study = module("study", Seq(coreI18n, tree, memo, room, ui), Seq(lettuce) ++ tests.bundle ++ Seq(scalacheck, munitCheck, chess.testKit)).dependsOn(common % "test->test")
     lazy val relay = module("relay", Seq(study, game), tests.bundle)
     lazy val studySearch = module("studySearch", Seq(study, search), Seq())
     lazy val learn = module("learn", Seq(db), Seq())
     lazy val evalCache = module("evalCache", Seq(tree, memo), Seq())
     lazy val practice = module("practice", Seq(study), Seq())
     lazy val playban = module("playban", Seq(memo), Seq())
     lazy val push = module("push", Seq(db), playWs.bundle ++ Seq(googleOAuth))
     lazy val irc = module("irc", Seq(common), playWs.bundle)
     lazy val mailer = module("mailer", Seq(memo, coreI18n), Seq(hasher, play.mailer))
     lazy val plan = module("plan", Seq(coreI18n, memo, ui), tests.bundle)
     lazy val relation = module("relation", Seq(memo, ui), Seq())
     lazy val pref = module("pref", Seq(coreI18n, memo, ui), Seq())
     lazy val msg = module("msg", Seq(coreI18n, memo), Seq())
     lazy val forum = module("forum", Seq(memo, ui), Seq())
     lazy val forumSearch = module("forumSearch", Seq(search), Seq())
     lazy val team = module("team", Seq(memo, room, ui), Seq())
     lazy val teamSearch = module("teamSearch", Seq(search), Seq())
     lazy val clas = module("clas", Seq(user, puzzle), Seq(bloomFilter))
     lazy val bookmark = module("bookmark", Seq(db), Seq())
     lazy val report = module("report", Seq(ui, memo), Seq())
     lazy val appeal = module("appeal", Seq(memo, ui), Seq())
     lazy val explorer = module("explorer", Seq(game), Seq())
     lazy val notifyModule = module("notify", Seq(memo, ui), Seq())
     lazy val socket = module("socket", Seq(memo), Seq(lettuce))
     lazy val tree = module("tree", Seq(core), Seq(chess.playJson))
     lazy val ui = module("ui", Seq(core, coreI18n), Seq()).enablePlugins(RoutesCompiler).settings(
       Compile / RoutesKeys.generateForwardRouter := false,
       Compile / RoutesKeys.routes / sources ++= {
         val dirs = baseDirectory.value / ".." / ".." / "conf"
         (dirs * "routes").get ++ (dirs * "*.routes").get
       }
     )
     lazy val web = module("web", Seq(ui, memo), playWs.bundle ++ tests.bundle ++ Seq(play.logback, play.server, play.netty))
     lazy val api = module("api", moduleCPDeps, Seq(play.api, play.json, hasher, kamon.core, kamon.influxdb, lettuce) ++ tests.bundle ++ flexmark.bundle).settings(
       Runtime / aggregate := false,
       Test / aggregate := true  // Test <: Runtime
     ) aggregate (moduleRefs: _*)
