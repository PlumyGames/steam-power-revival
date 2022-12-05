package steam.world.pressure

interface IPressurizedBlock {
    var pressureCapacity: Pressure
}

interface IPressureProducerBlock : IPressurizedBlock {
    var pressureOutput: Pressure
}

interface IPressureConsumerBlock : IPressurizedBlock {
    var pressureConsumption: Pressure
}