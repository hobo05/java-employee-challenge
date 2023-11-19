# Implementation

by: Timothy Cheng tim.cheng09@gmail.com https://www.linkedin.com/in/tcheng09/

## Original README

[Original README](ORIGINAL_README.md)

## Running the solution

**Note:**

1. Run `./gradlew bootRun`
2. Navigate to http://localhost:8080/swagger-ui/index.html to explore the API

### Configuring retries and timeout

- Take a look in `AppConfig` to see the configuration options
- The app has been configured to deal with the tendency for https://dummy.restapiexample.com to return 429s when 
  you hit the API too quickly between requests

## High level approach

1. I did some exploratory testing which I documented in [API Exploration](Notes/API%20Exploration.md)
2. I wrote up a Swagger / OpenAPI spec that was consistent with what I found. It can found here [employee-api.yaml](specs/employee-api.yaml)
3. I manually tested the spec to ensure it was consistent with my notes and the description on the API website 
4. I wrote up a [TODO](Notes/TODO.md) list to plan out the work
5. I upgraded all the Spring dependencies and configured the project for Java 17
6. I generated an [OpenFeign](https://github.com/OpenFeign/feign) client using [openapi-generator](https://github.com/OpenAPITools/openapi-generator)
7. I used [Wiremock](https://wiremock.org/) to mock out the endpoints of the API and wrote tests against them (service and controller)
8. I added Swagger annotations to the implemented API and exposed it using [springdoc](https://springdoc.org/)
9. I wrote an alternative implementation of the controller `IEmployeeController` which uses built-in Spring features 
  that would cut down on boilerplate code. It's called `IEmployeeControllerImproved` and the implementation is called `EmployeeControllerImproved`
   - The `@Component` annotation is commented out, but it can be easily swapped out

## Comments on implementation

- Search
  - Using `String.contains()` with `String.toLowerCase()` is not the optimal way to implement search. Instead we should use
    a DB that is built for full-text search. Some of the features of those DBs would include: ASCII folder, word stemming,
    stop word removal, fuzzy searching using Levenshtein distance, auto-complete using ngrams, etc...
- Performance
  - The current implementation filters the results in-memory, which is not optimal
  - Ideally, we would want the underlying API to support filtering/paging/sorting natively so when we have this 
    higher level API, we would simply be able to pass on the appropriate parameters to achieve that result. 
  - One way to achieve a higher performance, given that the underlying API's rate limiting is very aggressive is to 
    cache the results of the various endpoints. Spring has a `@Cacheable` annotation that supports different cache implementations
    - Caching can lead to other problems as we'd have to make sure that we set the expectation of consumers appropriately and that 
      we evict the cache on write
- Testing
  - Testing has been done against wiremock which gives us full control of the behavior of the stubs, including delays or network failures
  - The `EmployeeServiceTest` tests the service hitting the underlying API and converting the results in local domain objects. Any business
    logic is also contained here. In this case, it's just some in-memory filtering. Known or expected errors are raised as domain-specific 
    exceptions which gives more context to the `ExceptionHandler` later down the line. However, status codes are not determined at this level.
    - no web environment is needed, but we still use `@SpringBootTest` to have access to our beans in our application context
  - The `EmployeeControllerTest` tests that the controller returns the results in the expected format and any input parameter or request
    body are validated. It is also at this level where we test the handling of `Exception`s and assert the appropriate status code and error response
    - a mock web environment is used as this provides the very handy `MockMvc` where we can use Spring's built-in class to hit our endpoints
      without actually running a full server and assert on the response all in a fluent API

## Future considerations

- Security
  - We could implement JWT auth to secure our endpoints
  - We could create enable method security to individually secure our endpoints based on authorities/roles (e.g. only admins can perform write operations)
- Testing
  - Automated E2E tests would ideally be written as well. This would be part of the CI/CD pipeline where the build passes and the web app is
    automatically deployed to a dev or integration environment where a suite of tests is triggered against real endpoints
- Versioning
  - Typically, APIs are reworked over time and sometimes there can be breaking changes that warrant a completely new set of endpoints.
    Adding versions to the API endpoints are a good way to anticipate migrating between two versions of the API
- Metrics
  - Spring has the [Spring Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) project which allows
    provides a default set of metrics, including health checks
  - Since this API relies directly on another API, it may be a good idea to implement a healthcheck that hits the underlying API
  - We could also gather metrics on the performance of the underlying API: average (or 98 percentile) request time, error rate, status code breakdown, etc...
  - In this specific implementation, we could explore using `feign.Capability` to intercept request/responses to add these metrics
- Containerization
  - Spring boot already includes a gradle task called `bootBuildImage` that we could call during our CI/CD pipeline to build, tag, and upload to an image repository