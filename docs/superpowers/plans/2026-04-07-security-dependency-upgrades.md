# Security Dependency Upgrades Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Upgrade three outdated dependencies with known security vulnerabilities (prettytime, restfb, primefaces) and close nine stale automated PRs.

**Architecture:** Direct `pom.xml` version edits in the affected Maven modules, followed by fixing any breaking API changes in Java source and XHTML views. One commit per dependency so each is independently revertable.

**Tech Stack:** Java 1.8, Maven, Java EE 8, JSF 2.3, PrimeFaces, RestFB, EclipseLink

---

## Files Modified

| File | Why |
|---|---|
| `MeteoCal-web/pom.xml` | Bump prettytime and primefaces versions |
| `MeteoCal-ejb/pom.xml` | Bump restfb version |
| `MeteoCal-ejb/src/main/java/it/polimi/meteocal/ejb/HandleAuthFacebookImpl.java` | Fix restfb 3.x API breaking changes |
| `MeteoCal-web/src/main/java/it/polimi/meteocal/web/CalendarBean.java` | Fix PrimeFaces 12 schedule API changes |
| `MeteoCal-web/src/main/java/it/polimi/meteocal/web/schedule/WeatherScheduleModel.java` | Fix PrimeFaces 12 schedule API changes |
| `MeteoCal-web/src/main/java/it/polimi/meteocal/web/schedule/DefaultWeatherScheduleModel.java` | Fix PrimeFaces 12 schedule API changes |
| `MeteoCal-web/src/main/java/it/polimi/meteocal/web/SettingBean.java` | Verify UploadedFile API still valid |
| `MeteoCal-web/src/main/webapp/templates/commonTemplate.xhtml` | Replace removed p:layout/p:layoutUnit if present |
| `MeteoCal-web/src/main/webapp/calendar/calendar.xhtml` | Replace p:growl → p:messages if needed |
| `MeteoCal-web/src/main/webapp/calendar/account.xhtml` | Replace p:growl → p:messages if needed |
| `MeteoCal-web/src/main/webapp/calendar/settings.xhtml` | Replace p:growl → p:messages if needed |
| `MeteoCal-web/src/main/webapp/index.xhtml` | Replace p:growl → p:messages if needed |

---

## Task 1: Upgrade prettytime

**Files:**
- Modify: `MeteoCal-web/pom.xml`

- [ ] **Step 1: Open `MeteoCal-web/pom.xml` and find the prettytime version**

  Search for `prettytime`. You will find:
  ```xml
  <groupId>org.ocpsoft.prettytime</groupId>
  <artifactId>prettytime-integration-jsf</artifactId>
  <version>5.0.0.Final</version>
  ```

- [ ] **Step 2: Update the version to `5.0.1.Final`**

  Change:
  ```xml
  <version>5.0.0.Final</version>
  ```
  To:
  ```xml
  <version>5.0.1.Final</version>
  ```

- [ ] **Step 3: Commit**

  ```bash
  git add MeteoCal-web/pom.xml
  git commit -m "build(deps): bump prettytime-integration-jsf from 5.0.0.Final to 5.0.1.Final"
  ```

---

## Task 2: Upgrade restfb — pom.xml

**Files:**
- Modify: `MeteoCal-ejb/pom.xml`

- [ ] **Step 1: Open `MeteoCal-ejb/pom.xml` and find the restfb version**

  Search for `restfb`. You will find:
  ```xml
  <groupId>com.restfb</groupId>
  <artifactId>restfb</artifactId>
  <version>2.8.0</version>
  ```

- [ ] **Step 2: Update the version to `3.24.0`**

  Change:
  ```xml
  <version>2.8.0</version>
  ```
  To:
  ```xml
  <version>3.24.0</version>
  ```

  Do NOT commit yet — the API changes in Task 3 must be in the same commit.

---

## Task 3: Fix restfb 3.x API breaking changes in HandleAuthFacebookImpl.java

**Files:**
- Modify: `MeteoCal-ejb/src/main/java/it/polimi/meteocal/ejb/HandleAuthFacebookImpl.java`

**Context:** restfb 3.x changed the `DefaultFacebookClient` constructor and removed some deprecated `User` methods. The file currently uses:
- `new DefaultFacebookClient(accessToken, appSecret, Version.VERSION_2_5)` — constructor signature unchanged in 3.x, this is fine
- `userFB.getBirthdayAsDate()` — **removed in restfb 3.x**, use `userFB.getBirthday()` and parse manually
- `userFB.getTimezone()` — **changed in 3.x**, now returns `Double` instead of `Float`

