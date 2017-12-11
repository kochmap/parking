**Parking meter**
- Starting parking meter will create new one if not exists.

**Ticket**
- In order to get ticket fee it must be charged.
- Charging ticket fee again will update to new one.

**Fee**
- Fee is calculated by adding hour rates e.g. 4 hours of parking for regular rate will cost (1 + 2 + 4 + 8) = 15.

**Other**
- App is written in Scala using Play framework, slick for db handling and for testing scala test with scala mock. 
Moreover app use in memory sql database h2 so let's say it is "fully functional".
- Examples of request can be found in directory requests. They are written in Intellij Idea request format.
For api description please refer to api.md
- Services have only integration tests because slick is very hard to mock. 
So I was trying to enclose as little business logic in service layer as I can and move it to domain. 
This design principle was intended by me despite the fact that it was forced by slick.
I think it can be called DDD but I am not sure that I have been fallowing exactly this principle :)