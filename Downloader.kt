package downloadImage

import java.io.File
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern
import javax.imageio.ImageIO

class Downloader() {

    private val imageObserver = QueueObservable<String>()
    private val webObserver = QueueObservable<String>()
    private val checkWebObserver = QueueSetObservable<String>()
    private val checkImageObserver =  QueueObservable<String>()
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

    private fun start(website: String) {
        webName = siteName(website)
        val service = Executors.newFixedThreadPool(4)

        imageObserver.subscribe(object :QueueObservable.QueueObserver<String>{
            override fun observerAdd(value: String) {

                service.submit( {
                    val rep = downloadAndSaveImage(value,"${value.replace("\"","")}image.jpg")
                    if (rep!=null)
                    synchronized(listReport){
                        listReport.add(rep)
                    }
                })
            }
        })

        checkImageObserver.subscribe(object :QueueObservable.QueueObserver<String>{
            override fun observerAdd(value: String) {
                val listImageUri = returnListImage(value)
                listImageUri.forEach {
                    imageObserver.addValue(it)
                }
            }
        })

        checkWebObserver.subscribe(object :QueueSetObservable.ObserverSet<String>{
            override fun observerAdd(value: String) {
                service.submit( {
                   val listUri = returnListUri(value)
                    listUri.forEach{
                        webObserver.addValue(it)
                    }
                })
            }
        })

        webObserver.subscribe(object :QueueObservable.QueueObserver<String>{
            override fun observerAdd(value: String) {
                service.submit({
                    val textUri = downloadUri(value)
                    checkWebObserver.addValue(textUri)
                    checkImageObserver.addValue(textUri)
                })
            }
        })



    }

    private fun downloadUri(uri: String)= URL(uri).readText()



    private fun returnListUri(textUri: String): ArrayList<String> {
        val pattern3 = "(\")(http)([^\\s-]{0,})($webName)([^\\s-]{0,})(\")"
        val pat = Pattern.compile(pattern3)

        val matcher = pat.matcher(textUri)
        val listUri = arrayListOf<String>()
        while (matcher.find()) {
            var a = matcher.group().replace("\"", "").replace(" ", "")
            if (a.indexOf(".jpg") == -1 && a.indexOf(".png") == -1) {

                if (a.indexOf("//") == 0) {
                    a = "https:$a"
                    listUri.add(a)
                }
                println("check web $a")
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
            val width = image.width
            val height = image.height
            val file = File(fileName)
            ImageIO.write(image, "jpg", file)
            val byte = file.length()
            println("download image $imageUri")

            return Report(time, byte, width, height, imageUri, fileName)


        } catch (e: Exception) {
            return null
        }

    }
}