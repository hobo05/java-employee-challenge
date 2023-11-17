# API Exploration

## Fetch all employees

- The response does not match exactly what's in the `README.md` and website. It includes a `message` field

```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "employee_name": "Tiger Nixon",
      "employee_salary": 320800,
      "employee_age": 61,
      "profile_image": ""
    },
    {
      "id": 2,
      "employee_name": "Garrett Winters",
      "employee_salary": 170750,
      "employee_age": 63,
      "profile_image": ""
    }
  ],
  "message": "Successfully! All records has been fetched."
}
```

## Fetch single employee

- When fetching `/employee/1`, I sometimes get a 429 (too many requests) so we should build in fault-tolerance in the service layer (retries)
- Success returns `200`
- Passing in a non-existent id (e.g. `0`) returns a `400` with the following response
  - it should really return `404`
  - The `errors` field is a string instead of an array which is confusing
  - The actual error message is also misleading. The `id` isn't empty it just does not exist

```json
{
  "status": "error",
  "message": "Not found record",
  "code": 400,
  "errors": "id is empty"
}
```

- The response does not match exactly what's in the `README.md` and website. It includes a `message` field

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "employee_name": "Tiger Nixon",
    "employee_salary": 320800,
    "employee_age": 61,
    "profile_image": ""
  },
  "message": "Successfully! Record has been fetched."
}
```

## Create employee

- Success returns `200`
- The response does not match exactly what's in the `README.md` and website. It includes a `message` field

```json
{
  "status": "success",
  "data": {
    "name": "test",
    "salary": "123",
    "age": "23",
    "id": 1510
  },
  "message": "Successfully! Record has been added."
}
```

- There is no validation on this API. Passing in `null` for any of the fields simply results in

```json
{
    "status": "success",
    "data": {
        "name": null,
        "salary": null,
        "age": null,
        "id": 2655
    },
    "message": "Successfully! Record has been added."
}
```

- Although the API allows us to send in strings for `salary` and `age`, our Swagger should limit it to `integer`

- Passing in empty field returns

```json
{
    "status": "success",
    "data": {
        "id": 3734
    },
    "message": "Successfully! Record has been added."
}
```

- Despite this, we should still have validation in our own employee model
- I encountered a situation where I created an employee and attempted to retrieve it, but the `data` was `null`. 
  This should be considered an error case. We wouldn't return `404` since the record was retrieved, but rather a `500` 
  since the underlying service is fundamentally broken. `ERROR` level log should be raised as well for investigation.

```json
{
    "status": "success",
    "data": null,
    "message": "Successfully! Record has been fetched."
}
```

## Delete employee

- Success returns `200`
- The response does not match exactly what's in the `README.md` and website. It includes a `data` field which seems to
  include the string representation of the `id` field

```json
{
    "status": "success",
    "data": "1",
    "message": "Successfully! Record has been deleted"
}
```

- Error response for non-existent id is the same as **Fetch single employee**

## Update employee (Not included in exercise)

- Success returns `200`
- The response does not match exactly what's in the `README.md` and website. It includes a `message` field

```json
{
    "status": "success",
    "data": {
        "name": "foobar",
        "salary": "999",
        "age": "99"
    },
    "message": "Successfully! Record has been updated."
}
```

- Update is just a stub endpoint and doesn't actually update anything. Even a non-existent id works

## Non-standard REST elements

- Inconsistent resource naming. Fetching all employees uses the plural form `/employees`, but the endpoint for fetching a single one is `/employee/{id}`
- Create uses separate endpoint called `/create` rather than simply `/employees` with a `POST`
- Create does not return `Location` header
- Update uses separate endpoint called `/update/{id}` rather than simply `/employees/{id}` with a `PUT`
- Delete uses separate endpoint called `/delete/{id}` rather than simply `/employees/{id}` with a `DELETE`