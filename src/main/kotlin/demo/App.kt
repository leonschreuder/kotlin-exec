package demo

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

data class ShellResult(val out:String, val exitCode: Int)

/** Run a system-level command.
 * Note: This is a system independent java exec (e.g. | doesn't work). For shell: prefix with "bash -c"
 * Inputting the string in stdIn (if any), and returning stdout and stderr as a string. */
fun shell(cmd: String, stdIn: String = "", captureOutput:Boolean = true, workingDir: File = File(".")): ShellResult {
    try {
        val process = ProcessBuilder(*cmd.split("\\s".toRegex()).toTypedArray())
            .directory(workingDir)
            .redirectOutput(if (captureOutput) ProcessBuilder.Redirect.PIPE else ProcessBuilder.Redirect.INHERIT)
            .redirectError(if (captureOutput) ProcessBuilder.Redirect.PIPE else ProcessBuilder.Redirect.INHERIT)
            .start().apply {
                if (stdIn != "") {
                    outputStream.bufferedWriter().apply {
                        write(stdIn)
                        flush()
                        close()
                    }
                }
                waitFor(60, TimeUnit.SECONDS)
            }
        if (captureOutput) {
            return ShellResult(process.inputStream.bufferedReader().readText(), 0)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return ShellResult("", 1)
}

