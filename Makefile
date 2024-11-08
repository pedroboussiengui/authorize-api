
run:
	@gradle run

test:
	@gradle test

refresh:
	@gradle --refresh-dependencies

mig-create:
	@docker exec -i postgres_produtos_unidades psql -h localhost -U username -d produtos_unidades -f - < ./migrations-create.sql

mig-destroy:
	@docker exec -i postgres_produtos_unidades psql -h localhost -U username -d produtos_unidades -f - < ./migrations-destroy.sql