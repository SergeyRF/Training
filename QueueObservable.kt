package downloadImage

class QueueObservable<T>{

    private val observers = mutableListOf<QueueObserver<T>>()
   // private val valueList = mutableListOf<T>()

    fun subscribe(obs:QueueObserver<T>){
        observers.add(obs)
    }

    fun addValue(value:T){
        observers.forEach {
            it.observerAdd(value)
        }
    }

    interface QueueObserver<T>{
        fun observerAdd(value:T)
    }

}