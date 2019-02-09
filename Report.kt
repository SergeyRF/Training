package downloadImage

import java.text.SimpleDateFormat

class Report(var timeDownload: Long, var byte: Long, var width: Int, var height: Int, var stringUri: String, var fileImage: String) {

    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

    override fun toString(): String {
        return "\n Time download ${sdf.format(timeDownload)} \n byte $byte \n width $width \n height $height \n " +
                "String Uri $stringUri \n fileImage $fileImage \n\n"

    }
}