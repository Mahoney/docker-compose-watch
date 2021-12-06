class App(
  private val dockerComposeRunner: DockerComposeRunner
) {
  fun execute(args: Array<String>) {
    dockerComposeRunner.execute(args)
  }
}

interface DockerComposeRunner {
  fun execute(args: Array<out String>)
}
