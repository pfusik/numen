include ../common.mak

# .PHONY: run
# run: musk.obx
#	$(RUNOBX) $<

musk.nex musk.obx: musk.gr9 musk.grA musk.grB

musk.gr9 musk.grA: ../PCXtoHIP.class musk.pcx
	$(JAVA) PCXtoHIP musk.pcx musk.gr9 musk.grA

../PCXtoHIP.class: ../PCXImage.class

.PHONY: clean
clean:
	$(RM) musk.nex musk.obx musk.gr9 musk.grA
