# Jakarta EE 10 / GlassFish 7 / Java 17 Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate MeteoCal from Java EE 8 / Java 1.8 / GlassFish 3.1 to Jakarta EE 10 / Java 17 / GlassFish 7, including EclipseLink 4.0.2.

**Architecture:** Six sequential commits — Java version bump, dependency swap, namespace migration, GlassFish descriptor updates, Arquillian test config, EclipseLink upgrade. Each commit is independently revertable. No application logic changes.

**Tech Stack:** Java 17, Maven, Jakarta EE 10, GlassFish 7, EclipseLink 4.0.2, Arquillian + GlassFish 7 embedded

---

## Files Modified

| File | Task |
|---|---|
| `pom.xml` | Task 1: Java 17 |
| `MeteoCal-ejb/pom.xml` | Tasks 1, 2, 5, 6 |
| `MeteoCal-web/pom.xml` | Tasks 1, 2 |
| `MeteoCal-EJBClient/pom.xml` | Tasks 1, 2 |
| `MeteoCal-ear/pom.xml` | Task 1 |
| All 41 `.java` files in ejb/web/EJBClient src | Task 3 |
| `MeteoCal-ejb/src/main/resources/META-INF/persistence.xml` | Task 3 |
| `MeteoCal-web/src/main/webapp/WEB-INF/glassfish-web.xml` | Task 4 |
| `MeteoCal-ear/src/main/application/META-INF/glassfish-application.xml` | Task 4 |
| `MeteoCal-ejb/src/test/resources/glassfish-resources.xml` | Task 4 |
| `MeteoCal-ejb/src/test/resources/arquillian.xml` | Task 5 |

---

## Task 1: Java 17 compiler bump + remove endorsed API

**Files:**
- Modify: `pom.xml:54`
- Modify: `MeteoCal-ejb/pom.xml:183-187, 205-220`
- Modify: `MeteoCal-web/pom.xml:87-91, 109-124`
- Modify: `MeteoCal-EJBClient/pom.xml:38-42, 59-74`
- Modify: `MeteoCal-ear/pom.xml:28-29`

- [ ] **Step 1: Update `pom.xml` (root) — change source**

  At line 54, change:
  ```xml
  <source>1.8</source>
  ```
  To:
  ```xml
  <source>17</source>
  ```

- [ ] **Step 2: Update `MeteoCal-ejb/pom.xml` — compiler source/target and remove endorsed**

  At lines 183-187, change:
  ```xml
  <source>1.8</source>
  <target>1.8</target>
  <compilerArguments>
      <endorseddirs>${endorsed.dir}</endorseddirs>
  </compilerArguments>
  ```
  To:
  ```xml
  <source>17</source>
  <target>17</target>
  ```

  Also remove the `<endorsed.dir>` property at line 18:
  ```xml
  <endorsed.dir>${project.build.directory}/endorsed</endorsed.dir>
  ```
  Delete that line entirely.

  Also remove the entire endorsed-api copy execution from the `maven-dependency-plugin`. Find the block that looks like:
  ```xml
  <execution>
      <id>copy-endorsed</id>
      ...
      <outputDirectory>${endorsed.dir}</outputDirectory>
      ...
      <artifactId>javaee-endorsed-api</artifactId>
      ...
  </execution>
  ```
  Delete the entire `<execution>...</execution>` block for the endorsed copy. If after removing this execution the `maven-dependency-plugin` has no remaining executions, remove the entire plugin entry too.

- [ ] **Step 3: Update `MeteoCal-web/pom.xml` — compiler source/target and remove endorsed**

  At lines 87-91, change:
  ```xml
  <source>1.8</source>
  <target>1.8</target>
  <compilerArguments>
      <endorseddirs>${endorsed.dir}</endorseddirs>
  </compilerArguments>
  ```
  To:
  ```xml
  <source>17</source>
  <target>17</target>
  ```

  Remove the `<endorsed.dir>` property at line 18.

  Remove the endorsed-api copy execution from `maven-dependency-plugin` (lines ~109-124, the block that copies `javaee-endorsed-api`). If no executions remain in the plugin, remove the plugin entry.

- [ ] **Step 4: Update `MeteoCal-EJBClient/pom.xml` — compiler source/target and remove endorsed**

  At lines 38-42, change:
  ```xml
  <source>1.8</source>
  <target>1.8</target>
  <compilerArguments>
      <endorseddirs>${endorsed.dir}</endorseddirs>
  </compilerArguments>
  ```
  To:
  ```xml
  <source>17</source>
  <target>17</target>
  ```

  Remove the `<endorsed.dir>` property at line 18.

  Remove the endorsed-api copy execution (lines ~64-74).

