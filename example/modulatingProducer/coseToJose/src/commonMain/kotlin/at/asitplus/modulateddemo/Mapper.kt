package at.asitplus.modulateddemo

fun SomeJoseDataStructure.toCose() = SomeCoseDataStructure(someJoseSpecificData.encodeToByteArray())
fun SomeCoseDataStructure.toJose() = SomeJoseDataStructure(someCoseSpecificProperty.decodeToString())