include ../common.mak

.PHONY: run
run: logo256.obx
	$(RUNOBX) $<

logo256.nex logo256.obx: logo256.gfx

logo256.gfx: ../PCXGRBto256.class logo256.pcx logo256.grB
	$(JAVA) PCXGRBto256 logo256.pcx logo256.grB logo256.gfx

../PCXGRBto256.class: ../PCXImage.class

.PHONY: clean
clean:
	$(RM) logo256.nex logo256.obx logo256.gfx
