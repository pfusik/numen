include ../common.mak

.PHONY: run
run: title.obx
	$(RUNOBX) $<

title.nex title.obx: ejn.gr9 ejn.grA ejn.grB

%.gr9 %.grA: %.pcx ../PCXtoHIP.class
	$(JAVA) PCXtoHIP $< $*.gr9 $*.grA

../PCXtoHIP.class: ../PCXImage.class

.PHONY: clean
clean:
	$(RM) title.nex title.obx ejn.gr9 ejn.grA
