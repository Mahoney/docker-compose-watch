fun main(args: Array<String>) {
  val app = build()
  app.execute(args)
}

private fun build() = App(
  DockerComposeExecRunner()
)