- [ ] **Step 5: Update `MeteoCal-ear/pom.xml` — compiler source/target**

  At lines 28-29, change:
  ```xml
  <source>1.8</source>
  <target>1.8</target>
  ```
  To:
  ```xml
  <source>17</source>
  <target>17</target>
  ```

- [ ] **Step 6: Commit**

  ```bash
  git add pom.xml MeteoCal-ejb/pom.xml MeteoCal-web/pom.xml MeteoCal-EJBClient/pom.xml MeteoCal-ear/pom.xml
  git commit -m "build: bump Java compiler source/target to 17 and remove endorsed API"
  ```

---

## Task 2: Swap javaee-api → jakarta.jakartaee-api

**Files:**
- Modify: `MeteoCal-ejb/pom.xml:134-140`
- Modify: `MeteoCal-web/pom.xml:72-78`
- Modify: `MeteoCal-EJBClient/pom.xml:23-29`

- [ ] **Step 1: Update `MeteoCal-ejb/pom.xml` — replace javaee-api**

  Find:
  ```xml
  <dependency>
      <groupId>javax</groupId>
      <artifactId>javaee-api</artifactId>
      <version>8.0.1</version>
      <scope>provided</scope>
  </dependency>
  ```
  Replace with:
  ```xml
  <dependency>
      <groupId>jakarta.platform</groupId>
      <artifactId>jakarta.jakartaee-api</artifactId>
      <version>10.0.0</version>
      <scope>provided</scope>
  </dependency>
  ```

- [ ] **Step 2: Update `MeteoCal-web/pom.xml` — replace javaee-web-api**

  Find:
  ```xml
  <dependency>
      <groupId>javax</groupId>
      <artifactId>javaee-web-api</artifactId>
      <version>8.0.1</version>
      <scope>provided</scope>
  </dependency>
  ```
  Replace with:
  ```xml
  <dependency>
      <groupId>jakarta.platform</groupId>
      <artifactId>jakarta.jakartaee-web-api</artifactId>
      <version>10.0.0</version>
      <scope>provided</scope>
  </dependency>
  ```

- [ ] **Step 3: Update `MeteoCal-EJBClient/pom.xml` — replace javaee-api**

  Find:
  ```xml
  <dependency>
      <groupId>javax</groupId>
      <artifactId>javaee-api</artifactId>
      <version>8.0.1</version>
      <scope>provided</scope>
  </dependency>
  ```
  Replace with:
  ```xml
  <dependency>
      <groupId>jakarta.platform</groupId>
      <artifactId>jakarta.jakartaee-api</artifactId>
      <version>10.0.0</version>
      <scope>provided</scope>
  </dependency>
  ```

- [ ] **Step 4: Commit**

  ```bash
  git add MeteoCal-ejb/pom.xml MeteoCal-web/pom.xml MeteoCal-EJBClient/pom.xml
  git commit -m "build(deps): replace javaee-api 8.0.1 with jakarta.jakartaee-api 10.0.0"
  ```

---

## Task 3: Namespace migration — javax.* → jakarta.* in all Java files and persistence.xml

**Files:**
- Modify: All `.java` files in `MeteoCal-ejb/src`, `MeteoCal-web/src`, `MeteoCal-EJBClient/src`
- Modify: `MeteoCal-ejb/src/main/resources/META-INF/persistence.xml`

- [ ] **Step 1: Run automated namespace replacement on all Java source files**

  From the repo root, run (macOS `sed -i ''` syntax):
  ```bash
  find MeteoCal-ejb/src MeteoCal-web/src MeteoCal-EJBClient/src -name "*.java" | xargs sed -i '' \
    -e 's/import javax\.annotation\./import jakarta.annotation./g' \
    -e 's/import javax\.ejb\./import jakarta.ejb./g' \
    -e 's/import javax\.enterprise\./import jakarta.enterprise./g' \
    -e 's/import javax\.faces\./import jakarta.faces./g' \
    -e 's/import javax\.inject\./import jakarta.inject./g' \
    -e 's/import javax\.persistence\./import jakarta.persistence./g' \
    -e 's/import javax\.servlet\./import jakarta.servlet./g' \
    -e 's/import javax\.validation\./import jakarta.validation./g'
  ```

  **Important:** `javax.crypto` must NOT be migrated — it is Java SE, not Jakarta EE. The patterns above are scoped to specific packages so `javax.crypto` is untouched.

