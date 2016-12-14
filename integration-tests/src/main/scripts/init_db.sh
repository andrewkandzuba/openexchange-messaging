#!/bin/bash

mysqladmin -uroot -p$1 create messaging
mysqladmin -uroot -p$1 create treasure
mysql -uroot -p$1 < /maven/docker-entrypoint-initdb.d/init_db.sql
