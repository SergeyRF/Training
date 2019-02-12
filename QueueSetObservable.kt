package downloadImage

class QueueSetObservable<T>{

    private val observers = mutableListOf<ObserverSet<T>>()
     private val valueList = hashSetOf<T>()

    @Synchronized
    fun subscribe(obs:ObserverSet<T>){
        observers.add(obs)
    }

    fun subscribe(callback: (T) -> Unit) { // с такой функцией можно передавать лямбду, избавит от кучи лишнего кода
        // лучше будет перенести ее в extensions
        val observer = object : ObserverSet<T> {
            override fun observerAdd(value: T) {
                callback.invoke(value)
            }
        }
        subscribe(observer)
    }

    @Synchronized
    fun addValue(value:T){

        if (valueList.add(value)) {
            observers.forEach {
                it.observerAdd(value)
            }
        }
    }

    interface ObserverSet<T>{ // не нужно использовать слово set в названии класса если это не коллекция
        fun observerAdd(value:T) // правильнее будет назвать notify
    }

}