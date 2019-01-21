Numen
=====

This is a popular
[Atari XL/XE](http://en.wikipedia.org/wiki/Atari_8-bit_family)
[demo](https://en.wikipedia.org/wiki/Demo_(computer_programming)).

Build instructions
------------------

Install:
* [xasm](https://github.com/pfusik/xasm)
* Java SDK
* gcc, Make - on Windows install [Cygwin](http://cygwin.com)

These tools should be on your PATH.

Build with:

    make all

This will create `Numen.atr`, `Numen_A.atr` and `Numen_B.atr`.

History
-------

The demo was developed in 1997-2002 without a version control system,
so don't expect real commits.
The initial commit here is actually a complete demo as we released it
at the Lato Ludzików party.
The second commit here is the final release, containing a fix
for uninitialized memory reference and some improvements
such as synchronization of effects with the soundtrack.

In 2016 I prepared an endless loop version for an exhibition.
It's on the [loop](https://github.com/pfusik/numen/commits/loop) branch.
This required some fixes in order to compile the demo on modern Windows
(originally it was built on Windows 98!).
These were real Git commits.

Authors
-------

* [Piotr 'Fox' Fusik](https://github.com/pfusik) - programming
* [Marcin 'Eru' Żukowski](https://github.com/MarcinZukowski) - programming
* Łukasz 'X-Ray' Sychowicz - soundtrack
* Michał 'Dracon' Garbaciak - graphics

License
-------

This source code is licensed under the poetic license:

This work 'as-is' we provide.
No warranty express or implied.
We've done our best,
to debug and test.
Liability for damages denied.

Permission is granted hereby,
to copy, share, and modify.
Use as is fit,
free or for profit.
These rights, on this notice, rely.

Links
-----

* [Website](http://numen.scene.pl/)
* [Comment and vote at pouët.net](http://www.pouet.net/prod.php?which=9044&howmanycomments=-1)
