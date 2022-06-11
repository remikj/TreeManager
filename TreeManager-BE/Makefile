build-all: ## Build jar package and test
	./mvnw clean package failsafe:integration-test

build: ## Build jar package
	./mvnw clean package -DskipTests

run: build
	./mvnw spring-boot:run