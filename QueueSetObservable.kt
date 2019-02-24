package downloadImage

class QueueSetObservable<T>{

    private val observers = mutableListOf<ObserverSet<T>>()
     private val valueList = hashSetOf<T>()


    fun subscribe(obs:ObserverSet<T>){
        observers.add(obs)
    }


    @Synchronized
    fun addValue(value:T){
        if (valueList.add(value)) {
            observers.forEach {
                it.notify(value)
            }
        }
        Thread.currentThread().name
    }

    interface ObserverSet<T>{ // не нужно использовать слово set в названии класса если это не коллекция
        fun notify(value:T) // правильнее будет назвать notify
    }

}