#!/bin/bash
set -e

# Start domain temporarily to run remote configuration commands
asadmin start-domain

# JDBC connection pool
asadmin create-jdbc-connection-pool \
    --datasourceclassname=com.mysql.cj.jdbc.MysqlDataSource \
    --restype=javax.sql.DataSource \
    --property "port=3306:password=docker:user=docker:ServerName=db:DatabaseName=meteocal:useSSL=false:zeroDateTimeBehavior=CONVERT_TO_NULL:useUnicode=true:serverTimezone=UTC:characterEncoding=UTF-8:useInformationSchema=true:nullCatalogMeansCurrent=true:nullNamePatternMatchesAll=false" \
    MySQLConnPool || true

# JDBC resource
asadmin create-jdbc-resource --connectionpoolid MySQLConnPool jdbc/meteocaldb || true

# Auth realm
asadmin create-auth-realm \
    --classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm \
    --property "jaas-context=jdbcRealm:datasource-jndi=jdbc/meteocaldb:user-table=user:user-name-column=EMAIL:password-column=PASSWORD:group-table=user:group-name-column=ROLE:digest-algorithm=none" \
    realmMeteoCal || true

# Stop domain — startserv will restart it in the foreground
asadmin stop-domain
