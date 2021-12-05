import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import platform.posix.errno
import platform.posix.execvp
import platform.posix.sleep
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker

class App(
  private val dockerComposeRunner: DockerComposeRunner
) {
  fun execute(args: Array<String>) {

    val w = Worker.start()
    val result = w.execute(TransferMode.SAFE, { }) {
      var count = 0
      while (count < 3) {
        println("hi $count")
        sleep(2)
        count++
      }
    }
    dockerComposeRunner.execute(args)
    result.consume {  }
  }
}

interface DockerComposeRunner {
  fun execute(args: Array<out String>)
}

class DockerComposeShellRunner : DockerComposeRunner {
  override fun execute(args: Array<out String>) {
    println("In execute")
    memScoped {
      println("In memScoped")
      val items = ((listOf("docker", "compose") + args) + listOf<String?>(null)).map { it?.cstr?.ptr }
      println("turned args to pointers $items")
      val result = execvp("docker", items.toCValues().ptr)
      if (result != 0) {
        error("failed to run docker $errno")
      }
    }
  }
}
