package downloadImage

fun main(arg: Array<String>) {
    // println(  Downloader().start("https://habr.com/ru/post/116363/").size)
// че то у меня завершилось с ошибкой, а че за ошибка не пишет, хуета какая то
     val a = ParseAndLoad("https://habr.com/ru/post/116363/")
    //a.load()
    // a.download()
    a.loadWebImage()

    while (!a.loadCancel()) {
    }
    println(a.getReport().size)


}
