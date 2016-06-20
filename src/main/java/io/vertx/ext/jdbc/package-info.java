/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * = Vert.x JDBC client
 *
 * This client allows you to interact with any JDBC compliant database using an asynchronous API from your Vert.x
 * application.
 *
 * The client API is represented with the interface {@link io.vertx.ext.jdbc.JDBCClient}.
 *
 * To use this project, add the following dependency to the _dependencies_ section of your build descriptor:
 *
 * * Maven (in your `pom.xml`):
 *
 * [source,xml,subs="+attributes"]
 * ----
 * <dependency>
 *   <groupId>${maven.groupId}</groupId>
 *   <artifactId>${maven.artifactId}</artifactId>
 *   <version>${maven.version}</version>
 * </dependency>
 * ----
 *
 * * Gradle (in your `build.gradle` file):
 *
 * [source,groovy,subs="+attributes"]
 * ----
 * compile '${maven.groupId}:${maven.artifactId}:${maven.version}'
 * ----
 *
 * == Creating a the client
 *
 * There are several ways to create a client. Let's go through them all.
 *
 * === Using default shared data source
 *
 * In most cases you will want to share a data source between different client instances.
 *
 * E.g. you scale your application by deploying multiple instances of your verticle and you want each verticle instance
 * to share the same datasource so you don't end up with multiple pools
 *
 * You do this as follows:
 *
 * [source,java]
 * ----
 * {@link examples.JDBCExamples#exampleCreateDefault}
 * ----
 *
 * The first call to {@link io.vertx.ext.jdbc.JDBCClient#createShared(io.vertx.core.Vertx, io.vertx.core.json.JsonObject)}
 * will actually create the data source, and the specified config will be used.
 *
 * Subsequent calls will return a new client instance that uses the same data source, so the configuration won't be used.
 *
 * === Specifying a data source name
 *
 * You can create a client specifying a data source name as follows
 *
 * [source,java]
 * ----
 * {@link examples.JDBCExamples#exampleCreateDataSourceName}
 * ----
 *
 * If different clients are created using the same Vert.x instance and specifying the same data source name, they will
 * share the same data source.
 *
 * The first call to {@link io.vertx.ext.jdbc.JDBCClient#createShared(io.vertx.core.Vertx, io.vertx.core.json.JsonObject)}
 * will actually create the data source, and the specified config will be used.
 *
 * Subsequent calls will return a new client instance that uses the same data source, so the configuration won't be used.
 *
 * Use this way of creating if you wish different groups of clients to have different data sources, e.g. they're
 * interacting with different databases.
 *
 * === Creating a client with a non shared data source
 *
 * In most cases you will want to share a data source between different client instances.
 * However, it's possible you want to create a client instance that doesn't share its data source with any other client.
 *
 * In that case you can use {@link io.vertx.ext.jdbc.JDBCClient#createNonShared(io.vertx.core.Vertx, io.vertx.core.json.JsonObject)}.
 *
 * [source,java]
 * ----
 * {@link examples.JDBCExamples#exampleCreateNonShared}
 * ----
 *
 * This is equivalent to calling {@link io.vertx.ext.jdbc.JDBCClient#createShared(io.vertx.core.Vertx, io.vertx.core.json.JsonObject, java.lang.String)}
 * with a unique data source name each time.
 *
 * === Specifying a data source
 *
 * If you already have a pre-existing data source, you can also create the client directly specifying that:
 *
 * [source,java]
 * ----
 * {@link examples.JDBCExamples#exampleCreateWithDataSource}
 * ----
 *
 * == Closing the client
 *
 * It's fine to keep hold of the client for a long time (e.g. the lifetime of your verticle), but once you're
 * done with it you should close it.
 *
 * Clients that share a data source with other client instances are reference counted. Once the last one that references
 * the same data source is closed, the data source will be closed.
 *
 * === Automatic clean-up in verticles
 *
 * If you're creating clients from inside verticles, the clients will be automatically closed when the verticle is undeployed.
 *
 * == Getting a connection
 *
 * Once you've created a client you use {@link io.vertx.ext.jdbc.JDBCClient#getConnection(io.vertx.core.Handler)} to get
 * a connection.
 *
 * This will return the connection in the handler when one is ready from the pool.
 *
 * [source,java]
 * ----
 * {@link examples.JDBCExamples#example4}
 * ----
 *
 * The connection is an instance of {@link io.vertx.ext.sql.SQLConnection} which is a common interface not only used by
 * the Vert.x JDBC Client.
 *
 * You can learn how to use it in the http://vertx.io/docs/vertx-sql-common/$lang/[common sql interface] documentation.
 *
 * == Configuration
 *
 * Configuration is passed to the client when creating or deploying it.
 *
 * The following configuration properties generally apply:
 *
 * `provider_class`:: The class name of the class actually used to manage the database connections. By default this is
 * `io.vertx.ext.jdbc.spi.impl.C3P0DataSourceProvider`but if you want to use a different provider you can override
 * this property and provide your implementation.
 *
 * Assuming the C3P0 implementation is being used (the default), the following extra configuration properties apply:
 *
 * `url`:: the JDBC connection URL for the database
 * `driver_class`:: the class of the JDBC driver
 * `user`:: the username for the database
 * `password`:: the password for the database
 * `max_pool_size`:: the maximum number of connections to pool - default is `15`
 * `initial_pool_size`:: the number of connections to initialise the pool with - default is `3`
 * `min_pool_size`:: the minimum number of connections to pool
 * `max_statements`:: the maximum number of prepared statements to cache - default is `0`.
 * `max_statements_per_connection`:: the maximum number of prepared statements to cache per connection - default is `0`.
 * `max_idle_time`:: number of seconds after which an idle connection will be closed - default is `0` (never expire).
 *
 * Other Connection Pool providers are:
 *
 * * BoneCP
 * * Hikari
 *
 * Similar to C3P0 they can be configured by passing the configuration values on the JSON config object. For the special
 * case where you do not want to deploy your app as a fat jar but run with a vert.x distribution, then it is recommented
 * to use BoneCP if you have no write permissions to add the JDBC driver to the vert.x lib directory and are passing it
 * using the `-cp` command line flag.
 *
 * If you want to configure any other C3P0 properties, you can add a file `c3p0.properties` to the classpath.
 *
 * Here's an example of configuring a service:
 *
 * [source,java]
 * ----
 * {@link examples.JDBCExamples#example5}
 * ----
 *
 * Hikari uses a different set of properties:
 *
 * * `jdbcUrl` for the JDBC URL
 * * `driverClassName` for the JDBC driven class name
 * * `maximumPoolSize` for the pool size
 * * `username` for the login (`password` for the password)
 *
 * Refer to the https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby[Hikari documentation]
 * for further details. Also refer to the http://www.jolbox.com/configuration.html[BoneCP documentation]
 * to configure BoneCP.
 *
 * == JDBC Drivers
 *
 * If you are using the default `DataSourceProvider` (relying on c3p0), you would need to copy the JDBC driver class
 * in your _classpath_.
 *
 * If your application is packaged as a _fat jar_, be sure to embed the jdbc driver. If your application is launched
 * with the `vertx` command line, copy the JDBC driver to `${VERTX_HOME}/lib`.
 *
 * The behavior may be different when using a different connection pool.
 *
 *
 * == Use as OSGi bundle
 *
 * Vert.x JDBC client can be used as an OSGi bundle. However notice that you would need to deploy all dependencies
 * first. Some connection pool requires the JDBC driver to be loaded from the classpath, and so cannot be packaged /
 * deployed as bundle.
 */
@Document(fileName = "index.adoc")
@ModuleGen(name = "vertx-jdbc", groupPackage = "io.vertx")
package io.vertx.ext.jdbc;

import io.vertx.codegen.annotations.ModuleGen;
import io.vertx.docgen.Document;
