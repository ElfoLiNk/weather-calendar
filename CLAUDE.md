# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MeteoCal is a weather-based social calendar application built as a Jakarta EE enterprise application. It integrates with OpenWeatherMap for weather data and supports OAuth login via Facebook, Google, and Twitter.

## Build Commands

```bash
# Full build (from root)
mvn clean install

# Skip tests (faster)
mvn clean package -DskipTests

# Run unit tests only
mvn test

# Run unit + integration tests
mvn verify

# Run a single test class
mvn test -pl MeteoCal-ejb -Dtest=ClassName

# Start full stack (GlassFish + MySQL)
docker-compose up
```

The deployable artifact is `MeteoCal-ear/target/MeteoCal-ear-*.ear`.

## Module Architecture

Maven multi-module EAR project with four submodules:

```
MeteoCal-EJBClient (jar)  — shared interfaces, DTOs, enums, exceptions
MeteoCal-ejb (ejb)        — stateless EJB implementations, JPA entities, external API clients
MeteoCal-web (war)        — JSF/PrimeFaces managed beans, XHTML views, auth filter
MeteoCal-ear (ear)        — packages ejb + war for GlassFish deployment
```

**Dependency rule:** Both `ejb` and `web` depend only on `EJBClient`. The `web` module never directly references classes from `ejb` — all cross-module calls go through the EJB interfaces defined in `EJBClient`.

## Key Technologies

- **Runtime:** GlassFish 8, Java 21, Jakarta EE 11
- **Web layer:** Jakarta Faces 6 + PrimeFaces 12 (saga theme), CDI beans
- **Business layer:** Stateless EJBs with `@EJB` injection
- **Persistence:** JPA 3.2 / EclipseLink 5, persistence unit `MeteoCalEJB`, datasource `jdbc/meteocaldb` (MySQL 8)
- **External APIs:** OpenWeatherMap (`net.aksingh.owmjapis`), RestFB (Facebook), Twitter4j, Google API Client

## Authentication Flow

`AuthenticationFilter` protects `/user/*` and `/calendar/*`. Social logins land on `/loginFacebook.xhtml`, `/loginGoogle.xhtml`, `/loginTwitter.xhtml` which invoke the corresponding `HandleAuth*` EJB. Credentials are stored in the HTTP session. Password hashing is in `MeteoCal-ejb/.../util/PasswordHash.java`.

## JSF Resource Serving Notes

Static resources (images, CSS) must be referenced with `h:graphicImage` / `h:outputStylesheet` rather than plain HTML tags. A `ResourcePathFilter` servlet filter (`MeteoCal-web`) rewrites resource URLs for GlassFish 8 / Mojarra 4.1 compatibility. The GlassFish descriptor is at `MeteoCal-web/src/main/webapp/WEB-INF/glassfish-web.xml`.

## Persistence Configuration

`MeteoCal-ejb/src/main/resources/META-INF/persistence.xml` — schema auto-created on first deploy (`jakarta.persistence.schema-generation.database.action=create`). Change to `none` after initial setup to avoid data loss on redeploy.

## Infrastructure

`docker-compose.yaml` spins up GlassFish 8 + MySQL 8. The Dockerfile (`Dockerfile.glassfish`) copies the EAR to the autodeploy directory and provisions the JDBC datasource via `asadmin`.
