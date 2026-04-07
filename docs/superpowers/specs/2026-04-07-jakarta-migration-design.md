# Jakarta EE 10 / GlassFish 7 / Java 17 Migration Design

**Date:** 2026-04-07
**Project:** MeteoCal (weather-calendar)
**Goal:** Migrate from Java EE 8 / GlassFish 3.1 / Java 8 to Jakarta EE 10 / GlassFish 7 / Java 17, including upgrading EclipseLink to 4.0.x.

---

## Context

The project currently targets Java EE 8 (`javax.*` namespace), Java 1.8, and GlassFish 3.1 deployment descriptors (with GlassFish 5.1.0 used for embedded testing). EclipseLink 3.x (Jakarta EE 9+) was deferred from the previous security upgrade effort because it requires a full `javax` → `jakarta` namespace migration. This spec covers that migration in full.

**Scope:**
- 41 Java source files across 3 modules with `javax.*` imports
- 3 `pom.xml` module files + root `pom.xml`
- 3 GlassFish deployment descriptor files
- 1 Arquillian test configuration
- 1 or more `persistence.xml` files

---

## Migration Steps (in order, one commit each)

### Step 1 — Java 17 compiler bump

**Files:** root `pom.xml`, `MeteoCal-ejb/pom.xml`, `MeteoCal-web/pom.xml`, `MeteoCal-EJBClient/pom.xml`, `MeteoCal-ear/pom.xml`

- Change `<maven.compiler.source>` and `<maven.compiler.target>` from `1.8` to `17` in all modules.
- Remove the `maven-dependency-plugin` endorsed-API copy steps (endorsed APIs are obsolete in Java 9+ module system).

### Step 2 — Dependency swap (javaee → jakarta)

**Files:** `MeteoCal-ejb/pom.xml`, `MeteoCal-web/pom.xml`, `MeteoCal-EJBClient/pom.xml`

Replace Java EE 8 APIs with Jakarta EE 10 equivalents (all `provided` scope):

| Old | New |
|---|---|
| `javax:javaee-api:8.0.1` | `jakarta.platform:jakarta.jakartaee-api:10.0.0` |
| `javax:javaee-web-api:8.0.1` | `jakarta.platform:jakarta.jakartaee-web-api:10.0.0` |

### Step 3 — Namespace migration (41 Java source files)

**Files:** All `.java` files in `MeteoCal-ejb/src`, `MeteoCal-web/src`, `MeteoCal-EJBClient/src`, plus any `persistence.xml` files.

Replace `javax.*` → `jakarta.*` for all Jakarta EE packages via automated sed:

| Old package | New package |
|---|---|
| `javax.annotation` | `jakarta.annotation` |
| `javax.ejb` | `jakarta.ejb` |
| `javax.enterprise` | `jakarta.enterprise` |
| `javax.faces` | `jakarta.faces` |
| `javax.inject` | `jakarta.inject` |
| `javax.persistence` | `jakarta.persistence` |
| `javax.servlet` | `jakarta.servlet` |
| `javax.validation` | `jakarta.validation` |

**Exception:** `javax.crypto` is part of Java SE (not Jakarta EE) — must NOT be migrated.

Also update `persistence.xml` schema URLs from `javax.persistence` to `jakarta.persistence` namespace.

### Step 4 — GlassFish deployment descriptor DTDs

**Files:**
- `MeteoCal-web/src/main/webapp/WEB-INF/glassfish-web.xml`
- `MeteoCal-ear/src/main/application/META-INF/glassfish-application.xml`
- `MeteoCal-ejb/src/test/resources/glassfish-resources.xml`

Update DOCTYPE declarations from GlassFish AS 3.1 format to GlassFish 7 / Jakarta EE 10 format:
- Servlet 3.0 → Servlet 6.0 (glassfish-web.xml)
- Java EE Application 6.0 → Jakarta EE Application 10 (glassfish-application.xml)

### Step 5 — Arquillian + embedded GlassFish test config

**Files:** `MeteoCal-ejb/pom.xml`, `MeteoCal-ejb/src/test/resources/arquillian.xml`

Replace embedded GlassFish test dependencies:

| Old | New |
|---|---|
| `org.jboss.arquillian.container:arquillian-glassfish-embedded-3.1:1.0.2` | `org.omnifaces.arquillian:arquillian-glassfish-server-embedded:1.4` |
| `org.glassfish.main.extras:glassfish-embedded-all:5.1.0` | `org.glassfish.main.extras:glassfish-embedded-all:7.0.21` |

Update `arquillian.xml` container qualifier if changed for GlassFish 7.

### Step 6 — EclipseLink 4.0.x upgrade + close stale PRs

**Files:** `MeteoCal-ejb/pom.xml`

- Upgrade `org.eclipse.persistence:org.eclipse.persistence.jpa:2.7.7` → `4.0.2` (Jakarta EE 10 / Jakarta Persistence 3.1 aligned).

Also close two now-superseded PRs:
- PR #139 (Snyk: EclipseLink 2.7.7 → 3.0.2) — superseded by 4.0.2 upgrade
- PR #138 (Snyk: restfb 2.8.0 → 3.24.0) — already done on main

---

## Constraints

- One commit per step — each independently revertable.
- `javax.crypto` must not be migrated (Java SE, not Jakarta EE).
- Do not change application logic, only migration-required changes.
- GlassFish 7 requires Java 11+ — Java 17 satisfies this.
- EclipseLink 4.x requires Jakarta Persistence 3.1 (Jakarta EE 10) — consistent with GlassFish 7.
