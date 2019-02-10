package downloadImage

fun main(arg:Array<String>){
    println(  Downloader().start("https://habr.com/ru/post/116363/").size)

}