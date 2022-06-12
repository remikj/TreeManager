build-be:
	cd ./TreeManager-BE && make build-docker

build-fe:
	cd ./TreeManager-FE && make build-docker

build-docker: build-be build-fe

run:
	docker-compose up -d

build-and-run: build-docker run

stop:
	docker-compose down