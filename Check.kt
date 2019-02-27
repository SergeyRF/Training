package downloadImage

import java.util.*
import java.util.concurrent.ExecutorService
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class Check(webName: String) {

    private val pattern2 = "(\")([^\\s-]{0,})((\\.jpg)|(\\.png))"
    private val pattern3 = "(\")(http)([^\\s-]{0,})($webName)([^\\s-]{0,})(\")"
    private val patUri = Pattern.compile(pattern3)
    private val patImage =Pattern.compile(pattern2)





    fun chekcAll(textUri: String) = LinkLists(checkListImage(textUri),checkListUri(textUri))

    private fun checkListUri(textUri: String): ArrayList<String> {

        val matcher = patUri.matcher(textUri)
        val arrayWeb = arrayListOf<String>()
        while (matcher.find()) {
            var a = matcher.group().replace("\"", "").replace(" ", "")
            if (a.indexOf(".jpg") == -1 && a.indexOf(".png") == -1) {
                if (a.indexOf("//") == 0) {
                    a = "https:$a"
                }
                arrayWeb.add(a)
            }
        }
        println("Check web size ${arrayWeb.size}")
        return arrayWeb
    }


    private fun checkListImage(textUri: String): ArrayList<String> {

        val matcher = patImage.matcher(textUri)
        val arrayImage = arrayListOf<String>()
        while (matcher.find()) {
            var a = matcher.group().replace("\"", "").replace(" ", "")
            if (a.indexOf("//") == 0) {
                a = "https:$a"
            }
            arrayImage.add(a)
        }
        println("check image size ${arrayImage.size}")
        return arrayImage
    }

}