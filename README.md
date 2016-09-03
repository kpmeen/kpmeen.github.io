# Blaargh! - yet another blog template

A [scalajs-react](https://github.com/japgolly/scalajs-react) and [bootstrap v4 (alpha)](http://v4-alpha.getbootstrap.com) based blog template.

## Requirements

* JDK 8
* [SBT](http://www.scala-sbt.org)
* [Ammonite 0.7.6](http://www.lihaoyi.com/Ammonite/)

## Customising

TBD...

* `config/config.json`
* `_project/src`
* ...

## Creating a new post

TBD...

```sh
cd _build
amm publish.scala "Some crafty title"
```

The new markdown formatted file can be found in `_posts`.

## Generate publishable posts and pages

TBD...

```sh
cd _build
amm publish.scala
```

## Build scalajs app

```sh
cd _project
sbt fullOptJS
```



