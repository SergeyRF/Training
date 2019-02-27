package downloadImage

import java.net.URL
import java.util.regex.Pattern

fun main(arg: Array<String>) {
    // println(  Downloader().start("https://habr.com/ru/post/116363/").size)
// че то у меня завершилось с ошибкой, а че за ошибка не пишет, хуета какая то
    /*val a = ParseAndLoad("https://habr.com/ru/post/116363/")
   //a.load()
   // a.download()
   a.loadWebImage()

   while (!a.loadCancel()) {
   }
   println(a.getReport().size)
*/
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