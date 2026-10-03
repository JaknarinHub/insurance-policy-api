# Verification record

Verified on 4 October 2026 (Asia/Bangkok) with Java 21.0.10 and Maven 3.6.3. The build targets Java 17; Java 17 itself was not installed on the verification machine.

- `mvn clean package`: BUILD SUCCESS.
- JUnit/MockMvc: 8 tests, 0 failures, 0 errors, 0 skipped.
- Packaged executable JAR started successfully on localhost port 18080.
- Verified 2 seed customers and 2 seed policies.
- Executed all 12 requests from the included Postman collection through real HTTP using a verification client: all expected status codes passed, including 400 validation, 409 protected deletion, and 204 cleanup.
- Postman GUI import and manual screenshots were not performed. Screenshots remain documented placeholders.

Tests can be reproduced with `mvn clean test`. Reports are generated in `target/surefire-reports/` and are excluded from the source ZIP.

## Layered MVC refactor

On 4 October 2026 (Asia/Bangkok), the code was reorganized into `controller`, `service`, `model`, `dto`, and `repository` packages. The customer API now uses a response DTO. `mvn clean test` passed: 8 tests, 0 failures, 0 errors. The endpoint paths and JSON fields were preserved.

## Feature-first package organization

On 4 October 2026 (Asia/Bangkok), the project was grouped into `customer/` and `policy/` features. Each contains its own controller, service, model, DTOs, and repository; shared code remains in `common/`. The integration tests were split into customer and policy folders. HTTP paths and JSON contracts are unchanged. `mvn clean package` passed with 9 tests, 0 failures, and 0 errors. The packaged JAR contains the expected feature packages.

## Layer-first subject subfolders

On 4 October 2026 (Asia/Bangkok), files were reorganized by technical layer first, then by customer or policy. For example, request DTOs now live in `dto/customer/` and `dto/policy/`. Tests mirror their respective controller or service layer. HTTP paths and JSON contracts are unchanged. `mvn clean package` passed with 9 tests, 0 failures, and 0 errors.
