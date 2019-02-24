package downloadImage

import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.regex.Pattern

class LoaderImage(website:String){

    private val downloader = Downloader2()
    private var check = Check(siteName(website))
    private val service = Executors.newFixedThreadPool(4) as ThreadPoolExecutor
    private val report = arrayListOf<Report>()
    private val setImage = HashSet<String>()
    private val queueImage = LinkedList<String>()
    private val setWeb = HashSet<String>()
    private val queueWeb = LinkedList<String>()
    private var loadCancel = false

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