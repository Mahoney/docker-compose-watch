import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import platform.posix.errno
import platform.posix.execvp

class DockerComposeExecRunner : DockerComposeRunner {
  override fun execute(args: Array<out String>) {
    memScoped {
      val items = ((listOf("docker", "compose") + args) + listOf<String?>(null)).map { it?.cstr?.ptr }
      val result = execvp("docker", items.toCValues().ptr)
      if (result != 0) {
        error("failed to run docker. errno: $errno")
      }
    }
  }
}
