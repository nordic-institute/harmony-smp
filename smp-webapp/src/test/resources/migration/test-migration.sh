#!/bin/sh



SQLFOLDER=../smp-setup/database-scripts
DATABASE=smpmig
DB_USERNAME=smpmig;
DB_PASSWORD=smpmig;


# recreate database
echo "clean database"
mysql -h localhost -u root --password=root -e "drop schema if exists $DATABASE;DROP USER IF EXISTS $DB_USERNAME;  create schema $DATABASE;alter database $DATABASE charset=utf8; create user $DB_USERNAME identified by '$DB_PASSWORD';grant all on $DATABASE.* to $DB_USERNAME;"
echo "create database"

# create old database 
mysql -h localhost -u root --password=root $DATABASE < "$SQLFOLDER/mysql5innodb-4.0.0.ddl"

echo "init data for old database"
mysql -h localhost -u root --password=root $DATABASE < "mysql-init-data-4.0.sql"

echo "database created - run migration"
mysql -h localhost -u root --password=root $DATABASE < "$SQLFOLDER/migration from 4.0.x to 4.1.0/mysql5innoDb_4.0_to_4.1.sql"



