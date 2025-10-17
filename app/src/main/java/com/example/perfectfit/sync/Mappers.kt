package com.example.perfectfit.sync

import com.example.perfectfit.models.Customer
import com.example.perfectfit.models.Measurement
import com.example.perfectfit.models.Order
import com.example.perfectfit.network.ApiCustomer
import com.example.perfectfit.network.ApiMeasurement
import com.example.perfectfit.network.ApiOrder

/**
 * Extension functions to convert between Room models and API models
 */

// Order conversions
fun Order.toApiModel() = ApiOrder(
    id = serverId,
    localId = id,
    customerId = customerId,
    customerServerId = null, // Can be enhanced to include customer's serverId
    customerName = customerName,
    orderDate = orderDate,
    orderType = orderType,
    estimatedDeliveryDate = estimatedDeliveryDate,
    instructions = instructions,
    amount = amount,
    status = status,
    lastModified = lastModified
)

fun ApiOrder.toRoomModel() = Order(
    id = localId ?: 0,
    customerId = customerId,
    customerName = customerName,
    orderDate = orderDate,
    orderType = orderType,
    estimatedDeliveryDate = estimatedDeliveryDate,
    instructions = instructions,
    amount = amount,
    status = status,
    serverId = id,
    lastModified = lastModified,
    syncStatus = Order.SYNC_SYNCED
)

// Customer conversions
fun Customer.toApiModel() = ApiCustomer(
    id = serverId,
    localId = id,
    firstName = firstName,
    lastName = lastName,
    address = address,
    mobile = mobile,
    alternateMobile = alternateMobile,
    birthDate = birthDate,
    lastModified = lastModified
)

fun ApiCustomer.toRoomModel() = Customer(
    id = localId ?: 0,
    firstName = firstName,
    lastName = lastName,
    address = address,
    mobile = mobile,
    alternateMobile = alternateMobile,
    birthDate = birthDate,
    serverId = id,
    lastModified = lastModified,
    syncStatus = Customer.SYNC_SYNCED
)

// Measurement conversions
fun Measurement.toApiModel() = ApiMeasurement(
    id = serverId,
    localId = id,
    customerId = customerId,
    customerServerId = null,
    
    // Kurti Measurements
    kurtiLength = kurtiLength,
    fullShoulder = fullShoulder,
    upperChestRound = upperChestRound,
    chestRound = chestRound,
    waistRound = waistRound,
    shoulderToApex = shoulderToApex,
    apexToApex = apexToApex,
    shoulderToLowChestLength = shoulderToLowChestLength,
    skapLength = skapLength,
    skapLengthRound = skapLengthRound,
    hipRound = hipRound,
    frontNeckDeep = frontNeckDeep,
    frontNeckWidth = frontNeckWidth,
    backNeckDeep = backNeckDeep,
    readyShoulder = readyShoulder,
    sleevesHeightShort = sleevesHeightShort,
    sleevesHeightElbow = sleevesHeightElbow,
    sleevesHeightThreeQuarter = sleevesHeightThreeQuarter,
    sleevesRound = sleevesRound,
    
    // Pant Measurements
    pantWaist = pantWaist,
    pantLength = pantLength,
    pantHip = pantHip,
    pantBottom = pantBottom,
    
    // Blouse Measurements
    blouseLength = blouseLength,
    blouseFullShoulder = blouseFullShoulder,
    blouseChest = blouseChest,
    blouseWaist = blouseWaist,
    blouseShoulderToApex = blouseShoulderToApex,
    blouseApexToApex = blouseApexToApex,
    blouseBackLength = blouseBackLength,
    blouseFrontNeckDeep = blouseFrontNeckDeep,
    blouseFrontNeckWidth = blouseFrontNeckWidth,
    blouseBackNeckDeep = blouseBackNeckDeep,
    blouseReadyShoulder = blouseReadyShoulder,
    blouseSleevesHeightShort = blouseSleevesHeightShort,
    blouseSleevesHeightElbow = blouseSleevesHeightElbow,
    blouseSleevesHeightThreeQuarter = blouseSleevesHeightThreeQuarter,
    blouseSleevesRound = blouseSleevesRound,
    blouseHookOn = blouseHookOn,
    
    lastModified = lastModified
)

fun ApiMeasurement.toRoomModel() = Measurement(
    id = localId ?: 0,
    customerId = customerId,
    
    // Kurti Measurements
    kurtiLength = kurtiLength,
    fullShoulder = fullShoulder,
    upperChestRound = upperChestRound,
    chestRound = chestRound,
    waistRound = waistRound,
    shoulderToApex = shoulderToApex,
    apexToApex = apexToApex,
    shoulderToLowChestLength = shoulderToLowChestLength,
    skapLength = skapLength,
    skapLengthRound = skapLengthRound,
    hipRound = hipRound,
    frontNeckDeep = frontNeckDeep,
    frontNeckWidth = frontNeckWidth,
    backNeckDeep = backNeckDeep,
    readyShoulder = readyShoulder,
    sleevesHeightShort = sleevesHeightShort,
    sleevesHeightElbow = sleevesHeightElbow,
    sleevesHeightThreeQuarter = sleevesHeightThreeQuarter,
    sleevesRound = sleevesRound,
    
    // Pant Measurements
    pantWaist = pantWaist,
    pantLength = pantLength,
    pantHip = pantHip,
    pantBottom = pantBottom,
    
    // Blouse Measurements
    blouseLength = blouseLength,
    blouseFullShoulder = blouseFullShoulder,
    blouseChest = blouseChest,
    blouseWaist = blouseWaist,
    blouseShoulderToApex = blouseShoulderToApex,
    blouseApexToApex = blouseApexToApex,
    blouseBackLength = blouseBackLength,
    blouseFrontNeckDeep = blouseFrontNeckDeep,
    blouseFrontNeckWidth = blouseFrontNeckWidth,
    blouseBackNeckDeep = blouseBackNeckDeep,
    blouseReadyShoulder = blouseReadyShoulder,
    blouseSleevesHeightShort = blouseSleevesHeightShort,
    blouseSleevesHeightElbow = blouseSleevesHeightElbow,
    blouseSleevesHeightThreeQuarter = blouseSleevesHeightThreeQuarter,
    blouseSleevesRound = blouseSleevesRound,
    blouseHookOn = blouseHookOn,
    
    serverId = id,
    lastModified = lastModified,
    syncStatus = Measurement.SYNC_SYNCED
)

