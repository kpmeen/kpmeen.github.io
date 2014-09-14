---
layout: post
title: "Introducing ClammyScan"
modified:
categories:
excerpt: "Anti-virus scanning of files upload streams in Play framework with ClamAV."
tags: [scala, play, clamav, reactive-mongo, mongodb, anti-virus]
image:
  feature: banner1.jpg
date: 2014-09-13T22:11:04+02:00
---

Recently I had to figure out a solution to ensure files being uploaded were scanned by an AV scanner. After a day or so of research (google), I were not able to find any  existing libraries that would do what I needed with the technology stack I am using (Scala, Play framework and MongoDB with ReactiveMongo, ++).  There are, of course, some Java based implementations for scanning files using the input stream functionality in ClamAV. But they are typically;

* a) Written in Java (and typically for a JEE stack) and very verbose (which would be fair enough if it wasn't for b)
* b) Blocking execution and buffering the file before streaming it to ClamAV

Which lead me to just write my own implementation, hereby known as "ClammyScan". It's a simple library taking advantage of the powerful composability of the BodyParser implementations in Play. At the current time of writing there are 3 different ones. Which are all extensions of the standard multipart form data parser found in Play.

1. The simplest one will just scan a file and return the result.
2. Another one behaves more or less the same way as the default parser, except it will send the stream to a running clamd process in parallel with creating a temporary file.
3. The third is a MongoDB (GridFS) specific body parser that will save the file in MongoDB at the same time as it's scanned by clamd.

In both 1) and 2) above, the file is removed immediately if infected (default), and a suitable response is sent back as a JSON message to the client that was uploading the file.

Anyway, if you should find yourself in need of something similar. You can check out ClammyScan in the [github repository](https://github.com/scalytica/clammyscan). If you have any feature, improvement or other suggestions, please feel free to contribute or create an issue in the github issue tracker.