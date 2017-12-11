Default location: localhost:9000

**API:**


**Start of parking meter**
----

```http request
POST /parking-meters/:domainId/start 
```

**Path parameters:**

Required:

**:domainId** - id of parking meter. Can be alphanumeric.

**Body**
```json
{
    "licensePlateNumber": "abcde"
}
```

**response examples:**
```
200 OK

{"id":2,"parkingMeterId":1,"vehicleLicensePlateNumber":"abcde","startTimestamp":"2017-12-11T08:58:55.710Z"}
```
```
403 Forbidden
{"message":"Can't start parking meter because it has been started before"}
```
```
404 Not Found
```

**Stop of parking meter**
----

```http request
GET /parking-meters/:domainId/stop 
```

**Path parameters:**

Required:

**:domainId** - id of parking meter. Can be alphanumeric.

**response examples:**
```
200 OK


{"id":2,"parkingMeterId":1,"vehicleLicensePlateNumber":"abcde","startTimestamp":"2017-12-11T08:58:55.710Z","stopTimestamp":"2017-12-11T10:27:12.597Z"}
```
```
403 Forbidden
{"message":"Can't stop parking meter because it has been stopped before"}
```
```
404 Not Found
```

**Has vehicle had active parking meter**
----

```http request
GET /vehicles/:vehicleLicensePlateId/has-active-parking-meter
```

**Path parameters:**

Required:

**:vehicleLicensePlateId** - vehicle license plate id. Can be alphanumeric.


**response examples:**
```
200 OK


true
```

**Charge a ticket fee**
----

```http request
/parking-tickets/:ticketId/charge-fee
```

**Path parameters:**

Required:

**:ticketId** - id of ticket. Only numeric it is db PK.

**Body**
```json
{
    "tariff": "REGULAR_TARIFF", 
    "currency": "PLN"
}
```

**response examples:**
```
200 OK

{"amount":1,"currency":"PLN"}
```
```
403 Forbidden
{"message":"Can't charge ticket fee because ticket hasn't been stopped yet"}
```
```
404 Not Found
```

**Get ticket fee**
----

```http request
GET /parking-tickets/:ticketId/fee
```

**Path parameters:**

Required:

**:ticketId** - id of ticket. Only numeric it is db PK.

**response examples:**
```
200 OK

{"amount":1,"currency":"PLN"}
```
```
404 Not Found
```
**Get fees from given day**
----

```http request
GET /fees?date=:date
```

**Query parameters:**

Required:

**:date** - date parsed by java LocalDate

**response examples:**
```
200 OK

[5.3,"PLN"]
```
```
404 Not Found
```


