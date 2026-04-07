# Security Dependency Upgrades Design

**Date:** 2026-04-07  
**Project:** MeteoCal (weather-calendar)  
**Goal:** Fix open security vulnerabilities by upgrading outdated dependencies incrementally, and close stale automated PRs.

---

## Context

The repository has 12 open automated security PRs from Snyk and Dependabot. After comparing proposed versions against the current `pom.xml` files, most PRs are stale — the main branch already has newer versions than what the PRs propose. Three dependencies still require genuine security upgrades, plus one minor patch. The highest-risk upgrade (EclipseLink `javax` → `jakarta` namespace migration) is deferred to its own dedicated effort.

### Current vs. target versions

| Dependency | Module | Current | Target | Risk |
|---|---|---|---|---|
| prettytime-integration-jsf | MeteoCal-web | 5.0.0.Final | 5.0.1.Final | Low — patch bump |
| restfb | MeteoCal-ejb | 2.8.0 | 3.24.0 | Medium — major bump, scan usages |
| primefaces | MeteoCal-web | 8.0 | 12.0.0 | Medium-High — major bump spanning 4 versions |
| eclipse.persistence.jpa | MeteoCal-ejb | 2.7.7 | 3.0.2 | Deferred — requires javax→jakarta migration |

### Stale PRs to close

PRs #165, #164, #161, #160, #159, #154, #137, #132, #126 — already superseded by main branch.

---

## Approach

Direct `pom.xml` edits. One commit per dependency upgrade so each can be reverted independently. No Maven plugin automation — changes are surgical and controlled.

---

## Upgrade Steps (in order)

### Step 1 — prettytime `5.0.0.Final` → `5.0.1.Final`

- **File:** `MeteoCal-web/pom.xml`
- **Risk:** None. Patch bump with no breaking changes.
- **Action:** Update version string, commit.

### Step 2 — restfb `2.8.0` → `3.24.0`

- **File:** `MeteoCal-ejb/pom.xml`
- **Risk:** Major version bump. The restfb API changed significantly between 2.x and 3.x (e.g. `DefaultFacebookClient` constructor, type system, method signatures).
- **Action:**
  1. Scan all usages of `restfb` in `MeteoCal-ejb/src/` to identify affected call sites.
  2. Update version in `pom.xml`.
  3. Fix any breaking changes in the Facebook integration code.
  4. Commit.

### Step 3 — primefaces `8.0` → `12.0.0`

- **File:** `MeteoCal-web/pom.xml`
- **Risk:** Major version bump spanning 4 major versions. PrimeFaces 9–12 introduced component API changes, removed deprecated components, and changed theming.
- **Action:**
  1. Scan all `.xhtml` / `.jsf` view files and managed beans for PrimeFaces component usages.
  2. Update version in `pom.xml`.
  3. Fix any breaking changes in views or backing beans.
  4. Commit.

### Step 4 — Close stale PRs

- Close PRs #165, #164, #161, #160, #159, #154, #137, #132, #126 via `gh pr close` with a comment explaining each was superseded by the main branch.

### Step 5 — EclipseLink jakarta migration (deferred)

- Upgrade `eclipse.persistence.jpa` `2.7.7` → `3.0.2`.
- Requires migrating all `javax.persistence.*` imports to `jakarta.persistence.*` across all modules.
- Scope and plan to be defined separately.

---

## Constraints

- Each upgrade is an independent commit — no batching.
- Do not upgrade eclipse.persistence.jpa in this effort.
- No changes to build tooling, CI/CD, or other dependencies.
