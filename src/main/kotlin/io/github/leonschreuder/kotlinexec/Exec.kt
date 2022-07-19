package io.github.leonschreuder.kotlinexec

import java.io.File
import java.io.IOException

/**
 * Class returned once the command is finished. The exit code is always set, stdout and stderr only
 * if capturing was configured
 */
data class ShellResult(val exitCode: Int, val stdout: String? = null, val stderr: String? = null)

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
): ShellResult {
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
            return ShellResult(
                exitCode,
                process.inputStream.bufferedReader().readText(),
                process.errorStream.bufferedReader().readText()
            )
        } else {
            return ShellResult(exitCode)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return ShellResult(1)
}

/** Tries to split the string into words, but also support grouping commands in quotes */
internal fun String.splitWords(): List<String> {
    val matchList: MutableList<String> = ArrayList()
    val regex = ("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'").toRegex()
    val iterator = regex.findAll(this).iterator()
    while (iterator.hasNext()) {
        val match = iterator.next()
        if (match.groups[1] != null) {
            // Add double-quoted string without the quotes
            matchList.add(match.groupValues[1])
        } else if (match.groups[2] != null) {
            // Add single-quoted string without the quotes
            matchList.add(match.groupValues[2])
        } else {
            // Add unquoted word
            matchList.add(match.groupValues[0])
        }
    }
    return matchList
}
