package downloadImage

import java.net.URL
import java.util.regex.Pattern

fun main(arg: Array<String>) {

    val web = "https://habr.com/ru/post/116363/"
    val load = LoaderImage(web)

    load.getLoadCancel().subscribe {
       print(it)
    }
    load.load()

}

fun print(list:List<Report>){
    println("Load Cancel")
    println(list.size)
}