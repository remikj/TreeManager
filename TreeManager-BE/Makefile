build-with-tests: ## Build jar package and test
	./mvnw clean package failsafe:integration-test

build: ## Build jar package
	./mvnw clean package -DskipTests

build-docker:
	./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=rj/tree-manager-be -DskipTests

run: build
	./mvnw spring-boot:run

build-and-run-docker: build-docker
	docker run -p 8080:8080 rj/tree-manager-be:latest
