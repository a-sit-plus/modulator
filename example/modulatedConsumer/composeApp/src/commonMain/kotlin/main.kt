import at.asitplus.modulateddemo.SomeCoseDataStructure
import at.asitplus.modulateddemo.SomeJoseDataStructure
import at.asitplus.modulateddemo.toCose
import at.asitplus.modulateddemo.toJose

fun main(){
    println(SomeCoseDataStructure("foo".encodeToByteArray()).toJose().someJoseSpecificData)
    println(SomeJoseDataStructure("foo").toCose().someCoseSpecificProperty.decodeToString())
}