- [ ] **Step 1: Open the file**

  ```
  MeteoCal-ejb/src/main/java/it/polimi/meteocal/ejb/HandleAuthFacebookImpl.java
  ```

- [ ] **Step 2: Find usages of `getBirthdayAsDate()`**

  Search for `getBirthdayAsDate`. It will look like:
  ```java
  userFB.getBirthdayAsDate()
  ```

  Replace with a manual parse using `getBirthday()` (which returns a `String` like `"MM/DD/YYYY"`):
  ```java
  // restfb 3.x removed getBirthdayAsDate() - parse the birthday string manually
  java.util.Date birthdayDate = null;
  String birthdayStr = userFB.getBirthday();
  if (birthdayStr != null && !birthdayStr.isEmpty()) {
      try {
          birthdayDate = new java.text.SimpleDateFormat("MM/dd/yyyy").parse(birthdayStr);
      } catch (java.text.ParseException e) {
          // leave null if unparseable
      }
  }
  ```

  Wherever `getBirthdayAsDate()` was used, replace with `birthdayDate`.

- [ ] **Step 3: Find usages of `getTimezone()`**

  Search for `getTimezone`. In restfb 3.x this now returns `Double`. If the code assigns to a `float` or `Float`, change the type to `Double` / `double`. Example:

  Before:
  ```java
  float tz = userFB.getTimezone();
  ```
  After:
  ```java
  Double tz = userFB.getTimezone();
  ```

  If `getTimezone()` is only used in a null check or string concatenation, no type change is needed.

- [ ] **Step 4: Verify `Version.VERSION_2_5` still compiles**

  The `com.restfb.Version` enum still has `VERSION_2_5` in 3.x — no change needed. Confirm by grepping:
  ```bash
  grep -r "Version\." MeteoCal-ejb/src/
  ```

- [ ] **Step 5: Commit both pom.xml and the Java fix together**

  ```bash
  git add MeteoCal-ejb/pom.xml MeteoCal-ejb/src/main/java/it/polimi/meteocal/ejb/HandleAuthFacebookImpl.java
  git commit -m "build(deps): bump restfb from 2.8.0 to 3.24.0 and fix API breaking changes"
  ```

---

## Task 4: Upgrade primefaces — pom.xml

**Files:**
- Modify: `MeteoCal-web/pom.xml`

- [ ] **Step 1: Open `MeteoCal-web/pom.xml` and find the primefaces version**

  Search for `primefaces`. You will find:
  ```xml
  <groupId>org.primefaces</groupId>
  <artifactId>primefaces</artifactId>
  <version>8.0</version>
  ```

- [ ] **Step 2: Update the version to `12.0.0`**

  Change:
  ```xml
  <version>8.0</version>
  ```
  To:
  ```xml
  <version>12.0.0</version>
  ```

  Do NOT commit yet — API fixes in Tasks 5 and 6 must be in the same commit.

---

## Task 5: Fix PrimeFaces 12 schedule API breaking changes

**Files:**
- Modify: `MeteoCal-web/src/main/java/it/polimi/meteocal/web/CalendarBean.java`
- Modify: `MeteoCal-web/src/main/java/it/polimi/meteocal/web/schedule/WeatherScheduleModel.java`
- Modify: `MeteoCal-web/src/main/java/it/polimi/meteocal/web/schedule/DefaultWeatherScheduleModel.java`

**Context:** PrimeFaces 11+ changed `DefaultScheduleEvent` to use a builder pattern. Direct constructor instantiation no longer compiles.

Before (PrimeFaces 8):
```java
DefaultScheduleEvent event = new DefaultScheduleEvent("title", startDate, endDate);
```

After (PrimeFaces 11+):
```java
DefaultScheduleEvent<?> event = DefaultScheduleEvent.builder()
    .title("title")
    .startDate(startDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
    .endDate(endDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
    .build();
```

Note: PrimeFaces 11+ also changed schedule date types from `java.util.Date` to `java.time.LocalDateTime`.

- [ ] **Step 1: Open `CalendarBean.java`**

  Find all uses of `DefaultScheduleEvent` constructor and `ScheduleEvent` / `ScheduleEntryMoveEvent` / `ScheduleEntryResizeEvent`. Also find uses of `DefaultWeatherScheduleModel` and how events are added.

