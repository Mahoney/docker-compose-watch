import kotlinx.cinterop.CPointer
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.FILE
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

operator fun String.invoke() = CommandAndPointer(
  command = this,
  pointer = popen(this, "r") ?: error("Failed to run command: $this")
)

class CommandAndPointer(
  val command: String,
  val pointer: CPointer<FILE>
)

private fun CommandAndPointer.capture(): String {
  val stdout = buildString {
    val buffer = ByteArray(4096)
    while (true) {
      val input = fgets(buffer.refTo(0), buffer.size, this@capture.pointer) ?: break
      append(input.toKString())
    }
  }

  val status = pclose(pointer)
  if (status != 0) {
    error("Command `$command` failed with status $status")
  }
  return stdout
}
