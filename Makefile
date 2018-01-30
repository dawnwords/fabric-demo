up:
	@ docker-compose -f docker-compose.yml up -d
	@ docker logs cli -f

down:
	docker-compose -f docker-compose.yml down

certgen:
	@ docker-compose -f docker-compose-certgen.yml -p certgen up
	@ docker-compose -f docker-compose-certgen.yml -p certgen down
	
del:
	rm -rf config