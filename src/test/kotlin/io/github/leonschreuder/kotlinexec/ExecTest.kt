package io.github.leonschreuder.kotlinexec

import java.io.*
import java.nio.file.Files
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ExecTest {

    lateinit var tmpDir: File

    @BeforeTest
    fun setup() {
        tmpDir = Files.createTempDirectory("tmp-kotlin-shell-").toFile()
    }

    @Test
    fun should_execute_with_output_to_stdout() {
        File(tmpDir, "test.txt").apply { writeText("") }

        val result = exec("ls", workingDir = tmpDir)

        // the way the ProcessBuilder is implemented prevents us from capturing what is being
        // printed to the outputs using System.setOut() etc. I did not find a way around this
        // so the only thing to do is test this manually and simply see that the output is not
        // captured here.
        assertEquals(0, result.exitCode)
        assertEquals(null, result.stdout)
    }

    @Test
    fun should_capture_output() {
        File(tmpDir, "test.txt").apply { writeText("") }

        val result = exec("ls", captureOutput = true, workingDir = tmpDir).stdout

        assertEquals("test.txt\n", result)
    }

    @Test
    fun should_capture_individual_streams() {
        File(tmpDir, "test.sh").apply {
            writeText(
                """
            #!/bin/bash
            echo "stdout"
            echo "stderr" >&2
        """.trimIndent()
            )
            setExecutable(true)
        }

        val result = exec("./test.sh", captureOutput = true, workingDir = tmpDir)

        assertEquals("stdout\n", result.stdout)
        assertEquals("stderr\n", result.stderr)
    }

    @Test
    fun should_capture_joined_streams() {
        File(tmpDir, "test.sh").apply {
            writeText(
                """
            #!/bin/bash
            echo "stdout"
            echo "stderr" >&2
        """.trimIndent()
            )
            setExecutable(true)
        }

        val result =
            exec("./test.sh", workingDir = tmpDir, captureOutput = true, redirectErrToOut = true)

        assertEquals("stdout\nstderr\n", result.stdout)
        assertEquals("", result.stderr)
    }

    @Test
    fun should_support_stdin() {

        val result = exec("cat -", workingDir = tmpDir, captureOutput = true, stdIn = "input")

        assertEquals("input", result.stdout)
        assertEquals("", result.stderr)
    }

    @Test
    fun should_support_quoting() {

        val result =
            exec(
                "echo 'something here'",
                workingDir = tmpDir,
                captureOutput = true,
                stdIn = "input"
            )

        assertEquals("something here\n", result.stdout)
        assertEquals("", result.stderr)
    }
}
