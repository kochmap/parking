- App is written in Scala using Play framework and for testing scala test with scala mock. 
Moreover app use in memory sql database h2 so let's say it is "fully functional".
- Examples of request can be found in directory requests. They are written in Intellij Idea request format.
For api description please refer to api.md
- Starting parking meter will create new one if not exists.
- Fee is calculated by adding hour rates e.g. 4 hours of parking for regular rate will cost (1 + 2 + 4 + 8) = 15.
- In order to get ticket fee it must be charged.