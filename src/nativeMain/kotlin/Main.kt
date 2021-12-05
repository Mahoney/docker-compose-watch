import kotlinx.cinterop.CPointer
import kotlinx.cinterop.refTo
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import platform.posix.*

fun main(args: Array<String>) {
    signal(SIGINT, staticCFunction<Int, Unit> {
        println("Interrupt: $it")
        exit(0)
    })

    val stdout = "docker compose ${args.joinToString(" ")}"().capture()

    val sleep = popen("sleep 20 && echo 'done sleeping'", "r") ?: error("Failed to run command: sleep 20")

    println("stdout:")
    println(stdout)
    println("done")
    pclose(sleep)
}

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

class CommandAndPointer(val command: String, val pointer: CPointer<FILE>)

private operator fun String.invoke() = CommandAndPointer(this, popen(this, "r") ?: error("Failed to run command: $this"))
