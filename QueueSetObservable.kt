package downloadImage

class QueueSetObservable<T>{

    private val observers = mutableListOf<ObserverSet<T>>()
     private val valueList = hashSetOf<T>()

    @Synchronized
    fun subscribe(obs:ObserverSet<T>){
        observers.add(obs)
    }

    @Synchronized
    fun addValue(value:T){

        if (valueList.add(value)) {
            observers.forEach {
                it.observerAdd(value)
            }
        }
    }

    interface ObserverSet<T>{
        fun observerAdd(value:T)
    }

}