# TODO

- [x] Play around with  API using Postman and make notes
- [x] Create a Swagger spec around notes
- [x] Generate a client using the Swagger spec
- [x] Develop `EmployeeService` using Wiremock tests and generated client
  - [x] Make sure all error cases have appropriate exceptions
  - [x] Add fault tolerance, retry cases, timeouts
  - [x] Add logging where appropriate
- [x] Develop `EmployeeController` using already created Wiremock stubs
  - [x] All exceptions should be captured by an `@ExceptionHandler`
  - [x] Uncaught exceptions should not leak internal info
  - [x] Add Swagger annotations
- [x] Add `IEmployeeControllerImproved` and implement with the TODOs addressed
- [x] Hit endpoints using Postman
- [x] Add notes on caching (or maybe add a separate implementation) and make notes and add caveats regarding caching
- [x] Add notes on true automated E2E tests and also smoketests
- [x] Add notes on versioning APIs
