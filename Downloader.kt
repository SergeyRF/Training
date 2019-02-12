package downloadImage

import java.io.File
import java.net.URL
import java.util.concurrent.*
import java.util.regex.Pattern
import javax.imageio.ImageIO

class Downloader() {

    private val imageObserver = QueueObservable<String>()
    private val webObserver = QueueObservable<String>()
    private val checkWebObserver = QueueSetObservable<String?>() // эти переменные должны называться observable а не observer
    private val checkImageObserver =  QueueObservable<String?>()
    private lateinit var webName: String
    private val listReport = arrayListOf<Report>()

    private fun siteName(uri: String): String {
        val patternName = "//.+?/"
        val pat = Pattern.compile(patternName)
        val matcher = pat.matcher(uri)
        var name = ""
        while (matcher.find()) {
            name = matcher.group().replace("/", "")
        }
        return name
    }

    fun start(website: String): ArrayList<Report> {
        webName = siteName(website)
        val service = Executors.newFixedThreadPool(4)


        imageObserver.subscribe(object : QueueObservable.QueueObserver<String> {
            override fun observerAdd(value: String) {

                service.submit {
                    val rep = downloadAndSaveImage(value,
                            "${value.replace("/", "").replace(".", "")}image.jpg")
                    if (rep != null)
                        synchronized(listReport) {
                            listReport.add(rep)
                        }
                }
            }
        })

        checkImageObserver.subscribe(object : QueueSetObservable.ObserverSet<String?> {
            override fun observerAdd(value: String?) {
                if (value!=null) { // вот эти проверки, это гавнецо, ты же на котлине пишешь, юзай let
                    // или вообще сделай значение не нулейбл, если все равно везде не нул юзаешь, или типа observeSafe запили
                    returnListImage(value).forEach {
                        imageObserver.addValue(it)
                    }
                }
            }
        })

        checkWebObserver.subscribe(object : QueueSetObservable.ObserverSet<String?> {
            override fun observerAdd(value: String?) {
                if (value != null) {
                    service.submit {
                        returnListUri(value).forEach {
                            webObserver.addValue(it)
                        }
                    }
                }
            }
        })

        webObserver.subscribe(object : QueueObservable.QueueObserver<String> {
            override fun observerAdd(value: String) {

                // service.submit{
                val textUri = downloadUri(value)
                checkWebObserver.addValue(textUri)
                checkImageObserver.addValue(textUri)
                println("thread sleep")
                Thread.sleep(100)
                // }
            }
        })
        val textWeb = downloadUri(website)
        checkImageObserver.addValue(textWeb)
        checkWebObserver.addValue(textWeb)

         service.awaitTermination(10,TimeUnit.SECONDS) // тут вообще не понятно че происходит
         service.shutdown()
         println("service Shutdown \n\n\n\n\t")
         service.awaitTermination(100,TimeUnit.SECONDS)
         synchronized(listReport) {
             return listReport
         }


    }

    private fun downloadUri(uri: String): String? {
        return try {
            URL(uri).readText()
        } catch (ex: Exception) {
            println("download Exception ")
            null
        }
    }

        private fun returnListUri(textUri: String): ArrayList<String> {
            val pattern3 = "(\")(http)([^\\s-]{0,})($webName)([^\\s-]{0,})(\")" // можно все такое вынести в переменные класса
            val pat = Pattern.compile(pattern3)

        val matcher = pat.matcher(textUri)
        val listUri = arrayListOf<String>()
        while (matcher.find()) {
            var a = matcher.group().replace("\"", "").replace(" ", "")
            if (a.indexOf(".jpg") == -1 && a.indexOf(".png") == -1) {

                if (a.indexOf("//") == 0) {
                    a = "https:$a"
                }
                listUri.add(a)
                println("any.check web $a")
            }
        }
        return listUri
    }

    private fun returnListImage(textUri: String): ArrayList<String> {

        val pattern2 = "(\")([^\\s-]{0,})((\\.jpg)|(\\.png))"
        val pat = Pattern.compile(pattern2)
        val matcher = pat.matcher(textUri)
        val result = arrayListOf<String>()
        while (matcher.find()) {
            var a = matcher.group().replace("\"", "").replace(" ", "")
            if (a.indexOf("//") == 0) {
                a = "https:$a"
            }
            result.add(a)

        }
        return result
    }

        private fun downloadAndSaveImage(imageUri: String,
                                         fileName: String): Report? {

            try {
                val image = ImageIO.read(URL(imageUri))
                val time = System.currentTimeMillis()
                val file = File(fileName)
                ImageIO.write(image, "jpg", file)

                // не обязательно выносить это все в отдельные переменные
                /*val width = image.width
                val height = image.height

                val byte = file.length()
                println("download image $imageUri")*/

                return Report(time, file.length(), image.width, image.height, imageUri, fileName)


            } catch (e: Exception) {
                // printStackTrace как ты дебажить иначе будешь?
                return null
            }

    }
}
