---
out: Credits.html
---

Credits
-------

sbt was originally created by Mark Harrah ([@harrah][@harrah]) in [2008](https://www.scala-lang.org/old/node/392.html). Most of the fundamental aspects of sbt, such as the Scala incremental compiler, integration with Maven and Ivy dependencies, and parallel task processing were conceived and initially implemented by Mark.

By 2010, when sbt 0.7 came out, many open-source Scala projects were using sbt as their build tool.

Mark joined Typesafe (now Lightbend) in 2011, the year the company was founded.  sbt 0.10.0 shipped that same year. Mark remained the maintainer and most active contributor until March 2014, with sbt 0.13.1 as his last release.

Josh Suereth ([@jsuereth][@jsuereth]) at Typesafe became the next maintainer of sbt.

In 2014, Eugene Yokota ([@eed3si9n][@eed3si9n]) joined Typesafe to co-lead sbt with Josh. This team carried the 0.13 series through 0.13.5 and started the trajectory to 1.0 as [technology previews][sbt-0.13-Tech-Previews]. By the time of Josh's departure in 2015, after sbt 0.13.9, they had shipped `AutoPlugin`, kept sbt 0.13 in shape, and laid groundwork for sbt server.

Grzegorz Kossakowski ([@gkossakowski][@gkossakowski]) worked on a better incremental compiler algorithm called "name hashing" during his time on the Scala team at Typesafe. Name hashing became the default incremental compiler in sbt 0.13.6 (2014). Lightbend later commissioned Grzegorz to refine name hashing using a technique called class-based name hashing, which was adopted by Zinc 1. Another notable contribution from Grzegorz was hosting a series of [meetups](http://blog.japila.pl/2014/07/gkossakowski-on-warszawscala-about-how-to-patch-scalasbt/) with @WarszawScaLa, and (with his arm in a sling!) guiding the Warszaw Scala community to [fix](http://blog.japila.pl/2014/07/hacking-scalasbt-with-gkossakowski-on-warszawscala-meetup-in-javeo_eu/) the infamous blank-line problem.

In May 2015, Dale Wijnand ([@dwijnand][@dwijnand]) became a committer from the community after contributing features such as `inThisBuild` and `-=`.

From June 2015 to early 2016, Martin Duhem ([@Duhemm][@Duhemm]) joined Typesafe as an intern, working on sbt. During this time, Martin worked on crucial components such as making the compiler bridge configurable for Zinc, and code generation for pseudo case classes (which later became Contraband).

Around this time, Eugene, Martin, and Dale started the sbt 1.x codebase, splitting the code base into multiple modules: sbt/sbt, Zinc 1, sbt/librarymanagement, sbt/util, and sbt/io. The aim was to make Zinc 1, an incremental compiler usable by all build tools.

In August 2016, Dale joined the Tooling team at Lightbend. Dale and Eugene oversaw the releases 0.13.12 through 0.13.16, as well as the development of sbt 1.0.

In spring 2017, the Scala Center participated in the Zinc 1 development effort. Jorge Vicente Cantero ([@jvican][@jvican]) has contributed a number of improvements including the fix for the "as seen from" bug that had blocked Zinc 1.

From spring 2018, Ethan Atkins joined the sbt project as a community member, and quickly became the leading contributor to the project. Initially his contribution was implementing Close Watch that uses native code to provide watch service on macOS. He's worked on various performance related improvements since then including layered ClassLoader, logging rewrite, and native thin client that uses GraalVM native image.

According to `git shortlog -sn --no-merges` on [sbt/sbt](https://github.com/sbt/sbt/graphs/contributors), [sbt/zinc](https://github.com/sbt/zinc/graphs/contributors), [sbt/librarymanagement](https://github.com/sbt/librarymanagement/graphs/contributors), [sbt/util](https://github.com/sbt/util/graphs/contributors), [sbt/io](https://github.com/sbt/io/graphs/contributors), [sbt/contraband](https://github.com/sbt/contraband/graphs/contributors), and [sbt/website](https://github.com/sbt/website/graphs/contributors) there were 9151 non-merge commits by 318 contributors.

- Mark Harrah 3852
- Eugene Yokota (eed3si9n)  1760
- Dale Wijnand  524
- Josh Suereth  357
- Grzegorz Kossakowski  349
- Martin Duhem  333
- Jorge Vicente Cantero (jvican)  314
- Eugene Vigdorchik 108
- Kenji Yoshida (xuwei-k) 96
- Indrajit Raychaudhuri 90
- Dan Sanduleac 74
- Benjy Weinberger  52
- Max Peng  52
- Jacek Laskowski 40
- Jason Zaugg 40
- Josh Soref  39
- Krzysztof Romanowski  39
- Pierre DAL-PRA  36
- Andrzej Jozwik  33
- Antonio Cunei 30
- Aaron S. Hawley 29
- Guillaume Martres 25
- James Roper 24
- Chua Chee Seng (cheeseng) 24
- Paolo G. Giarrusso  23
- Matej Urbas 22
- Stu Hood  22
- Adriaan Moors 18
- Jean-Rémi Desjardins  16
- Sanjin Sehic  16
- Fedor Korotkov  14
- Andrew Johnson  13
- David Perez 13
- Havoc Pennington  13
- Liang Tang  12
- Peter Vlugter 12
- Taro L. Saito 10
- Paul Phillips 9
- Roberto Tyley 9
- Vojin Jovanovic 9
- William Benton  9
- 杨博 (Yang Bo)  9
- Brian Topping 8
- Bruno Bieth 8
- Johannes Rudolph  8
- KAWACHI Takashi 8
- Ken Kaizu (krrrr38) 8
- Artyom Olshevskiy 7
- Eugene Platonov 7
- Matthew Farwell 7
- Michael Allman  7
- David Pratt 6
- Luca Milanesio  6
- Nepomuk Seiler  6
- Peiyu Wang  6
- Simeon H.K. Fitch 6
- Stephen Samuel  6
- Thierry Treyer  6
- James Earl Douglas  5
- Jean-Remi Desjardins  5
- Miles Sabin 5
- Seth Tisue  5
- qgd 5
- Anthony Whitford  4
- Bardur Arantsson  4
- Ches Martin 4
- Chris Birchall  4
- Daniel C. Sobral  4
- Heikki Vesalainen 4
- Krzysztof Nirski  4
- Lloyd Meta  4
- Michael Schmitz 4
- Orr Sella 4
- Philipp Dörfler 4
- Tim Harper  4
- Vasya Novikov 4
- Vincent Munier  4
- Jürgen Keck (j-keck)  4
- Richard Summerhayes (rasummer)  4
- Adam Warski 3
- Ben McCann  3
- Enno Runne  3
- Eric Bowman 3
- Henrik Engstrom 3
- Ian Forsey  3
- James Ward  3
- Jesse Kinkead 3
- Justin Pihony 3
- Kazuhiro Sera 3
- Krzysztof Borowski  3
- Lars Hupel  3
- Leif Wickland 3
- Lukas Rytz  3
- Max Worgan  3
- Oliver Wickham  3
- Olli Helenius 3
- Roman Timushev  3
- Simon Schäfer 3
- ZhiFeng Hu  3
- daniel-shuy 3
- Roland Schatz 3
- soc 3
- wpitula 3
- Alex Dupre  2
- Alexey Alekhin  2
- Allan Erskine 2
- Alois Cochard 2
- Andreas Flierl  2
- Anthony 2
- Antoine Gourlay 2
- Arnout Engelen  2
- Ben Hutchison 2
- Benjamin Darfler  2
- Brendan W. McAdams  2
- Brennan Saeta 2
- Brian McKenna 2
- Brian Smith 2
- BrianLondon 2
- Charles Feduke  2
- Christian Dedie 2
- Cody Allen  2
- Damien Lecan  2
- David Barri 2
- David Harcombe  2
- David Hotham  2
- Derek Wickern 2
- Eric D. Reichert  2
- Eric J. Christeson  2
- Evgeny Goldin 2
- Evgeny Vereshchagin 2
- Francois Armand (fanf42)  2
- Fred Dubois 2
- Heejong Lee 2
- Henri Kerola  2
- Hideki Ikio 2
- Ikenna Nwaiwu 2
- Ismael Juma 2
- Jakob Odersky 2
- Jan Berkel  2
- Jan Niehusmann  2
- Jarek Sacha 2
- Jens Halm 2
- Joachim Hofer 2
- Joe Barnes  2
- Johan Andrén  2
- Jonas Fonseca 2
- Josh Kalderimis 2
- Juan Manuel Caicedo Carvajal  2
- Justin Kaeser 2
- Konrad Malawski 2
- Lex Spoon 2
- Li Haoyi  2
- Lloyd 2
- Lukasz Piepiora 2
- Marcus Lönnberg 2
- Marko Elezovic  2
- Michael Parrott 2
- Mikael Vallerie 2
- Myyk Seok 2
- Ngoc Dao  2
- Nicolas Rémond  2
- Oscar Vargas Torres 2
- Paul Draper 2
- Paulo "JCranky" Siqueira  2
- Petro Verkhogliad 2
- Piotr Kukielka  2
- Robin Green 2
- Roch Delsalle 2
- Roman Iakovlev  2
- Scott Royston 2
- Simon Hafner  2
- Sukant Hajra  2
- Suzanne Hamilton  2
- Tejas Mandke  2
- Thomas Koch 2
- Thomas Lockney  2
- Tobias Neef 2
- Tomasz Bartczak 2
- Travis  2
- Vitalii Voloshyn  2
- Wei Chen  2
- Wojciech Langiewicz 2
- Xin Ren 2
- Zava  2
- amishak 2
- beolnix 2
- ddworak 2
- drdamour  2
- Eric K Richardson (ekrich)  2
- fsi206914 2
- henry 2
- kaatzee 2
- kalmanb 2
- nau 2
- qvaughan  2
- sam 2
- softprops 2
- tbje  2
- timt  2
- Aaron D. Valade 1
- Alexander Buchholtz 1
- Alexandr Nikitin  1
- Alexandre Archambault 1
- Alexey Levan  1
- Anatoly Fayngelerin 1
- Andrea  1
- Andrew D Bate 1
- Andrew Miller 1
- Ashley Mercer 1
- Bruce Mitchener 1
- Cause Cheng 1
- Cause Chung 1
- Christian Krause  1
- Christophe Vidal  1
- Claudio Bley  1
- Daniel Peebles  1
- Denis T 1
- Devis Lucato  1
- Dmitry Melnichenko  1
- EECOLOR 1
- Edward Samson 1
- Erik Bakker 1
- Erik Bruchez  1
- Ethan 1
- Federico Ragona 1
- Felix Leipold 1
- Geoffroy Couprie  1
- Gerolf Seitz  1
- Gilad Hoch  1
- Gregor Heine  1
- HairyFotr 1
- Heiko Seeberger 1
- Holden Karau  1
- Hussachai Puripunpinyo  1
- Jacques 1
- Jakob Grunig  1
- James Koch  1
- Jan Polák 1
- Jan Ziniewicz 1
- Jisoo Park  1
- Joonas Javanainen 1
- Joscha Feth 1
- Josef Vlach 1
- Joseph Earl 1
- João Costa  1
- Justin Ko 1
- Kamil Kloch 1
- Kazuyoshi Kato  1
- Kevin Scaldeferri 1
- Knut Petter Meen  1
- Krzysztof 1
- Kunihiko Ito  1
- LMnet 1
- Luc Bourlier  1
- Lucas Mogari  1
- Lutz Huehnken 1
- Mal Graty 1
- Marcos Savoury  1
- Marek Żebrowski 1
- Markus Siemens  1
- Martynas Mickevicius  1
- Martynas Mickevičius  1
- Michael Bayne 1
- Michael Ledin 1
- Nathan Hamblen  1
- Nyavro  1
- OlegYch 1
- Olivier ROLAND  1
- Pavel Penkov  1
- Pedro Larroy  1
- Peter Pan 1
- Piotr Kukiełka  1
- Rikard Pavelic  1
- Robert Jacob  1
- Rogach  1
- Sergey Andreev  1
- Shanbin Wang  1
- Shane Hender  1
- Simon Olofsson  1
- Stefan Zeiger 1
- Stephen Duncan Jr 1
- Steve Gury  1
- Sören Brunk 1
- Thomas Grainger 1
- Tim Sheppard  1
- Todor Todorov 1
- Toshiyuki Takahashi 1
- Travis Brown  1
- Tsubasa Irisawa 1
- Victor Hiairrassary 1
- Yasuo Nakanishi 1
- Yoshitaka Fujii 1
- adinath 1
- albuch  1
- cchantep  1
- cdietze 1
- choucri 1
- hokada  1
- joiskov 1
- jozic 1
- jyane 1
- k.bigwheel  1
- kavedaa 1
- mmcbride  1
- pishen tsai 1
- sanjiv sahayam  1
- saturday06  1
- seroperson  1
- slideon 1
- thricejamie 1
- todesking 1
- totem3  1
- upescatore  1
- valydia 1
- walidbenchikha  1
- Wiesław Popielarski 1
- Łukasz Indykiewicz  1

For the details on individual contributions, see [Changes][Changes].

The following people contributed ideas, documentation, or code to sbt but are not listed above:

- Josh Cough
- Nolan Darilek
- Viktor Klang
- David R. MacIver
- Ross McDonald
- Andrew O'Malley
- Jorge Ortiz
- Mikko Peltonen
- Ray Racine
- Stuart Roebuck
- Harshad RJ
- Tony Sloane
- Francisco Treacy
- Vesa Vilhonen

The sbt ecosystem would not be the same without so many awesome plugins. Here are some of the plugins and their contributors:

- [Play Framework](https://playframework.com/) by Lightbend (James Roper, Peter Hausel, and many others)
- [Scala.js](https://www.scala-js.org/) by Sébastien Doeraene, Tobias Schlatter, et al
- [sbt-assembly](https://github.com/sbt/sbt-assembly) by Eugene Yokota (eed3si9n)
- [coursier](https://github.com/coursier/coursier) by Alexandre Archambault
- [sbt Native Packager](https://sbt-native-packager.readthedocs.io/en/stable/) by Nepomuk Seiler (muuki88) and Josh Suereth
- [sbt-dependency-graph](https://github.com/jrudolph/sbt-dependency-graph) by Johannes Rudolph
- [WartRemover](https://www.wartremover.org/) by Claire Neveu and Brian McKenna
- [sbt-android](https://github.com/scala-android/sbt-android) by Perry (pfn)
- [sbt-revolver](https://github.com/spray/sbt-revolver) by Johannes Rudolph and Mathias (sirthias)
- [sbt-docker](https://github.com/marcuslonnberg/sbt-docker) by Marcus Lönnberg
- [tut](https://github.com/tpolecat/tut) by Rob Norris (tpolecat)
- [sbt-release](https://github.com/sbt/sbt-release) by Gerolf Seitz
- [sbt-jmh](https://github.com/ktoso/sbt-jmh) by Konrad Malawski (ktoso)
- [sbt-updates](https://github.com/rtimush/sbt-updates) by Roman Timushev
- [xsbt-web-plugin](https://github.com/earldouglas/xsbt-web-plugin) by James Earl Douglas and Artyom Olshevskiy
- [sbt-scoverage](https://github.com/scoverage/sbt-scoverage) by Stephen Samuel and Mikko Koponen
- [sbt-web](https://github.com/sbt/sbt-web) by Lightbend (Christopher Hunt, Peter Vlugter, et al)
- [sbt-buildinfo](https://github.com/sbt/sbt-buildinfo) by Eugene Yokota (eed3si9n)
- [sbt-pack](https://github.com/xerial/sbt-pack) by Taro L. Saito (xerial)
- [sbt-onejar](https://github.com/sbt/sbt-onejar) by Jason Zaugg (retronym)
- [sbt-git](https://github.com/sbt/sbt-git) by Josh Suereth
- [sbt-scalariform](https://github.com/sbt/sbt-scalariform) by Heiko Seeberger, Daniel Trinh, et al
- [ensime-sbt](http://ensime.org/build_tools/sbt/) by Sam Halliday (fommil)
- [sbt-fresh](https://github.com/sbt/sbt-fresh) by Heiko Seeberger
- [sbt-web-scalajs](https://github.com/vmunier/sbt-web-scalajs) by Vincent Munier
- [sbt-sonatype](https://github.com/xerial/sbt-sonatype) by Taro L. Saito (xerial)
- [sbt-sublime](https://github.com/orrsella/sbt-sublime) by Orr Sella
- [sbt-errors-summary](https://github.com/Duhemm/sbt-errors-summary) by Martin Duhem
- [sbt-bintray](https://github.com/sbt/sbt-bintray) by Doug Tangren (softprops)
- [Migration Manager](https://github.com/lightbend/migration-manager/wiki) by Lightbend (Mirco Dotta, Seth Tisue, et al)
- [sbt-protobuf](https://github.com/sbt/sbt-protobuf) by Gerolf Seitz and Kenji Yoshida (xuwei-k)
- [sbt-site](https://github.com/sbt/sbt-site) by Jonas Fonseca, Josh Suereth, et al
- [sbt-doctest](https://github.com/tkawachi/sbt-doctest) by KAWACHI Takashi
- [sbt-robovm](https://github.com/roboscala/sbt-robovm) by Jan Polák
- [scalastyle-sbt-plugin](https://github.com/scalastyle/scalastyle-sbt-plugin) by Matthew Farwell
- [sbt-microsites](https://github.com/47deg/sbt-microsites) by 47 Degrees (Juan Pedro Moreno, Javier de Silóniz Sandino, et al)
- [sbt-header](https://github.com/sbt/sbt-header) by Heiko Seeberger and Benedikt Ritter
- [sbt-groll](https://github.com/sbt/sbt-groll) by Heiko Seeberger
- [sbt-ctags](https://github.com/ceedubs/sbt-ctags) by Cody Allen
- [sbt-aws-lambda](https://github.com/gilt/sbt-aws-lambda) by Gilt (Brendan St John, et al)
- [sbt-heroku](https://github.com/heroku/sbt-heroku) by Heroku (Joe Kutner)
- [sbt-dynver](https://github.com/dwijnand/sbt-dynver) by Dale Wijnand
- [sbt-unidoc](https://github.com/sbt/sbt-unidoc) by Eugene Yokota and Peter Vlugter
- [sbt-docker-compose](https://github.com/Tapad/sbt-docker-compose) by Tapad (Kurt Kopchik et al)
- [sbt-coveralls](https://github.com/scoverage/sbt-coveralls) by Ian Forsey and Stephen Samuel
- [gatling-sbt](https://github.com/gatling/gatling-sbt) by Pierre Dal-Pra
- [sbt-boilerplate](https://github.com/sbt/sbt-boilerplate) by Johannes Rudolph
- [fm-sbt-s3-resolver](https://github.com/frugalmechanic/fm-sbt-s3-resolver) by Tim Underwood
- [sbt-reactjs](https://github.com/dispalt/sbt-reactjs) by Dan Di Spaltro
- [sbt-scalabuff](https://github.com/sbt/sbt-scalabuff) by Aloïs Cochard
- [sbt-pgp](https://github.com/sbt/sbt-pgp) by Josh Suereth
- [jacoco4sbt](https://github.com/sbt/jacoco4sbt) by Joachim Hofer
- [sbt-s3-resolver](https://github.com/ohnosequences/sbt-s3-resolver) by Alexey Alekhin (laughedelic)
- [sbt-maven-plugin](https://github.com/shivawu/sbt-maven-plugin) by Shiva Wu
- [sbt-newrelic](https://github.com/gilt/sbt-newrelic) by Gilt (Gary Coady et al)
- [naptime](https://github.com/coursera/naptime) by Coursera (Brennan Saeta, Bryan Kane et al)
- [neo-sbt-scalafmt](https://github.com/lucidsoftware/neo-sbt-scalafmt) by Lucid Software (Paul Draper et al)
- [Courier](http://coursera.github.io/courier/) by Coursera (Joe Betz et al)
- [sbt-optimizer](https://github.com/jrudolph/sbt-optimizer) by Johannes Rudolph
- [sbt-appengine](https://github.com/sbt/sbt-appengine) by Eugene Yokota (eed3si9n) and Yasushi Abe
- [sbt/sbt-ghpages](https://github.com/sbt/sbt-ghpages) by Josh Suereth
- [kotlin-plugin](https://github.com/pfn/kotlin-plugin) by Perry (pfn)
- [sbt-avro](https://github.com/sbt/sbt-avro) by Juan Manuel Caicedo Carvajal (cavorite), Ben McCann, et al
- [sbt-aspectj](https://github.com/sbt/sbt-aspectj) by Lightbend (Peter Vlugter et al)
- [sbt-crossproject](https://github.com/scala-native/sbt-crossproject) Denys Shabalin and Guillaume Massé
- [sbt-scapegoat](https://github.com/sksamuel/sbt-scapegoat) by Stephen Samuel
- [sbt-dependency-graph-sugar](https://github.com/gilt/sbt-dependency-graph-sugar) by Gilt (Brendan St John et al)
- [sbt-aether-deploy](https://github.com/arktekk/sbt-aether-deploy) by Arktekk (Erlend Hamnaberg et al)
- [sbt-spark-submit](https://github.com/saurfang/sbt-spark-submit) by Forest Fang
- [sbt-proguard](https://github.com/sbt/sbt-proguard) by Lightbend (Peter Vlugter et al)
- [Jenkins CI sbt plugin](https://github.com/jenkinsci/sbt-plugin) by Uzi Landsmann
- [sbt-quickfix](https://github.com/dscleaver/sbt-quickfix) by Dave Cleaver
- [sbt-growl-plugin](https://github.com/softprops/sbt-growl-plugin) Doug Tangren (softprops)
- [sbt-dependency-check](https://github.com/albuch/sbt-dependency-check) by Alexander v. Buchholtz
- [sbt-structure](https://github.com/JetBrains/sbt-structure) by JetBrains (Justin Kaeser et al)
- [sbt-typescript](https://github.com/ArpNetworking/sbt-typescript) by Brandon Arp
- [sbt-javacv](https://github.com/bytedeco/sbt-javacv) by Bytedeco (Lloyd Chan et al)
- [sbt-stats](https://github.com/orrsella/sbt-stats) by Orr Sella
- [sbt-rig](https://github.com/Verizon/sbt-rig) by Verizon (Timothy Perrett et al)
- [sbt-swagger-codegen](https://github.com/unicredit/sbt-swagger-codegen) by UniCredit (Andrea Peruffo, Francesco MDE, et al)
- [sbt-pom-reader](https://github.com/sbt/sbt-pom-reader) by Josh Suereth
- [sbt-class-diagram](https://github.com/xuwei-k/sbt-class-diagram) by Kenji Yoshida (xuwei-k)

Kudos also to people who have answered questions on [Stack Overflow](https://stackoverflow.com/tags/sbt/topusers) (Jacek Laskowski, Lukasz Piepiora, et al) and [sbt Gitter channel](gitter.im/sbt/sbt), and many who have reported issues and contributed ideas on GitHub.

Thank you all.

  [Changes]: Changes.html
  [@harrah]: https://github.com/harrah
  [@jsuereth]: https://github.com/jsuereth
  [@eed3si9n]: https://github.com/eed3si9n
  [@dwijnand]: https://github.com/dwijnand
  [@gkossakowski]: https://github.com/gkossakowski
  [@Duhemm]: https://github.com/Duhemm
  [@jvican]: https://github.com/jvican
  [sbt-0.13-Tech-Previews]: sbt-0.13-Tech-Previews.html
