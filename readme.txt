                       _   _                      _   _
                      | \ | |                    | \ | |
                      |  \| |_   _ _ __ ___   ___|  \| |
                      | . ` | | | | '_ ` _ \ / _ \ . ` |
                      | |\  | |_| | | | | | |  __/ |\  |
                      |_| \_|\__,_|_| |_| |_|\___|_| \_|

       This is the complete source code of "Numen", an Atari 8-bit demo.
            Visit http://numen.scene.pl to learn more about "Numen".

We hope that this stuff  will be useful for people who  create new demos and/or
games. We think that  writing 8-bit  software is now much  easier than 20 years
ago. Nevertheless, you *do*  need good knowledge of  the 6502 assembly language
and Atari hardware.

All sources are original, same as used  in final version of "Numen" a year ago.
We haven't added any comments nor  documentation that could help you understand
them. Some comments  are in Polish. Many  6502 programmers  may be surprised by
some strange mnemonics in code, like MVA: they are xasm-specific.


How to build the demo
---------------------

You need a PC-compatible machine with following software:
* xasm (http://xasm.atari.org) - there's only DOS/Windows version, so you
  need a Windows operating system.
* Java SDK (http://java.sun.com).
* gcc, make, GNU Textutils (cat), GNU Fileutils (ls, rm) - I used DJGPP
  versions (http://www.delorie.com/djgpp).
All of these tools should be on your PATH. If so, then just type:
make all
to get all three ATRs. This takes over 2 minutes on my Celeron 800.

If you use Cygwin instead of DJGPP, type:
make --win32 all
I get a "Couldn't duplicate my handle..." message for each call of xasm,
but it works.

If you want to mess around with the demo, we recommend:
* The best Atari emulator: Atari800Win PLus (www.a800win.atari-area.prv.pl)
  - type its path in common.mak and runobx.bat, put runobx.bat on your PATH.
* A general-purpose text editor, e.g. Code-Genie (www.code-genie.com)
  - configure it to have "make in file's dir" and "make in Numen dir"
  at single keystrokes, install xasm syntax highlighting.
* A graphics editor: e.g. Corel Photo-Paint (http://www.corel.com).
* A Duke Nukem 3D level editor: e.g. Mapster (.util/mapster) - just run
  "make edit" in a subdirectory of "duke".


Quick guide to file types
-------------------------

* act - palette (256 RGB colors)
* asx - X-Assembler source
* atr - Atari disk image
* bat - MS-DOS/Windows batch file
* bmp - standard picture
* c - C source
* class - Java binary
* com - MS-DOS executable (e.g. xboot.com) or Atari executable (mptfox.com)
* cpt - Corel Photo-Paint picture
* exe - MS-DOS/Windows executable
* fnt - Atari 8-bit font
* grB - colors for TIP and 256 picture. Run "make" in .util/ColorEditor
        to launch GUI editor of these files. Note there's a newer version
        of ColorEditor, just contact us.
* hr - 3-color hi-res picture, edited with "HR" on Atari
* html - formatted text (view with a web browser)
* java - Java source
* mak - makefile fragment
* map - Duke Nukem 3D map
* mpf - module for a modified Music Protracker - see .util/MPT
* mpt - standard Music Protracker module
* nex - "Numen executable" - a part of the demo
* obx - xasm object file, often an Atari executable using $c000-$ffff RAM:
        use "runobx"
* pas - Turbo Pascal source
* pcx - standard picture
* psd - Adobe Photoshop picture
* sap - Atari music for "SAP Player"
* txt - a text file, gfx.txt and track.txt are scripts for
        ConvArt/ConvMap/ConvTrack
* xex - Atari executable


Statistics
----------

* 6502 sources (*.asx, *.equ):
       48 files
  573,841 bytes
   30,097 lines
* Java sources (*.java):
       37 files
  138,241 bytes
    4,596 lines
* C sources (*.c):
        2 files
   13,549 bytes
      578 lines
* Turbo Pascal sources (*.pas):
        3 files
   11,722 bytes
      695 lines
* makefiles (makefile, *.mak):
       34 files
   16,815 bytes
      713 lines
* graphics (*.bmp, *.cpt, *.fnt, *.grB, *.hr, *.pcx, *.psd):
       57 files
  402,242 bytes


Ending words
------------

We had great fun  creating our demo. We  know that final  product is not ideal,
but we still like  it. In our  opinion it's far  from technical  limitations of
Atari 8-bit hardware. We already have many new ideas, so don't say that "Numen"
was our last word.

We want you to be creative. Don't make  just another clone of "Boulder Dash" or
"Numen". Use your imagination. And the most important: finish your work.


Copyright and license
---------------------

(c) 1997-2002 Taquart

Permission is granted to anyone to use  this software and its documentation for
any purpose, including  commercial applications, and  redistribute it freely in
its original form. The authors make no representations about the suitability of
this software  for  any  purpose. It  is provided  "as is"  without  express or
implied warranty.
