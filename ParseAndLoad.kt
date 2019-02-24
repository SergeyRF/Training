package downloadImage

import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class ParseAndLoad(private val website: String) {

    private val downloader = Downloader2()
    private var check = Check(siteName(website))
    private val service = Executors.newFixedThreadPool(4) as ThreadPoolExecutor
    private val report = arrayListOf<Report>()
    private val setImage = HashSet<String>()
    private val queueImage = LinkedList<String>()
    private val setWeb = HashSet<String>()
    private val queueWeb = LinkedList<String>()
    private var loadCancel = false
    private val imageObservable = QueueSetObservable<ArrayList<String>>()

/*

    fun load() {


        check.getImageObservable().subscribe {
            service.submit {
                downloader.downloadAndSaveImage(it)?.let { addReport(it) }
            }
        }

        check.getWebObservable().subscribe {
            service.submit {
                downloader.downloadUri(it)?.let {
                    check.checkUri(it)
                }
            }
        }

        downloader.downloadUri(website)?.let {
            check.checkUri(it)
        }
    }

    fun download() {

        check.getImageListObservable().subscribe { list ->
            addImage(list)
            loadImage()
        }
        check.getWebObservable().subscribe {
            addWeb(it)
        }

        downloader.downloadUri(website)?.let {
            check.checkUri(it, service)
        }
    }
*/

    fun loadWebImage() {

        imageObservable.subscribe {
            it.forEach { webImage ->
                if (setImage.add(webImage)) {
                    service.submit {
                        downloader.downloadAndSaveImage(webImage)?.let { addReport(it) }
                    }
                }
            }

           /* while (service.queue.size >20) {
            }*/
           loadNextWeb()
            Thread.currentThread().name

        }

        setWeb.add(website)
        queueWeb.add(website)
        loadNextWeb()

    }


    private fun loadNextWeb() {
        val webIter = queueWeb.iterator()
        if (webIter.hasNext()) {
            val text = downloader.downloadUri(webIter.next())
            if (text != null) {
                val checks = check.checkText(text)
                imageObservable.addValue(checks[0])
                checks[1].forEach {
                    addWeb(it)
                }
            }else{
                loadNextWeb()
            }
        } else {
            loadCancel = true
        }
    }

    private fun serviceQueue() {
        if (!service.isTerminating) {
            if (service.queue.size < 10) {
                loadWeb()
            }
            Thread.sleep(100)
            serviceQueue()
        }
    }

    fun stopLoad() {
        service.shutdownNow()
    }

    fun loadCancel() = loadCancel

    @Synchronized
    private fun addImage(webImage: String) {
        if (setImage.add(webImage)) {
            queueImage.add(webImage)
        }
    }


    private fun addImage(webImageList: ArrayList<String>) {
        webImageList.forEach {
            if (setImage.add(it)) {
                synchronized(queueImage) {
                    queueImage.add(it)
                }
            }
        }
    }


    private fun getNextImage() = synchronized(queueImage) { queueImage.pollFirst() }


    private fun queueImageSize() = synchronized(queueImage) { queueImage.size }


    private fun loadImage() {

        val queue = queueImage.iterator()
        queue.forEach {
            service.submit {
                downloader.downloadAndSaveImage(it)?.let { addReport(it) }
            }
        }
        queue.forEach { queueImage.remove(it) }
        /* while (queueImageSize() > 0) {
             service.submit {
                 val imageUri = getNextImage()
                 if (imageUri!=null){
                     val report = downloader.downloadAndSaveImage(imageUri)
                     if (report!=null){
                         addReport(report)
                     }
                 }
             //    downloader.downloadAndSaveImage(getNextImage())?.let { addReport(it) }
             }
         }*/
        if (report.size >= 50) {
            service.shutdown()
        } else {
            loadWeb()
        }
    }

    @Synchronized
    private fun addWeb(webUri: String) {
        if (setWeb.add(webUri)) {
            queueWeb.add(webUri)
        }
    }

    @Synchronized
    private fun getNextWeb() = queueWeb.pollFirst()

    private fun loadWeb() {
        service.submit {
            val text = downloader.downloadUri(getNextWeb())
            if (text == null) {
                loadWeb()
            } else {
                check.checkUri(text)
            }
        }
    }


    @Synchronized
    fun getReport() = report

    @Synchronized
    private fun addReport(report: Report) {
        this.report.add(report)
    }

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

}