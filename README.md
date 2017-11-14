Numen
=====

This is [Atari XL/XE](http://en.wikipedia.org/wiki/Atari_8-bit_family)
[demo](https://en.wikipedia.org/wiki/Demo_(computer_programming)).

Build instructions
------------------

Install:
* [xasm](/pfusik/xasm)
* Java SDK
* gcc, Make - use [Cygwin](http://cygwin.com)

These tools should be on your PATH.

Build with:

    make all

This will create `Numen.atr`, `Numen_A.atr` and `Numen_B.atr`.

History
-------

The demo was developed in 1997-2002 without a version control system,
so don't expect real commits or authors.
The initial commit here is actually a complete demo as we released it
at the Lato Ludzików party.
The second commit here is the final release, containing a fix
for uninitialized memory usage and some improvements
such as synchronization of effects with the soundtrack.

In 2016 I prepared an endless loop version for an exhibition,
it's on the [loop](/pfusik/numen/commits/loop) branch.
This needed some fixes to compile the demo on modern Windows
(originally it was built on Windows 98!).
These were real Git commits.

Authors
-------

* [Piotr 'Fox' Fusik](/pfusik) - programming
* [Marcin 'Eru' Żukowski](/MarcinZukowski) - programming
* Łukasz 'X-Ray' Sychowicz - soundtrack
* Michał 'Dracon' Garbaciak - graphics

Links
-----

* [Website](http://numen.scene.pl/)
* [Comment and vote at pouet.net](http://www.pouet.net/prod.php?which=9044&howmanycomments=-1)