- [ ] **Step 2: Update `DefaultScheduleEvent` instantiation in `CalendarBean.java`**

  Replace any constructor call like:
  ```java
  DefaultScheduleEvent<WeatherScheduleEventData> event = new DefaultScheduleEvent<>(title, start, end, data);
  ```
  With the builder:
  ```java
  DefaultScheduleEvent<WeatherScheduleEventData> event = DefaultScheduleEvent.<WeatherScheduleEventData>builder()
      .title(title)
      .startDate(start.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
      .endDate(end.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
      .data(data)
      .build();
  ```

  If `start` / `end` are already `LocalDateTime`, omit the conversion.

- [ ] **Step 3: Update event move/resize handlers in `CalendarBean.java`**

  `ScheduleEntryMoveEvent` and `ScheduleEntryResizeEvent` now return `LocalDateTime` from `getStartDate()` / `getEndDate()`. Update any code that assigns these to `java.util.Date`:

  Before:
  ```java
  Date start = event.getScheduleEvent().getStartDate();
  ```
  After:
  ```java
  java.time.LocalDateTime start = event.getScheduleEvent().getStartDate();
  ```

- [ ] **Step 4: Open `WeatherScheduleModel.java` and `DefaultWeatherScheduleModel.java`**

  Check if they implement `ScheduleModel` or extend `DefaultScheduleModel`. In PrimeFaces 12, `ScheduleModel` is generified as `ScheduleModel<T>`. Update accordingly:

  Before:
  ```java
  public class DefaultWeatherScheduleModel implements ScheduleModel {
  ```
  After:
  ```java
  public class DefaultWeatherScheduleModel implements ScheduleModel<WeatherScheduleEventData> {
  ```

  Update all method signatures in the class that return or accept `ScheduleEvent` to use `ScheduleEvent<WeatherScheduleEventData>`.

- [ ] **Step 5: Check `SettingBean.java` for `UploadedFile`**

  Open `MeteoCal-web/src/main/java/it/polimi/meteocal/web/SettingBean.java`.

  The import `org.primefaces.model.file.UploadedFile` is already the correct package for PrimeFaces 8+. No change needed if the import is already `org.primefaces.model.file.UploadedFile`.

  Verify:
  ```bash
  grep "UploadedFile" MeteoCal-web/src/main/java/it/polimi/meteocal/web/SettingBean.java
  ```
  Expected: `import org.primefaces.model.file.UploadedFile;` — this is already the 8+ package, no change needed.

---

## Task 6: Fix PrimeFaces 12 XHTML component breaking changes

**Files:**
- Modify: `MeteoCal-web/src/main/webapp/calendar/calendar.xhtml`
- Modify: `MeteoCal-web/src/main/webapp/calendar/account.xhtml`
- Modify: `MeteoCal-web/src/main/webapp/calendar/settings.xhtml`
- Modify: `MeteoCal-web/src/main/webapp/index.xhtml`
- Modify: `MeteoCal-web/src/main/webapp/templates/commonTemplate.xhtml`

**Context:**
- `p:growl` was removed in PrimeFaces 12. Replace with `p:messages`.
- `p:layout` / `p:layoutUnit` were removed in PrimeFaces 12. Replace with HTML/CSS layout.
- `p:calendar` was deprecated; in PF 12 use `p:datePicker`. However `p:calendar` may still render — verify at runtime.

- [ ] **Step 1: Find all `p:growl` usages**

  ```bash
  grep -rn "p:growl" MeteoCal-web/src/main/webapp/
  ```

  For each occurrence, replace:
  ```xml
  <p:growl id="growl" showDetail="true"/>
  ```
  With:
  ```xml
  <p:messages id="growl" showDetail="true" closable="true"/>
  ```

  The `id` attribute should remain the same so that existing `update="growl"` AJAX calls keep working.

- [ ] **Step 2: Find all `p:layout` and `p:layoutUnit` usages**

  ```bash
  grep -rn "p:layout\b\|p:layoutUnit" MeteoCal-web/src/main/webapp/
  ```

  If found, replace the layout structure with a CSS-based approach. For example, a typical two-column layout:

  Before:
  ```xml
  <p:layout fullPage="true">
      <p:layoutUnit position="west" size="200">
          <!-- sidebar content -->
      </p:layoutUnit>
      <p:layoutUnit position="center">
          <!-- main content -->
      </p:layoutUnit>
  </p:layout>
  ```

  After (using Bootstrap grid or plain CSS flex, consistent with what the rest of the app already uses):
  ```xml
  <div class="ui-g">
      <div class="ui-g-2">
          <!-- sidebar content -->
      </div>
      <div class="ui-g-10">
          <!-- main content -->
      </div>
  </div>
  ```

  If no `p:layout` is found, skip this step.

