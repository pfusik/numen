include ../common.mak

.PHONY: run
run: rzoom.obx
	$(RUNOBX) $<

rzoom.nex rzoom.obx: rzoom.gfx rzoom.tab

rzoom.gfx: ../PCXtoGR9.class rzoom.pcx
	$(JAVA) PCXtoGR9 rzoom.pcx rzoom.gfx

.PHONY: clean
clean:
	$(RM) rzoom.nex rzoom.obx rzoom.gfx
