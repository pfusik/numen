include ../common.mak

.PHONY: run
run: bigbump.obx
	$(RUNOBX) $<

bigbump.nex bigbump.obx: theend.raw endtext.gr8
# bigbump.nex bigbump.obx: end2.raw
# bigbump.nex bigbump.obx: end5.raw

%.raw: %.pcx LsrMap.class ../PCXImage.class
	$(JAVA) LsrMap $< $@

LsrMap.class: ../PCXImage.class

%.gr8: %.pcx ../PCXtoGR8.class ../PCXImage.class
	$(JAVA) PCXtoGR8 $< $@

.PHONY: clean
clean:
	$(RM) bigbump.nex bigbump.obx *.raw LsrMap.class endtext.gr8
