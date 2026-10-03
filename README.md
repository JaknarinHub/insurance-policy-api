# Insurance Policy Management REST API

A beginner-friendly Java backend portfolio project built with Spring Boot, Maven, Spring Data JPA and H2. It manages fictional customers and insurance policies through a REST API.

**This is a personal demo, not a client engagement or a production insurance system.** All names, emails, policies and rules are fictional. No proprietary company code or actuarial logic is used.

## Features

- Customer and policy create, read, update and delete (CRUD).
- One customer can have many policies; each policy belongs to one customer.
- Bean Validation for required fields, email format, positive coverage and amount precision.
- Demo rules: end date must not precede start date; emails and policy numbers are unique.
- Customer deletion is blocked while policies still reference that customer.
- Consistent JSON errors for invalid input, missing resources and conflicts.
- Case-insensitive customer name search and combined customer/status policy filters.
- Paginated lists (default 20, maximum 100) with stable ascending ID order.
- Two fictional customers and policies loaded on startup.
- Unit tests and integration tests against the real HTTP layer and H2 database.
- Postman collection and a plain HTTP request file included.

## Technology

Java 17+, Spring Boot 3.5.16, Maven 3.6.3+, Spring MVC, Jakarta Validation, Spring Data JPA/Hibernate, H2, JUnit 5 and MockMvc. Java 21 is also suitable.

