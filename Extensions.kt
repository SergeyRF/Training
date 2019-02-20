package downloadImage

 fun <T> QueueSetObservable<T>.subscribe(callback: (T) -> Unit) {
     val observer = object : QueueSetObservable.ObserverSet<T> {
         override fun notify(value: T) {
             callback.invoke(value)
         }
     }
     subscribe(observer)
 }

fun <T> QueueObservable<T>.subscribe(callback: (T) -> Unit) {
    val observer = object : QueueObservable.QueueObserver<T> {
        override fun notify(value: T) {
            callback.invoke(value)
        }
    }
    subscribe(observer)
}