- [ ] **Step 3: Verify `p:schedule` attributes**

  In PrimeFaces 12, `p:schedule` dropped the `eventLimit` attribute and renamed some event listener attributes. Check the schedule in `calendar.xhtml`:

  ```bash
  grep -A 10 "p:schedule" MeteoCal-web/src/main/webapp/calendar/calendar.xhtml
  ```

  - If `eventLimit` attribute is present, remove it (PF 12 handles overflow automatically).
  - The `model` and `widgetVar` attributes are unchanged.

- [ ] **Step 4: Commit pom.xml and all XHTML/Java fixes together**

  ```bash
  git add MeteoCal-web/pom.xml \
    MeteoCal-web/src/main/java/it/polimi/meteocal/web/CalendarBean.java \
    MeteoCal-web/src/main/java/it/polimi/meteocal/web/schedule/WeatherScheduleModel.java \
    MeteoCal-web/src/main/java/it/polimi/meteocal/web/schedule/DefaultWeatherScheduleModel.java \
    MeteoCal-web/src/main/webapp/calendar/calendar.xhtml \
    MeteoCal-web/src/main/webapp/calendar/account.xhtml \
    MeteoCal-web/src/main/webapp/calendar/settings.xhtml \
    MeteoCal-web/src/main/webapp/index.xhtml \
    MeteoCal-web/src/main/webapp/templates/commonTemplate.xhtml
  git commit -m "build(deps): bump primefaces from 8.0 to 12.0.0 and fix API breaking changes"
  ```

---

## Task 7: Close stale automated PRs

**No files modified.**

- [ ] **Step 1: Close PR #165 (snyk: google-api-client stale)**

  ```bash
  gh pr close 165 --comment "Closing: the main branch already has google-api-client 2.6.0, which is newer than the 2.4.1 proposed here. This PR is stale."
  ```

- [ ] **Step 2: Close PR #164 (snyk: google-api-client stale)**

  ```bash
  gh pr close 164 --comment "Closing: the main branch already has google-api-client 2.6.0, which is newer than the 2.3.0 proposed here. This PR is stale."
  ```

- [ ] **Step 3: Close PR #161 (snyk: org.json stale)**

  ```bash
  gh pr close 161 --comment "Closing: the main branch already has org.json 20231013, which is newer than 20230227. This PR is stale."
  ```

- [ ] **Step 4: Close PR #160 (dependabot: json stale)**

  ```bash
  gh pr close 160 --comment "Closing: the main branch already has org.json 20231013, which is newer than 20230227. This PR is stale."
  ```

- [ ] **Step 5: Close PR #159 (dependabot: commons-fileupload stale)**

  ```bash
  gh pr close 159 --comment "Closing: the main branch already has commons-fileupload 1.6.0, which is newer than the 1.5 proposed here. This PR is stale."
  ```

- [ ] **Step 6: Close PR #154 (snyk: google-api-client stale)**

  ```bash
  gh pr close 154 --comment "Closing: the main branch already has google-api-client 2.6.0, which is newer than the 1.33.0 proposed here. This PR is stale."
  ```

- [ ] **Step 7: Close PR #137 (snyk: google-api-services-calendar — version downgrade)**

  ```bash
  gh pr close 137 --comment "Closing: this PR proposes v3-rev99-1.19.0 which is older than the current v3-rev20210429-1.32.1. This would be a downgrade. PR is stale."
  ```

- [ ] **Step 8: Close PR #132 (snyk: prettytime — now upgraded via Task 1)**

  ```bash
  gh pr close 132 --comment "Closing: prettytime-integration-jsf has been upgraded to 5.0.1.Final directly on main. This PR is no longer needed."
  ```

- [ ] **Step 9: Close PR #126 (dependabot: eclipse.persistence.jpa — jakarta migration deferred)**

  ```bash
  gh pr close 126 --comment "Closing: the eclipse.persistence.jpa upgrade to 3.x requires a javax→jakarta namespace migration across all modules. This will be handled in a dedicated effort."
  ```