Requirements follow the [official Spring Boot documentation](https://docs.spring.io/spring-boot/3.5/system-requirements.html).

## Quick start

1. Install a JDK 17 or 21 and Apache Maven, or select them in your IDE.
2. Extract this project and open a terminal in the folder containing `pom.xml`.
3. Check your tools:

```sh
java -version
mvn -version
```

Both commands should show the same supported JDK. If Maven shows Java 8, update `JAVA_HOME` to the JDK 17/21 installation and reopen the terminal.

4. Run the tests and start the app:

```sh
mvn clean test
mvn spring-boot:run
```

5. Visit [the customer endpoint](http://localhost:8080/api/customers) or run:

```sh
curl http://localhost:8080/api/customers
```

No database installation, Docker, account, credentials or environment file is needed. The app listens on `127.0.0.1:8080` for local use. Press Ctrl+C to stop it.

Alternatively, package and run:

```sh
mvn clean package
java -jar target/insurance-policy-api-1.0.0.jar
```

### IntelliJ IDEA

Open the project folder as a Maven project. Select JDK 17 or 21 under Project Structure and as the Maven runner JDK, wait for dependencies to download, then run `InsuranceApplication.main`. Tests can be run from `src/test/java` or the Maven `test` lifecycle.

### H2 database console

Open [H2 console](http://localhost:8080/h2-console) while the app is running:

| Setting | Value |
| --- | --- |
| JDBC URL | `jdbc:h2:mem:insurance` |
| Driver | `org.h2.Driver` |
| User | `sa` |
| Password | leave blank |

The database exists only while the app runs. Restarting deletes changes and recreates the demo data. The empty password and console are for local practice only. The app has no authentication or authorization; do not expose it publicly or store real customer information.

To start with an empty database:

```sh
mvn spring-boot:run -Dspring-boot.run.arguments="--app.seed-data=false"
```

## API reference

Base URL: `http://localhost:8080/api`. Requests with a JSON body require `Content-Type: application/json`.

| Method | Endpoint | Result |
| --- | --- | --- |
| GET | `/customers?name=alex&page=0&size=20` | Search/list customers |
| GET | `/customers/{id}` | Get a customer |
| POST | `/customers` | Create, return 201 + Location header |
| PUT | `/customers/{id}` | Replace editable fields, return 200 |
| DELETE | `/customers/{id}` | Delete, return 204; 409 if policies exist |
| GET | `/policies?customerId=1&status=ACTIVE&page=0&size=20` | List/filter policies |
| GET | `/policies/{id}` | Get a policy |
| POST | `/policies` | Create, return 201 + Location header |
| PUT | `/policies/{id}` | Replace editable fields, return 200 |
| DELETE | `/policies/{id}` | Delete, return 204 |

Filters are optional. Customer name search matches a substring regardless of letter case. Both policy filters apply together when provided. A filter without matches returns an empty page. Page numbering starts at 0; `size` must be 1–100. IDs and `customerId` must be positive. Status values are case-sensitive: `ACTIVE`, `CANCELLED`, `EXPIRED`.

PUT requires the complete request body. IDs are assigned by the server. DELETE removes the record permanently from this demo database. There is no automatic status update when dates pass; status is explicitly set by the caller.

### Customer example

```sh
curl -i -X POST http://localhost:8080/api/customers \
  -H 'Content-Type: application/json' \
  -d '{"name":"Taylor Demo","email":"taylor@example.com"}'
```

Example response on a fresh seeded database:

```json
{"id":3,"name":"Taylor Demo","email":"taylor@example.com"}
```

Use the returned ID in later requests. IDs may differ after previous requests.

```sh
curl http://localhost:8080/api/customers/3
curl 'http://localhost:8080/api/customers?name=taylor&page=0&size=5'
curl -i -X PUT http://localhost:8080/api/customers/3 \
  -H 'Content-Type: application/json' \
  -d '{"name":"Taylor Updated","email":"taylor.updated@example.com"}'
```

### Policy example

Use your newly created customer's actual ID:

```sh
curl -i -X POST http://localhost:8080/api/policies \
  -H 'Content-Type: application/json' \
  -d '{"policyNumber":"DEMO-003","customerId":3,"sumAssured":150000.00,"startDate":"2026-01-01","endDate":"2027-01-01","status":"ACTIVE"}'
```

Example response:

```json
{"id":3,"policyNumber":"DEMO-003","customerId":3,"sumAssured":150000.00,"startDate":"2026-01-01","endDate":"2027-01-01","status":"ACTIVE"}
```

```sh
curl http://localhost:8080/api/policies/3
curl 'http://localhost:8080/api/policies?customerId=3&status=ACTIVE'
curl -i -X PUT http://localhost:8080/api/policies/3 \
  -H 'Content-Type: application/json' \
  -d '{"policyNumber":"DEMO-003","customerId":3,"sumAssured":175000.00,"startDate":"2026-01-01","endDate":"2027-01-01","status":"CANCELLED"}'
curl -i -X DELETE http://localhost:8080/api/policies/3
curl -i -X DELETE http://localhost:8080/api/customers/3
```

Delete a customer's policies before deleting the customer.

### List response

```json
{
  "content": [{"id":1,"name":"Alex Demo","email":"alex@example.com"}],
  "page": 0,
  "size": 1,
  "totalElements": 2,
  "totalPages": 2
}
```

### Validation and demo rules

| Field/rule | Requirement |
| --- | --- |
| Customer name | Nonblank, maximum 100 characters; trimmed before storage |
| Customer email | Nonblank valid email, maximum 200 characters; trimmed and lowercased; unique |
| Policy number | 1–40 letters, digits or hyphens; uppercased; unique |
| Customer relationship | Must reference an existing customer |
| Sum assured | Greater than zero, at most 13 integer digits and 2 decimal places |
| Dates | ISO `YYYY-MM-DD`; end date on or after start date |
| Status | Required; ACTIVE, CANCELLED or EXPIRED |

Amounts use `BigDecimal` and a decimal database column. This demo uses a single unspecified currency; it does not calculate premiums, claims, underwriting or currency conversions. Same-day coverage is allowed as a simple demo choice.

### Error example

Posting a policy with `sumAssured: 0` returns 400:

```json
{
  "timestamp": "2026-10-04T00:00:00Z",
  "status": 400,
  "message": "Validation failed",
  "path": "/api/policies",
  "errors": {"sumAssured": "must be greater than 0"}
}
```

The timestamp varies. Cross-field errors and missing resources use the same structure with an empty `errors` object. Common statuses: **400** invalid input, **404** missing customer/policy, **409** duplicates or protected customer deletion. Unexpected server failures use Spring Boot's default error response rather than this custom validation format.

## Try it in Postman

Import `docs/insurance-policy-api.postman_collection.json`. The `baseUrl` variable defaults to `http://localhost:8080`. Run the collection in its listed order: it creates a customer, captures its ID, creates a policy and captures its ID, demonstrates search, update and validation, then deletes both new records. The two seed customers are preserved. The create requests generate unique values for repeat runs. Responses and basic status assertions are included.

You can also open `docs/requests.http` in an IDE with an HTTP client. Set the IDs from create responses manually. The cURL examples above are for macOS/Linux/Git Bash; on Windows use Postman or adapt quoting for PowerShell.

## Project structure and how to read the code

```text
src/main/java/com/example/insurance/
  InsuranceApplication.java       # Startup
  controller/                     # HTTP endpoints for customers and policies
  service/                        # Business rules and transactions
  model/                          # JPA entities and policy status enum
  dto/                            # Request/response data transfer objects
  repository/                     # Spring Data JPA database access
  common/                         # JSON error handling
  config/DemoData.java             # Fictional seed data
src/main/resources/application.properties
src/test/java/com/example/insurance/
  InsuranceApiIntegrationTest.java
  service/PolicyServiceTest.java
docs/                             # Postman, HTTP examples, learning notes
screenshots/                      # Screenshot instructions/placeholders
```

Request flow: **Controller → Service → Repository → H2**. The `model` package maps data to tables; the `dto` package defines HTTP request and response shapes. Controllers handle HTTP and validate request DTOs; services apply demo rules in database transactions; repositories access the database. Both customer and policy responses use DTOs, so JPA entities stay inside the application. The policy response exposes `customerId` instead of serializing the linked customer entity, keeping JSON simple and avoiding lazy-loading issues.

Start reading `controller/CustomerController`, then `service/CustomerService`, then `repository/CustomerRepository`. Next read `service/PolicyService` and the integration tests. See `docs/LEARNING_GUIDE.md` for a short walkthrough and practice exercises.

Database unique constraints and foreign keys also protect records if concurrent requests bypass a service pre-check. No generic framework, Lombok or unnecessary service interfaces are used.

## Tests

```sh
mvn clean test
```

Integration tests exercise HTTP requests, validation, JSON errors, service rules and persistence in H2. Each test clears its own data and seeding is disabled in the test context. Unit tests check date rule boundaries.

Covered flows: customer CRUD/search/email normalization, policy CRUD/combined filters, delete protection, duplicate checks on create/update, missing resources, invalid money and date ranges, required fields, malformed JSON, invalid statuses/IDs and pagination. Test reports are generated under `target/surefire-reports/`.

## Screenshots for your portfolio

Screenshots are intentionally not fabricated. After running the API, use Postman to capture these views, crop them neatly and place the files in `screenshots/`:

| Planned file | Capture |
| --- | --- |
| `create-customer.png` | POST customer request, JSON response and 201 status |
| `create-policy.png` | POST policy request, JSON response and 201 status |
| `filter-policies.png` | Filter by customerId and ACTIVE, with list response |
| `validation-error.png` | Policy with sumAssured = 0, showing 400 and field error |
| `tests-passed.png` | Maven's test summary with successful result |

Once captured, replace this placeholder with real images:

<!-- ![Create customer](screenshots/create-customer.png) -->
<!-- ![Create policy](screenshots/create-policy.png) -->
<!-- ![Filter policies](screenshots/filter-policies.png) -->
<!-- ![Validation error](screenshots/validation-error.png) -->
<!-- ![Tests passed](screenshots/tests-passed.png) -->

## Publish as a portfolio project

1. Run the app and tests locally; practice explaining one create request and one error.
2. Add real screenshots using the checklist above.
3. Create an empty GitHub repository and upload the project files. Exclude `target/` and IDE files.
4. Paste the text in `UPWORK_PORTFOLIO.md` into Upwork and add your GitHub repository link.
5. Describe it honestly as a personal demo. Make sure you understand and can modify the code before offering equivalent work to a client.

## Troubleshooting

- **Java version/build errors:** `mvn -version` must show Java 17 or newer; update `JAVA_HOME` or the Maven runner JDK.
- **Port 8080 occupied:** run `mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"` and change request URLs.
- **Dependency downloads fail:** check your internet connection and Maven proxy configuration; the first build downloads dependencies.
- **H2 console cannot connect:** use the exact JDBC URL above and ensure the app is running.
- **409 duplicate error:** use a different email/policy number or restart the app to reset demo data.
- **404 using an example ID:** use the ID returned from your own create request.

## Scope and possible next steps

This local demo deliberately omits authentication, production database migrations, audit logging, real insurance calculations and deployment configuration. Useful future exercises include PostgreSQL + Flyway migrations, authentication/authorization, OpenAPI documentation and CI. Those should be added before considering a real application.

Licensed under MIT; see `LICENSE`.
