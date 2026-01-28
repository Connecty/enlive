# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2026-01-28

This is the first release of the community fork of [cgrand/enlive](https://github.com/cgrand/enlive),
which has been unmaintained since 2019.

### Changed

- **BREAKING**: Minimum Clojure version is now 1.10+ (tested with 1.12.0)
- **BREAKING**: Minimum Java version is now 11+ (tested with 11, 17, 21)
- Updated `org.jsoup/jsoup` from 1.7.2 to 1.18.3
- Migrated from Leiningen to tools.deps (deps.edn)
- Replaced Travis CI with GitHub Actions

### Fixed

- Fixed all reflection warnings for modern JVM compatibility (Issue #158, PR #152)
  - `net.cgrand.xml/startparse-sax` - added type hints
  - `net.cgrand.tagsoup/parser` - added type hints for InputSource
  - `net.cgrand.jsoup/parser` - fixed reflection in ->key and Jsoup/parse
- Improved error message when HTML resource not found (Issue #154, PR #155)
  - Now throws `MissingResourceException` with helpful message about classpath resources
- Added missing HTML5 void elements to `self-closing-tags` (Issue #140)
  - Added: `:col`, `:embed`, `:keygen`, `:param`, `:source`, `:track`, `:wbr`
- JSoup parser now supports `java.io.Reader` input (Issue #90)
  - Enables `html-snippet` to work with JSoup parser
- TagSoup parser now supports `java.io.Reader` input

### Added

- `make-input-source` helper function for proper InputSource creation
- GitHub Actions CI workflow with Java 11/17/21 matrix testing
- Reflection warning check in CI

### Removed

- Removed obsolete `.travis.yml`
- Removed obsolete `build.xml` (Ant build)

## Previous Releases

For releases prior to this fork, see the [original project](https://github.com/cgrand/enlive).
