# Blaargh! - yet another blog template

A [scalajs-react](https://github.com/japgolly/scalajs-react) and [bootstrap v4](http://v4-alpha.getbootstrap.com) based blog template.

## Requirements

* JDK 8
* [SBT](http://www.scala-sbt.org)
* [Ammonite 0.8.5](http://www.lihaoyi.com/Ammonite/)

## Customising

TBD...

* `config/config.json`
* `_project/src`
* ...

## Creating a new post

#### Using the CLI

Create a new post by executing the `Publish.sc` script in the `_build` directory:

```sh
cd _build
amm Publish.sc "Some crafty title"
```

#### Using a simple GUI

By executing the `NewArticle.sc` script, you will be presented with a simple Swing dialogue. Here you can enter relevant metadata and attach an image if you wish. The image will be copied into the `_posts` directory together with the generated markdown file.

```sh
cd _build
amm NewArticle.sc
```

## Generate / render publishable posts and pages

When you're done editing your markdown formatted post, it needs to be converted to an HTML file to be visible on your site. To convert your posts to HTML run the `Publish.sc` script without any arguments.

```sh
cd _build
amm Publish.sc
```

The generated HTML files can be found in the `posts` directory.

## Build scalajs app

```sh
cd _project
sbt fullOptJS
```



