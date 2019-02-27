package downloadImage

import org.w3c.dom.Text
import java.lang.Exception
import java.net.URL
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class LoaderImage(private val website: String) {

    private val downloader = Downloader2()
    private var check = Check(URL(website).host)
    private val service = Executors.newFixedThreadPool(4) as ThreadPoolExecutor
    private val report = mutableListOf<Report>()
    private val setImage = HashSet<String>()
    private val queueImage = LinkedList<String>()
    private val setWeb = HashSet<String>()
    private val queueWeb = LinkedList<String>()
    private var loadCancel = QueueObservable<List<Report>>()
    private val imageObservable = QueueSetObservable<List<String>>()


    fun load() {

        imageObservable.subscribe { list ->
            list.forEach {
                service.submit {
                    downloader.downloadAndSaveImage(it)?.let {
                        addReport(it)
                    }
                }

            }
        }
        addWeb(website)
        loadNext()
    }

    fun getLoadCancel() = loadCancel

    private fun loadNext() {
        val nextWeb = queueWeb.pollFirst()
        if (nextWeb == null) {
            service.shutdown()
            service.awaitTermination(1, TimeUnit.DAYS)
            loadCancel.addValue(report)
        }
        val text = downloader.downloadUri(nextWeb)

        if (text == null) {
            if (queueWeb.size == 0) {
                service.shutdown()
                service.awaitTermination(1, TimeUnit.DAYS)
                loadCancel.addValue(report)
            } else {
                loadNext()
            }
        } else {

            val listLinks = check.chekcAll(text)
            imageObservable.addValue(listLinks.imageLinks)
            listLinks.uriLinks.forEach {
                addWeb(it)
            }
            if (report.size > 20) {
                service.shutdown()
                service.awaitTermination(1, TimeUnit.DAYS)
                loadCancel.addValue(report)
            } else {
                loadNext()
            }
        }
    }

    private fun addWeb(web: String) {

        if (setWeb.add(web)) {
            queueWeb.add(web)
        }

    }

    @Synchronized
    private fun addReport(r: Report) {
        report.add(r)
    }


}

data class LinkLists(val imageLinks: List<String>, val uriLinks: List<String>) {}