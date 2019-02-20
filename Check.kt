package downloadImage

import any.a
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.regex.Pattern

class Check(webName: String) {

    private val pattern2 = "(\")([^\\s-]{0,})((\\.jpg)|(\\.png))"
    private val pattern3 = "(\")(http)([^\\s-]{0,})($webName)([^\\s-]{0,})(\")"
    private val imageObservable = QueueSetObservable<String>()
    private val webObservable = QueueSetObservable<String>()

    private val imageListObservable = QueueObservable<ArrayList<String>>()
    private val webListObservable = QueueObservable<ArrayList<String>>()


    @Synchronized
    fun checkUri(textUri: String) {
        checkListImage(textUri)
          /*      .forEach {
            imageObservable.addValue(it)
        }*/
        checkListUri(textUri).forEach {

            webObservable.addValue(it)

        }
    }

    @Synchronized
    fun checkUri(text:String,servise:ExecutorService){
        servise.submit {
            checkListImage(text)
        }
        servise.submit{
            checkListUri(text)
        }
    }
    fun getImageObservable() = imageObservable
    fun getWebObservable() = webObservable

    fun getImageListObservable() = imageListObservable
    fun getWebListObservable() = webListObservable


    private fun checkListUri(textUri: String): ArrayList<String> {

        val pat = Pattern.compile(pattern3)
        val matcher = pat.matcher(textUri)
        val arrayWeb = arrayListOf<String>()
        while (matcher.find()) {
            var a = matcher.group().replace("\"", "").replace(" ", "")
            if (a.indexOf(".jpg") == -1 && a.indexOf(".png") == -1) {
                if (a.indexOf("//") == 0) {
                    a = "https:$a"
                }
                //webObservable.addValue(a)
                arrayWeb.add(a)
                println("any.check web $a")
            }
        }
        webListObservable.addValue(arrayWeb)
        return arrayWeb
    }


    private fun checkListImage(textUri: String): ArrayList<String> {

        val pat = Pattern.compile(pattern2)
        val matcher = pat.matcher(textUri)
        val arrayImage = arrayListOf<String>()
        while (matcher.find()) {
            var a = matcher.group().replace("\"", "").replace(" ", "")
            if (a.indexOf("//") == 0) {
                a = "https:$a"
            }
            //imageObservable.addValue(a)
            arrayImage.add(a)
            println("any.check image $a")
        }
        imageListObservable.addValue(arrayImage)
        return arrayImage
    }

}