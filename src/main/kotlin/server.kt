package kweb.template

import kweb.*
import java.io.File
import java.util.concurrent.TimeUnit
import java.io.IOException

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.system.*

import kweb.state.KVar
import kweb.state.render

fun main() {

val counter = KVar("")

    println("Kotlin Start")

    GlobalScope.launch {
	while(true) {
        	delay(1000L)
		val mdate=exec("date", captureOutput=true)
		if(mdate!=null)    counter.value=mdate;
		println("date: "+counter.value)
	}
    }

    Kweb(port = 16097, buildPage = {
        doc.body.new {
            h1().text(counter.map { n -> "Current date: $n!" })
        }
    })
}

/** Run a system-level command.
 * Note: This is a system independent java exec (e.g. | doesn't work). For shell: prefix with "bash -c"
 * Inputting the string in stdIn (if any), and returning stdout and stderr as a string. 
https://stackoverflow.com/questions/35421699/how-to-invoke-external-command-from-within-kotlin-code/59771204#59771204
*/
fun exec(cmd: String, stdIn: String = "", captureOutput:Boolean = false, workingDir: File = File(".")): String? {
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
            return process.inputStream.bufferedReader().readText()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}


