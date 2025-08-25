import at.asitplus.modulateddemo.SomeCoseDataStructure
import at.asitplus.modulateddemo.SomeJoseDataStructure
import at.asitplus.modulateddemo.toCose
import at.asitplus.modulateddemo.toJose

fun main(){
    SomeCoseDataStructure("foo").toJose()
    SomeJoseDataStructure("foo").toCose()
}