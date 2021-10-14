.PHONY: pilot
pilot: duke.obx
	$(RUNOBX) $<

.PHONY: game
game: game.xex
	$(RUNXEX) game.xex

# Mapster (Duke Nukem 3D level editor) directory
# (relative to a level's directory)
MAPSTERDIR = ../../.util/mapster
MAPSTER = ./mapster32.exe
# This directory (relative to Mapster directory)
RELMAPSTERDIR = ../../duke

.PHONY: edit
edit: tiles000.art palette.dat
	cp palette.dat tiles000.art $(MAPSTERDIR)
	cd $(MAPSTERDIR) && $(MAPSTER) -map $(RELMAPSTERDIR)/$(DIRNAME)/map.map

.PHONY: play
play: tiles000.art palette.dat
	../playduke.bat

duke.obx game.xex duke.nex: ../world.equ map.asx tiles.asx
duke.obx duke.nex: duke.asx ../pilot.asx ../../numendef.asx track.asx
duke.obx game.xex: ../engine.asx ../lookup.asx ../upsector.asx
game.xex: ../animate.asx
ifeq ($(LEVEL),1)
duke.nex: ../engine.asx ../lookup.asx ../upsector.asx
endif

duke.obx:
	$(XASM) -d numen=0 -d level=$(LEVEL) duke.asx

game.xex: ../game.asx
	$(XASM) -o game.xex -d level=$(LEVEL) ../game.asx

duke.nex:
	$(XASM) -o duke.nex -d numen=1 -d level=$(LEVEL) duke.asx

map.asx: ../ConvMap.class map.map gfx.txt
	$(JAVA) ConvMap map.map gfx.txt $(COLORS) >map.asx

tiles.asx: ../ConvArt.exe tiles000.art gfx.txt
	../ConvArt.exe tiles000.art gfx.txt >tiles.asx

track.asx: ../ConvTrack.class track.txt
	$(JAVA) ConvTrack <track.txt >track.asx

tiles000.art: ../PCXToArt.class 100.pcx $(wildcard *.pcx)
	$(JAVA) PCXToArt . tiles000.art palette.dat

100.pcx: ../MakePatternPCX.class ../real.act
	$(JAVA) MakePatternPCX ../real.act $(COLORS)

.PHONY: setcolors
setcolors: ../SetAtariColors.class ../real.act
	ls *.pcx | $(JAVA) SetAtariColors ../real.act $(COLORS) -

.PHONY: clean
clean:
	$(RM) duke.obx game.xex duke.nex map.asx track.asx
	$(RM) tiles.asx tiles000.art 000.pcx [1-2][0-5][0-9].pcx
ifeq ($(LEVEL),7)
	$(RM) text.raw ConvText.class
endif
