include ../common.mak

PCXToArt.class: ../PCXImage.class

.PHONY: clean
clean:
	$(RM) PCXToArt.class ConvArt.exe
	$(RM) ConvMap.class LittleEndianDataInputStream.class
	$(RM) MakePatternPCX.class FindAtariColors.class SetAtariColors.class
	$(RM) ConvTrack.class
	$(MAKE) -C 1intro clean
	$(MAKE) -C 2corrid clean
	$(MAKE) -C 3window clean
	$(MAKE) -C 4exit clean
	$(MAKE) -C 5pool clean
	$(MAKE) -C 6box clean
	$(MAKE) -C 7credits clean
