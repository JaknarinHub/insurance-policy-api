# Beginner walkthrough

1. Start the app. Call `GET /api/customers` to see the seed data.
2. Create a customer in Postman and note the returned `id`.
3. Create a policy using that customer ID.
4. Repeat with `sumAssured: 0`. Notice the validation error before service logic runs.
5. Use valid money but an end date before the start date. Notice the service's cross-field rule.
6. Delete the customer while the policy still exists. Explain why the API returns 409.
7. Delete the policy and then the customer.
8. Run `mvn test` and read each test's request and expected response.

## What the main annotations do

- `@RestController`: maps methods to JSON HTTP responses.
- `@RequestMapping`, `@GetMapping` etc.: define URL paths and HTTP operations.
- `@Valid`: checks annotated request fields before the controller invokes the service.
- `@Service`: registers business logic with Spring.
- `@Transactional`: groups a service operation into a database transaction.
- `@Entity`: maps a class to a database table.
- `@ManyToOne`: many policies can reference the same customer.
- `JpaRepository`: provides common database operations without hand-written SQL.
- `@RestControllerAdvice`: translates expected exceptions into HTTP error responses.

## Small practice changes

- Add an optional phone field to Customer, then update DTO, entity, API examples and tests.
- Add a policy number search filter and test matching/nonmatching results.
- Add a test that checks updating a policy to a different existing customer.

## Explain it to a potential client

“This is a personal Java backend demo. I used controllers for HTTP, services for validation across fields and database transactions, and repositories for persistence. I tested both successful requests and errors, including preventing deletion of a customer whose policies still exist.”

Only describe skills you can explain and demonstrate yourself.

## Feature folders and Spring Boot MVC layers

`customer/` owns customer endpoints, rules, data model and persistence. `policy/` owns the same parts for policies. Within each feature:

- `controller/`: receives HTTP requests and sends JSON responses.
- `service/`: applies application rules and starts transactions.
- `model/`: JPA entities representing stored data (and policy status).
- `dto/`: validated request bodies and response shapes.
- `repository/`: accesses H2 through Spring Data JPA.

`common/` contains shared error handling and the paginated response DTO. `config/` holds fictional startup data. The policy model references the customer model because each policy belongs to a customer. The customer service checks the policy repository before deleting a customer so it does not leave orphaned policies.

This is a REST API, so JSON responses take the place of server-rendered HTML views. The feature folders let you find everything about a customer or policy in one place.