- [ ] **Step 2: Verify no javax EE imports remain**

  ```bash
  grep -rn "^import javax\.\(annotation\|ejb\|enterprise\|faces\|inject\|persistence\|servlet\|validation\)\." \
    MeteoCal-ejb/src MeteoCal-web/src MeteoCal-EJBClient/src
  ```
  Expected: no output (empty).

- [ ] **Step 3: Verify javax.crypto is untouched**

  ```bash
  grep -rn "import javax\.crypto\." MeteoCal-ejb/src MeteoCal-web/src MeteoCal-EJBClient/src
  ```
  Expected: lines from `PasswordHash.java` — these should still show `javax.crypto`.

- [ ] **Step 4: Update `persistence.xml` — schema namespace and property key**

  Open `MeteoCal-ejb/src/main/resources/META-INF/persistence.xml`.

  Replace the entire opening `<persistence>` tag. Change from:
  ```xml
  <persistence version="2.1" xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
  ```
  To:
  ```xml
  <persistence version="3.1" xmlns="https://jakarta.ee/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_1.xsd">
  ```

  Also update the property key inside `<persistence-unit>`. Change:
  ```xml
  <property name="javax.persistence.schema-generation.database.action" value="create"/>
  ```
  To:
  ```xml
  <property name="jakarta.persistence.schema-generation.database.action" value="create"/>
  ```

- [ ] **Step 5: Commit**

  ```bash
  git add MeteoCal-ejb/src MeteoCal-web/src MeteoCal-EJBClient/src \
    MeteoCal-ejb/src/main/resources/META-INF/persistence.xml
  git commit -m "refactor: migrate javax.* to jakarta.* namespace for Jakarta EE 10"
  ```

---

## Task 4: Update GlassFish deployment descriptor DTDs

**Files:**
- Modify: `MeteoCal-web/src/main/webapp/WEB-INF/glassfish-web.xml`
- Modify: `MeteoCal-ear/src/main/application/META-INF/glassfish-application.xml`
- Modify: `MeteoCal-ejb/src/test/resources/glassfish-resources.xml`

- [ ] **Step 1: Update `glassfish-web.xml` DOCTYPE**

  Change line 2 from:
  ```xml
  <!DOCTYPE glassfish-web-app PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Servlet 3.0//EN" "http://glassfish.org/dtds/glassfish-web-app_3_0-1.dtd">
  ```
  To:
  ```xml
  <!DOCTYPE glassfish-web-app PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Servlet 3.0//EN" "https://glassfish.org/dtds/glassfish-web-app_3_0-1.dtd">
  ```

  Note: GlassFish 7 is backward compatible with the 3.1 descriptor format. The only change needed is `http://` → `https://` for the DTD URL to avoid connection issues.

- [ ] **Step 2: Update `glassfish-application.xml` DOCTYPE**

  Change line 2 from:
  ```xml
  <!DOCTYPE glassfish-application PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Java EE Application 6.0//EN" "http://glassfish.org/dtds/glassfish-application_6_0-1.dtd">
  ```
  To:
  ```xml
  <!DOCTYPE glassfish-application PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Java EE Application 6.0//EN" "https://glassfish.org/dtds/glassfish-application_6_0-1.dtd">
  ```

- [ ] **Step 3: Update `glassfish-resources.xml` DOCTYPE**

  Change lines 2-4 from:
  ```xml
  <!DOCTYPE resources PUBLIC
      "-//GlassFish.org//DTD GlassFish Application Server 3.1 Resource Definitions//EN"
      "http://glassfish.org/dtds/glassfish-resources_1_5.dtd">
  ```
  To:
  ```xml
  <!DOCTYPE resources PUBLIC
      "-//GlassFish.org//DTD GlassFish Application Server 3.1 Resource Definitions//EN"
      "https://glassfish.org/dtds/glassfish-resources_1_5.dtd">
  ```

- [ ] **Step 4: Commit**

  ```bash
  git add MeteoCal-web/src/main/webapp/WEB-INF/glassfish-web.xml \
    MeteoCal-ear/src/main/application/META-INF/glassfish-application.xml \
    MeteoCal-ejb/src/test/resources/glassfish-resources.xml
  git commit -m "build: update GlassFish deployment descriptor DTD URLs to https"
  ```

---

## Task 5: Upgrade Arquillian + embedded GlassFish to version 7

**Files:**
- Modify: `MeteoCal-ejb/pom.xml:100-115, 144-148`
- Modify: `MeteoCal-ejb/src/test/resources/arquillian.xml`

