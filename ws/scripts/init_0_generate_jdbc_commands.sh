#!/bin/bash

# Check required variables are set
if [ -z $DEPLOY_DIR ]; then echo "Variable DEPLOY_DIR is not set."; exit 1; fi
if [ -z $PREBOOT_COMMANDS ]; then echo "Variable PREBOOT_COMMANDS is not set."; exit 1; fi
if [ -z $POSTBOOT_COMMANDS ]; then echo "Variable POSTBOOT_COMMANDS is not set."; exit 1; fi

# Create pre and post boot command files if they don't exist
touch $POSTBOOT_COMMANDS
touch $PREBOOT_COMMANDS


CREATE_POOL_STATEMENT="create-jdbc-connection-pool --datasourceclassname=com.mysql.cj.jdbc.MysqlDataSource \
                                    --restype=javax.sql.DataSource \
                                    --property port=3306:password=docker:user=docker:ServerName=db:DatabaseName=meteocal:useSSL=false:zeroDateTimeBehavior=CONVERT_TO_NULL:useUnicode=true:serverTimezone=UTC:characterEncoding=UTF-8:useInformationSchema=true:nullCatalogMeansCurrent=true:nullNamePatternMatchesAll=false MySQLConnPool"

if grep -q "MySQLConnPool" $POSTBOOT_COMMANDS; then
  echo "post boot commands already have create-jdbc-connection-pool"
else
  echo "$CREATE_POOL_STATEMENT" >> $POSTBOOT_COMMANDS;
fi


CREATE_JDBC_STATEMENT="create-jdbc-resource --connectionpoolid MySQLConnPool jdbc/meteocaldb"

if grep -q "jdbc/meteocaldb" $POSTBOOT_COMMANDS; then
  echo "post boot commands already have create-jdbc-resource"
else
  echo "$CREATE_JDBC_STATEMENT" >> $POSTBOOT_COMMANDS;
fi

AUTH_REALM_STATEMENT="create-auth-realm --classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm \
                          --property jaas-context=jdbcRealm:datasource-jndi=jdbc\\/meteocaldb:user-table=user:user-name-column=EMAIL:password-column=PASSWORD:group-table=user:group-name-column=ROLE:digest-algorithm=none \
                          realmMeteoCal"

if grep -q "jrealmMeteoCal" $POSTBOOT_COMMANDS; then
  echo "post boot commands already have create-auth-realm"
else
  echo "$AUTH_REALM_STATEMENT" >> $POSTBOOT_COMMANDS;
fi