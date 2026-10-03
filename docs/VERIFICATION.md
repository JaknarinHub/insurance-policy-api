# Verification record

Verified on 4 October 2026 (Asia/Bangkok) with Java 21.0.10 and Maven 3.6.3. The build targets Java 17; Java 17 itself was not installed on the verification machine.

- `mvn clean package`: BUILD SUCCESS.
- JUnit/MockMvc: 8 tests, 0 failures, 0 errors, 0 skipped.
- Packaged executable JAR started successfully on localhost port 18080.
- Verified 2 seed customers and 2 seed policies.
- Executed all 12 requests from the included Postman collection through real HTTP using a verification client: all expected status codes passed, including 400 validation, 409 protected deletion, and 204 cleanup.
- Postman GUI import and manual screenshots were not performed. Screenshots remain documented placeholders.

Tests can be reproduced with `mvn clean test`. Reports are generated in `target/surefire-reports/` and are excluded from the source ZIP.