- [ ] **Step 1: Replace Arquillian GlassFish container adapter in `MeteoCal-ejb/pom.xml`**

  Find:
  ```xml
  <dependency>
      <groupId>org.jboss.arquillian.container</groupId>
      <artifactId>arquillian-glassfish-embedded-3.1</artifactId>
      <version>1.0.2</version>
      <scope>test</scope>
  </dependency>
  ```
  Replace with:
  ```xml
  <dependency>
      <groupId>org.omnifaces.arquillian</groupId>
      <artifactId>arquillian-glassfish-server-embedded</artifactId>
      <version>1.4</version>
      <scope>test</scope>
  </dependency>
  ```

- [ ] **Step 2: Replace embedded GlassFish runtime in `MeteoCal-ejb/pom.xml`**

  Find:
  ```xml
  <dependency>
      <groupId>org.glassfish.main.extras</groupId>
      <artifactId>glassfish-embedded-all</artifactId>
      <version>5.1.0</version>
      <scope>provided</scope>
  </dependency>
  ```
  Replace with:
  ```xml
  <dependency>
      <groupId>org.glassfish.main.extras</groupId>
      <artifactId>glassfish-embedded-all</artifactId>
      <version>7.0.21</version>
      <scope>provided</scope>
  </dependency>
  ```

- [ ] **Step 3: Update Arquillian BOM version in `MeteoCal-ejb/pom.xml`**

  Find the `arquillian-bom` import in `<dependencyManagement>`:
  ```xml
  <groupId>org.jboss.arquillian</groupId>
  <artifactId>arquillian-bom</artifactId>
  ```
  Update its `<version>` to `1.8.0.Final`:
  ```xml
  <groupId>org.jboss.arquillian</groupId>
  <artifactId>arquillian-bom</artifactId>
  <version>1.8.0.Final</version>
  <type>pom</type>
  <scope>import</scope>
  ```

- [ ] **Step 4: Verify `arquillian.xml` container qualifier**

  Read `MeteoCal-ejb/src/test/resources/arquillian.xml`. The container qualifier is currently `glassfish-embedded`. The `org.omnifaces.arquillian:arquillian-glassfish-server-embedded` adapter uses the same qualifier — no change needed.

  Confirm the file still reads:
  ```xml
  <container qualifier="glassfish-embedded" default="true">
      <configuration>
          <property name="resourcesXml">
              src/test/resources/glassfish-resources.xml
          </property>
      </configuration>
  </container>
  ```

  If it already matches, make no changes to this file.

- [ ] **Step 5: Commit**

  ```bash
  git add MeteoCal-ejb/pom.xml
  git commit -m "build(deps): upgrade Arquillian to 1.8.0.Final and GlassFish embedded to 7.0.21"
  ```

---

## Task 6: Upgrade EclipseLink to 4.0.2 + close stale PRs

**Files:**
- Modify: `MeteoCal-ejb/pom.xml:82-84`

- [ ] **Step 1: Update EclipseLink version in `MeteoCal-ejb/pom.xml`**

  Find (around lines 82-84):
  ```xml
  <groupId>org.eclipse.persistence</groupId>
  <artifactId>org.eclipse.persistence.jpa</artifactId>
  <version>2.7.7</version>
  ```
  Change version to `4.0.2`:
  ```xml
  <groupId>org.eclipse.persistence</groupId>
  <artifactId>org.eclipse.persistence.jpa</artifactId>
  <version>4.0.2</version>
  ```

- [ ] **Step 2: Commit**

  ```bash
  git add MeteoCal-ejb/pom.xml
  git commit -m "build(deps): bump EclipseLink from 2.7.7 to 4.0.2 for Jakarta EE 10"
  ```

- [ ] **Step 3: Close stale PR #139 (EclipseLink 3.0.2 — superseded)**

  ```bash
  gh pr close 139 --comment "Closing: EclipseLink has been upgraded directly to 4.0.2 (Jakarta EE 10 aligned) on main. The 3.0.2 proposed here is superseded."
  ```

- [ ] **Step 4: Close stale PR #138 (restfb 3.24.0 — already done)**

  ```bash
  gh pr close 138 --comment "Closing: restfb was already upgraded to 3.24.0 directly on main. This PR is no longer needed."
  ```

- [ ] **Step 5: Close stale PR #157 (primefaces 12.0.0 — already done)**

  ```bash
  gh pr close 157 --comment "Closing: PrimeFaces was already upgraded to 12.0.0 directly on main. This PR is no longer needed."
  ```
