package io.github.leonschreuder.kotlinexec

import java.io.File
import java.io.IOException

/**
 * Class returned once the command is finished. The exit code is always set, stdout and stderr only
 * if capturing was configured
 */
data class ExecResult(val exitCode: Int, val stdout: String? = null, val stderr: String? = null)

/**
 * Run a system level command, mimicking normal shell behaviour as closely as possible. That means
 * the default behaviour is that it will run a blocking command while writing the output to
 * stdout/stderr and the result only contains the exit code of the process. You can of course modify
 * the default behaviour to capture the output, provide stdin, etc.
 *
 * Note that this uses a system independent java ProcessBuilder, so for example unix pipe `|`
 * doesn't work here. If you do want to use a linux shell with shell piping for example, prefix the
 * command with "bash -c"
 */
fun exec(
    cmd: String,
    stdIn: String = "",
    captureOutput: Boolean = false,
    workingDir: File = File("."),
    redirectErrToOut: Boolean = false
): ExecResult {
    try {
        val process =
            ProcessBuilder(*cmd.splitWords().toTypedArray())
                .apply {
                    directory(workingDir)
                    redirectErrorStream(redirectErrToOut)

                    val redirection =
                        if (captureOutput) ProcessBuilder.Redirect.PIPE
                        else ProcessBuilder.Redirect.INHERIT

                    redirectOutput(redirection)
                    redirectError(redirection)
                }
                .start()
        if (stdIn != "") {
            process.outputStream.bufferedWriter().use {
                it.write(stdIn)
                it.flush()
            }
        }

        val exitCode = process.waitFor()
        if (captureOutput) {
            return ExecResult(
                exitCode,
                process.inputStream.bufferedReader().readText(),
                process.errorStream.bufferedReader().readText()
            )
        } else {
            return ExecResult(exitCode)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return ExecResult(1)
}
